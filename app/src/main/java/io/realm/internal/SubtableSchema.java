package io.realm.internal;

public class SubtableSchema implements TableSchema {
    private long parentNativePtr;
    private long[] path;

    protected native long nativeAddColumn(long j, long[] jArr, int i, String str);

    protected native void nativeRemoveColumn(long j, long[] jArr, long j2);

    protected native void nativeRenameColumn(long j, long[] jArr, long j2, String str);

    SubtableSchema(long parentNativePtr, long[] path) {
        this.parentNativePtr = parentNativePtr;
        this.path = path;
    }

    public SubtableSchema getSubtableSchema(long columnIndex) {
        long[] newPath = new long[(this.path.length + 1)];
        for (int i = 0; i < this.path.length; i++) {
            newPath[i] = this.path[i];
        }
        newPath[this.path.length] = columnIndex;
        return new SubtableSchema(this.parentNativePtr, newPath);
    }

    private void verifyColumnName(String name) {
        if (name.length() > 63) {
            throw new IllegalArgumentException("Column names are currently limited to max 63 characters.");
        }
    }

    public long addColumn(ColumnType type, String name) {
        verifyColumnName(name);
        return nativeAddColumn(this.parentNativePtr, this.path, type.getValue(), name);
    }

    public void removeColumn(long columnIndex) {
        nativeRemoveColumn(this.parentNativePtr, this.path, columnIndex);
    }

    public void renameColumn(long columnIndex, String newName) {
        verifyColumnName(newName);
        nativeRenameColumn(this.parentNativePtr, this.path, columnIndex, newName);
    }
}
