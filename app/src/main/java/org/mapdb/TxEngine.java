package org.mapdb;

import android.support.v4.media.TransportMediator;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.mapdb.Fun.Tuple2;
import org.mapdb.LongMap.LongMapIterator;

public class TxEngine extends EngineWrapper {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected static final Object TOMBSTONE;
    protected final int PREALLOC_RECID_SIZE;
    protected final ReentrantReadWriteLock commitLock;
    protected final boolean fullTx;
    protected final ReentrantReadWriteLock[] locks;
    protected final Queue<Long> preallocRecids;
    protected ReferenceQueue<Tx> txQueue;
    protected Set<Reference<Tx>> txs;
    protected volatile boolean uncommitedData;

    public class Tx implements Engine {
        static final /* synthetic */ boolean $assertionsDisabled;
        protected boolean closed;
        protected LongConcurrentHashMap<Tuple2> mod;
        protected LongConcurrentHashMap old;
        private Store parentEngine;
        SerializerPojo pojo;
        protected final Reference<Tx> ref;
        final /* synthetic */ TxEngine this$0;
        protected Collection<Long> usedPreallocatedRecids;

        static {
            $assertionsDisabled = !TxEngine.class.desiredAssertionStatus();
        }

        public Tx(TxEngine txEngine) {
            Collection collection = null;
            this.this$0 = txEngine;
            this.old = new LongConcurrentHashMap();
            this.mod = this.this$0.fullTx ? new LongConcurrentHashMap() : null;
            if (this.this$0.fullTx) {
                collection = new ArrayList();
            }
            this.usedPreallocatedRecids = collection;
            this.ref = new WeakReference(this, this.this$0.txQueue);
            this.closed = false;
            this.pojo = new SerializerPojo((CopyOnWriteArrayList) this.this$0.getSerializerPojo().registered.clone());
            if ($assertionsDisabled || txEngine.commitLock.isWriteLockedByCurrentThread()) {
                txEngine.txs.add(this.ref);
                return;
            }
            throw new AssertionError();
        }

        public long preallocate() {
            long longValue;
            if (this.this$0.fullTx) {
                this.this$0.commitLock.writeLock().lock();
                try {
                    Long recid = this.this$0.preallocRecidTake();
                    this.usedPreallocatedRecids.add(recid);
                    longValue = recid.longValue();
                    return longValue;
                } finally {
                    longValue = this.this$0.commitLock.writeLock();
                    longValue.unlock();
                }
            } else {
                throw new UnsupportedOperationException("read-only");
            }
        }

        public void preallocate(long[] recids) {
            if (this.this$0.fullTx) {
                this.this$0.commitLock.writeLock().lock();
                int i = 0;
                while (i < recids.length) {
                    try {
                        Long recid = this.this$0.preallocRecidTake();
                        this.usedPreallocatedRecids.add(recid);
                        recids[i] = recid.longValue();
                        i++;
                    } finally {
                        this.this$0.commitLock.writeLock().unlock();
                    }
                }
                return;
            }
            throw new UnsupportedOperationException("read-only");
        }

        public <A> long put(A value, Serializer<A> serializer) {
            if (this.this$0.fullTx) {
                this.this$0.commitLock.writeLock().lock();
                long longValue;
                try {
                    Long recid = this.this$0.preallocRecidTake();
                    this.usedPreallocatedRecids.add(recid);
                    this.mod.put(recid.longValue(), Fun.t2(value, serializer));
                    longValue = recid.longValue();
                    return longValue;
                } finally {
                    longValue = this.this$0.commitLock.writeLock();
                    longValue.unlock();
                }
            } else {
                throw new UnsupportedOperationException("read-only");
            }
        }

        public <A> A get(long recid, Serializer<A> serializer) {
            this.this$0.commitLock.readLock().lock();
            Lock lock;
            try {
                if (this.closed) {
                    throw new IllegalAccessError("closed");
                }
                lock = this.this$0.locks[Store.lockPos(recid)].readLock();
                lock.lock();
                A noLock = getNoLock(recid, serializer);
                lock.unlock();
                this.this$0.commitLock.readLock().unlock();
                return noLock;
            } catch (Throwable th) {
                this.this$0.commitLock.readLock().unlock();
            }
        }

        private <A> A getNoLock(long recid, Serializer<A> serializer) {
            if (this.this$0.fullTx) {
                Tuple2 tu = (Tuple2) this.mod.get(recid);
                if (tu != null) {
                    if (tu.a == TxEngine.TOMBSTONE) {
                        return null;
                    }
                    return tu.a;
                }
            }
            A oldVal = this.old.get(recid);
            if (oldVal == null) {
                return this.this$0.get(recid, serializer);
            }
            if (oldVal == TxEngine.TOMBSTONE) {
                return null;
            }
            return oldVal;
        }

