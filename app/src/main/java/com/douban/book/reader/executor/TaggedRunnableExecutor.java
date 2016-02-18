package com.douban.book.reader.executor;

import com.douban.book.reader.util.Logger;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class TaggedRunnableExecutor extends ScheduledThreadPoolExecutor {
    private String TAG;
    private Map<Object, Future<?>> mFutures;
    private Map<Object, Future<?>> mRunningFutures;
    private StatusCallback mStatusCallback;

    /* renamed from: com.douban.book.reader.executor.TaggedRunnableExecutor.1 */
    static class AnonymousClass1 implements ThreadFactory {
        final /* synthetic */ String val$name;
        final /* synthetic */ int val$priority;

        AnonymousClass1(String str, int i) {
            this.val$name = str;
            this.val$priority = i;
        }

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(this.val$name);
            thread.setPriority(this.val$priority);
            return thread;
        }
    }

    public interface StatusCallback {
        void afterExecute(Object obj, Throwable th);

        void beforeExecute(Object obj);

        void cancelledBeforeExecute(Object obj);
    }

    public interface TagMatcher {
        boolean match(Object obj);
    }

    public TaggedRunnableExecutor(String name, int corePoolSize, int priority) {
        super(corePoolSize, createThreadFactory(name, priority));
        this.mFutures = new ConcurrentHashMap();
        this.mRunningFutures = new ConcurrentHashMap();
        this.mStatusCallback = null;
        this.TAG = String.format("EXECUTOR(%s)", new Object[]{name});
    }

    protected void beforeExecute(Thread thread, Runnable runnable) {
        super.beforeExecute(thread, runnable);
        if (runnable instanceof TaggedRunnableScheduledFuture) {
            Object tag = ((TaggedRunnableScheduledFuture) runnable).getTag();
            this.mRunningFutures.put(tag, (TaggedRunnableScheduledFuture) runnable);
            if (this.mStatusCallback != null) {
                this.mStatusCallback.beforeExecute(tag);
            }
            Logger.d(this.TAG, "%s beforeExecute", tag);
        }
    }

    protected void afterExecute(Runnable runnable, Throwable throwable) {
        super.afterExecute(runnable, throwable);
        if (runnable instanceof TaggedRunnableScheduledFuture) {
            Object tag = ((TaggedRunnableScheduledFuture) runnable).getTag();
            this.mFutures.remove(tag);
            this.mRunningFutures.remove(tag);
            try {
                ((TaggedRunnableScheduledFuture) runnable).get();
            } catch (Throwable t) {
                Logger.e(this.TAG, t, "Error occurred while executing %s", tag);
                throwable = t;
            }
            if (this.mStatusCallback != null) {
                this.mStatusCallback.afterExecute(tag, throwable);
            }
            Logger.d(this.TAG, "%s afterExecute. throwable=%s", tag, throwable);
        }
    }

    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        if (runnable instanceof TaggedRunnable) {
            Object tag = ((TaggedRunnable) runnable).getTag();
            cancelByTag(tag);
            TaggedRunnableScheduledFuture<V> future = new TaggedRunnableScheduledFuture(super.decorateTask(runnable, task), tag);
            this.mFutures.put(tag, future);
            Logger.d(this.TAG, "%s scheduled", tag);
            return future;
        }
        throw new IllegalArgumentException("TaggedRunnableExecutor only accepts TaggedRunnable.");
    }

    public void setStatusCallback(StatusCallback callback) {
        this.mStatusCallback = callback;
    }

    public boolean isScheduled(Object tag) {
        return this.mFutures.containsKey(tag);
    }

    public boolean isScheduled(TagMatcher matcher) {
        if (matcher != null) {
            for (Object key : this.mFutures.keySet()) {
                if (matcher.match(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRunning(Object tag) {
        return this.mRunningFutures.containsKey(tag);
    }

    public boolean isRunning(TagMatcher matcher) {
        if (matcher != null) {
            for (Object key : this.mRunningFutures.keySet()) {
                if (matcher.match(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void cancelByTag(Object tag) {
        Future<?> future = (Future) this.mFutures.get(tag);
        if (future != null) {
            boolean wasRunning = isRunning(tag);
            if (future.cancel(true) && !wasRunning) {
                this.mFutures.remove(tag);
                this.mRunningFutures.remove(tag);
                if (this.mStatusCallback != null) {
                    this.mStatusCallback.cancelledBeforeExecute(tag);
                }
            }
            Logger.d(this.TAG, "%s cancelled", tag);
        }
    }

    public void cancelMatched(TagMatcher matcher) {
        if (matcher != null) {
            for (Entry<Object, Future<?>> entry : this.mFutures.entrySet()) {
                Object key = entry.getKey();
                if (matcher.match(key)) {
                    cancelByTag(key);
                }
            }
        }
    }

    public void cancelAll() {
        for (Entry<Object, Future<?>> entry : this.mFutures.entrySet()) {
            Future<?> future = (Future) entry.getValue();
            if (future != null) {
                future.cancel(true);
            }
            Logger.d(this.TAG, "%s cancelled (by cancelAll)", entry.getKey());
        }
    }

    private static ThreadFactory createThreadFactory(String name, int priority) {
        return new AnonymousClass1(name, priority);
    }
}
