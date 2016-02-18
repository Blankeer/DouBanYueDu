package com.tencent.mm.sdk.c;

import android.net.Uri;
import android.provider.BaseColumns;
import u.aly.ci;
import u.aly.dj;
import u.aly.dx;

public final class a {

    public static final class a {
        public static Object a(int i, String str) {
            switch (i) {
                case dx.b /*1*/:
                    return Integer.valueOf(str);
                case dx.c /*2*/:
                    return Long.valueOf(str);
                case dx.d /*3*/:
                    return str;
                case dx.e /*4*/:
                    return Boolean.valueOf(str);
                case dj.f /*5*/:
                    return Float.valueOf(str);
                case ci.g /*6*/:
                    return Double.valueOf(str);
                default:
                    try {
                        com.tencent.mm.sdk.b.a.a("MicroMsg.SDK.PluginProvider.Resolver", "unknown type");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
            }
        }
    }

    public static final class b implements BaseColumns {
        public static final Uri CONTENT_URI;

        static {
            CONTENT_URI = Uri.parse("content://com.tencent.mm.sdk.plugin.provider/sharedpref");
        }
    }
}
