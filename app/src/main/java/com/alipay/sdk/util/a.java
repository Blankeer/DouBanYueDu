package com.alipay.sdk.util;

import android.content.Context;
import android.text.TextUtils;
import com.alipay.sdk.cons.b;
import io.realm.internal.Table;

public final class a {
    public static String[] a(String str) {
        int indexOf = str.indexOf(40) + 1;
        int lastIndexOf = str.lastIndexOf(41);
        if (indexOf == 0 || lastIndexOf == -1) {
            return null;
        }
        String[] split = str.substring(indexOf, lastIndexOf).split(",");
        if (split != null) {
            for (indexOf = 0; indexOf < split.length; indexOf++) {
                if (!TextUtils.isEmpty(split[indexOf])) {
                    split[indexOf] = split[indexOf].trim();
                    split[indexOf] = split[indexOf].replaceAll("'", Table.STRING_DEFAULT_VALUE).replaceAll("\"", Table.STRING_DEFAULT_VALUE);
                }
            }
        }
        return split;
    }

    private static void b(String str) {
        String[] a = a(str);
        if (a.length == 3 && TextUtils.equals(b.c, a[0])) {
            Context context = com.alipay.sdk.sys.b.a().a;
            com.alipay.sdk.tid.b a2 = com.alipay.sdk.tid.b.a();
            if (!TextUtils.isEmpty(a[1]) && !TextUtils.isEmpty(a[2])) {
                a2.a = a[1];
                a2.b = a[2];
                com.alipay.sdk.tid.a aVar = new com.alipay.sdk.tid.a(context);
                try {
                    aVar.a(b.a(context).a(), b.a(context).b(), a2.a, a2.b);
                } catch (Exception e) {
                } finally {
                    aVar.close();
                }
            }
        }
    }
}
