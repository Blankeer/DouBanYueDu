package de.greenrobot.event;

final class BackgroundPoster implements Runnable {
    private final EventBus eventBus;
    private volatile boolean executorRunning;
    private final PendingPostQueue queue;

    BackgroundPoster(EventBus eventBus) {
        this.eventBus = eventBus;
        this.queue = new PendingPostQueue();
    }

    public void enqueue(Subscription subscription, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        synchronized (this) {
            this.queue.enqueue(pendingPost);
            if (!this.executorRunning) {
                this.executorRunning = true;
                this.eventBus.getExecutorService().execute(this);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
        r6 = this;
        r5 = 0;
    L_0x0001:
        r2 = r6.queue;	 Catch:{ InterruptedException -> 0x0022 }
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r1 = r2.poll(r3);	 Catch:{ InterruptedException -> 0x0022 }
        if (r1 != 0) goto L_0x001c;
    L_0x000b:
        monitor-enter(r6);	 Catch:{ InterruptedException -> 0x0022 }
        r2 = r6.queue;	 Catch:{ all -> 0x0046 }
        r1 = r2.poll();	 Catch:{ all -> 0x0046 }
        if (r1 != 0) goto L_0x001b;
    L_0x0014:
        r2 = 0;
        r6.executorRunning = r2;	 Catch:{ all -> 0x0046 }
        monitor-exit(r6);	 Catch:{ all -> 0x0046 }
        r6.executorRunning = r5;
    L_0x001a:
        return;
    L_0x001b:
        monitor-exit(r6);	 Catch:{ all -> 0x0046 }
    L_0x001c:
        r2 = r6.eventBus;	 Catch:{ InterruptedException -> 0x0022 }
        r2.invokeSubscriber(r1);	 Catch:{ InterruptedException -> 0x0022 }
        goto L_0x0001;
    L_0x0022:
        r0 = move-exception;
        r2 = "Event";
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0049 }
        r3.<init>();	 Catch:{ all -> 0x0049 }
        r4 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0049 }
        r4 = r4.getName();	 Catch:{ all -> 0x0049 }
        r3 = r3.append(r4);	 Catch:{ all -> 0x0049 }
        r4 = " was interruppted";
        r3 = r3.append(r4);	 Catch:{ all -> 0x0049 }
        r3 = r3.toString();	 Catch:{ all -> 0x0049 }
        android.util.Log.w(r2, r3, r0);	 Catch:{ all -> 0x0049 }
        r6.executorRunning = r5;
        goto L_0x001a;
    L_0x0046:
        r2 = move-exception;
        monitor-exit(r6);	 Catch:{ all -> 0x0046 }
        throw r2;	 Catch:{ InterruptedException -> 0x0022 }
    L_0x0049:
        r2 = move-exception;
        r6.executorRunning = r5;
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.BackgroundPoster.run():void");
    }
}
