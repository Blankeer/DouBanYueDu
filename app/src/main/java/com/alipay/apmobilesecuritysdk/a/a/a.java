package com.alipay.apmobilesecuritysdk.a.a;

import android.content.Context;
import com.alipay.apmobilesecuritysdk.e.b;
import com.alipay.security.mobile.module.commonutils.crypto.i;
import com.alipay.security.mobile.module.localstorage.c;
import io.realm.internal.Table;
import java.util.Map;
import org.json.JSONObject;

public final class a {
    public static synchronized String a() {
        String str = null;
        synchronized (a.class) {
            String b = b();
            if (!com.alipay.security.mobile.module.commonutils.a.a(b)) {
                String[] split = b.split("`");
                if (split != null && split.length >= 2) {
                    str = split[0];
                }
            }
        }
        return str;
    }

    public static synchronized String a(Context context) {
        String a;
        synchronized (a.class) {
            a = a();
            if (com.alipay.security.mobile.module.commonutils.a.a(a)) {
                a = b(context);
            }
        }
        return a;
    }

    public static synchronized void a(b bVar) {
        synchronized (a.class) {
            if (!com.alipay.security.mobile.module.commonutils.a.a(bVar.a)) {
                if (!bVar.a.equals(a())) {
                    String str = bVar.a + "`" + bVar.d;
                    if (str != null) {
                        try {
                            str = i.a(i.a(), str);
                            JSONObject jSONObject = new JSONObject();
                            jSONObject.put("device", str);
                            com.alipay.security.mobile.module.localstorage.a.a("deviceid_v2", jSONObject.toString());
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    private static String b() {
        try {
            return i.b(i.a(), new JSONObject(com.alipay.security.mobile.module.localstorage.a.a("deviceid_v2")).getString("device"));
        } catch (Exception e) {
            return null;
        }
    }

    public static synchronized String b(Context context) {
        String str = null;
        synchronized (a.class) {
            String str2 = Table.STRING_DEFAULT_VALUE;
            try {
                String a = c.a(context, "profiles", "deviceid", Table.STRING_DEFAULT_VALUE);
                a = com.alipay.security.mobile.module.commonutils.a.a(a) ? null : i.b(i.a(), a);
                if (!com.alipay.security.mobile.module.commonutils.a.a(a)) {
                    b bVar = new b();
                    Map a2 = b.a(a);
                    if (a2 != null) {
                        str = (String) a2.get("deviceId");
                    }
                    str = str2;
                }
            } catch (Throwable th) {
            }
        }
        return str;
    }
}
