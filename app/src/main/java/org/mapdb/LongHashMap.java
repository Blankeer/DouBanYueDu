package org.mapdb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import org.mapdb.LongMap.LongMapIterator;

public class LongHashMap<V> extends LongMap<V> implements Serializable {
    private static final int DEFAULT_SIZE = 16;
    private static final long serialVersionUID = 362340234235222265L;
    transient int elementCount;
    transient Entry<V>[] elementData;
    protected final long hashSalt;
    final float loadFactor;
    transient int modCount;
    int threshold;

    private static class AbstractMapIterator<V> {
        final LongHashMap<V> associatedMap;
        Entry<V> currentEntry;
        int expectedModCount;
        Entry<V> futureEntry;
        private int position;
        Entry<V> prevEntry;

        AbstractMapIterator(LongHashMap<V> hm) {
            this.position = 0;
            this.associatedMap = hm;
            this.expectedModCount = hm.modCount;
            this.futureEntry = null;
        }

        public boolean hasNext() {
            if (this.futureEntry != null) {
                return true;
            }
            while (this.position < this.associatedMap.elementData.length) {
                if (this.associatedMap.elementData[this.position] != null) {
                    return true;
                }
                this.position++;
            }
            return false;
        }

        final void checkConcurrentMod() throws ConcurrentModificationException {
            if (this.expectedModCount != this.associatedMap.modCount) {
                throw new ConcurrentModificationException();
            }
        }

        final void makeNext() {
            checkConcurrentMod();
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else if (this.futureEntry == null) {
                Entry[] entryArr = this.associatedMap.elementData;
                int i = this.position;
                this.position = i + 1;
                this.currentEntry = entryArr[i];
                this.futureEntry = this.currentEntry.next;
                this.prevEntry = null;
            } else {
                if (this.currentEntry != null) {
                    this.prevEntry = this.currentEntry;
                }
                this.currentEntry = this.futureEntry;
                this.futureEntry = this.futureEntry.next;
            }
        }

        public final void remove() {
            checkConcurrentMod();
            if (this.currentEntry == null) {
                throw new IllegalStateException();
            }
            if (this.prevEntry == null) {
                int index = this.currentEntry.origKeyHash & (this.associatedMap.elementData.length - 1);
                this.associatedMap.elementData[index] = this.associatedMap.elementData[index].next;
            } else {
                this.prevEntry.next = this.currentEntry.next;
            }
            this.currentEntry = null;
            this.expectedModCount++;
            LongHashMap longHashMap = this.associatedMap;
            longHashMap.modCount++;
            longHashMap = this.associatedMap;
            longHashMap.elementCount--;
        }
    }

    static class Entry<V> {
        final long key;
        Entry<V> next;
        final int origKeyHash;
        V value;

        public Entry(long key, int hash) {
            this.key = key;
            this.origKeyHash = hash;
        }
    }

    private static class EntryIterator<V> extends AbstractMapIterator<V> implements LongMapIterator<V> {
        EntryIterator(LongHashMap<V> map) {
            super(map);
        }

        public boolean moveToNext() {
            if (!hasNext()) {
                return false;
            }
            makeNext();
            return true;
        }

        public long key() {
            return this.currentEntry.key;
        }

        public V value() {
            return this.currentEntry.value;
        }
    }

    private static class ValueIterator<V> extends AbstractMapIterator<V> implements Iterator<V> {
        ValueIterator(LongHashMap<V> map) {
            super(map);
        }

        public V next() {
            makeNext();
            return this.currentEntry.value;
        }
    }

    protected long hashSaltValue() {
        return new Random().nextLong();
    }

    Entry<V>[] newElementArray(int s) {
        return new Entry[s];
    }

    public LongHashMap() {
        this(DEFAULT_SIZE);
    }

    public LongHashMap(int capacity) {
        this(capacity, 0.75f);
    }

    private static int calculateCapacity(int x) {
        if (x >= 1073741824) {
            return 1073741824;
        }
        if (x == 0) {
            return DEFAULT_SIZE;
        }
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        return (x | (x >> DEFAULT_SIZE)) + 1;
    }

