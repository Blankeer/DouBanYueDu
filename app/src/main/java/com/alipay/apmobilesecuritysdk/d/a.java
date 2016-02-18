package com.alipay.apmobilesecuritysdk.d;

import android.content.Context;
import io.realm.internal.Table;

public class a {
    private static String a;
    private static volatile boolean b;
    private static Context c;
    private static a d;

    static {
        a = Table.STRING_DEFAULT_VALUE;
        b = false;
        c = null;
        d = null;
    }

    private a() {
    }

    public static a a(Context context) {
        if (d == null) {
            synchronized (a.class) {
                if (d == null) {
                    d = new a();
                    c = context;
                }
            }
        }
        return d;
    }

    public static String a() {
        com.alipay.security.mobile.module.commonutils.a.a(a);
        return a;
    }

    public static String b() {
        return a;
    }
}