        public <A> void update(long recid, A value, Serializer<A> serializer) {
            if (this.this$0.fullTx) {
                this.this$0.commitLock.readLock().lock();
                try {
                    this.mod.put(recid, Fun.t2(value, serializer));
                } finally {
                    this.this$0.commitLock.readLock().unlock();
                }
            } else {
                throw new UnsupportedOperationException("read-only");
            }
        }

        public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
            if (this.this$0.fullTx) {
                this.this$0.commitLock.readLock().lock();
                Lock lock;
                try {
                    lock = this.this$0.locks[Store.lockPos(recid)].writeLock();
                    lock.lock();
                    A oldVal = getNoLock(recid, serializer);
                    boolean ret = oldVal != null && oldVal.equals(expectedOldValue);
                    if (ret) {
                        this.mod.put(recid, Fun.t2(newValue, serializer));
                    }
                    lock.unlock();
                    this.this$0.commitLock.readLock().unlock();
                    return ret;
                } catch (Throwable th) {
                    this.this$0.commitLock.readLock().unlock();
                }
            } else {
                throw new UnsupportedOperationException("read-only");
            }
        }

        public <A> void delete(long recid, Serializer<A> serializer) {
            if (this.this$0.fullTx) {
                this.this$0.commitLock.readLock().lock();
                try {
                    this.mod.put(recid, Fun.t2(TxEngine.TOMBSTONE, serializer));
                } finally {
                    this.this$0.commitLock.readLock().unlock();
                }
            } else {
                throw new UnsupportedOperationException("read-only");
            }
        }

        public void close() {
            this.closed = true;
            this.old.clear();
            this.ref.clear();
        }

        public boolean isClosed() {
            return this.closed;
        }

        public void commit() {
            if (this.this$0.fullTx) {
                this.this$0.commitLock.writeLock().lock();
                try {
                    if (!this.closed) {
                        if (this.this$0.uncommitedData) {
                            throw new IllegalAccessError("uncommitted data");
                        }
                        long recid;
                        Tx tx;
                        this.this$0.txs.remove(this.ref);
                        this.this$0.cleanTxQueue();
                        if (this.pojo.hasUnsavedChanges()) {
                            this.pojo.save(this);
                        }
                        LongMapIterator oldIter = this.old.longMapIterator();
                        while (oldIter.moveToNext()) {
                            recid = oldIter.key();
                            for (Reference<Tx> ref2 : this.this$0.txs) {
                                tx = (Tx) ref2.get();
                                if (tx != this && tx != null && tx.mod.containsKey(recid)) {
                                    close();
                                    throw new TxRollbackException();
                                }
                            }
                        }
                        LongMapIterator<Tuple2> iter = this.mod.longMapIterator();
                        while (iter.moveToNext()) {
                            if (this.old.containsKey(iter.key())) {
                                close();
                                throw new TxRollbackException();
                            }
                        }
                        iter = this.mod.longMapIterator();
                        while (iter.moveToNext()) {
                            recid = iter.key();
                            Tuple2 val = (Tuple2) iter.value();
                            Serializer ser = val.b;
                            Object old = this.this$0.superGet(recid, ser);
                            if (old == null) {
                                old = TxEngine.TOMBSTONE;
                            }
                            for (Reference<Tx> txr : this.this$0.txs) {
                                tx = (Tx) txr.get();
                                if (!(tx == null || tx == this)) {
                                    tx.old.putIfAbsent(recid, old);
                                }
                            }
                            if (val.a == TxEngine.TOMBSTONE) {
                                this.this$0.superDelete(recid, ser);
                            } else {
                                this.this$0.superUpdate(recid, val.a, ser);
                            }
                        }
                        getWrappedEngine().getSerializerPojo().registered = this.pojo.registered;
                        this.this$0.superCommit();
                        close();
                        this.this$0.commitLock.writeLock().unlock();
                    }
                } finally {
                    this.this$0.commitLock.writeLock().unlock();
                }
            } else {
                throw new UnsupportedOperationException("read-only");
            }
        }

        public void rollback() throws UnsupportedOperationException {
            if (this.this$0.fullTx) {
                this.this$0.commitLock.writeLock().lock();
                try {
                    if (!this.closed) {
                        if (this.this$0.uncommitedData) {
                            throw new IllegalAccessError("uncommitted data");
                        }
                        this.this$0.txs.remove(this.ref);
                        this.this$0.cleanTxQueue();
                        for (Long prealloc : this.usedPreallocatedRecids) {
                            this.this$0.superDelete(prealloc.longValue(), null);
                        }
                        this.this$0.superCommit();
                        close();
                        this.this$0.commitLock.writeLock().unlock();
                    }
                } finally {
                    this.this$0.commitLock.writeLock().unlock();
                }
            } else {
                throw new UnsupportedOperationException("read-only");
            }
        }

