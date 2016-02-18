package com.igexin.a.a.d;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.support.v4.widget.ExploreByTouchHelper;
import com.igexin.a.a.c.a;
import com.igexin.a.a.d.a.c;
import com.igexin.a.a.d.a.f;
import com.igexin.push.c.c.n;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class e extends BroadcastReceiver implements Comparator {
    public static final String i;
    public static final long z;
    private boolean a;
    public String h;
    final i j;
    final HashMap k;
    final ConcurrentLinkedQueue l;
    final c m;
    final ReentrantLock n;
    final ReentrantLock o;
    PowerManager p;
    AlarmManager q;
    Intent r;
    PendingIntent s;
    Intent t;
    PendingIntent u;
    Intent v;
    PendingIntent w;
    String x;
    volatile boolean y;

    static {
        i = e.class.getSimpleName();
        z = TimeUnit.SECONDS.toMillis(2);
    }

    protected e() {
        this.h = getClass().getSimpleName();
        this.n = new ReentrantLock();
        this.o = new ReentrantLock();
        this.a = false;
        this.k = new HashMap(7);
        this.m = new c(this, this);
        this.l = new ConcurrentLinkedQueue();
        this.j = new i(this);
        d.P = this;
    }

    public final int a(d dVar, d dVar2) {
        int i = dVar.L > dVar2.L ? -1 : dVar.L < dVar2.L ? 1 : dVar.G < dVar2.G ? -1 : dVar.G > dVar2.G ? 1 : 0;
        if (dVar.F != dVar2.F) {
            i = dVar.F < dVar2.F ? -1 : 1;
        }
        return i == 0 ? dVar.hashCode() - dVar2.hashCode() : i;
    }

    public final void a(long j) {
        if (this.y) {
            if (j < 0) {
                j = System.currentTimeMillis() + z;
            }
            try {
                if (this.s != null && this.u != null) {
                    if (VERSION.SDK_INT < 19) {
                        this.q.set(0, j, this.s);
                        this.q.set(0, z + j, this.u);
                        return;
                    }
                    try {
                        this.q.setExact(0, j, this.s);
                        this.q.setExact(0, z + j, this.u);
                    } catch (Exception e) {
                        this.q.set(0, j, this.s);
                        this.q.set(0, z + j, this.u);
                    }
                }
            } catch (Exception e2) {
            }
        }
    }

    public final void a(Context context) {
        if (!this.a) {
            this.p = (PowerManager) context.getSystemService("power");
            this.y = true;
            this.q = (AlarmManager) context.getSystemService(NotificationCompatApi21.CATEGORY_ALARM);
            context.registerReceiver(this, new IntentFilter("AlarmTaskSchedule." + context.getPackageName()));
            context.registerReceiver(this, new IntentFilter("AlarmTaskScheduleBak." + context.getPackageName()));
            context.registerReceiver(this, new IntentFilter("android.intent.action.SCREEN_OFF"));
            context.registerReceiver(this, new IntentFilter("android.intent.action.SCREEN_ON"));
            this.x = "AlarmNioTaskSchedule." + context.getPackageName();
            context.registerReceiver(this, new IntentFilter(this.x));
            this.r = new Intent("AlarmTaskSchedule." + context.getPackageName());
            this.s = PendingIntent.getBroadcast(context, hashCode(), this.r, 134217728);
            this.t = new Intent("AlarmTaskScheduleBak." + context.getPackageName());
            this.u = PendingIntent.getBroadcast(context, hashCode() + 1, this.t, 134217728);
            this.v = new Intent(this.x);
            this.w = PendingIntent.getBroadcast(context, hashCode() + 2, this.v, 134217728);
            this.j.start();
            Thread.yield();
            this.a = true;
        }
    }

    public final boolean a(c cVar) {
        if (cVar == null) {
            throw new NullPointerException();
        }
        ReentrantLock reentrantLock = this.n;
        if (!reentrantLock.tryLock()) {
            return false;
        }
        try {
            if (this.k.keySet().contains(Long.valueOf(cVar.o()))) {
                return false;
            }
            this.k.put(Long.valueOf(cVar.o()), cVar);
            reentrantLock.unlock();
            return true;
        } finally {
            reentrantLock.unlock();
        }
    }

    final boolean a(f fVar, c cVar) {
        int b = fVar.b();
        if (b <= ExploreByTouchHelper.INVALID_ID || b >= 0) {
            return (b < 0 || b >= AdvancedShareActionProvider.WEIGHT_MAX) ? false : cVar.a(fVar, this);
        } else {
            d dVar = (d) fVar;
            boolean a = dVar.E ? cVar.a(dVar, this) : cVar.a(fVar, this);
            if (a) {
                dVar.c();
            }
            return a;
        }
    }

    public final boolean a(d dVar, boolean z) {
        int i = 0;
        if (dVar == null) {
            throw new NullPointerException();
        } else if (dVar.A || dVar.w) {
            return false;
        } else {
            c cVar = this.m;
            if (z) {
                i = cVar.d.incrementAndGet();
            }
            dVar.L = i;
            return cVar.a(dVar);
        }
    }

    public final boolean a(d dVar, boolean z, boolean z2) {
        boolean z3 = true;
        if (dVar == null) {
            throw new NullPointerException();
        } else if (dVar.x) {
            return false;
        } else {
            if (!z || z2) {
                if (!(z2 && z)) {
                    z3 = false;
                }
                return a(dVar, z3);
            }
            dVar.d();
            try {
                dVar.a_();
                dVar.s();
                dVar.u();
                if (!dVar.E) {
                    dVar.c();
                }
                return true;
            } catch (Exception e) {
                dVar.E = true;
                dVar.M = e;
                dVar.o();
                dVar.v();
                a((Object) dVar);
                g();
                if (dVar.E) {
                    return false;
                }
                dVar.c();
                return false;
            } catch (Throwable th) {
                if (!dVar.E) {
                    dVar.c();
                }
            }
        }
    }

    public final boolean a(Class cls) {
        c cVar = this.m;
        return cVar != null ? cVar.a(cls) : false;
    }

    public final boolean a(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            if (obj instanceof n) {
                a.a("TaskService|responseTask|" + obj.getClass().getName() + "|" + obj.hashCode() + "|" + ((String) ((n) obj).e));
            } else {
                a.a("TaskService|responseTask|" + obj.getClass().getName() + "|" + obj.hashCode());
            }
        } catch (Exception e) {
        }
        if (obj instanceof f) {
            f fVar = (f) obj;
            if (fVar.j()) {
                return false;
            }
            fVar.a(false);
            this.l.offer(fVar);
            return true;
        }
        throw new ClassCastException("response Obj is not a TaskResult ");
    }

    public final void b(long j) {
        a.a("setnioalarm|" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(j)));
        if (j < 0) {
            j = System.currentTimeMillis() + z;
        }
        if (this.w == null) {
            return;
        }
        if (VERSION.SDK_INT < 19) {
            this.q.set(0, j, this.w);
            return;
        }
        try {
            this.q.setExact(0, j, this.w);
        } catch (Exception e) {
            this.q.set(0, j, this.w);
        }
    }

    public /* synthetic */ int compare(Object obj, Object obj2) {
        return a((d) obj, (d) obj2);
    }

    public final void f() {
        if (this.w != null) {
            this.q.cancel(this.w);
        }
    }

    protected final void g() {
        if (this.j != null && !this.j.isInterrupted()) {
            this.j.interrupt();
        }
    }

    final void h() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x00ba in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:58)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JavaClass.getMethods(JavaClass.java:188)
