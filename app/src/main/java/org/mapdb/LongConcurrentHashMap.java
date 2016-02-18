package org.mapdb;

import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import org.mapdb.LongMap.LongMapIterator;

public class LongConcurrentHashMap<V> extends LongMap<V> implements Serializable {
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final int RETRIES_BEFORE_LOCK = 2;
    private static final long serialVersionUID = 7249069246763182397L;
    protected final long hashSalt;
    final int segmentMask;
    final int segmentShift;
    final Segment<V>[] segments;

    static final class HashEntry<V> {
        final int hash;
        final long key;
        final HashEntry<V> next;
        volatile V value;

        HashEntry(long key, int hash, HashEntry<V> next, V value) {
            this.key = key;
            this.hash = hash;
            this.next = next;
            this.value = value;
        }

        static <V> HashEntry<V>[] newArray(int i) {
            return new HashEntry[i];
        }
    }

    abstract class HashIterator {
        HashEntry<V>[] currentTable;
        HashEntry<V> lastReturned;
        HashEntry<V> nextEntry;
        int nextSegmentIndex;
        int nextTableIndex;

        HashIterator() {
            this.nextSegmentIndex = LongConcurrentHashMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            advance();
        }

        final void advance() {
            HashEntry hashEntry;
            if (this.nextEntry != null) {
                hashEntry = this.nextEntry.next;
                this.nextEntry = hashEntry;
                if (hashEntry != null) {
                    return;
                }
            }
            while (this.nextTableIndex >= 0) {
                HashEntry[] hashEntryArr = this.currentTable;
                int i = this.nextTableIndex;
                this.nextTableIndex = i - 1;
                hashEntry = hashEntryArr[i];
                this.nextEntry = hashEntry;
                if (hashEntry != null) {
                    return;
                }
            }
            while (this.nextSegmentIndex >= 0) {
                Segment[] segmentArr = LongConcurrentHashMap.this.segments;
                i = this.nextSegmentIndex;
                this.nextSegmentIndex = i - 1;
                Segment<V> seg = segmentArr[i];
                if (seg.count != 0) {
                    this.currentTable = seg.table;
                    for (int j = this.currentTable.length - 1; j >= 0; j--) {
                        hashEntry = this.currentTable[j];
                        this.nextEntry = hashEntry;
                        if (hashEntry != null) {
                            this.nextTableIndex = j - 1;
                            return;
                        }
                    }
                    continue;
                }
            }
        }

        public boolean hasNext() {
            return this.nextEntry != null;
        }

        HashEntry<V> nextEntry() {
            if (this.nextEntry == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.nextEntry;
            advance();
            return this.lastReturned;
        }

        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            LongConcurrentHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
        }
    }

    static final class Segment<V> extends ReentrantLock implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        volatile transient int count;
        final float loadFactor;
        transient int modCount;
        volatile transient HashEntry<V>[] table;
        transient int threshold;

        Segment(int initialCapacity, float lf) {
            super(false);
            this.loadFactor = lf;
            setTable(HashEntry.newArray(initialCapacity));
        }

        static <V> Segment<V>[] newArray(int i) {
            return new Segment[i];
        }

        void setTable(HashEntry<V>[] newTable) {
            this.threshold = (int) (((float) newTable.length) * this.loadFactor);
            this.table = newTable;
        }

        HashEntry<V> getFirst(int hash) {
            HashEntry<V>[] tab = this.table;
            return tab[(tab.length - 1) & hash];
        }

        V readValueUnderLock(HashEntry<V> e) {
            lock();
            try {
                V v = e.value;
                return v;
            } finally {
                unlock();
            }
        }

