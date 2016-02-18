package com.igexin.push.e.b;

import com.igexin.a.a.b.d;
import com.igexin.push.config.k;
import com.igexin.push.core.f;
import com.igexin.push.core.g;
import com.umeng.analytics.a;
import java.util.concurrent.TimeUnit;

public class e extends f {
    private static final String a;
    private static e b;

    static {
        a = k.a;
    }

    private e() {
        super(a.h);
        this.z = true;
    }

    public static e g() {
        if (b == null) {
            b = new e();
        }
        return b;
    }

    protected void a() {
        com.igexin.push.core.a.e.a().B();
        boolean a = com.igexin.push.core.a.e.a().a(System.currentTimeMillis());
        boolean b = com.igexin.push.f.a.b();
        if (g.i && g.j && g.k && !g.m && !a && b && !g.m) {
            com.igexin.a.a.c.a.b("ReconnectTimerTask|doTaskMethod|do login before");
            int d = f.a().i().d();
            com.igexin.a.a.c.a.b("ReconnectTimerTask|doTaskMethod|do login result|" + d);
            if (d != 1 && d == 0) {
                d.c().a((Object) new com.igexin.push.c.b.a());
                d.c().d();
            }
            a(1800000, TimeUnit.MILLISECONDS);
            return;
        }
        com.igexin.a.a.c.a.b("ReconnectTimerTask|doTaskMethod|stop");
        a((long) a.h, TimeUnit.MILLISECONDS);
    }

    public final int b() {
        return -2147483641;
    }

    public void c() {
        super.c();
    }

    public void d() {
    }

    public void h() {
        long c = f.a().g().c();
        com.igexin.a.a.c.a.b("ReconnectTimerTask|refreshDelayTime|" + c);
        a(c, TimeUnit.MILLISECONDS);
    }
}
