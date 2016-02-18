package com.igexin.push.e.b;

import com.igexin.a.a.c.a;
import com.igexin.push.core.a.e;
import com.igexin.push.core.f;
import com.igexin.push.core.g;
import com.igexin.push.core.i;
import java.util.concurrent.TimeUnit;

public class c extends f {
    private static c a;

    public c() {
        super(i.a().b());
        this.z = true;
    }

    public static c g() {
        if (a == null) {
            a = new c();
        }
        return a;
    }

    protected void a() {
        e.a().B();
        g.E = System.currentTimeMillis();
        if (g.m) {
            a.b("heartbeatReq");
            f.a().i().f();
            return;
        }
        a.b("HeartBeatTimerTask doTaskMethod isOnline = false, refresh wait time !!!!!!");
        h();
    }

    public final int b() {
        return -2147483642;
    }

    public void c() {
        super.c();
        if (!this.w) {
            h();
        }
    }

    public void d() {
    }

    public void h() {
        a(i.a().b(), TimeUnit.MILLISECONDS);
    }

    public void i() {
    }
}
