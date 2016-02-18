package com.igexin.push.e.b;

import com.igexin.a.a.d.d;
import java.util.concurrent.TimeUnit;

public abstract class f extends d {
    long f;

    public f(long j) {
        this(0, j);
    }

    public f(long j, long j2) {
        super(5);
        if (j > 0) {
            j2 += j - System.currentTimeMillis();
        }
        this.f = j2;
        a(this.f, TimeUnit.MILLISECONDS);
    }

    protected abstract void a();

    public final void a_() {
        super.a_();
        a();
    }

    protected void e() {
    }
}
