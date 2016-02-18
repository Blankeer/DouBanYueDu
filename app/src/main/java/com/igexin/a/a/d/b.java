package com.igexin.a.a.d;

import com.igexin.a.a.d.a.g;
import java.util.concurrent.TimeUnit;

public abstract class b implements g {
    protected boolean a;

    public b() {
        this.a = true;
    }

    public void a() {
        this.a = false;
    }

    public boolean a(long j, d dVar) {
        return TimeUnit.SECONDS.toMillis((long) dVar.J) < j - dVar.H;
    }

    public long b(long j, d dVar) {
        return (TimeUnit.SECONDS.toMillis((long) dVar.J) + dVar.H) - j;
    }

    public boolean b() {
        return this.a;
    }
}
