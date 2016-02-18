package org.mapdb;

import com.path.android.jobqueue.JobManager;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.mapdb.Fun.Tuple2;
import org.mapdb.LongMap.LongMapIterator;

public class AsyncWriteEngine extends EngineWrapper implements Engine {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected static final Object TOMBSTONE;
    protected static final AtomicLong threadCounter;
    protected final AtomicReference<CountDownLatch> action;
    protected final CountDownLatch activeThreadsCount;
    protected final int asyncFlushDelay;
    protected volatile boolean closeInProgress;
    protected final ReentrantReadWriteLock commitLock;
    protected final int maxSize;
    protected final AtomicInteger size;
    protected volatile Throwable threadFailedException;
    protected final LongConcurrentHashMap<Tuple2<Object, Serializer>> writeCache;

    protected static final class WriterRunnable implements Runnable {
        protected final long asyncFlushDelay;
        private final ReentrantReadWriteLock commitLock;
        protected final WeakReference<AsyncWriteEngine> engineRef;
        protected final int maxParkSize;
        protected final AtomicInteger size;

        public WriterRunnable(AsyncWriteEngine engine) {
            this.engineRef = new WeakReference(engine);
            this.asyncFlushDelay = (long) engine.asyncFlushDelay;
            this.commitLock = engine.commitLock;
            this.size = engine.size;
            this.maxParkSize = engine.maxSize / 4;
        }

        public void run() {
            AsyncWriteEngine engine;
            do {
                try {
                    if (!(this.asyncFlushDelay == 0 || this.commitLock.isWriteLocked() || this.size.get() >= this.maxParkSize)) {
                        LockSupport.parkNanos(JobManager.NS_PER_MS * this.asyncFlushDelay);
                    }
                    engine = (AsyncWriteEngine) this.engineRef.get();
                    if (engine == null) {
                        engine = (AsyncWriteEngine) this.engineRef.get();
                        if (engine != null) {
                            engine.activeThreadsCount.countDown();
                            return;
                        }
                        return;
                    } else if (engine.threadFailedException != null) {
                        engine = (AsyncWriteEngine) this.engineRef.get();
                        if (engine != null) {
                            engine.activeThreadsCount.countDown();
                            return;
                        }
                        return;
                    }
                } catch (Throwable th) {
                    engine = (AsyncWriteEngine) this.engineRef.get();
                    if (engine != null) {
                        engine.activeThreadsCount.countDown();
                    }
                }
            } while (engine.runWriter());
            engine = (AsyncWriteEngine) this.engineRef.get();
            if (engine != null) {
                engine.activeThreadsCount.countDown();
            }
        }
    }

    static {
        $assertionsDisabled = !AsyncWriteEngine.class.desiredAssertionStatus();
        threadCounter = new AtomicLong();
        TOMBSTONE = new Object();
    }

    public AsyncWriteEngine(Engine engine, int _asyncFlushDelay, int queueSize, Executor executor) {
        super(engine);
        this.size = new AtomicInteger();
        this.writeCache = new LongConcurrentHashMap();
        this.commitLock = new ReentrantReadWriteLock(false);
        this.activeThreadsCount = new CountDownLatch(1);
        this.threadFailedException = null;
        this.closeInProgress = false;
        this.action = new AtomicReference(null);
        this.asyncFlushDelay = _asyncFlushDelay;
        this.maxSize = queueSize;
        startThreads(executor);
    }

    public AsyncWriteEngine(Engine engine) {
        this(engine, 100, CC.ASYNC_WRITE_QUEUE_SIZE, null);
    }

    protected void startThreads(Executor executor) {
        Runnable writerRun = new WriterRunnable(this);
        if (executor != null) {
            executor.execute(writerRun);
            return;
        }
        Thread writerThread = new Thread(writerRun, "MapDB writer #" + threadCounter.incrementAndGet());
        writerThread.setDaemon(true);
        writerThread.start();
    }

    protected boolean runWriter() throws InterruptedException {
        CountDownLatch latch = (CountDownLatch) this.action.getAndSet(null);
        do {
            LongMapIterator<Tuple2<Object, Serializer>> iter = this.writeCache.longMapIterator();
            while (iter.moveToNext()) {
                long recid = iter.key();
                Tuple2<Object, Serializer> item = (Tuple2) iter.value();
                if (item != null) {
                    if (item.a == TOMBSTONE) {
                        super.delete(recid, (Serializer) item.b);
                    } else {
                        super.update(recid, item.a, (Serializer) item.b);
                    }
                    if (this.writeCache.remove(recid, item)) {
                        this.size.decrementAndGet();
                    }
                }
            }
            if (latch == null) {
                break;
            }
        } while (!this.writeCache.isEmpty());
        if (latch != null) {
            if ($assertionsDisabled || this.writeCache.isEmpty()) {
                long count = latch.getCount();
                if (count == 0) {
                    return false;
                }
                if (count == 1) {
                    super.commit();
                    latch.countDown();
                } else if (count == 2) {
                    super.rollback();
                    latch.countDown();
                    latch.countDown();
                } else if (count == 3) {
                    super.compact();
                    latch.countDown();
                    latch.countDown();
                    latch.countDown();
                } else {
                    throw new AssertionError();
                }
            }
            throw new AssertionError();
        }
        return true;
    }

