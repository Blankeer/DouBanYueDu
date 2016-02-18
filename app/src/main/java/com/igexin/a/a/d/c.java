package com.igexin.a.a.d;

import com.igexin.a.a.c.a;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class c {
    static final /* synthetic */ boolean h;
    final transient ReentrantLock a;
    final transient Condition b;
    final TreeSet c;
    final AtomicInteger d;
    int e;
    e f;
    public final AtomicLong g;

    static {
        h = !c.class.desiredAssertionStatus();
    }

    public c(Comparator comparator, e eVar) {
        this.a = new ReentrantLock();
        this.b = this.a.newCondition();
        this.d = new AtomicInteger(0);
        this.g = new AtomicLong(-1);
        this.c = new TreeSet(comparator);
        this.f = eVar;
    }

    private d e() {
        d a = a();
        return (a != null && this.c.remove(a)) ? a : null;
    }

    public final int a(d dVar, long j, TimeUnit timeUnit) {
        ReentrantLock reentrantLock = this.a;
        reentrantLock.lock();
        try {
            if (!this.c.contains(dVar)) {
                return -1;
            }
            this.c.remove(dVar);
            dVar.F = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(j, timeUnit);
            int i = a(dVar) ? 1 : -2;
            reentrantLock.unlock();
            return i;
        } finally {
            reentrantLock.unlock();
        }
    }

    d a() {
        try {
            return (d) this.c.first();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public final boolean a(d dVar) {
        if (dVar == null) {
            return false;
        }
        ReentrantLock reentrantLock = this.a;
        reentrantLock.lock();
        try {
            d a = a();
            int i = this.e + 1;
            this.e = i;
            dVar.G = i;
            if (this.c.add(dVar)) {
                dVar.m();
                if (a == null || this.c.comparator().compare(dVar, a) < 0) {
                    this.b.signalAll();
                }
                a.a("ScheduleQueue|offer|succeeded|" + dVar.getClass().getName() + "|" + dVar.hashCode() + "|" + dVar.a(TimeUnit.MILLISECONDS));
                reentrantLock.unlock();
                return true;
            }
            dVar.G--;
            a.a("ScheduleQueue|offer|failed|" + dVar.getClass().getName() + "|" + dVar.hashCode() + "|" + dVar.a(TimeUnit.MILLISECONDS));
            return false;
        } catch (Exception e) {
            a.a("ScheduleQueue|offer|exception|" + dVar.getClass().getName() + "|" + dVar.a(TimeUnit.MILLISECONDS));
            return false;
        } finally {
            reentrantLock.unlock();
        }
    }

    public final boolean a(Class cls) {
        if (cls == null) {
            return false;
        }
        ReentrantLock reentrantLock = this.a;
        reentrantLock.lock();
        try {
            Collection arrayList = new ArrayList();
            a.a("ScheduleQueue|removeByType|" + cls.getName());
            Iterator it = this.c.iterator();
            while (it.hasNext()) {
                d dVar = (d) it.next();
                if (dVar.getClass() == cls) {
                    arrayList.add(dVar);
                }
            }
            a.a("ScheduleQueue|removeByType|" + cls.getName() + "|" + arrayList.size());
            this.c.removeAll(arrayList);
            return true;
        } finally {
            reentrantLock.unlock();
        }
    }

    final boolean b() {
        ReentrantLock reentrantLock = this.a;
        reentrantLock.lock();
        try {
            boolean isEmpty = this.c.isEmpty();
            return isEmpty;
        } finally {
            reentrantLock.unlock();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.igexin.a.a.d.d c() {
        /*
        r10 = this;
        r2 = 1;
        r1 = 0;
        r3 = r10.a;
        r3.lockInterruptibly();
    L_0x0007:
        r4 = r10.a();	 Catch:{ all -> 0x0021 }
        if (r4 != 0) goto L_0x0026;
    L_0x000d:
        r0 = r10.d;	 Catch:{ all -> 0x0021 }
        r4 = 1;
        r0.set(r4);	 Catch:{ all -> 0x0021 }
        r0 = 0;
        r10.e = r0;	 Catch:{ all -> 0x0021 }
        r0 = "ScheduleQueue|take|forever";
        com.igexin.a.a.c.a.a(r0);	 Catch:{ all -> 0x0021 }
        r0 = r10.b;	 Catch:{ all -> 0x0021 }
        r0.await();	 Catch:{ all -> 0x0021 }
        goto L_0x0007;
    L_0x0021:
        r0 = move-exception;
        r3.unlock();
        throw r0;
    L_0x0026:
        r0 = java.util.concurrent.TimeUnit.NANOSECONDS;	 Catch:{ all -> 0x0021 }
        r6 = r4.a(r0);	 Catch:{ all -> 0x0021 }
        r0 = r4.w;	 Catch:{ all -> 0x0021 }
        if (r0 != 0) goto L_0x0034;
    L_0x0030:
        r0 = r4.x;	 Catch:{ all -> 0x0021 }
        if (r0 == 0) goto L_0x004d;
    L_0x0034:
        r0 = r2;
    L_0x0035:
        r8 = 0;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 <= 0) goto L_0x003d;
    L_0x003b:
        if (r0 == 0) goto L_0x0097;
    L_0x003d:
        r0 = r10.e();	 Catch:{ all -> 0x0021 }
        r1 = h;	 Catch:{ all -> 0x0021 }
        if (r1 != 0) goto L_0x004f;
    L_0x0045:
        if (r0 != 0) goto L_0x004f;
    L_0x0047:
        r0 = new java.lang.AssertionError;	 Catch:{ all -> 0x0021 }
        r0.<init>();	 Catch:{ all -> 0x0021 }
        throw r0;	 Catch:{ all -> 0x0021 }
    L_0x004d:
        r0 = r1;
        goto L_0x0035;
    L_0x004f:
        r1 = r10.b();	 Catch:{ all -> 0x0021 }
        if (r1 != 0) goto L_0x005a;
    L_0x0055:
        r1 = r10.b;	 Catch:{ all -> 0x0021 }
        r1.signalAll();	 Catch:{ all -> 0x0021 }
    L_0x005a:
        r1 = r10.g;	 Catch:{ all -> 0x0021 }
        r6 = -1;
        r1.set(r6);	 Catch:{ all -> 0x0021 }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0021 }
        r1.<init>();	 Catch:{ all -> 0x0021 }
        r2 = "ScheduleQueue|take|";
        r1 = r1.append(r2);	 Catch:{ all -> 0x0021 }
        r2 = r4.getClass();	 Catch:{ all -> 0x0021 }
        r2 = r2.getName();	 Catch:{ all -> 0x0021 }
        r1 = r1.append(r2);	 Catch:{ all -> 0x0021 }
        r2 = "|";
        r1 = r1.append(r2);	 Catch:{ all -> 0x0021 }
        r2 = r4.hashCode();	 Catch:{ all -> 0x0021 }
        r1 = r1.append(r2);	 Catch:{ all -> 0x0021 }
        r2 = "|imediate";
        r1 = r1.append(r2);	 Catch:{ all -> 0x0021 }
        r1 = r1.toString();	 Catch:{ all -> 0x0021 }
        com.igexin.a.a.c.a.a(r1);	 Catch:{ all -> 0x0021 }
        r3.unlock();
        return r0;
    L_0x0097:
        r0 = new java.text.SimpleDateFormat;	 Catch:{ all -> 0x0021 }
        r5 = "yyyy-MM-dd HH:mm:ss";
        r0.<init>(r5);	 Catch:{ all -> 0x0021 }
        r5 = new java.util.Date;	 Catch:{ all -> 0x0021 }
        r8 = r4.F;	 Catch:{ all -> 0x0021 }
        r5.<init>(r8);	 Catch:{ all -> 0x0021 }
        r0 = r0.format(r5);	 Catch:{ all -> 0x0021 }
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0021 }
        r5.<init>();	 Catch:{ all -> 0x0021 }
        r8 = "ScheduleQueue|take|";
        r5 = r5.append(r8);	 Catch:{ all -> 0x0021 }
        r8 = r4.getClass();	 Catch:{ all -> 0x0021 }
        r8 = r8.getName();	 Catch:{ all -> 0x0021 }
        r5 = r5.append(r8);	 Catch:{ all -> 0x0021 }
        r8 = "|";
        r5 = r5.append(r8);	 Catch:{ all -> 0x0021 }
        r8 = r4.hashCode();	 Catch:{ all -> 0x0021 }
        r5 = r5.append(r8);	 Catch:{ all -> 0x0021 }
        r8 = "|";
        r5 = r5.append(r8);	 Catch:{ all -> 0x0021 }
        r0 = r5.append(r0);	 Catch:{ all -> 0x0021 }
        r0 = r0.toString();	 Catch:{ all -> 0x0021 }
        com.igexin.a.a.c.a.a(r0);	 Catch:{ all -> 0x0021 }
        r0 = r10.g;	 Catch:{ all -> 0x0021 }
        r8 = r4.F;	 Catch:{ all -> 0x0021 }
        r0.set(r8);	 Catch:{ all -> 0x0021 }
        r0 = r10.f;	 Catch:{ all -> 0x0021 }
        r0 = r0.y;	 Catch:{ all -> 0x0021 }
        if (r0 == 0) goto L_0x00f3;
    L_0x00ec:
        r0 = r10.f;	 Catch:{ all -> 0x0021 }
        r4 = r4.F;	 Catch:{ all -> 0x0021 }
        r0.a(r4);	 Catch:{ all -> 0x0021 }
    L_0x00f3:
        r0 = r10.b;	 Catch:{ all -> 0x0021 }
        r0.awaitNanos(r6);	 Catch:{ all -> 0x0021 }
        goto L_0x0007;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.a.a.d.c.c():com.igexin.a.a.d.d");
    }

    public final void d() {
        this.c.clear();
    }
}
