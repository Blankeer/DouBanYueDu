package com.alipay.security.mobile.module.localstorage;

import com.alipay.security.mobile.module.localstorage.util.a;

public final class d {
    private static String a(String str) {
        return System.getProperty(str);
    }

    private static void a(String str, String str2) {
        if (!a.a(str2)) {
            System.setProperty(str, str2);
        }
    }
}
