package io.realm.internal;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Group implements Closeable {
    private final Context context;
    protected boolean immutable;
    protected long nativePtr;

    public enum OpenMode {
        READ_ONLY(0),
        READ_WRITE(1),
        READ_WRITE_NO_CREATE(2);
        
        private int value;

        private OpenMode(int value) {
            this.value = value;
        }
    }

    protected static native void nativeClose(long j);

    protected static native long nativeLoadFromMem(byte[] bArr);

    protected native long createNative();

    protected native long createNative(String str, int i);

    protected native long createNative(ByteBuffer byteBuffer);

    protected native long createNative(byte[] bArr);

    protected native void nativeCommit(long j);

    protected native String nativeGetTableName(long j, int i);

    protected native long nativeGetTableNativePtr(long j, String str);

    protected native boolean nativeHasTable(long j, String str);

    protected native long nativeSize(long j);

    protected native String nativeToJson(long j);

    protected native String nativeToString(long j);

    protected native void nativeWriteToFile(long j, String str, byte[] bArr) throws IOException;

    protected native byte[] nativeWriteToMem(long j);

    static {
        RealmCore.loadLibrary();
    }

    private void checkNativePtrNotZero() {
        if (this.nativePtr == 0) {
            throw new OutOfMemoryError("Out of native memory.");
        }
    }

    public Group() {
        this.immutable = false;
        this.context = new Context();
        this.nativePtr = createNative();
        checkNativePtrNotZero();
    }

    public Group(String filepath, OpenMode mode) {
        this.immutable = mode.equals(OpenMode.READ_ONLY);
        this.context = new Context();
        this.nativePtr = createNative(filepath, mode.value);
        checkNativePtrNotZero();
    }

    public Group(String filepath) {
        this(filepath, OpenMode.READ_ONLY);
    }

    public Group(File file) {
        this(file.getAbsolutePath(), file.canWrite() ? OpenMode.READ_WRITE : OpenMode.READ_ONLY);
    }

    public Group(byte[] data) {
        this.immutable = false;
        this.context = new Context();
        if (data != null) {
            this.nativePtr = createNative(data);
            checkNativePtrNotZero();
            return;
        }
        throw new IllegalArgumentException();
    }

    public Group(ByteBuffer buffer) {
        this.immutable = false;
        this.context = new Context();
        if (buffer != null) {
            this.nativePtr = createNative(buffer);
            checkNativePtrNotZero();
            return;
        }
        throw new IllegalArgumentException();
    }

    Group(Context context, long nativePointer, boolean immutable) {
        this.context = context;
        this.nativePtr = nativePointer;
        this.immutable = immutable;
    }

    public void close() {
        synchronized (this.context) {
            if (this.nativePtr != 0) {
                nativeClose(this.nativePtr);
                this.nativePtr = 0;
            }
        }
    }

    boolean isClosed() {
        return this.nativePtr == 0;
    }

    protected void finalize() {
        synchronized (this.context) {
            if (this.nativePtr != 0) {
                this.context.asyncDisposeGroup(this.nativePtr);
                this.nativePtr = 0;
            }
        }
    }

    private void verifyGroupIsValid() {
        if (this.nativePtr == 0) {
            throw new IllegalStateException("Illegal to call methods on a closed Group.");
        }
    }

    public long size() {
        verifyGroupIsValid();
        return nativeSize(this.nativePtr);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean hasTable(String name) {
        verifyGroupIsValid();
        return name != null && nativeHasTable(this.nativePtr, name);
    }

    public String getTableName(int index) {
        verifyGroupIsValid();
        long cnt = size();
        if (index >= 0 && ((long) index) < cnt) {
            return nativeGetTableName(this.nativePtr, index);
        }
        throw new IndexOutOfBoundsException("Table index argument is out of range. possible range is [0, " + (cnt - 1) + "]");
    }

    public Table getTable(String name) {
        verifyGroupIsValid();
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid name. Name must be a non-empty String.");
        } else if (!this.immutable || hasTable(name)) {
            this.context.executeDelayedDisposal();
            long nativeTablePointer = nativeGetTableNativePtr(this.nativePtr, name);
            try {
                return new Table(this.context, this, nativeTablePointer);
            } catch (RuntimeException e) {
                Table.nativeClose(nativeTablePointer);
                throw e;
            }
        } else {
            throw new IllegalStateException("Requested table is not in this Realm. Creating it requires a transaction: " + name);
        }
    }

    public void writeToFile(File file, byte[] key) throws IOException {
        verifyGroupIsValid();
        if (file.isFile() && file.exists()) {
            throw new IllegalArgumentException("The destination file must not exist");
        } else if (key == null || key.length == 64) {
            nativeWriteToFile(this.nativePtr, file.getAbsolutePath(), key);
        } else {
            throw new IllegalArgumentException("Realm AES keys must be 64 bytes long");
        }
    }

    public byte[] writeToMem() {
        verifyGroupIsValid();
        return nativeWriteToMem(this.nativePtr);
    }

    public void commit() {
        verifyGroupIsValid();
        nativeCommit(this.nativePtr);
    }

    public String toJson() {
        return nativeToJson(this.nativePtr);
    }

    public String toString() {
        return nativeToString(this.nativePtr);
    }

    private void throwImmutable() {
        throw new IllegalStateException("Objects cannot be changed outside a transaction; see beginTransaction() for details.");
    }
}
