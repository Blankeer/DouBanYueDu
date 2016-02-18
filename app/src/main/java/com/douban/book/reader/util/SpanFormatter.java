package com.douban.book.reader.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.realm.internal.Table;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpanFormatter {
    public static final Pattern FORMAT_SEQUENCE;

    static {
        FORMAT_SEQUENCE = Pattern.compile("%([0-9]+\\$|<?)([^a-zA-z%]*)([[a-zA-Z%]&&[^tT]]|[tT][a-zA-Z])");
    }

    private SpanFormatter() {
    }

    public static SpannedString format(CharSequence format, Object... args) {
        return format(Locale.getDefault(), format, args);
    }

    public static SpannedString format(Locale locale, CharSequence format, Object... args) {
        SpannableStringBuilder out = new SpannableStringBuilder(format);
        int i = 0;
        int argAt = -1;
        while (i < out.length()) {
            Matcher m = FORMAT_SEQUENCE.matcher(out);
            if (!m.find(i)) {
                break;
            }
            CharSequence cookedArg;
            i = m.start();
            int exprEnd = m.end();
            String argTerm = m.group(1);
            String modTerm = m.group(2);
            String typeTerm = m.group(3);
            if (typeTerm.equals("%")) {
                cookedArg = "%";
            } else if (typeTerm.equals("n")) {
                cookedArg = "\n";
            } else {
                int argIdx;
                if (argTerm.equals(Table.STRING_DEFAULT_VALUE)) {
                    argAt++;
                    argIdx = argAt;
                } else if (argTerm.equals(SimpleComparison.LESS_THAN_OPERATION)) {
                    argIdx = argAt;
                } else {
                    argIdx = Integer.parseInt(argTerm.substring(0, argTerm.length() - 1)) - 1;
                }
                Object argItem = args[argIdx];
                if (typeTerm.equals("s") && (argItem instanceof Spanned)) {
                    Spanned cookedArg2 = (Spanned) argItem;
                } else {
                    cookedArg = String.format(locale, "%" + modTerm + typeTerm, new Object[]{argItem});
                }
            }
            out.replace(i, exprEnd, cookedArg);
            i += cookedArg.length();
        }
        return new SpannedString(out);
    }
}