        public boolean isReadOnly() {
            return !this.this$0.fullTx;
        }

        public boolean canRollback() {
            return this.this$0.fullTx;
        }

        public boolean canSnapshot() {
            return false;
        }

        public Engine snapshot() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public void clearCache() {
        }

        public void compact() {
        }

        public SerializerPojo getSerializerPojo() {
            return this.pojo;
        }

        public void closeListenerRegister(Runnable closeListener) {
            throw new UnsupportedOperationException();
        }

        public void closeListenerUnregister(Runnable closeListener) {
            throw new UnsupportedOperationException();
        }

        public Engine getWrappedEngine() {
            return this.this$0.getWrappedEngine();
        }
    }

    static {
        $assertionsDisabled = !TxEngine.class.desiredAssertionStatus();
        TOMBSTONE = new Object();
    }

    protected TxEngine(Engine engine, boolean fullTx) {
        super(engine);
        this.commitLock = new ReentrantReadWriteLock(false);
        this.locks = new ReentrantReadWriteLock[TransportMediator.FLAG_KEY_MEDIA_NEXT];
        for (int i = 0; i < this.locks.length; i++) {
            this.locks[i] = new ReentrantReadWriteLock(false);
        }
        this.uncommitedData = false;
        this.txs = new LinkedHashSet();
        this.txQueue = new ReferenceQueue();
        this.PREALLOC_RECID_SIZE = TransportMediator.FLAG_KEY_MEDIA_NEXT;
        this.fullTx = fullTx;
        this.preallocRecids = fullTx ? new ArrayBlockingQueue(TransportMediator.FLAG_KEY_MEDIA_NEXT) : null;
    }

    protected Long preallocRecidTake() {
        if ($assertionsDisabled || this.commitLock.isWriteLockedByCurrentThread()) {
            Long recid = (Long) this.preallocRecids.poll();
            if (recid != null) {
                return recid;
            }
            if (this.uncommitedData) {
                throw new IllegalAccessError("uncommited data");
            }
            for (int i = 0; i < TransportMediator.FLAG_KEY_MEDIA_NEXT; i++) {
                this.preallocRecids.add(Long.valueOf(super.preallocate()));
            }
            recid = Long.valueOf(super.preallocate());
            super.commit();
            this.uncommitedData = false;
            return recid;
        }
        throw new AssertionError();
    }

    public static Engine createSnapshotFor(Engine engine) {
        if (engine.isReadOnly()) {
            return engine;
        }
        if (engine instanceof TxEngine) {
            return ((TxEngine) engine).snapshot();
        }
        if (engine instanceof EngineWrapper) {
            return createSnapshotFor(((EngineWrapper) engine).getWrappedEngine());
        }
        throw new UnsupportedOperationException("Snapshots are not enabled, use DBMaker.snapshotEnable()");
    }

    public boolean canSnapshot() {
        return true;
    }

    public Engine snapshot() {
        this.commitLock.writeLock().lock();
        try {
            cleanTxQueue();
            if (this.uncommitedData && canRollback()) {
                throw new IllegalAccessError("Can not create snapshot with uncommited data");
            }
            Engine tx = new Tx(this);
            return tx;
        } finally {
            this.commitLock.writeLock().unlock();
        }
    }

    protected void cleanTxQueue() {
        if ($assertionsDisabled || this.commitLock.writeLock().isHeldByCurrentThread()) {
            Reference<? extends Tx> ref = this.txQueue.poll();
            while (ref != null) {
                this.txs.remove(ref);
                ref = this.txQueue.poll();
            }
            return;
        }
        throw new AssertionError();
    }

    public long preallocate() {
        this.commitLock.writeLock().lock();
        Lock lock;
        try {
            this.uncommitedData = true;
            long recid = super.preallocate();
            lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            for (Reference<Tx> txr : this.txs) {
                Tx tx = (Tx) txr.get();
                if (tx != null) {
                    tx.old.putIfAbsent(recid, TOMBSTONE);
                }
            }
            lock.unlock();
            this.commitLock.writeLock().unlock();
            return recid;
        } catch (Throwable th) {
            this.commitLock.writeLock().unlock();
        }
    }

    public void preallocate(long[] recids) {
        Lock lock;
        this.commitLock.writeLock().lock();
        try {
            this.uncommitedData = true;
            super.preallocate(recids);
            for (long recid : recids) {
                lock = this.locks[Store.lockPos(recid)].writeLock();
                lock.lock();
                for (Reference<Tx> txr : this.txs) {
                    Tx tx = (Tx) txr.get();
                    if (tx != null) {
                        tx.old.putIfAbsent(recid, TOMBSTONE);
                    }
                }
                lock.unlock();
            }
            this.commitLock.writeLock().unlock();
        } catch (Throwable th) {
            this.commitLock.writeLock().unlock();
        }
    }

    public <A> long put(A value, Serializer<A> serializer) {
        this.commitLock.readLock().lock();
        Lock lock;
        try {
            this.uncommitedData = true;
            long recid = super.put(value, serializer);
            lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            for (Reference<Tx> txr : this.txs) {
                Tx tx = (Tx) txr.get();
                if (tx != null) {
                    tx.old.putIfAbsent(recid, TOMBSTONE);
                }
            }
            lock.unlock();
            this.commitLock.readLock().unlock();
            return recid;
        } catch (Throwable th) {
            this.commitLock.readLock().unlock();
        }
    }

    public <A> A get(long recid, Serializer<A> serializer) {
        this.commitLock.readLock().lock();
        try {
            A a = super.get(recid, serializer);
            return a;
        } finally {
            this.commitLock.readLock().unlock();
        }
    }

    public <A> void update(long recid, A value, Serializer<A> serializer) {
        Lock lock;
        this.commitLock.readLock().lock();
        try {
            this.uncommitedData = true;
            lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            Object old = get(recid, serializer);
            for (Reference<Tx> txr : this.txs) {
                Tx tx = (Tx) txr.get();
                if (tx != null) {
                    tx.old.putIfAbsent(recid, old);
                }
            }
            super.update(recid, value, serializer);
            lock.unlock();
            this.commitLock.readLock().unlock();
        } catch (Throwable th) {
            this.commitLock.readLock().unlock();
        }
    }

    public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
        this.commitLock.readLock().lock();
        Lock lock;
        try {
            this.uncommitedData = true;
            lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            boolean ret = super.compareAndSwap(recid, expectedOldValue, newValue, serializer);
            if (ret) {
                for (Reference<Tx> txr : this.txs) {
                    Tx tx = (Tx) txr.get();
                    if (tx != null) {
                        tx.old.putIfAbsent(recid, expectedOldValue);
                    }
                }
            }
            lock.unlock();
            this.commitLock.readLock().unlock();
            return ret;
        } catch (Throwable th) {
            this.commitLock.readLock().unlock();
        }
    }

    public <A> void delete(long recid, Serializer<A> serializer) {
        this.commitLock.readLock().lock();
        Lock lock;
        try {
            this.uncommitedData = true;
            lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            Object old = get(recid, serializer);
            for (Reference<Tx> txr : this.txs) {
                Tx tx = (Tx) txr.get();
                if (tx != null) {
                    tx.old.putIfAbsent(recid, old);
                }
            }
            super.delete(recid, serializer);
            lock.unlock();
            this.commitLock.readLock().unlock();
        } catch (Throwable th) {
            this.commitLock.readLock().unlock();
        }
    }

    public void close() {
        this.commitLock.writeLock().lock();
        try {
            super.close();
        } finally {
            this.commitLock.writeLock().unlock();
        }
    }

    public void commit() {
        this.commitLock.writeLock().lock();
        try {
            cleanTxQueue();
            super.commit();
            this.uncommitedData = false;
        } finally {
            this.commitLock.writeLock().unlock();
        }
    }

    public void rollback() {
        this.commitLock.writeLock().lock();
        try {
            cleanTxQueue();
            super.rollback();
            this.uncommitedData = false;
        } finally {
            this.commitLock.writeLock().unlock();
        }
    }

    protected void superCommit() {
        if ($assertionsDisabled || this.commitLock.isWriteLockedByCurrentThread()) {
            super.commit();
            return;
        }
        throw new AssertionError();
    }

    protected <A> void superUpdate(long recid, A value, Serializer<A> serializer) {
        if ($assertionsDisabled || this.commitLock.isWriteLockedByCurrentThread()) {
            super.update(recid, value, serializer);
            return;
        }
        throw new AssertionError();
    }

    protected <A> void superDelete(long recid, Serializer<A> serializer) {
        if ($assertionsDisabled || this.commitLock.isWriteLockedByCurrentThread()) {
            super.delete(recid, serializer);
            return;
        }
        throw new AssertionError();
    }

    protected <A> A superGet(long recid, Serializer<A> serializer) {
        if ($assertionsDisabled || this.commitLock.isWriteLockedByCurrentThread()) {
            return super.get(recid, serializer);
        }
        throw new AssertionError();
    }
}
