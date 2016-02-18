package org.mapdb;

import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import org.mapdb.LongMap.LongMapIterator;

public class LongConcurrentLRUMap<V> extends LongMap<V> {
    protected final int acceptableWaterMark;
    protected final AtomicLong accessCounter;
    protected final AtomicLong evictionCounter;
    protected boolean isCleaning;
    protected final int lowerWaterMark;
    protected final LongConcurrentHashMap<CacheEntry<V>> map;
    protected final ReentrantLock markAndSweepLock;
    protected final AtomicLong missCounter;
    protected long oldestEntry;
    protected final AtomicLong putCounter;
    protected final AtomicInteger size;
    protected final int upperWaterMark;

    /* renamed from: org.mapdb.LongConcurrentLRUMap.1 */
    class AnonymousClass1 implements Iterator<V> {
        final /* synthetic */ Iterator val$iter;

        AnonymousClass1(Iterator it) {
            this.val$iter = it;
        }

        public boolean hasNext() {
            return this.val$iter.hasNext();
        }

        public V next() {
            return ((CacheEntry) this.val$iter.next()).value;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class CacheEntry<V> implements Comparable<CacheEntry<V>> {
        final long key;
        volatile long lastAccessed;
        long lastAccessedCopy;
        final V value;

        public CacheEntry(long key, V value, long lastAccessed) {
            this.lastAccessed = 0;
            this.lastAccessedCopy = 0;
            this.key = key;
            this.value = value;
            this.lastAccessed = lastAccessed;
        }

        public int compareTo(CacheEntry<V> that) {
            if (this.lastAccessedCopy == that.lastAccessedCopy) {
                return 0;
            }
            return this.lastAccessedCopy < that.lastAccessedCopy ? 1 : -1;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public boolean equals(Object obj) {
            return this.value.equals(obj);
        }

        public String toString() {
            return "key: " + this.key + " value: " + this.value + " lastAccessed:" + this.lastAccessed;
        }
    }

    private static abstract class PriorityQueue<T> {
        private final T[] heap;
        private final int maxSize;
        private int size;

        protected abstract boolean lessThan(T t, T t2);

        public PriorityQueue(int maxSize) {
            this(maxSize, true);
        }

        public PriorityQueue(int maxSize, boolean prepopulate) {
            int heapSize;
            this.size = 0;
            if (maxSize == 0) {
                heapSize = 2;
            } else if (maxSize == AdvancedShareActionProvider.WEIGHT_MAX) {
                heapSize = AdvancedShareActionProvider.WEIGHT_MAX;
            } else {
                heapSize = maxSize + 1;
            }
            this.heap = new Object[heapSize];
            this.maxSize = maxSize;
            if (prepopulate) {
                T sentinel = getSentinelObject();
                if (sentinel != null) {
                    this.heap[1] = sentinel;
                    for (int i = 2; i < this.heap.length; i++) {
                        this.heap[i] = getSentinelObject();
                    }
                    this.size = maxSize;
                }
            }
        }

        protected T getSentinelObject() {
            return null;
        }

        public final T add(T element) {
            this.size++;
            this.heap[this.size] = element;
            upHeap();
            return this.heap[1];
        }

        public T insertWithOverflow(T element) {
            if (this.size < this.maxSize) {
                add(element);
                return null;
            } else if (this.size <= 0 || lessThan(element, this.heap[1])) {
                return element;
            } else {
                T ret = this.heap[1];
                this.heap[1] = element;
                updateTop();
                return ret;
            }
        }

        public final T top() {
            return this.heap[1];
        }

        public final T pop() {
            if (this.size <= 0) {
                return null;
            }
            T result = this.heap[1];
            this.heap[1] = this.heap[this.size];
            this.heap[this.size] = null;
            this.size--;
            downHeap();
            return result;
        }

        public final T updateTop() {
            downHeap();
            return this.heap[1];
        }

        public final int size() {
            return this.size;
        }

        public final void clear() {
            for (int i = 0; i <= this.size; i++) {
                this.heap[i] = null;
            }
            this.size = 0;
        }

        private void upHeap() {
            int i = this.size;
            T node = this.heap[i];
            int j = i >>> 1;
            while (j > 0 && lessThan(node, this.heap[j])) {
                this.heap[i] = this.heap[j];
                i = j;
                j >>>= 1;
            }
            this.heap[i] = node;
        }

        private void downHeap() {
            int i = 1;
            T node = this.heap[1];
            int j = 1 << 1;
            int k = j + 1;
            if (k <= this.size && lessThan(this.heap[k], this.heap[j])) {
                j = k;
            }
            while (j <= this.size && lessThan(this.heap[j], node)) {
                this.heap[i] = this.heap[j];
                i = j;
                j = i << 1;
                k = j + 1;
                if (k <= this.size && lessThan(this.heap[k], this.heap[j])) {
                    j = k;
                }
            }
            this.heap[i] = node;
        }

        protected final T[] getHeapArray() {
            return this.heap;
        }
    }

    /* renamed from: org.mapdb.LongConcurrentLRUMap.2 */
    class AnonymousClass2 implements LongMapIterator<V> {
        final /* synthetic */ LongMapIterator val$iter;

        AnonymousClass2(LongMapIterator longMapIterator) {
            this.val$iter = longMapIterator;
        }

        public boolean moveToNext() {
            return this.val$iter.moveToNext();
        }

        public long key() {
            return this.val$iter.key();
        }

        public V value() {
            return ((CacheEntry) this.val$iter.value()).value;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class PQueue<V> extends PriorityQueue<CacheEntry<V>> {
        final Object[] heap;
        int myMaxSize;

        PQueue(int maxSz) {
            super(maxSz);
            this.heap = getHeapArray();
            this.myMaxSize = maxSz;
        }

        Iterable<CacheEntry<V>> getValues() {
            return Collections.unmodifiableCollection(Arrays.asList(this.heap));
        }

        protected boolean lessThan(CacheEntry<V> a, CacheEntry<V> b) {
            return b.lastAccessedCopy < a.lastAccessedCopy;
        }

        public CacheEntry<V> myInsertWithOverflow(CacheEntry<V> element) {
            if (size() < this.myMaxSize) {
                add(element);
                return null;
            } else if (size() <= 0 || lessThan((CacheEntry) element, (CacheEntry) this.heap[1])) {
                return element;
            } else {
                CacheEntry<V> ret = this.heap[1];
                this.heap[1] = element;
                updateTop();
                return ret;
            }
        }
    }

    public LongConcurrentLRUMap(int upperWaterMark, int lowerWaterMark, int acceptableWatermark, int initialSize) {
        this.markAndSweepLock = new ReentrantLock(true);
        this.isCleaning = false;
        this.oldestEntry = 0;
        this.accessCounter = new AtomicLong(0);
        this.putCounter = new AtomicLong(0);
        this.missCounter = new AtomicLong();
        this.evictionCounter = new AtomicLong();
        this.size = new AtomicInteger();
        if (upperWaterMark < 1) {
            throw new IllegalArgumentException("upperWaterMark must be > 0");
        } else if (lowerWaterMark >= upperWaterMark) {
            throw new IllegalArgumentException("lowerWaterMark must be  < upperWaterMark");
        } else {
            this.map = new LongConcurrentHashMap(initialSize);
            this.upperWaterMark = upperWaterMark;
            this.lowerWaterMark = lowerWaterMark;
            this.acceptableWaterMark = acceptableWatermark;
        }
    }

    public LongConcurrentLRUMap(int size, int lowerWatermark) {
        this(size, lowerWatermark, (int) Math.floor((double) ((lowerWatermark + size) / 2)), (int) Math.ceil(0.75d * ((double) size)));
    }

    public V get(long key) {
        CacheEntry<V> e = (CacheEntry) this.map.get(key);
        if (e == null) {
            this.missCounter.incrementAndGet();
            return null;
        }
        e.lastAccessed = this.accessCounter.incrementAndGet();
        return e.value;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public V remove(long key) {
        CacheEntry<V> cacheEntry = (CacheEntry) this.map.remove(key);
        if (cacheEntry == null) {
            return null;
        }
        this.size.decrementAndGet();
        return cacheEntry.value;
    }

    public V put(long key, V val) {
        if (val == null) {
            return null;
        }
        int currentSize;
        V v;
        CacheEntry<V> oldCacheEntry = (CacheEntry) this.map.put(key, new CacheEntry(key, val, this.accessCounter.incrementAndGet()));
        if (oldCacheEntry == null) {
            currentSize = this.size.incrementAndGet();
        } else {
            currentSize = this.size.get();
        }
        this.putCounter.incrementAndGet();
        if (currentSize > this.upperWaterMark && !this.isCleaning) {
            markAndSweep();
        }
        if (oldCacheEntry == null) {
            v = null;
        } else {
            v = oldCacheEntry.value;
        }
        return v;
    }

    private void markAndSweep() {
        if (this.markAndSweepLock.tryLock()) {
            try {
                CacheEntry<V> ce;
                long thisEntry;
                int eSize;
                int i;
                long oldestEntry = this.oldestEntry;
                this.isCleaning = true;
                this.oldestEntry = oldestEntry;
                long timeCurrent = this.accessCounter.get();
                int sz = this.size.get();
                int numRemoved = 0;
                int numKept = 0;
                long newestEntry = timeCurrent;
                long newNewestEntry = -1;
                long newOldestEntry = Long.MAX_VALUE;
                int wantToKeep = this.lowerWaterMark;
                int wantToRemove = sz - this.lowerWaterMark;
                CacheEntry[] eset = new CacheEntry[sz];
                Iterator<CacheEntry<V>> iter = this.map.valuesIterator();
                int eSize2 = 0;
                while (iter.hasNext()) {
                    ce = (CacheEntry) iter.next();
                    ce.lastAccessedCopy = ce.lastAccessed;
                    thisEntry = ce.lastAccessedCopy;
                    if (thisEntry > newestEntry - ((long) wantToKeep)) {
                        numKept++;
                        newOldestEntry = Math.min(thisEntry, newOldestEntry);
                        eSize = eSize2;
                    } else {
                        if (thisEntry < ((long) wantToRemove) + oldestEntry) {
                            evictEntry(ce.key);
                            numRemoved++;
                            eSize = eSize2;
                        } else {
                            if (eSize2 < eset.length - 1) {
                                eSize = eSize2 + 1;
                                eset[eSize2] = ce;
                                newNewestEntry = Math.max(thisEntry, newNewestEntry);
                                newOldestEntry = Math.min(thisEntry, newOldestEntry);
                            } else {
                                eSize = eSize2;
                            }
                        }
                    }
                    eSize2 = eSize;
                }
                int numPasses = 1;
                eSize = eSize2;
                while (sz - numRemoved > this.acceptableWaterMark) {
                    numPasses--;
                    if (numPasses < 0) {
                        break;
                    }
                    if (newOldestEntry != Long.MAX_VALUE) {
                        oldestEntry = newOldestEntry;
                    }
                    newOldestEntry = Long.MAX_VALUE;
                    newestEntry = newNewestEntry;
                    newNewestEntry = -1;
                    wantToKeep = this.lowerWaterMark - numKept;
                    wantToRemove = (sz - this.lowerWaterMark) - numRemoved;
                    for (i = eSize - 1; i >= 0; i--) {
                        CacheEntry ce2 = eset[i];
                        thisEntry = ce2.lastAccessedCopy;
                        if (thisEntry > newestEntry - ((long) wantToKeep)) {
                            numKept++;
                            eset[i] = eset[eSize - 1];
                            eSize--;
                            newOldestEntry = Math.min(thisEntry, newOldestEntry);
                        } else {
                            if (thisEntry < ((long) wantToRemove) + oldestEntry) {
                                evictEntry(ce2.key);
                                numRemoved++;
                                eset[i] = eset[eSize - 1];
                                eSize--;
                            } else {
                                newNewestEntry = Math.max(thisEntry, newNewestEntry);
                                newOldestEntry = Math.min(thisEntry, newOldestEntry);
                            }
                        }
                    }
                }
                if (sz - numRemoved > this.acceptableWaterMark) {
                    if (newOldestEntry != Long.MAX_VALUE) {
                        oldestEntry = newOldestEntry;
                    }
                    newOldestEntry = Long.MAX_VALUE;
                    newestEntry = newNewestEntry;
                    wantToKeep = this.lowerWaterMark - numKept;
                    wantToRemove = (sz - this.lowerWaterMark) - numRemoved;
                    PQueue<V> pQueue = new PQueue(wantToRemove);
                    for (i = eSize - 1; i >= 0; i--) {
                        ce = eset[i];
                        thisEntry = ce.lastAccessedCopy;
                        if (thisEntry > newestEntry - ((long) wantToKeep)) {
                            numKept++;
                            newOldestEntry = Math.min(thisEntry, newOldestEntry);
                        } else {
                            if (thisEntry < ((long) wantToRemove) + oldestEntry) {
                                evictEntry(ce.key);
                                numRemoved++;
                            } else {
                                pQueue.myMaxSize = (sz - this.lowerWaterMark) - numRemoved;
                                while (pQueue.size() > pQueue.myMaxSize && pQueue.size() > 0) {
                                    newOldestEntry = Math.min(((CacheEntry) pQueue.pop()).lastAccessedCopy, newOldestEntry);
                                }
                                if (pQueue.myMaxSize <= 0) {
                                    break;
                                }
                                CacheEntry o = pQueue.myInsertWithOverflow(ce);
                                if (o != null) {
                                    newOldestEntry = Math.min(o.lastAccessedCopy, newOldestEntry);
                                }
                            }
                        }
                    }
                    for (CacheEntry<V> ce3 : pQueue.getValues()) {
                        if (ce3 != null) {
                            evictEntry(ce3.key);
                            numRemoved++;
                        }
                    }
                }
                if (newOldestEntry != Long.MAX_VALUE) {
                    oldestEntry = newOldestEntry;
                }
                this.oldestEntry = oldestEntry;
            } finally {
                this.isCleaning = false;
                this.markAndSweepLock.unlock();
            }
        }
    }

    private void evictEntry(long key) {
        CacheEntry<V> o = (CacheEntry) this.map.remove(key);
        if (o != null) {
            this.size.decrementAndGet();
            this.evictionCounter.incrementAndGet();
            evictedEntry(o.key, o.value);
        }
    }

    public int size() {
        return this.size.get();
    }

    public Iterator<V> valuesIterator() {
        return new AnonymousClass1(this.map.valuesIterator());
    }

    public LongMapIterator<V> longMapIterator() {
        return new AnonymousClass2(this.map.longMapIterator());
    }

    public void clear() {
        this.map.clear();
    }

    public LongMap<CacheEntry<V>> getMap() {
        return this.map;
    }

    protected void evictedEntry(long key, V v) {
    }
}
