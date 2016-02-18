package com.igexin.push.a.a;

import com.igexin.push.config.l;
import com.igexin.push.core.a.e;
import com.igexin.push.core.f;
import com.igexin.push.e.b.d;
import com.umeng.analytics.a;
import java.util.Calendar;

public class c implements d {
    private static c c;
    private long a;
    private long b;
    private boolean d;

    private c() {
        this.a = 0;
        this.b = 0;
        this.d = false;
    }

    public static c c() {
        if (c == null) {
            c = new c();
        }
        return c;
    }

    public void a() {
        d();
    }

    public void a(long j) {
        this.a = j;
    }

    public boolean b() {
        return System.currentTimeMillis() - this.a > this.b;
    }

    public void d() {
        this.b = a.h;
        long currentTimeMillis = System.currentTimeMillis();
        if (l.b != 0) {
            Calendar instance = Calendar.getInstance();
            com.igexin.push.d.a aVar;
            if (e.a().a(currentTimeMillis)) {
                if (!this.d) {
                    this.d = true;
                    aVar = new com.igexin.push.d.a();
                    aVar.a(com.igexin.push.core.c.stop);
                    f.a().h().a(aVar);
                }
                if (l.a + l.b > 24) {
                    instance.set(11, (l.a + l.b) - 24);
                } else {
                    instance.set(11, l.a + l.b);
                }
                instance.set(12, 0);
                instance.set(13, 0);
                if (instance.getTimeInMillis() < currentTimeMillis) {
                    instance.add(5, 1);
                }
            } else {
                if (this.d) {
                    this.d = false;
                    aVar = new com.igexin.push.d.a();
                    aVar.a(com.igexin.push.core.c.start);
                    f.a().h().a(aVar);
                }
                instance.set(11, l.a);
                instance.set(12, 0);
                instance.set(13, 0);
                if (instance.getTimeInMillis() < currentTimeMillis) {
                    instance.add(5, 1);
                }
            }
            this.b = instance.getTimeInMillis() - currentTimeMillis;
        } else if (this.d) {
            this.d = false;
            com.igexin.push.d.a aVar2 = new com.igexin.push.d.a();
            aVar2.a(com.igexin.push.core.c.start);
            f.a().h().a(aVar2);
        }
        if (l.c > this.b + currentTimeMillis) {
            this.b = l.c - currentTimeMillis;
            if (!this.d) {
                this.d = true;
                com.igexin.push.d.a aVar3 = new com.igexin.push.d.a();
                aVar3.a(com.igexin.push.core.c.stop);
                f.a().h().a(aVar3);
            }
        }
    }
}
