package io.fabric.sdk.android.services.common;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.internal.Backoff;
import io.fabric.sdk.android.services.concurrency.internal.RetryPolicy;
import io.fabric.sdk.android.services.concurrency.internal.RetryThreadPoolExecutor;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class ExecutorUtils {
    private static final long DEFAULT_TERMINATION_TIMEOUT = 2;

    /* renamed from: io.fabric.sdk.android.services.common.ExecutorUtils.1 */
    static class AnonymousClass1 implements ThreadFactory {
        final /* synthetic */ AtomicLong val$count;
        final /* synthetic */ String val$threadNameTemplate;

        /* renamed from: io.fabric.sdk.android.services.common.ExecutorUtils.1.1 */
        class AnonymousClass1 extends BackgroundPriorityRunnable {
            final /* synthetic */ Runnable val$runnable;

            AnonymousClass1(Runnable runnable) {
                this.val$runnable = runnable;
            }

            public void onRun() {
                this.val$runnable.run();
            }
        }

        AnonymousClass1(String str, AtomicLong atomicLong) {
            this.val$threadNameTemplate = str;
            this.val$count = atomicLong;
        }

        public Thread newThread(Runnable runnable) {
            Thread thread = Executors.defaultThreadFactory().newThread(new AnonymousClass1(runnable));
            thread.setName(this.val$threadNameTemplate + this.val$count.getAndIncrement());
            return thread;
        }
    }

    /* renamed from: io.fabric.sdk.android.services.common.ExecutorUtils.2 */
    static class AnonymousClass2 extends BackgroundPriorityRunnable {
        final /* synthetic */ ExecutorService val$service;
        final /* synthetic */ String val$serviceName;
        final /* synthetic */ long val$terminationTimeout;
        final /* synthetic */ TimeUnit val$timeUnit;

        AnonymousClass2(String str, ExecutorService executorService, long j, TimeUnit timeUnit) {
            this.val$serviceName = str;
            this.val$service = executorService;
            this.val$terminationTimeout = j;
            this.val$timeUnit = timeUnit;
        }

        public void onRun() {
            try {
                Fabric.getLogger().d(Fabric.TAG, "Executing shutdown hook for " + this.val$serviceName);
                this.val$service.shutdown();
                if (!this.val$service.awaitTermination(this.val$terminationTimeout, this.val$timeUnit)) {
                    Fabric.getLogger().d(Fabric.TAG, this.val$serviceName + " did not shut down in the" + " allocated time. Requesting immediate shutdown.");
                    this.val$service.shutdownNow();
                }
            } catch (InterruptedException e) {
                Fabric.getLogger().d(Fabric.TAG, String.format(Locale.US, "Interrupted while waiting for %s to shut down. Requesting immediate shutdown.", new Object[]{this.val$serviceName}));
                this.val$service.shutdownNow();
            }
        }
    }

    private ExecutorUtils() {
    }

    public static ExecutorService buildSingleThreadExecutorService(String name) {
        ExecutorService executor = Executors.newSingleThreadExecutor(getNamedThreadFactory(name));
        addDelayedShutdownHook(name, executor);
        return executor;
    }

    public static RetryThreadPoolExecutor buildRetryThreadPoolExecutor(String name, int corePoolSize, RetryPolicy retryPolicy, Backoff backoff) {
        RetryThreadPoolExecutor executor = new RetryThreadPoolExecutor(corePoolSize, getNamedThreadFactory(name), retryPolicy, backoff);
        addDelayedShutdownHook(name, executor);
        return executor;
    }

    public static ScheduledExecutorService buildSingleThreadScheduledExecutorService(String name) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(getNamedThreadFactory(name));
        addDelayedShutdownHook(name, executor);
        return executor;
    }

    public static final ThreadFactory getNamedThreadFactory(String threadNameTemplate) {
        return new AnonymousClass1(threadNameTemplate, new AtomicLong(1));
    }

    private static final void addDelayedShutdownHook(String serviceName, ExecutorService service) {
        addDelayedShutdownHook(serviceName, service, DEFAULT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
    }

    public static final void addDelayedShutdownHook(String serviceName, ExecutorService service, long terminationTimeout, TimeUnit timeUnit) {
        Runtime.getRuntime().addShutdownHook(new Thread(new AnonymousClass2(serviceName, service, terminationTimeout, timeUnit), "Crashlytics Shutdown Hook for " + serviceName));
    }
}
