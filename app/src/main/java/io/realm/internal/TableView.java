package io.realm.internal;

import io.realm.internal.TableOrView.PivotType;
import java.io.Closeable;
import java.util.Date;
import java.util.List;

public class TableView implements TableOrView, Closeable {
    protected boolean DEBUG;
    private final Context context;
    protected long nativePtr;
    protected final Table parent;
    private final TableQuery query;

    public enum Order {
        ascending,
        descending
    }

    protected static native void nativeClose(long j);

    protected native long createNativeTableView(Table table, long j);

    protected native void nativeAddInt(long j, long j2, long j3);

    protected native double nativeAverageDouble(long j, long j2);

    protected native double nativeAverageFloat(long j, long j2);

    protected native double nativeAverageInt(long j, long j2);

    protected native void nativeClear(long j);

    protected native void nativeClearSubtable(long j, long j2, long j3);

    protected native long nativeFindAllBool(long j, long j2, boolean z);

    protected native long nativeFindAllDate(long j, long j2, long j3);

    protected native long nativeFindAllDouble(long j, long j2, double d);

    protected native long nativeFindAllFloat(long j, long j2, float f);

    protected native long nativeFindAllInt(long j, long j2, long j3);

    protected native long nativeFindAllString(long j, long j2, String str);

    protected native long nativeFindFirstBool(long j, long j2, boolean z);

    protected native long nativeFindFirstDate(long j, long j2, long j3);

    protected native long nativeFindFirstDouble(long j, long j2, double d);

    protected native long nativeFindFirstFloat(long j, long j2, float f);

    protected native long nativeFindFirstInt(long j, long j2, long j3);

    protected native long nativeFindFirstString(long j, long j2, String str);

    protected native boolean nativeGetBoolean(long j, long j2, long j3);

    protected native byte[] nativeGetByteArray(long j, long j2, long j3);

    protected native long nativeGetColumnCount(long j);

    protected native long nativeGetColumnIndex(long j, String str);

    protected native String nativeGetColumnName(long j, long j2);

    protected native int nativeGetColumnType(long j, long j2);

    protected native long nativeGetDateTimeValue(long j, long j2, long j3);

    protected native double nativeGetDouble(long j, long j2, long j3);

    protected native float nativeGetFloat(long j, long j2, long j3);

    protected native long nativeGetLink(long j, long j2, long j3);

    protected native long nativeGetLong(long j, long j2, long j3);

    protected native Mixed nativeGetMixed(long j, long j2, long j3);

    protected native int nativeGetMixedType(long j, long j2, long j3);

    protected native long nativeGetSourceRowIndex(long j, long j2);

    protected native String nativeGetString(long j, long j2, long j3);

    protected native long nativeGetSubtable(long j, long j2, long j3);

    protected native long nativeGetSubtableSize(long j, long j2, long j3);

    protected native boolean nativeIsNullLink(long j, long j2, long j3);

    protected native long nativeMaximumDate(long j, long j2);

    protected native double nativeMaximumDouble(long j, long j2);

    protected native float nativeMaximumFloat(long j, long j2);

    protected native long nativeMaximumInt(long j, long j2);

    protected native long nativeMinimumDate(long j, long j2);

    protected native double nativeMinimumDouble(long j, long j2);

    protected native float nativeMinimumFloat(long j, long j2);

    protected native long nativeMinimumInt(long j, long j2);

    protected native void nativeNullifyLink(long j, long j2, long j3);

    protected native void nativePivot(long j, long j2, long j3, int i, long j4);

    protected native void nativeRemoveRow(long j, long j2);

    protected native String nativeRowToString(long j, long j2);

    protected native void nativeSetBoolean(long j, long j2, long j3, boolean z);

    protected native void nativeSetByteArray(long j, long j2, long j3, byte[] bArr);

    protected native void nativeSetDateTimeValue(long j, long j2, long j3, long j4);

    protected native void nativeSetDouble(long j, long j2, long j3, double d);

    protected native void nativeSetFloat(long j, long j2, long j3, float f);

    protected native void nativeSetLink(long j, long j2, long j3, long j4);

    protected native void nativeSetLong(long j, long j2, long j3, long j4);

    protected native void nativeSetMixed(long j, long j2, long j3, Mixed mixed);

    protected native void nativeSetString(long j, long j2, long j3, String str);

    protected native long nativeSize(long j);

