package com.alipay.android.phone.mrpc.core;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

public final class s implements f {
    private static s g;
    private static final ThreadFactory i;
    Context a;
    i b;
    long c;
    long d;
    long e;
    int f;
    private ThreadPoolExecutor h;

    static {
        g = null;
        i = new u();
    }

    private s(Context context) {
        this.a = context;
        this.b = i.a(AbstractSpiCall.ANDROID_CLIENT_TYPE);
        this.h = new ThreadPoolExecutor(10, 11, 3, TimeUnit.SECONDS, new ArrayBlockingQueue(20), i, new CallerRunsPolicy());
        try {
            this.h.allowCoreThreadTimeOut(true);
        } catch (Exception e) {
        }
        CookieSyncManager.createInstance(this.a);
        CookieManager.getInstance().setAcceptCookie(true);
    }

    private i a() {
        return this.b;
    }

    public static final s a(Context context) {
        return g != null ? g : b(context);
    }

    private void a(long j) {
        this.c += j;
    }

    private static final synchronized s b(Context context) {
        s sVar;
        synchronized (s.class) {
            if (g != null) {
                sVar = g;
            } else {
                sVar = new s(context);
                g = sVar;
            }
        }
        return sVar;
    }

    private void b(long j) {
        this.d += j;
        this.f++;
    }

    private void c(long j) {
        this.e += j;
    }

    public final Future<ab> a(aa aaVar) {
        long j = 0;
        if (z.a(this.a)) {
            String str = "HttpManager" + hashCode() + ": Active Task = %d, Completed Task = %d, All Task = %d,Avarage Speed = %d KB/S, Connetct Time = %d ms, All data size = %d bytes, All enqueueConnect time = %d ms, All socket time = %d ms, All request times = %d times";
            Object[] objArr = new Object[9];
            objArr[0] = Integer.valueOf(this.h.getActiveCount());
            objArr[1] = Long.valueOf(this.h.getCompletedTaskCount());
            objArr[2] = Long.valueOf(this.h.getTaskCount());
            objArr[3] = Long.valueOf(this.e == 0 ? 0 : ((this.c * 1000) / this.e) >> 10);
            if (this.f != 0) {
                j = this.d / ((long) this.f);
            }
            objArr[4] = Long.valueOf(j);
            objArr[5] = Long.valueOf(this.c);
            objArr[6] = Long.valueOf(this.d);
            objArr[7] = Long.valueOf(this.e);
            objArr[8] = Integer.valueOf(this.f);
            String.format(str, objArr);
        }
        Object xVar = new x(this, (v) aaVar);
        Object tVar = new t(this, xVar, xVar);
        this.h.execute(tVar);
        return tVar;
    }
}
