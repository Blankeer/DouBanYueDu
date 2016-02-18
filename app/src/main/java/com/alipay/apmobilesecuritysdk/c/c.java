package com.alipay.apmobilesecuritysdk.c;

import android.content.Context;
import com.alipay.apmobilesecuritysdk.e.d;
import com.alipay.security.mobile.module.commonutils.a;
import com.alipay.security.mobile.module.deviceinfo.b;
import com.douban.amonsul.StatConstant;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public final class c {
    public static Map<String, String> a(Context context) {
        String str;
        b a = b.a();
        Map<String, String> hashMap = new HashMap();
        d a2 = com.alipay.apmobilesecuritysdk.e.c.a(context);
        String a3 = b.a(context);
        String b = b.b(context);
        String h = b.h(context);
        String h2 = b.h();
        String j = b.j(context);
        if (a2 != null) {
            if (a.a(a3)) {
                a3 = a.c(a2.a);
            }
            if (a.a(b)) {
                b = a.c(a2.b);
            }
            if (a.a(h)) {
                h = a.c(a2.c);
            }
            if (a.a(h2)) {
                h2 = a.c(a2.d);
            }
            if (a.a(j)) {
                j = a.c(a2.e);
            }
            str = j;
            j = h2;
            h2 = h;
            h = b;
            b = a3;
        } else {
            str = j;
            j = h2;
            h2 = h;
            h = b;
            b = a3;
        }
        d dVar = new d(b, h, h2, j, str);
        if (context != null) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("imei", a.c(dVar.a));
                jSONObject.put("imsi", a.c(dVar.b));
                jSONObject.put(StatConstant.JSON_KEY_MAC, a.c(dVar.c));
                jSONObject.put("bluetoothmac", a.c(dVar.d));
                jSONObject.put("gsi", a.c(dVar.e));
                a3 = jSONObject.toString();
                com.alipay.apmobilesecuritysdk.f.c.a("device_feature_file_name", "device_feature_file_key", a3);
                com.alipay.apmobilesecuritysdk.f.c.a(context, "device_feature_prefs_name", "device_feature_prefs_key", a3);
            } catch (Throwable e) {
                com.alipay.security.mobile.module.commonutils.d.a(e);
            }
        }
        hashMap.put("AD1", b);
        hashMap.put("AD2", h);
        hashMap.put("AD3", b.d(context));
        hashMap.put("AD5", b.e(context));
        hashMap.put("AD6", b.f(context));
        hashMap.put("AD7", b.g(context));
        hashMap.put("AD8", h2);
        hashMap.put("AD9", b.i(context));
        hashMap.put("AD10", str);
        hashMap.put("AD11", b.b());
        hashMap.put("AD12", a.c());
        hashMap.put("AD13", b.d());
        hashMap.put("AD14", b.e());
        hashMap.put("AD15", b.f());
        hashMap.put("AD16", b.g());
        hashMap.put("AD17", Table.STRING_DEFAULT_VALUE);
        hashMap.put("AD18", j);
        hashMap.put("AD19", b.k(context));
        hashMap.put("AD20", b.i());
        hashMap.put("AD21", b.c(context));
        hashMap.put("AD22", Table.STRING_DEFAULT_VALUE);
        hashMap.put("AD23", b.j());
        hashMap.put("AL3", b.l(context));
        return hashMap;
    }
}
