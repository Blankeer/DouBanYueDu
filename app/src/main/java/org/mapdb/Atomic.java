package org.mapdb;

import java.io.DataInput;
import java.io.IOException;

public final class Atomic {

    public static final class Boolean {
        protected final Engine engine;
        protected final long recid;

        public Boolean(Engine engine, long recid) {
            this.engine = engine;
            this.recid = recid;
        }

        public long getRecid() {
            return this.recid;
        }

        public final boolean get() {
            return ((Boolean) this.engine.get(this.recid, Serializer.BOOLEAN)).booleanValue();
        }

        public final boolean compareAndSet(boolean expect, boolean update) {
            return this.engine.compareAndSwap(this.recid, Boolean.valueOf(expect), Boolean.valueOf(update), Serializer.BOOLEAN);
        }

        public final void set(boolean newValue) {
            this.engine.update(this.recid, Boolean.valueOf(newValue), Serializer.BOOLEAN);
        }

        public final boolean getAndSet(boolean newValue) {
            boolean current;
            do {
                current = get();
            } while (!compareAndSet(current, newValue));
            return current;
        }

        public String toString() {
            return Boolean.toString(get());
        }
    }

    public static final class Integer extends Number {
        private static final long serialVersionUID = 4615119399830853054L;
        protected final Engine engine;
        protected final long recid;

        public Integer(Engine engine, long recid) {
            this.engine = engine;
            this.recid = recid;
        }

        public long getRecid() {
            return this.recid;
        }

        public final int get() {
            return ((Integer) this.engine.get(this.recid, Serializer.INTEGER)).intValue();
        }

        public final void set(int newValue) {
            this.engine.update(this.recid, Integer.valueOf(newValue), Serializer.INTEGER);
        }

        public final int getAndSet(int newValue) {
            int current;
            do {
                current = get();
            } while (!compareAndSet(current, newValue));
            return current;
        }

        public final boolean compareAndSet(int expect, int update) {
            return this.engine.compareAndSwap(this.recid, Integer.valueOf(expect), Integer.valueOf(update), Serializer.INTEGER);
        }

        public final int getAndIncrement() {
            int current;
            do {
                current = get();
            } while (!compareAndSet(current, current + 1));
            return current;
        }

        public final int getAndDecrement() {
            int current;
            do {
                current = get();
            } while (!compareAndSet(current, current - 1));
            return current;
        }

        public final int getAndAdd(int delta) {
            int current;
            do {
                current = get();
            } while (!compareAndSet(current, current + delta));
            return current;
        }

        public final int incrementAndGet() {
            int next;
            int current;
            do {
                current = get();
                next = current + 1;
            } while (!compareAndSet(current, next));
            return next;
        }

        public final int decrementAndGet() {
            int next;
            int current;
            do {
                current = get();
                next = current - 1;
            } while (!compareAndSet(current, next));
            return next;
        }

        public final int addAndGet(int delta) {
            int next;
            int current;
            do {
                current = get();
                next = current + delta;
            } while (!compareAndSet(current, next));
            return next;
        }

        public String toString() {
            return Integer.toString(get());
        }

        public int intValue() {
            return get();
        }

        public long longValue() {
            return (long) get();
        }

        public float floatValue() {
            return (float) get();
        }

        public double doubleValue() {
            return (double) get();
        }
    }

    public static final class Long extends Number {
        private static final long serialVersionUID = 2882620413591274781L;
        protected final Engine engine;
        protected final long recid;

        public Long(Engine engine, long recid) {
            this.engine = engine;
            this.recid = recid;
        }

        public long getRecid() {
            return this.recid;
        }

        public final long get() {
            return ((Long) this.engine.get(this.recid, Serializer.LONG)).longValue();
        }

        public final void set(long newValue) {
            this.engine.update(this.recid, Long.valueOf(newValue), Serializer.LONG);
        }

        public final long getAndSet(long newValue) {
            long current;
            do {
                current = get();
            } while (!compareAndSet(current, newValue));
            return current;
        }

        public final boolean compareAndSet(long expect, long update) {
            return this.engine.compareAndSwap(this.recid, Long.valueOf(expect), Long.valueOf(update), Serializer.LONG);
        }

        public final long getAndIncrement() {
            long current;
            do {
                current = get();
            } while (!compareAndSet(current, current + 1));
            return current;
        }

        public final long getAndDecrement() {
            long current;
            do {
                current = get();
            } while (!compareAndSet(current, current - 1));
            return current;
        }

        public final long getAndAdd(long delta) {
            long current;
            do {
                current = get();
            } while (!compareAndSet(current, current + delta));
            return current;
        }

        public final long incrementAndGet() {
            long next;
            long current;
            do {
                current = get();
                next = current + 1;
            } while (!compareAndSet(current, next));
            return next;
        }

        public final long decrementAndGet() {
            long next;
            long current;
            do {
                current = get();
                next = current - 1;
            } while (!compareAndSet(current, next));
            return next;
        }

        public final long addAndGet(long delta) {
            long next;
            long current;
            do {
                current = get();
                next = current + delta;
            } while (!compareAndSet(current, next));
            return next;
        }

        public String toString() {
            return Long.toString(get());
        }

        public int intValue() {
            return (int) get();
        }

        public long longValue() {
            return get();
        }

        public float floatValue() {
            return (float) get();
        }

        public double doubleValue() {
            return (double) get();
        }
    }

    public static final class String {
        protected final Engine engine;
        protected final long recid;

        public String(Engine engine, long recid) {
            this.engine = engine;
            this.recid = recid;
        }

        public long getRecid() {
            return this.recid;
        }

        public String toString() {
            return get();
        }

        public final String get() {
            return (String) this.engine.get(this.recid, Serializer.STRING_NOSIZE);
        }

        public final boolean compareAndSet(String expect, String update) {
            return this.engine.compareAndSwap(this.recid, expect, update, Serializer.STRING_NOSIZE);
        }

        public final void set(String newValue) {
            this.engine.update(this.recid, newValue, Serializer.STRING_NOSIZE);
        }

        public final String getAndSet(String newValue) {
            String current;
            do {
                current = get();
            } while (!compareAndSet(current, newValue));
            return current;
        }
    }

    public static final class Var<E> {
        protected final Engine engine;
        protected final long recid;
        protected final Serializer<E> serializer;

        public Var(Engine engine, long recid, Serializer<E> serializer) {
            this.engine = engine;
            this.recid = recid;
            this.serializer = serializer;
        }

        protected Var(Engine engine, SerializerBase serializerBase, DataInput is, FastArrayList<Object> objectStack) throws IOException {
            objectStack.add(this);
            this.engine = engine;
            this.recid = DataInput2.unpackLong(is);
            this.serializer = (Serializer) serializerBase.deserialize(is, (FastArrayList) objectStack);
        }

        public long getRecid() {
            return this.recid;
        }

        public String toString() {
            E v = get();
            return v == null ? null : v.toString();
        }

        public final E get() {
            return this.engine.get(this.recid, this.serializer);
        }

        public final boolean compareAndSet(E expect, E update) {
            return this.engine.compareAndSwap(this.recid, expect, update, this.serializer);
        }

        public final void set(E newValue) {
            this.engine.update(this.recid, newValue, this.serializer);
        }

        public final E getAndSet(E newValue) {
            E current;
            do {
                current = get();
            } while (!compareAndSet(current, newValue));
            return current;
        }
    }

    private Atomic() {
    }
}
