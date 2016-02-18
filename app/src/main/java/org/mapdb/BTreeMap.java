package org.mapdb;

import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import io.realm.internal.Table;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.LockSupport;
import org.mapdb.Atomic.Long;
import org.mapdb.Bind.MapListener;
import org.mapdb.Bind.MapWithModificationListener;
import org.mapdb.Fun.Tuple2;
import org.mapdb.LongMap.LongMapIterator;

public class BTreeMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V>, MapWithModificationListener<K, V> {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected static final int B_TREE_NODE_DIR_C = 187;
    protected static final int B_TREE_NODE_DIR_L = 185;
    protected static final int B_TREE_NODE_DIR_LR = 184;
    protected static final int B_TREE_NODE_DIR_R = 186;
    protected static final int B_TREE_NODE_LEAF_C = 183;
    protected static final int B_TREE_NODE_LEAF_L = 181;
    protected static final int B_TREE_NODE_LEAF_LR = 180;
    protected static final int B_TREE_NODE_LEAF_R = 182;
    public static final Comparator COMPARABLE_COMPARATOR;
    protected static final Object EMPTY;
    protected final Comparator comparator;
    protected final Long counter;
    private final ConcurrentNavigableMap<K, V> descendingMap;
    protected final Engine engine;
    private final EntrySet entrySet;
    protected final boolean hasValues;
    protected final BTreeKeySerializer keySerializer;
    private final KeySet keySet;
    protected final List<Long> leftEdges;
    protected final int maxNodeSize;
    protected MapListener<K, V>[] modListeners;
    protected final Object modListenersLock;
    protected final LongConcurrentHashMap<Thread> nodeLocks;
    protected final Serializer<BNode> nodeSerializer;
    protected final int numberOfNodeMetas;
    protected final long rootRecidRef;
    protected final boolean valsOutsideNodes;
    protected final Serializer<V> valueSerializer;
    private final Values values;

    protected interface BNode {
        long[] child();

        Object highKey();

        boolean isLeaf();

        Object[] keys();

        long next();

        Object[] vals();
    }

    protected static class BTreeIterator {
        LeafNode currentLeaf;
        int currentPos;
        final Object hi;
        final boolean hiInclusive;
        Object lastReturnedKey;
        final BTreeMap m;

        BTreeIterator(BTreeMap m) {
            this.m = m;
            this.hi = null;
            this.hiInclusive = BTreeMap.$assertionsDisabled;
            pointToStart();
        }

        BTreeIterator(BTreeMap m, Object lo, boolean loInclusive, Object hi, boolean hiInclusive) {
            this.m = m;
            if (lo == null) {
                pointToStart();
            } else {
                int intValue;
                LeafNode leafNode;
                Tuple2<Integer, LeafNode> l = m.findLargerNode(lo, loInclusive);
                if (l != null) {
                    intValue = ((Integer) l.a).intValue();
                } else {
                    intValue = -1;
                }
                this.currentPos = intValue;
                if (l != null) {
                    leafNode = (LeafNode) l.b;
                } else {
                    leafNode = null;
                }
                this.currentLeaf = leafNode;
            }
            this.hi = hi;
            this.hiInclusive = hiInclusive;
            if (hi != null && this.currentLeaf != null) {
                int c = m.comparator.compare(this.currentLeaf.keys[this.currentPos], hi);
                if (c > 0 || (c == 0 && !hiInclusive)) {
                    this.currentLeaf = null;
                    this.currentPos = -1;
                }
            }
        }

        private void pointToStart() {
            BNode node = (BNode) this.m.engine.get(((Long) this.m.engine.get(this.m.rootRecidRef, Serializer.LONG)).longValue(), this.m.nodeSerializer);
            while (!node.isLeaf()) {
                node = (BNode) this.m.engine.get(node.child()[0], this.m.nodeSerializer);
            }
            this.currentLeaf = (LeafNode) node;
            this.currentPos = 1;
            while (this.currentLeaf.keys.length == 2) {
                if (this.currentLeaf.next == 0) {
                    this.currentLeaf = null;
                    return;
                }
                this.currentLeaf = (LeafNode) this.m.engine.get(this.currentLeaf.next, this.m.nodeSerializer);
            }
        }

        public boolean hasNext() {
            return this.currentLeaf != null ? true : BTreeMap.$assertionsDisabled;
        }

        public void remove() {
            if (this.lastReturnedKey == null) {
                throw new IllegalStateException();
            }
            this.m.remove(this.lastReturnedKey);
            this.lastReturnedKey = null;
        }

        protected void advance() {
            if (this.currentLeaf != null) {
                this.lastReturnedKey = this.currentLeaf.keys[this.currentPos];
                this.currentPos++;
                if (this.currentPos == this.currentLeaf.keys.length - 1) {
                    if (this.currentLeaf.next == 0) {
                        this.currentLeaf = null;
                        this.currentPos = -1;
                        return;
                    }
                    this.currentPos = 1;
                    this.currentLeaf = (LeafNode) this.m.engine.get(this.currentLeaf.next, this.m.nodeSerializer);
                    while (this.currentLeaf.keys.length == 2) {
                        if (this.currentLeaf.next == 0) {
                            this.currentLeaf = null;
                            this.currentPos = -1;
                            return;
                        }
                        this.currentLeaf = (LeafNode) this.m.engine.get(this.currentLeaf.next, this.m.nodeSerializer);
                    }
                }
                if (this.hi != null && this.currentLeaf != null) {
                    int c = this.m.comparator.compare(this.currentLeaf.keys[this.currentPos], this.hi);
                    if (c > 0 || (c == 0 && !this.hiInclusive)) {
                        this.currentLeaf = null;
                        this.currentPos = -1;
                    }
                }
            }
        }
    }

    protected static class DescendingMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V> {
        protected final K hi;
        protected final boolean hiInclusive;
        protected final K lo;
        protected final boolean loInclusive;
        protected final BTreeMap<K, V> m;

        abstract class Iter<E> implements Iterator<E> {
            Entry<K, V> current;
            Entry<K, V> last;

            Iter() {
                this.current = DescendingMap.this.firstEntry();
                this.last = null;
            }

            public boolean hasNext() {
                return this.current != null ? true : BTreeMap.$assertionsDisabled;
            }

            public void advance() {
                if (this.current == null) {
                    throw new NoSuchElementException();
                }
                this.last = this.current;
                this.current = DescendingMap.this.higherEntry(this.current.getKey());
            }

            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                DescendingMap.this.remove(this.last.getKey());
                this.last = null;
            }
        }

        public DescendingMap(BTreeMap<K, V> m, K lo, boolean loInclusive, K hi, boolean hiInclusive) {
            this.m = m;
            this.lo = lo;
            this.loInclusive = loInclusive;
            this.hi = hi;
            this.hiInclusive = hiInclusive;
            if (lo != null && hi != null && m.comparator.compare(lo, hi) > 0) {
                throw new IllegalArgumentException();
            }
        }

        public boolean containsKey(Object key) {
            if (key == null) {
                throw new NullPointerException();
            }
            K k = key;
            return (inBounds(k) && this.m.containsKey(k)) ? true : BTreeMap.$assertionsDisabled;
        }

        public V get(Object key) {
            if (key == null) {
                throw new NullPointerException();
            }
            K k = key;
            return !inBounds(k) ? null : this.m.get(k);
        }

        public V put(K key, V value) {
            checkKeyBounds(key);
            return this.m.put(key, value);
        }

        public V remove(Object key) {
            K k = key;
            return !inBounds(k) ? null : this.m.remove(k);
        }

        public int size() {
            Iterator<K> i = keyIterator();
            int counter = 0;
            while (i.hasNext()) {
                counter++;
                i.next();
            }
            return counter;
        }

        public boolean isEmpty() {
            return !keyIterator().hasNext() ? true : BTreeMap.$assertionsDisabled;
        }

        public boolean containsValue(Object value) {
            if (value == null) {
                throw new NullPointerException();
            }
            Iterator<V> i = valueIterator();
            while (i.hasNext()) {
                if (value.equals(i.next())) {
                    return true;
                }
            }
            return BTreeMap.$assertionsDisabled;
        }

        public void clear() {
            Iterator<K> i = keyIterator();
            while (i.hasNext()) {
                i.next();
                i.remove();
            }
        }

        public V putIfAbsent(K key, V value) {
            checkKeyBounds(key);
            return this.m.putIfAbsent(key, value);
        }

        public boolean remove(Object key, Object value) {
            K k = key;
            return (inBounds(k) && this.m.remove(k, value)) ? true : BTreeMap.$assertionsDisabled;
        }

        public boolean replace(K key, V oldValue, V newValue) {
            checkKeyBounds(key);
            return this.m.replace(key, oldValue, newValue);
        }

        public V replace(K key, V value) {
            checkKeyBounds(key);
            return this.m.replace(key, value);
        }

        public Comparator<? super K> comparator() {
            return this.m.comparator();
        }

        public Entry<K, V> higherEntry(K key) {
            if (key == null) {
                throw new NullPointerException();
            } else if (tooLow(key)) {
                return null;
            } else {
                if (tooHigh(key)) {
                    return firstEntry();
                }
                Entry<K, V> r = this.m.lowerEntry(key);
                if (r == null || tooLow(r.getKey())) {
                    r = null;
                }
                return r;
            }
        }

        public K lowerKey(K key) {
            Entry<K, V> n = lowerEntry(key);
            return n == null ? null : n.getKey();
        }

        public Entry<K, V> ceilingEntry(K key) {
            if (key == null) {
                throw new NullPointerException();
            } else if (tooLow(key)) {
                return null;
            } else {
                if (tooHigh(key)) {
                    return firstEntry();
                }
                Entry<K, V> ret = this.m.floorEntry(key);
                if (ret == null || !tooLow(ret.getKey())) {
                    return ret;
                }
                return null;
            }
        }

        public K floorKey(K key) {
            Entry<K, V> n = floorEntry(key);
            return n == null ? null : n.getKey();
        }

        public Entry<K, V> floorEntry(K key) {
            if (key == null) {
                throw new NullPointerException();
            } else if (tooHigh(key)) {
                return null;
            } else {
                if (tooLow(key)) {
                    return lastEntry();
                }
                Entry<K, V> ret = this.m.ceilingEntry(key);
                if (ret == null || !tooHigh(ret.getKey())) {
                    return ret;
                }
                return null;
            }
        }

        public K ceilingKey(K key) {
            Entry<K, V> k = ceilingEntry(key);
            return k != null ? k.getKey() : null;
        }

        public Entry<K, V> lowerEntry(K key) {
            Entry<K, V> r = this.m.higherEntry(key);
            return (r == null || !inBounds(r.getKey())) ? null : r;
        }

        public K higherKey(K key) {
            Entry<K, V> k = higherEntry(key);
            return k != null ? k.getKey() : null;
        }

        public K firstKey() {
            Entry<K, V> e = firstEntry();
            if (e != null) {
                return e.getKey();
            }
            throw new NoSuchElementException();
        }

        public K lastKey() {
            Entry<K, V> e = lastEntry();
            if (e != null) {
                return e.getKey();
            }
            throw new NoSuchElementException();
        }

        public Entry<K, V> lastEntry() {
            Entry<K, V> k = this.lo == null ? this.m.firstEntry() : this.m.findLarger(this.lo, this.loInclusive);
            return (k == null || !inBounds(k.getKey())) ? null : k;
        }

        public Entry<K, V> firstEntry() {
            Entry<K, V> k = this.hi == null ? this.m.lastEntry() : this.m.findSmaller(this.hi, this.hiInclusive);
            return (k == null || !inBounds(k.getKey())) ? null : k;
        }

        public Entry<K, V> pollFirstEntry() {
            Entry<K, V> e;
            do {
                e = firstEntry();
                if (e == null) {
                    break;
                }
            } while (!remove(e.getKey(), e.getValue()));
            return e;
        }

        public Entry<K, V> pollLastEntry() {
            Entry<K, V> e;
            do {
                e = lastEntry();
                if (e == null) {
                    break;
                }
            } while (!remove(e.getKey(), e.getValue()));
            return e;
        }