    protected native void nativeSort(long j, long j2, boolean z);

    protected native void nativeSortMulti(long j, long[] jArr, boolean[] zArr);

    protected native double nativeSumDouble(long j, long j2);

    protected native double nativeSumFloat(long j, long j2);

    protected native long nativeSumInt(long j, long j2);

    protected native long nativeSync(long j);

    protected native String nativeToJson(long j);

    protected native String nativeToString(long j, long j2);

    protected native long nativeWhere(long j);

    protected TableView(Context context, Table parent, long nativePtr) {
        this.DEBUG = false;
        this.context = context;
        this.parent = parent;
        this.nativePtr = nativePtr;
        this.query = null;
    }

    protected TableView(Context context, Table parent, long nativePtr, TableQuery query) {
        this.DEBUG = false;
        this.context = context;
        this.parent = parent;
        this.nativePtr = nativePtr;
        this.query = query;
    }

    public Table getTable() {
        return this.parent;
    }

    public void close() {
        synchronized (this.context) {
            if (this.nativePtr != 0) {
                nativeClose(this.nativePtr);
                if (this.DEBUG) {
                    System.err.println("==== TableView CLOSE, ptr= " + this.nativePtr);
                }
                this.nativePtr = 0;
            }
        }
    }

    protected void finalize() {
        synchronized (this.context) {
            if (this.nativePtr != 0) {
                this.context.asyncDisposeTableView(this.nativePtr);
                this.nativePtr = 0;
            }
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public long size() {
        return nativeSize(this.nativePtr);
    }

    public long getSourceRowIndex(long rowIndex) {
        return nativeGetSourceRowIndex(this.nativePtr, rowIndex);
    }

    public long getColumnCount() {
        return nativeGetColumnCount(this.nativePtr);
    }

    public String getColumnName(long columnIndex) {
        return nativeGetColumnName(this.nativePtr, columnIndex);
    }

    public long getColumnIndex(String columnName) {
        if (columnName != null) {
            return nativeGetColumnIndex(this.nativePtr, columnName);
        }
        throw new IllegalArgumentException("Column name can not be null.");
    }

    public ColumnType getColumnType(long columnIndex) {
        return ColumnType.fromNativeValue(nativeGetColumnType(this.nativePtr, columnIndex));
    }

    public long getLong(long columnIndex, long rowIndex) {
        return nativeGetLong(this.nativePtr, columnIndex, rowIndex);
    }

    public boolean getBoolean(long columnIndex, long rowIndex) {
        return nativeGetBoolean(this.nativePtr, columnIndex, rowIndex);
    }

    public float getFloat(long columnIndex, long rowIndex) {
        return nativeGetFloat(this.nativePtr, columnIndex, rowIndex);
    }

    public double getDouble(long columnIndex, long rowIndex) {
        return nativeGetDouble(this.nativePtr, columnIndex, rowIndex);
    }

    public Date getDate(long columnIndex, long rowIndex) {
        return new Date(nativeGetDateTimeValue(this.nativePtr, columnIndex, rowIndex) * 1000);
    }

    public String getString(long columnIndex, long rowIndex) {
        return nativeGetString(this.nativePtr, columnIndex, rowIndex);
    }

    public byte[] getBinaryByteArray(long columnIndex, long rowIndex) {
        return nativeGetByteArray(this.nativePtr, columnIndex, rowIndex);
    }

    public ColumnType getMixedType(long columnIndex, long rowIndex) {
        return ColumnType.fromNativeValue(nativeGetMixedType(this.nativePtr, columnIndex, rowIndex));
    }

    public Mixed getMixed(long columnIndex, long rowIndex) {
        return nativeGetMixed(this.nativePtr, columnIndex, rowIndex);
    }

    public long getLink(long columnIndex, long rowIndex) {
        return nativeGetLink(this.nativePtr, columnIndex, rowIndex);
    }

    public Table getSubtable(long columnIndex, long rowIndex) {
        this.context.executeDelayedDisposal();
        long nativeSubtablePtr = nativeGetSubtable(this.nativePtr, columnIndex, rowIndex);
        try {
            return new Table(this.context, this.parent, nativeSubtablePtr);
        } catch (RuntimeException e) {
            Table.nativeClose(nativeSubtablePtr);
            throw e;
        }
    }

    public long getSubtableSize(long columnIndex, long rowIndex) {
        return nativeGetSubtableSize(this.nativePtr, columnIndex, rowIndex);
    }

    public void clearSubtable(long columnIndex, long rowIndex) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeClearSubtable(this.nativePtr, columnIndex, rowIndex);
    }

    public void setLong(long columnIndex, long rowIndex, long value) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeSetLong(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void setBoolean(long columnIndex, long rowIndex, boolean value) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeSetBoolean(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void setFloat(long columnIndex, long rowIndex, float value) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeSetFloat(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void setDouble(long columnIndex, long rowIndex, double value) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeSetDouble(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void setDate(long columnIndex, long rowIndex, Date value) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeSetDateTimeValue(this.nativePtr, columnIndex, rowIndex, value.getTime() / 1000);
    }

    public void setString(long columnIndex, long rowIndex, String value) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeSetString(this.nativePtr, columnIndex, rowIndex, value);
    }

    public void setBinaryByteArray(long columnIndex, long rowIndex, byte[] data) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeSetByteArray(this.nativePtr, columnIndex, rowIndex, data);
    }

    public void setMixed(long columnIndex, long rowIndex, Mixed data) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeSetMixed(this.nativePtr, columnIndex, rowIndex, data);
    }

    public void setLink(long columnIndex, long rowIndex, long value) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeSetLink(this.nativePtr, columnIndex, rowIndex, value);
    }

