package org.mapdb;

import android.support.v4.media.TransportMediator;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.mapdb.Atomic.Long;

public final class Queues {

    public static abstract class SimpleQueue<E> implements BlockingQueue<E> {
        protected static final int TICK = 10000;
        protected final Engine engine;
        protected final Long head;
        protected final ReentrantLock[] locks;
        protected final Serializer<Node<E>> nodeSerializer;
        protected final Serializer<E> serializer;
        protected final boolean useLocks;

        protected static final class Node<E> {
            protected static final Node<?> EMPTY;
            protected final long next;
            protected final E value;

            static {
                EMPTY = new Node(0, null);
            }

            public Node(long next, E value) {
                this.next = next;
                this.value = value;
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                Node node = (Node) o;
                if (this.next != node.next) {
                    return false;
                }
                if (this.value != null) {
                    if (this.value.equals(node.value)) {
                        return true;
                    }
                } else if (node.value == null) {
                    return true;
                }
                return false;
            }

            public int hashCode() {
                return (((int) (this.next ^ (this.next >>> 32))) * 31) + (this.value != null ? this.value.hashCode() : 0);
            }
        }

        protected static class NodeSerializer<E> implements Serializer<Node<E>> {
            private final Serializer<E> serializer;

            public NodeSerializer(Serializer<E> serializer) {
                this.serializer = serializer;
            }

            public void serialize(DataOutput out, Node<E> value) throws IOException {
                if (value != Node.EMPTY) {
                    DataOutput2.packLong(out, value.next);
                    this.serializer.serialize(out, value.value);
                }
            }

            public Node<E> deserialize(DataInput in, int available) throws IOException {
                if (available == 0) {
                    return Node.EMPTY;
                }
                return new Node(DataInput2.unpackLong(in), this.serializer.deserialize(in, -1));
            }

            public int fixedSize() {
                return -1;
            }
        }

        public SimpleQueue(Engine engine, Serializer<E> serializer, long headRecidRef, boolean useLocks) {
            this.engine = engine;
            this.serializer = serializer;
            this.head = new Long(engine, headRecidRef);
            this.nodeSerializer = new NodeSerializer(serializer);
            this.useLocks = useLocks;
            if (useLocks) {
                this.locks = new ReentrantLock[TransportMediator.FLAG_KEY_MEDIA_NEXT];
                for (int i = 0; i < this.locks.length; i++) {
                    this.locks[i] = new ReentrantLock(false);
                }
                return;
            }
            this.locks = null;
        }

        public void close() {
            this.engine.close();
        }

