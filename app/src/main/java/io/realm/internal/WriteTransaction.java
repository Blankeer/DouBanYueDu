package io.realm.internal;

public class WriteTransaction extends Group {
    private boolean committed;
    private final SharedGroup db;

    public void commit() {
        if (this.committed) {
            throw new IllegalStateException("You can only commit once after a WriteTransaction has been made.");
        }
        this.db.commit();
        this.committed = true;
    }

    public void rollback() {
        this.db.rollback();
    }

    public void close() {
        if (!this.committed) {
            rollback();
        }
    }

    WriteTransaction(Context context, SharedGroup db, long nativePtr) {
        super(context, nativePtr, false);
        this.db = db;
        this.committed = false;
    }

    protected void finalize() {
    }
}
