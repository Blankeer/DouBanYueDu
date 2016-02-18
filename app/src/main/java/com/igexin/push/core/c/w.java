package com.igexin.push.core.c;

import com.igexin.a.a.c.a;
import com.igexin.push.config.SDKUrlConfig;
import com.igexin.push.config.l;
import com.igexin.push.core.g;
import java.util.List;
import u.aly.dx;

public class w {
    private static int a;
    private static int b;
    private static int c;
    private static List d;
    private static y e;
    private static int f;

    static {
        a = 0;
        b = 0;
        c = 0;
        e = y.NORMAL;
        f = 0;
    }

    public static void a() {
        String e = e();
        if (e == null) {
            int i;
            if (!l.f) {
                a.b("ServerAddressManager switch from normal domain!");
                b++;
                if (b >= 3) {
                    b = 0;
                    i = a + 1;
                    a = i;
                    a = i % SDKUrlConfig.XFR_ADDRESS_IPS.length;
                }
                e = SDKUrlConfig.XFR_ADDRESS_IPS[a];
                a.b("retry connect to " + e + " " + b + " times");
            } else if (e == y.BACKUP) {
                i = a + 1;
                a = i;
                a = i % SDKUrlConfig.XFR_ADDRESS_IPS_BAK.length;
                e = SDKUrlConfig.XFR_ADDRESS_IPS_BAK[a];
                a.b("ServerAddressManager switch from backup domain, address : " + e);
            } else {
                i = a + 1;
                a = i;
                a = i % SDKUrlConfig.XFR_ADDRESS_IPS.length;
                e = SDKUrlConfig.XFR_ADDRESS_IPS[a];
                a.b("ServerAddressManager switch from normal domain, address : " + e);
            }
        }
        a.b("ServerAddressManager SERVER_CM_Address changed : form [" + SDKUrlConfig.getCmAddress() + "] to [" + e + "]");
        SDKUrlConfig.setCmAddress(e);
    }

    private static void a(y yVar) {
        if (l.f) {
            if (e != yVar) {
                a(null);
            }
            switch (x.a[yVar.ordinal()]) {
                case dx.b /*1*/:
                    break;
                case dx.c /*2*/:
                    if (e != yVar) {
                        f.a().e(System.currentTimeMillis());
                    }
                    SDKUrlConfig.setCmAddress(SDKUrlConfig.XFR_ADDRESS_IPS_BAK[0]);
                    break;
                case dx.d /*3*/:
                    if (e != yVar) {
                        f = 0;
                        break;
                    }
                    break;
            }
            SDKUrlConfig.setCmAddress(SDKUrlConfig.XFR_ADDRESS_IPS[0]);
            e = yVar;
        }
    }

    public static void a(List list) {
        d = list;
        c = 0;
    }

    public static void b() {
        switch (x.a[e.ordinal()]) {
            case dx.c /*2*/:
                if (System.currentTimeMillis() - g.Q > com.umeng.analytics.a.g) {
                    a(y.TRY_NORMAL);
                }
            default:
        }
    }

    public static void c() {
        switch (x.a[e.ordinal()]) {
            case dx.b /*1*/:
                f.a().b(System.currentTimeMillis());
            case dx.d /*3*/:
                a(y.NORMAL);
            default:
        }
    }

    public static void d() {
        f();
        if (g.m) {
            f.a().b(System.currentTimeMillis());
        }
        switch (x.a[e.ordinal()]) {
            case dx.d /*3*/:
                int i = f + 1;
                f = i;
                if (i >= 10) {
                    a(y.BACKUP);
                }
            default:
        }
    }

    private static String e() {
        if (d != null) {
            long currentTimeMillis = System.currentTimeMillis();
            while (!d.isEmpty()) {
                if (c >= d.size()) {
                    c = 0;
                }
                z zVar = (z) d.get(c);
                if (zVar.b < currentTimeMillis) {
                    d.remove(zVar);
                } else {
                    c++;
                    return zVar.a;
                }
            }
        }
        return null;
    }

    private static void f() {
        switch (x.a[e.ordinal()]) {
            case dx.b /*1*/:
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - g.P <= 1296000000) {
                    return;
                }
                if (currentTimeMillis - g.Q > com.umeng.analytics.a.g) {
                    a(y.TRY_NORMAL);
                } else {
                    a(y.BACKUP);
                }
            case dx.c /*2*/:
                if (System.currentTimeMillis() - g.Q > com.umeng.analytics.a.g) {
                    a(y.TRY_NORMAL);
                }
            default:
        }
    }
}