        private DescendingMap<K, V> newSubMap(K toKey, boolean toInclusive, K fromKey, boolean fromInclusive) {
            int c;
            if (this.lo != null) {
                if (fromKey == null) {
                    fromKey = this.lo;
                    fromInclusive = this.loInclusive;
                } else {
                    c = this.m.comparator.compare(fromKey, this.lo);
                    if (c < 0 || (c == 0 && !this.loInclusive && fromInclusive)) {
                        throw new IllegalArgumentException("key out of range");
                    }
                }
            }
            if (this.hi != null) {
                if (toKey == null) {
                    toKey = this.hi;
                    toInclusive = this.hiInclusive;
                } else {
                    c = this.m.comparator.compare(toKey, this.hi);
                    if (c > 0 || (c == 0 && !this.hiInclusive && toInclusive)) {
                        throw new IllegalArgumentException("key out of range");
                    }
                }
            }
            return new DescendingMap(this.m, fromKey, fromInclusive, toKey, toInclusive);
        }

        public DescendingMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            if (fromKey != null && toKey != null) {
                return newSubMap(fromKey, fromInclusive, toKey, toInclusive);
            }
            throw new NullPointerException();
        }

        public DescendingMap<K, V> headMap(K toKey, boolean inclusive) {
            if (toKey != null) {
                return newSubMap(null, BTreeMap.$assertionsDisabled, toKey, inclusive);
            }
            throw new NullPointerException();
        }

        public DescendingMap<K, V> tailMap(K fromKey, boolean inclusive) {
            if (fromKey != null) {
                return newSubMap(fromKey, inclusive, null, BTreeMap.$assertionsDisabled);
            }
            throw new NullPointerException();
        }

        public DescendingMap<K, V> subMap(K fromKey, K toKey) {
            return subMap((Object) fromKey, true, (Object) toKey, (boolean) BTreeMap.$assertionsDisabled);
        }

        public DescendingMap<K, V> headMap(K toKey) {
            return headMap((Object) toKey, (boolean) BTreeMap.$assertionsDisabled);
        }

        public DescendingMap<K, V> tailMap(K fromKey) {
            return tailMap((Object) fromKey, true);
        }

        public ConcurrentNavigableMap<K, V> descendingMap() {
            if (this.lo == null && this.hi == null) {
                return this.m;
            }
            return this.m.subMap(this.lo, this.loInclusive, this.hi, this.hiInclusive);
        }

        public NavigableSet<K> navigableKeySet() {
            return new KeySet(this, this.m.hasValues);
        }

        private boolean tooLow(K key) {
            if (this.lo != null) {
                int c = this.m.comparator.compare(key, this.lo);
                if (c < 0 || (c == 0 && !this.loInclusive)) {
                    return true;
                }
            }
            return BTreeMap.$assertionsDisabled;
        }

        private boolean tooHigh(K key) {
            if (this.hi != null) {
                int c = this.m.comparator.compare(key, this.hi);
                if (c > 0 || (c == 0 && !this.hiInclusive)) {
                    return true;
                }
            }
            return BTreeMap.$assertionsDisabled;
        }

        private boolean inBounds(K key) {
            return (tooLow(key) || tooHigh(key)) ? BTreeMap.$assertionsDisabled : true;
        }

        private void checkKeyBounds(K key) throws IllegalArgumentException {
            if (key == null) {
                throw new NullPointerException();
            } else if (!inBounds(key)) {
                throw new IllegalArgumentException("key out of range");
            }
        }

        public NavigableSet<K> keySet() {
            return new KeySet(this, this.m.hasValues);
        }

        public NavigableSet<K> descendingKeySet() {
            return new KeySet(descendingMap(), this.m.hasValues);
        }

        public Set<Entry<K, V>> entrySet() {
            return new EntrySet(this);
        }

        Iterator<K> keyIterator() {
            return new Iter<K>() {
                public K next() {
                    advance();
                    return this.last.getKey();
                }
            };
        }

        Iterator<V> valueIterator() {
            return new Iter<V>() {
                public V next() {
                    advance();
                    return this.last.getValue();
                }
            };
        }

        Iterator<Entry<K, V>> entryIterator() {
            return new Iter<Entry<K, V>>() {
                public Entry<K, V> next() {
                    advance();
                    return this.last;
                }
            };
        }
    }

    static final class EntrySet<K1, V1> extends AbstractSet<Entry<K1, V1>> {
        private final ConcurrentNavigableMap<K1, V1> m;

        EntrySet(ConcurrentNavigableMap<K1, V1> map) {
            this.m = map;
        }

        public Iterator<Entry<K1, V1>> iterator() {
            if (this.m instanceof BTreeMap) {
                return ((BTreeMap) this.m).entryIterator();
            }
            if (this.m instanceof SubMap) {
                return ((SubMap) this.m).entryIterator();
            }
            return ((DescendingMap) this.m).entryIterator();
        }

        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return BTreeMap.$assertionsDisabled;
            }
            Entry<K1, V1> e = (Entry) o;
            K1 key = e.getKey();
            if (key == null) {
                return BTreeMap.$assertionsDisabled;
            }
            V1 v = this.m.get(key);
            if (v == null || !v.equals(e.getValue())) {
                return BTreeMap.$assertionsDisabled;
            }
            return true;
        }

        public boolean remove(Object o) {
            if (!(o instanceof Entry)) {
                return BTreeMap.$assertionsDisabled;
            }
            Entry<K1, V1> e = (Entry) o;
            K1 key = e.getKey();
            if (key != null) {
                return this.m.remove(key, e.getValue());
            }
            return BTreeMap.$assertionsDisabled;
        }

        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        public int size() {
            return this.m.size();
        }

        public void clear() {
            this.m.clear();
        }

        public boolean equals(Object o) {
            boolean z = true;
            if (o == this) {
                return true;
            }
            if (!(o instanceof Set)) {
                return BTreeMap.$assertionsDisabled;
            }
            Collection<?> c = (Collection) o;
            try {
                if (!(containsAll(c) && c.containsAll(this))) {
                    z = BTreeMap.$assertionsDisabled;
                }
                return z;
            } catch (ClassCastException e) {
                return BTreeMap.$assertionsDisabled;
            } catch (NullPointerException e2) {
                return BTreeMap.$assertionsDisabled;
            }
        }

        public Object[] toArray() {
            return BTreeMap.toList(this).toArray();
        }

        public <T> T[] toArray(T[] a) {
            return BTreeMap.toList(this).toArray(a);
        }
    }

    static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final boolean hasValues;
        protected final ConcurrentNavigableMap<E, Object> m;

        KeySet(ConcurrentNavigableMap<E, Object> map, boolean hasValues) {
            this.m = map;
            this.hasValues = hasValues;
        }

        public int size() {
            return this.m.size();
        }

        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        public boolean contains(Object o) {
            return this.m.containsKey(o);
        }

        public boolean remove(Object o) {
            return this.m.remove(o) != null ? true : BTreeMap.$assertionsDisabled;
        }

        public void clear() {
            this.m.clear();
        }

        public E lower(E e) {
            return this.m.lowerKey(e);
        }

        public E floor(E e) {
            return this.m.floorKey(e);
        }

        public E ceiling(E e) {
            return this.m.ceilingKey(e);
        }

        public E higher(E e) {
            return this.m.higherKey(e);
        }

        public Comparator<? super E> comparator() {
            return this.m.comparator();
        }

        public E first() {
            return this.m.firstKey();
        }

        public E last() {
            return this.m.lastKey();
        }

        public E pollFirst() {
            Entry<E, Object> e = this.m.pollFirstEntry();
            return e == null ? null : e.getKey();
        }

        public E pollLast() {
            Entry<E, Object> e = this.m.pollLastEntry();
            return e == null ? null : e.getKey();
        }

        public Iterator<E> iterator() {
            if (this.m instanceof BTreeMap) {
                return ((BTreeMap) this.m).keyIterator();
            }
            if (this.m instanceof SubMap) {
                return ((SubMap) this.m).keyIterator();
            }
            return ((DescendingMap) this.m).keyIterator();
        }

        public boolean equals(Object o) {
            boolean z = true;
            if (o == this) {
                return true;
            }
            if (!(o instanceof Set)) {
                return BTreeMap.$assertionsDisabled;
            }
            Collection<?> c = (Collection) o;
            try {
                if (!(containsAll(c) && c.containsAll(this))) {
                    z = BTreeMap.$assertionsDisabled;
                }
                return z;
            } catch (ClassCastException e) {
                return BTreeMap.$assertionsDisabled;
            } catch (NullPointerException e2) {
                return BTreeMap.$assertionsDisabled;
            }
        }

        public Object[] toArray() {
            return BTreeMap.toList(this).toArray();
        }

        public <T> T[] toArray(T[] a) {
            return BTreeMap.toList(this).toArray(a);
        }

        public Iterator<E> descendingIterator() {
            return descendingSet().iterator();
        }

        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return new KeySet(this.m.subMap(fromElement, fromInclusive, toElement, toInclusive), this.hasValues);
        }

        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return new KeySet(this.m.headMap(toElement, inclusive), this.hasValues);
        }

        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return new KeySet(this.m.tailMap(fromElement, inclusive), this.hasValues);
        }

        public NavigableSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, BTreeMap.$assertionsDisabled);
        }

        public NavigableSet<E> headSet(E toElement) {
            return headSet(toElement, BTreeMap.$assertionsDisabled);
        }

        public NavigableSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }

        public NavigableSet<E> descendingSet() {
            return new KeySet(this.m.descendingMap(), this.hasValues);
        }

        public boolean add(E k) {
            if (!this.hasValues) {
                return this.m.put(k, BTreeMap.EMPTY) == null ? true : BTreeMap.$assertionsDisabled;
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    protected static class SubMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V> {
        protected final K hi;
        protected final boolean hiInclusive;
        protected final K lo;
        protected final boolean loInclusive;
        protected final BTreeMap<K, V> m;

        public SubMap(BTreeMap<K, V> m, K lo, boolean loInclusive, K hi, boolean hiInclusive) {
            this.m = m;
            this.lo = lo;
            this.loInclusive = loInclusive;
            this.hi = hi;
            this.hiInclusive = hiInclusive;
            if (lo != null && hi != null && m.comparator.compare(lo, hi) > 0) {
                throw new IllegalArgumentException();
            }
        }

        public boolean containsKey(Object key) {
            if (key == null) {
                throw new NullPointerException();
            }
            K k = key;
            return (inBounds(k) && this.m.containsKey(k)) ? true : BTreeMap.$assertionsDisabled;
        }

        public V get(Object key) {
            if (key == null) {
                throw new NullPointerException();
            }
            K k = key;
            return !inBounds(k) ? null : this.m.get(k);
        }

        public V put(K key, V value) {
            checkKeyBounds(key);
            return this.m.put(key, value);
        }

        public V remove(Object key) {
            K k = key;
            return !inBounds(k) ? null : this.m.remove(k);
        }

        public int size() {
            Iterator<K> i = keyIterator();
            int counter = 0;
            while (i.hasNext()) {
                counter++;
                i.next();
            }
            return counter;
        }

        public boolean isEmpty() {
            return !keyIterator().hasNext() ? true : BTreeMap.$assertionsDisabled;
        }

        public boolean containsValue(Object value) {
            if (value == null) {
                throw new NullPointerException();
            }
            Iterator<V> i = valueIterator();
            while (i.hasNext()) {
                if (value.equals(i.next())) {
                    return true;
                }
            }
            return BTreeMap.$assertionsDisabled;
        }

        public void clear() {
            Iterator<K> i = keyIterator();
            while (i.hasNext()) {
                i.next();
                i.remove();
            }
        }

        public V putIfAbsent(K key, V value) {
            checkKeyBounds(key);
            return this.m.putIfAbsent(key, value);
        }

        public boolean remove(Object key, Object value) {
            K k = key;
            return (inBounds(k) && this.m.remove(k, value)) ? true : BTreeMap.$assertionsDisabled;
        }

        public boolean replace(K key, V oldValue, V newValue) {
            checkKeyBounds(key);
            return this.m.replace(key, oldValue, newValue);
        }

        public V replace(K key, V value) {
            checkKeyBounds(key);
            return this.m.replace(key, value);
        }

        public Comparator<? super K> comparator() {
            return this.m.comparator();
        }

        public Entry<K, V> lowerEntry(K key) {
            if (key == null) {
                throw new NullPointerException();
            } else if (tooLow(key)) {
                return null;
            } else {
                if (tooHigh(key)) {
                    return lastEntry();
                }
                Entry<K, V> r = this.m.lowerEntry(key);
                if (r == null || tooLow(r.getKey())) {
                    r = null;
                }
                return r;
            }
        }

        public K lowerKey(K key) {
            Entry<K, V> n = lowerEntry(key);
            return n == null ? null : n.getKey();
        }

        public Entry<K, V> floorEntry(K key) {
            if (key == null) {
                throw new NullPointerException();
            } else if (tooLow(key)) {
                return null;
            } else {
                if (tooHigh(key)) {
                    return lastEntry();
                }
                Entry<K, V> ret = this.m.floorEntry(key);
                if (ret == null || !tooLow(ret.getKey())) {
                    return ret;
                }
                return null;
            }
        }

        public K floorKey(K key) {
            Entry<K, V> n = floorEntry(key);
            return n == null ? null : n.getKey();
        }

        public Entry<K, V> ceilingEntry(K key) {
            if (key == null) {
                throw new NullPointerException();
            } else if (tooHigh(key)) {
                return null;
            } else {
                if (tooLow(key)) {
                    return firstEntry();
                }
                Entry<K, V> ret = this.m.ceilingEntry(key);
                if (ret == null || !tooHigh(ret.getKey())) {
                    return ret;
                }
                return null;
            }
        }

        public K ceilingKey(K key) {
            Entry<K, V> k = ceilingEntry(key);
            return k != null ? k.getKey() : null;
        }

        public Entry<K, V> higherEntry(K key) {
            Entry<K, V> r = this.m.higherEntry(key);
            return (r == null || !inBounds(r.getKey())) ? null : r;
        }

        public K higherKey(K key) {
            Entry<K, V> k = higherEntry(key);
            return k != null ? k.getKey() : null;
        }

        public K firstKey() {
            Entry<K, V> e = firstEntry();
            if (e != null) {
                return e.getKey();
            }
            throw new NoSuchElementException();
        }

        public K lastKey() {
            Entry<K, V> e = lastEntry();
            if (e != null) {
                return e.getKey();
            }
            throw new NoSuchElementException();
        }

        public Entry<K, V> firstEntry() {
            Entry<K, V> k = this.lo == null ? this.m.firstEntry() : this.m.findLarger(this.lo, this.loInclusive);
            return (k == null || !inBounds(k.getKey())) ? null : k;
        }

        public Entry<K, V> lastEntry() {
            Entry<K, V> k = this.hi == null ? this.m.lastEntry() : this.m.findSmaller(this.hi, this.hiInclusive);
            return (k == null || !inBounds(k.getKey())) ? null : k;
        }

        public Entry<K, V> pollFirstEntry() {
            Entry<K, V> e;
            do {
                e = firstEntry();
                if (e == null) {
                    break;
                }
            } while (!remove(e.getKey(), e.getValue()));
            return e;
        }

        public Entry<K, V> pollLastEntry() {
            Entry<K, V> e;
            do {
                e = lastEntry();
                if (e == null) {
                    break;
                }
            } while (!remove(e.getKey(), e.getValue()));
            return e;
        }

        private SubMap<K, V> newSubMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            int c;
            if (this.lo != null) {
                if (fromKey == null) {
                    fromKey = this.lo;
                    fromInclusive = this.loInclusive;
                } else {
                    c = this.m.comparator.compare(fromKey, this.lo);
                    if (c < 0 || (c == 0 && !this.loInclusive && fromInclusive)) {
                        throw new IllegalArgumentException("key out of range");
                    }
                }
            }
            if (this.hi != null) {
                if (toKey == null) {
                    toKey = this.hi;
                    toInclusive = this.hiInclusive;
                } else {
                    c = this.m.comparator.compare(toKey, this.hi);
                    if (c > 0 || (c == 0 && !this.hiInclusive && toInclusive)) {
                        throw new IllegalArgumentException("key out of range");
                    }
                }
            }
            return new SubMap(this.m, fromKey, fromInclusive, toKey, toInclusive);
        }

        public SubMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            if (fromKey != null && toKey != null) {
                return newSubMap(fromKey, fromInclusive, toKey, toInclusive);
            }
            throw new NullPointerException();
        }

        public SubMap<K, V> headMap(K toKey, boolean inclusive) {
            if (toKey != null) {
                return newSubMap(null, BTreeMap.$assertionsDisabled, toKey, inclusive);
            }
            throw new NullPointerException();
        }

        public SubMap<K, V> tailMap(K fromKey, boolean inclusive) {
            if (fromKey != null) {
                return newSubMap(fromKey, inclusive, null, BTreeMap.$assertionsDisabled);
            }
            throw new NullPointerException();
        }

        public SubMap<K, V> subMap(K fromKey, K toKey) {
            return subMap((Object) fromKey, true, (Object) toKey, (boolean) BTreeMap.$assertionsDisabled);
        }

        public SubMap<K, V> headMap(K toKey) {
            return headMap((Object) toKey, (boolean) BTreeMap.$assertionsDisabled);
        }

        public SubMap<K, V> tailMap(K fromKey) {
            return tailMap((Object) fromKey, true);
        }

        public ConcurrentNavigableMap<K, V> descendingMap() {
            return new DescendingMap(this.m, this.lo, this.loInclusive, this.hi, this.hiInclusive);
        }

        public NavigableSet<K> navigableKeySet() {
            return new KeySet(this, this.m.hasValues);
        }

        private boolean tooLow(K key) {
            if (this.lo != null) {
                int c = this.m.comparator.compare(key, this.lo);
                if (c < 0 || (c == 0 && !this.loInclusive)) {
                    return true;
                }
            }
            return BTreeMap.$assertionsDisabled;
        }

        private boolean tooHigh(K key) {
            if (this.hi != null) {
                int c = this.m.comparator.compare(key, this.hi);
                if (c > 0 || (c == 0 && !this.hiInclusive)) {
                    return true;
                }
            }
            return BTreeMap.$assertionsDisabled;
        }

        private boolean inBounds(K key) {
            return (tooLow(key) || tooHigh(key)) ? BTreeMap.$assertionsDisabled : true;
        }

        private void checkKeyBounds(K key) throws IllegalArgumentException {
            if (key == null) {
                throw new NullPointerException();
            } else if (!inBounds(key)) {
                throw new IllegalArgumentException("key out of range");
            }
        }

        public NavigableSet<K> keySet() {
            return new KeySet(this, this.m.hasValues);
        }

        public NavigableSet<K> descendingKeySet() {
            return new DescendingMap(this.m, this.lo, this.loInclusive, this.hi, this.hiInclusive).keySet();
        }

        public Set<Entry<K, V>> entrySet() {
            return new EntrySet(this);
        }

        Iterator<K> keyIterator() {
            return new BTreeKeyIterator(this.m, this.lo, this.loInclusive, this.hi, this.hiInclusive);
        }

        Iterator<V> valueIterator() {
            return new BTreeValueIterator(this.m, this.lo, this.loInclusive, this.hi, this.hiInclusive);
        }

        Iterator<Entry<K, V>> entryIterator() {
            return new BTreeEntryIterator(this.m, this.lo, this.loInclusive, this.hi, this.hiInclusive);
        }
    }

    protected static final class ValRef {
        final long recid;

        public ValRef(long recid) {
            this.recid = recid;
        }

        public boolean equals(Object obj) {
            throw new IllegalAccessError();
        }

        public int hashCode() {
            throw new IllegalAccessError();
        }

        public String toString() {
            return "BTreeMap-ValRer[" + this.recid + "]";
        }
    }

    static final class Values<E> extends AbstractCollection<E> {
        private final ConcurrentNavigableMap<Object, E> m;

        Values(ConcurrentNavigableMap<Object, E> map) {
            this.m = map;
        }

        public Iterator<E> iterator() {
            if (this.m instanceof BTreeMap) {
                return ((BTreeMap) this.m).valueIterator();
            }
            return ((SubMap) this.m).valueIterator();
        }

        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        public int size() {
            return this.m.size();
        }

        public boolean contains(Object o) {
            return this.m.containsValue(o);
        }

        public void clear() {
            this.m.clear();
        }

        public Object[] toArray() {
            return BTreeMap.toList(this).toArray();
        }

        public <T> T[] toArray(T[] a) {
            return BTreeMap.toList(this).toArray(a);
        }
    }

    static class BTreeEntryIterator<K, V> extends BTreeIterator implements Iterator<Entry<K, V>> {
        BTreeEntryIterator(BTreeMap m) {
            super(m);
        }

        BTreeEntryIterator(BTreeMap m, Object lo, boolean loInclusive, Object hi, boolean hiInclusive) {
            super(m, lo, loInclusive, hi, hiInclusive);
        }

        public Entry<K, V> next() {
            if (this.currentLeaf == null) {
                throw new NoSuchElementException();
            }
            K ret = this.currentLeaf.keys[this.currentPos];
            Object val = this.currentLeaf.vals[this.currentPos - 1];
            advance();
            return this.m.makeEntry(ret, this.m.valExpand(val));
        }
    }

    static class BTreeKeyIterator<K> extends BTreeIterator implements Iterator<K> {
        BTreeKeyIterator(BTreeMap m) {
            super(m);
        }

        BTreeKeyIterator(BTreeMap m, Object lo, boolean loInclusive, Object hi, boolean hiInclusive) {
            super(m, lo, loInclusive, hi, hiInclusive);
        }

        public K next() {
            if (this.currentLeaf == null) {
                throw new NoSuchElementException();
            }
            K ret = this.currentLeaf.keys[this.currentPos];
            advance();
            return ret;
        }
    }

    static class BTreeValueIterator<V> extends BTreeIterator implements Iterator<V> {
        BTreeValueIterator(BTreeMap m) {
            super(m);
        }

        BTreeValueIterator(BTreeMap m, Object lo, boolean loInclusive, Object hi, boolean hiInclusive) {
            super(m, lo, loInclusive, hi, hiInclusive);
        }

        public V next() {
            if (this.currentLeaf == null) {
                throw new NoSuchElementException();
            }
            Object ret = this.currentLeaf.vals[this.currentPos - 1];
            advance();
            return this.m.valExpand(ret);
        }
    }

    protected static final class DirNode implements BNode {
        final long[] child;
        final Object[] keys;

        DirNode(Object[] keys, long[] child) {
            this.keys = keys;
            this.child = child;
        }

        DirNode(Object[] keys, List<Long> child) {
            this.keys = keys;
            this.child = new long[child.size()];
            for (int i = 0; i < child.size(); i++) {
                this.child[i] = ((Long) child.get(i)).longValue();
            }
        }

        public boolean isLeaf() {
            return BTreeMap.$assertionsDisabled;
        }

        public Object[] keys() {
            return this.keys;
        }

        public Object[] vals() {
            return null;
        }

        public Object highKey() {
            return this.keys[this.keys.length - 1];
        }

        public long[] child() {
            return this.child;
        }

        public long next() {
            return this.child[this.child.length - 1];
        }

        public String toString() {
            return "Dir(K" + Arrays.toString(this.keys) + ", C" + Arrays.toString(this.child) + ")";
        }
    }

    protected static final class LeafNode implements BNode {
        static final /* synthetic */ boolean $assertionsDisabled;
        final Object[] keys;
        final long next;
        final Object[] vals;

        static {
            $assertionsDisabled = !BTreeMap.class.desiredAssertionStatus() ? true : BTreeMap.$assertionsDisabled;
        }

        LeafNode(Object[] keys, Object[] vals, long next) {
            this.keys = keys;
            this.vals = vals;
            this.next = next;
            if (!$assertionsDisabled && vals != null && keys.length != vals.length + 2) {
                throw new AssertionError();
            }
        }

        public boolean isLeaf() {
            return true;
        }

        public Object[] keys() {
            return this.keys;
        }

        public Object[] vals() {
            return this.vals;
        }

        public Object highKey() {
            return this.keys[this.keys.length - 1];
        }

        public long[] child() {
            return null;
        }

        public long next() {
            return this.next;
        }

        public String toString() {
            return "Leaf(K" + Arrays.toString(this.keys) + ", V" + Arrays.toString(this.vals) + ", L=" + this.next + ")";
        }
    }

    protected static class NodeSerializer<A, B> implements Serializer<BNode> {
        static final /* synthetic */ boolean $assertionsDisabled;
        protected final Comparator comparator;
        protected final boolean hasValues;
        protected final BTreeKeySerializer keySerializer;
        protected final int numberOfNodeMetas;
        protected final boolean valsOutsideNodes;
        protected final Serializer<Object> valueSerializer;

        static {
            $assertionsDisabled = !BTreeMap.class.desiredAssertionStatus() ? true : BTreeMap.$assertionsDisabled;
        }

        public NodeSerializer(boolean valsOutsideNodes, BTreeKeySerializer keySerializer, Serializer valueSerializer, Comparator comparator, int numberOfNodeMetas) {
            if (!$assertionsDisabled && keySerializer == null) {
                throw new AssertionError();
            } else if ($assertionsDisabled || comparator != null) {
                this.hasValues = valueSerializer != null ? true : BTreeMap.$assertionsDisabled;
                this.valsOutsideNodes = valsOutsideNodes;
                this.keySerializer = keySerializer;
                this.valueSerializer = valueSerializer;
                this.comparator = comparator;
                this.numberOfNodeMetas = numberOfNodeMetas;
            } else {
                throw new AssertionError();
            }
        }

        public void serialize(DataOutput out, BNode value) throws IOException {
            boolean isLeaf = value.isLeaf();
            if (!$assertionsDisabled) {
                int length = value.keys().length;
                if (r0 > 255) {
                    throw new AssertionError();
                }
            }
            if ($assertionsDisabled || isLeaf || value.child().length == value.keys().length) {
                int header;
                int i$;
                int len$;
                if (!$assertionsDisabled && isLeaf && this.hasValues) {
                    if (value.vals().length != value.keys().length - 2) {
                        throw new AssertionError();
                    }
                }
                if (!($assertionsDisabled || isLeaf || value.highKey() == null)) {
                    if (value.child()[value.child().length - 1] == 0) {
                        throw new AssertionError();
                    }
                }
                boolean left = value.keys()[0] == null ? true : BTreeMap.$assertionsDisabled;
                boolean right = value.keys()[value.keys().length + -1] == null ? true : BTreeMap.$assertionsDisabled;
                if (isLeaf) {
                    if (right) {
                        if (left) {
                            header = BTreeMap.B_TREE_NODE_LEAF_LR;
                        } else {
                            header = BTreeMap.B_TREE_NODE_LEAF_R;
                        }
                    } else if (left) {
                        header = BTreeMap.B_TREE_NODE_LEAF_L;
                    } else {
                        header = BTreeMap.B_TREE_NODE_LEAF_C;
                    }
                } else if (right) {
                    if (left) {
                        header = BTreeMap.B_TREE_NODE_DIR_LR;
                    } else {
                        header = BTreeMap.B_TREE_NODE_DIR_R;
                    }
                } else if (left) {
                    header = BTreeMap.B_TREE_NODE_DIR_L;
                } else {
                    header = BTreeMap.B_TREE_NODE_DIR_C;
                }
                out.write(header);
                out.write(value.keys().length);
                int i = 0;
                while (true) {
                    length = this.numberOfNodeMetas;
                    if (i >= r0) {
                        break;
                    }
                    DataOutput2.packLong(out, 0);
                    i++;
                }
                if (isLeaf) {
                    DataOutput2.packLong(out, ((LeafNode) value).next);
                } else {
                    for (long child : ((DirNode) value).child) {
                        DataOutput2.packLong(out, child);
                    }
                }
                this.keySerializer.serialize(out, left ? 1 : 0, right ? value.keys().length - 1 : value.keys().length, value.keys());
                if (!isLeaf) {
                    return;
                }
                if (this.hasValues) {
                    Object[] arr$ = value.vals();
                    len$ = arr$.length;
                    i$ = 0;
                    while (i$ < len$) {
                        Object val = arr$[i$];
                        if ($assertionsDisabled || val != null) {
                            if (this.valsOutsideNodes) {
                                DataOutput2.packLong(out, ((ValRef) val).recid);
                            } else {
                                this.valueSerializer.serialize(out, val);
                            }
                            i$++;
                        } else {
                            throw new AssertionError();
                        }
                    }
                    return;
                }
                boolean[] bools = new boolean[value.vals().length];
                i = 0;
                while (true) {
                    length = bools.length;
                    if (i < r0) {
                        bools[i] = value.vals()[i] != null ? true : BTreeMap.$assertionsDisabled;
                        i++;
                    } else {
                        out.write(SerializerBase.booleanToByteArray(bools));
                        return;
                    }
                }
            }
            throw new AssertionError();
        }

        public BNode deserialize(DataInput in, int available) throws IOException {
            int end;
            int header = in.readUnsignedByte();
            int size = in.readUnsignedByte();
            int i = 0;
            while (true) {
                int i2 = this.numberOfNodeMetas;
                if (i >= r0) {
                    break;
                }
                DataInput2.unpackLong(in);
                i++;
            }
            boolean isLeaf = (header == BTreeMap.B_TREE_NODE_LEAF_C || header == BTreeMap.B_TREE_NODE_LEAF_L || header == BTreeMap.B_TREE_NODE_LEAF_LR || header == BTreeMap.B_TREE_NODE_LEAF_R) ? true : BTreeMap.$assertionsDisabled;
            int start = (header == BTreeMap.B_TREE_NODE_LEAF_L || header == BTreeMap.B_TREE_NODE_LEAF_LR || header == BTreeMap.B_TREE_NODE_DIR_L || header == BTreeMap.B_TREE_NODE_DIR_LR) ? 1 : 0;
            if (header == BTreeMap.B_TREE_NODE_LEAF_R || header == BTreeMap.B_TREE_NODE_LEAF_LR || header == BTreeMap.B_TREE_NODE_DIR_R || header == BTreeMap.B_TREE_NODE_DIR_LR) {
                end = size - 1;
            } else {
                end = size;
            }
            Object[] keys;
            if (isLeaf) {
                long next = DataInput2.unpackLong(in);
                keys = this.keySerializer.deserialize(in, start, end, size);
                if (!$assertionsDisabled) {
                    i2 = keys.length;
                    if (r0 != size) {
                        throw new AssertionError();
                    }
                }
                Object[] vals = new Object[(size - 2)];
                if (!this.hasValues) {
                    boolean[] bools = SerializerBase.readBooleanArray(vals.length, in);
                    i = 0;
                    while (true) {
                        i2 = bools.length;
                        if (i >= r0) {
                            break;
                        }
                        if (bools[i]) {
                            vals[i] = BTreeMap.EMPTY;
                        }
                        i++;
                    }
                } else {
                    for (i = 0; i < size - 2; i++) {
                        if (this.valsOutsideNodes) {
                            ValRef valRef;
                            long recid = DataInput2.unpackLong(in);
                            if (recid == 0) {
                                valRef = null;
                            } else {
                                ValRef valRef2 = new ValRef(recid);
                            }
                            vals[i] = valRef;
                        } else {
                            vals[i] = this.valueSerializer.deserialize(in, -1);
                        }
                    }
                }
                return new LeafNode(keys, vals, next);
            }
            long[] child = new long[size];
            for (i = 0; i < size; i++) {
                child[i] = DataInput2.unpackLong(in);
            }
            keys = this.keySerializer.deserialize(in, start, end, size);
            if (!$assertionsDisabled) {
                i2 = keys.length;
                if (r0 != size) {
                    throw new AssertionError();
                }
            }
            return new DirNode(keys, child);
        }

        public int fixedSize() {
            return -1;
        }
    }

    static {
        $assertionsDisabled = !BTreeMap.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        COMPARABLE_COMPARATOR = new Comparator<Comparable>() {
            public int compare(Comparable o1, Comparable o2) {
                return o1.compareTo(o2);
            }
        };
        EMPTY = new Object();
    }

    protected static SortedMap<String, Object> preinitCatalog(DB db) {
        if (((Long) db.getEngine().get(1, Serializer.LONG)) == null) {
            if (db.getEngine().isReadOnly()) {
                return Collections.unmodifiableSortedMap(new TreeMap());
            }
            NodeSerializer rootSerializer = new NodeSerializer($assertionsDisabled, BTreeKeySerializer.STRING, db.getDefaultSerializer(), COMPARABLE_COMPARATOR, 0);
            Long valueOf = Long.valueOf(db.getEngine().put(new LeafNode(new Object[]{null, null}, new Object[0], 0), rootSerializer));
            db.getEngine().update(1, rootRef, Serializer.LONG);
            db.getEngine().commit();
        }
        return new BTreeMap(db.engine, 1, 32, $assertionsDisabled, 0, BTreeKeySerializer.STRING, db.getDefaultSerializer(), COMPARABLE_COMPARATOR, 0, $assertionsDisabled);
    }

    public BTreeMap(Engine engine, long rootRecidRef, int maxNodeSize, boolean valsOutsideNodes, long counterRecid, BTreeKeySerializer<K> keySerializer, Serializer<V> valueSerializer, Comparator<K> comparator, int numberOfNodeMetas, boolean disableLocks) {
        this.nodeLocks = new LongConcurrentHashMap();
        this.entrySet = new EntrySet(this);
        this.values = new Values(this);
        this.descendingMap = new DescendingMap(this, null, true, null, $assertionsDisabled);
        this.modListenersLock = new Object();
        this.modListeners = new MapListener[0];
        if (maxNodeSize % 2 != 0) {
            throw new IllegalArgumentException("maxNodeSize must be dividable by 2");
        } else if (maxNodeSize < 6) {
            throw new IllegalArgumentException("maxNodeSize too low");
        } else if (maxNodeSize > 126) {
            throw new IllegalArgumentException("maxNodeSize too high");
        } else if (rootRecidRef <= 0 || counterRecid < 0 || numberOfNodeMetas < 0) {
            throw new IllegalArgumentException();
        } else if (keySerializer == null) {
            throw new NullPointerException();
        } else if (comparator == null) {
            throw new NullPointerException();
        } else {
            SerializerBase.assertSerializable(keySerializer);
            SerializerBase.assertSerializable(valueSerializer);
            SerializerBase.assertSerializable(comparator);
            this.rootRecidRef = rootRecidRef;
            this.hasValues = valueSerializer != null ? true : $assertionsDisabled;
            this.valsOutsideNodes = valsOutsideNodes;
            this.engine = engine;
            this.maxNodeSize = maxNodeSize;
            this.comparator = comparator;
            this.numberOfNodeMetas = numberOfNodeMetas;
            Comparator requiredComparator = keySerializer.getComparator();
            if (requiredComparator == null || requiredComparator.equals(comparator)) {
                this.keySerializer = keySerializer;
                this.valueSerializer = valueSerializer;
                this.nodeSerializer = new NodeSerializer(valsOutsideNodes, keySerializer, valueSerializer, comparator, numberOfNodeMetas);
                this.keySet = new KeySet(this, this.hasValues);
                if (counterRecid != 0) {
                    this.counter = new Long(engine, counterRecid);
                    Bind.size(this, this.counter);
                } else {
                    this.counter = null;
                }
                ArrayList leftEdges2 = new ArrayList();
                long r = ((Long) engine.get(rootRecidRef, Serializer.LONG)).longValue();
                while (true) {
                    BNode n = (BNode) engine.get(r, this.nodeSerializer);
                    leftEdges2.add(Long.valueOf(r));
                    if (n.isLeaf()) {
                        Collections.reverse(leftEdges2);
                        this.leftEdges = new CopyOnWriteArrayList(leftEdges2);
                        return;
                    }
                    r = n.child()[0];
                }
            } else {
                throw new IllegalArgumentException("KeySerializers requires its own comparator");
            }
        }
    }

    protected static long createRootRef(Engine engine, BTreeKeySerializer keySer, Serializer valueSer, Comparator comparator, int numberOfNodeMetas) {
        return engine.put(Long.valueOf(engine.put(new LeafNode(new Object[]{null, null}, new Object[0], 0), new NodeSerializer($assertionsDisabled, keySer, valueSer, comparator, numberOfNodeMetas))), Serializer.LONG);
    }

    protected final int findChildren(Object key, Object[] keys) {
        int left = 0;
        if (keys[0] == null) {
            left = 0 + 1;
        }
        right = keys[keys.length + -1] == null ? keys.length - 1 : keys.length;
        do {
            int middle = (left + right) / 2;
            if (keys[middle] == null) {
                return middle;
            }
            if (this.comparator.compare(keys[middle], key) < 0) {
                left = middle + 1;
                continue;
            } else {
                right = middle;
                continue;
            }
        } while (left < right);
        return right;
    }

    public V get(Object key) {
        return get(key, true);
    }

    protected Object get(Object key, boolean expandValue) {
        if (key == null) {
            throw new NullPointerException();
        }
        K v = key;
        BNode A = (BNode) this.engine.get(((Long) this.engine.get(this.rootRecidRef, Serializer.LONG)).longValue(), this.nodeSerializer);
        while (!A.isLeaf()) {
            A = (BNode) this.engine.get(nextDir((DirNode) A, v), this.nodeSerializer);
        }
        LeafNode leaf = (LeafNode) A;
        int pos = findChildren(v, leaf.keys);
        while (pos == leaf.keys.length) {
            leaf = (LeafNode) this.engine.get(leaf.next, this.nodeSerializer);
            pos = findChildren(v, leaf.keys);
        }
        if (pos == leaf.keys.length - 1 || leaf.keys[pos] == null || this.comparator.compare(v, leaf.keys[pos]) != 0) {
            return null;
        }
        Object ret = leaf.vals[pos - 1];
        if (expandValue) {
            return valExpand(ret);
        }
        return ret;
    }

    protected V valExpand(Object ret) {
        if (!this.valsOutsideNodes || ret == null) {
            return ret;
        }
        return this.engine.get(((ValRef) ret).recid, this.valueSerializer);
    }

    protected long nextDir(DirNode d, Object key) {
        int pos = findChildren(key, d.keys) - 1;
        if (pos < 0) {
            pos = 0;
        }
        return d.child[pos];
    }

    public V put(K key, V value) {
        if (key != null && value != null) {
            return put2(key, value, $assertionsDisabled);
        }
        throw new NullPointerException();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected V put2(K r51, V r52, boolean r53) {
        /*
        r50 = this;
        r39 = r51;
        if (r39 != 0) goto L_0x000c;
    L_0x0004:
        r45 = new java.lang.IllegalArgumentException;
        r46 = "null key";
        r45.<init>(r46);
        throw r45;
    L_0x000c:
        if (r52 != 0) goto L_0x0016;
    L_0x000e:
        r45 = new java.lang.IllegalArgumentException;
        r46 = "null value";
        r45.<init>(r46);
        throw r45;
    L_0x0016:
        r44 = r52;
        r0 = r50;
        r0 = r0.valsOutsideNodes;
        r45 = r0;
        if (r45 == 0) goto L_0x003f;
    L_0x0020:
        r0 = r50;
        r0 = r0.engine;
        r45 = r0;
        r0 = r50;
        r0 = r0.valueSerializer;
        r46 = r0;
        r0 = r45;
        r1 = r52;
        r2 = r46;
        r30 = r0.put(r1, r2);
        r44 = new org.mapdb.BTreeMap$ValRef;
        r0 = r44;
        r1 = r30;
        r0.<init>(r1);
    L_0x003f:
        r36 = -1;
        r45 = 4;
        r0 = r45;
        r0 = new long[r0];
        r38 = r0;
        r0 = r50;
        r0 = r0.engine;
        r45 = r0;
        r0 = r50;
        r0 = r0.rootRecidRef;
        r46 = r0;
        r48 = org.mapdb.Serializer.LONG;
        r45 = r45.get(r46, r48);
        r45 = (java.lang.Long) r45;
        r34 = r45.longValue();
        r10 = r34;
        r0 = r50;
        r0 = r0.engine;
        r45 = r0;
        r0 = r50;
        r0 = r0.nodeSerializer;
        r46 = r0;
        r0 = r45;
        r1 = r46;
        r4 = r0.get(r10, r1);
        r4 = (org.mapdb.BTreeMap.BNode) r4;
    L_0x0079:
        r45 = r4.isLeaf();
        if (r45 != 0) goto L_0x00ec;
    L_0x007f:
        r40 = r10;
        r45 = r4;
        r45 = (org.mapdb.BTreeMap.DirNode) r45;
        r0 = r50;
        r1 = r45;
        r2 = r39;
        r10 = r0.nextDir(r1, r2);
        r45 = $assertionsDisabled;
        if (r45 != 0) goto L_0x00a1;
    L_0x0093:
        r46 = 0;
        r45 = (r10 > r46 ? 1 : (r10 == r46 ? 0 : -1));
        if (r45 > 0) goto L_0x00a1;
    L_0x0099:
        r45 = new java.lang.AssertionError;
        r0 = r45;
        r0.<init>(r4);
        throw r45;
    L_0x00a1:
        r45 = r4.child();
        r46 = r4.child();
        r0 = r46;
        r0 = r0.length;
        r46 = r0;
        r46 = r46 + -1;
        r46 = r45[r46];
        r45 = (r10 > r46 ? 1 : (r10 == r46 ? 0 : -1));
        if (r45 != 0) goto L_0x00cd;
    L_0x00b6:
        r0 = r50;
        r0 = r0.engine;
        r45 = r0;
        r0 = r50;
        r0 = r0.nodeSerializer;
        r46 = r0;
        r0 = r45;
        r1 = r46;
        r4 = r0.get(r10, r1);
        r4 = (org.mapdb.BTreeMap.BNode) r4;
        goto L_0x0079;
    L_0x00cd:
        r36 = r36 + 1;
        r0 = r38;
        r0 = r0.length;
        r45 = r0;
        r0 = r45;
        r1 = r36;
        if (r0 != r1) goto L_0x00e9;
    L_0x00da:
        r0 = r38;
        r0 = r0.length;
        r45 = r0;
        r45 = r45 * 2;
        r0 = r38;
        r1 = r45;
        r38 = java.util.Arrays.copyOf(r0, r1);
    L_0x00e9:
        r38[r36] = r40;
        goto L_0x00b6;
    L_0x00ec:
        r17 = 1;
        r24 = 0;
        r37 = r36;
    L_0x00f2:
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r45;
        lock(r0, r10);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r14 = 1;
        r0 = r50;
        r0 = r0.engine;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r50;
        r0 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        r0 = r45;
        r1 = r46;
        r45 = r0.get(r10, r1);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r45;
        r0 = (org.mapdb.BTreeMap.BNode) r0;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r4 = r0;
        r45 = r4.keys();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r50;
        r1 = r39;
        r2 = r45;
        r26 = r0.findChildren(r1, r2);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r4.keys();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r45;
        r0 = r0.length;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r45 = r45 + -1;
        r0 = r26;
        r1 = r45;
        if (r0 >= r1) goto L_0x0214;
    L_0x0136:
        if (r39 == 0) goto L_0x0214;
    L_0x0138:
        r45 = r4.keys();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r45[r26];	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        if (r45 == 0) goto L_0x0214;
    L_0x0140:
        r0 = r50;
        r0 = r0.comparator;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r46 = r4.keys();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r46[r26];	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r45;
        r1 = r39;
        r2 = r46;
        r45 = r0.compare(r1, r2);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        if (r45 != 0) goto L_0x0214;
    L_0x0158:
        r45 = r4.vals();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r26 + -1;
        r19 = r45[r46];	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        if (r53 == 0) goto L_0x0176;
    L_0x0162:
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r45;
        unlock(r0, r10);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r50;
        r1 = r19;
        r32 = r0.valExpand(r1);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
    L_0x0175:
        return r32;
    L_0x0176:
        r45 = r4.vals();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r4.vals();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r46;
        r0 = r0.length;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        r42 = java.util.Arrays.copyOf(r45, r46);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r26 + -1;
        r42[r45] = r44;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r5 = new org.mapdb.BTreeMap$LeafNode;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r4.keys();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r4.keys();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r46;
        r0 = r0.length;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        r46 = java.util.Arrays.copyOf(r45, r46);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r4;
        r0 = (org.mapdb.BTreeMap.LeafNode) r0;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r45;
        r0 = r0.next;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r48 = r0;
        r0 = r46;
        r1 = r42;
        r2 = r48;
        r5.<init>(r0, r1, r2);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = $assertionsDisabled;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        if (r45 != 0) goto L_0x01e0;
    L_0x01b6:
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r45;
        r45 = r0.get(r10);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = java.lang.Thread.currentThread();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r45;
        r1 = r46;
        if (r0 == r1) goto L_0x01e0;
    L_0x01cc:
        r45 = new java.lang.AssertionError;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45.<init>();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        throw r45;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
    L_0x01d2:
        r13 = move-exception;
        r4 = r5;
        r36 = r37;
    L_0x01d6:
        r0 = r50;
        r0 = r0.nodeLocks;
        r45 = r0;
        unlockAll(r45);
        throw r13;
    L_0x01e0:
        r0 = r50;
        r0 = r0.engine;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r50;
        r0 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r0;
        r0 = r45;
        r1 = r46;
        r0.update(r10, r5, r1);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r50;
        r1 = r19;
        r32 = r0.valExpand(r1);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r50;
        r1 = r51;
        r2 = r32;
        r3 = r52;
        r0.notify(r1, r2, r3);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r45;
        unlock(r0, r10);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r4 = r5;
        goto L_0x0175;
    L_0x0214:
        r45 = r4.highKey();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        if (r45 == 0) goto L_0x0638;
    L_0x021a:
        r0 = r50;
        r0 = r0.comparator;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r46 = r4.highKey();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r45;
        r1 = r39;
        r2 = r46;
        r45 = r0.compare(r1, r2);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        if (r45 <= 0) goto L_0x0638;
    L_0x0230:
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r45;
        unlock(r0, r10);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r14 = 0;
        r45 = r4.keys();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r50;
        r1 = r39;
        r2 = r45;
        r27 = r0.findChildren(r1, r2);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
    L_0x024a:
        if (r4 == 0) goto L_0x0638;
    L_0x024c:
        r45 = r4.keys();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r45;
        r0 = r0.length;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r27;
        r1 = r45;
        if (r0 != r1) goto L_0x0638;
    L_0x025b:
        r22 = r4.next();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = 0;
        r45 = (r22 > r46 ? 1 : (r22 == r46 ? 0 : -1));
        if (r45 != 0) goto L_0x0304;
    L_0x0265:
        r5 = r4;
    L_0x0266:
        if (r14 == 0) goto L_0x0635;
    L_0x0268:
        r45 = r5.keys();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r45;
        r0 = r0.length;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r0;
        r45 = r5.isLeaf();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        if (r45 == 0) goto L_0x032f;
    L_0x0277:
        r45 = 2;
    L_0x0279:
        r45 = r46 - r45;
        r0 = r50;
        r0 = r0.maxNodeSize;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r0;
        r0 = r45;
        r1 = r46;
        if (r0 >= r1) goto L_0x03bc;
    L_0x0287:
        r45 = r5.keys();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r50;
        r1 = r39;
        r2 = r45;
        r26 = r0.findChildren(r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r5.keys();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r45;
        r1 = r26;
        r2 = r39;
        r15 = arrayPut(r0, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r5.isLeaf();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        if (r45 == 0) goto L_0x0365;
    L_0x02a9:
        r45 = r5.vals();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r26 + -1;
        r0 = r45;
        r1 = r46;
        r2 = r44;
        r42 = arrayPut(r0, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r18 = new org.mapdb.BTreeMap$LeafNode;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r5;
        r0 = (org.mapdb.BTreeMap.LeafNode) r0;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r45;
        r0 = r0.next;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r0;
        r0 = r18;
        r1 = r42;
        r2 = r46;
        r0.<init>(r15, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = $assertionsDisabled;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        if (r45 != 0) goto L_0x0333;
    L_0x02d3:
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r45;
        r45 = r0.get(r10);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = java.lang.Thread.currentThread();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r45;
        r1 = r46;
        if (r0 == r1) goto L_0x0333;
    L_0x02e9:
        r45 = new java.lang.AssertionError;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45.<init>();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        throw r45;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
    L_0x02ef:
        r13 = move-exception;
        r4 = r5;
        r36 = r37;
    L_0x02f3:
        r0 = r50;
        r0 = r0.nodeLocks;
        r45 = r0;
        unlockAll(r45);
        r45 = new java.lang.RuntimeException;
        r0 = r45;
        r0.<init>(r13);
        throw r45;
    L_0x0304:
        r10 = r22;
        r0 = r50;
        r0 = r0.engine;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r50;
        r0 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        r0 = r45;
        r1 = r46;
        r45 = r0.get(r10, r1);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r45;
        r0 = (org.mapdb.BTreeMap.BNode) r0;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r4 = r0;
        r45 = r4.keys();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r50;
        r1 = r39;
        r2 = r45;
        r27 = r0.findChildren(r1, r2);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        goto L_0x024a;
    L_0x032f:
        r45 = 1;
        goto L_0x0279;
    L_0x0333:
        r0 = r50;
        r0 = r0.engine;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r50;
        r0 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r0;
        r0 = r45;
        r1 = r18;
        r2 = r46;
        r0.update(r10, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
    L_0x0348:
        r45 = 0;
        r0 = r50;
        r1 = r51;
        r2 = r45;
        r3 = r52;
        r0.notify(r1, r2, r3);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r45;
        unlock(r0, r10);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r32 = 0;
        r4 = r5;
        goto L_0x0175;
    L_0x0365:
        r45 = $assertionsDisabled;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        if (r45 != 0) goto L_0x0375;
    L_0x0369:
        r46 = 0;
        r45 = (r24 > r46 ? 1 : (r24 == r46 ? 0 : -1));
        if (r45 != 0) goto L_0x0375;
    L_0x036f:
        r45 = new java.lang.AssertionError;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45.<init>();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        throw r45;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
    L_0x0375:
        r45 = r5.child();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r45;
        r1 = r26;
        r2 = r24;
        r8 = arrayLongPut(r0, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r12 = new org.mapdb.BTreeMap$DirNode;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r12.<init>(r15, r8);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = $assertionsDisabled;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        if (r45 != 0) goto L_0x03a8;
    L_0x038c:
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r45;
        r45 = r0.get(r10);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = java.lang.Thread.currentThread();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r45;
        r1 = r46;
        if (r0 == r1) goto L_0x03a8;
    L_0x03a2:
        r45 = new java.lang.AssertionError;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45.<init>();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        throw r45;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
    L_0x03a8:
        r0 = r50;
        r0 = r0.engine;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r50;
        r0 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r0;
        r0 = r45;
        r1 = r46;
        r0.update(r10, r12, r1);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        goto L_0x0348;
    L_0x03bc:
        r45 = r5.keys();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r50;
        r1 = r39;
        r2 = r45;
        r26 = r0.findChildren(r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r5.keys();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r45;
        r1 = r26;
        r2 = r39;
        r15 = arrayPut(r0, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r5.isLeaf();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        if (r45 == 0) goto L_0x049c;
    L_0x03de:
        r45 = r5.vals();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r26 + -1;
        r0 = r45;
        r1 = r46;
        r2 = r44;
        r42 = arrayPut(r0, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
    L_0x03ee:
        r45 = r5.isLeaf();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        if (r45 == 0) goto L_0x04a0;
    L_0x03f4:
        r8 = 0;
    L_0x03f5:
        r0 = r15.length;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r33 = r45 / 2;
        r45 = r5.isLeaf();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        if (r45 == 0) goto L_0x04b0;
    L_0x0400:
        r0 = r42;
        r0 = r0.length;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r42;
        r1 = r33;
        r2 = r45;
        r43 = java.util.Arrays.copyOfRange(r0, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r6 = new org.mapdb.BTreeMap$LeafNode;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r15.length;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r33;
        r1 = r45;
        r46 = java.util.Arrays.copyOfRange(r15, r0, r1);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r5;
        r0 = (org.mapdb.BTreeMap.LeafNode) r0;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r45;
        r0 = r0.next;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r48 = r0;
        r0 = r46;
        r1 = r43;
        r2 = r48;
        r6.<init>(r0, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
    L_0x0430:
        r0 = r50;
        r0 = r0.engine;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r50;
        r0 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r0;
        r0 = r45;
        r1 = r46;
        r28 = r0.put(r6, r1);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r5.isLeaf();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        if (r45 == 0) goto L_0x04d1;
    L_0x044a:
        r45 = r33 + 2;
        r0 = r45;
        r16 = java.util.Arrays.copyOf(r15, r0);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r16;
        r0 = r0.length;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r45 = r45 + -1;
        r0 = r16;
        r0 = r0.length;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r0;
        r46 = r46 + -2;
        r46 = r16[r46];	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r16[r45] = r46;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r42;
        r1 = r33;
        r43 = java.util.Arrays.copyOf(r0, r1);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r4 = new org.mapdb.BTreeMap$LeafNode;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r16;
        r1 = r43;
        r2 = r28;
        r4.<init>(r0, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
    L_0x0477:
        r45 = $assertionsDisabled;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        if (r45 != 0) goto L_0x04eb;
    L_0x047b:
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r45;
        r45 = r0.get(r10);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = java.lang.Thread.currentThread();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r45;
        r1 = r46;
        if (r0 == r1) goto L_0x04eb;
    L_0x0491:
        r45 = new java.lang.AssertionError;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45.<init>();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        throw r45;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
    L_0x0497:
        r13 = move-exception;
        r36 = r37;
        goto L_0x01d6;
    L_0x049c:
        r42 = 0;
        goto L_0x03ee;
    L_0x04a0:
        r45 = r5.child();	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r45;
        r1 = r26;
        r2 = r24;
        r8 = arrayLongPut(r0, r1, r2);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        goto L_0x03f5;
    L_0x04b0:
        r6 = new org.mapdb.BTreeMap$DirNode;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r15.length;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r0;
        r0 = r33;
        r1 = r45;
        r45 = java.util.Arrays.copyOfRange(r15, r0, r1);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r15.length;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r46 = r0;
        r0 = r33;
        r1 = r46;
        r46 = java.util.Arrays.copyOfRange(r8, r0, r1);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r45;
        r1 = r46;
        r6.<init>(r0, r1);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        goto L_0x0430;
    L_0x04d1:
        r45 = r33 + 1;
        r0 = r45;
        r9 = java.util.Arrays.copyOf(r8, r0);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r9[r33] = r28;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r4 = new org.mapdb.BTreeMap$DirNode;	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r45 = r33 + 1;
        r0 = r45;
        r45 = java.util.Arrays.copyOf(r15, r0);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        r0 = r45;
        r4.<init>(r0, r9);	 Catch:{ RuntimeException -> 0x01d2, Exception -> 0x02ef }
        goto L_0x0477;
    L_0x04eb:
        r0 = r50;
        r0 = r0.engine;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r50;
        r0 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        r0 = r45;
        r1 = r46;
        r0.update(r10, r4, r1);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = (r10 > r34 ? 1 : (r10 == r34 ? 0 : -1));
        if (r45 == 0) goto L_0x0549;
    L_0x0502:
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r45;
        unlock(r0, r10);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r24 = r28;
        r39 = r4.highKey();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r17 = r17 + 1;
        r45 = -1;
        r0 = r37;
        r1 = r45;
        if (r0 == r1) goto L_0x0534;
    L_0x051d:
        r36 = r37 + -1;
        r10 = r38[r37];	 Catch:{ RuntimeException -> 0x0531, Exception -> 0x0632 }
    L_0x0521:
        r45 = $assertionsDisabled;	 Catch:{ RuntimeException -> 0x0531, Exception -> 0x0632 }
        if (r45 != 0) goto L_0x062e;
    L_0x0525:
        r46 = 0;
        r45 = (r10 > r46 ? 1 : (r10 == r46 ? 0 : -1));
        if (r45 > 0) goto L_0x062e;
    L_0x052b:
        r45 = new java.lang.AssertionError;	 Catch:{ RuntimeException -> 0x0531, Exception -> 0x0632 }
        r45.<init>();	 Catch:{ RuntimeException -> 0x0531, Exception -> 0x0632 }
        throw r45;	 Catch:{ RuntimeException -> 0x0531, Exception -> 0x0632 }
    L_0x0531:
        r13 = move-exception;
        goto L_0x01d6;
    L_0x0534:
        r0 = r50;
        r0 = r0.leftEdges;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r46 = r17 + -1;
        r45 = r45.get(r46);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = (java.lang.Long) r45;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r10 = r45.longValue();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r36 = r37;
        goto L_0x0521;
    L_0x0549:
        r7 = new org.mapdb.BTreeMap$DirNode;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = 3;
        r0 = r45;
        r0 = new java.lang.Object[r0];	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        r45 = 0;
        r47 = r4.keys();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r48 = 0;
        r47 = r47[r48];	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46[r45] = r47;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = 1;
        r47 = r4.highKey();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46[r45] = r47;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r47 = 2;
        r45 = r6.isLeaf();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        if (r45 == 0) goto L_0x05e7;
    L_0x056f:
        r45 = 0;
    L_0x0571:
        r46[r47] = r45;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = 3;
        r0 = r45;
        r0 = new long[r0];	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r47 = 0;
        r45[r47] = r10;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r47 = 1;
        r45[r47] = r28;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r47 = 2;
        r48 = 0;
        r45[r47] = r48;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r46;
        r1 = r45;
        r7.<init>(r0, r1);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r50;
        r0 = r0.rootRecidRef;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        lock(r45, r46);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r45;
        unlock(r0, r10);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r50;
        r0 = r0.engine;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r50;
        r0 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        r0 = r45;
        r1 = r46;
        r20 = r0.put(r7, r1);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = $assertionsDisabled;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        if (r45 != 0) goto L_0x05ec;
    L_0x05c2:
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r50;
        r0 = r0.rootRecidRef;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        r45 = r45.get(r46);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = java.lang.Thread.currentThread();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r45;
        r1 = r46;
        if (r0 == r1) goto L_0x05ec;
    L_0x05dc:
        r45 = new java.lang.AssertionError;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45.<init>();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        throw r45;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
    L_0x05e2:
        r13 = move-exception;
        r36 = r37;
        goto L_0x02f3;
    L_0x05e7:
        r45 = r6.highKey();	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        goto L_0x0571;
    L_0x05ec:
        r0 = r50;
        r0 = r0.engine;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r50;
        r0 = r0.rootRecidRef;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        r48 = java.lang.Long.valueOf(r20);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r49 = org.mapdb.Serializer.LONG;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45.update(r46, r48, r49);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r50;
        r0 = r0.leftEdges;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r46 = java.lang.Long.valueOf(r20);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45.add(r46);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = 0;
        r0 = r50;
        r1 = r51;
        r2 = r45;
        r3 = r52;
        r0.notify(r1, r2, r3);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r0 = r50;
        r0 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r45 = r0;
        r0 = r50;
        r0 = r0.rootRecidRef;	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r46 = r0;
        unlock(r45, r46);	 Catch:{ RuntimeException -> 0x0497, Exception -> 0x05e2 }
        r32 = 0;
        goto L_0x0175;
    L_0x062e:
        r37 = r36;
        goto L_0x00f2;
    L_0x0632:
        r13 = move-exception;
        goto L_0x02f3;
    L_0x0635:
        r4 = r5;
        goto L_0x00f2;
    L_0x0638:
        r5 = r4;
        goto L_0x0266;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mapdb.BTreeMap.put2(java.lang.Object, java.lang.Object, boolean):V");
    }

    public V remove(Object key) {
        return remove2(key, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private V remove2(java.lang.Object r17, java.lang.Object r18) {
        /*
        r16 = this;
        r0 = r16;
        r12 = r0.engine;
        r0 = r16;
        r14 = r0.rootRecidRef;
        r13 = org.mapdb.Serializer.LONG;
        r12 = r12.get(r14, r13);
        r12 = (java.lang.Long) r12;
        r4 = r12.longValue();
        r0 = r16;
        r12 = r0.engine;
        r0 = r16;
        r13 = r0.nodeSerializer;
        r2 = r12.get(r4, r13);
        r2 = (org.mapdb.BTreeMap.BNode) r2;
    L_0x0022:
        r12 = r2.isLeaf();
        if (r12 != 0) goto L_0x0041;
    L_0x0028:
        r2 = (org.mapdb.BTreeMap.DirNode) r2;
        r0 = r16;
        r1 = r17;
        r4 = r0.nextDir(r2, r1);
        r0 = r16;
        r12 = r0.engine;
        r0 = r16;
        r13 = r0.nodeSerializer;
        r2 = r12.get(r4, r13);
        r2 = (org.mapdb.BTreeMap.BNode) r2;
        goto L_0x0022;
    L_0x0041:
        r0 = r16;
        r12 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        lock(r12, r4);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r16;
        r12 = r0.engine;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r16;
        r13 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12.get(r4, r13);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r12;
        r0 = (org.mapdb.BTreeMap.BNode) r0;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r2 = r0;
        r12 = r2.keys();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r16;
        r1 = r17;
        r9 = r0.findChildren(r1, r12);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r2.keys();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12.length;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        if (r9 >= r12) goto L_0x0144;
    L_0x006b:
        if (r17 == 0) goto L_0x0144;
    L_0x006d:
        r12 = r2.keys();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12[r9];	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        if (r12 == 0) goto L_0x0144;
    L_0x0075:
        r0 = r16;
        r12 = r0.comparator;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r13 = r2.keys();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r13 = r13[r9];	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r17;
        r12 = r12.compare(r0, r13);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        if (r12 != 0) goto L_0x0144;
    L_0x0087:
        r12 = r2.keys();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12.length;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12 + -1;
        if (r9 != r12) goto L_0x009b;
    L_0x0090:
        if (r18 != 0) goto L_0x009b;
    L_0x0092:
        r0 = r16;
        r12 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        unlock(r12, r4);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r8 = 0;
    L_0x009a:
        return r8;
    L_0x009b:
        r12 = r2.vals();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r13 = r9 + -1;
        r8 = r12[r13];	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r16;
        r8 = r0.valExpand(r8);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        if (r18 == 0) goto L_0x00bc;
    L_0x00ab:
        r0 = r18;
        r12 = r0.equals(r8);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        if (r12 != 0) goto L_0x00bc;
    L_0x00b3:
        r0 = r16;
        r12 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        unlock(r12, r4);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r8 = 0;
        goto L_0x009a;
    L_0x00bc:
        r12 = r2.keys();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12.length;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12 + -1;
        r7 = new java.lang.Object[r12];	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r2.keys();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r13 = 0;
        r14 = 0;
        java.lang.System.arraycopy(r12, r13, r7, r14, r9);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r2.keys();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r13 = r9 + 1;
        r14 = r7.length;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r14 = r14 - r9;
        java.lang.System.arraycopy(r12, r13, r7, r9, r14);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r2.vals();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12.length;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12 + -1;
        r11 = new java.lang.Object[r12];	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r2.vals();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r13 = 0;
        r14 = 0;
        r15 = r9 + -1;
        java.lang.System.arraycopy(r12, r13, r11, r14, r15);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r2.vals();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r13 = r9 + -1;
        r14 = r11.length;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r15 = r9 + -1;
        r14 = r14 - r15;
        java.lang.System.arraycopy(r12, r9, r11, r13, r14);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r3 = new org.mapdb.BTreeMap$LeafNode;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r2;
        r0 = (org.mapdb.BTreeMap.LeafNode) r0;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r0;
        r12 = r12.next;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r3.<init>(r7, r11, r12);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = $assertionsDisabled;	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        if (r12 != 0) goto L_0x0127;
    L_0x0109:
        r0 = r16;
        r12 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        r12 = r12.get(r4);	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        r13 = java.lang.Thread.currentThread();	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        if (r12 == r13) goto L_0x0127;
    L_0x0117:
        r12 = new java.lang.AssertionError;	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        r12.<init>();	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        throw r12;	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
    L_0x011d:
        r6 = move-exception;
        r2 = r3;
    L_0x011f:
        r0 = r16;
        r12 = r0.nodeLocks;
        unlockAll(r12);
        throw r6;
    L_0x0127:
        r0 = r16;
        r12 = r0.engine;	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        r0 = r16;
        r13 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        r12.update(r4, r3, r13);	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        r12 = 0;
        r0 = r16;
        r1 = r17;
        r0.notify(r1, r8, r12);	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        r0 = r16;
        r12 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        unlock(r12, r4);	 Catch:{ RuntimeException -> 0x011d, Exception -> 0x019c }
        r2 = r3;
        goto L_0x009a;
    L_0x0144:
        r0 = r16;
        r12 = r0.nodeLocks;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        unlock(r12, r4);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r2.highKey();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        if (r12 == 0) goto L_0x018b;
    L_0x0151:
        r0 = r16;
        r12 = r0.comparator;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r13 = r2.highKey();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r17;
        r12 = r12.compare(r0, r13);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        if (r12 <= 0) goto L_0x018b;
    L_0x0161:
        r12 = r2.keys();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r16;
        r1 = r17;
        r10 = r0.findChildren(r1, r12);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
    L_0x016d:
        r12 = r2.keys();	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12.length;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        if (r10 != r12) goto L_0x0041;
    L_0x0174:
        r0 = r2;
        r0 = (org.mapdb.BTreeMap.LeafNode) r0;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r0;
        r4 = r12.next;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r16;
        r12 = r0.engine;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r16;
        r13 = r0.nodeSerializer;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r12 = r12.get(r4, r13);	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r0 = r12;
        r0 = (org.mapdb.BTreeMap.BNode) r0;	 Catch:{ RuntimeException -> 0x019f, Exception -> 0x018e }
        r2 = r0;
        goto L_0x016d;
    L_0x018b:
        r8 = 0;
        goto L_0x009a;
    L_0x018e:
        r6 = move-exception;
    L_0x018f:
        r0 = r16;
        r12 = r0.nodeLocks;
        unlockAll(r12);
        r12 = new java.lang.RuntimeException;
        r12.<init>(r6);
        throw r12;
    L_0x019c:
        r6 = move-exception;
        r2 = r3;
        goto L_0x018f;
    L_0x019f:
        r6 = move-exception;
        goto L_0x011f;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mapdb.BTreeMap.remove2(java.lang.Object, java.lang.Object):V");
    }

    public void clear() {
        Iterator iter = keyIterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }

    protected Entry<K, V> makeEntry(Object key, Object value) {
        if ($assertionsDisabled || !(value instanceof ValRef)) {
            return new SimpleImmutableEntry(key, value);
        }
        throw new AssertionError();
    }

    public boolean isEmpty() {
        return !keyIterator().hasNext() ? true : $assertionsDisabled;
    }

    public int size() {
        long size = sizeLong();
        if (size > 2147483647L) {
            return AdvancedShareActionProvider.WEIGHT_MAX;
        }
        return (int) size;
    }

    public long sizeLong() {
        if (this.counter != null) {
            return this.counter.get();
        }
        long size = 0;
        BTreeIterator iter = new BTreeIterator(this);
        while (iter.hasNext()) {
            iter.advance();
            size++;
        }
        return size;
    }

    public V putIfAbsent(K key, V value) {
        if (key != null && value != null) {
            return put2(key, value, true);
        }
        throw new NullPointerException();
    }

    public boolean remove(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException();
        } else if (value == null || remove2(key, value) == null) {
            return $assertionsDisabled;
        } else {
            return true;
        }
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null || oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        long j = this.rootRecidRef;
        long current = ((Long) this.engine.get(r16, Serializer.LONG)).longValue();
        BNode node = (BNode) this.engine.get(current, this.nodeSerializer);
        while (!node.isLeaf()) {
            current = nextDir((DirNode) node, key);
            node = (BNode) this.engine.get(current, this.nodeSerializer);
        }
        lock(this.nodeLocks, current);
        try {
            LeafNode leaf = (LeafNode) this.engine.get(current, this.nodeSerializer);
            int pos = findChildren(key, leaf.keys);
            LeafNode leaf2 = leaf;
            while (pos == leaf2.keys.length) {
                lock(this.nodeLocks, leaf2.next);
                unlock(this.nodeLocks, current);
                current = leaf2.next;
                leaf = (LeafNode) this.engine.get(current, this.nodeSerializer);
                pos = findChildren(key, leaf.keys);
                leaf2 = leaf;
            }
            boolean ret = $assertionsDisabled;
            if (!(key == null || leaf2.keys[pos] == null)) {
                if (this.comparator.compare(key, leaf2.keys[pos]) == 0) {
                    if (oldValue.equals(valExpand(leaf2.vals[pos - 1]))) {
                        Object[] vals = Arrays.copyOf(leaf2.vals, leaf2.vals.length);
                        notify(key, oldValue, newValue);
                        vals[pos - 1] = newValue;
                        if (this.valsOutsideNodes) {
                            vals[pos - 1] = new ValRef(this.engine.put(newValue, this.valueSerializer));
                        }
                        leaf = new LeafNode(Arrays.copyOf(leaf2.keys, leaf2.keys.length), vals, leaf2.next);
                        if ($assertionsDisabled || this.nodeLocks.get(current) == Thread.currentThread()) {
                            this.engine.update(current, leaf, this.nodeSerializer);
                            ret = true;
                            unlock(this.nodeLocks, current);
                            return ret;
                        }
                        throw new AssertionError();
                    }
                }
            }
            leaf = leaf2;
            unlock(this.nodeLocks, current);
            return ret;
        } catch (RuntimeException e) {
            unlockAll(this.nodeLocks);
            throw e;
        } catch (Exception e2) {
            unlockAll(this.nodeLocks);
            throw new RuntimeException(e2);
        }
    }

    public V replace(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        long current = ((Long) this.engine.get(this.rootRecidRef, Serializer.LONG)).longValue();
        BNode node = (BNode) this.engine.get(current, this.nodeSerializer);
        while (!node.isLeaf()) {
            current = nextDir((DirNode) node, key);
            node = (BNode) this.engine.get(current, this.nodeSerializer);
        }
        lock(this.nodeLocks, current);
        try {
            LeafNode leaf = (LeafNode) this.engine.get(current, this.nodeSerializer);
            int pos = findChildren(key, leaf.keys);
            LeafNode leaf2 = leaf;
            while (true) {
                int length = leaf2.keys.length;
                if (pos != r0) {
                    break;
                }
                lock(this.nodeLocks, leaf2.next);
                unlock(this.nodeLocks, current);
                current = leaf2.next;
                leaf = (LeafNode) this.engine.get(current, this.nodeSerializer);
                pos = findChildren(key, leaf.keys);
                leaf2 = leaf;
            }
            V v = null;
            if (!(key == null || leaf2.keys()[pos] == null)) {
                if (this.comparator.compare(key, leaf2.keys[pos]) == 0) {
                    Object[] vals = Arrays.copyOf(leaf2.vals, leaf2.vals.length);
                    v = valExpand(vals[pos - 1]);
                    notify(key, v, value);
                    vals[pos - 1] = value;
                    if (this.valsOutsideNodes && value != null) {
                        vals[pos - 1] = new ValRef(this.engine.put(value, this.valueSerializer));
                    }
                    leaf = new LeafNode(Arrays.copyOf(leaf2.keys, leaf2.keys.length), vals, leaf2.next);
                    if ($assertionsDisabled || this.nodeLocks.get(current) == Thread.currentThread()) {
                        this.engine.update(current, leaf, this.nodeSerializer);
                        unlock(this.nodeLocks, current);
                        return v;
                    }
                    throw new AssertionError();
                }
            }
            leaf = leaf2;
            unlock(this.nodeLocks, current);
            return v;
        } catch (RuntimeException e) {
            unlockAll(this.nodeLocks);
            throw e;
        } catch (Exception e2) {
            unlockAll(this.nodeLocks);
            throw new RuntimeException(e2);
        }
    }

    public Comparator<? super K> comparator() {
        return this.comparator;
    }

    public Entry<K, V> firstEntry() {
        BNode n = (BNode) this.engine.get(((Long) this.engine.get(this.rootRecidRef, Serializer.LONG)).longValue(), this.nodeSerializer);
        while (!n.isLeaf()) {
            n = (BNode) this.engine.get(n.child()[0], this.nodeSerializer);
        }
        LeafNode l = (LeafNode) n;
        while (l.keys.length == 2) {
            if (l.next == 0) {
                return null;
            }
            l = (LeafNode) this.engine.get(l.next, this.nodeSerializer);
        }
        return makeEntry(l.keys[1], valExpand(l.vals[0]));
    }

    public Entry<K, V> pollFirstEntry() {
        Entry<K, V> e;
        do {
            e = firstEntry();
            if (e == null) {
                break;
            }
        } while (!remove(e.getKey(), e.getValue()));
        return e;
    }

    public Entry<K, V> pollLastEntry() {
        Entry<K, V> e;
        do {
            e = lastEntry();
            if (e == null) {
                break;
            }
        } while (!remove(e.getKey(), e.getValue()));
        return e;
    }

    protected Entry<K, V> findSmaller(K key, boolean inclusive) {
        if (key == null) {
            throw new NullPointerException();
        }
        Entry<K, V> k = findSmallerRecur((BNode) this.engine.get(((Long) this.engine.get(this.rootRecidRef, Serializer.LONG)).longValue(), this.nodeSerializer), key, inclusive);
        if (k == null || k.getValue() == null) {
            return null;
        }
        return k;
    }

    private Entry<K, V> findSmallerRecur(BNode n, K key, boolean inclusive) {
        boolean leaf = n.isLeaf();
        int start = leaf ? n.keys().length - 2 : n.keys().length - 1;
        int end = leaf ? 1 : 0;
        int res = inclusive ? 1 : 0;
        for (int i = start; i >= end; i--) {
            Object key2 = n.keys()[i];
            if ((key2 == null ? -1 : this.comparator.compare(key2, key)) < res) {
                if (!leaf) {
                    long recid = n.child()[i];
                    if (recid == 0) {
                        continue;
                    } else {
                        Entry<K, V> ret = findSmallerRecur((BNode) this.engine.get(recid, this.nodeSerializer), key, inclusive);
                        if (ret != null) {
                            return ret;
                        }
                    }
                } else if (key2 == null) {
                    return null;
                } else {
                    return makeEntry(key2, valExpand(n.vals()[i - 1]));
                }
            }
        }
        return null;
    }

    public Entry<K, V> lastEntry() {
        Entry e = lastEntryRecur((BNode) this.engine.get(((Long) this.engine.get(this.rootRecidRef, Serializer.LONG)).longValue(), this.nodeSerializer));
        if (e == null || e.getValue() != null) {
            return e;
        }
        return null;
    }

    private Entry<K, V> lastEntryRecur(BNode n) {
        Entry<K, V> ret;
        int i;
        if (n.isLeaf()) {
            if (n.next() != 0) {
                ret = lastEntryRecur((BNode) this.engine.get(n.next(), this.nodeSerializer));
                if (ret != null) {
                    return ret;
                }
            }
            for (i = n.keys().length - 2; i > 0; i--) {
                Object k = n.keys()[i];
                if (k != null && n.vals().length > 0) {
                    Object val = valExpand(n.vals()[i - 1]);
                    if (val != null) {
                        return makeEntry(k, val);
                    }
                }
            }
        } else {
            for (i = n.child().length - 1; i >= 0; i--) {
                long childRecid = n.child()[i];
                if (childRecid != 0) {
                    ret = lastEntryRecur((BNode) this.engine.get(childRecid, this.nodeSerializer));
                    if (ret != null) {
                        return ret;
                    }
                }
            }
        }
        return null;
    }

    public Entry<K, V> lowerEntry(K key) {
        if (key != null) {
            return findSmaller(key, $assertionsDisabled);
        }
        throw new NullPointerException();
    }

    public K lowerKey(K key) {
        Entry<K, V> n = lowerEntry(key);
        return n == null ? null : n.getKey();
    }

    public Entry<K, V> floorEntry(K key) {
        if (key != null) {
            return findSmaller(key, true);
        }
        throw new NullPointerException();
    }

    public K floorKey(K key) {
        Entry<K, V> n = floorEntry(key);
        return n == null ? null : n.getKey();
    }

    public Entry<K, V> ceilingEntry(K key) {
        if (key != null) {
            return findLarger(key, true);
        }
        throw new NullPointerException();
    }

    protected Entry<K, V> findLarger(K key, boolean inclusive) {
        if (key == null) {
            return null;
        }
        BNode A = (BNode) this.engine.get(((Long) this.engine.get(this.rootRecidRef, Serializer.LONG)).longValue(), this.nodeSerializer);
        while (!A.isLeaf()) {
            A = (BNode) this.engine.get(nextDir((DirNode) A, key), this.nodeSerializer);
        }
        LeafNode leaf = (LeafNode) A;
        int comp = inclusive ? 1 : 0;
        while (true) {
            int i = 1;
            while (i < leaf.keys.length - 1) {
                if (leaf.keys[i] != null && this.comparator.compare(key, leaf.keys[i]) < comp) {
                    return makeEntry(leaf.keys[i], valExpand(leaf.vals[i - 1]));
                }
                i++;
            }
            if (leaf.next == 0) {
                return null;
            }
            leaf = (LeafNode) this.engine.get(leaf.next, this.nodeSerializer);
        }
    }

    protected Tuple2<Integer, LeafNode> findLargerNode(K key, boolean inclusive) {
        if (key == null) {
            return null;
        }
        BNode A = (BNode) this.engine.get(((Long) this.engine.get(this.rootRecidRef, Serializer.LONG)).longValue(), this.nodeSerializer);
        while (!A.isLeaf()) {
            A = (BNode) this.engine.get(nextDir((DirNode) A, key), this.nodeSerializer);
        }
        LeafNode leaf = (LeafNode) A;
        int comp = inclusive ? 1 : 0;
        while (true) {
            int i = 1;
            while (i < leaf.keys.length - 1) {
                if (leaf.keys[i] != null && this.comparator.compare(key, leaf.keys[i]) < comp) {
                    return Fun.t2(Integer.valueOf(i), leaf);
                }
                i++;
            }
            if (leaf.next == 0) {
                return null;
            }
            leaf = (LeafNode) this.engine.get(leaf.next, this.nodeSerializer);
        }
    }

    public K ceilingKey(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        Entry<K, V> n = ceilingEntry(key);
        return n == null ? null : n.getKey();
    }

    public Entry<K, V> higherEntry(K key) {
        if (key != null) {
            return findLarger(key, $assertionsDisabled);
        }
        throw new NullPointerException();
    }

    public K higherKey(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        Entry<K, V> n = higherEntry(key);
        return n == null ? null : n.getKey();
    }

    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException();
        } else if (get(key, $assertionsDisabled) != null) {
            return true;
        } else {
            return $assertionsDisabled;
        }
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Iterator<V> valueIter = valueIterator();
        while (valueIter.hasNext()) {
            if (value.equals(valueIter.next())) {
                return true;
            }
        }
        return $assertionsDisabled;
    }

    public K firstKey() {
        Entry<K, V> e = firstEntry();
        if (e != null) {
            return e.getKey();
        }
        throw new NoSuchElementException();
    }

    public K lastKey() {
        Entry<K, V> e = lastEntry();
        if (e != null) {
            return e.getKey();
        }
        throw new NoSuchElementException();
    }

    public ConcurrentNavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        if (fromKey != null && toKey != null) {
            return new SubMap(this, fromKey, fromInclusive, toKey, toInclusive);
        }
        throw new NullPointerException();
    }

    public ConcurrentNavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        if (toKey != null) {
            return new SubMap(this, null, $assertionsDisabled, toKey, inclusive);
        }
        throw new NullPointerException();
    }

    public ConcurrentNavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        if (fromKey != null) {
            return new SubMap(this, fromKey, inclusive, null, $assertionsDisabled);
        }
        throw new NullPointerException();
    }

    public ConcurrentNavigableMap<K, V> subMap(K fromKey, K toKey) {
        return subMap((Object) fromKey, true, (Object) toKey, (boolean) $assertionsDisabled);
    }

    public ConcurrentNavigableMap<K, V> headMap(K toKey) {
        return headMap((Object) toKey, (boolean) $assertionsDisabled);
    }

    public ConcurrentNavigableMap<K, V> tailMap(K fromKey) {
        return tailMap((Object) fromKey, true);
    }

    Iterator<K> keyIterator() {
        return new BTreeKeyIterator(this);
    }

    Iterator<V> valueIterator() {
        return new BTreeValueIterator(this);
    }

    Iterator<Entry<K, V>> entryIterator() {
        return new BTreeEntryIterator(this);
    }

    public NavigableSet<K> keySet() {
        return this.keySet;
    }

    public NavigableSet<K> navigableKeySet() {
        return this.keySet;
    }

    public Collection<V> values() {
        return this.values;
    }

    public Set<Entry<K, V>> entrySet() {
        return this.entrySet;
    }

    public ConcurrentNavigableMap<K, V> descendingMap() {
        return this.descendingMap;
    }

    public NavigableSet<K> descendingKeySet() {
        return this.descendingMap.keySet();
    }

    static <E> List<E> toList(Collection<E> c) {
        List<E> list = new ArrayList();
        for (E e : c) {
            list.add(e);
        }
        return list;
    }

    public NavigableMap<K, V> snapshot() {
        return new BTreeMap(TxEngine.createSnapshotFor(this.engine), this.rootRecidRef, this.maxNodeSize, this.valsOutsideNodes, this.counter == null ? 0 : this.counter.recid, this.keySerializer, this.valueSerializer, this.comparator, this.numberOfNodeMetas, $assertionsDisabled);
    }

    public void modificationListenerAdd(MapListener<K, V> listener) {
        synchronized (this.modListenersLock) {
            MapListener[] modListeners2 = (MapListener[]) Arrays.copyOf(this.modListeners, this.modListeners.length + 1);
            modListeners2[modListeners2.length - 1] = listener;
            this.modListeners = modListeners2;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void modificationListenerRemove(org.mapdb.Bind.MapListener<K, V> r5) {
        /*
        r4 = this;
        r2 = r4.modListenersLock;
        monitor-enter(r2);
        r0 = 0;
    L_0x0004:
        r1 = r4.modListeners;	 Catch:{ all -> 0x0019 }
        r1 = r1.length;	 Catch:{ all -> 0x0019 }
        if (r0 >= r1) goto L_0x0017;
    L_0x0009:
        r1 = r4.modListeners;	 Catch:{ all -> 0x0019 }
        r1 = r1[r0];	 Catch:{ all -> 0x0019 }
        if (r1 != r5) goto L_0x0014;
    L_0x000f:
        r1 = r4.modListeners;	 Catch:{ all -> 0x0019 }
        r3 = 0;
        r1[r0] = r3;	 Catch:{ all -> 0x0019 }
    L_0x0014:
        r0 = r0 + 1;
        goto L_0x0004;
    L_0x0017:
        monitor-exit(r2);	 Catch:{ all -> 0x0019 }
        return;
    L_0x0019:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0019 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mapdb.BTreeMap.modificationListenerRemove(org.mapdb.Bind$MapListener):void");
    }

    protected void notify(K key, V oldValue, V newValue) {
        if (!$assertionsDisabled && (oldValue instanceof ValRef)) {
            throw new AssertionError();
        } else if ($assertionsDisabled || !(newValue instanceof ValRef)) {
            for (MapListener<K, V> listener : this.modListeners) {
                if (listener != null) {
                    listener.update(key, oldValue, newValue);
                }
            }
        } else {
            throw new AssertionError();
        }
    }

    public void close() {
        this.engine.close();
    }

    public Engine getEngine() {
        return this.engine;
    }

    public void printTreeStructure() {
        printRecur(this, ((Long) this.engine.get(this.rootRecidRef, Serializer.LONG)).longValue(), Table.STRING_DEFAULT_VALUE);
    }

    private static void printRecur(BTreeMap m, long recid, String s) {
        BNode n = (BNode) m.engine.get(recid, m.nodeSerializer);
        System.out.println(s + recid + "-" + n);
        if (!n.isLeaf()) {
            for (int i = 0; i < n.child().length - 1; i++) {
                long recid2 = n.child()[i];
                if (recid2 != 0) {
                    printRecur(m, recid2, s + "  ");
                }
            }
        }
    }

    protected static long[] arrayLongPut(long[] array, int pos, long value) {
        long[] ret = Arrays.copyOf(array, array.length + 1);
        if (pos < array.length) {
            System.arraycopy(array, pos, ret, pos + 1, array.length - pos);
        }
        ret[pos] = value;
        return ret;
    }

    protected static Object[] arrayPut(Object[] array, int pos, Object value) {
        Object[] ret = Arrays.copyOf(array, array.length + 1);
        if (pos < array.length) {
            System.arraycopy(array, pos, ret, pos + 1, array.length - pos);
        }
        ret[pos] = value;
        return ret;
    }

    protected static void assertNoLocks(LongConcurrentHashMap<Thread> locks) {
        LongMapIterator<Thread> i = locks.longMapIterator();
        Thread t = null;
        while (i.moveToNext()) {
            if (t == null) {
                t = Thread.currentThread();
            }
            if (i.value() == t) {
                throw new AssertionError("Node " + i.key() + " is still locked");
            }
        }
    }

    protected static void unlock(LongConcurrentHashMap<Thread> locks, long recid) {
        Thread t = (Thread) locks.remove(recid);
        if (!$assertionsDisabled && t != Thread.currentThread()) {
            throw new AssertionError("unlocked wrong thread");
        }
    }

    protected static void unlockAll(LongConcurrentHashMap<Thread> locks) {
        Thread t = Thread.currentThread();
        LongMapIterator<Thread> iter = locks.longMapIterator();
        while (iter.moveToNext()) {
            if (iter.value() == t) {
                iter.remove();
            }
        }
    }

    protected static void lock(LongConcurrentHashMap<Thread> locks, long recid) {
        Thread currentThread = Thread.currentThread();
        if ($assertionsDisabled || locks.get(recid) != currentThread) {
            while (locks.putIfAbsent(recid, currentThread) != null) {
                LockSupport.parkNanos(10);
            }
            return;
        }
        throw new AssertionError("node already locked by current thread: " + recid);
    }
}
