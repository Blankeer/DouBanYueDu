package org.mapdb;

import android.support.v4.media.TransportMediator;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Caches {

    public static class HashTable extends EngineWrapper implements Engine {
        protected final int cacheMaxSize;
        protected final int cacheMaxSizeMask;
        protected final long hashSalt;
        protected HashItem[] items;
        protected final ReentrantLock[] locks;

        private static class HashItem {
            final long key;
            final Object val;

            private HashItem(long key, Object val) {
                this.key = key;
                this.val = val;
            }
        }

        public HashTable(Engine engine, int cacheMaxSize, boolean disableLocks) {
            super(engine);
            this.locks = new ReentrantLock[TransportMediator.FLAG_KEY_MEDIA_NEXT];
            for (int i = 0; i < this.locks.length; i++) {
                this.locks[i] = new ReentrantLock(false);
            }
            this.hashSalt = new Random().nextLong();
            this.items = new HashItem[cacheMaxSize];
            this.cacheMaxSize = 1 << (32 - Integer.numberOfLeadingZeros(cacheMaxSize - 1));
            this.cacheMaxSizeMask = cacheMaxSize - 1;
        }

        public <A> long put(A value, Serializer<A> serializer) {
            long recid = getWrappedEngine().put(value, serializer);
            HashItem[] items2 = (HashItem[]) EngineWrapper.checkClosed(this.items);
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                items2[position(recid)] = new HashItem(value, null);
                return recid;
            } finally {
                lock.unlock();
            }
        }

        public <A> A get(long recid, Serializer<A> serializer) {
            int pos = position(recid);
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                HashItem[] items2 = (HashItem[]) EngineWrapper.checkClosed(this.items);
                HashItem item = items2[pos];
                A value;
                if (item == null || recid != item.key) {
                    value = getWrappedEngine().get(recid, serializer);
                    if (value != null) {
                        items2[pos] = new HashItem(value, null);
                    }
                    lock.unlock();
                    return value;
                }
                value = item.val;
                return value;
            } finally {
                lock.unlock();
            }
        }

        private int position(long recid) {
            return LongHashMap.longHash(this.hashSalt ^ recid) & this.cacheMaxSizeMask;
        }

        public <A> void update(long recid, A value, Serializer<A> serializer) {
            int pos = position(recid);
            HashItem[] items2 = (HashItem[]) EngineWrapper.checkClosed(this.items);
            HashItem item = new HashItem(value, null);
            Engine engine = getWrappedEngine();
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                items2[pos] = item;
                engine.update(recid, value, serializer);
            } finally {
                lock.unlock();
            }
        }

        public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
            int pos = position(recid);
            HashItem[] items2 = (HashItem[]) EngineWrapper.checkClosed(this.items);
            Engine engine = getWrappedEngine();
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                HashItem item = items2[pos];
                if (item == null || item.key != recid) {
                    boolean ret = engine.compareAndSwap(recid, expectedOldValue, newValue, serializer);
                    if (ret) {
                        items2[pos] = new HashItem(newValue, null);
                    }
                    lock.unlock();
                    return ret;
                } else if (item.val == expectedOldValue || item.val.equals(expectedOldValue)) {
                    items2[pos] = new HashItem(newValue, null);
                    engine.update(recid, newValue, serializer);
                    return true;
                } else {
                    lock.unlock();
                    return false;
                }
            } finally {
                lock.unlock();
            }
        }

        public <A> void delete(long recid, Serializer<A> serializer) {
            int pos = position(recid);
            HashItem[] items2 = (HashItem[]) EngineWrapper.checkClosed(this.items);
            Engine engine = getWrappedEngine();
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                HashItem item = items2[pos];
                if (item != null && recid == item.key) {
                    this.items[pos] = null;
                }
                engine.delete(recid, serializer);
            } finally {
                lock.unlock();
            }
        }

        public void close() {
            super.close();
            this.items = null;
        }

        public void rollback() {
            for (int i = 0; i < this.items.length; i++) {
                this.items[i] = null;
            }
            super.rollback();
        }

        public void clearCache() {
            Arrays.fill(this.items, null);
            super.clearCache();
        }
    }

    public static class LRU extends EngineWrapper {
        protected LongMap<Object> cache;
        protected final ReentrantLock[] locks;

        public LRU(Engine engine, int cacheSize, boolean disableLocks) {
            this(engine, new LongConcurrentLRUMap(cacheSize, (int) (((double) cacheSize) * 0.8d)), disableLocks);
        }

        public LRU(Engine engine, LongMap<Object> cache, boolean disableLocks) {
            super(engine);
            this.locks = new ReentrantLock[TransportMediator.FLAG_KEY_MEDIA_NEXT];
            for (int i = 0; i < this.locks.length; i++) {
                this.locks[i] = new ReentrantLock(false);
            }
            this.cache = cache;
        }

        public <A> long put(A value, Serializer<A> serializer) {
            long recid = super.put(value, serializer);
            LongMap<Object> cache2 = (LongMap) EngineWrapper.checkClosed(this.cache);
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                cache2.put(recid, value);
                return recid;
            } finally {
                lock.unlock();
            }
        }

        public <A> A get(long recid, Serializer<A> serializer) {
            LongMap<Object> cache2 = (LongMap) EngineWrapper.checkClosed(this.cache);
            Object ret = cache2.get(recid);
            if (ret != null) {
                return ret;
            }
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                ret = super.get(recid, serializer);
                if (ret != null) {
                    cache2.put(recid, ret);
                }
                lock.unlock();
                return ret;
            } catch (Throwable th) {
                lock.unlock();
            }
        }

        public <A> void update(long recid, A value, Serializer<A> serializer) {
            LongMap<Object> cache2 = (LongMap) EngineWrapper.checkClosed(this.cache);
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                cache2.put(recid, value);
                super.update(recid, value, serializer);
            } finally {
                lock.unlock();
            }
        }

        public <A> void delete(long recid, Serializer<A> serializer) {
            LongMap<Object> cache2 = (LongMap) EngineWrapper.checkClosed(this.cache);
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                cache2.remove(recid);
                super.delete(recid, serializer);
            } finally {
                lock.unlock();
            }
        }

        public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
            Engine engine = getWrappedEngine();
            LongMap cache2 = (LongMap) EngineWrapper.checkClosed(this.cache);
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                A oldValue = cache2.get(recid);
                if (oldValue == expectedOldValue || (oldValue != null && oldValue.equals(expectedOldValue))) {
                    cache2.put(recid, newValue);
                    engine.update(recid, newValue, serializer);
                    return true;
                }
                boolean ret = engine.compareAndSwap(recid, expectedOldValue, newValue, serializer);
                if (ret) {
                    cache2.put(recid, newValue);
                }
                lock.unlock();
                return ret;
            } finally {
                lock.unlock();
            }
        }

        public void close() {
            this.cache = null;
            super.close();
        }

        public void rollback() {
            ((LongMap) EngineWrapper.checkClosed(this.cache)).clear();
            super.rollback();
        }

        public void clearCache() {
            this.cache.clear();
            super.clearCache();
        }
    }

    public static class WeakSoftRef extends EngineWrapper implements Engine {
        protected LongConcurrentHashMap<CacheItem> items;
        protected final ReentrantLock[] locks;
        protected ReferenceQueue<Object> queue;
        protected Thread queueThread;
        protected final boolean useWeakRef;

        /* renamed from: org.mapdb.Caches.WeakSoftRef.1 */
        class AnonymousClass1 extends Thread {
            AnonymousClass1(String x0) {
                super(x0);
            }

            public void run() {
                WeakSoftRef.this.runRefQueue();
            }
        }

        protected interface CacheItem {
            Object get();

            long getRecid();
        }

        protected static final class CacheSoftItem<A> extends SoftReference<A> implements CacheItem {
            final long recid;

            public CacheSoftItem(A referent, ReferenceQueue<A> q, long recid) {
                super(referent, q);
                this.recid = recid;
            }

            public long getRecid() {
                return this.recid;
            }
        }

        protected static final class CacheWeakItem<A> extends WeakReference<A> implements CacheItem {
            final long recid;

            public CacheWeakItem(A referent, ReferenceQueue<A> q, long recid) {
                super(referent, q);
                this.recid = recid;
            }

            public long getRecid() {
                return this.recid;
            }
        }

        public WeakSoftRef(Engine engine, boolean useWeakRef, boolean disableLocks) {
            super(engine);
            this.locks = new ReentrantLock[TransportMediator.FLAG_KEY_MEDIA_NEXT];
            for (int i = 0; i < this.locks.length; i++) {
                this.locks[i] = new ReentrantLock(false);
            }
            this.queue = new ReferenceQueue();
            this.queueThread = new AnonymousClass1("MapDB GC collector");
            this.items = new LongConcurrentHashMap();
            this.useWeakRef = useWeakRef;
            this.queueThread.setDaemon(true);
            this.queueThread.start();
        }

        protected void runRefQueue() {
            try {
                ReferenceQueue<?> queue = this.queue;
                if (queue != null) {
                    LongConcurrentHashMap<CacheItem> items = this.items;
                    do {
                        CacheItem item = (CacheItem) queue.remove();
                        items.remove(item.getRecid(), item);
                    } while (!Thread.interrupted());
                }
            } catch (InterruptedException e) {
            }
        }

        public <A> long put(A value, Serializer<A> serializer) {
            long recid = getWrappedEngine().put(value, serializer);
            ReferenceQueue<A> q = (ReferenceQueue) EngineWrapper.checkClosed(this.queue);
            LongConcurrentHashMap<CacheItem> items2 = (LongConcurrentHashMap) EngineWrapper.checkClosed(this.items);
            CacheItem item = this.useWeakRef ? new CacheWeakItem(value, q, recid) : new CacheSoftItem(value, q, recid);
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                items2.put(recid, item);
                return recid;
            } finally {
                lock.unlock();
            }
        }

        public <A> A get(long recid, Serializer<A> serializer) {
            LongConcurrentHashMap<CacheItem> items2 = (LongConcurrentHashMap) EngineWrapper.checkClosed(this.items);
            CacheItem item = (CacheItem) items2.get(recid);
            if (item != null) {
                A a = item.get();
                if (a != null) {
                    return a;
                }
                items2.remove(recid);
            }
            Engine engine = getWrappedEngine();
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                A value = engine.get(recid, serializer);
                if (value != null) {
                    ReferenceQueue<A> q = (ReferenceQueue) EngineWrapper.checkClosed(this.queue);
                    items2.put(recid, this.useWeakRef ? new CacheWeakItem(value, q, recid) : new CacheSoftItem(value, q, recid));
                }
                lock.unlock();
                return value;
            } catch (Throwable th) {
                lock.unlock();
            }
        }

        public <A> void update(long recid, A value, Serializer<A> serializer) {
            Engine engine = getWrappedEngine();
            ReferenceQueue<A> q = (ReferenceQueue) EngineWrapper.checkClosed(this.queue);
            LongConcurrentHashMap<CacheItem> items2 = (LongConcurrentHashMap) EngineWrapper.checkClosed(this.items);
            CacheItem item = this.useWeakRef ? new CacheWeakItem(value, q, recid) : new CacheSoftItem(value, q, recid);
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                items2.put(recid, item);
                engine.update(recid, value, serializer);
            } finally {
                lock.unlock();
            }
        }

        public <A> void delete(long recid, Serializer<A> serializer) {
            Engine engine = getWrappedEngine();
            LongMap items2 = (LongMap) EngineWrapper.checkClosed(this.items);
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                items2.remove(recid);
                engine.delete(recid, serializer);
            } finally {
                lock.unlock();
            }
        }

        public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
            Engine engine = getWrappedEngine();
            LongMap<CacheItem> items2 = (LongMap) EngineWrapper.checkClosed(this.items);
            ReferenceQueue<A> q = (ReferenceQueue) EngineWrapper.checkClosed(this.queue);
            Lock lock = this.locks[Store.lockPos(recid)];
            lock.lock();
            try {
                CacheItem item = (CacheItem) items2.get(recid);
                A oldValue = item == null ? null : item.get();
                boolean ret;
                if (item == null || item.getRecid() != recid || (oldValue != expectedOldValue && (oldValue == null || !oldValue.equals(expectedOldValue)))) {
                    ret = engine.compareAndSwap(recid, expectedOldValue, newValue, serializer);
                    if (ret) {
                        items2.put(recid, this.useWeakRef ? new CacheWeakItem(newValue, q, recid) : new CacheSoftItem(newValue, q, recid));
                    }
                    lock.unlock();
                    return ret;
                }
                Object cacheWeakItem;
                if (this.useWeakRef) {
                    cacheWeakItem = new CacheWeakItem(newValue, q, recid);
                } else {
                    cacheWeakItem = new CacheSoftItem(newValue, q, recid);
                }
                items2.put(recid, cacheWeakItem);
                engine.update(recid, newValue, serializer);
                ret = true;
                return ret;
            } finally {
                lock.unlock();
            }
        }

        public void close() {
            super.close();
            this.items = null;
            this.queue = null;
            if (this.queueThread != null) {
                this.queueThread.interrupt();
                this.queueThread = null;
            }
        }

        public void rollback() {
            this.items.clear();
            super.rollback();
        }

        public void clearCache() {
            this.items.clear();
            super.clearCache();
        }
    }

    public static class HardRef extends LRU {
        static final int CHECK_EVERY_N = 10000;
        int counter;

        public HardRef(Engine engine, int initialCapacity, boolean disableLocks) {
            super(engine, new LongConcurrentHashMap(initialCapacity), disableLocks);
            this.counter = 0;
        }

        public <A> A get(long recid, Serializer<A> serializer) {
            checkFreeMem();
            return super.get(recid, serializer);
        }

        private void checkFreeMem() {
            int i = this.counter;
            this.counter = i + 1;
            if (i % CHECK_EVERY_N == 0) {
                Runtime r = Runtime.getRuntime();
                long max = r.maxMemory();
                if (max != Long.MAX_VALUE) {
                    double free = ((double) r.freeMemory()) + (((double) max) - ((double) r.totalMemory()));
                    if (free < 1.0E7d || 4.0d * free < ((double) max)) {
                        ((LongMap) EngineWrapper.checkClosed(this.cache)).clear();
                    }
                }
            }
        }

        public <A> void update(long recid, A value, Serializer<A> serializer) {
            checkFreeMem();
            super.update(recid, value, serializer);
        }

        public <A> void delete(long recid, Serializer<A> serializer) {
            checkFreeMem();
            super.delete(recid, serializer);
        }

        public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
            checkFreeMem();
            return super.compareAndSwap(recid, expectedOldValue, newValue, serializer);
        }
    }

    private Caches() {
    }
}
