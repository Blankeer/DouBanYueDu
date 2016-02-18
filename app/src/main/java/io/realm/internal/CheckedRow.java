package io.realm.internal;

public class CheckedRow extends UncheckedRow {
    private UncheckedRow originalRow;

    protected native boolean nativeGetBoolean(long j, long j2);

    protected native byte[] nativeGetByteArray(long j, long j2);

    protected native long nativeGetColumnCount(long j);

    protected native long nativeGetColumnIndex(long j, String str);

    protected native String nativeGetColumnName(long j, long j2);

    protected native int nativeGetColumnType(long j, long j2);

    protected native long nativeGetDateTime(long j, long j2);

    protected native double nativeGetDouble(long j, long j2);

    protected native float nativeGetFloat(long j, long j2);

    protected native long nativeGetLink(long j, long j2);

    protected native long nativeGetLinkView(long j, long j2);

    protected native long nativeGetLong(long j, long j2);

    protected native Mixed nativeGetMixed(long j, long j2);

    protected native int nativeGetMixedType(long j, long j2);

    protected native String nativeGetString(long j, long j2);

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

    private CheckedRow(Context context, Table parent, long nativePtr) {
        super(context, parent, nativePtr);
    }

    private CheckedRow(UncheckedRow row) {
        super(row.context, row.parent, row.nativePointer);
        this.originalRow = row;
    }

    public static CheckedRow get(Context context, Table table, long index) {
        CheckedRow row = new CheckedRow(context, table, table.nativeGetRowPtr(table.nativePtr, index));
        context.rowReferences.add(new NativeObjectReference(row, context.referenceQueue));
        return row;
    }

    public static CheckedRow get(Context context, LinkView linkView, long index) {
        CheckedRow row = new CheckedRow(context, linkView.parent.getLinkTarget(linkView.columnIndexInParent), linkView.nativeGetRow(linkView.nativeLinkViewPtr, index));
        context.rowReferences.add(new NativeObjectReference(row, context.referenceQueue));
        return row;
    }

    public static CheckedRow getFromRow(UncheckedRow row) {
        return new CheckedRow(row);
    }

    public boolean isNullLink(long columnIndex) {
        ColumnType columnType = getColumnType(columnIndex);
        if (columnType == ColumnType.LINK || columnType == ColumnType.LINK_LIST) {
            return super.isNullLink(columnIndex);
        }
        return false;
    }
}
