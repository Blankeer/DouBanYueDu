package org.mapdb;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import org.mapdb.Fun.Tuple2;

public class StoreHeap extends Store implements Serializable {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected static final Object NULL;
    protected static final Tuple2 TOMBSTONE;
    private static final long serialVersionUID = 150060834534309445L;
    protected final Queue<Long> freeRecids;
    protected final AtomicLong maxRecid;
    protected final ConcurrentNavigableMap<Long, Tuple2> records;
    protected final ConcurrentNavigableMap<Long, Tuple2> rollback;

    static {
        $assertionsDisabled = !StoreHeap.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        TOMBSTONE = Fun.t2(null, null);
        NULL = new Object();
    }

    public StoreHeap() {
        super($assertionsDisabled, $assertionsDisabled, null, $assertionsDisabled);
        this.records = new ConcurrentSkipListMap();
        this.rollback = new ConcurrentSkipListMap();
        this.freeRecids = new ConcurrentLinkedQueue();
        this.maxRecid = new AtomicLong(7);
        for (long recid = 1; recid <= 7; recid++) {
            this.records.put(Long.valueOf(recid), Fun.t2(null, (Serializer) null));
        }
    }

    public long preallocate() {
        Lock lock = this.locks[new Random().nextInt(this.locks.length)].writeLock();
        lock.lock();
        try {
            Long recid = (Long) this.freeRecids.poll();
            if (recid == null) {
                recid = Long.valueOf(this.maxRecid.incrementAndGet());
            }
            long longValue = recid.longValue();
            return longValue;
        } finally {
            lock.unlock();
        }
    }

    public void preallocate(long[] recids) {
        Lock lock = this.locks[new Random().nextInt(this.locks.length)].writeLock();
        lock.lock();
        int i = 0;
        while (i < recids.length) {
            try {
                Long recid = (Long) this.freeRecids.poll();
                if (recid == null) {
                    recid = Long.valueOf(this.maxRecid.incrementAndGet());
                }
                recids[i] = recid.longValue();
                i++;
            } finally {
                lock.unlock();
            }
        }
    }

    public <A> long put(A value, Serializer<A> serializer) {
        if (value == null) {
            value = NULL;
        }
        Lock lock = this.locks[new Random().nextInt(this.locks.length)].writeLock();
        lock.lock();
        try {
            Long recid = (Long) this.freeRecids.poll();
            if (recid == null) {
                recid = Long.valueOf(this.maxRecid.incrementAndGet());
            }
            this.records.put(recid, Fun.t2(value, serializer));
            this.rollback.put(recid, Fun.t2(TOMBSTONE, serializer));
            if ($assertionsDisabled || recid.longValue() > 0) {
                long longValue = recid.longValue();
                return longValue;
            }
            throw new AssertionError();
        } finally {
            lock.unlock();
        }
    }

    public <A> A get(long recid, Serializer<A> serializer) {
        if ($assertionsDisabled || recid > 0) {
            Lock lock = this.locks[Store.lockPos(recid)].readLock();
            lock.lock();
            try {
                Tuple2 t = (Tuple2) this.records.get(Long.valueOf(recid));
                if (t == null || t.a == NULL) {
                    lock.unlock();
                    return null;
                }
                A a = t.a;
                lock.unlock();
                return a;
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public <A> void update(long recid, A value, Serializer<A> serializer) {
        if (!$assertionsDisabled && recid <= 0) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && serializer == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || recid > 0) {
            if (value == null) {
                value = NULL;
            }
            Lock lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            try {
                Tuple2 old = (Tuple2) this.records.put(Long.valueOf(recid), Fun.t2(value, serializer));
                if (old != null) {
                    this.rollback.putIfAbsent(Long.valueOf(recid), old);
                }
                lock.unlock();
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
        if ($assertionsDisabled || recid > 0) {
            if (expectedOldValue == null) {
                expectedOldValue = NULL;
            }
            if (newValue == null) {
                newValue = NULL;
            }
            Lock lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            try {
                Tuple2 old = Fun.t2(expectedOldValue, serializer);
                boolean ret = this.records.replace(Long.valueOf(recid), old, Fun.t2(newValue, serializer));
                if (ret) {
                    this.rollback.putIfAbsent(Long.valueOf(recid), old);
                }
                lock.unlock();
                return ret;
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public <A> void delete(long recid, Serializer<A> serializer) {
        if ($assertionsDisabled || recid > 0) {
            Lock lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            try {
                Tuple2 t2 = (Tuple2) this.records.remove(Long.valueOf(recid));
                if (t2 != null) {
                    this.rollback.putIfAbsent(Long.valueOf(recid), t2);
                }
                this.freeRecids.add(Long.valueOf(recid));
            } finally {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public void close() {
        for (Runnable closeListener : this.closeListeners) {
            closeListener.run();
        }
        lockAllWrite();
        try {
            this.records.clear();
            this.freeRecids.clear();
            this.rollback.clear();
        } finally {
            unlockAllWrite();
        }
    }

    public boolean isClosed() {
        return $assertionsDisabled;
    }

    public void commit() {
        lockAllWrite();
        try {
            this.rollback.clear();
        } finally {
            unlockAllWrite();
        }
    }

    public void rollback() throws UnsupportedOperationException {
        lockAllWrite();
        try {
            for (Entry<Long, Tuple2> e : this.rollback.entrySet()) {
                Long recid = (Long) e.getKey();
                Tuple2 val = (Tuple2) e.getValue();
                if (val == TOMBSTONE) {
                    this.records.remove(recid);
                } else {
                    this.records.put(recid, val);
                }
            }
            this.rollback.clear();
        } finally {
            unlockAllWrite();
        }
    }

    public boolean isReadOnly() {
        return $assertionsDisabled;
    }

    public void clearCache() {
    }

    public void compact() {
    }

    public boolean canRollback() {
        return true;
    }

    public long getMaxRecid() {
        return this.maxRecid.get();
    }

    public ByteBuffer getRaw(long recid) {
        Tuple2 t = (Tuple2) this.records.get(Long.valueOf(recid));
        if (t == null || t.a == null) {
            return null;
        }
        return ByteBuffer.wrap(serialize(t.a, (Serializer) t.b).copyBytes());
    }

    public Iterator<Long> getFreeRecids() {
        return Collections.unmodifiableCollection(this.freeRecids).iterator();
    }

    public void updateRaw(long recid, ByteBuffer data) {
        throw new UnsupportedOperationException("can not put raw data into StoreHeap");
    }

    public long getSizeLimit() {
        return 0;
    }

    public long getCurrSize() {
        return (long) this.records.size();
    }

    public long getFreeSize() {
        return 0;
    }

    public String calculateStatistics() {
        return null;
    }
}