        V get(long key, int hash) {
            if (this.count != 0) {
                HashEntry<V> e = getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key == e.key) {
                        V v = e.value;
                        if (v != null) {
                            return v;
                        }
                        return readValueUnderLock(e);
                    }
                    e = e.next;
                }
            }
            return null;
        }

        boolean containsKey(long key, int hash) {
            if (this.count != 0) {
                HashEntry<V> e = getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key == e.key) {
                        return true;
                    }
                    e = e.next;
                }
            }
            return false;
        }

        boolean containsValue(Object value) {
            if (this.count != 0) {
                for (HashEntry<V> e : this.table) {
                    for (HashEntry<V> e2 = arr$[i$]; e2 != null; e2 = e2.next) {
                        V v = e2.value;
                        if (v == null) {
                            v = readValueUnderLock(e2);
                        }
                        if (value.equals(v)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        boolean replace(long key, int hash, V oldValue, V newValue) {
            lock();
            try {
                HashEntry<V> e = getFirst(hash);
                while (e != null && (e.hash != hash || key != e.key)) {
                    e = e.next;
                }
                boolean replaced = false;
                if (e != null && oldValue.equals(e.value)) {
                    replaced = true;
                    e.value = newValue;
                }
                unlock();
                return replaced;
            } catch (Throwable th) {
                unlock();
            }
        }

        V replace(long key, int hash, V newValue) {
            lock();
            try {
                HashEntry<V> e = getFirst(hash);
                while (e != null && (e.hash != hash || key != e.key)) {
                    e = e.next;
                }
                V oldValue = null;
                if (e != null) {
                    oldValue = e.value;
                    e.value = newValue;
                }
                unlock();
                return oldValue;
            } catch (Throwable th) {
                unlock();
            }
        }

        V put(long key, int hash, V value, boolean onlyIfAbsent) {
            lock();
            try {
                V oldValue;
                int i = this.count;
                int c = i + 1;
                if (i > this.threshold) {
                    rehash();
                }
                HashEntry<V>[] tab = this.table;
                int index = hash & (tab.length - 1);
                HashEntry<V> first = tab[index];
                HashEntry<V> e = first;
                while (e != null && (e.hash != hash || key != e.key)) {
                    e = e.next;
                }
                if (e != null) {
                    oldValue = e.value;
                    if (!onlyIfAbsent) {
                        e.value = value;
                    }
                } else {
                    oldValue = null;
                    this.modCount++;
                    tab[index] = new HashEntry(key, hash, first, value);
                    this.count = c;
                }
                unlock();
                return oldValue;
            } catch (Throwable th) {
                unlock();
            }
        }

        void rehash() {
            HashEntry<V>[] oldTable = this.table;
            int oldCapacity = oldTable.length;
            if (oldCapacity < LongConcurrentHashMap.MAXIMUM_CAPACITY) {
                HashEntry<V>[] newTable = HashEntry.newArray(oldCapacity << 1);
                this.threshold = (int) (((float) newTable.length) * this.loadFactor);
                int sizeMask = newTable.length - 1;
                for (HashEntry<V> e : oldTable) {
                    if (e != null) {
                        HashEntry<V> next = e.next;
                        int idx = e.hash & sizeMask;
                        if (next == null) {
                            newTable[idx] = e;
                        } else {
                            int k;
                            HashEntry<V> lastRun = e;
                            int lastIdx = idx;
                            for (HashEntry<V> last = next; last != null; last = last.next) {
                                k = last.hash & sizeMask;
                                if (k != lastIdx) {
                                    lastIdx = k;
                                    lastRun = last;
                                }
                            }
                            newTable[lastIdx] = lastRun;
                            for (HashEntry<V> p = e; p != lastRun; p = p.next) {
                                k = p.hash & sizeMask;
                                newTable[k] = new HashEntry(p.key, p.hash, newTable[k], p.value);
                            }
                        }
                    }
                }
                this.table = newTable;
            }
        }

        V remove(long key, int hash, Object value) {
            lock();
            try {
                int c = this.count - 1;
                HashEntry<V>[] tab = this.table;
                int index = hash & (tab.length - 1);
                HashEntry<V> first = tab[index];
                HashEntry<V> e = first;
                while (e != null && (e.hash != hash || key != e.key)) {
                    e = e.next;
                }
                V oldValue = null;
                if (e != null) {
                    V v = e.value;
                    if (value == null || value.equals(v)) {
                        oldValue = v;
                        this.modCount++;
                        HashEntry<V> p = first;
                        HashEntry<V> newFirst = e.next;
                        while (p != e) {
                            HashEntry<V> newFirst2 = new HashEntry(p.key, p.hash, newFirst, p.value);
                            p = p.next;
                            newFirst = newFirst2;
                        }
                        tab[index] = newFirst;
                        this.count = c;
                    }
                }
                unlock();
                return oldValue;
            } catch (Throwable th) {
                unlock();
            }
        }

        void clear() {
            if (this.count != 0) {
                lock();
                try {
                    HashEntry<V>[] tab = this.table;
                    for (int i = 0; i < tab.length; i++) {
                        tab[i] = null;
                    }
                    this.modCount++;
                    this.count = 0;
                } finally {
                    unlock();
                }
            }
        }
    }

    final class KeyIterator extends HashIterator implements Iterator<Long> {
        KeyIterator() {
            super();
        }

        public Long next() {
            return Long.valueOf(super.nextEntry().key);
        }
    }

    final class MapIterator extends HashIterator implements LongMapIterator<V> {
        private long key;
        private V value;

        MapIterator() {
            super();
        }

        public boolean moveToNext() {
            if (!hasNext()) {
                return false;
            }
            HashEntry<V> next = nextEntry();
            this.key = next.key;
            this.value = next.value;
            return true;
        }

        public long key() {
            return this.key;
        }

        public V value() {
            return this.value;
        }
    }

    final class ValueIterator extends HashIterator implements Iterator<V> {
        ValueIterator() {
            super();
        }

        public V next() {
            return super.nextEntry().value;
        }
    }

    final Segment<V> segmentFor(int hash) {
        return this.segments[(hash >>> this.segmentShift) & this.segmentMask];
    }

    public LongConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        this.hashSalt = new Random().nextLong();
        if (loadFactor <= 0.0f || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (concurrencyLevel > MAX_SEGMENTS) {
            concurrencyLevel = MAX_SEGMENTS;
        }
        int sshift = 0;
        int ssize = 1;
        while (ssize < concurrencyLevel) {
            sshift++;
            ssize <<= 1;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        this.segments = Segment.newArray(ssize);
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        int c = initialCapacity / ssize;
        if (c * ssize < initialCapacity) {
            c++;
        }
        int cap = 1;
        while (cap < c) {
            cap <<= 1;
        }
        for (int i = 0; i < this.segments.length; i++) {
            this.segments[i] = new Segment(cap, loadFactor);
        }
    }

    public LongConcurrentHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_INITIAL_CAPACITY);
    }

    public LongConcurrentHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_INITIAL_CAPACITY);
    }

    public boolean isEmpty() {
        int i;
        Segment<V>[] segments = this.segments;
        int[] mc = new int[segments.length];
        int mcsum = 0;
        for (i = 0; i < segments.length; i++) {
            if (segments[i].count != 0) {
                return false;
            }
            int i2 = segments[i].modCount;
            mc[i] = i2;
            mcsum += i2;
        }
        if (mcsum != 0) {
            i = 0;
            while (i < segments.length) {
                if (segments[i].count != 0 || mc[i] != segments[i].modCount) {
                    return false;
                }
                i++;
            }
        }
        return true;
    }

    public int size() {
        Segment<V>[] segments = this.segments;
        long sum = 0;
        long check = 0;
        int[] mc = new int[segments.length];
        for (int k = 0; k < RETRIES_BEFORE_LOCK; k++) {
            int i;
            check = 0;
            sum = 0;
            int mcsum = 0;
            for (i = 0; i < segments.length; i++) {
                sum += (long) segments[i].count;
                int i2 = segments[i].modCount;
                mc[i] = i2;
                mcsum += i2;
            }
            if (mcsum != 0) {
                for (i = 0; i < segments.length; i++) {
                    check += (long) segments[i].count;
                    i2 = mc[i];
                    int i3 = segments[i].modCount;
                    if (i2 != r0) {
                        check = -1;
                        break;
                    }
                }
            }
            if (check == sum) {
                break;
            }
        }
        if (check != sum) {
            sum = 0;
            for (Segment<V> segment : segments) {
                segment.lock();
            }
            for (Segment<V> segment2 : segments) {
                sum += (long) segment2.count;
            }
            for (Segment<V> segment22 : segments) {
                segment22.unlock();
            }
        }
        if (sum > 2147483647L) {
            return AdvancedShareActionProvider.WEIGHT_MAX;
        }
        return (int) sum;
    }

    public Iterator<V> valuesIterator() {
        return new ValueIterator();
    }

    public LongMapIterator<V> longMapIterator() {
        return new MapIterator();
    }

    public V get(long key) {
        int hash = LongHashMap.longHash(this.hashSalt ^ key);
        return segmentFor(hash).get(key, hash);
    }

    public boolean containsKey(long key) {
        int hash = LongHashMap.longHash(this.hashSalt ^ key);
        return segmentFor(hash).containsKey(key, hash);
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Segment<V>[] segments = this.segments;
        int[] mc = new int[segments.length];
        for (int k = 0; k < RETRIES_BEFORE_LOCK; k++) {
            int i;
            int mcsum = 0;
            for (i = 0; i < segments.length; i++) {
                int i2 = segments[i].modCount;
                mc[i] = i2;
                mcsum += i2;
                if (segments[i].containsValue(value)) {
                    return true;
                }
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                for (i = 0; i < segments.length; i++) {
                    if (mc[i] != segments[i].modCount) {
                        cleanSweep = false;
                        break;
                    }
                }
            }
            if (cleanSweep) {
                return false;
            }
        }
        for (Segment<V> segment : segments) {
            segment.lock();
        }
        boolean found = false;
        try {
            for (Segment<V> segment2 : segments) {
                if (segment2.containsValue(value)) {
                    found = true;
                    break;
                }
            }
            for (Segment<V> segment22 : segments) {
                segment22.unlock();
            }
            return found;
        } catch (Throwable th) {
            for (Segment<V> segment222 : segments) {
                segment222.unlock();
            }
        }
    }

    public V put(long key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = LongHashMap.longHash(this.hashSalt ^ key);
        return segmentFor(hash).put(key, hash, value, false);
    }

    public V putIfAbsent(long key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = LongHashMap.longHash(this.hashSalt ^ key);
        return segmentFor(hash).put(key, hash, value, true);
    }

    public V remove(long key) {
        int hash = LongHashMap.longHash(this.hashSalt ^ key);
        return segmentFor(hash).remove(key, hash, null);
    }

    public boolean remove(long key, Object value) {
        int hash = LongHashMap.longHash(this.hashSalt ^ key);
        return (value == null || segmentFor(hash).remove(key, hash, value) == null) ? false : true;
    }

    public boolean replace(long key, V oldValue, V newValue) {
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        int hash = LongHashMap.longHash(this.hashSalt ^ key);
        return segmentFor(hash).replace(key, hash, oldValue, newValue);
    }

    public V replace(long key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = LongHashMap.longHash(this.hashSalt ^ key);
        return segmentFor(hash).replace(key, hash, value);
    }

    public void clear() {
        for (Segment<V> segment : this.segments) {
            segment.clear();
        }
    }
}
