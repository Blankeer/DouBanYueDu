package com.alipay.apmobilesecuritysdk.f;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import com.alipay.security.mobile.module.commonutils.a;
import com.alipay.security.mobile.module.commonutils.crypto.d;
import com.alipay.security.mobile.module.localstorage.c;
import io.realm.internal.Table;
import java.util.UUID;

public final class b {
    private static String a;

    static {
        a = Table.STRING_DEFAULT_VALUE;
    }

    public static synchronized String a(Context context) {
        String a;
        synchronized (b.class) {
            if (a.a(a)) {
                a = c.a(context, "alipay_vkey_random", "random", Table.STRING_DEFAULT_VALUE);
                a = a;
                if (a.a(a)) {
                    a = d.a(UUID.randomUUID().toString());
                    a = "alipay_vkey_random";
                    String str = "random";
                    String str2 = a;
                    if (str2 != null) {
                        Editor edit = context.getSharedPreferences(a, 0).edit();
                        if (edit != null) {
                            edit.clear();
                            edit.putString(str, str2);
                            edit.commit();
                        }
                    }
                }
            }
            a = a;
        }
        return a;
    }
}
