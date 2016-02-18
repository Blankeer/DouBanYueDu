package com.igexin.push.core.b;

import java.util.HashMap;
import java.util.Map;

public class b {
    public static b a;
    private Map b;

    public b() {
        this.b = new HashMap();
    }

    public static b a() {
        if (a == null) {
            a = new b();
        }
        return a;
    }

    public a a(String str) {
        return (a) this.b.get(str);
    }

    public void a(String str, a aVar) {
        this.b.put(str, aVar);
    }
}
