package com.alipay.apmobilesecuritysdk.c;

import android.content.Context;
import com.alipay.security.mobile.module.commonutils.a;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Map;

public final class b {
    public static synchronized Map<String, String> a(Context context, Map<String, String> map) {
        Map<String, String> hashMap;
        synchronized (b.class) {
            hashMap = new HashMap();
            String a = a.a(map, com.alipay.sdk.cons.b.c, Table.STRING_DEFAULT_VALUE);
            String a2 = a.a(map, com.alipay.sdk.cons.b.g, Table.STRING_DEFAULT_VALUE);
            String a3 = com.alipay.apmobilesecuritysdk.f.b.a(context);
            String a4 = a.a(map, "userId", Table.STRING_DEFAULT_VALUE);
            hashMap.put("AC1", a);
            hashMap.put("AC2", a2);
            hashMap.put("AC3", Table.STRING_DEFAULT_VALUE);
            hashMap.put("AC4", a3);
            hashMap.put("AC5", a4);
        }
        return hashMap;
    }
}
