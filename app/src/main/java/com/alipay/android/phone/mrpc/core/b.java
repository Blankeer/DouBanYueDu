package com.alipay.android.phone.mrpc.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class b implements Serializable {
    private static final long b = -6098125857367743614L;
    Map<String, String> a;

    public b() {
        this.a = new HashMap();
    }

    private String a(String str) {
        return (String) this.a.get(str);
    }

    private Map<String, String> a() {
        return this.a;
    }

    private void a(String str, String str2) {
        this.a.put(str, str2);
    }

    private void a(Map<String, String> map) {
        this.a = map;
    }
}
