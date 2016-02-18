package com.tencent.mm.sdk.b;

import com.douban.book.reader.constant.Char;
import java.util.TimeZone;

public final class e {
    private static final long[] G;
    private static final TimeZone GMT;
    private static final long[] H;
    private static final char[] I;
    private static final String[] J;

    static {
        G = new long[]{300, 200, 300, 200};
        H = new long[]{300, 50, 300, 50};
        GMT = TimeZone.getTimeZone("GMT");
        I = new char[]{'<', '>', '\"', '\'', '&', Char.CARRIAGE_RETURN, '\n', Char.SPACE, '\t'};
        J = new String[]{"&lt;", "&gt;", "&quot;", "&apos;", "&amp;", "&#x0D;", "&#x0A;", "&#x20;", "&#x09;"};
    }

    public static boolean j(String str) {
        return str == null || str.length() <= 0;
    }
}