    public boolean isNullLink(long columnIndex, long rowIndex) {
        return nativeIsNullLink(this.nativePtr, columnIndex, rowIndex);
    }

    public void nullifyLink(long columnIndex, long rowIndex) {
        nativeNullifyLink(this.nativePtr, columnIndex, rowIndex);
    }

    public void adjust(long columnIndex, long value) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeAddInt(this.nativePtr, columnIndex, value);
    }

    public void clear() {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeClear(this.nativePtr);
    }

    public void remove(long rowIndex) {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        nativeRemoveRow(this.nativePtr, rowIndex);
    }

    public void removeLast() {
        if (this.parent.isImmutable()) {
            throwImmutable();
        }
        if (!isEmpty()) {
            nativeRemoveRow(this.nativePtr, size() - 1);
        }
    }

    public long findFirstLong(long columnIndex, long value) {
        return nativeFindFirstInt(this.nativePtr, columnIndex, value);
    }

    public long findFirstBoolean(long columnIndex, boolean value) {
        return nativeFindFirstBool(this.nativePtr, columnIndex, value);
    }

    public long findFirstFloat(long columnIndex, float value) {
        return nativeFindFirstFloat(this.nativePtr, columnIndex, value);
    }

    public long findFirstDouble(long columnIndex, double value) {
        return nativeFindFirstDouble(this.nativePtr, columnIndex, value);
    }

    public long findFirstDate(long columnIndex, Date date) {
        return nativeFindFirstDate(this.nativePtr, columnIndex, date.getTime() / 1000);
    }

    public long findFirstString(long columnIndex, String value) {
        return nativeFindFirstString(this.nativePtr, columnIndex, value);
    }

    public long lowerBoundLong(long columnIndex, long value) {
        throw new RuntimeException("Not implemented yet");
    }

    public long upperBoundLong(long columnIndex, long value) {
        throw new RuntimeException("Not implemented yet");
    }

    public TableView findAllLong(long columnIndex, long value) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllInt(this.nativePtr, columnIndex, value);
        try {
            return new TableView(this.context, this.parent, nativeViewPtr);
        } catch (RuntimeException e) {
            nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAllBoolean(long columnIndex, boolean value) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllBool(this.nativePtr, columnIndex, value);
        try {
            return new TableView(this.context, this.parent, nativeViewPtr);
        } catch (RuntimeException e) {
            nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAllFloat(long columnIndex, float value) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllFloat(this.nativePtr, columnIndex, value);
        try {
            return new TableView(this.context, this.parent, nativeViewPtr);
        } catch (RuntimeException e) {
            nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAllDouble(long columnIndex, double value) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllDouble(this.nativePtr, columnIndex, value);
        try {
            return new TableView(this.context, this.parent, nativeViewPtr);
        } catch (RuntimeException e) {
            nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAllDate(long columnIndex, Date date) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllDate(this.nativePtr, columnIndex, date.getTime() / 1000);
        try {
            return new TableView(this.context, this.parent, nativeViewPtr);
        } catch (RuntimeException e) {
            nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public TableView findAllString(long columnIndex, String value) {
        this.context.executeDelayedDisposal();
        long nativeViewPtr = nativeFindAllString(this.nativePtr, columnIndex, value);
        try {
            return new TableView(this.context, this.parent, nativeViewPtr);
        } catch (RuntimeException e) {
            nativeClose(nativeViewPtr);
            throw e;
        }
    }

    public long sumLong(long columnIndex) {
        return nativeSumInt(this.nativePtr, columnIndex);
    }

    public long maximumLong(long columnIndex) {
        return nativeMaximumInt(this.nativePtr, columnIndex);
    }

    public long minimumLong(long columnIndex) {
        return nativeMinimumInt(this.nativePtr, columnIndex);
    }

    public double averageLong(long columnIndex) {
        return nativeAverageInt(this.nativePtr, columnIndex);
    }

    public double sumFloat(long columnIndex) {
        return nativeSumFloat(this.nativePtr, columnIndex);
    }

    public float maximumFloat(long columnIndex) {
        return nativeMaximumFloat(this.nativePtr, columnIndex);
    }

    public float minimumFloat(long columnIndex) {
        return nativeMinimumFloat(this.nativePtr, columnIndex);
    }

    public double averageFloat(long columnIndex) {
        return nativeAverageFloat(this.nativePtr, columnIndex);
    }

    public double sumDouble(long columnIndex) {
        return nativeSumDouble(this.nativePtr, columnIndex);
    }

    public double maximumDouble(long columnIndex) {
        return nativeMaximumDouble(this.nativePtr, columnIndex);
    }

    public double minimumDouble(long columnIndex) {
        return nativeMinimumDouble(this.nativePtr, columnIndex);
    }

    public double averageDouble(long columnIndex) {
        return nativeAverageDouble(this.nativePtr, columnIndex);
    }

    public Date maximumDate(long columnIndex) {
        return new Date(nativeMaximumDate(this.nativePtr, columnIndex) * 1000);
    }

    public Date minimumDate(long columnIndex) {
        return new Date(nativeMinimumDate(this.nativePtr, columnIndex) * 1000);
    }

    public void sort(long columnIndex, Order order) {
        nativeSort(this.nativePtr, columnIndex, order == Order.ascending);
    }

    public void sort(long columnIndex) {
        nativeSort(this.nativePtr, columnIndex, true);
    }

    public void sort(List<Long> columnIndices, List<Order> order) {
        int i;
        long[] indices = new long[columnIndices.size()];
        boolean[] sortOrder = new boolean[order.size()];
        for (i = 0; i < columnIndices.size(); i++) {
            indices[i] = ((Long) columnIndices.get(i)).longValue();
        }
        for (i = 0; i < order.size(); i++) {
            sortOrder[i] = order.get(i) == Order.ascending;
        }
        nativeSortMulti(this.nativePtr, indices, sortOrder);
    }

    public String toJson() {
        return nativeToJson(this.nativePtr);
    }

    public String toString() {
        return nativeToString(this.nativePtr, 500);
    }

    public String toString(long maxRows) {
        return nativeToString(this.nativePtr, maxRows);
    }

    public String rowToString(long rowIndex) {
        return nativeRowToString(this.nativePtr, rowIndex);
    }

    public TableQuery where() {
        this.context.executeDelayedDisposal();
        long nativeQueryPtr = nativeWhere(this.nativePtr);
        try {
            return new TableQuery(this.context, this.parent, nativeQueryPtr, this);
        } catch (RuntimeException e) {
            TableQuery.nativeClose(nativeQueryPtr);
            throw e;
        }
    }

    private void throwImmutable() {
        throw new IllegalStateException("Mutable method call during read transaction.");
    }

    public long count(long columnIndex, String value) {
        throw new RuntimeException("Not implemented yet.");
    }

    public Table pivot(long stringCol, long intCol, PivotType pivotType) {
        if (!getColumnType(stringCol).equals(ColumnType.STRING)) {
            throw new UnsupportedOperationException("Group by column must be of type String");
        } else if (getColumnType(intCol).equals(ColumnType.INTEGER)) {
            Table result = new Table();
            nativePivot(this.nativePtr, stringCol, intCol, pivotType.value, result.nativePtr);
            return result;
        } else {
            throw new UnsupportedOperationException("Aggregeation column must be of type Int");
        }
    }

    public long sync() {
        return nativeSync(this.nativePtr);
    }
}