        public E peek() {
            long head2 = this.head.get();
            if (this.useLocks) {
                this.locks[Store.lockPos(head2)].lock();
            }
            try {
                E e;
                Node n = (Node) this.engine.get(head2, this.nodeSerializer);
                if (n == Node.EMPTY) {
                    e = null;
                } else {
                    e = n.value;
                    if (this.useLocks) {
                        this.locks[Store.lockPos(head2)].unlock();
                    }
                }
                return e;
            } finally {
                if (this.useLocks) {
                    this.locks[Store.lockPos(head2)].unlock();
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public E poll() {
            /*
            r6 = this;
        L_0x0000:
            r3 = r6.head;
            r0 = r3.get();
            r3 = r6.useLocks;
            if (r3 == 0) goto L_0x0015;
        L_0x000a:
            r3 = r6.locks;
            r4 = org.mapdb.Store.lockPos(r0);
            r3 = r3[r4];
            r3.lock();
        L_0x0015:
            r3 = r6.engine;	 Catch:{ all -> 0x0065 }
            r4 = r6.nodeSerializer;	 Catch:{ all -> 0x0065 }
            r2 = r3.get(r0, r4);	 Catch:{ all -> 0x0065 }
            r2 = (org.mapdb.Queues.SimpleQueue.Node) r2;	 Catch:{ all -> 0x0065 }
            r3 = org.mapdb.Queues.SimpleQueue.Node.EMPTY;	 Catch:{ all -> 0x0065 }
            if (r2 != r3) goto L_0x0034;
        L_0x0023:
            r3 = 0;
            r4 = r6.useLocks;
            if (r4 == 0) goto L_0x0033;
        L_0x0028:
            r4 = r6.locks;
            r5 = org.mapdb.Store.lockPos(r0);
            r4 = r4[r5];
            r4.unlock();
        L_0x0033:
            return r3;
        L_0x0034:
            r3 = r6.head;	 Catch:{ all -> 0x0065 }
            r4 = r2.next;	 Catch:{ all -> 0x0065 }
            r3 = r3.compareAndSet(r0, r4);	 Catch:{ all -> 0x0065 }
            if (r3 == 0) goto L_0x0076;
        L_0x003e:
            r3 = r6.useLocks;	 Catch:{ all -> 0x0065 }
            if (r3 == 0) goto L_0x005b;
        L_0x0042:
            r3 = r6.engine;	 Catch:{ all -> 0x0065 }
            r4 = r6.nodeSerializer;	 Catch:{ all -> 0x0065 }
            r3.delete(r0, r4);	 Catch:{ all -> 0x0065 }
        L_0x0049:
            r3 = r2.value;	 Catch:{ all -> 0x0065 }
            r4 = r6.useLocks;
            if (r4 == 0) goto L_0x0033;
        L_0x004f:
            r4 = r6.locks;
            r5 = org.mapdb.Store.lockPos(r0);
            r4 = r4[r5];
            r4.unlock();
            goto L_0x0033;
        L_0x005b:
            r3 = r6.engine;	 Catch:{ all -> 0x0065 }
            r4 = org.mapdb.Queues.SimpleQueue.Node.EMPTY;	 Catch:{ all -> 0x0065 }
            r5 = r6.nodeSerializer;	 Catch:{ all -> 0x0065 }
            r3.update(r0, r4, r5);	 Catch:{ all -> 0x0065 }
            goto L_0x0049;
        L_0x0065:
            r3 = move-exception;
            r4 = r6.useLocks;
            if (r4 == 0) goto L_0x0075;
        L_0x006a:
            r4 = r6.locks;
            r5 = org.mapdb.Store.lockPos(r0);
            r4 = r4[r5];
            r4.unlock();
        L_0x0075:
            throw r3;
        L_0x0076:
            r3 = r6.useLocks;
            if (r3 == 0) goto L_0x0000;
        L_0x007a:
            r3 = r6.locks;
            r4 = org.mapdb.Store.lockPos(r0);
            r3 = r3[r4];
            r3.unlock();
            goto L_0x0000;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.mapdb.Queues.SimpleQueue.poll():E");
        }

        public void clear() {
            while (!isEmpty()) {
                poll();
            }
        }

        public E remove() {
            E ret = poll();
            if (ret != null) {
                return ret;
            }
            throw new NoSuchElementException();
        }

        public E element() {
            E ret = peek();
            if (ret != null) {
                return ret;
            }
            throw new NoSuchElementException();
        }

        public boolean offer(E e) {
            try {
                return add(e);
            } catch (IllegalStateException e2) {
                return false;
            }
        }

        public void put(E e) throws InterruptedException {
            while (!offer(e)) {
                Thread.sleep(0, TICK);
            }
        }

        public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
            if (offer(e)) {
                return true;
            }
            long target = System.currentTimeMillis() + unit.toMillis(timeout);
            while (target >= System.currentTimeMillis()) {
                if (offer(e)) {
                    return true;
                }
                Thread.sleep(0, TICK);
            }
            return false;
        }

        public E take() throws InterruptedException {
            E e = poll();
            while (e == null) {
                Thread.sleep(0, TICK);
                e = poll();
            }
            return e;
        }

        public E poll(long timeout, TimeUnit unit) throws InterruptedException {
            E e = poll();
            if (e != null) {
                return e;
            }
            long target = System.currentTimeMillis() + unit.toMillis(timeout);
            while (target >= System.currentTimeMillis()) {
                Thread.sleep(0, TICK);
                e = poll();
                if (e != null) {
                    return e;
                }
            }
            return null;
        }

        public int drainTo(Collection<? super E> c) {
            return drainTo(c, AdvancedShareActionProvider.WEIGHT_MAX);
        }

        public int drainTo(Collection<? super E> c, int maxElements) {
            int counter = 0;
            while (counter < maxElements) {
                E e = poll();
                if (e == null) {
                    break;
                }
                c.add(e);
                counter++;
            }
            return counter;
        }

        public int remainingCapacity() {
            return AdvancedShareActionProvider.WEIGHT_MAX;
        }

        public boolean isEmpty() {
            return peek() == null;
        }

        public int size() {
            throw new UnsupportedOperationException();
        }

        public boolean contains(Object o) {
            throw new UnsupportedOperationException();
        }

        public Iterator<E> iterator() {
            throw new UnsupportedOperationException();
        }

        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        public <T> T[] toArray(T[] tArr) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }
    }

    public static class CircularQueue<E> extends SimpleQueue<E> {
        protected final Long headInsert;
        protected final Lock lock;
        protected final long size;

        public CircularQueue(Engine engine, Serializer<E> serializer, long headRecid, long headInsertRecid, long size) {
            super(engine, serializer, headRecid, false);
            this.lock = new ReentrantLock(false);
            this.headInsert = new Long(engine, headInsertRecid);
            this.size = size;
        }

        public boolean add(Object o) {
            this.lock.lock();
            try {
                long nRecid = this.headInsert.get();
                Node<E> n = new Node(((Node) this.engine.get(nRecid, this.nodeSerializer)).next, o);
                this.engine.update(nRecid, n, this.nodeSerializer);
                this.headInsert.set(n.next);
                this.head.compareAndSet(nRecid, n.next);
                return true;
            } finally {
                this.lock.unlock();
            }
        }

        public void clear() {
            this.lock.lock();
            int i = 0;
            while (true) {
                try {
                    if (((long) i) >= this.size) {
                        break;
                    }
                    poll();
                    i++;
                } finally {
                    this.lock.unlock();
                }
            }
        }

        public E poll() {
            this.lock.lock();
            try {
                long nRecid = this.head.get();
                Node<E> n = (Node) this.engine.get(nRecid, this.nodeSerializer);
                this.engine.update(nRecid, new Node(n.next, null), this.nodeSerializer);
                this.head.set(n.next);
                E e = n.value;
                return e;
            } finally {
                this.lock.unlock();
            }
        }

        public E peek() {
            this.lock.lock();
            try {
                E e = ((Node) this.engine.get(this.head.get(), this.nodeSerializer)).value;
                return e;
            } finally {
                this.lock.unlock();
            }
        }
    }

    public static class Queue<E> extends SimpleQueue<E> {
        protected final Long tail;

        public Queue(Engine engine, Serializer<E> serializer, long headerRecid, long nextTailRecid, boolean useLocks) {
            super(engine, serializer, headerRecid, useLocks);
            this.tail = new Long(engine, nextTailRecid);
        }

        public boolean add(E e) {
            long nextTail = this.engine.put(Node.EMPTY, this.nodeSerializer);
            long tail2 = this.tail.get();
            while (!this.tail.compareAndSet(tail2, nextTail)) {
                tail2 = this.tail.get();
            }
            this.engine.update(tail2, new Node(nextTail, e), this.nodeSerializer);
            return true;
        }
    }

    public static class Stack<E> extends SimpleQueue<E> {
        public Stack(Engine engine, Serializer<E> serializer, long headerRecidRef, boolean useLocks) {
            super(engine, serializer, headerRecidRef, useLocks);
        }

        public boolean add(E e) {
            long head2 = this.head.get();
            long recid = this.engine.put(new Node(head2, e), this.nodeSerializer);
            while (!this.head.compareAndSet(head2, recid)) {
                head2 = this.head.get();
                this.engine.update(recid, new Node(head2, e), this.nodeSerializer);
            }
            return true;
        }
    }

    private Queues() {
    }
}
