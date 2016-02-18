package io.realm.internal;

public class ImplicitTransaction extends Group {
    private final SharedGroup parent;

    public ImplicitTransaction(Context context, SharedGroup sharedGroup, long nativePtr) {
        super(context, nativePtr, true);
        this.parent = sharedGroup;
    }

    public void advanceRead() {
        assertNotClosed();
        this.parent.advanceRead();
    }

    public void promoteToWrite() {
        assertNotClosed();
        if (this.immutable) {
            this.immutable = false;
            this.parent.promoteToWrite();
            return;
        }
        throw new IllegalStateException("Nested transactions are not allowed. Use commitTransaction() after each beginTransaction().");
    }

    public void commitAndContinueAsRead() {
        assertNotClosed();
        this.parent.commitAndContinueAsRead();
        this.immutable = true;
    }

    public void endRead() {
        assertNotClosed();
        this.parent.endRead();
    }

    public void rollbackAndContinueAsRead() {
        assertNotClosed();
        if (this.immutable) {
            throw new IllegalStateException("Cannot cancel a non-write transaction.");
        }
        this.parent.rollbackAndContinueAsRead();
        this.immutable = true;
    }

    private void assertNotClosed() {
        if (isClosed() || this.parent.isClosed()) {
            throw new IllegalStateException("Cannot use ImplicitTransaction after it or its parent has been closed.");
        }
    }

    public String getPath() {
        return this.parent.getPath();
    }

    protected void finalize() {
    }
}
