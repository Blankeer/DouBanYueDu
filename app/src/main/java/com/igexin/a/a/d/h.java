package com.igexin.a.a.d;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

final class h implements ThreadFactory {
    final AtomicInteger a;
    final /* synthetic */ f b;

    public h(f fVar) {
        this.b = fVar;
        this.a = new AtomicInteger(0);
    }

    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, "TaskService-pool-" + this.a.incrementAndGet());
    }
}
