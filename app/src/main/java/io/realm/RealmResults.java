package io.realm;

import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.exceptions.RealmException;
import io.realm.internal.ColumnType;
import io.realm.internal.TableOrView;
import io.realm.internal.TableView;
import io.realm.internal.TableView.Order;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import u.aly.dx;

public class RealmResults<E extends RealmObject> extends AbstractList<E> {
    public static final boolean SORT_ORDER_ASCENDING = true;
    public static final boolean SORT_ORDER_DESCENDING = false;
    private static final String TYPE_MISMATCH = "Field '%s': type mismatch - %s expected.";
    private Class<E> classSpec;
    private long currentTableViewVersion;
    private Realm realm;
    private TableOrView table;

    /* renamed from: io.realm.RealmResults.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$io$realm$internal$ColumnType;

        static {
            $SwitchMap$io$realm$internal$ColumnType = new int[ColumnType.values().length];
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.INTEGER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.FLOAT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$io$realm$internal$ColumnType[ColumnType.DOUBLE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private class RealmResultsIterator implements Iterator<E> {
        int pos;

        RealmResultsIterator() {
            this.pos = -1;
            RealmResults.this.currentTableViewVersion = RealmResults.this.table.sync();
        }

        public boolean hasNext() {
            RealmResults.this.assertRealmIsStable();
            return this.pos + 1 < RealmResults.this.size() ? RealmResults.SORT_ORDER_ASCENDING : RealmResults.SORT_ORDER_DESCENDING;
        }

        public E next() {
            RealmResults.this.assertRealmIsStable();
            this.pos++;
            if (this.pos < RealmResults.this.size()) {
                return RealmResults.this.get(this.pos);
            }
            throw new IndexOutOfBoundsException("Cannot access index " + this.pos + " when size is " + RealmResults.this.size() + ". Remember to check hasNext() before using next().");
        }

        public void remove() {
            throw new RealmException("Removing is not supported.");
        }
    }

    private class RealmResultsListIterator extends RealmResultsIterator implements ListIterator<E> {
        RealmResultsListIterator(int start) {
            super();
            if (start < 0 || start > RealmResults.this.size()) {
                throw new IndexOutOfBoundsException("Starting location must be a valid index: [0, " + (RealmResults.this.size() - 1) + "]. Yours was " + start);
            }
            this.pos = start - 1;
        }

        public void add(E e) {
            throw new RealmException("Adding elements not supported. Use Realm.createObject() instead.");
        }

        public boolean hasPrevious() {
            RealmResults.this.assertRealmIsStable();
            return this.pos > 0 ? RealmResults.SORT_ORDER_ASCENDING : RealmResults.SORT_ORDER_DESCENDING;
        }

        public int nextIndex() {
            RealmResults.this.assertRealmIsStable();
            return this.pos + 1;
        }

        public E previous() {
            RealmResults.this.assertRealmIsStable();
            this.pos--;
            if (this.pos >= 0) {
                return RealmResults.this.get(this.pos);
            }
            throw new IndexOutOfBoundsException("Cannot access index less than zero. This was " + this.pos + ". Remember to check hasPrevious() before using previous().");
        }

        public int previousIndex() {
            RealmResults.this.assertRealmIsStable();
            return this.pos;
        }

        public void set(E e) {
            throw new RealmException("Replacing elements not supported.");
        }

        public void remove() {
            throw new RealmException("Removing elements not supported.");
        }
    }

    RealmResults(Realm realm, Class<E> classSpec) {
        this.table = null;
        this.currentTableViewVersion = -1;
        this.realm = realm;
        this.classSpec = classSpec;
    }

    RealmResults(Realm realm, TableOrView table, Class<E> classSpec) {
        this(realm, classSpec);
        this.table = table;
    }

    Realm getRealm() {
        return this.realm;
    }

    TableOrView getTable() {
        if (this.table == null) {
            return this.realm.getTable(this.classSpec);
        }
        return this.table;
    }

    public RealmQuery<E> where() {
        this.realm.checkIfValid();
        return new RealmQuery(this, this.classSpec);
    }

    public E get(int location) {
        this.realm.checkIfValid();
        TableOrView table = getTable();
        if (table instanceof TableView) {
            return this.realm.get(this.classSpec, ((TableView) table).getSourceRowIndex((long) location));
        }
        return this.realm.get(this.classSpec, (long) location);
    }

    public int indexOf(Object o) {
        throw new NoSuchMethodError("indexOf is not supported on RealmResults");
    }

    public E first() {
        return get(0);
    }

    public E last() {
        return get(size() - 1);
    }

    public Iterator<E> iterator() {
        return new RealmResultsIterator();
    }

    public ListIterator<E> listIterator() {
        return new RealmResultsListIterator(0);
    }

    public ListIterator<E> listIterator(int location) {
        return new RealmResultsListIterator(location);
    }

    private long getColumnIndex(String fieldName) {
        if (fieldName.contains(".")) {
            throw new IllegalArgumentException("Sorting using child object properties is not supported: " + fieldName);
        }
        long columnIndex = this.table.getColumnIndex(fieldName);
        if (columnIndex >= 0) {
            return columnIndex;
        }
        throw new IllegalArgumentException(String.format("Field '%s' does not exist.", new Object[]{fieldName}));
    }

    public void sort(String fieldName) {
        sort(fieldName, (boolean) SORT_ORDER_ASCENDING);
    }

    public void sort(String fieldName, boolean sortAscending) {
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must be provided");
        }
        this.realm.checkIfValid();
        TableOrView table = getTable();
        if (table instanceof TableView) {
            ((TableView) table).sort(getColumnIndex(fieldName), sortAscending ? Order.ascending : Order.descending);
            return;
        }
        throw new IllegalArgumentException("Only RealmResults can be sorted - please use allObject() to create a RealmResults.");
    }

    public void sort(String[] fieldNames, boolean[] sortAscending) {
        if (fieldNames == null) {
            throw new IllegalArgumentException("fieldNames must be provided.");
        } else if (sortAscending == null) {
            throw new IllegalArgumentException("sortAscending must be provided.");
        } else if (fieldNames.length == 1 && sortAscending.length == 1) {
            sort(fieldNames[0], sortAscending[0]);
        } else {
            this.realm.checkIfValid();
            TableOrView table = getTable();
            if (table instanceof TableView) {
                List TVOrder = new ArrayList();
                List columnIndices = new ArrayList();
                for (String fieldName : fieldNames) {
                    columnIndices.add(Long.valueOf(getColumnIndex(fieldName)));
                }
                for (boolean z : sortAscending) {
                    TVOrder.add(z ? Order.ascending : Order.descending);
                }
                ((TableView) table).sort(columnIndices, TVOrder);
            }
        }
    }

    public void sort(String fieldName1, boolean sortAscending1, String fieldName2, boolean sortAscending2) {
        sort(new String[]{fieldName1, fieldName2}, new boolean[]{sortAscending1, sortAscending2});
    }

    public void sort(String fieldName1, boolean sortAscending1, String fieldName2, boolean sortAscending2, String fieldName3, boolean sortAscending3) {
        sort(new String[]{fieldName1, fieldName2, fieldName3}, new boolean[]{sortAscending1, sortAscending2, sortAscending3});
    }

    public int size() {
        return Long.valueOf(getTable().size()).intValue();
    }

    public Number min(String fieldName) {
        this.realm.checkIfValid();
        long columnIndex = this.table.getColumnIndex(fieldName);
        switch (AnonymousClass1.$SwitchMap$io$realm$internal$ColumnType[this.table.getColumnType(columnIndex).ordinal()]) {
            case dx.b /*1*/:
                return Long.valueOf(this.table.minimumLong(columnIndex));
            case dx.c /*2*/:
                return Float.valueOf(this.table.minimumFloat(columnIndex));
            case dx.d /*3*/:
                return Double.valueOf(this.table.minimumDouble(columnIndex));
            default:
                throw new IllegalArgumentException(String.format(TYPE_MISMATCH, new Object[]{fieldName, "int, float or double"}));
        }
    }

    public Date minDate(String fieldName) {
        this.realm.checkIfValid();
        long columnIndex = this.table.getColumnIndex(fieldName);
        if (this.table.getColumnType(columnIndex) == ColumnType.DATE) {
            return this.table.minimumDate(columnIndex);
        }
        throw new IllegalArgumentException(String.format(TYPE_MISMATCH, new Object[]{fieldName, HttpRequest.HEADER_DATE}));
    }

    public Number max(String fieldName) {
        this.realm.checkIfValid();
        long columnIndex = this.table.getColumnIndex(fieldName);
        switch (AnonymousClass1.$SwitchMap$io$realm$internal$ColumnType[this.table.getColumnType(columnIndex).ordinal()]) {
            case dx.b /*1*/:
                return Long.valueOf(this.table.maximumLong(columnIndex));
            case dx.c /*2*/:
                return Float.valueOf(this.table.maximumFloat(columnIndex));
            case dx.d /*3*/:
                return Double.valueOf(this.table.maximumDouble(columnIndex));
            default:
                throw new IllegalArgumentException(String.format(TYPE_MISMATCH, new Object[]{fieldName, "int, float or double"}));
        }
    }

    public Date maxDate(String fieldName) {
        this.realm.checkIfValid();
        long columnIndex = this.table.getColumnIndex(fieldName);
        if (this.table.getColumnType(columnIndex) == ColumnType.DATE) {
            return this.table.maximumDate(columnIndex);
        }
        throw new IllegalArgumentException(String.format(TYPE_MISMATCH, new Object[]{fieldName, HttpRequest.HEADER_DATE}));
    }

    public Number sum(String fieldName) {
        this.realm.checkIfValid();
        long columnIndex = this.table.getColumnIndex(fieldName);
        switch (AnonymousClass1.$SwitchMap$io$realm$internal$ColumnType[this.table.getColumnType(columnIndex).ordinal()]) {
            case dx.b /*1*/:
                return Long.valueOf(this.table.sumLong(columnIndex));
            case dx.c /*2*/:
                return Double.valueOf(this.table.sumFloat(columnIndex));
            case dx.d /*3*/:
                return Double.valueOf(this.table.sumDouble(columnIndex));
            default:
                throw new IllegalArgumentException(String.format(TYPE_MISMATCH, new Object[]{fieldName, "int, float or double"}));
        }
    }

    public double average(String fieldName) {
        this.realm.checkIfValid();
        long columnIndex = this.table.getColumnIndex(fieldName);
        switch (AnonymousClass1.$SwitchMap$io$realm$internal$ColumnType[this.table.getColumnType(columnIndex).ordinal()]) {
            case dx.b /*1*/:
                return this.table.averageLong(columnIndex);
            case dx.c /*2*/:
                return this.table.averageFloat(columnIndex);
            case dx.d /*3*/:
                return this.table.averageDouble(columnIndex);
            default:
                throw new IllegalArgumentException(String.format(TYPE_MISMATCH, new Object[]{fieldName, "int, float or double"}));
        }
    }

    public E remove(int index) {
        getTable().remove((long) index);
        return null;
    }

    public void removeLast() {
        getTable().removeLast();
    }

    public void clear() {
        getTable().clear();
    }

    @Deprecated
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void add(int index, E e) {
        throw new UnsupportedOperationException();
    }

    private void assertRealmIsStable() {
        long version = this.table.sync();
        if (this.currentTableViewVersion <= -1 || version == this.currentTableViewVersion) {
            this.currentTableViewVersion = version;
            return;
        }
        throw new ConcurrentModificationException("No outside changes to a Realm is allowed while iterating a RealmResults. Use iterators methods instead.");
    }
}
