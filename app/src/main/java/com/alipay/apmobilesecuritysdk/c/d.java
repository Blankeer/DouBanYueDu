package com.alipay.apmobilesecuritysdk.c;

import android.content.Context;
import com.tencent.connect.common.Constants;
import java.util.HashMap;
import java.util.Map;

public final class d {
    public static synchronized Map<String, String> a(Context context) {
        Map<String, String> hashMap;
        synchronized (d.class) {
            com.alipay.security.mobile.module.deviceinfo.d.a();
            hashMap = new HashMap();
            hashMap.put("AE1", com.alipay.security.mobile.module.deviceinfo.d.b());
            hashMap.put("AE2", (com.alipay.security.mobile.module.deviceinfo.d.c() ? Constants.VIA_TO_TYPE_QQ_GROUP : Constants.VIA_RESULT_SUCCESS));
            hashMap.put("AE3", (com.alipay.security.mobile.module.deviceinfo.d.a(context) ? Constants.VIA_TO_TYPE_QQ_GROUP : Constants.VIA_RESULT_SUCCESS));
            hashMap.put("AE4", com.alipay.security.mobile.module.deviceinfo.d.d());
            hashMap.put("AE5", com.alipay.security.mobile.module.deviceinfo.d.e());
            hashMap.put("AE6", com.alipay.security.mobile.module.deviceinfo.d.f());
            hashMap.put("AE7", com.alipay.security.mobile.module.deviceinfo.d.g());
            hashMap.put("AE8", com.alipay.security.mobile.module.deviceinfo.d.h());
            hashMap.put("AE9", com.alipay.security.mobile.module.deviceinfo.d.i());
            hashMap.put("AE10", com.alipay.security.mobile.module.deviceinfo.d.j());
            hashMap.put("AE11", com.alipay.security.mobile.module.deviceinfo.d.k());
            hashMap.put("AE12", com.alipay.security.mobile.module.deviceinfo.d.l());
            hashMap.put("AE13", com.alipay.security.mobile.module.deviceinfo.d.m());
            hashMap.put("AE14", com.alipay.security.mobile.module.deviceinfo.d.n());
            hashMap.put("AE15", com.alipay.security.mobile.module.deviceinfo.d.o());
        }
        return hashMap;
    }
}
