package com.alipay.apmobilesecuritysdk.d;

import android.content.Context;
import io.realm.internal.Table;

public final class b {
    public static String a(Context context) {
        try {
            return (String) Class.forName("com.ut.device.a").getMethod("getUtdid", new Class[]{Context.class}).invoke(null, new Object[]{context});
        } catch (Exception e) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }
}
