package com.douban.book.reader.executor;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TaggedRunnableScheduledFuture<V> implements RunnableScheduledFuture<V> {
    private RunnableScheduledFuture<V> mFuture;
    private Object mTag;

    public TaggedRunnableScheduledFuture(RunnableScheduledFuture<V> future, Object tag) {
        this.mFuture = future;
        this.mTag = tag;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.mFuture.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return this.mFuture.isCancelled();
    }

    public boolean isDone() {
        return this.mFuture.isDone();
    }

    public V get() throws InterruptedException, ExecutionException {
        return this.mFuture.get();
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.mFuture.get(timeout, unit);
    }

    public boolean isPeriodic() {
        return this.mFuture.isPeriodic();
    }

    public long getDelay(TimeUnit unit) {
        return this.mFuture.getDelay(unit);
    }

    public int compareTo(Delayed another) {
        return this.mFuture.compareTo(another);
    }

    public void run() {
        this.mFuture.run();
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag() {
        return this.mTag;
    }
}