    protected void checkState() {
        if (this.closeInProgress) {
            throw new IllegalAccessError("db has been closed");
        } else if (this.threadFailedException != null) {
            throw new RuntimeException("Writer thread failed", this.threadFailedException);
        }
    }

    public <A> long put(A value, Serializer<A> serializer) {
        int size2 = 0;
        this.commitLock.readLock().lock();
        try {
            checkState();
            long recid = preallocate();
            if (this.writeCache.put(recid, new Tuple2(value, serializer)) == null) {
                size2 = this.size.incrementAndGet();
            }
            this.commitLock.readLock().unlock();
            if (size2 > this.maxSize) {
                clearCache();
            }
            return recid;
        } catch (Throwable th) {
            this.commitLock.readLock().unlock();
        }
    }

    public <A> A get(long recid, Serializer<A> serializer) {
        this.commitLock.readLock().lock();
        try {
            checkState();
            Tuple2<Object, Serializer> item = (Tuple2) this.writeCache.get(recid);
            A a;
            if (item == null) {
                a = super.get(recid, serializer);
                this.commitLock.readLock().unlock();
                return a;
            } else if (item.a == TOMBSTONE) {
                return null;
            } else {
                a = item.a;
                this.commitLock.readLock().unlock();
                return a;
            }
        } finally {
            this.commitLock.readLock().unlock();
        }
    }

    public <A> void update(long recid, A value, Serializer<A> serializer) {
        int size2 = 0;
        this.commitLock.readLock().lock();
        try {
            checkState();
            if (this.writeCache.put(recid, new Tuple2(value, serializer)) == null) {
                size2 = this.size.incrementAndGet();
            }
            this.commitLock.readLock().unlock();
            if (size2 > this.maxSize) {
                clearCache();
            }
        } catch (Throwable th) {
            this.commitLock.readLock().unlock();
        }
    }

    public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
        int size2 = 0;
        this.commitLock.writeLock().lock();
        try {
            boolean ret;
            checkState();
            Tuple2<Object, Serializer> existing = (Tuple2) this.writeCache.get(recid);
            A oldValue = existing != null ? existing.a : super.get(recid, serializer);
            if (oldValue == expectedOldValue || (oldValue != null && oldValue.equals(expectedOldValue))) {
                if (this.writeCache.put(recid, new Tuple2(newValue, serializer)) == null) {
                    size2 = this.size.incrementAndGet();
                }
                ret = true;
            } else {
                ret = false;
            }
            this.commitLock.writeLock().unlock();
            if (size2 > this.maxSize) {
                clearCache();
            }
            return ret;
        } catch (Throwable th) {
            this.commitLock.writeLock().unlock();
        }
    }

    public <A> void delete(long recid, Serializer<A> serializer) {
        update(recid, TOMBSTONE, serializer);
    }

    public void close() {
        this.commitLock.writeLock().lock();
        try {
            if (this.closeInProgress) {
                this.commitLock.writeLock().unlock();
                return;
            }
            checkState();
            this.closeInProgress = true;
            if (this.action.compareAndSet(null, new CountDownLatch(0))) {
                do {
                } while (!this.activeThreadsCount.await(1000, TimeUnit.MILLISECONDS));
                super.close();
                this.commitLock.writeLock().unlock();
                return;
            }
            throw new AssertionError();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Throwable th) {
            this.commitLock.writeLock().unlock();
        }
    }

    protected void waitForAction(int actionNumber) {
        this.commitLock.writeLock().lock();
        try {
            checkState();
            CountDownLatch msg = new CountDownLatch(actionNumber);
            if (this.action.compareAndSet(null, msg)) {
                while (!msg.await(100, TimeUnit.MILLISECONDS)) {
                    checkState();
                }
                this.commitLock.writeLock().unlock();
                return;
            }
            throw new AssertionError();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Throwable th) {
            this.commitLock.writeLock().unlock();
        }
    }

    public void commit() {
        waitForAction(1);
    }

    public void rollback() {
        waitForAction(2);
    }

    public void compact() {
        waitForAction(3);
    }

    public void clearCache() {
        this.commitLock.writeLock().lock();
        try {
            checkState();
            while (!this.writeCache.isEmpty()) {
                checkState();
                Thread.sleep(100);
            }
            this.commitLock.writeLock().unlock();
            super.clearCache();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Throwable th) {
            this.commitLock.writeLock().unlock();
        }
    }
}
