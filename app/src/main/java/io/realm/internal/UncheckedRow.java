package io.realm.internal;

import java.util.Date;

public class UncheckedRow extends NativeObject implements Row {
    final Context context;
    final Table parent;

    protected static native void nativeClose(long j);

    protected native boolean nativeGetBoolean(long j, long j2);

    protected native byte[] nativeGetByteArray(long j, long j2);

    protected native long nativeGetColumnCount(long j);

    protected native long nativeGetColumnIndex(long j, String str);

    protected native String nativeGetColumnName(long j, long j2);

    protected native int nativeGetColumnType(long j, long j2);

    protected native long nativeGetDateTime(long j, long j2);

    protected native double nativeGetDouble(long j, long j2);

    protected native float nativeGetFloat(long j, long j2);

    protected native long nativeGetIndex(long j);

    protected native long nativeGetLink(long j, long j2);

    protected native long nativeGetLinkView(long j, long j2);

    protected native long nativeGetLong(long j, long j2);

    protected native Mixed nativeGetMixed(long j, long j2);

    protected native int nativeGetMixedType(long j, long j2);

    protected native String nativeGetString(long j, long j2);

    protected native boolean nativeHasColumn(long j, String str);

    protected native boolean nativeIsAttached(long j);

    protected native boolean nativeIsNullLink(long j, long j2);

    protected native void nativeNullifyLink(long j, long j2);

    protected native void nativeSetBoolean(long j, long j2, boolean z);

    protected native void nativeSetByteArray(long j, long j2, byte[] bArr);

    protected native void nativeSetDate(long j, long j2, long j3);

    protected native void nativeSetDouble(long j, long j2, double d);

    protected native void nativeSetFloat(long j, long j2, float f);

    protected native void nativeSetLink(long j, long j2, long j3);

    protected native void nativeSetLong(long j, long j2, long j3);

    protected native void nativeSetMixed(long j, long j2, Mixed mixed);

    protected native void nativeSetString(long j, long j2, String str);

    protected UncheckedRow(Context context, Table parent, long nativePtr) {
        this.context = context;
        this.parent = parent;
        this.nativePointer = nativePtr;
        context.cleanRows();
    }

    public static UncheckedRow get(Context context, Table table, long index) {
        UncheckedRow row = new UncheckedRow(context, table, table.nativeGetRowPtr(table.nativePtr, index));
        context.rowReferences.add(new NativeObjectReference(row, context.referenceQueue));
        return row;
    }

    public static UncheckedRow get(Context context, LinkView linkView, long index) {
        UncheckedRow row = new UncheckedRow(context, linkView.parent.getLinkTarget(linkView.columnIndexInParent), linkView.nativeGetRow(linkView.nativeLinkViewPtr, index));
        context.rowReferences.add(new NativeObjectReference(row, context.referenceQueue));
        return row;
    }

    public long getColumnCount() {
        return nativeGetColumnCount(this.nativePointer);
    }

    public String getColumnName(long columnIndex) {
        return nativeGetColumnName(this.nativePointer, columnIndex);
    }

    public long getColumnIndex(String columnName) {
        if (columnName != null) {
            return nativeGetColumnIndex(this.nativePointer, columnName);
        }
        throw new IllegalArgumentException("Column name can not be null.");
    }

    public ColumnType getColumnType(long columnIndex) {
        return ColumnType.fromNativeValue(nativeGetColumnType(this.nativePointer, columnIndex));
    }

    public Table getTable() {
        return this.parent;
    }

    public long getIndex() {
        return nativeGetIndex(this.nativePointer);
    }

    public long getLong(long columnIndex) {
        return nativeGetLong(this.nativePointer, columnIndex);
    }

    public boolean getBoolean(long columnIndex) {
        return nativeGetBoolean(this.nativePointer, columnIndex);
    }

    public float getFloat(long columnIndex) {
        return nativeGetFloat(this.nativePointer, columnIndex);
    }

