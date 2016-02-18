package com.igexin.a.a.d;

import android.os.PowerManager.WakeLock;
import com.douban.book.reader.event.PageFlipEvent;
import com.igexin.a.a.d.a.a;
import com.igexin.a.a.d.a.e;
import com.igexin.a.a.d.a.g;
import com.tencent.mm.sdk.modelbase.BaseResp.ErrCode;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import u.aly.dx;

public abstract class d extends a implements a {
    protected static e P;
    protected volatile boolean A;
    protected volatile boolean B;
    protected volatile boolean C;
    protected volatile boolean D;
    protected volatile boolean E;
    protected volatile long F;
    volatile int G;
    public long H;
    public int I;
    public int J;
    public int K;
    public int L;
    public Exception M;
    public Object N;
    public g O;
    protected final ReentrantLock Q;
    protected final Condition R;
    Thread S;
    protected volatile boolean T;
    WakeLock U;
    int V;
    protected com.igexin.a.a.d.a.d W;
    private byte a;
    protected volatile boolean w;
    protected volatile boolean x;
    protected volatile boolean y;
    protected volatile boolean z;

    public d(int i) {
        this(i, null);
    }

    public d(int i, com.igexin.a.a.d.a.d dVar) {
        this.K = i;
        this.W = dVar;
        this.Q = new ReentrantLock();
        this.R = this.Q.newCondition();
    }

    public final int a(long j, TimeUnit timeUnit) {
        if (j <= 0) {
            return 0;
        }
        switch (P.m.a(this, j, timeUnit)) {
            case ErrCode.ERR_USER_CANCEL /*-2*/:
                return -2;
            case PageFlipEvent.FLIP_TO_PREV /*-1*/:
                this.F = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(j, timeUnit);
                return -1;
            case dx.b /*1*/:
                return 1;
            default:
                return 0;
        }
    }

    public long a(TimeUnit timeUnit) {
        return timeUnit.convert(n(), TimeUnit.MILLISECONDS);
    }

    public final void a(int i) {
        this.a = (byte) (this.a & 15);
        this.a = (byte) (this.a | ((i & 15) << 4));
    }

    public final void a(int i, g gVar) {
        if (i < 0) {
            throw new IllegalArgumentException("second must > 0");
        }
        this.J = i;
        this.O = gVar;
    }

    public final void a(long j) {
        this.H = j;
    }

    public void a(WakeLock wakeLock) {
        this.U = wakeLock;
    }

    public final void a(com.igexin.a.a.d.a.d dVar) {
        this.W = dVar;
    }

    public void a_() {
        this.S = Thread.currentThread();
        this.A = true;
    }

    public void c() {
        if (this.w || this.x) {
            f();
        }
    }

    public void d() {
        this.D = true;
    }

    protected abstract void e();

    public void f() {
        this.N = null;
        this.M = null;
        this.S = null;
    }

    public WakeLock l() {
        return this.U;
    }

    final void m() {
        this.V++;
        this.V &= 1090519038;
    }

    long n() {
        return this.F - System.currentTimeMillis();
    }

    public final void o() {
        this.w = true;
    }

    public final boolean p() {
        return this.y;
    }

    public final boolean q() {
        return this.x;
    }

    public final boolean r() {
        return this.D;
    }

    protected void s() {
    }

    public final void t() {
        this.x = true;
    }

    protected void u() {
        if (!this.z && !this.B && !this.C) {
            this.w = true;
            this.A = false;
        } else if (this.B && !this.w) {
            this.A = false;
        } else if (this.z && !this.y && !this.w) {
            this.A = false;
        }
    }

    protected void v() {
        if (this.W != null) {
            this.W.a(e.error);
        }
    }
}
