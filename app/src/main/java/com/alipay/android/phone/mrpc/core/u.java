package com.alipay.android.phone.mrpc.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

final class u implements ThreadFactory {
    private final AtomicInteger a;

    u() {
        this.a = new AtomicInteger(1);
    }

    public final Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "com.alipay.mobile.common.transport.http.HttpManager.HttpWorker #" + this.a.getAndIncrement());
        thread.setPriority(4);
        return thread;
    }
}
