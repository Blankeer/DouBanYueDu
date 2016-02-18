package com.igexin.a.a.b.a.a;

import com.igexin.a.a.b.c;
import com.igexin.a.a.b.d;
import com.igexin.a.a.b.f;
import java.nio.ByteBuffer;

public final class g extends f {
    e e;
    ByteBuffer f;

    public g(String str, c cVar) {
        super(str, cVar);
        this.C = true;
    }

    public void a_() {
        super.a_();
        this.e.o.offer(this);
        if (!this.e.r() && !d.c().a(this.e, true)) {
            throw new IllegalStateException("NioSocketTask is invalid");
        } else if (this.e.h) {
            this.e.i();
        }
    }

    public final int b() {
        return -2046;
    }

    public final void d() {
        super.d();
        this.e = e.a(this.a, this.b);
    }

    protected void e() {
    }

    public void f() {
        this.e = null;
        super.f();
    }
}