*/
        /*
        r9 = this;
        r8 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
    L_0x0002:
        r0 = r9.l;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x00d3;
    L_0x000a:
        r0 = r9.l;
        r0 = r0.poll();
        r0 = (com.igexin.a.a.d.a.f) r0;
        r1 = 1;
        r0.a(r1);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "TaskService|notifyObserver|";
        r1 = r1.append(r2);
        r2 = r0.getClass();
        r2 = r2.getName();
        r1 = r1.append(r2);
        r2 = "|";
        r1 = r1.append(r2);
        r2 = r0.hashCode();
        r1 = r1.append(r2);
        r1 = r1.toString();
        com.igexin.a.a.c.a.a(r1);
        r2 = 0;
        r3 = r9.n;
        r3.lock();
        r1 = r9.k;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        r1 = r1.isEmpty();	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        if (r1 != 0) goto L_0x0073;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
    L_0x0050:
        r4 = r0.k();	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        r6 = 0;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        if (r1 == 0) goto L_0x0087;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
    L_0x005a:
        r1 = r9.k;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        r4 = java.lang.Long.valueOf(r4);	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        r1 = r1.get(r4);	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        r1 = (com.igexin.a.a.d.a.c) r1;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        if (r1 == 0) goto L_0x00d4;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
    L_0x0068:
        r4 = r1.n();	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        if (r4 == 0) goto L_0x00d4;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
    L_0x006e:
        r1 = r9.a(r0, r1);	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
    L_0x0072:
        r2 = r1;
    L_0x0073:
        if (r2 != 0) goto L_0x0082;
    L_0x0075:
        r1 = r0.b();
        if (r1 <= r8) goto L_0x0082;
    L_0x007b:
        if (r1 >= 0) goto L_0x0082;
    L_0x007d:
        r0 = (com.igexin.a.a.d.d) r0;
        r0.c();
    L_0x0082:
        r3.unlock();
        goto L_0x0002;
    L_0x0087:
        r1 = r9.k;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        r1 = r1.values();	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        r4 = r1.iterator();	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
    L_0x0091:
        r1 = r4.hasNext();	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        if (r1 == 0) goto L_0x0073;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
    L_0x0097:
        r1 = r4.next();	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        r1 = (com.igexin.a.a.d.a.c) r1;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        r5 = r1.n();	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        if (r5 == 0) goto L_0x0091;	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
    L_0x00a3:
        r2 = r9.a(r0, r1);	 Catch:{ Exception -> 0x00aa, all -> 0x00bf }
        if (r2 == 0) goto L_0x0091;
    L_0x00a9:
        goto L_0x0073;
    L_0x00aa:
        r1 = move-exception;
        if (r2 != 0) goto L_0x00ba;
    L_0x00ad:
        r1 = r0.b();
        if (r1 <= r8) goto L_0x00ba;
    L_0x00b3:
        if (r1 >= 0) goto L_0x00ba;
    L_0x00b5:
        r0 = (com.igexin.a.a.d.d) r0;
        r0.c();
    L_0x00ba:
        r3.unlock();
        goto L_0x0002;
    L_0x00bf:
        r1 = move-exception;
        if (r2 != 0) goto L_0x00cf;
    L_0x00c2:
        r2 = r0.b();
        if (r2 <= r8) goto L_0x00cf;
    L_0x00c8:
        if (r2 >= 0) goto L_0x00cf;
    L_0x00ca:
        r0 = (com.igexin.a.a.d.d) r0;
        r0.c();
    L_0x00cf:
        r3.unlock();
        throw r1;
    L_0x00d3:
        return;
    L_0x00d4:
        r1 = r2;
        goto L_0x0072;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.a.a.d.e.h():void");
    }

    public final void onReceive(Context context, Intent intent) {
        if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
            this.y = true;
            a.b("screenoff");
            if (this.m.g.get() > 0) {
                a(this.m.g.get());
            }
        } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
            this.y = false;
            a.b("screenon");
        } else if (intent.getAction().startsWith("AlarmTaskSchedule.") || intent.getAction().startsWith("AlarmTaskScheduleBak.")) {
            a.a("receivealarm|" + this.y);
            g();
        } else if (this.x.equals(intent.getAction())) {
            a.a("receivenioalarm");
            try {
                if (com.igexin.a.a.b.a.a.e.h() != null) {
                    com.igexin.a.a.b.a.a.e.h().i();
                }
            } catch (Exception e) {
            }
        }
    }
}
