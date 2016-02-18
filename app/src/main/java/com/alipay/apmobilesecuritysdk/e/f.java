package com.alipay.apmobilesecuritysdk.e;

import android.content.Context;
import com.alipay.security.mobile.module.commonutils.d;
import com.umeng.analytics.a;
import io.realm.internal.Table;

public final class f {
    private static String a;
    private static String b;

    static {
        a = Table.STRING_DEFAULT_VALUE;
        b = Table.STRING_DEFAULT_VALUE;
    }

    public static synchronized String a() {
        String str;
        synchronized (f.class) {
            str = a;
        }
        return str;
    }

    public static synchronized void a(b bVar) {
        synchronized (f.class) {
            if (bVar != null) {
                a = bVar.a;
                b = bVar.c;
                new StringBuilder("Update Token Storage. apdid = ").append(a).append(", token = ").append(b);
            }
        }
    }

    public static synchronized boolean a(Context context) {
        boolean z;
        synchronized (f.class) {
            try {
                if (Math.abs(System.currentTimeMillis() - e.b(context)) < a.g) {
                    z = true;
                }
            } catch (Throwable th) {
                d.a(th);
            }
            z = false;
        }
        return z;
    }

    public static synchronized String b() {
        String str;
        synchronized (f.class) {
            str = b;
        }
        return str;
    }
}
