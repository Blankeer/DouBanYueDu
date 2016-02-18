package io.realm.internal;

import io.realm.exceptions.RealmIOException;
import java.io.Closeable;
import java.io.IOError;

public class SharedGroup implements Closeable {
    private boolean activeTransaction;
    private final Context context;
    private boolean implicitTransactionsEnabled;
    private long nativePtr;
    private long nativeReplicationPtr;
    private final String path;

    public enum Durability {
        FULL(0),
        MEM_ONLY(1);
        
        final int value;

        private Durability(int value) {
            this.value = value;
        }
    }

    private native long createNativeWithImplicitTransactions(long j, int i, byte[] bArr);

    private native void nativeAdvanceRead(long j);

    private native long nativeBeginImplicit(long j);

    private native long nativeBeginRead(long j);

    private native long nativeBeginWrite(long j);

    protected static native void nativeClose(long j);

    private native void nativeCloseReplication(long j);

    private native void nativeCommit(long j);

    private native void nativeCommitAndContinueAsRead(long j);

    private native boolean nativeCompact(long j);

    private native long nativeCreate(String str, int i, boolean z, boolean z2, byte[] bArr);

    private native long nativeCreateReplication(String str, byte[] bArr);

    private native void nativeEndRead(long j);

    private native String nativeGetDefaultReplicationDatabaseFileName();

    private native boolean nativeHasChanged(long j);

    private native void nativePromoteToWrite(long j);

    private native void nativeReserve(long j, long j2);

    private native void nativeRollback(long j);

    private native void nativeRollbackAndContinueAsRead(long j);

    static {
        RealmCore.loadLibrary();
    }

    public SharedGroup(String databaseFile) {
        this.implicitTransactionsEnabled = false;
        this.context = new Context();
        this.path = databaseFile;
        this.nativePtr = nativeCreate(databaseFile, Durability.FULL.value, false, false, null);
        checkNativePtrNotZero();
    }

    public SharedGroup(String databaseFile, boolean enableImplicitTransactions, Durability durability, byte[] key) {
        this.implicitTransactionsEnabled = false;
        if (enableImplicitTransactions) {
            this.nativeReplicationPtr = nativeCreateReplication(databaseFile, key);
            this.nativePtr = createNativeWithImplicitTransactions(this.nativeReplicationPtr, durability.value, key);
            this.implicitTransactionsEnabled = true;
        } else {
            this.nativePtr = nativeCreate(databaseFile, Durability.FULL.value, false, false, key);
        }
        this.context = new Context();
        this.path = databaseFile;
        checkNativePtrNotZero();
    }

    public SharedGroup(String databaseFile, Durability durability, byte[] key) {
        this.implicitTransactionsEnabled = false;
        this.path = databaseFile;
        this.context = new Context();
        this.nativePtr = nativeCreate(databaseFile, durability.value, false, false, key);
        checkNativePtrNotZero();
    }

    public SharedGroup(String databaseFile, Durability durability, boolean fileMustExist) {
        this.implicitTransactionsEnabled = false;
        this.path = databaseFile;
        this.context = new Context();
        this.nativePtr = nativeCreate(databaseFile, durability.value, fileMustExist, false, null);
        checkNativePtrNotZero();
    }

    void advanceRead() {
        nativeAdvanceRead(this.nativePtr);
    }

    void promoteToWrite() {
        nativePromoteToWrite(this.nativePtr);
    }

    void commitAndContinueAsRead() {
        nativeCommitAndContinueAsRead(this.nativePtr);
    }

    void rollbackAndContinueAsRead() {
        nativeRollbackAndContinueAsRead(this.nativePtr);
    }

    public ImplicitTransaction beginImplicitTransaction() {
        if (this.activeTransaction) {
            throw new IllegalStateException("Can't beginImplicitTransaction() during another active transaction");
        }
        ImplicitTransaction transaction = new ImplicitTransaction(this.context, this, nativeBeginImplicit(this.nativePtr));
        this.activeTransaction = true;
        return transaction;
    }

    public WriteTransaction beginWrite() {
        if (this.activeTransaction) {
            throw new IllegalStateException("Can't beginWrite() during another active transaction");
        }
        long nativeWritePtr = nativeBeginWrite(this.nativePtr);
        try {
            WriteTransaction t = new WriteTransaction(this.context, this, nativeWritePtr);
            this.activeTransaction = true;
            return t;
        } catch (RuntimeException e) {
            Group.nativeClose(nativeWritePtr);
            throw e;
        }
    }

    public ReadTransaction beginRead() {
        if (this.activeTransaction) {
            throw new IllegalStateException("Can't beginRead() during another active transaction");
        }
        long nativeReadPtr = nativeBeginRead(this.nativePtr);
        try {
            ReadTransaction t = new ReadTransaction(this.context, this, nativeReadPtr);
            this.activeTransaction = true;
            return t;
        } catch (RuntimeException e) {
            Group.nativeClose(nativeReadPtr);
            throw e;
        }
    }

    void endRead() {
        if (isClosed()) {
            throw new IllegalStateException("Can't endRead() on closed group. ReadTransaction is invalid.");
        }
        nativeEndRead(this.nativePtr);
        this.activeTransaction = false;
    }

    public void close() {
        synchronized (this.context) {
            if (this.nativePtr != 0) {
                nativeClose(this.nativePtr);
                this.nativePtr = 0;
                if (this.implicitTransactionsEnabled && this.nativeReplicationPtr != 0) {
                    nativeCloseReplication(this.nativeReplicationPtr);
                    this.nativeReplicationPtr = 0;
                }
            }
        }
    }

    protected void finalize() {
        synchronized (this.context) {
            if (this.nativePtr != 0) {
                this.context.asyncDisposeSharedGroup(this.nativePtr);
                this.nativePtr = 0;
                if (this.implicitTransactionsEnabled && this.nativeReplicationPtr != 0) {
                    nativeCloseReplication(this.nativeReplicationPtr);
                    this.nativeReplicationPtr = 0;
                }
            }
        }
    }

    void commit() {
        if (isClosed()) {
            throw new IllegalStateException("Can't commit() on closed group. WriteTransaction is invalid.");
        }
        nativeCommit(this.nativePtr);
        this.activeTransaction = false;
    }

    void rollback() {
        if (isClosed()) {
            throw new IllegalStateException("Can't rollback() on closed group. WriteTransaction is invalid.");
        }
        nativeRollback(this.nativePtr);
        this.activeTransaction = false;
    }

    boolean isClosed() {
        return this.nativePtr == 0;
    }

    public boolean hasChanged() {
        return nativeHasChanged(this.nativePtr);
    }

    public void reserve(long bytes) {
        nativeReserve(this.nativePtr, bytes);
    }

    public boolean compact() {
        return nativeCompact(this.nativePtr);
    }

    public String getPath() {
        return this.path;
    }

    private void checkNativePtrNotZero() {
        if (this.nativePtr == 0) {
            throw new IOError(new RealmIOException("Realm could not be opened"));
        }
    }
}
