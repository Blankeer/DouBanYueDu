package com.alipay.apmobilesecuritysdk.a;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import com.alipay.apmobilesecuritysdk.e.b;
import com.alipay.apmobilesecuritysdk.e.e;
import com.alipay.apmobilesecuritysdk.e.f;
import com.alipay.security.mobile.module.a.b.c;
import com.alipay.security.mobile.module.a.d;
import com.alipay.security.mobile.module.commonutils.crypto.i;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.realm.internal.Table;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public final class a {
    private static boolean b;
    private final Context a;

    static {
        b = false;
    }

    public a(Context context) {
        this.a = context;
    }

    public static String a(Context context) {
        String str = Table.STRING_DEFAULT_VALUE;
        try {
            str = f.b();
            if (com.alipay.security.mobile.module.commonutils.a.a(str)) {
                b a = com.alipay.apmobilesecuritysdk.e.a.a(context);
                if (!(a == null || com.alipay.security.mobile.module.commonutils.a.a(a.c))) {
                    str = a.c;
                    f.a(a);
                }
            }
        } catch (Throwable th) {
        }
        return str;
    }

    static /* synthetic */ void a(a aVar, String str, String str2, String str3) {
        try {
            com.alipay.security.mobile.module.a.b.a a = d.a(aVar.a).a(AbstractSpiCall.ANDROID_CLIENT_TYPE, str, str2, str3);
            if (a.c) {
                e.a(aVar.a, a.b, a.a);
                a(false);
            }
        } catch (Throwable th) {
            com.alipay.security.mobile.module.commonutils.d.a(th);
        } finally {
            a(false);
        }
    }

    private static synchronized void a(boolean z) {
        synchronized (a.class) {
            b = z;
        }
    }

    private static synchronized boolean a() {
        boolean z;
        synchronized (a.class) {
            z = b;
        }
        return z;
    }

    private c b(Map<String, String> map) {
        c cVar = null;
        try {
            Context context = this.a;
            com.alipay.security.mobile.module.a.b.d dVar = new com.alipay.security.mobile.module.a.b.d();
            String str = Constants.VIA_TO_TYPE_QQ_DISCUSS_GROUP;
            String str2 = Table.STRING_DEFAULT_VALUE;
            String str3 = Table.STRING_DEFAULT_VALUE;
            String str4 = Table.STRING_DEFAULT_VALUE;
            String str5 = Table.STRING_DEFAULT_VALUE;
            String a = com.alipay.security.mobile.module.commonutils.a.a(map, "umid", Table.STRING_DEFAULT_VALUE);
            b b = com.alipay.apmobilesecuritysdk.e.a.b(context);
            if (b != null) {
                str2 = b.c;
                str3 = b.a;
                str5 = str3;
                str3 = b.d;
            } else {
                String str6 = str5;
                str5 = str3;
                str3 = str6;
            }
            b = com.alipay.apmobilesecuritysdk.e.a.a();
            if (b != null) {
                str4 = b.a;
            }
            if (com.alipay.security.mobile.module.commonutils.a.a(str5) && com.alipay.security.mobile.module.commonutils.a.a(r1)) {
                str5 = com.alipay.apmobilesecuritysdk.a.a.a.b(context);
                str4 = com.alipay.apmobilesecuritysdk.a.a.a.a();
            }
            dVar.a = AbstractSpiCall.ANDROID_CLIENT_TYPE;
            dVar.d = str5;
            dVar.c = str4;
            dVar.e = str2;
            dVar.f = a;
            dVar.h = str3;
            dVar.g = str;
            dVar.i = com.alipay.apmobilesecuritysdk.c.e.a(context, map);
            cVar = d.a(this.a).a(dVar);
        } catch (Throwable th) {
            com.alipay.security.mobile.module.commonutils.d.a(th);
        }
        return cVar;
    }

    public static String b(Context context) {
        try {
            String a = f.a();
            if (!com.alipay.security.mobile.module.commonutils.a.a(a)) {
                return a;
            }
            b a2 = com.alipay.apmobilesecuritysdk.e.a.a(context);
            if (a2 == null || com.alipay.security.mobile.module.commonutils.a.a(a2.a)) {
                a = com.alipay.apmobilesecuritysdk.a.a.a.a(context);
                if (!com.alipay.security.mobile.module.commonutils.a.a(a)) {
                    return a;
                }
                a = com.alipay.apmobilesecuritysdk.f.b.a(context);
                return !com.alipay.security.mobile.module.commonutils.a.a(a) ? a : a;
            } else {
                f.a(a2);
                return a2.a;
            }
        } catch (Throwable th) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    public final String a(Map<String, String> map) {
        String str = Table.STRING_DEFAULT_VALUE;
        if (map == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        String b;
        com.alipay.apmobilesecuritysdk.b.b bVar;
        String str2 = this.a.getFilesDir().getAbsolutePath() + "/log/ap";
        String str3 = Build.MODEL;
        String str4 = this.a.getApplicationContext().getApplicationInfo().packageName;
        String str5 = "security-sdk-token";
        String str6 = "3.0.2.20151027";
        String a = com.alipay.security.mobile.module.commonutils.a.a(map, com.alipay.sdk.cons.b.c, Table.STRING_DEFAULT_VALUE);
        String a2 = com.alipay.security.mobile.module.commonutils.a.a(map, com.alipay.sdk.cons.b.g, Table.STRING_DEFAULT_VALUE);
        String str7 = Table.STRING_DEFAULT_VALUE;
        b a3 = com.alipay.apmobilesecuritysdk.e.a.a(this.a);
        if (a3 != null) {
            str7 = a3.a;
        }
        com.alipay.security.mobile.module.commonutils.d.a(str2, new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()) + ".log", new com.alipay.apmobilesecuritysdk.b.a(str3, str4, str5, str6, a, a2, str7).toString());
        String a4 = com.alipay.security.mobile.module.commonutils.a.a(map, "userId", Table.STRING_DEFAULT_VALUE);
        Date date = new Date();
        Object obj = (date.after(new Date(Header.ARRAY_DOUBLE, 10, 10, 0, 0, 0)) && date.before(new Date(Header.ARRAY_DOUBLE, 10, 11, 23, 59, 59))) ? 1 : null;
        if (obj != null) {
            obj = 1;
        } else {
            obj = (date.after(new Date(Header.ARRAY_DOUBLE, 11, 11, 0, 0, 0)) && date.before(new Date(Header.ARRAY_DOUBLE, 11, 12, 23, 59, 59))) ? 1 : null;
        }
        if (obj != null) {
            a3 = com.alipay.apmobilesecuritysdk.e.a.a(this.a);
            obj = a3 == null ? 1 : com.alipay.security.mobile.module.commonutils.a.a(a3.c) ? 1 : null;
        } else {
            com.alipay.apmobilesecuritysdk.c.e.a();
            b = com.alipay.apmobilesecuritysdk.c.e.b(this.a, map);
            b a5 = com.alipay.apmobilesecuritysdk.e.a.a(this.a);
            if (a5 == null) {
                obj = 1;
            } else {
                obj = (!com.alipay.security.mobile.module.commonutils.a.a(b, a5.b) ? 1 : null) != null ? 1 : !f.a(this.a) ? 1 : com.alipay.security.mobile.module.commonutils.a.a(a5.c) ? 1 : null;
            }
        }
        if (obj != null) {
            com.alipay.security.mobile.module.a.b.b b2 = b((Map) map);
            if (b2 != null) {
                if (!com.alipay.security.mobile.module.commonutils.a.a(b2.a) ? b2.c : false) {
                    Context context = this.a;
                    boolean equals = Constants.VIA_TO_TYPE_QQ_GROUP.equals(b2.h);
                    try {
                        Editor edit = context.getSharedPreferences("vkeyid_settings", 0).edit();
                        if (edit != null) {
                            edit.putString("log_switch", equals ? i.a(i.a(), Constants.VIA_TO_TYPE_QQ_GROUP) : i.a(i.a(), Constants.VIA_RESULT_SUCCESS));
                            edit.commit();
                        }
                    } catch (Throwable th) {
                    }
                    try {
                        b = e.c(this.a);
                        if (!(com.alipay.security.mobile.module.commonutils.a.a(b2.i) || com.alipay.security.mobile.module.commonutils.a.a(b, b2.i))) {
                            b = b2.b;
                            str4 = Table.STRING_DEFAULT_VALUE;
                            if (!a()) {
                                a(true);
                                try {
                                    new b(this, a4, b, str4).start();
                                } catch (Throwable th2) {
                                }
                            }
                        }
                        b = com.alipay.apmobilesecuritysdk.c.e.b(this.a, map);
                        if (b2 != null) {
                            b bVar2 = new b(b2, b);
                            com.alipay.apmobilesecuritysdk.e.a.a(this.a, bVar2);
                            f.a(bVar2);
                            context = this.a;
                            com.alipay.apmobilesecuritysdk.a.a.a.a(bVar2);
                            context = this.a;
                            long currentTimeMillis = System.currentTimeMillis();
                            try {
                                Editor edit2 = context.getSharedPreferences("vkeyid_settings", 0).edit();
                                if (edit2 != null) {
                                    edit2.putString("vkey_valid", i.a(i.a(), String.valueOf(currentTimeMillis)));
                                    edit2.commit();
                                }
                            } catch (Throwable th3) {
                            }
                        }
                        str3 = b2.b;
                        bVar = new com.alipay.apmobilesecuritysdk.b.b(str2, d.a(this.a));
                        Context context2 = this.a;
                        ConnectivityManager connectivityManager = (ConnectivityManager) context2.getSystemService("connectivity");
                        NetworkInfo activeNetworkInfo = connectivityManager != null ? null : connectivityManager.getActiveNetworkInfo();
                        obj = (activeNetworkInfo == null && activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == 1) ? 1 : null;
                        boolean a6 = e.a(context2);
                        if (obj != null && a6) {
                            new Thread(new com.alipay.apmobilesecuritysdk.b.c(bVar)).start();
                        }
                        return str3;
                    } catch (Throwable th4) {
                        Throwable th5 = th4;
                        b = str;
                        com.alipay.security.mobile.module.commonutils.d.a(th5);
                        return b;
                    }
                }
            }
            if (b2 != null) {
                com.alipay.security.mobile.module.commonutils.d.a("Server error, result:" + b2.d);
            } else {
                com.alipay.security.mobile.module.commonutils.d.a("Server error, response is null");
            }
        }
        str3 = a(this.a);
        try {
            bVar = new com.alipay.apmobilesecuritysdk.b.b(str2, d.a(this.a));
            Context context22 = this.a;
            ConnectivityManager connectivityManager2 = (ConnectivityManager) context22.getSystemService("connectivity");
            if (connectivityManager2 != null) {
            }
            if (activeNetworkInfo == null) {
            }
            boolean a62 = e.a(context22);
            new Thread(new com.alipay.apmobilesecuritysdk.b.c(bVar)).start();
            return str3;
        } catch (Throwable th42) {
            Throwable th6 = th42;
            b = str3;
            th5 = th6;
            com.alipay.security.mobile.module.commonutils.d.a(th5);
            return b;
        }
    }
}
