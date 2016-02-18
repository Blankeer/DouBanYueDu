package io.fabric.sdk.android.services.concurrency;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import u.aly.dx;

public class DependencyPriorityBlockingQueue<E extends Dependency & Task & PriorityProvider> extends PriorityBlockingQueue<E> {
    static final int PEEK = 1;
    static final int POLL = 2;
    static final int POLL_WITH_TIMEOUT = 3;
    static final int TAKE = 0;
    final Queue<E> blockedQueue;
    private final ReentrantLock lock;

    public DependencyPriorityBlockingQueue() {
        this.blockedQueue = new LinkedList();
        this.lock = new ReentrantLock();
    }

    public E take() throws InterruptedException {
        return get(0, null, null);
    }

    public E peek() {
        E e = null;
        try {
            e = get(PEEK, null, null);
        } catch (InterruptedException e2) {
        }
        return e;
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return get(POLL_WITH_TIMEOUT, Long.valueOf(timeout), unit);
    }

    public E poll() {
        E e = null;
        try {
            e = get(POLL, null, null);
        } catch (InterruptedException e2) {
        }
        return e;
    }

    public int size() {
        try {
            this.lock.lock();
            int size = this.blockedQueue.size() + super.size();
            return size;
        } finally {
            this.lock.unlock();
        }
    }

    public <T> T[] toArray(T[] a) {
        try {
            this.lock.lock();
            T[] concatenate = concatenate(super.toArray(a), this.blockedQueue.toArray(a));
            return concatenate;
        } finally {
            this.lock.unlock();
        }
    }

    public Object[] toArray() {
        try {
            this.lock.lock();
            Object[] concatenate = concatenate(super.toArray(), this.blockedQueue.toArray());
            return concatenate;
        } finally {
            this.lock.unlock();
        }
    }

    public int drainTo(Collection<? super E> c) {
        try {
            this.lock.lock();
            int numberOfItems = super.drainTo(c) + this.blockedQueue.size();
            while (!this.blockedQueue.isEmpty()) {
                c.add(this.blockedQueue.poll());
            }
            return numberOfItems;
        } finally {
            this.lock.unlock();
        }
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        try {
            this.lock.lock();
            int numberOfItems = super.drainTo(c, maxElements);
            while (!this.blockedQueue.isEmpty() && numberOfItems <= maxElements) {
                c.add(this.blockedQueue.poll());
                numberOfItems += PEEK;
            }
            this.lock.unlock();
            return numberOfItems;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public boolean contains(Object o) {
        try {
            this.lock.lock();
            boolean z = super.contains(o) || this.blockedQueue.contains(o);
            this.lock.unlock();
            return z;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public void clear() {
        try {
            this.lock.lock();
            this.blockedQueue.clear();
            super.clear();
        } finally {
            this.lock.unlock();
        }
    }

    public boolean remove(Object o) {
        try {
            this.lock.lock();
            boolean z = super.remove(o) || this.blockedQueue.remove(o);
            this.lock.unlock();
            return z;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public boolean removeAll(Collection<?> collection) {
        try {
            this.lock.lock();
            boolean removeAll = super.removeAll(collection) | this.blockedQueue.removeAll(collection);
            return removeAll;
        } finally {
            this.lock.unlock();
        }
    }

    E performOperation(int operation, Long time, TimeUnit unit) throws InterruptedException {
        switch (operation) {
            case dx.a /*0*/:
                return (Dependency) super.take();
            case PEEK /*1*/:
                return (Dependency) super.peek();
            case POLL /*2*/:
                return (Dependency) super.poll();
            case POLL_WITH_TIMEOUT /*3*/:
                return (Dependency) super.poll(time.longValue(), unit);
            default:
                return null;
        }
    }

    boolean offerBlockedResult(int operation, E result) {
        try {
            this.lock.lock();
            if (operation == PEEK) {
                super.remove(result);
            }
            boolean offer = this.blockedQueue.offer(result);
            return offer;
        } finally {
            this.lock.unlock();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    E get(int r3, java.lang.Long r4, java.util.concurrent.TimeUnit r5) throws java.lang.InterruptedException {
        /*
        r2 = this;
    L_0x0000:
        r0 = r2.performOperation(r3, r4, r5);
        if (r0 == 0) goto L_0x000c;
    L_0x0006:
        r1 = r2.canProcess(r0);
        if (r1 == 0) goto L_0x000d;
    L_0x000c:
        return r0;
    L_0x000d:
        r2.offerBlockedResult(r3, r0);
        goto L_0x0000;
        */
        throw new UnsupportedOperationException("Method not decompiled: io.fabric.sdk.android.services.concurrency.DependencyPriorityBlockingQueue.get(int, java.lang.Long, java.util.concurrent.TimeUnit):E");
    }

    boolean canProcess(E result) {
        return result.areDependenciesMet();
    }

    public void recycleBlockedQueue() {
        try {
            this.lock.lock();
            Iterator<E> iterator = this.blockedQueue.iterator();
            while (iterator.hasNext()) {
                Dependency blockedItem = (Dependency) iterator.next();
                if (canProcess(blockedItem)) {
                    super.offer(blockedItem);
                    iterator.remove();
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    <T> T[] concatenate(T[] arr1, T[] arr2) {
        int arr1Len = arr1.length;
        int arr2Len = arr2.length;
        Object[] C = (Object[]) ((Object[]) Array.newInstance(arr1.getClass().getComponentType(), arr1Len + arr2Len));
        System.arraycopy(arr1, 0, C, 0, arr1Len);
        System.arraycopy(arr2, 0, C, arr1Len, arr2Len);
        return C;
    }
}
