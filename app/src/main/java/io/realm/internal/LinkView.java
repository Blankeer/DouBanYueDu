package io.realm.internal;

public class LinkView {
    final long columnIndexInParent;
    private final Context context;
    final long nativeLinkViewPtr;
    final Table parent;

    private native void nativeAdd(long j, long j2);

    private native void nativeClear(long j);

    protected static native void nativeClose(long j);

    private native long nativeGetTargetRowIndex(long j, long j2);

    private native void nativeInsert(long j, long j2, long j3);

    private native boolean nativeIsEmpty(long j);

    private native void nativeMove(long j, long j2, long j3);

    private native void nativeRemove(long j, long j2);

    private native void nativeSet(long j, long j2, long j3);

    private native long nativeSize(long j);

    native long nativeGetRow(long j, long j2);

    protected native long nativeWhere(long j);

    public LinkView(Context context, Table parent, long columnIndexInParent, long nativeLinkViewPtr) {
        this.context = context;
        this.parent = parent;
        this.columnIndexInParent = columnIndexInParent;
        this.nativeLinkViewPtr = nativeLinkViewPtr;
    }

    public UncheckedRow getUncheckedRow(long index) {
        return UncheckedRow.get(this.context, this, index);
    }

    public CheckedRow getCheckedRow(long index) {
        return CheckedRow.get(this.context, this, index);
    }

    public long getTargetRowIndex(long pos) {
        return nativeGetTargetRowIndex(this.nativeLinkViewPtr, pos);
    }

    public void add(long rowIndex) {
        checkImmutable();
        nativeAdd(this.nativeLinkViewPtr, rowIndex);
    }

    public void insert(long pos, long rowIndex) {
        checkImmutable();
        nativeInsert(this.nativeLinkViewPtr, pos, rowIndex);
    }

    public void set(long pos, long rowIndex) {
        checkImmutable();
        nativeSet(this.nativeLinkViewPtr, pos, rowIndex);
    }

    public void move(long oldPos, long newPos) {
        checkImmutable();
        nativeMove(this.nativeLinkViewPtr, oldPos, newPos);
    }

    public void remove(long pos) {
        checkImmutable();
        nativeRemove(this.nativeLinkViewPtr, pos);
    }

    public void clear() {
        checkImmutable();
        nativeClear(this.nativeLinkViewPtr);
    }

    public long size() {
        return nativeSize(this.nativeLinkViewPtr);
    }

    public boolean isEmpty() {
        return nativeIsEmpty(this.nativeLinkViewPtr);
    }

    public TableQuery where() {
        this.context.executeDelayedDisposal();
        long nativeQueryPtr = nativeWhere(this.nativeLinkViewPtr);
        try {
            return new TableQuery(this.context, this.parent, nativeQueryPtr);
        } catch (RuntimeException e) {
            TableQuery.nativeClose(nativeQueryPtr);
            throw e;
        }
    }

    public Table getTable() {
        return this.parent;
    }

    private void checkImmutable() {
        if (this.parent.isImmutable()) {
            throw new IllegalStateException("Changing Realm data can only be done from inside a transaction.");
        }
    }
}
