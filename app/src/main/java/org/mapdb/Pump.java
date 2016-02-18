package org.mapdb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import org.mapdb.Fun.Function1;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Fun.Tuple2Comparator;

public final class Pump {

    /* renamed from: org.mapdb.Pump.1 */
    static class AnonymousClass1 implements Iterator {
        final /* synthetic */ DataInputStream[] val$ins;
        final /* synthetic */ int val$pos;
        final /* synthetic */ int[] val$presortCount;
        final /* synthetic */ List val$presortFiles;
        final /* synthetic */ Serializer val$serializer;

        AnonymousClass1(int[] iArr, int i, Serializer serializer, DataInputStream[] dataInputStreamArr, List list) {
            this.val$presortCount = iArr;
            this.val$pos = i;
            this.val$serializer = serializer;
            this.val$ins = dataInputStreamArr;
            this.val$presortFiles = list;
        }

        public boolean hasNext() {
            return this.val$presortCount[this.val$pos] > 0;
        }

        public Object next() {
            try {
                Object ret = this.val$serializer.deserialize(this.val$ins[this.val$pos], -1);
                int[] iArr = this.val$presortCount;
                int i = this.val$pos;
                int i2 = iArr[i] - 1;
                iArr[i] = i2;
                if (i2 == 0) {
                    this.val$ins[this.val$pos].close();
                    ((File) this.val$presortFiles.get(this.val$pos)).delete();
                }
                return ret;
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void remove() {
        }
    }

    /* renamed from: org.mapdb.Pump.2 */
    static class AnonymousClass2 implements Iterator<E> {
        final NavigableSet<Tuple2<Object, Integer>> items;
        Object next;
        final /* synthetic */ Comparator val$comparator2;
        final /* synthetic */ Iterator[] val$iterators;
        final /* synthetic */ boolean val$mergeDuplicates;

        AnonymousClass2(Comparator comparator, Iterator[] itArr, boolean z) {
            this.val$comparator2 = comparator;
            this.val$iterators = itArr;
            this.val$mergeDuplicates = z;
            this.items = new TreeSet(new Tuple2Comparator(this.val$comparator2, null));
            this.next = this;
            for (int i = 0; i < this.val$iterators.length; i++) {
                if (this.val$iterators[i].hasNext()) {
                    this.items.add(Fun.t2(this.val$iterators[i].next(), Integer.valueOf(i)));
                }
            }
            next();
        }

        public boolean hasNext() {
            return this.next != null;
        }

        public E next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            AnonymousClass2 oldNext = this.next;
            Tuple2<Object, Integer> lo = (Tuple2) this.items.pollFirst();
            if (lo == null) {
                this.next = null;
            } else {
                this.next = lo.a;
                if (oldNext == this || this.val$comparator2.compare(oldNext, this.next) <= 0) {
                    Iterator iter = this.val$iterators[((Integer) lo.b).intValue()];
                    if (iter.hasNext()) {
                        this.items.add(Fun.t2(iter.next(), lo.b));
                    }
                    if (this.val$mergeDuplicates) {
                        while (true) {
                            Set<Tuple2<Object, Integer>> subset = this.items.subSet(Fun.t2(this.next, null), Fun.t2(this.next, Fun.HI));
                            if (subset.isEmpty()) {
                                break;
                            }
                            List toadd = new ArrayList();
                            for (Tuple2<Object, Integer> t : subset) {
                                iter = this.val$iterators[((Integer) t.b).intValue()];
                                if (iter.hasNext()) {
                                    toadd.add(Fun.t2(iter.next(), t.b));
                                }
                            }
                            subset.clear();
                            this.items.addAll(toadd);
                        }
                    }
                } else {
                    throw new IllegalArgumentException("One of the iterators is not sorted");
                }
            }
            return oldNext;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* renamed from: org.mapdb.Pump.3 */
    static class AnonymousClass3 implements Iterator<E> {
        int i;
        Object next;
        final /* synthetic */ Iterator[] val$iters;

        AnonymousClass3(Iterator[] itArr) {
            this.val$iters = itArr;
            this.i = 0;
            this.next = this;
            next();
        }

        public boolean hasNext() {
            return this.next != null;
        }

        public E next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            Object ret;
            while (!this.val$iters[this.i].hasNext()) {
                this.i++;
                if (this.i == this.val$iters.length) {
                    ret = this.next;
                    this.next = null;
                    return ret;
                }
            }
            ret = this.next;
            this.next = this.val$iters[this.i].next();
            return ret;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* renamed from: org.mapdb.Pump.4 */
    static class AnonymousClass4 implements Iterator<E> {
        int index;
        final /* synthetic */ Object[] val$array;
        final /* synthetic */ int val$fromIndex;
        final /* synthetic */ int val$toIndex;

        AnonymousClass4(int i, int i2, Object[] objArr) {
            this.val$fromIndex = i;
            this.val$toIndex = i2;
            this.val$array = objArr;
            this.index = this.val$fromIndex;
        }

        public boolean hasNext() {
            return this.index < this.val$toIndex;
        }

        public E next() {
            if (this.index >= this.val$toIndex) {
                throw new NoSuchElementException();
            }
            Object[] objArr = this.val$array;
            int i = this.index;
            this.index = i + 1;
            return objArr[i];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static void copy(DB db1, DB db2) {
        copy(Store.forDB(db1), Store.forDB(db2));
        db2.engine.clearCache();
        db2.reinit();
    }

    static void copy(Store s1, Store s2) {
        long maxRecid = s1.getMaxRecid();
        for (long recid = 1; recid <= maxRecid; recid++) {
            ByteBuffer bb = s1.getRaw(recid);
            if (bb != null) {
                s2.updateRaw(recid, bb);
            }
        }
        Iterator<Long> iter = s1.getFreeRecids();
        while (iter.hasNext()) {
            s2.delete(((Long) iter.next()).longValue(), null);
        }
    }

    public static <E> Iterator<E> sort(Iterator<E> source, boolean mergeDuplicates, int batchSize, Comparator comparator, Serializer serializer) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException();
        }
        Iterator<E> arrayIterator;
        if (comparator == null) {
            comparator = BTreeMap.COMPARABLE_COMPARATOR;
        }
        if (source == null) {
            source = Fun.EMPTY_ITERATOR;
        }
        int counter = 0;
        Object[] presort = new Object[batchSize];
        List<File> presortFiles = new ArrayList();
        List<Integer> presortCount2 = new ArrayList();
        File f;
        while (source.hasNext()) {
            try {
                presort[counter] = source.next();
                counter++;
                if (counter >= batchSize) {
                    Arrays.sort(presort, comparator);
                    f = File.createTempFile("mapdb", "sort");
                    f.deleteOnExit();
                    presortFiles.add(f);
                    DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
                    for (Object e : presort) {
                        serializer.serialize(dataOutputStream, e);
                    }
                    dataOutputStream.close();
                    presortCount2.add(Integer.valueOf(counter));
                    Arrays.fill(presort, Integer.valueOf(0));
                    counter = 0;
                }
            } catch (IOException e2) {
                throw new IOError(e2);
            } catch (Throwable th) {
                for (File f2 : presortFiles) {
                    f2.delete();
                }
            }
        }
        if (presortFiles.isEmpty()) {
            Arrays.sort(presort, 0, counter, comparator);
            arrayIterator = arrayIterator(presort, 0, counter);
            for (File f22 : presortFiles) {
                f22.delete();
            }
        } else {
            int i;
            int[] presortCount = new int[presortFiles.size()];
            for (i = 0; i < presortCount.length; i++) {
                presortCount[i] = ((Integer) presortCount2.get(i)).intValue();
            }
            Iterator[] iterators = new Iterator[(presortFiles.size() + 1)];
            DataInputStream[] ins = new DataInputStream[presortFiles.size()];
            for (i = 0; i < presortFiles.size(); i++) {
                ins[i] = new DataInputStream(new BufferedInputStream(new FileInputStream((File) presortFiles.get(i))));
                iterators[i] = new AnonymousClass1(presortCount, i, serializer, ins, presortFiles);
            }
            Arrays.sort(presort, 0, counter, comparator);
            iterators[iterators.length - 1] = arrayIterator(presort, 0, counter);
            arrayIterator = sort(comparator, mergeDuplicates, iterators);
            for (File f222 : presortFiles) {
                f222.delete();
            }
        }
        return arrayIterator;
    }

    public static <E> Iterator<E> sort(Comparator comparator, boolean mergeDuplicates, Iterator... iterators) {
        Comparator comparator2;
        if (comparator == null) {
            comparator2 = BTreeMap.COMPARABLE_COMPARATOR;
        } else {
            comparator2 = comparator;
        }
        return new AnonymousClass2(comparator2, iterators, mergeDuplicates);
    }

    public static <E> Iterator<E> merge(Iterator... iters) {
        if (iters.length == 0) {
            return Fun.EMPTY_ITERATOR;
        }
        return new AnonymousClass3(iters);
    }

    public static <E, K, V> long buildTreeMap(Iterator<E> source, Engine engine, Function1<K, E> keyExtractor, Function1<V, E> valueExtractor, boolean ignoreDuplicates, int nodeSize, boolean valuesStoredOutsideNodes, long counterRecid, BTreeKeySerializer<K> keySerializer, Serializer<V> valueSerializer, Comparator comparator) {
        if (comparator == null) {
            comparator = BTreeMap.COMPARABLE_COMPARATOR;
        }
        Serializer<BNode> nodeSerializer = new NodeSerializer(valuesStoredOutsideNodes, keySerializer, valueSerializer, comparator, 0);
        int nload = (int) (((double) nodeSize) * 0.75d);
        ArrayList<ArrayList<Object>> dirKeys = arrayList(arrayList(null));
        ArrayList<ArrayList<Long>> dirRecids = arrayList(arrayList(Long.valueOf(0)));
        long counter = 0;
        long nextNode = 0;
        List<K> keys = arrayList(null);
        ArrayList<Object> values = new ArrayList();
        K k = null;
        while (source.hasNext()) {
            int i = 0;
            while (i < nload && source.hasNext()) {
                counter++;
                E next = source.next();
                if (next == null) {
                    throw new NullPointerException("source returned null element");
                }
                K key = keyExtractor == null ? next : keyExtractor.run(next);
                int compared = k == null ? -1 : comparator.compare(key, k);
                while (ignoreDuplicates && compared == 0) {
                    if (!source.hasNext()) {
                        break;
                    }
                    next = source.next();
                    if (next == null) {
                        throw new NullPointerException("source returned null element");
                    }
                    key = keyExtractor == null ? next : keyExtractor.run(next);
                    compared = comparator.compare(key, k);
                }
                if (k == null || compared < 0) {
                    k = key;
                    keys.add(key);
                    ValRef run = valueExtractor != null ? valueExtractor.run(next) : BTreeMap.EMPTY;
                    if (run == null) {
                        throw new NullPointerException("extractValue returned null value");
                    }
                    if (valuesStoredOutsideNodes) {
                        ValRef valRef = new ValRef(engine.put(run, valueSerializer));
                    }
                    values.add(run);
                    i++;
                } else {
                    throw new IllegalArgumentException("Keys in 'source' iterator are not reverse sorted");
                }
            }
            if (!source.hasNext()) {
                keys.add(null);
                values.add(null);
            }
            Collections.reverse(keys);
            Object nextVal = values.remove(values.size() - 1);
            Collections.reverse(values);
            LeafNode leafNode = new LeafNode(keys.toArray(), values.toArray(), nextNode);
            nextNode = engine.put(leafNode, nodeSerializer);
            K nextKey = keys.get(0);
            keys.clear();
            keys.add(nextKey);
            keys.add(nextKey);
            values.clear();
            values.add(nextVal);
            ((ArrayList) dirKeys.get(0)).add(leafNode.keys()[0]);
            ((ArrayList) dirRecids.get(0)).add(Long.valueOf(nextNode));
            i = 0;
            while (i < dirKeys.size() && ((ArrayList) dirKeys.get(i)).size() >= nload) {
                Collections.reverse((List) dirKeys.get(i));
                Collections.reverse((List) dirRecids.get(i));
                long dirRecid = engine.put(new DirNode(((ArrayList) dirKeys.get(i)).toArray(), (List) dirRecids.get(i)), nodeSerializer);
                Object dirStart = ((ArrayList) dirKeys.get(i)).get(0);
                ((ArrayList) dirKeys.get(i)).clear();
                ((ArrayList) dirKeys.get(i)).add(dirStart);
                ((ArrayList) dirRecids.get(i)).clear();
                ((ArrayList) dirRecids.get(i)).add(Long.valueOf(dirRecid));
                if (dirKeys.size() == i + 1) {
                    dirKeys.add(arrayList(dirStart));
                    dirRecids.add(arrayList(Long.valueOf(dirRecid)));
                } else {
                    ((ArrayList) dirKeys.get(i + 1)).add(dirStart);
                    ((ArrayList) dirRecids.get(i + 1)).add(Long.valueOf(dirRecid));
                }
                i++;
            }
        }
        for (i = 0; i < dirKeys.size() - 1; i++) {
            ArrayList<Object> keys2 = (ArrayList) dirKeys.get(i);
            Collections.reverse(keys2);
            Collections.reverse((List) dirRecids.get(i));
            if (keys2.size() > 2 && keys2.get(0) == null && keys2.get(1) == null) {
                keys2.remove(0);
                ((ArrayList) dirRecids.get(i)).remove(0);
            }
            dirRecid = engine.put(new DirNode(keys2.toArray(), (List) dirRecids.get(i)), nodeSerializer);
            ((ArrayList) dirKeys.get(i + 1)).add(keys2.get(0));
            ((ArrayList) dirRecids.get(i + 1)).add(Long.valueOf(dirRecid));
        }
        int len = dirKeys.size() - 1;
        Collections.reverse((List) dirKeys.get(len));
        Collections.reverse((List) dirRecids.get(len));
        if (counterRecid != 0) {
            engine.update(counterRecid, Long.valueOf(counter), Serializer.LONG);
        }
        return engine.put(Long.valueOf(engine.put(new DirNode(((ArrayList) dirKeys.get(len)).toArray(), (List) dirRecids.get(len)), nodeSerializer)), Serializer.LONG);
    }

    private static <E> ArrayList<E> arrayList(E item) {
        ArrayList<E> ret = new ArrayList();
        ret.add(item);
        return ret;
    }

    private static <E> Iterator<E> arrayIterator(Object[] array, int fromIndex, int toIndex) {
        return new AnonymousClass4(fromIndex, toIndex, array);
    }
}
