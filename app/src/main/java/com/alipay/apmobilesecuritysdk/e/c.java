package com.alipay.apmobilesecuritysdk.e;

import android.content.Context;
import com.alipay.security.mobile.module.commonutils.a;
import com.alipay.security.mobile.module.commonutils.d;
import com.douban.amonsul.StatConstant;
import org.json.JSONObject;

public final class c {
    public static d a(Context context) {
        if (context == null) {
            return null;
        }
        String a = com.alipay.apmobilesecuritysdk.f.c.a(context, "device_feature_prefs_name", "device_feature_prefs_key");
        if (a.a(a)) {
            a = com.alipay.apmobilesecuritysdk.f.c.a("device_feature_file_name", "device_feature_file_key");
        }
        if (a.a(a)) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject(a);
            d dVar = new d();
            dVar.a = jSONObject.getString("imei");
            dVar.b = jSONObject.getString("imsi");
            dVar.c = jSONObject.getString(StatConstant.JSON_KEY_MAC);
            dVar.d = jSONObject.getString("bluetoothmac");
            dVar.e = jSONObject.getString("gsi");
            return dVar;
        } catch (Throwable e) {
            d.a(e);
            return null;
        }
    }
}
