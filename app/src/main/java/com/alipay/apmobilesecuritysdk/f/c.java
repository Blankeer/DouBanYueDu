package com.alipay.apmobilesecuritysdk.f;

import android.content.Context;
import com.alipay.security.mobile.module.commonutils.a;
import com.alipay.security.mobile.module.commonutils.crypto.i;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public final class c {
    public static String a(Context context, String str, String str2) {
        String str3 = null;
        if (!(context == null || a.a(str) || a.a(str2))) {
            try {
                String a = com.alipay.security.mobile.module.localstorage.c.a(context, str, str2, Table.STRING_DEFAULT_VALUE);
                if (!a.a(a)) {
                    str3 = i.b(i.a(), a);
                }
            } catch (Exception e) {
            }
        }
        return str3;
    }

    public static String a(String str, String str2) {
        String str3 = null;
        if (!(a.a(str) || a.a(str2))) {
            try {
                String a = com.alipay.security.mobile.module.localstorage.a.a(str);
                if (!a.a(a)) {
                    a = new JSONObject(a).getString(str2);
                    if (!a.a(a)) {
                        str3 = i.b(i.a(), a);
                    }
                }
            } catch (Exception e) {
            }
        }
        return str3;
    }

    public static void a(Context context, String str, String str2, String str3) {
        if (!a.a(str) && !a.a(str2) && context != null && !a.a(str3)) {
            try {
                String a = i.a(i.a(), str3);
                Map hashMap = new HashMap();
                hashMap.put(str2, a);
                com.alipay.security.mobile.module.localstorage.c.a(context, str, hashMap);
            } catch (Exception e) {
            }
        }
    }

    public static void a(String str, String str2, String str3) {
        if (!a.a(str) && !a.a(str2) && !a.a(str3)) {
            try {
                String a = i.a(i.a(), str3);
                JSONObject jSONObject = new JSONObject();
                jSONObject.put(str2, a);
                com.alipay.security.mobile.module.localstorage.a.a(str, jSONObject.toString());
            } catch (Exception e) {
            }
        }
    }
}
