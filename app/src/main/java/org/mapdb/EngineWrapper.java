package org.mapdb;

import java.io.IOError;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class EngineWrapper implements Engine {
    public static final Engine CLOSED;
    private Engine engine;

    public static class CloseOnJVMShutdown extends EngineWrapper {
        Thread hook;
        final Runnable hookRunnable;
        protected final AtomicBoolean shutdownHappened;

        public CloseOnJVMShutdown(Engine engine) {
            super(engine);
            this.shutdownHappened = new AtomicBoolean(false);
            this.hookRunnable = new Runnable() {
                public void run() {
                    CloseOnJVMShutdown.this.shutdownHappened.set(true);
                    CloseOnJVMShutdown.this.hook = null;
                    if (!CloseOnJVMShutdown.this.isClosed()) {
                        CloseOnJVMShutdown.this.close();
                    }
                }
            };
            this.hook = new Thread(this.hookRunnable, "MapDB shutdown hook");
            Runtime.getRuntime().addShutdownHook(this.hook);
        }

        public void close() {
            super.close();
            if (!(this.shutdownHappened.get() || this.hook == null)) {
                Runtime.getRuntime().removeShutdownHook(this.hook);
            }
            this.hook = null;
        }
    }

    public static class ImmutabilityCheckEngine extends EngineWrapper {
        protected LongConcurrentHashMap<Item> items;

        protected static class Item {
            final Object item;
            final int oldChecksum;
            final Serializer serializer;

            public Item(Serializer serializer, Object item) {
                if (item == null || serializer == null) {
                    throw new AssertionError("null");
                }
                this.serializer = serializer;
                this.item = item;
                this.oldChecksum = checksum();
                if (this.oldChecksum != checksum()) {
                    throw new AssertionError("inconsistent serialization");
                }
            }

            private int checksum() {
                try {
                    DataOutput2 out = new DataOutput2();
                    this.serializer.serialize(out, this.item);
                    return Arrays.hashCode(out.copyBytes());
                } catch (IOException e) {
                    throw new IOError(e);
                }
            }

            void check() {
                if (this.oldChecksum != checksum()) {
                    throw new AssertionError("Record instance was modified: \n  " + this.item + "\n  " + this.serializer);
                }
            }
        }

        protected ImmutabilityCheckEngine(Engine engine) {
            super(engine);
            this.items = new LongConcurrentHashMap();
        }

        public <A> A get(long recid, Serializer<A> serializer) {
            Item item = (Item) this.items.get(recid);
            if (item != null) {
                item.check();
            }
            A ret = super.get(recid, serializer);
            if (ret != null) {
                this.items.put(recid, new Item(serializer, ret));
            }
            return ret;
        }

        public <A> long put(A value, Serializer<A> serializer) {
            long ret = super.put(value, serializer);
            if (value != null) {
                this.items.put(ret, new Item(serializer, value));
            }
            return ret;
        }

        public <A> void update(long recid, A value, Serializer<A> serializer) {
            Item item = (Item) this.items.get(recid);
            if (item != null) {
                item.check();
            }
            super.update(recid, value, serializer);
            if (value != null) {
                this.items.put(recid, new Item(serializer, value));
            }
        }

        public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
            Item item = (Item) this.items.get(recid);
            if (item != null) {
                item.check();
            }
            boolean ret = super.compareAndSwap(recid, expectedOldValue, newValue, serializer);
            if (ret && newValue != null) {
                this.items.put(recid, new Item(serializer, item));
            }
            return ret;
        }

        public void close() {
            super.close();
            Iterator<Item> iter = this.items.valuesIterator();
            while (iter.hasNext()) {
                ((Item) iter.next()).check();
            }
            this.items.clear();
        }
    }

    public static class ReadOnlyEngine extends EngineWrapper {
        public ReadOnlyEngine(Engine engine) {
            super(engine);
        }

        public long preallocate() {
            throw new UnsupportedOperationException("Read-only");
        }

        public void preallocate(long[] recids) {
            throw new UnsupportedOperationException("Read-only");
        }

        public <A> boolean compareAndSwap(long recid, A a, A a2, Serializer<A> serializer) {
            throw new UnsupportedOperationException("Read-only");
        }

        public <A> long put(A a, Serializer<A> serializer) {
            throw new UnsupportedOperationException("Read-only");
        }

        public <A> void update(long recid, A a, Serializer<A> serializer) {
            throw new UnsupportedOperationException("Read-only");
        }

        public <A> void delete(long recid, Serializer<A> serializer) {
            throw new UnsupportedOperationException("Read-only");
        }

        public void commit() {
            throw new UnsupportedOperationException("Read-only");
        }

        public void rollback() {
            throw new UnsupportedOperationException("Read-only");
        }

        public boolean isReadOnly() {
            return true;
        }

        public boolean canSnapshot() {
            return true;
        }

        public Engine snapshot() throws UnsupportedOperationException {
            return this;
        }
    }

    public static class SerializerCheckEngineWrapper extends EngineWrapper {
        protected LongMap<Serializer> recid2serializer;

        protected SerializerCheckEngineWrapper(Engine engine) {
            super(engine);
            this.recid2serializer = new LongConcurrentHashMap();
        }

        protected synchronized <A> void checkSerializer(long recid, Serializer<A> serializer) {
            Serializer<A> other = (Serializer) this.recid2serializer.get(recid);
            if (other == null) {
                this.recid2serializer.put(recid, serializer);
            } else if (!(other == serializer || other.getClass() == serializer.getClass())) {
                throw new IllegalArgumentException("Serializer does not match. \n found: " + serializer + " \n expected: " + other);
            }
        }

        public <A> A get(long recid, Serializer<A> serializer) {
            checkSerializer(recid, serializer);
            return super.get(recid, serializer);
        }

        public <A> void update(long recid, A value, Serializer<A> serializer) {
            checkSerializer(recid, serializer);
            super.update(recid, value, serializer);
        }

        public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
            checkSerializer(recid, serializer);
            return super.compareAndSwap(recid, expectedOldValue, newValue, serializer);
        }

        public <A> void delete(long recid, Serializer<A> serializer) {
            checkSerializer(recid, serializer);
            this.recid2serializer.remove(recid);
            super.delete(recid, serializer);
        }
    }

    public static class SynchronizedEngineWrapper extends EngineWrapper {
        protected SynchronizedEngineWrapper(Engine engine) {
            super(engine);
        }

        public synchronized long preallocate() {
            return super.preallocate();
        }

        public synchronized void preallocate(long[] recids) {
            super.preallocate(recids);
        }

        public synchronized <A> long put(A value, Serializer<A> serializer) {
            return super.put(value, serializer);
        }

        public synchronized <A> A get(long recid, Serializer<A> serializer) {
            return super.get(recid, serializer);
        }

        public synchronized <A> void update(long recid, A value, Serializer<A> serializer) {
            super.update(recid, value, serializer);
        }

        public synchronized <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
            return super.compareAndSwap(recid, expectedOldValue, newValue, serializer);
        }

        public synchronized <A> void delete(long recid, Serializer<A> serializer) {
            super.delete(recid, serializer);
        }

        public synchronized void close() {
            super.close();
        }

        public synchronized boolean isClosed() {
            return super.isClosed();
        }

        public synchronized void commit() {
            super.commit();
        }

        public synchronized void rollback() {
            super.rollback();
        }

        public synchronized boolean isReadOnly() {
            return super.isReadOnly();
        }

        public synchronized boolean canSnapshot() {
            return super.canSnapshot();
        }

        public synchronized Engine snapshot() throws UnsupportedOperationException {
            return super.snapshot();
        }

        public synchronized void compact() {
            super.compact();
        }
    }

    protected EngineWrapper(Engine engine) {
        if (engine == null) {
            throw new IllegalArgumentException();
        }
        this.engine = engine;
    }

    public long preallocate() {
        return getWrappedEngine().preallocate();
    }

    public void preallocate(long[] recids) {
        getWrappedEngine().preallocate(recids);
    }

    public <A> long put(A value, Serializer<A> serializer) {
        return getWrappedEngine().put(value, serializer);
    }

    public <A> A get(long recid, Serializer<A> serializer) {
        return getWrappedEngine().get(recid, serializer);
    }

    public <A> void update(long recid, A value, Serializer<A> serializer) {
        getWrappedEngine().update(recid, value, serializer);
    }

    public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
        return getWrappedEngine().compareAndSwap(recid, expectedOldValue, newValue, serializer);
    }

    public <A> void delete(long recid, Serializer<A> serializer) {
        getWrappedEngine().delete(recid, serializer);
    }

    public void close() {
        Engine e = this.engine;
        if (e != null) {
            try {
                e.close();
            } catch (Throwable th) {
                this.engine = CLOSED;
            }
        }
        this.engine = CLOSED;
    }

    public boolean isClosed() {
        return this.engine == CLOSED || this.engine == null;
    }

    public void commit() {
        getWrappedEngine().commit();
    }

    public void rollback() {
        getWrappedEngine().rollback();
    }

    public boolean isReadOnly() {
        return getWrappedEngine().isReadOnly();
    }

    public boolean canRollback() {
        return getWrappedEngine().canRollback();
    }

    public boolean canSnapshot() {
        return getWrappedEngine().canSnapshot();
    }

    public Engine snapshot() throws UnsupportedOperationException {
        return getWrappedEngine().snapshot();
    }

    public void clearCache() {
        getWrappedEngine().clearCache();
    }

    public void compact() {
        getWrappedEngine().compact();
    }

    public SerializerPojo getSerializerPojo() {
        return getWrappedEngine().getSerializerPojo();
    }

    public void closeListenerRegister(Runnable closeListener) {
        getWrappedEngine().closeListenerRegister(closeListener);
    }

    public void closeListenerUnregister(Runnable closeListener) {
        getWrappedEngine().closeListenerUnregister(closeListener);
    }

    public Engine getWrappedEngine() {
        return (Engine) checkClosed(this.engine);
    }

    protected static <V> V checkClosed(V v) {
        if (v != null) {
            return v;
        }
        throw new IllegalAccessError("DB has been closed");
    }

    static {
        CLOSED = new Engine() {
            public long preallocate() {
                throw new IllegalAccessError("already closed");
            }

            public void preallocate(long[] recids) {
                throw new IllegalAccessError("already closed");
            }

            public <A> long put(A a, Serializer<A> serializer) {
                throw new IllegalAccessError("already closed");
            }

            public <A> A get(long recid, Serializer<A> serializer) {
                throw new IllegalAccessError("already closed");
            }

            public <A> void update(long recid, A a, Serializer<A> serializer) {
                throw new IllegalAccessError("already closed");
            }

            public <A> boolean compareAndSwap(long recid, A a, A a2, Serializer<A> serializer) {
                throw new IllegalAccessError("already closed");
            }

            public <A> void delete(long recid, Serializer<A> serializer) {
                throw new IllegalAccessError("already closed");
            }

            public void close() {
                throw new IllegalAccessError("already closed");
            }

            public boolean isClosed() {
                return true;
            }

            public void commit() {
                throw new IllegalAccessError("already closed");
            }

            public void rollback() throws UnsupportedOperationException {
                throw new IllegalAccessError("already closed");
            }

            public boolean isReadOnly() {
                throw new IllegalAccessError("already closed");
            }

            public boolean canRollback() {
                throw new IllegalAccessError("already closed");
            }

            public boolean canSnapshot() {
                throw new IllegalAccessError("already closed");
            }

            public Engine snapshot() throws UnsupportedOperationException {
                throw new IllegalAccessError("already closed");
            }

            public void clearCache() {
                throw new IllegalAccessError("already closed");
            }

            public void compact() {
                throw new IllegalAccessError("already closed");
            }

            public SerializerPojo getSerializerPojo() {
                throw new IllegalAccessError("already closed");
            }

            public void closeListenerRegister(Runnable closeListener) {
                throw new IllegalAccessError("already closed");
            }

            public void closeListenerUnregister(Runnable closeListener) {
            }
        };
    }
}
