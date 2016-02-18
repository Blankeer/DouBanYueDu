package com.igexin.push.config;

import com.igexin.push.core.g;

public class j {
    private static j a;

    private j() {
    }

    public static synchronized j a() {
        j jVar;
        synchronized (j.class) {
            if (a == null) {
                a = new j();
            }
            jVar = a;
        }
        return jVar;
    }

    public boolean b() {
        o.a();
        m.a();
        m.a(g.g);
        return true;
    }
}
