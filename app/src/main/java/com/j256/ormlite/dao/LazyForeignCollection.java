package com.j256.ormlite.dao;

import com.j256.ormlite.field.FieldType;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LazyForeignCollection<T, ID> extends BaseForeignCollection<T, ID> implements ForeignCollection<T>, Serializable {
    private static final long serialVersionUID = -5460708106909626233L;
    private transient CloseableIterator<T> lastIterator;

    /* renamed from: com.j256.ormlite.dao.LazyForeignCollection.1 */
    class AnonymousClass1 implements CloseableIterable<T> {
        final /* synthetic */ int val$flags;

        AnonymousClass1(int i) {
            this.val$flags = i;
        }

        public CloseableIterator<T> iterator() {
            return closeableIterator();
        }

        public CloseableIterator<T> closeableIterator() {
            try {
                return LazyForeignCollection.this.seperateIteratorThrow(this.val$flags);
            } catch (Exception e) {
                throw new IllegalStateException("Could not build lazy iterator for " + LazyForeignCollection.this.dao.getDataClass(), e);
            }
        }
    }

    public LazyForeignCollection(Dao<T, ID> dao, Object parent, Object parentId, FieldType foreignFieldType, String orderColumn, boolean orderAscending) {
        super(dao, parent, parentId, foreignFieldType, orderColumn, orderAscending);
    }

    public CloseableIterator<T> iterator() {
        return closeableIterator(-1);
    }

    public CloseableIterator<T> iterator(int flags) {
        return closeableIterator(flags);
    }

    public CloseableIterator<T> closeableIterator() {
        return closeableIterator(-1);
    }

    public CloseableIterator<T> closeableIterator(int flags) {
        try {
            return iteratorThrow(flags);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not build lazy iterator for " + this.dao.getDataClass(), e);
        }
    }

    public CloseableIterator<T> iteratorThrow() throws SQLException {
        return iteratorThrow(-1);
    }

    public CloseableIterator<T> iteratorThrow(int flags) throws SQLException {
        this.lastIterator = seperateIteratorThrow(flags);
        return this.lastIterator;
    }

    public CloseableWrappedIterable<T> getWrappedIterable() {
        return getWrappedIterable(-1);
    }

    public CloseableWrappedIterable<T> getWrappedIterable(int flags) {
        return new CloseableWrappedIterableImpl(new AnonymousClass1(flags));
    }

    public void closeLastIterator() throws SQLException {
        if (this.lastIterator != null) {
            this.lastIterator.close();
            this.lastIterator = null;
        }
    }

    public boolean isEager() {
        return false;
    }

    public int size() {
        CloseableIterator<T> iterator = iterator();
        int sizeC = 0;
        while (iterator.hasNext()) {
            try {
                iterator.moveToNext();
                sizeC++;
            } finally {
                try {
                    iterator.close();
                } catch (SQLException e) {
                }
            }
        }
        return sizeC;
    }

    public boolean isEmpty() {
        CloseableIterator<T> iterator = iterator();
        try {
            boolean z = !iterator.hasNext();
            return z;
        } finally {
            try {
                iterator.close();
            } catch (SQLException e) {
            }
        }
    }

    public boolean contains(Object obj) {
        boolean z;
        CloseableIterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            try {
                if (iterator.next().equals(obj)) {
                    z = true;
                    break;
                }
            } finally {
                try {
                    iterator.close();
                } catch (SQLException e) {
                }
            }
        }
        z = false;
        try {
            iterator.close();
        } catch (SQLException e2) {
        }
        return z;
    }

    public boolean containsAll(Collection<?> collection) {
        Set<Object> leftOvers = new HashSet(collection);
        CloseableIterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            try {
                leftOvers.remove(iterator.next());
            } finally {
                try {
                    iterator.close();
                } catch (SQLException e) {
                }
            }
        }
        boolean isEmpty = leftOvers.isEmpty();
        return isEmpty;
    }

    public boolean remove(Object data) {
        boolean z;
        CloseableIterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            try {
                if (iterator.next().equals(data)) {
                    iterator.remove();
                    z = true;
                    break;
                }
            } finally {
                try {
                    iterator.close();
                } catch (SQLException e) {
                }
            }
        }
        z = false;
        try {
            iterator.close();
        } catch (SQLException e2) {
        }
        return z;
    }

    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        CloseableIterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            try {
                if (collection.contains(iterator.next())) {
                    iterator.remove();
                    changed = true;
                }
            } finally {
                try {
                    iterator.close();
                } catch (SQLException e) {
                }
            }
        }
        return changed;
    }

    public Object[] toArray() {
        List<T> items = new ArrayList();
        CloseableIterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            try {
                items.add(iterator.next());
            } finally {
                try {
                    iterator.close();
                } catch (SQLException e) {
                }
            }
        }
        Object[] toArray = items.toArray();
        return toArray;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <E> E[] toArray(E[] r12) {
        /*
        r11 = this;
        r5 = 0;
        r4 = 0;
        r7 = r11.iterator();
        r6 = r5;
    L_0x0007:
        r9 = r7.hasNext();	 Catch:{ all -> 0x0042 }
        if (r9 == 0) goto L_0x0034;
    L_0x000d:
        r2 = r7.next();	 Catch:{ all -> 0x0042 }
        r9 = r12.length;	 Catch:{ all -> 0x0042 }
        if (r4 < r9) goto L_0x0030;
    L_0x0014:
        if (r6 != 0) goto L_0x0028;
    L_0x0016:
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x0042 }
        r5.<init>();	 Catch:{ all -> 0x0042 }
        r0 = r12;
        r8 = r0.length;	 Catch:{ all -> 0x0051 }
        r3 = 0;
    L_0x001e:
        if (r3 >= r8) goto L_0x0029;
    L_0x0020:
        r1 = r0[r3];	 Catch:{ all -> 0x0051 }
        r5.add(r1);	 Catch:{ all -> 0x0051 }
        r3 = r3 + 1;
        goto L_0x001e;
    L_0x0028:
        r5 = r6;
    L_0x0029:
        r5.add(r2);	 Catch:{ all -> 0x0051 }
    L_0x002c:
        r4 = r4 + 1;
        r6 = r5;
        goto L_0x0007;
    L_0x0030:
        r12[r4] = r2;	 Catch:{ all -> 0x0042 }
        r5 = r6;
        goto L_0x002c;
    L_0x0034:
        r7.close();	 Catch:{ SQLException -> 0x004d }
    L_0x0037:
        if (r6 != 0) goto L_0x0048;
    L_0x0039:
        r9 = r12.length;
        r9 = r9 + -1;
        if (r4 >= r9) goto L_0x0041;
    L_0x003e:
        r9 = 0;
        r12[r4] = r9;
    L_0x0041:
        return r12;
    L_0x0042:
        r9 = move-exception;
        r5 = r6;
    L_0x0044:
        r7.close();	 Catch:{ SQLException -> 0x004f }
    L_0x0047:
        throw r9;
    L_0x0048:
        r12 = r6.toArray(r12);
        goto L_0x0041;
    L_0x004d:
        r9 = move-exception;
        goto L_0x0037;
    L_0x004f:
        r10 = move-exception;
        goto L_0x0047;
    L_0x0051:
        r9 = move-exception;
        goto L_0x0044;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.j256.ormlite.dao.LazyForeignCollection.toArray(java.lang.Object[]):E[]");
    }

    public int updateAll() {
        throw new UnsupportedOperationException("Cannot call updateAll() on a lazy collection.");
    }

    public int refreshAll() {
        throw new UnsupportedOperationException("Cannot call updateAll() on a lazy collection.");
    }

    public int refreshCollection() {
        return 0;
    }

    public boolean equals(Object other) {
        return super.equals(other);
    }

    public int hashCode() {
        return super.hashCode();
    }

    private CloseableIterator<T> seperateIteratorThrow(int flags) throws SQLException {
        if (this.dao != null) {
            return this.dao.iterator(getPreparedQuery(), flags);
        }
        throw new IllegalStateException("Internal DAO object is null.  Lazy collections cannot be used if they have been deserialized.");
    }
}
