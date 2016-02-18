package com.crashlytics.android.core;

import android.os.Looper;
import io.fabric.sdk.android.Fabric;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

class CrashlyticsExecutorServiceWrapper {
    private final ExecutorService executorService;

    /* renamed from: com.crashlytics.android.core.CrashlyticsExecutorServiceWrapper.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Runnable val$runnable;

        AnonymousClass1(Runnable runnable) {
            this.val$runnable = runnable;
        }

        public void run() {
            try {
                this.val$runnable.run();
            } catch (Exception e) {
                Fabric.getLogger().e(CrashlyticsCore.TAG, "Failed to execute task.", e);
            }
        }
    }

    /* renamed from: com.crashlytics.android.core.CrashlyticsExecutorServiceWrapper.2 */
    class AnonymousClass2 implements Callable<T> {
        final /* synthetic */ Callable val$callable;

        AnonymousClass2(Callable callable) {
            this.val$callable = callable;
        }

        public T call() throws Exception {
            try {
                return this.val$callable.call();
            } catch (Exception e) {
                Fabric.getLogger().e(CrashlyticsCore.TAG, "Failed to execute task.", e);
                return null;
            }
        }
    }

    public CrashlyticsExecutorServiceWrapper(ExecutorService executorService) {
        this.executorService = executorService;
    }

    <T> T executeSyncLoggingException(Callable<T> callable) {
        try {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                return this.executorService.submit(callable).get(4, TimeUnit.SECONDS);
            }
            return this.executorService.submit(callable).get();
        } catch (RejectedExecutionException e) {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Executor is shut down because we're handling a fatal crash.");
            return null;
        } catch (Exception e2) {
            Fabric.getLogger().e(CrashlyticsCore.TAG, "Failed to execute task.", e2);
            return null;
        }
    }

    Future<?> executeAsync(Runnable runnable) {
        try {
            return this.executorService.submit(new AnonymousClass1(runnable));
        } catch (RejectedExecutionException e) {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Executor is shut down because we're handling a fatal crash.");
            return null;
        }
    }

    <T> Future<T> executeAsync(Callable<T> callable) {
        try {
            return this.executorService.submit(new AnonymousClass2(callable));
        } catch (RejectedExecutionException e) {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Executor is shut down because we're handling a fatal crash.");
            return null;
        }
    }
}