    public double getDouble(long columnIndex) {
        return nativeGetDouble(this.nativePointer, columnIndex);
    }

    public Date getDate(long columnIndex) {
        return new Date(nativeGetDateTime(this.nativePointer, columnIndex) * 1000);
    }

    public String getString(long columnIndex) {
        return nativeGetString(this.nativePointer, columnIndex);
    }

    public byte[] getBinaryByteArray(long columnIndex) {
        return nativeGetByteArray(this.nativePointer, columnIndex);
    }

    public Mixed getMixed(long columnIndex) {
        return nativeGetMixed(this.nativePointer, columnIndex);
    }

    public ColumnType getMixedType(long columnIndex) {
        return ColumnType.fromNativeValue(nativeGetMixedType(this.nativePointer, columnIndex));
    }

    public long getLink(long columnIndex) {
        return nativeGetLink(this.nativePointer, columnIndex);
    }

    public boolean isNullLink(long columnIndex) {
        return nativeIsNullLink(this.nativePointer, columnIndex);
    }

    public LinkView getLinkList(long columnIndex) {
        return new LinkView(this.context, this.parent, columnIndex, nativeGetLinkView(this.nativePointer, columnIndex));
    }

    public void setLong(long columnIndex, long value) {
        this.parent.checkImmutable();
        getTable().checkIntValueIsLegal(columnIndex, getIndex(), value);
        nativeSetLong(this.nativePointer, columnIndex, value);
    }

    public void setBoolean(long columnIndex, boolean value) {
        this.parent.checkImmutable();
        nativeSetBoolean(this.nativePointer, columnIndex, value);
    }

    public void setFloat(long columnIndex, float value) {
        this.parent.checkImmutable();
        nativeSetFloat(this.nativePointer, columnIndex, value);
    }

    public void setDouble(long columnIndex, double value) {
        this.parent.checkImmutable();
        nativeSetDouble(this.nativePointer, columnIndex, value);
    }

    public void setDate(long columnIndex, Date date) {
        this.parent.checkImmutable();
        if (date == null) {
            throw new IllegalArgumentException("Null Date is not allowed.");
        }
        long timestamp = date.getTime() / 1000;
        if (timestamp >= 2147483647L || timestamp <= -2147483648L) {
            throw new IllegalArgumentException("Date/timestamp is outside valid range");
        }
        nativeSetDate(this.nativePointer, columnIndex, timestamp);
    }

    public void setString(long columnIndex, String value) {
        this.parent.checkImmutable();
        getTable().checkStringValueIsLegal(columnIndex, getIndex(), value);
        nativeSetString(this.nativePointer, columnIndex, value);
    }

    public void setBinaryByteArray(long columnIndex, byte[] data) {
        this.parent.checkImmutable();
        if (data == null) {
            throw new IllegalArgumentException("Null array is not allowed");
        }
        nativeSetByteArray(this.nativePointer, columnIndex, data);
    }

    public void setMixed(long columnIndex, Mixed data) {
        this.parent.checkImmutable();
        if (data == null) {
            throw new IllegalArgumentException("Null data is not allowed");
        }
        nativeSetMixed(this.nativePointer, columnIndex, data);
    }

    public void setLink(long columnIndex, long value) {
        this.parent.checkImmutable();
        nativeSetLink(this.nativePointer, columnIndex, value);
    }

    public void nullifyLink(long columnIndex) {
        this.parent.checkImmutable();
        nativeNullifyLink(this.nativePointer, columnIndex);
    }

    public CheckedRow convertToChecked() {
        return CheckedRow.getFromRow(this);
    }

    public boolean isAttached() {
        return this.nativePointer != 0 && nativeIsAttached(this.nativePointer);
    }

    public boolean hasColumn(String fieldName) {
        return nativeHasColumn(this.nativePointer, fieldName);
    }
}
