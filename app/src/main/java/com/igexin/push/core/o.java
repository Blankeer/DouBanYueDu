package com.igexin.push.core;

import com.igexin.push.config.k;
import com.igexin.sdk.aidl.c;

public class o {
    private static String a;
    private static c b;

    static {
        a = k.a;
    }

    public static c a() {
        if (b == null) {
            b = new p();
        }
        return b;
    }
}
