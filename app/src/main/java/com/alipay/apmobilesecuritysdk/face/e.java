package com.alipay.apmobilesecuritysdk.face;

import android.content.Context;
import com.alipay.apmobilesecuritysdk.a.a;
import java.util.Map;

public final class e {
    public static synchronized String a(Context context, Map<String, String> map) {
        String a;
        synchronized (e.class) {
            a = new a(context).a((Map) map);
        }
        return a;
    }
}
