package com.google.tagmanager.protobuf.nano;

import com.douban.book.reader.constant.Char;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

public final class MessageNanoPrinter {
    private static final String INDENT = "  ";
    private static final int MAX_STRING_LEN = 200;

    private MessageNanoPrinter() {
    }

    public static <T extends MessageNano> String print(T message) {
        if (message == null) {
            return "null";
        }
        StringBuffer buf = new StringBuffer();
        try {
            print(message.getClass().getSimpleName(), message.getClass(), message, new StringBuffer(), buf);
            return buf.toString();
        } catch (IllegalAccessException e) {
            return "Error printing proto: " + e.getMessage();
        }
    }

    private static void print(String identifier, Class<?> clazz, Object message, StringBuffer indentBuf, StringBuffer buf) throws IllegalAccessException {
        if (MessageNano.class.isAssignableFrom(clazz)) {
            if (message != null) {
                buf.append(indentBuf).append(identifier);
                indentBuf.append(INDENT);
                buf.append(" <\n");
                for (Field field : clazz.getFields()) {
                    int modifiers = field.getModifiers();
                    String fieldName = field.getName();
                    if (!((modifiers & 1) != 1 || (modifiers & 8) == 8 || fieldName.startsWith(EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR) || fieldName.endsWith(EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR))) {
                        Class<?> fieldType = field.getType();
                        Object value = field.get(message);
                        if (fieldType.isArray()) {
                            Class<?> arrayType = fieldType.getComponentType();
                            if (arrayType == Byte.TYPE) {
                                print(fieldName, fieldType, value, indentBuf, buf);
                            } else {
                                int len = value == null ? 0 : Array.getLength(value);
                                for (int i = 0; i < len; i++) {
                                    print(fieldName, arrayType, Array.get(value, i), indentBuf, buf);
                                }
                            }
                        } else {
                            print(fieldName, fieldType, value, indentBuf, buf);
                        }
                    }
                }
                indentBuf.delete(indentBuf.length() - INDENT.length(), indentBuf.length());
                buf.append(indentBuf).append(">\n");
            }
        } else if (message != null) {
            buf.append(indentBuf).append(deCamelCaseify(identifier)).append(": ");
            if (message instanceof String) {
                buf.append("\"").append(sanitizeString((String) message)).append("\"");
            } else {
                buf.append(message);
            }
            buf.append("\n");
        }
    }

    private static String deCamelCaseify(String identifier) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < identifier.length(); i++) {
            char currentChar = identifier.charAt(i);
            if (i == 0) {
                out.append(Character.toLowerCase(currentChar));
            } else if (Character.isUpperCase(currentChar)) {
                out.append(Char.UNDERLINE).append(Character.toLowerCase(currentChar));
            } else {
                out.append(currentChar);
            }
        }
        return out.toString();
    }

    private static String sanitizeString(String str) {
        if (!str.startsWith("http") && str.length() > MAX_STRING_LEN) {
            str = str.substring(0, MAX_STRING_LEN) + "[...]";
        }
        return escapeString(str);
    }

    private static String escapeString(String str) {
        int strLen = str.length();
        StringBuilder b = new StringBuilder(strLen);
        for (int i = 0; i < strLen; i++) {
            char original = str.charAt(i);
            if (original < Char.SPACE || original > '~' || original == '\"' || original == '\'') {
                b.append(String.format("\\u%04x", new Object[]{Integer.valueOf(original)}));
            } else {
                b.append(original);
            }
        }
        return b.toString();
    }
}
