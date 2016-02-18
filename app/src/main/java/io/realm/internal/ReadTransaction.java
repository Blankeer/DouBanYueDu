package io.realm.internal;

public class ReadTransaction extends Group {
    private final SharedGroup db;

    ReadTransaction(Context context, SharedGroup db, long nativePointer) {
        super(context, nativePointer, true);
        this.db = db;
    }

    public void endRead() {
        this.db.endRead();
    }

    public void close() {
        this.db.endRead();
    }

    protected void finalize() {
    }
}
