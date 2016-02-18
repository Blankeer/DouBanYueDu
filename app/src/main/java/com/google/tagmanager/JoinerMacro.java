package com.google.tagmanager;

import com.douban.book.reader.fragment.share.ShareUrlEditFragment_;
import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import u.aly.dx;

class JoinerMacro extends FunctionCallImplementation {
    private static final String ARG0;
    private static final String DEFAULT_ITEM_SEPARATOR = "";
    private static final String DEFAULT_KEY_VALUE_SEPARATOR = "=";
    private static final String ESCAPE;
    private static final String ID;
    private static final String ITEM_SEPARATOR;
    private static final String KEY_VALUE_SEPARATOR;

    /* renamed from: com.google.tagmanager.JoinerMacro.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$tagmanager$JoinerMacro$EscapeType;

        static {
            $SwitchMap$com$google$tagmanager$JoinerMacro$EscapeType = new int[EscapeType.values().length];
            try {
                $SwitchMap$com$google$tagmanager$JoinerMacro$EscapeType[EscapeType.URL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$tagmanager$JoinerMacro$EscapeType[EscapeType.BACKSLASH.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$tagmanager$JoinerMacro$EscapeType[EscapeType.NONE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private enum EscapeType {
        NONE,
        URL,
        BACKSLASH
    }

    static {
        ID = FunctionType.JOINER.toString();
        ARG0 = Key.ARG0.toString();
        ITEM_SEPARATOR = Key.ITEM_SEPARATOR.toString();
        KEY_VALUE_SEPARATOR = Key.KEY_VALUE_SEPARATOR.toString();
        ESCAPE = Key.ESCAPE.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public JoinerMacro() {
        super(ID, ARG0);
    }

    public boolean isCacheable() {
        return true;
    }

    public Value evaluate(Map<String, Value> parameters) {
        Value input = (Value) parameters.get(ARG0);
        if (input == null) {
            return Types.getDefaultValue();
        }
        Value itemSeparatorParameter = (Value) parameters.get(ITEM_SEPARATOR);
        String itemSeparator = itemSeparatorParameter != null ? Types.valueToString(itemSeparatorParameter) : DEFAULT_ITEM_SEPARATOR;
        Value keyValueSeparatorParameter = (Value) parameters.get(KEY_VALUE_SEPARATOR);
        String keyValueSeparator = keyValueSeparatorParameter != null ? Types.valueToString(keyValueSeparatorParameter) : DEFAULT_KEY_VALUE_SEPARATOR;
        EscapeType escapeType = EscapeType.NONE;
        Value escapeParameter = (Value) parameters.get(ESCAPE);
        Set<Character> charsToBackslashEscape = null;
        if (escapeParameter != null) {
            String escape = Types.valueToString(escapeParameter);
            if (ShareUrlEditFragment_.URL_ARG.equals(escape)) {
                escapeType = EscapeType.URL;
            } else {
                if ("backslash".equals(escape)) {
                    escapeType = EscapeType.BACKSLASH;
                    charsToBackslashEscape = new HashSet();
                    addTo(charsToBackslashEscape, itemSeparator);
                    addTo(charsToBackslashEscape, keyValueSeparator);
                    charsToBackslashEscape.remove(Character.valueOf('\\'));
                } else {
                    Log.e("Joiner: unsupported escape type: " + escape);
                    return Types.getDefaultValue();
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        switch (input.type) {
            case dx.c /*2*/:
                boolean firstTime = true;
                for (Value itemVal : input.listItem) {
                    if (!firstTime) {
                        sb.append(itemSeparator);
                    }
                    firstTime = false;
                    append(sb, Types.valueToString(itemVal), escapeType, charsToBackslashEscape);
                }
                break;
            case dx.d /*3*/:
                int i = 0;
                while (true) {
                    int length = input.mapKey.length;
                    if (i >= r0) {
                        break;
                    }
                    if (i > 0) {
                        sb.append(itemSeparator);
                    }
                    String key = Types.valueToString(input.mapKey[i]);
                    String value = Types.valueToString(input.mapValue[i]);
                    append(sb, key, escapeType, charsToBackslashEscape);
                    sb.append(keyValueSeparator);
                    append(sb, value, escapeType, charsToBackslashEscape);
                    i++;
                }
            default:
                append(sb, Types.valueToString(input), escapeType, charsToBackslashEscape);
                break;
        }
        return Types.objectToValue(sb.toString());
    }

    private void addTo(Set<Character> set, String string) {
        for (int i = 0; i < string.length(); i++) {
            set.add(Character.valueOf(string.charAt(i)));
        }
    }

    private void append(StringBuilder sb, String s, EscapeType escapeType, Set<Character> charsToBackslashEscape) {
        sb.append(escape(s, escapeType, charsToBackslashEscape));
    }

    private String escape(String s, EscapeType escapeType, Set<Character> charsToBackslashEscape) {
        switch (AnonymousClass1.$SwitchMap$com$google$tagmanager$JoinerMacro$EscapeType[escapeType.ordinal()]) {
            case dx.b /*1*/:
                try {
                    return ValueEscapeUtil.urlEncode(s);
                } catch (UnsupportedEncodingException e) {
                    Log.e("Joiner: unsupported encoding", e);
                    return s;
                }
            case dx.c /*2*/:
                s = s.replace("\\", "\\\\");
                for (Character c : charsToBackslashEscape) {
                    String charAsString = c.toString();
                    s = s.replace(charAsString, "\\" + charAsString);
                }
                return s;
            default:
                return s;
        }
    }
}
