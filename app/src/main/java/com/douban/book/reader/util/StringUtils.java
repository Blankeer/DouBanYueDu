package com.douban.book.reader.util;

import android.support.annotation.Nullable;
import com.douban.book.reader.constant.Char;
import io.realm.internal.Table;
import java.util.Arrays;
import java.util.UUID;

public class StringUtils {
    public static boolean isNotEmpty(CharSequence... args) {
        if (args != null) {
            for (CharSequence text : args) {
                if (isEmpty(text)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    public static boolean hasReadableText(CharSequence text) {
        if (text != null) {
            if (isNotEmpty(text.toString().replaceAll("\\s", Table.STRING_DEFAULT_VALUE))) {
                return true;
            }
        }
        return false;
    }

    public static boolean equals(CharSequence text1, CharSequence text2) {
        if (text1 == null || text2 == null) {
            return isEmpty(text1) && isEmpty(text2);
        } else {
            return text1.toString().equals(text2.toString());
        }
    }

    public static boolean equals(CharSequence text1, int resId) {
        return equals(text1, Res.getString(resId));
    }

    public static boolean equalsIgnoreCase(CharSequence text1, CharSequence text2) {
        if (text1 == null || text2 == null) {
            return text1 == null && text2 == null;
        } else {
            return text1.toString().equalsIgnoreCase(text2.toString());
        }
    }

    public static CharSequence trimWhitespace(CharSequence str) {
        if (isEmpty(str)) {
            return Table.STRING_DEFAULT_VALUE;
        }
        int start = 0;
        int end = str.length() - 1;
        while (start <= end && Character.isWhitespace(str.charAt(start))) {
            start++;
        }
        while (end >= start && Character.isWhitespace(str.charAt(end))) {
            end--;
        }
        return str.subSequence(start, end + 1);
    }

    public static boolean containsIgnoreCase(String str, String key) {
        if (isEmpty(str) || isEmpty(key)) {
            return false;
        }
        return str.toLowerCase().contains(key.toLowerCase());
    }

    public static boolean containsLineBreak(String str) {
        return isNotEmpty(str) && (str.contains("\n") || str.contains("\r"));
    }

    public static boolean containsAll(String findFrom, CharSequence... args) {
        if (isEmpty(findFrom)) {
            return false;
        }
        if (args != null) {
            for (CharSequence arg : args) {
                if (!findFrom.contains(arg)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static CharSequence join(char splitter, Object... args) {
        if (args == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        CharSequence csSplitter = String.valueOf(splitter);
        if (args.length != 1 || !(args[0] instanceof Iterable)) {
            return join(csSplitter, Arrays.asList(args));
        }
        return join(csSplitter, args[0]);
    }

    public static CharSequence join(CharSequence splitter, Object... args) {
        if (args == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        if (args.length != 1 || !(args[0] instanceof Iterable)) {
            return join(splitter, Arrays.asList(args));
        }
        return join(splitter, args[0]);
    }

    public static CharSequence joinSkippingNull(CharSequence splitter, Object... args) {
        if (args == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        if (args.length != 1 || !(args[0] instanceof Iterable)) {
            return join(splitter, Arrays.asList(args), true);
        }
        return join(splitter, args[0], Boolean.valueOf(true));
    }

    public static CharSequence join(CharSequence splitter, Iterable<?> args) {
        return join(splitter, args, false);
    }

    public static CharSequence joinSkippingNull(CharSequence splitter, Iterable<?> args) {
        return join(splitter, args, true);
    }

    private static CharSequence join(CharSequence splitter, Iterable<?> args, boolean skippingNull) {
        if (args == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Object next : args) {
            if (next != null || !skippingNull) {
                if (!first) {
                    builder.append(splitter);
                }
                builder.append(next);
                first = false;
            }
        }
        return builder.toString();
    }

    public static CharSequence quoteIfNotEmpty(CharSequence text, char startQuote) {
        if (!isNotEmpty(text)) {
            return text;
        }
        char endQuote = Char.getMatchingQuote(startQuote);
        return String.format("%s%s%s", new Object[]{Character.valueOf(startQuote), text, Character.valueOf(endQuote)});
    }

    public static boolean inList(CharSequence query, CharSequence... list) {
        for (CharSequence arg : list) {
            if (equals(query, arg)) {
                return true;
            }
        }
        return false;
    }

    public static boolean inListIgnoreCase(CharSequence query, CharSequence... list) {
        for (CharSequence arg : list) {
            if (equalsIgnoreCase(query, arg)) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence truncate(CharSequence str, int length) {
        if (str == null) {
            return null;
        }
        return str.length() > length ? str.subSequence(0, length - 1) + " ..." : str;
    }

    public static int toInt(CharSequence str) {
        try {
            return Integer.valueOf(String.valueOf(str)).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public static long toLong(CharSequence str) {
        try {
            return Long.valueOf(String.valueOf(str)).longValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public static float toFloat(String str) {
        try {
            return Float.valueOf(str).floatValue();
        } catch (Exception e) {
            return 0.0f;
        }
    }

    public static boolean isNumber(String str) {
        try {
            Integer.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String toStr(Object object) {
        if (object == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return String.valueOf(object);
    }

    public static String filterOut(String str, CharSequence... filters) {
        if (!(isEmpty(str) || filters == null)) {
            for (CharSequence filter : filters) {
                str = str.replace(filter, Table.STRING_DEFAULT_VALUE);
            }
        }
        return str;
    }

    public static String lastSegment(String str) {
        return lastSegment(str, Char.SLASH);
    }

    public static String lastSegment(String str, char divider) {
        int index = str.lastIndexOf(divider);
        if (index == str.length() - 1) {
            return Table.STRING_DEFAULT_VALUE;
        }
        if (index >= 0) {
            return str.substring(index + 1);
        }
        return str;
    }

    public static String removeLastSegment(String str, char divider) {
        int index = str.lastIndexOf(divider);
        if (index == str.length() - 1) {
            return str;
        }
        if (index >= 0) {
            return str.substring(0, index);
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    public static String getSegment(String str, int index) {
        if (isEmpty(str)) {
            return Table.STRING_DEFAULT_VALUE;
        }
        String[] segments = str.split("\\.", 0);
        if (index >= 0 && index < segments.length) {
            return segments[index];
        }
        if (index >= 0 || index < (-segments.length)) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return segments[segments.length + index];
    }

    @Nullable
    public static UUID toUUID(CharSequence cs) {
        try {
            return UUID.fromString(toStr(cs));
        } catch (Throwable th) {
            return null;
        }
    }
}