    public LongHashMap(int capacity, float loadFactor) {
        this.modCount = 0;
        this.hashSalt = hashSaltValue();
        if (capacity < 0 || loadFactor <= 0.0f) {
            throw new IllegalArgumentException();
        }
        capacity = calculateCapacity(capacity);
        this.elementCount = 0;
        this.elementData = newElementArray(capacity);
        this.loadFactor = loadFactor;
        computeThreshold();
    }

    public void clear() {
        if (this.elementCount > 0) {
            this.elementCount = 0;
            Arrays.fill(this.elementData, null);
            this.modCount++;
        }
    }

    private void computeThreshold() {
        this.threshold = (int) (((float) this.elementData.length) * this.loadFactor);
    }

    public V get(long key) {
        Entry<V> m = getEntry(key);
        if (m != null) {
            return m.value;
        }
        return null;
    }

    final Entry<V> getEntry(long key) {
        int hash = longHash(this.hashSalt ^ key);
        return findNonNullKeyEntry(key, hash & (this.elementData.length - 1), hash);
    }

    final Entry<V> findNonNullKeyEntry(long key, int index, int keyHash) {
        Entry<V> m = this.elementData[index];
        while (m != null && (m.origKeyHash != keyHash || key != m.key)) {
            m = m.next;
        }
        return m;
    }

    public boolean isEmpty() {
        return this.elementCount == 0;
    }

    public V put(long key, V value) {
        int hash = longHash(this.hashSalt ^ key);
        int index = hash & (this.elementData.length - 1);
        Entry<V> entry = findNonNullKeyEntry(key, index, hash);
        if (entry == null) {
            this.modCount++;
            entry = createHashedEntry(key, index, hash);
            int i = this.elementCount + 1;
            this.elementCount = i;
            if (i > this.threshold) {
                rehash();
            }
        }
        V result = entry.value;
        entry.value = value;
        return result;
    }

    Entry<V> createHashedEntry(long key, int index, int hash) {
        Entry<V> entry = new Entry(key, hash);
        entry.next = this.elementData[index];
        this.elementData[index] = entry;
        return entry;
    }

    void rehash(int capacity) {
        int length = calculateCapacity(capacity == 0 ? 1 : capacity << 1);
        Entry<V>[] newData = newElementArray(length);
        for (int i = 0; i < this.elementData.length; i++) {
            Entry<V> entry = this.elementData[i];
            this.elementData[i] = null;
            while (entry != null) {
                int index = entry.origKeyHash & (length - 1);
                Entry<V> next = entry.next;
                entry.next = newData[index];
                newData[index] = entry;
                entry = next;
            }
        }
        this.elementData = newData;
        computeThreshold();
    }

    void rehash() {
        rehash(this.elementData.length);
    }

    public V remove(long key) {
        Entry<V> entry = removeEntry(key);
        if (entry != null) {
            return entry.value;
        }
        return null;
    }

    final Entry<V> removeEntry(long key) {
        Entry<V> last = null;
        int hash = longHash(this.hashSalt ^ key);
        int index = hash & (this.elementData.length - 1);
        Entry<V> entry = this.elementData[index];
        while (entry != null && (entry.origKeyHash != hash || key != entry.key)) {
            last = entry;
            entry = entry.next;
        }
        if (entry == null) {
            return null;
        }
        if (last == null) {
            this.elementData[index] = entry.next;
        } else {
            last.next = entry.next;
        }
        this.modCount++;
        this.elementCount--;
        return entry;
    }

    public int size() {
        return this.elementCount;
    }

    public Iterator<V> valuesIterator() {
        return new ValueIterator(this);
    }

    public LongMapIterator<V> longMapIterator() {
        return new EntryIterator(this);
    }

    public static int longHash(long key) {
        int h = (int) ((key >>> 32) ^ key);
        h ^= (h >>> 20) ^ (h >>> 12);
        return ((h >>> 7) ^ h) ^ (h >>> 4);
    }

    public static int intHash(int h) {
        h ^= (h >>> 20) ^ (h >>> 12);
        return ((h >>> 7) ^ h) ^ (h >>> 4);
    }
}
