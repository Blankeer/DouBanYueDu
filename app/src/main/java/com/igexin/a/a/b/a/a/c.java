package com.igexin.a.a.b.a.a;

import com.igexin.a.a.b.f;
import com.igexin.a.a.c.a;

public class c extends f {
    e e;

    public c(String str) {
        super(str, null);
        this.a = this.a.replaceFirst("disConnect", "socket");
        this.C = true;
    }

    public void a_() {
        super.a_();
        a.a("DisSocketTask|run|" + this.e.f.get());
        if (!this.e.f.get()) {
            int i = 0;
            while (!this.e.f.compareAndSet(false, true)) {
                int i2 = i + 1;
                if (i > 10) {
                    break;
                }
                i = i2;
            }
        }
        a.a("DisSocketTask|run2|" + this.e.f.get());
        if (this.e.f.get()) {
            this.e.i();
        }
    }

    public final int b() {
        return -2045;
    }

    public void d() {
        super.d();
        this.e = e.a(this.a, this.b);
    }

    protected void e() {
    }
}
