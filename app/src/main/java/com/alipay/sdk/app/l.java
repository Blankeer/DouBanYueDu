package com.alipay.sdk.app;

import io.realm.internal.Table;

public final class l {
    static String a;

    private static void a(String str) {
        a = str;
    }

    private static String c() {
        return a;
    }

    public static String a() {
        m a = m.a(m.CANCELED.g);
        return a(a.g, a.h, Table.STRING_DEFAULT_VALUE);
    }

    public static String b() {
        m a = m.a(m.PARAMS_ERROR.g);
        return a(a.g, a.h, Table.STRING_DEFAULT_VALUE);
    }

    public static String a(int i, String str, String str2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("resultStatus={").append(i).append("};memo={").append(str).append("};result={").append(str2).append("}");
        return stringBuilder.toString();
    }
}
