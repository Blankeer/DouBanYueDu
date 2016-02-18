package org.mapdb;

import android.support.v4.media.TransportMediator;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.mapdb.Atomic.Long;
import org.mapdb.Bind.MapListener;
import org.mapdb.Bind.MapWithModificationListener;
import org.mapdb.Fun.Function1;

public class HTreeMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, MapWithModificationListener<K, V> {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected static final int BUCKET_OVERFLOW = 4;
    protected static final Serializer<long[][]> DIR_SERIALIZER;
    protected static final int DIV8 = 3;
    protected static final int MOD8 = 7;
    protected final Serializer<LinkedNode<K, V>> LN_SERIALIZER;
    private final Set<Entry<K, V>> _entrySet;
    private final Set<K> _keySet;
    private final Collection<V> _values;
    protected final CountDownLatch closeLatch;
    protected final Runnable closeListener;
    protected final Long counter;
    protected final Engine engine;
    protected final long expire;
    protected final long expireAccess;
    protected final boolean expireAccessFlag;
    protected final boolean expireFlag;
    protected final long[] expireHeads;
    protected final long expireMaxSize;
    protected final boolean expireMaxSizeFlag;
    protected final long expireStoreSize;
    protected final long[] expireTails;
    protected final long expireTimeStart;
    protected final boolean hasValues;
    protected final int hashSalt;
    protected final Hasher<K> hasher;
    protected final Serializer<K> keySerializer;
    protected MapListener<K, V>[] modListeners;
    protected final Object modListenersLock;
    protected final ReentrantReadWriteLock[] segmentLocks;
    protected final long[] segmentRecids;
    protected final Function1<V, K> valueCreator;
    protected final Serializer<V> valueSerializer;

    class Entry2 implements Entry<K, V> {
        private final K key;

        Entry2(K key) {
            this.key = key;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return HTreeMap.this.get(this.key);
        }

        public V setValue(V value) {
            return HTreeMap.this.put(this.key, value);
        }

        public boolean equals(Object o) {
            return ((o instanceof Entry) && HTreeMap.this.hasher.equals(this.key, ((Entry) o).getKey())) ? true : HTreeMap.$assertionsDisabled;
        }

        public int hashCode() {
            int i = 0;
            V value = HTreeMap.this.get(this.key);
            int hashCode = this.key == null ? 0 : HTreeMap.this.hasher.hashCode(this.key);
            if (value != null) {
                i = value.hashCode();
            }
            return i ^ hashCode;
        }
    }

    protected static final class ExpireLinkNode {
        public static final ExpireLinkNode EMPTY;
        public static final Serializer<ExpireLinkNode> SERIALIZER;
        public final int hash;
        public final long keyRecid;
        public final long next;
        public final long prev;
        public final long time;

        static {
            EMPTY = new ExpireLinkNode(0, 0, 0, 0, 0);
            SERIALIZER = new Serializer<ExpireLinkNode>() {
                public void serialize(DataOutput out, ExpireLinkNode value) throws IOException {
                    if (value != ExpireLinkNode.EMPTY) {
                        DataOutput2.packLong(out, value.prev);
                        DataOutput2.packLong(out, value.next);
                        DataOutput2.packLong(out, value.keyRecid);
                        DataOutput2.packLong(out, value.time);
                        out.writeInt(value.hash);
                    }
                }

                public ExpireLinkNode deserialize(DataInput in, int available) throws IOException {
                    if (available == 0) {
                        return ExpireLinkNode.EMPTY;
                    }
                    return new ExpireLinkNode(DataInput2.unpackLong(in), DataInput2.unpackLong(in), DataInput2.unpackLong(in), DataInput2.unpackLong(in), in.readInt());
                }

                public int fixedSize() {
                    return -1;
                }
            };
        }

        public ExpireLinkNode(long prev, long next, long keyRecid, long time, int hash) {
            this.prev = prev;
            this.next = next;
            this.keyRecid = keyRecid;
            this.time = time;
            this.hash = hash;
        }

        public ExpireLinkNode copyNext(long next2) {
            return new ExpireLinkNode(this.prev, next2, this.keyRecid, this.time, this.hash);
        }

        public ExpireLinkNode copyPrev(long prev2) {
            return new ExpireLinkNode(prev2, this.next, this.keyRecid, this.time, this.hash);
        }

        public ExpireLinkNode copyTime(long time2) {
            return new ExpireLinkNode(this.prev, this.next, this.keyRecid, time2, this.hash);
        }
    }

    protected static class ExpireRunnable implements Runnable {
        final WeakReference<HTreeMap> mapRef;

