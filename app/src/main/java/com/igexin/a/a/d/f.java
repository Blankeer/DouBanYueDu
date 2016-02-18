package com.igexin.a.a.d;

import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

final class f {
    final BlockingQueue a;
    final HashMap b;
    final ReentrantLock c;
    ThreadFactory d;
    volatile long e;
    volatile int f;
    volatile int g;
    volatile int h;
    final /* synthetic */ e i;

    public f(e eVar) {
        this.i = eVar;
        this.c = new ReentrantLock();
        this.a = new SynchronousQueue();
        this.b = new HashMap();
        this.e = TimeUnit.SECONDS.toNanos(60);
        this.f = 0;
        this.d = new h(this);
        this.h = AdvancedShareActionProvider.WEIGHT_MAX;
    }

    final d a() {
        while (true) {
            try {
                d dVar = this.g > this.f ? (d) this.a.poll(this.e, TimeUnit.NANOSECONDS) : (d) this.a.take();
                if (dVar != null) {
                    return dVar;
                }
                if (this.a.isEmpty()) {
                    return null;
                }
            } catch (InterruptedException e) {
            }
        }
    }

    final void a(d dVar) {
        if (dVar == null) {
            throw new NullPointerException();
        }
        if (dVar.K != 0) {
            ReentrantLock reentrantLock = this.c;
            reentrantLock.lock();
            try {
                g gVar = (g) this.b.get(Integer.valueOf(dVar.K));
                if (gVar != null) {
                    gVar.a.offer(dVar);
                    return;
                }
                reentrantLock.unlock();
            } finally {
                reentrantLock.unlock();
            }
        }
        b(dVar);
    }

    final boolean a(g gVar) {
        ReentrantLock reentrantLock = this.c;
        reentrantLock.lock();
        try {
            int i = this.g - 1;
            this.g = i;
            if (i == 0 && !this.a.isEmpty()) {
                Thread f = f(null);
                if (f != null) {
                    f.start();
                }
            } else if (!gVar.a.isEmpty()) {
                reentrantLock.unlock();
                return true;
            }
            this.b.remove(Integer.valueOf(gVar.d));
            return false;
        } finally {
            reentrantLock.unlock();
        }
    }

    final void b(d dVar) {
        if (this.g < this.f && c(dVar)) {
            return;
        }
        if (this.a.offer(dVar)) {
            if (this.g == 0) {
                e(dVar);
            }
        } else if (!d(dVar)) {
        }
    }

    final boolean c(d dVar) {
        Thread thread = null;
        ReentrantLock reentrantLock = this.c;
        reentrantLock.lock();
        try {
            if (this.g < this.f) {
                thread = f(dVar);
            }
            reentrantLock.unlock();
            if (thread == null) {
                return false;
            }
            thread.start();
            return true;
        } catch (Throwable th) {
            reentrantLock.unlock();
        }
    }

    final boolean d(d dVar) {
        Thread thread = null;
        ReentrantLock reentrantLock = this.c;
        reentrantLock.lock();
        try {
            if (this.g < this.h) {
                thread = f(dVar);
            }
            reentrantLock.unlock();
            if (thread == null) {
                return false;
            }
            thread.start();
            return true;
        } catch (Throwable th) {
            reentrantLock.unlock();
        }
    }

    final void e(d dVar) {
        Thread thread = null;
        ReentrantLock reentrantLock = this.c;
        reentrantLock.lock();
        try {
            if (this.g < Math.max(this.f, 1) && !this.a.isEmpty()) {
                thread = f(null);
            }
            reentrantLock.unlock();
            if (thread != null) {
                thread.start();
            }
        } catch (Throwable th) {
            reentrantLock.unlock();
        }
    }

    final Thread f(d dVar) {
        Runnable gVar = new g(this, dVar);
        if (!(dVar == null || dVar.K == 0)) {
            this.b.put(Integer.valueOf(dVar.K), gVar);
        }
        Thread newThread = this.d.newThread(gVar);
        if (newThread != null) {
            this.g++;
        }
        return newThread;
    }
}
