package org.mapdb;

import java.util.Iterator;

public abstract class LongMap<V> {

    public interface LongMapIterator<V> {
        long key();

        boolean moveToNext();

        void remove();

        V value();
    }

    public abstract void clear();

    public abstract V get(long j);

    public abstract boolean isEmpty();

    public abstract LongMapIterator<V> longMapIterator();

    public abstract V put(long j, V v);

    public abstract V remove(long j);

    public abstract int size();

    public abstract Iterator<V> valuesIterator();

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName());
        b.append('[');
        boolean first = true;
        LongMapIterator<V> iter = longMapIterator();
        while (iter.moveToNext()) {
            if (first) {
                first = false;
            } else {
                b.append(", ");
            }
            b.append(iter.key());
            b.append(" => ");
            b.append(iter.value());
        }
        b.append(']');
        return b.toString();
    }
}