        public ExpireRunnable(HTreeMap map) {
            this.mapRef = new WeakReference(map);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r10 = this;
            r8 = 2;
            r3 = 0;
        L_0x0003:
            if (r3 == 0) goto L_0x000a;
        L_0x0005:
            r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            java.lang.Thread.sleep(r4);	 Catch:{ Throwable -> 0x009c }
        L_0x000a:
            r4 = r10.mapRef;	 Catch:{ Throwable -> 0x009c }
            r2 = r4.get();	 Catch:{ Throwable -> 0x009c }
            r2 = (org.mapdb.HTreeMap) r2;	 Catch:{ Throwable -> 0x009c }
            if (r2 == 0) goto L_0x0026;
        L_0x0014:
            r4 = r2.engine;	 Catch:{ Throwable -> 0x009c }
            r4 = r4.isClosed();	 Catch:{ Throwable -> 0x009c }
            if (r4 != 0) goto L_0x0026;
        L_0x001c:
            r4 = r2.closeLatch;	 Catch:{ Throwable -> 0x009c }
            r4 = r4.getCount();	 Catch:{ Throwable -> 0x009c }
            r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
            if (r4 >= 0) goto L_0x003b;
        L_0x0026:
            r4 = r10.mapRef;
            r1 = r4.get();
            r1 = (org.mapdb.HTreeMap) r1;
            if (r1 == 0) goto L_0x0035;
        L_0x0030:
            r4 = r1.closeLatch;
            r4.countDown();
        L_0x0035:
            r4 = r10.mapRef;
            r4.clear();
        L_0x003a:
            return;
        L_0x003b:
            r2.expirePurge();	 Catch:{ Throwable -> 0x009c }
            r4 = r2.engine;	 Catch:{ Throwable -> 0x009c }
            r4 = r4.isClosed();	 Catch:{ Throwable -> 0x009c }
            if (r4 != 0) goto L_0x0050;
        L_0x0046:
            r4 = r2.closeLatch;	 Catch:{ Throwable -> 0x009c }
            r4 = r4.getCount();	 Catch:{ Throwable -> 0x009c }
            r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
            if (r4 >= 0) goto L_0x0065;
        L_0x0050:
            r4 = r10.mapRef;
            r1 = r4.get();
            r1 = (org.mapdb.HTreeMap) r1;
            if (r1 == 0) goto L_0x005f;
        L_0x005a:
            r4 = r1.closeLatch;
            r4.countDown();
        L_0x005f:
            r4 = r10.mapRef;
            r4.clear();
            goto L_0x003a;
        L_0x0065:
            r4 = r2.expireMaxSizeFlag;	 Catch:{ Throwable -> 0x009c }
            if (r4 == 0) goto L_0x0074;
        L_0x0069:
            r4 = r2.size();	 Catch:{ Throwable -> 0x009c }
            r4 = (long) r4;	 Catch:{ Throwable -> 0x009c }
            r6 = r2.expireMaxSize;	 Catch:{ Throwable -> 0x009c }
            r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r4 >= 0) goto L_0x009a;
        L_0x0074:
            r4 = r2.expireStoreSize;	 Catch:{ Throwable -> 0x009c }
            r6 = 0;
            r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r4 == 0) goto L_0x0097;
        L_0x007c:
            r4 = r2.engine;	 Catch:{ Throwable -> 0x009c }
            r4 = org.mapdb.Store.forEngine(r4);	 Catch:{ Throwable -> 0x009c }
            r4 = r4.getCurrSize();	 Catch:{ Throwable -> 0x009c }
            r6 = r2.engine;	 Catch:{ Throwable -> 0x009c }
            r6 = org.mapdb.Store.forEngine(r6);	 Catch:{ Throwable -> 0x009c }
            r6 = r6.getFreeSize();	 Catch:{ Throwable -> 0x009c }
            r4 = r4 - r6;
            r6 = r2.expireStoreSize;	 Catch:{ Throwable -> 0x009c }
            r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r4 >= 0) goto L_0x009a;
        L_0x0097:
            r3 = 1;
        L_0x0098:
            goto L_0x0003;
        L_0x009a:
            r3 = 0;
            goto L_0x0098;
        L_0x009c:
            r0 = move-exception;
            r0.printStackTrace();	 Catch:{ all -> 0x00b5 }
            r4 = r10.mapRef;
            r1 = r4.get();
            r1 = (org.mapdb.HTreeMap) r1;
            if (r1 == 0) goto L_0x00af;
        L_0x00aa:
            r4 = r1.closeLatch;
            r4.countDown();
        L_0x00af:
            r4 = r10.mapRef;
            r4.clear();
            goto L_0x003a;
        L_0x00b5:
            r4 = move-exception;
            r5 = r10.mapRef;
            r1 = r5.get();
            r1 = (org.mapdb.HTreeMap) r1;
            if (r1 == 0) goto L_0x00c5;
        L_0x00c0:
            r5 = r1.closeLatch;
            r5.countDown();
        L_0x00c5:
            r5 = r10.mapRef;
            r5.clear();
            throw r4;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.mapdb.HTreeMap.ExpireRunnable.run():void");
        }
    }

    abstract class HashIterator {
        static final /* synthetic */ boolean $assertionsDisabled;
        protected LinkedNode[] currentLinkedList;
        protected int currentLinkedListPos;
        private K lastReturnedKey;
        private int lastSegment;

        static {
            $assertionsDisabled = !HTreeMap.class.desiredAssertionStatus() ? true : HTreeMap.$assertionsDisabled;
        }

        HashIterator() {
            this.currentLinkedListPos = 0;
            this.lastReturnedKey = null;
            this.lastSegment = 0;
            this.currentLinkedList = findNextLinkedNode(0);
        }

        public void remove() {
            K keyToRemove = this.lastReturnedKey;
            if (this.lastReturnedKey == null) {
                throw new IllegalStateException();
            }
            this.lastReturnedKey = null;
            HTreeMap.this.remove(keyToRemove);
        }

        public boolean hasNext() {
            return (this.currentLinkedList == null || this.currentLinkedListPos >= this.currentLinkedList.length) ? HTreeMap.$assertionsDisabled : true;
        }

        protected void moveToNext() {
            this.lastReturnedKey = this.currentLinkedList[this.currentLinkedListPos].key;
            this.currentLinkedListPos++;
            if (this.currentLinkedListPos == this.currentLinkedList.length) {
                this.currentLinkedList = advance(HTreeMap.this.hash(this.lastReturnedKey));
                this.currentLinkedListPos = 0;
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private org.mapdb.HTreeMap.LinkedNode[] advance(int r13) {
            /*
            r12 = this;
            r10 = 1;
            r5 = r13 >>> 28;
            r6 = org.mapdb.HTreeMap.this;	 Catch:{ all -> 0x008b }
            r6 = r6.segmentLocks;	 Catch:{ all -> 0x008b }
            r6 = r6[r5];	 Catch:{ all -> 0x008b }
            r6 = r6.readLock();	 Catch:{ all -> 0x008b }
            r6.lock();	 Catch:{ all -> 0x008b }
            r6 = org.mapdb.HTreeMap.this;	 Catch:{ all -> 0x008b }
            r6 = r6.segmentRecids;	 Catch:{ all -> 0x008b }
            r2 = r6[r5];	 Catch:{ all -> 0x008b }
            r1 = 3;
        L_0x0018:
            r6 = org.mapdb.HTreeMap.this;	 Catch:{ all -> 0x008b }
            r6 = r6.engine;	 Catch:{ all -> 0x008b }
            r7 = org.mapdb.HTreeMap.DIR_SERIALIZER;	 Catch:{ all -> 0x008b }
            r0 = r6.get(r2, r7);	 Catch:{ all -> 0x008b }
            r0 = (long[][]) r0;	 Catch:{ all -> 0x008b }
            r6 = r1 * 7;
            r6 = r13 >>> r6;
            r4 = r6 & 127;
            r6 = r4 >>> 3;
            r6 = r0[r6];	 Catch:{ all -> 0x008b }
            if (r6 == 0) goto L_0x004b;
        L_0x0030:
            r6 = r4 >>> 3;
            r6 = r0[r6];	 Catch:{ all -> 0x008b }
            r7 = r4 & 7;
            r6 = r6[r7];	 Catch:{ all -> 0x008b }
            r8 = 0;
            r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r6 == 0) goto L_0x004b;
        L_0x003e:
            r6 = r4 >>> 3;
            r6 = r0[r6];	 Catch:{ all -> 0x008b }
            r7 = r4 & 7;
            r6 = r6[r7];	 Catch:{ all -> 0x008b }
            r6 = r6 & r10;
            r6 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
            if (r6 != 0) goto L_0x006b;
        L_0x004b:
            if (r1 == 0) goto L_0x0068;
        L_0x004d:
            r6 = r1 * 7;
            r6 = r13 >>> r6;
            r6 = r6 + 1;
            r7 = r1 * 7;
            r13 = r6 << r7;
        L_0x0057:
            if (r13 != 0) goto L_0x0079;
        L_0x0059:
            r6 = 0;
            r7 = org.mapdb.HTreeMap.this;
            r7 = r7.segmentLocks;
            r7 = r7[r5];
            r7 = r7.readLock();
            r7.unlock();
        L_0x0067:
            return r6;
        L_0x0068:
            r13 = r13 + 1;
            goto L_0x0057;
        L_0x006b:
            r6 = r4 >>> 3;
            r6 = r0[r6];	 Catch:{ all -> 0x008b }
            r7 = r4 & 7;
            r6 = r6[r7];	 Catch:{ all -> 0x008b }
            r8 = 1;
            r2 = r6 >>> r8;
            r1 = r1 + -1;
            goto L_0x0018;
        L_0x0079:
            r6 = org.mapdb.HTreeMap.this;
            r6 = r6.segmentLocks;
            r6 = r6[r5];
            r6 = r6.readLock();
            r6.unlock();
            r6 = r12.findNextLinkedNode(r13);
            goto L_0x0067;
        L_0x008b:
            r6 = move-exception;
            r7 = org.mapdb.HTreeMap.this;
            r7 = r7.segmentLocks;
            r7 = r7[r5];
            r7 = r7.readLock();
            r7.unlock();
            throw r6;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.mapdb.HTreeMap.HashIterator.advance(int):org.mapdb.HTreeMap$LinkedNode[]");
        }

        private LinkedNode[] findNextLinkedNode(int hash) {
            int segment = Math.max(hash >>> 28, this.lastSegment);
            while (segment < 16) {
                Lock lock = HTreeMap.this.expireAccessFlag ? HTreeMap.this.segmentLocks[segment].writeLock() : HTreeMap.this.segmentLocks[segment].readLock();
                lock.lock();
                try {
                    int len$;
                    int i$;
                    LinkedNode ln;
                    this.lastSegment = Math.max(segment, this.lastSegment);
                    LinkedNode[] ret = findNextLinkedNodeRecur(HTreeMap.this.segmentRecids[segment], hash, HTreeMap.DIV8);
                    if (ret != null) {
                        LinkedNode[] arr$ = ret;
                        len$ = arr$.length;
                        i$ = 0;
                        while (i$ < len$) {
                            ln = arr$[i$];
                            if ($assertionsDisabled || (HTreeMap.this.hash(ln.key) >>> 28) == segment) {
                                i$++;
                            } else {
                                throw new AssertionError();
                            }
                        }
                    }
                    if (ret != null) {
                        if (HTreeMap.this.expireAccessFlag) {
                            for (LinkedNode ln2 : ret) {
                                HTreeMap.this.expireLinkBump(segment, ln2.expireLinkNodeRecid, true);
                            }
                        }
                        lock.unlock();
                        return ret;
                    }
                    hash = 0;
                    lock.unlock();
                    segment++;
                } catch (Throwable th) {
                    lock.unlock();
                }
            }
            return null;
        }

        private LinkedNode[] findNextLinkedNodeRecur(long dirRecid, int newHash, int level) {
            long[][] dir = (long[][]) HTreeMap.this.engine.get(dirRecid, HTreeMap.DIR_SERIALIZER);
            if (dir == null) {
                return null;
            }
            boolean first = true;
            for (int pos = (newHash >>> (level * HTreeMap.MOD8)) & TransportMediator.KEYCODE_MEDIA_PAUSE; pos < TransportMediator.FLAG_KEY_MEDIA_NEXT; pos++) {
                if (dir[pos >>> HTreeMap.DIV8] != null) {
                    long recid = dir[pos >>> HTreeMap.DIV8][pos & HTreeMap.MOD8];
                    if (recid == 0) {
                        continue;
                    } else if ((1 & recid) == 1) {
                        recid >>= 1;
                        LinkedNode[] array = new LinkedNode[1];
                        int arrayPos = 0;
                        while (recid != 0) {
                            LinkedNode ln = (LinkedNode) HTreeMap.this.engine.get(recid, HTreeMap.this.LN_SERIALIZER);
                            if (ln == null) {
                                recid = 0;
                            } else {
                                if (arrayPos == array.length) {
                                    array = (LinkedNode[]) Arrays.copyOf(array, array.length + 1);
                                }
                                int arrayPos2 = arrayPos + 1;
                                array[arrayPos] = ln;
                                recid = ln.next;
                                arrayPos = arrayPos2;
                            }
                        }
                        return array;
                    } else {
                        LinkedNode[] ret = findNextLinkedNodeRecur(recid >> 1, first ? newHash : 0, level - 1);
                        if (ret != null) {
                            return ret;
                        }
                    }
                }
                first = HTreeMap.$assertionsDisabled;
            }
            return null;
        }
    }

    protected class KeySet extends AbstractSet<K> {
        protected KeySet() {
        }

        public int size() {
            return HTreeMap.this.size();
        }

        public boolean isEmpty() {
            return HTreeMap.this.isEmpty();
        }

        public boolean contains(Object o) {
            return HTreeMap.this.containsKey(o);
        }

        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        public boolean add(K k) {
            if (!HTreeMap.this.hasValues) {
                return HTreeMap.this.put(k, BTreeMap.EMPTY) == null ? true : HTreeMap.$assertionsDisabled;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        public boolean remove(Object o) {
            return HTreeMap.this.remove(o) != null ? true : HTreeMap.$assertionsDisabled;
        }

        public void clear() {
            HTreeMap.this.clear();
        }

        public HTreeMap<K, V> parent() {
            return HTreeMap.this;
        }

        public int hashCode() {
            int result = 0;
            Iterator i$ = iterator();
            while (i$.hasNext()) {
                result += HTreeMap.this.hasher.hashCode(i$.next());
            }
            return result;
        }
    }

    protected static final class LinkedNode<K, V> {
        public final long expireLinkNodeRecid;
        public final K key;
        public final long next;
        public final V value;

        public LinkedNode(long next, long expireLinkNodeRecid, K key, V value) {
            this.key = key;
            this.expireLinkNodeRecid = expireLinkNodeRecid;
            this.value = value;
            this.next = next;
        }
    }

    class EntryIterator extends HashIterator implements Iterator<Entry<K, V>> {
        EntryIterator() {
            super();
        }

        public Entry<K, V> next() {
            if (this.currentLinkedList == null) {
                throw new NoSuchElementException();
            }
            K key = this.currentLinkedList[this.currentLinkedListPos].key;
            moveToNext();
            return new Entry2(key);
        }
    }

    class KeyIterator extends HashIterator implements Iterator<K> {
        KeyIterator() {
            super();
        }

        public K next() {
            if (this.currentLinkedList == null) {
                throw new NoSuchElementException();
            }
            K key = this.currentLinkedList[this.currentLinkedListPos].key;
            moveToNext();
            return key;
        }
    }

    class ValueIterator extends HashIterator implements Iterator<V> {
        ValueIterator() {
            super();
        }

        public V next() {
            if (this.currentLinkedList == null) {
                throw new NoSuchElementException();
            }
            V value = this.currentLinkedList[this.currentLinkedListPos].value;
            moveToNext();
            return value;
        }
    }

    public long sizeLong() {
        /* JADX: method processing error */
/*
        Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ssa.SSATransform.placePhi(SSATransform.java:82)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JavaClass.getMethods(JavaClass.java:188)
*/
        /*
        r8 = this;
        r5 = r8.counter;
        if (r5 == 0) goto L_0x000b;
    L_0x0004:
        r5 = r8.counter;
        r0 = r5.get();
    L_0x000a:
        return r0;
    L_0x000b:
        r0 = 0;
        r4 = 0;
    L_0x000e:
        r5 = 16;
        if (r4 >= r5) goto L_0x000a;
    L_0x0012:
        r5 = r8.segmentLocks;	 Catch:{ all -> 0x0034 }
        r5 = r5[r4];	 Catch:{ all -> 0x0034 }
        r5 = r5.readLock();	 Catch:{ all -> 0x0034 }
        r5.lock();	 Catch:{ all -> 0x0034 }
        r5 = r8.segmentRecids;	 Catch:{ all -> 0x0034 }
        r2 = r5[r4];	 Catch:{ all -> 0x0034 }
        r6 = r8.recursiveDirCount(r2);	 Catch:{ all -> 0x0034 }
        r0 = r0 + r6;
        r5 = r8.segmentLocks;
        r5 = r5[r4];
        r5 = r5.readLock();
        r5.unlock();
        r4 = r4 + 1;
        goto L_0x000e;
    L_0x0034:
        r5 = move-exception;
        r6 = r8.segmentLocks;
        r6 = r6[r4];
        r6 = r6.readLock();
        r6.unlock();
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mapdb.HTreeMap.sizeLong():long");
    }

    static {
        boolean z;
        if (HTreeMap.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        DIR_SERIALIZER = new Serializer<long[][]>() {
            static final /* synthetic */ boolean $assertionsDisabled;

            static {
                $assertionsDisabled = !HTreeMap.class.desiredAssertionStatus() ? true : HTreeMap.$assertionsDisabled;
            }

            public void serialize(DataOutput out, long[][] value) throws IOException {
                if ($assertionsDisabled || value.length == 16) {
                    int i;
                    int nulls = 0;
                    for (i = 0; i < 16; i++) {
                        if (value[i] != null) {
                            for (long l : value[i]) {
                                if (l != 0) {
                                    nulls |= 1 << i;
                                    break;
                                }
                            }
                        }
                    }
                    out.writeShort(nulls);
                    i = 0;
                    while (i < 16) {
                        if (value[i] != null) {
                            if ($assertionsDisabled || value[i].length == 8) {
                                for (long l2 : value[i]) {
                                    DataOutput2.packLong(out, l2);
                                }
                            } else {
                                throw new AssertionError();
                            }
                        }
                        i++;
                    }
                    return;
                }
                throw new AssertionError();
            }

            public long[][] deserialize(DataInput in, int available) throws IOException {
                long[][] ret = new long[16][];
                int nulls = in.readUnsignedShort();
                for (int i = 0; i < 16; i++) {
                    if ((nulls & 1) != 0) {
                        long[] subarray = new long[8];
                        for (int j = 0; j < 8; j++) {
                            subarray[j] = DataInput2.unpackLong(in);
                        }
                        ret[i] = subarray;
                    }
                    nulls >>>= 1;
                }
                return ret;
            }

            public int fixedSize() {
                return -1;
            }
        };
    }

    public HTreeMap(Engine engine, long counterRecid, int hashSalt, long[] segmentRecids, Serializer<K> keySerializer, Serializer<V> valueSerializer, long expireTimeStart, long expire, long expireAccess, long expireMaxSize, long expireStoreSize, long[] expireHeads, long[] expireTails, Function1<V, K> valueCreator, Hasher hasher, boolean disableLocks) {
        this.closeLatch = new CountDownLatch(2);
        this.closeListener = new Runnable() {
            public void run() {
                if (HTreeMap.this.closeLatch.getCount() > 1) {
                    HTreeMap.this.closeLatch.countDown();
                }
                try {
                    HTreeMap.this.closeLatch.await();
                    HTreeMap.this.engine.closeListenerUnregister(HTreeMap.this.closeListener);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        this.LN_SERIALIZER = new Serializer<LinkedNode<K, V>>() {
            static final /* synthetic */ boolean $assertionsDisabled;

            static {
                $assertionsDisabled = !HTreeMap.class.desiredAssertionStatus() ? true : HTreeMap.$assertionsDisabled;
            }

            public void serialize(DataOutput out, LinkedNode<K, V> value) throws IOException {
                DataOutput2.packLong(out, value.next);
                if (HTreeMap.this.expireFlag) {
                    DataOutput2.packLong(out, value.expireLinkNodeRecid);
                }
                HTreeMap.this.keySerializer.serialize(out, value.key);
                if (HTreeMap.this.hasValues) {
                    HTreeMap.this.valueSerializer.serialize(out, value.value);
                }
            }

            public LinkedNode<K, V> deserialize(DataInput in, int available) throws IOException {
                if ($assertionsDisabled || available != 0) {
                    return new LinkedNode(DataInput2.unpackLong(in), HTreeMap.this.expireFlag ? DataInput2.unpackLong(in) : 0, HTreeMap.this.keySerializer.deserialize(in, -1), HTreeMap.this.hasValues ? HTreeMap.this.valueSerializer.deserialize(in, -1) : BTreeMap.EMPTY);
                }
                throw new AssertionError();
            }

            public int fixedSize() {
                return -1;
            }
        };
        this.segmentLocks = new ReentrantReadWriteLock[16];
        for (int i = 0; i < 16; i++) {
            this.segmentLocks[i] = new ReentrantReadWriteLock($assertionsDisabled);
        }
        this._keySet = new KeySet();
        this._values = new AbstractCollection<V>() {
            public int size() {
                return HTreeMap.this.size();
            }

            public boolean isEmpty() {
                return HTreeMap.this.isEmpty();
            }

            public boolean contains(Object o) {
                return HTreeMap.this.containsValue(o);
            }

            public Iterator<V> iterator() {
                return new ValueIterator();
            }
        };
        this._entrySet = new AbstractSet<Entry<K, V>>() {
            public int size() {
                return HTreeMap.this.size();
            }

            public boolean isEmpty() {
                return HTreeMap.this.isEmpty();
            }

            public boolean contains(Object o) {
                if (!(o instanceof Entry)) {
                    return HTreeMap.$assertionsDisabled;
                }
                Entry e = (Entry) o;
                Object val = HTreeMap.this.get(e.getKey());
                if (val == null || !val.equals(e.getValue())) {
                    return HTreeMap.$assertionsDisabled;
                }
                return true;
            }

            public Iterator<Entry<K, V>> iterator() {
                return new EntryIterator();
            }

            public boolean add(Entry<K, V> kvEntry) {
                K key = kvEntry.getKey();
                V value = kvEntry.getValue();
                if (key == null || value == null) {
                    throw new NullPointerException();
                }
                HTreeMap.this.put(key, value);
                return true;
            }

            public boolean remove(Object o) {
                if (!(o instanceof Entry)) {
                    return HTreeMap.$assertionsDisabled;
                }
                Entry e = (Entry) o;
                Object key = e.getKey();
                if (key == null) {
                    return HTreeMap.$assertionsDisabled;
                }
                return HTreeMap.this.remove(key, e.getValue());
            }

            public void clear() {
                HTreeMap.this.clear();
            }
        };
        this.modListenersLock = new Object();
        this.modListeners = new MapListener[0];
        if (counterRecid < 0) {
            throw new IllegalArgumentException();
        } else if (engine == null) {
            throw new NullPointerException();
        } else if (segmentRecids == null) {
            throw new NullPointerException();
        } else if (keySerializer == null) {
            throw new NullPointerException();
        } else {
            SerializerBase.assertSerializable(keySerializer);
            this.hasValues = valueSerializer != null ? true : $assertionsDisabled;
            if (this.hasValues) {
                SerializerBase.assertSerializable(valueSerializer);
            }
            if (segmentRecids.length != 16) {
                throw new IllegalArgumentException();
            }
            this.engine = engine;
            this.hashSalt = hashSalt;
            this.segmentRecids = Arrays.copyOf(segmentRecids, 16);
            this.keySerializer = keySerializer;
            this.valueSerializer = valueSerializer;
            if (hasher == null) {
                hasher = Hasher.BASIC;
            }
            this.hasher = hasher;
            if (expire == 0 && expireAccess != 0) {
                expire = expireAccess;
            }
            if (expireMaxSize == 0 || counterRecid != 0) {
                boolean z = (expire == 0 && expireAccess == 0 && expireMaxSize == 0 && expireStoreSize == 0) ? $assertionsDisabled : true;
                this.expireFlag = z;
                this.expire = expire;
                this.expireTimeStart = expireTimeStart;
                z = (expireAccess == 0 && expireMaxSize == 0 && expireStoreSize == 0) ? $assertionsDisabled : true;
                this.expireAccessFlag = z;
                this.expireAccess = expireAccess;
                this.expireHeads = expireHeads == null ? null : Arrays.copyOf(expireHeads, 16);
                this.expireTails = expireTails == null ? null : Arrays.copyOf(expireTails, 16);
                this.expireMaxSizeFlag = expireMaxSize != 0 ? true : $assertionsDisabled;
                this.expireMaxSize = expireMaxSize;
                this.expireStoreSize = expireStoreSize;
                this.valueCreator = valueCreator;
                if (counterRecid != 0) {
                    this.counter = new Long(engine, counterRecid);
                    Bind.size(this, this.counter);
                } else {
                    this.counter = null;
                }
                if (this.expireFlag) {
                    Thread t = new Thread(new ExpireRunnable(this), "HTreeMap expirator");
                    t.setDaemon(true);
                    t.start();
                    engine.closeListenerRegister(this.closeListener);
                    return;
                }
                return;
            }
            throw new IllegalArgumentException("expireMaxSize must have counter enabled");
        }
    }

    protected static long[] preallocateSegments(Engine engine) {
        long[] ret = new long[16];
        for (int i = 0; i < 16; i++) {
            ret[i] = engine.put(new long[16][], DIR_SERIALIZER);
        }
        return ret;
    }

    public boolean containsKey(Object o) {
        return getPeek(o) != null ? true : $assertionsDisabled;
    }

    public int size() {
        long size = sizeLong();
        if (size > 2147483647L) {
            return AdvancedShareActionProvider.WEIGHT_MAX;
        }
        return (int) size;
    }

    private long recursiveDirCount(long dirRecid) {
        long counter = 0;
        for (long[] subdir : (long[][]) this.engine.get(dirRecid, DIR_SERIALIZER)) {
            if (subdir != null) {
                for (long recid : subdir) {
                    long recid2;
                    if (recid2 != 0) {
                        if ((1 & recid2) == 0) {
                            counter += recursiveDirCount(recid2 >>> 1);
                        } else {
                            recid2 >>>= 1;
                            while (recid2 != 0) {
                                LinkedNode n = (LinkedNode) this.engine.get(recid2, this.LN_SERIALIZER);
                                if (n != null) {
                                    counter++;
                                    recid2 = n.next;
                                } else {
                                    recid2 = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
        return counter;
    }

    public boolean isEmpty() {
        int i = 0;
        while (i < 16) {
            try {
                this.segmentLocks[i].readLock().lock();
                for (long[] d : (long[][]) this.engine.get(this.segmentRecids[i], DIR_SERIALIZER)) {
                    if (d != null) {
                        return $assertionsDisabled;
                    }
                }
                this.segmentLocks[i].readLock().unlock();
                i++;
            } finally {
                this.segmentLocks[i].readLock().unlock();
            }
        }
        return true;
    }

    public V get(Object o) {
        if (o == null) {
            return null;
        }
        int h = hash(o);
        int segment = h >>> 28;
        Lock lock = this.expireAccessFlag ? this.segmentLocks[segment].writeLock() : this.segmentLocks[segment].readLock();
        lock.lock();
        try {
            LinkedNode<K, V> ln = getInner(o, h, segment);
            if (ln != null && this.expireAccessFlag) {
                expireLinkBump(segment, ln.expireLinkNodeRecid, true);
            }
            lock.unlock();
            if (this.valueCreator != null) {
                V value = this.valueCreator.run(o);
                V prevVal = putIfAbsent(o, value);
                if (prevVal == null) {
                    return value;
                }
                return prevVal;
            } else if (ln != null) {
                return ln.value;
            } else {
                return null;
            }
        } catch (Throwable th) {
            lock.unlock();
        }
    }

    public V getPeek(Object key) {
        V v = null;
        if (key != null) {
            int h = hash(key);
            int segment = h >>> 28;
            Lock lock = this.segmentLocks[segment].readLock();
            lock.lock();
            try {
                LinkedNode<K, V> ln = getInner(key, h, segment);
                if (ln != null) {
                    v = ln.value;
                    lock.unlock();
                }
            } finally {
                lock.unlock();
            }
        }
        return v;
    }

    protected LinkedNode<K, V> getInner(Object o, int h, int segment) {
        long recid = this.segmentRecids[segment];
        int level = DIV8;
        while (level >= 0) {
            long[][] dir = (long[][]) this.engine.get(recid, DIR_SERIALIZER);
            if (dir == null) {
                return null;
            }
            int slot = (h >>> (level * MOD8)) & TransportMediator.KEYCODE_MEDIA_PAUSE;
            if (!$assertionsDisabled && slot >= TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                throw new AssertionError();
            } else if (dir[slot >>> DIV8] == null) {
                return null;
            } else {
                recid = dir[slot >>> DIV8][slot & MOD8];
                if (recid == 0) {
                    return null;
                }
                if ((1 & recid) != 0) {
                    LinkedNode<K, V> ln;
                    recid >>>= 1;
                    while (true) {
                        ln = (LinkedNode) this.engine.get(recid, this.LN_SERIALIZER);
                        if (ln == null) {
                            return null;
                        }
                        if (this.hasher.equals(ln.key, o)) {
                            break;
                        } else if (ln.next == 0) {
                            return null;
                        } else {
                            recid = ln.next;
                        }
                    }
                    if ($assertionsDisabled || hash(ln.key) == h) {
                        return ln;
                    }
                    throw new AssertionError();
                }
                recid >>>= 1;
                level--;
            }
        }
        return null;
    }

    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        } else if (value == null) {
            throw new IllegalArgumentException("null value");
        } else {
            int h = hash(key);
            int segment = h >>> 28;
            this.segmentLocks[segment].writeLock().lock();
            try {
                V putInner = putInner(key, value, h, segment);
                return putInner;
            } finally {
                this.segmentLocks[segment].writeLock().unlock();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private V putInner(K r44, V r45, int r46, int r47) {
        /*
        r43 = this;
        r0 = r43;
        r6 = r0.segmentRecids;
        r26 = r6[r47];
        r25 = 3;
    L_0x0008:
        r0 = r43;
        r6 = r0.engine;
        r8 = DIR_SERIALIZER;
        r0 = r26;
        r24 = r6.get(r0, r8);
        r24 = (long[][]) r24;
        r6 = r25 * 7;
        r6 = r46 >>> r6;
        r42 = r6 & 127;
        r6 = $assertionsDisabled;
        if (r6 != 0) goto L_0x002c;
    L_0x0020:
        r6 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        r0 = r42;
        if (r0 <= r6) goto L_0x002c;
    L_0x0026:
        r6 = new java.lang.AssertionError;
        r6.<init>();
        throw r6;
    L_0x002c:
        if (r24 != 0) goto L_0x0034;
    L_0x002e:
        r6 = 16;
        r0 = new long[r6][];
        r24 = r0;
    L_0x0034:
        r6 = r42 >>> 3;
        r6 = r24[r6];
        if (r6 != 0) goto L_0x004c;
    L_0x003a:
        r6 = 16;
        r0 = r24;
        r24 = java.util.Arrays.copyOf(r0, r6);
        r24 = (long[][]) r24;
        r6 = r42 >>> 3;
        r8 = 8;
        r8 = new long[r8];
        r24[r6] = r8;
    L_0x004c:
        r4 = 0;
        r6 = r42 >>> 3;
        r6 = r24[r6];
        r8 = r42 & 7;
        r18 = r6[r8];
        r8 = 0;
        r6 = (r18 > r8 ? 1 : (r18 == r8 ? 0 : -1));
        if (r6 == 0) goto L_0x00f8;
    L_0x005b:
        r8 = 1;
        r8 = r8 & r18;
        r16 = 0;
        r6 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r6 != 0) goto L_0x006b;
    L_0x0065:
        r6 = 1;
        r26 = r18 >>> r6;
        r25 = r25 + -1;
        goto L_0x0008;
    L_0x006b:
        r6 = 1;
        r18 = r18 >>> r6;
        r0 = r43;
        r6 = r0.engine;
        r0 = r43;
        r8 = r0.LN_SERIALIZER;
        r0 = r18;
        r5 = r6.get(r0, r8);
        r5 = (org.mapdb.HTreeMap.LinkedNode) r5;
        r28 = r5;
    L_0x0080:
        if (r28 == 0) goto L_0x00f8;
    L_0x0082:
        r0 = r43;
        r6 = r0.hasher;
        r0 = r28;
        r8 = r0.key;
        r0 = r44;
        r6 = r6.equals(r8, r0);
        if (r6 == 0) goto L_0x00d4;
    L_0x0092:
        r0 = r28;
        r0 = r0.value;
        r31 = r0;
        r5 = new org.mapdb.HTreeMap$LinkedNode;
        r0 = r28;
        r6 = r0.next;
        r0 = r28;
        r8 = r0.expireLinkNodeRecid;
        r0 = r28;
        r10 = r0.key;
        r11 = r45;
        r5.<init>(r6, r8, r10, r11);
        r0 = r43;
        r6 = r0.engine;
        r0 = r43;
        r8 = r0.LN_SERIALIZER;
        r0 = r18;
        r6.update(r0, r5, r8);
        r0 = r43;
        r6 = r0.expireFlag;
        if (r6 == 0) goto L_0x00c8;
    L_0x00be:
        r8 = r5.expireLinkNodeRecid;
        r6 = 0;
        r0 = r43;
        r1 = r47;
        r0.expireLinkBump(r1, r8, r6);
    L_0x00c8:
        r0 = r43;
        r1 = r44;
        r2 = r31;
        r3 = r45;
        r0.notify(r1, r2, r3);
    L_0x00d3:
        return r31;
    L_0x00d4:
        r0 = r28;
        r0 = r0.next;
        r18 = r0;
        r8 = 0;
        r6 = (r18 > r8 ? 1 : (r18 == r8 ? 0 : -1));
        if (r6 != 0) goto L_0x00e6;
    L_0x00e0:
        r5 = 0;
    L_0x00e1:
        r4 = r4 + 1;
        r28 = r5;
        goto L_0x0080;
    L_0x00e6:
        r0 = r43;
        r6 = r0.engine;
        r0 = r43;
        r8 = r0.LN_SERIALIZER;
        r0 = r18;
        r6 = r6.get(r0, r8);
        r6 = (org.mapdb.HTreeMap.LinkedNode) r6;
        r5 = r6;
        goto L_0x00e1;
    L_0x00f8:
        r40 = r18;
        r6 = 4;
        if (r4 < r6) goto L_0x023c;
    L_0x00fd:
        r6 = 1;
        r0 = r25;
        if (r0 < r6) goto L_0x023c;
    L_0x0102:
        r6 = 16;
        r0 = new long[r6][];
        r30 = r0;
        r0 = r43;
        r6 = r0.expireFlag;
        if (r6 == 0) goto L_0x01e2;
    L_0x010e:
        r0 = r43;
        r6 = r0.engine;
        r10 = r6.preallocate();
    L_0x0116:
        r7 = new org.mapdb.HTreeMap$LinkedNode;
        r8 = 0;
        r12 = r44;
        r13 = r45;
        r7.<init>(r8, r10, r12, r13);
        r0 = r43;
        r6 = r0.engine;
        r0 = r43;
        r8 = r0.LN_SERIALIZER;
        r12 = r6.put(r7, r8);
        r6 = r25 + -1;
        r6 = r6 * 7;
        r6 = r46 >>> r6;
        r39 = r6 & 127;
        r6 = r39 >>> 3;
        r8 = 8;
        r8 = new long[r8];
        r30[r6] = r8;
        r6 = r39 >>> 3;
        r6 = r30[r6];
        r8 = r39 & 7;
        r9 = 1;
        r16 = r12 << r9;
        r20 = 1;
        r16 = r16 | r20;
        r6[r8] = r16;
        r0 = r43;
        r6 = r0.expireFlag;
        if (r6 == 0) goto L_0x015b;
    L_0x0152:
        r8 = r43;
        r9 = r47;
        r14 = r46;
        r8.expireLinkAdd(r9, r10, r12, r14);
    L_0x015b:
        r6 = r42 >>> 3;
        r6 = r24[r6];
        r8 = r42 & 7;
        r8 = r6[r8];
        r6 = 1;
        r36 = r8 >>> r6;
    L_0x0166:
        r8 = 0;
        r6 = (r36 > r8 ? 1 : (r36 == r8 ? 0 : -1));
        if (r6 == 0) goto L_0x01e6;
    L_0x016c:
        r0 = r43;
        r6 = r0.engine;
        r0 = r43;
        r8 = r0.LN_SERIALIZER;
        r0 = r36;
        r29 = r6.get(r0, r8);
        r29 = (org.mapdb.HTreeMap.LinkedNode) r29;
        r0 = r29;
        r0 = r0.next;
        r34 = r0;
        r0 = r29;
        r6 = r0.key;
        r0 = r43;
        r6 = r0.hash(r6);
        r8 = r25 + -1;
        r8 = r8 * 7;
        r6 = r6 >>> r8;
        r39 = r6 & 127;
        r6 = r39 >>> 3;
        r6 = r30[r6];
        if (r6 != 0) goto L_0x01a1;
    L_0x0199:
        r6 = r39 >>> 3;
        r8 = 8;
        r8 = new long[r8];
        r30[r6] = r8;
    L_0x01a1:
        r15 = new org.mapdb.HTreeMap$LinkedNode;
        r6 = r39 >>> 3;
        r6 = r30[r6];
        r8 = r39 & 7;
        r8 = r6[r8];
        r6 = 1;
        r16 = r8 >>> r6;
        r0 = r29;
        r0 = r0.expireLinkNodeRecid;
        r18 = r0;
        r0 = r29;
        r0 = r0.key;
        r20 = r0;
        r0 = r29;
        r0 = r0.value;
        r21 = r0;
        r15.<init>(r16, r18, r20, r21);
        r6 = r39 >>> 3;
        r6 = r30[r6];
        r8 = r39 & 7;
        r9 = 1;
        r16 = r36 << r9;
        r20 = 1;
        r16 = r16 | r20;
        r6[r8] = r16;
        r0 = r43;
        r6 = r0.engine;
        r0 = r43;
        r8 = r0.LN_SERIALIZER;
        r0 = r36;
        r6.update(r0, r15, r8);
        r36 = r34;
        goto L_0x0166;
    L_0x01e2:
        r10 = 0;
        goto L_0x0116;
    L_0x01e6:
        r0 = r43;
        r6 = r0.engine;
        r8 = DIR_SERIALIZER;
        r0 = r30;
        r32 = r6.put(r0, r8);
        r6 = r25 * 7;
        r6 = r46 >>> r6;
        r38 = r6 & 127;
        r6 = 16;
        r0 = r24;
        r24 = java.util.Arrays.copyOf(r0, r6);
        r24 = (long[][]) r24;
        r6 = r38 >>> 3;
        r8 = r38 >>> 3;
        r8 = r24[r8];
        r9 = 8;
        r8 = java.util.Arrays.copyOf(r8, r9);
        r24[r6] = r8;
        r6 = r38 >>> 3;
        r6 = r24[r6];
        r8 = r38 & 7;
        r9 = 1;
        r16 = r32 << r9;
        r20 = 0;
        r16 = r16 | r20;
        r6[r8] = r16;
        r0 = r43;
        r6 = r0.engine;
        r8 = DIR_SERIALIZER;
        r0 = r26;
        r2 = r24;
        r6.update(r0, r2, r8);
        r6 = 0;
        r0 = r43;
        r1 = r44;
        r2 = r45;
        r0.notify(r1, r6, r2);
        r31 = 0;
        r18 = r40;
        goto L_0x00d3;
    L_0x023c:
        r6 = r42 >>> 3;
        r6 = r24[r6];
        r8 = r42 & 7;
        r8 = r6[r8];
        r6 = 1;
        r18 = r8 >>> r6;
        r0 = r43;
        r6 = r0.expireFlag;
        if (r6 == 0) goto L_0x02c3;
    L_0x024d:
        r0 = r43;
        r6 = r0.engine;
        r8 = org.mapdb.HTreeMap.ExpireLinkNode.EMPTY;
        r9 = org.mapdb.HTreeMap.ExpireLinkNode.SERIALIZER;
        r10 = r6.put(r8, r9);
    L_0x0259:
        r0 = r43;
        r6 = r0.engine;
        r17 = new org.mapdb.HTreeMap$LinkedNode;
        r20 = r10;
        r22 = r44;
        r23 = r45;
        r17.<init>(r18, r20, r22, r23);
        r0 = r43;
        r8 = r0.LN_SERIALIZER;
        r0 = r17;
        r12 = r6.put(r0, r8);
        r6 = 16;
        r0 = r24;
        r24 = java.util.Arrays.copyOf(r0, r6);
        r24 = (long[][]) r24;
        r6 = r42 >>> 3;
        r8 = r42 >>> 3;
        r8 = r24[r8];
        r9 = 8;
        r8 = java.util.Arrays.copyOf(r8, r9);
        r24[r6] = r8;
        r6 = r42 >>> 3;
        r6 = r24[r6];
        r8 = r42 & 7;
        r9 = 1;
        r16 = r12 << r9;
        r20 = 1;
        r16 = r16 | r20;
        r6[r8] = r16;
        r0 = r43;
        r6 = r0.engine;
        r8 = DIR_SERIALIZER;
        r0 = r26;
        r2 = r24;
        r6.update(r0, r2, r8);
        r0 = r43;
        r6 = r0.expireFlag;
        if (r6 == 0) goto L_0x02b5;
    L_0x02ac:
        r8 = r43;
        r9 = r47;
        r14 = r46;
        r8.expireLinkAdd(r9, r10, r12, r14);
    L_0x02b5:
        r6 = 0;
        r0 = r43;
        r1 = r44;
        r2 = r45;
        r0.notify(r1, r6, r2);
        r31 = 0;
        goto L_0x00d3;
    L_0x02c3:
        r10 = 0;
        goto L_0x0259;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mapdb.HTreeMap.putInner(java.lang.Object, java.lang.Object, int, int):V");
    }

    public V remove(Object key) {
        int h = hash(key);
        int segment = h >>> 28;
        this.segmentLocks[segment].writeLock().lock();
        try {
            V removeInternal = removeInternal(key, segment, h, true);
            return removeInternal;
        } finally {
            this.segmentLocks[segment].writeLock().unlock();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected V removeInternal(java.lang.Object r23, int r24, int r25, boolean r26) {
        /*
        r22 = this;
        r2 = 4;
        r5 = new long[r2];
        r4 = 3;
        r0 = r22;
        r2 = r0.segmentRecids;
        r2 = r2[r24];
        r5[r4] = r2;
        r2 = $assertionsDisabled;
        if (r2 != 0) goto L_0x0057;
    L_0x0010:
        r2 = r25 >>> 28;
        r0 = r24;
        if (r0 == r2) goto L_0x0057;
    L_0x0016:
        r2 = new java.lang.AssertionError;
        r2.<init>();
        throw r2;
    L_0x001c:
        if (r6 != 0) goto L_0x0022;
    L_0x001e:
        r2 = 16;
        r6 = new long[r2][];
    L_0x0022:
        r2 = r7 >>> 3;
        r2 = r6[r2];
        if (r2 != 0) goto L_0x0038;
    L_0x0028:
        r2 = 16;
        r6 = java.util.Arrays.copyOf(r6, r2);
        r6 = (long[][]) r6;
        r2 = r7 >>> 3;
        r3 = 8;
        r3 = new long[r3];
        r6[r2] = r3;
    L_0x0038:
        r2 = r7 >>> 3;
        r2 = r6[r2];
        r3 = r7 & 7;
        r20 = r2[r3];
        r2 = 0;
        r2 = (r20 > r2 ? 1 : (r20 == r2 ? 0 : -1));
        if (r2 == 0) goto L_0x016f;
    L_0x0046:
        r2 = 1;
        r2 = r2 & r20;
        r10 = 0;
        r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r2 != 0) goto L_0x0079;
    L_0x0050:
        r4 = r4 + -1;
        r2 = 1;
        r2 = r20 >>> r2;
        r5[r4] = r2;
    L_0x0057:
        r0 = r22;
        r2 = r0.engine;
        r10 = r5[r4];
        r3 = DIR_SERIALIZER;
        r6 = r2.get(r10, r3);
        r6 = (long[][]) r6;
        r2 = r4 * 7;
        r2 = r25 >>> r2;
        r7 = r2 & 127;
        r2 = $assertionsDisabled;
        if (r2 != 0) goto L_0x001c;
    L_0x006f:
        r2 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        if (r7 <= r2) goto L_0x001c;
    L_0x0073:
        r2 = new java.lang.AssertionError;
        r2.<init>();
        throw r2;
    L_0x0079:
        r2 = 1;
        r20 = r20 >>> r2;
        r0 = r22;
        r2 = r0.engine;
        r0 = r22;
        r3 = r0.LN_SERIALIZER;
        r0 = r20;
        r8 = r2.get(r0, r3);
        r8 = (org.mapdb.HTreeMap.LinkedNode) r8;
        r9 = 0;
        r18 = 0;
        r16 = r9;
    L_0x0091:
        if (r8 == 0) goto L_0x016d;
    L_0x0093:
        r0 = r22;
        r2 = r0.hasher;
        r3 = r8.key;
        r0 = r23;
        r2 = r2.equals(r3, r0);
        if (r2 == 0) goto L_0x0149;
    L_0x00a1:
        if (r16 != 0) goto L_0x00fd;
    L_0x00a3:
        r2 = r8.next;
        r10 = 0;
        r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r2 != 0) goto L_0x00ca;
    L_0x00ab:
        r2 = r22;
        r3 = r25;
        r2.recursiveDirDelete(r3, r4, r5, r6, r7);
        r9 = r16;
    L_0x00b4:
        r2 = $assertionsDisabled;
        if (r2 != 0) goto L_0x011e;
    L_0x00b8:
        r2 = r8.key;
        r0 = r22;
        r2 = r0.hash(r2);
        r0 = r25;
        if (r2 == r0) goto L_0x011e;
    L_0x00c4:
        r2 = new java.lang.AssertionError;
        r2.<init>();
        throw r2;
    L_0x00ca:
        r2 = 16;
        r6 = java.util.Arrays.copyOf(r6, r2);
        r6 = (long[][]) r6;
        r2 = r7 >>> 3;
        r3 = r7 >>> 3;
        r3 = r6[r3];
        r10 = 8;
        r3 = java.util.Arrays.copyOf(r3, r10);
        r6[r2] = r3;
        r2 = r7 >>> 3;
        r2 = r6[r2];
        r3 = r7 & 7;
        r10 = r8.next;
        r12 = 1;
        r10 = r10 << r12;
        r12 = 1;
        r10 = r10 | r12;
        r2[r3] = r10;
        r0 = r22;
        r2 = r0.engine;
        r10 = r5[r4];
        r3 = DIR_SERIALIZER;
        r2.update(r10, r6, r3);
        r9 = r16;
        goto L_0x00b4;
    L_0x00fd:
        r9 = new org.mapdb.HTreeMap$LinkedNode;
        r10 = r8.next;
        r0 = r16;
        r12 = r0.expireLinkNodeRecid;
        r0 = r16;
        r14 = r0.key;
        r0 = r16;
        r15 = r0.value;
        r9.<init>(r10, r12, r14, r15);
        r0 = r22;
        r2 = r0.engine;
        r0 = r22;
        r3 = r0.LN_SERIALIZER;
        r0 = r18;
        r2.update(r0, r9, r3);
        goto L_0x00b4;
    L_0x011e:
        r0 = r22;
        r2 = r0.engine;
        r0 = r22;
        r3 = r0.LN_SERIALIZER;
        r0 = r20;
        r2.delete(r0, r3);
        if (r26 == 0) goto L_0x013c;
    L_0x012d:
        r0 = r22;
        r2 = r0.expireFlag;
        if (r2 == 0) goto L_0x013c;
    L_0x0133:
        r2 = r8.expireLinkNodeRecid;
        r0 = r22;
        r1 = r24;
        r0.expireLinkRemove(r1, r2);
    L_0x013c:
        r2 = r8.value;
        r3 = 0;
        r0 = r22;
        r1 = r23;
        r0.notify(r1, r2, r3);
        r2 = r8.value;
    L_0x0148:
        return r2;
    L_0x0149:
        r18 = r20;
        r9 = r8;
        r0 = r8.next;
        r20 = r0;
        r2 = 0;
        r2 = (r20 > r2 ? 1 : (r20 == r2 ? 0 : -1));
        if (r2 != 0) goto L_0x015b;
    L_0x0156:
        r8 = 0;
    L_0x0157:
        r16 = r9;
        goto L_0x0091;
    L_0x015b:
        r0 = r22;
        r2 = r0.engine;
        r0 = r22;
        r3 = r0.LN_SERIALIZER;
        r0 = r20;
        r2 = r2.get(r0, r3);
        r2 = (org.mapdb.HTreeMap.LinkedNode) r2;
        r8 = r2;
        goto L_0x0157;
    L_0x016d:
        r2 = 0;
        goto L_0x0148;
    L_0x016f:
        r2 = 0;
        goto L_0x0148;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mapdb.HTreeMap.removeInternal(java.lang.Object, int, int, boolean):V");
    }

    private void recursiveDirDelete(int h, int level, long[] dirRecids, long[][] dir, int slot) {
        Object dir2 = (long[][]) Arrays.copyOf(dir, 16);
        dir2[slot >>> DIV8] = Arrays.copyOf(dir2[slot >>> DIV8], 8);
        dir2[slot >>> DIV8][slot & MOD8] = 0;
        boolean allZero = true;
        for (long l : dir2[slot >>> DIV8]) {
            if (l != 0) {
                allZero = $assertionsDisabled;
                break;
            }
        }
        if (allZero) {
            dir2[slot >>> DIV8] = null;
        }
        allZero = true;
        for (long[] l2 : dir2) {
            if (l2 != null) {
                allZero = $assertionsDisabled;
                break;
            }
        }
        if (!allZero) {
            this.engine.update(dirRecids[level], dir2, DIR_SERIALIZER);
        } else if (level == DIV8) {
            this.engine.update(dirRecids[level], new long[16][], DIR_SERIALIZER);
        } else {
            this.engine.delete(dirRecids[level], DIR_SERIALIZER);
            recursiveDirDelete(h, level + 1, dirRecids, (long[][]) this.engine.get(dirRecids[level + 1], DIR_SERIALIZER), (h >>> ((level + 1) * MOD8)) & TransportMediator.KEYCODE_MEDIA_PAUSE);
        }
    }

    public void clear() {
        int i = 0;
        while (i < 16) {
            try {
                this.segmentLocks[i].writeLock().lock();
                long dirRecid = this.segmentRecids[i];
                recursiveDirClear(dirRecid);
                this.engine.update(dirRecid, new long[16][], DIR_SERIALIZER);
                if (this.expireFlag) {
                    do {
                    } while (expireLinkRemoveLast(i) != null);
                }
                this.segmentLocks[i].writeLock().unlock();
                i++;
            } catch (Throwable th) {
                this.segmentLocks[i].writeLock().unlock();
            }
        }
    }

    private void recursiveDirClear(long dirRecid) {
        long[][] dir = (long[][]) this.engine.get(dirRecid, DIR_SERIALIZER);
        if (dir != null) {
            for (long[] subdir : dir) {
                if (subdir != null) {
                    for (long recid : subdir) {
                        long recid2;
                        if (recid2 != 0) {
                            if ((1 & recid2) == 0) {
                                recid2 >>>= 1;
                                recursiveDirClear(recid2);
                                this.engine.delete(recid2, DIR_SERIALIZER);
                            } else {
                                recid2 >>>= 1;
                                while (recid2 != 0) {
                                    LinkedNode n = (LinkedNode) this.engine.get(recid2, this.LN_SERIALIZER);
                                    this.engine.delete(recid2, this.LN_SERIALIZER);
                                    notify(n.key, n.value, null);
                                    recid2 = n.next;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean containsValue(Object value) {
        for (V v : values()) {
            if (v.equals(value)) {
                return true;
            }
        }
        return $assertionsDisabled;
    }

    public Set<K> keySet() {
        return this._keySet;
    }

    public Collection<V> values() {
        return this._values;
    }

    public Set<Entry<K, V>> entrySet() {
        return this._entrySet;
    }

    protected int hash(Object key) {
        int h = this.hasher.hashCode(key) ^ this.hashSalt;
        h ^= (h >>> 20) ^ (h >>> 12);
        return ((h >>> MOD8) ^ h) ^ (h >>> BUCKET_OVERFLOW);
    }

    public V putIfAbsent(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        int h = hash(key);
        int segment = h >>> 28;
        try {
            V put;
            this.segmentLocks[segment].writeLock().lock();
            LinkedNode<K, V> ln = getInner(key, h, segment);
            if (ln == null) {
                put = put(key, value);
            } else {
                put = ln.value;
                this.segmentLocks[segment].writeLock().unlock();
            }
            return put;
        } finally {
            this.segmentLocks[segment].writeLock().unlock();
        }
    }

    public boolean remove(Object key, Object value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        int h = hash(key);
        int segment = h >>> 28;
        try {
            this.segmentLocks[segment].writeLock().lock();
            LinkedNode otherVal = getInner(key, h, segment);
            if (otherVal == null || !otherVal.value.equals(value)) {
                this.segmentLocks[segment].writeLock().unlock();
                return $assertionsDisabled;
            }
            removeInternal(key, segment, h, true);
            return true;
        } finally {
            this.segmentLocks[segment].writeLock().unlock();
        }
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null || oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        int h = hash(key);
        int segment = h >>> 28;
        try {
            this.segmentLocks[segment].writeLock().lock();
            LinkedNode<K, V> ln = getInner(key, h, segment);
            if (ln == null || !ln.value.equals(oldValue)) {
                this.segmentLocks[segment].writeLock().unlock();
                return $assertionsDisabled;
            }
            putInner(key, newValue, h, segment);
            return true;
        } finally {
            this.segmentLocks[segment].writeLock().unlock();
        }
    }

    public V replace(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        int h = hash(key);
        int segment = h >>> 28;
        try {
            this.segmentLocks[segment].writeLock().lock();
            if (getInner(key, h, segment) != null) {
                V putInner = putInner(key, value, h, segment);
                return putInner;
            }
            this.segmentLocks[segment].writeLock().unlock();
            return null;
        } finally {
            this.segmentLocks[segment].writeLock().unlock();
        }
    }

    protected void expireLinkAdd(int segment, long expireNodeRecid, long keyRecid, int hash) {
        if (!$assertionsDisabled && !this.segmentLocks[segment].writeLock().isHeldByCurrentThread()) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && expireNodeRecid <= 0) {
            throw new AssertionError();
        } else if ($assertionsDisabled || keyRecid > 0) {
            long time = this.expire == 0 ? 0 : (this.expire + System.currentTimeMillis()) - this.expireTimeStart;
            long head = ((Long) this.engine.get(this.expireHeads[segment], Serializer.LONG)).longValue();
            if (head == 0) {
                this.engine.update(expireNodeRecid, new ExpireLinkNode(0, 0, keyRecid, time, hash), ExpireLinkNode.SERIALIZER);
                this.engine.update(this.expireHeads[segment], Long.valueOf(expireNodeRecid), Serializer.LONG);
                this.engine.update(this.expireTails[segment], Long.valueOf(expireNodeRecid), Serializer.LONG);
                return;
            }
            this.engine.update(expireNodeRecid, new ExpireLinkNode(head, 0, keyRecid, time, hash), ExpireLinkNode.SERIALIZER);
            this.engine.update(head, ((ExpireLinkNode) this.engine.get(head, ExpireLinkNode.SERIALIZER)).copyNext(expireNodeRecid), ExpireLinkNode.SERIALIZER);
            this.engine.update(this.expireHeads[segment], Long.valueOf(expireNodeRecid), Serializer.LONG);
        } else {
            throw new AssertionError();
        }
    }

    protected void expireLinkBump(int segment, long nodeRecid, boolean access) {
        if ($assertionsDisabled || this.segmentLocks[segment].writeLock().isHeldByCurrentThread()) {
            ExpireLinkNode n = (ExpireLinkNode) this.engine.get(nodeRecid, ExpireLinkNode.SERIALIZER);
            long newTime = access ? this.expireAccess == 0 ? n.time : (this.expireAccess + System.currentTimeMillis()) - this.expireTimeStart : this.expire == 0 ? n.time : (this.expire + System.currentTimeMillis()) - this.expireTimeStart;
            if (n.next == 0) {
                this.engine.update(nodeRecid, n.copyTime(newTime), ExpireLinkNode.SERIALIZER);
                return;
            }
            if (n.prev != 0) {
                this.engine.update(n.prev, ((ExpireLinkNode) this.engine.get(n.prev, ExpireLinkNode.SERIALIZER)).copyNext(n.next), ExpireLinkNode.SERIALIZER);
            } else {
                this.engine.update(this.expireTails[segment], Long.valueOf(n.next), Serializer.LONG);
            }
            this.engine.update(n.next, ((ExpireLinkNode) this.engine.get(n.next, ExpireLinkNode.SERIALIZER)).copyPrev(n.prev), ExpireLinkNode.SERIALIZER);
            long oldHeadRecid = ((Long) this.engine.get(this.expireHeads[segment], Serializer.LONG)).longValue();
            this.engine.update(oldHeadRecid, ((ExpireLinkNode) this.engine.get(oldHeadRecid, ExpireLinkNode.SERIALIZER)).copyNext(nodeRecid), ExpireLinkNode.SERIALIZER);
            this.engine.update(this.expireHeads[segment], Long.valueOf(nodeRecid), Serializer.LONG);
            this.engine.update(nodeRecid, new ExpireLinkNode(oldHeadRecid, 0, n.keyRecid, newTime, n.hash), ExpireLinkNode.SERIALIZER);
            return;
        }
        throw new AssertionError();
    }

    protected ExpireLinkNode expireLinkRemoveLast(int segment) {
        if ($assertionsDisabled || this.segmentLocks[segment].writeLock().isHeldByCurrentThread()) {
            long tail = ((Long) this.engine.get(this.expireTails[segment], Serializer.LONG)).longValue();
            if (tail == 0) {
                return null;
            }
            ExpireLinkNode n = (ExpireLinkNode) this.engine.get(tail, ExpireLinkNode.SERIALIZER);
            if (n.next == 0) {
                this.engine.update(this.expireHeads[segment], Long.valueOf(0), Serializer.LONG);
                this.engine.update(this.expireTails[segment], Long.valueOf(0), Serializer.LONG);
            } else {
                this.engine.update(this.expireTails[segment], Long.valueOf(n.next), Serializer.LONG);
                this.engine.update(n.next, ((ExpireLinkNode) this.engine.get(n.next, ExpireLinkNode.SERIALIZER)).copyPrev(0), ExpireLinkNode.SERIALIZER);
            }
            this.engine.delete(tail, ExpireLinkNode.SERIALIZER);
            return n;
        }
        throw new AssertionError();
    }

    protected ExpireLinkNode expireLinkRemove(int segment, long nodeRecid) {
        if ($assertionsDisabled || this.segmentLocks[segment].writeLock().isHeldByCurrentThread()) {
            ExpireLinkNode n = (ExpireLinkNode) this.engine.get(nodeRecid, ExpireLinkNode.SERIALIZER);
            this.engine.delete(nodeRecid, ExpireLinkNode.SERIALIZER);
            if (n.next == 0 && n.prev == 0) {
                this.engine.update(this.expireHeads[segment], Long.valueOf(0), Serializer.LONG);
                this.engine.update(this.expireTails[segment], Long.valueOf(0), Serializer.LONG);
            } else if (n.next == 0) {
                this.engine.update(n.prev, ((ExpireLinkNode) this.engine.get(n.prev, ExpireLinkNode.SERIALIZER)).copyNext(0), ExpireLinkNode.SERIALIZER);
                this.engine.update(this.expireHeads[segment], Long.valueOf(n.prev), Serializer.LONG);
            } else if (n.prev == 0) {
                this.engine.update(n.next, ((ExpireLinkNode) this.engine.get(n.next, ExpireLinkNode.SERIALIZER)).copyPrev(0), ExpireLinkNode.SERIALIZER);
                this.engine.update(this.expireTails[segment], Long.valueOf(n.next), Serializer.LONG);
            } else {
                this.engine.update(n.next, ((ExpireLinkNode) this.engine.get(n.next, ExpireLinkNode.SERIALIZER)).copyPrev(n.prev), ExpireLinkNode.SERIALIZER);
                this.engine.update(n.prev, ((ExpireLinkNode) this.engine.get(n.prev, ExpireLinkNode.SERIALIZER)).copyNext(n.next), ExpireLinkNode.SERIALIZER);
            }
            return n;
        }
        throw new AssertionError();
    }

    public long getMaxExpireTime() {
        if (!this.expireFlag) {
            return 0;
        }
        long ret = 0;
        int segment = 0;
        while (segment < 16) {
            this.segmentLocks[segment].readLock().lock();
            long head = ((Long) this.engine.get(this.expireHeads[segment], Serializer.LONG)).longValue();
            if (head == 0) {
                segment++;
            } else {
                ExpireLinkNode ln = (ExpireLinkNode) this.engine.get(head, ExpireLinkNode.SERIALIZER);
                if (ln == null || ln.time == 0) {
                    this.segmentLocks[segment].readLock().unlock();
                    segment++;
                } else {
                    try {
                        ret = Math.max(ret, ln.time + this.expireTimeStart);
                        this.segmentLocks[segment].readLock().unlock();
                        segment++;
                    } finally {
                        this.segmentLocks[segment].readLock().unlock();
                    }
                }
            }
        }
        return ret;
    }

    public long getMinExpireTime() {
        if (!this.expireFlag) {
            return 0;
        }
        long ret = Long.MAX_VALUE;
        int segment = 0;
        while (segment < 16) {
            this.segmentLocks[segment].readLock().lock();
            long tail = ((Long) this.engine.get(this.expireTails[segment], Serializer.LONG)).longValue();
            if (tail == 0) {
                segment++;
            } else {
                try {
                    ExpireLinkNode ln = (ExpireLinkNode) this.engine.get(tail, ExpireLinkNode.SERIALIZER);
                    if (ln == null || ln.time == 0) {
                        this.segmentLocks[segment].readLock().unlock();
                        segment++;
                    } else {
                        ret = Math.min(ret, ln.time + this.expireTimeStart);
                        this.segmentLocks[segment].readLock().unlock();
                        segment++;
                    }
                } finally {
                    this.segmentLocks[segment].readLock().unlock();
                }
            }
        }
        if (ret == Long.MAX_VALUE) {
            return 0;
        }
        return ret;
    }

    protected void expirePurge() {
        if (this.expireFlag) {
            long removePerSegment = 0;
            if (this.expireMaxSizeFlag) {
                long size = this.counter.get();
                if (size > this.expireMaxSize) {
                    removePerSegment = 1 + ((size - this.expireMaxSize) / 16);
                }
            }
            if (this.expireStoreSize != 0 && removePerSegment == 0) {
                Store store = Store.forEngine(this.engine);
                if (this.expireStoreSize < store.getCurrSize() - store.getFreeSize()) {
                    removePerSegment = 640;
                }
            }
            for (int seg = 0; seg < 16 && this.closeLatch.getCount() >= 2; seg++) {
                expirePurgeSegment(seg, removePerSegment);
            }
        }
    }

    protected void expirePurgeSegment(int seg, long removePerSegment) {
        this.segmentLocks[seg].writeLock().lock();
        try {
            long recid = ((Long) this.engine.get(this.expireTails[seg], Serializer.LONG)).longValue();
            long counter = 0;
            ExpireLinkNode last = null;
            while (recid != 0) {
                ExpireLinkNode n = (ExpireLinkNode) this.engine.get(recid, ExpireLinkNode.SERIALIZER);
                if (!$assertionsDisabled && n == ExpireLinkNode.EMPTY) {
                    throw new AssertionError();
                } else if ($assertionsDisabled || (n.hash >>> 28) == seg) {
                    counter++;
                    boolean remove = (counter < removePerSegment || (!(this.expire == 0 && this.expireAccess == 0) && n.time + this.expireTimeStart < System.currentTimeMillis())) ? true : $assertionsDisabled;
                    if (!remove) {
                        break;
                    }
                    this.engine.delete(recid, ExpireLinkNode.SERIALIZER);
                    removeInternal(((LinkedNode) this.engine.get(n.keyRecid, this.LN_SERIALIZER)).key, seg, n.hash, $assertionsDisabled);
                    last = n;
                    recid = n.next;
                } else {
                    throw new AssertionError();
                }
            }
            if (last != null) {
                if (recid == 0) {
                    this.engine.update(this.expireTails[seg], Long.valueOf(0), Serializer.LONG);
                    this.engine.update(this.expireHeads[seg], Long.valueOf(0), Serializer.LONG);
                } else {
                    this.engine.update(this.expireTails[seg], Long.valueOf(recid), Serializer.LONG);
                    this.engine.update(recid, ((ExpireLinkNode) this.engine.get(recid, ExpireLinkNode.SERIALIZER)).copyPrev(0), ExpireLinkNode.SERIALIZER);
                }
            }
            this.segmentLocks[seg].writeLock().unlock();
        } catch (Throwable th) {
            this.segmentLocks[seg].writeLock().unlock();
        }
    }

    protected void expireCheckSegment(int segment) {
        long current = ((Long) this.engine.get(this.expireTails[segment], Serializer.LONG)).longValue();
        if (current != 0) {
            long prev = 0;
            while (current != 0) {
                ExpireLinkNode curr = (ExpireLinkNode) this.engine.get(current, ExpireLinkNode.SERIALIZER);
                if ($assertionsDisabled || curr.prev == prev) {
                    prev = current;
                    current = curr.next;
                } else {
                    throw new AssertionError("wrong prev " + curr.prev + " - " + prev);
                }
            }
            if (((Long) this.engine.get(this.expireHeads[segment], Serializer.LONG)).longValue() != prev) {
                throw new AssertionError("wrong head");
            }
        } else if (((Long) this.engine.get(this.expireHeads[segment], Serializer.LONG)).longValue() != 0) {
            throw new AssertionError("head not 0");
        }
    }

    public Map<K, V> snapshot() {
        return new HTreeMap(TxEngine.createSnapshotFor(this.engine), this.counter == null ? 0 : this.counter.recid, this.hashSalt, this.segmentRecids, this.keySerializer, this.valueSerializer, 0, 0, 0, 0, 0, null, null, null, null, $assertionsDisabled);
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
        throw new UnsupportedOperationException("Method not decompiled: org.mapdb.HTreeMap.modificationListenerRemove(org.mapdb.Bind$MapListener):void");
    }

    protected void notify(K key, V oldValue, V newValue) {
        if ($assertionsDisabled || this.segmentLocks[hash(key) >>> 28].isWriteLockedByCurrentThread()) {
            for (MapListener<K, V> listener : this.modListeners) {
                if (listener != null) {
                    listener.update(key, oldValue, newValue);
                }
            }
            return;
        }
        throw new AssertionError();
    }

    public void close() {
        this.engine.close();
    }

    public Engine getEngine() {
        return this.engine;
    }
}
