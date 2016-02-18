package org.mapdb;

import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import io.realm.internal.Table;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import org.mapdb.Volume.ByteBufferVol;
import org.mapdb.Volume.Factory;
import org.mapdb.Volume.FileChannelVol;
import org.mapdb.Volume.MappedFileVol;
import org.mapdb.Volume.MemoryVol;

public class StoreDirect extends Store {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final String DATA_FILE_EXT = ".p";
    protected static final int HEADER = 234243482;
    protected static final int IO_FREE_RECID = 120;
    protected static final int IO_FREE_SIZE = 24;
    protected static final int IO_INDEX_SIZE = 8;
    protected static final int IO_INDEX_SUM = 32;
    protected static final int IO_PHYS_SIZE = 16;
    protected static final int IO_USER_START = 32896;
    protected static final int LONG_STACK_PREF_COUNT = 204;
    protected static final int LONG_STACK_PREF_COUNT_ALTER = 212;
    protected static final long LONG_STACK_PREF_SIZE = 1232;
    protected static final long LONG_STACK_PREF_SIZE_ALTER = 1280;
    protected static final long MASK_ARCHIVE = 2;
    protected static final long MASK_DISCARD = 4;
    protected static final long MASK_LINKED = 8;
    protected static final long MASK_OFFSET = 281474976710640L;
    protected static final int MAX_REC_SIZE = 65535;
    protected static final int PHYS_FREE_SLOTS_COUNT = 4096;
    protected static final short STORE_VERSION = (short) 10000;
    protected final boolean deleteFilesAfterClose;
    protected long freeSize;
    protected Volume index;
    protected long indexSize;
    protected long maxUsedIoList;
    protected Volume phys;
    protected long physSize;
    protected final boolean readOnly;
    protected final long sizeLimit;
    protected final boolean spaceReclaimReuse;
    protected final boolean spaceReclaimSplit;
    protected final boolean spaceReclaimTrack;
    protected final boolean syncOnCommitDisabled;

    static {
        $assertionsDisabled = !StoreDirect.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public StoreDirect(Factory volFac, boolean readOnly, boolean deleteFilesAfterClose, int spaceReclaimMode, boolean syncOnCommitDisabled, long sizeLimit, boolean checksum, boolean compress, byte[] password, boolean disableLocks, int sizeIncrement) {
        super(checksum, compress, password, disableLocks);
        this.maxUsedIoList = 0;
        this.readOnly = readOnly;
        this.deleteFilesAfterClose = deleteFilesAfterClose;
        this.syncOnCommitDisabled = syncOnCommitDisabled;
        this.sizeLimit = sizeLimit;
        this.spaceReclaimSplit = spaceReclaimMode > 4 ? true : $assertionsDisabled;
        this.spaceReclaimReuse = spaceReclaimMode > 2 ? true : $assertionsDisabled;
        this.spaceReclaimTrack = spaceReclaimMode > 0 ? true : $assertionsDisabled;
        try {
            this.index = volFac.createIndexVolume();
            this.phys = volFac.createPhysVolume();
            if (this.index.isEmpty()) {
                createStructure();
            } else {
                checkHeaders();
                this.indexSize = this.index.getLong(MASK_LINKED);
                this.physSize = this.index.getLong(16);
                this.freeSize = this.index.getLong(24);
                this.maxUsedIoList = 32888;
                while (this.index.getLong(this.maxUsedIoList) != 0 && this.maxUsedIoList > 120) {
                    this.maxUsedIoList -= MASK_LINKED;
                }
            }
            if (!true) {
                if (this.index != null) {
                    this.index.sync();
                    this.index.close();
                    this.index = null;
                }
                if (this.phys != null) {
                    this.phys.sync();
                    this.phys.close();
                    this.phys = null;
                }
            }
        } catch (Throwable th) {
            if (!$assertionsDisabled) {
                if (this.index != null) {
                    this.index.sync();
                    this.index.close();
                    this.index = null;
                }
                if (this.phys != null) {
                    this.phys.sync();
                    this.phys.close();
                    this.phys = null;
                }
            }
        }
    }

    public StoreDirect(Factory volFac) {
        this(volFac, $assertionsDisabled, $assertionsDisabled, 5, $assertionsDisabled, 0, $assertionsDisabled, $assertionsDisabled, null, $assertionsDisabled, 0);
    }

    protected void checkHeaders() {
        if (this.index.getInt(0) != HEADER || this.phys.getInt(0) != HEADER) {
            throw new IOError(new IOException("storage has invalid header"));
        } else if (this.index.getUnsignedShort(MASK_DISCARD) > AbstractSpiCall.DEFAULT_TIMEOUT || this.phys.getUnsignedShort(MASK_DISCARD) > AbstractSpiCall.DEFAULT_TIMEOUT) {
            throw new IOError(new IOException("New store format version, please use newer MapDB version"));
        } else {
            int masks = this.index.getUnsignedShort(6);
            if (masks != this.phys.getUnsignedShort(6)) {
                throw new IllegalArgumentException("Index and Phys file have different feature masks");
            } else if (masks != expectedMasks()) {
                throw new IllegalArgumentException("File created with different features. Please check compression, checksum or encryption");
            } else if (this.index.getLong(32) != indexHeaderChecksum()) {
                throw new IOError(new IOException("Wrong index checksum, store was not closed properly and could be corrupted."));
            }
        }
    }

    protected void createStructure() {
        this.indexSize = 32960;
        if ($assertionsDisabled || this.indexSize > 32896) {
            this.index.ensureAvailable(this.indexSize);
            for (int i = 0; ((long) i) < this.indexSize; i += IO_INDEX_SIZE) {
                this.index.putLong((long) i, 0);
            }
            this.index.putInt(0, HEADER);
            this.index.putUnsignedShort(MASK_DISCARD, AbstractSpiCall.DEFAULT_TIMEOUT);
            this.index.putUnsignedShort(6, expectedMasks());
            this.index.putLong(MASK_LINKED, this.indexSize);
            this.physSize = 16;
            this.index.putLong(16, this.physSize);
            this.phys.ensureAvailable(this.physSize);
            this.phys.putInt(0, HEADER);
            this.phys.putUnsignedShort(MASK_DISCARD, AbstractSpiCall.DEFAULT_TIMEOUT);
            this.phys.putUnsignedShort(6, expectedMasks());
            this.freeSize = 0;
            this.index.putLong(24, this.freeSize);
            this.index.putLong(32, indexHeaderChecksum());
            return;
        }
        throw new AssertionError();
    }

    protected long indexHeaderChecksum() {
        long ret = 0;
        for (long offset = 0; offset < 32896; offset += MASK_LINKED) {
            if (offset != 32) {
                long indexVal = this.index.getLong(offset);
                ret |= ((long) LongHashMap.longHash(indexVal | offset)) | indexVal;
            }
        }
        return ret;
    }

    public long preallocate() {
        Lock lock;
        this.newRecidLock.readLock().lock();
        try {
            this.structuralLock.lock();
            long ioRecid = freeIoRecidTake(true);
            this.structuralLock.unlock();
            lock = this.locks[Store.lockPos(ioRecid)].writeLock();
            lock.lock();
            this.index.putLong(ioRecid, MASK_DISCARD);
            lock.unlock();
            long recid = (ioRecid - 32896) / MASK_LINKED;
            if ($assertionsDisabled || recid > 0) {
                this.newRecidLock.readLock().unlock();
                return recid;
            }
            throw new AssertionError();
        } catch (Throwable th) {
            this.newRecidLock.readLock().unlock();
        }
    }

    public void preallocate(long[] recids) {
        this.newRecidLock.readLock().lock();
        this.structuralLock.lock();
        int i = 0;
        while (i < recids.length) {
            try {
                recids[i] = freeIoRecidTake(true);
                i++;
            } catch (Throwable th) {
                this.newRecidLock.readLock().unlock();
            }
        }
        this.structuralLock.unlock();
        i = 0;
        while (i < recids.length) {
            long ioRecid = recids[i];
            Lock lock = this.locks[Store.lockPos(ioRecid)].writeLock();
            lock.lock();
            this.index.putLong(ioRecid, MASK_DISCARD);
            lock.unlock();
            recids[i] = (ioRecid - 32896) / MASK_LINKED;
            if ($assertionsDisabled || recids[i] > 0) {
                i++;
            } else {
                throw new AssertionError();
            }
        }
        this.newRecidLock.readLock().unlock();
    }

    public <A> long put(A value, Serializer<A> serializer) {
        Lock lock;
        if ($assertionsDisabled || value != null) {
            DataOutput2 out = serialize(value, serializer);
            this.newRecidLock.readLock().lock();
            try {
                this.structuralLock.lock();
                long ioRecid = freeIoRecidTake(true);
                long[] indexVals = physAllocate(out.pos, true, $assertionsDisabled);
                this.structuralLock.unlock();
                lock = this.locks[Store.lockPos(ioRecid)].writeLock();
                lock.lock();
                put2(out, ioRecid, indexVals);
                lock.unlock();
                this.newRecidLock.readLock().unlock();
                long recid = (ioRecid - 32896) / MASK_LINKED;
                if ($assertionsDisabled || recid > 0) {
                    this.recycledDataOuts.offer(out);
                    return recid;
                }
                throw new AssertionError();
            } catch (Throwable th) {
                this.newRecidLock.readLock().unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    protected void put2(DataOutput2 out, long ioRecid, long[] indexVals) {
        if ($assertionsDisabled || this.locks[Store.lockPos(ioRecid)].writeLock().isHeldByCurrentThread()) {
            this.index.putLong(ioRecid, indexVals[0] | MASK_ARCHIVE);
            if (indexVals.length == 1 || indexVals[1] == 0) {
                this.phys.putData(indexVals[0] & MASK_OFFSET, out.buf, 0, out.pos);
                return;
            }
            int outPos = 0;
            int i = 0;
            while (i < indexVals.length) {
                int c = i == indexVals.length + -1 ? 0 : IO_INDEX_SIZE;
                long indexVal = indexVals[i];
                boolean isLast = (MASK_LINKED & indexVal) == 0 ? true : $assertionsDisabled;
                if (!$assertionsDisabled) {
                    if (isLast != (i == indexVals.length + -1 ? true : $assertionsDisabled)) {
                        throw new AssertionError();
                    }
                }
                int size = (int) (indexVal >>> 48);
                long offset = indexVal & MASK_OFFSET;
                this.phys.putData(((long) c) + offset, out.buf, outPos, size - c);
                outPos += size - c;
                if (c > 0) {
                    this.phys.putLong(offset, indexVals[i + 1]);
                }
                i++;
            }
            if (outPos != out.pos) {
                throw new AssertionError();
            }
            return;
        }
        throw new AssertionError();
    }

    public <A> A get(long recid, Serializer<A> serializer) {
        if ($assertionsDisabled || recid > 0) {
            long ioRecid = 32896 + (MASK_LINKED * recid);
            Lock lock = this.locks[Store.lockPos(ioRecid)].readLock();
            lock.lock();
            try {
                A 2 = get2(ioRecid, serializer);
                lock.unlock();
                return 2;
            } catch (IOException e) {
                throw new IOError(e);
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    protected <A> A get2(long ioRecid, Serializer<A> serializer) throws IOException {
        if ($assertionsDisabled || this.locks[Store.lockPos(ioRecid)].getWriteHoldCount() == 0 || this.locks[Store.lockPos(ioRecid)].writeLock().isHeldByCurrentThread()) {
            long indexVal = this.index.getLong(ioRecid);
            if (indexVal == MASK_DISCARD) {
                return null;
            }
            DataInput2 di;
            int size = (int) (indexVal >>> 48);
            long offset = indexVal & MASK_OFFSET;
            if ((MASK_LINKED & indexVal) == 0) {
                di = (DataInput2) this.phys.getDataInput(offset, size);
            } else {
                int pos = 0;
                int c = IO_INDEX_SIZE;
                byte[] buf = new byte[64];
                while (true) {
                    DataInput2 in = (DataInput2) this.phys.getDataInput(((long) c) + offset, size - c);
                    if (buf.length < (pos + size) - c) {
                        buf = Arrays.copyOf(buf, Math.max((pos + size) - c, buf.length * 2));
                    }
                    in.readFully(buf, pos, size - c);
                    pos += size - c;
                    if (c == 0) {
                        break;
                    }
                    long next = this.phys.getLong(offset);
                    offset = next & MASK_OFFSET;
                    size = (int) (next >>> 48);
                    c = (MASK_LINKED & next) == 0 ? 0 : IO_INDEX_SIZE;
                }
                di = new DataInput2(buf);
                size = pos;
            }
            return deserialize(serializer, size, di);
        }
        throw new AssertionError();
    }

    public <A> void update(long recid, A value, Serializer<A> serializer) {
        if (!$assertionsDisabled && value == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || recid > 0) {
            DataOutput2 out = serialize(value, serializer);
            long ioRecid = 32896 + (MASK_LINKED * recid);
            Lock lock = this.locks[Store.lockPos(ioRecid)].writeLock();
            lock.lock();
            try {
                update2(out, ioRecid);
                this.recycledDataOuts.offer(out);
            } finally {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    protected void update2(DataOutput2 out, long ioRecid) {
        long indexVal = this.index.getLong(ioRecid);
        int size = (int) (indexVal >>> 48);
        boolean linked = (MASK_LINKED & indexVal) != 0 ? true : $assertionsDisabled;
        if ($assertionsDisabled || this.locks[Store.lockPos(ioRecid)].writeLock().isHeldByCurrentThread()) {
            if (!linked && out.pos > 0 && size > 0) {
                if (size2ListIoRecid((long) size) == size2ListIoRecid((long) out.pos)) {
                    long offset = indexVal & MASK_OFFSET;
                    this.index.putLong(ioRecid, ((((long) out.pos) << 48) | offset) | MASK_ARCHIVE);
                    this.phys.putData(offset, out.buf, 0, out.pos);
                    if (!$assertionsDisabled && !this.locks[Store.lockPos(ioRecid)].writeLock().isHeldByCurrentThread()) {
                        throw new AssertionError();
                    }
                }
            }
            long[] linkedRecordsIndexVals = this.spaceReclaimTrack ? getLinkedRecordsIndexVals(indexVal) : null;
            this.structuralLock.lock();
            try {
                if (this.spaceReclaimTrack) {
                    if (size > 0) {
                        freePhysPut(indexVal, $assertionsDisabled);
                    }
                    if (linkedRecordsIndexVals != null) {
                        int i = 0;
                        while (i < linkedRecordsIndexVals.length && linkedRecordsIndexVals[i] != 0) {
                            freePhysPut(linkedRecordsIndexVals[i], $assertionsDisabled);
                            i++;
                        }
                    }
                }
                linkedRecordsIndexVals = physAllocate(out.pos, true, $assertionsDisabled);
                put2(out, ioRecid, linkedRecordsIndexVals);
                if (!$assertionsDisabled) {
                }
            } finally {
                this.structuralLock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
        if (!$assertionsDisabled && (expectedOldValue == null || newValue == null)) {
            throw new AssertionError();
        } else if ($assertionsDisabled || recid > 0) {
            long ioRecid = 32896 + (MASK_LINKED * recid);
            Lock lock = this.locks[Store.lockPos(ioRecid)].writeLock();
            lock.lock();
            try {
                A oldVal = get2(ioRecid, serializer);
                if ((oldVal != null || expectedOldValue == null) && (oldVal == null || oldVal.equals(expectedOldValue))) {
                    DataOutput2 out = serialize(newValue, serializer);
                    update2(out, ioRecid);
                    lock.unlock();
                    this.recycledDataOuts.offer(out);
                    return true;
                }
                lock.unlock();
                return $assertionsDisabled;
            } catch (IOException e) {
                throw new IOError(e);
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public <A> void delete(long recid, Serializer<A> serializer) {
        if ($assertionsDisabled || recid > 0) {
            long ioRecid = 32896 + (MASK_LINKED * recid);
            Lock lock = this.locks[Store.lockPos(ioRecid)].writeLock();
            lock.lock();
            try {
                long indexVal = this.index.getLong(ioRecid);
                this.index.putLong(ioRecid, MASK_ARCHIVE);
                if (this.spaceReclaimTrack) {
                    long[] linkedRecords = getLinkedRecordsIndexVals(indexVal);
                    this.structuralLock.lock();
                    freeIoRecidPut(ioRecid);
                    if ((indexVal >>> 48) > 0) {
                        freePhysPut(indexVal, $assertionsDisabled);
                    }
                    if (linkedRecords != null) {
                        int i = 0;
                        while (i < linkedRecords.length && linkedRecords[i] != 0) {
                            freePhysPut(linkedRecords[i], $assertionsDisabled);
                            i++;
                        }
                    }
                    this.structuralLock.unlock();
                    lock.unlock();
                    return;
                }
                lock.unlock();
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    protected long[] getLinkedRecordsIndexVals(long indexVal) {
        long[] linkedRecords = null;
        int linkedPos = 0;
        if ((MASK_LINKED & indexVal) != 0) {
            linkedRecords = new long[2];
            long linkedVal = this.phys.getLong(MASK_OFFSET & indexVal);
            while (true) {
                if (linkedPos == linkedRecords.length) {
                    linkedRecords = Arrays.copyOf(linkedRecords, linkedRecords.length * 2);
                }
                linkedRecords[linkedPos] = linkedVal;
                if ((MASK_LINKED & linkedVal) == 0) {
                    break;
                }
                linkedPos++;
                linkedVal = this.phys.getLong(MASK_OFFSET & linkedVal);
            }
        }
        return linkedRecords;
    }

    protected long[] physAllocate(int size, boolean ensureAvail, boolean recursive) {
        if (!$assertionsDisabled && !this.structuralLock.isHeldByCurrentThread()) {
            throw new AssertionError();
        } else if (((long) size) == 0) {
            return new long[]{0};
        } else if (size < MAX_REC_SIZE) {
            return new long[]{freePhysTake(size, ensureAvail, recursive) | (((long) size) << 48)};
        } else {
            long[] ret = new long[2];
            int c = IO_INDEX_SIZE;
            int retPos = 0;
            while (size > 0) {
                if (retPos == ret.length) {
                    ret = Arrays.copyOf(ret, ret.length * 2);
                }
                int allocSize = Math.min(size, MAX_REC_SIZE);
                size -= allocSize - c;
                long indexVal = freePhysTake(allocSize, ensureAvail, recursive) | (((long) allocSize) << 48);
                if (c != 0) {
                    indexVal |= MASK_LINKED;
                }
                int retPos2 = retPos + 1;
                ret[retPos] = indexVal;
                c = size <= MAX_REC_SIZE ? 0 : IO_INDEX_SIZE;
                retPos = retPos2;
            }
            if (size == 0) {
                return Arrays.copyOf(ret, retPos);
            }
            throw new AssertionError();
        }
    }

    protected static long roundTo16(long offset) {
        long rem = offset & 15;
        if (rem != 0) {
            return offset + (16 - rem);
        }
        return offset;
    }

    public void close() {
        for (Runnable closeListener : this.closeListeners) {
            closeListener.run();
        }
        lockAllWrite();
        try {
            if (!this.readOnly) {
                if (this.serializerPojo != null && this.serializerPojo.hasUnsavedChanges()) {
                    this.serializerPojo.save(this);
                }
                this.index.putLong(16, this.physSize);
                this.index.putLong(MASK_LINKED, this.indexSize);
                this.index.putLong(24, this.freeSize);
                this.index.putLong(32, indexHeaderChecksum());
            }
            if (!this.deleteFilesAfterClose) {
                this.index.sync();
                this.phys.sync();
            }
            this.index.close();
            this.phys.close();
            if (this.deleteFilesAfterClose) {
                this.index.deleteFile();
                this.phys.deleteFile();
            }
            this.index = null;
            this.phys = null;
            unlockAllWrite();
        } catch (Throwable th) {
            unlockAllWrite();
        }
    }

    public boolean isClosed() {
        return this.index == null ? true : $assertionsDisabled;
    }

    public void commit() {
        if (!this.readOnly) {
            if (this.serializerPojo != null && this.serializerPojo.hasUnsavedChanges()) {
                this.serializerPojo.save(this);
            }
            this.index.putLong(16, this.physSize);
            this.index.putLong(MASK_LINKED, this.indexSize);
            this.index.putLong(24, this.freeSize);
            this.index.putLong(32, indexHeaderChecksum());
        }
        if (!this.syncOnCommitDisabled) {
            this.index.sync();
            this.phys.sync();
        }
    }

    public void rollback() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("rollback not supported with journal disabled");
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public boolean canRollback() {
        return $assertionsDisabled;
    }

    public void clearCache() {
    }

    public void compact() {
        if (this.readOnly) {
            throw new IllegalAccessError();
        }
        int rafMode;
        File indexFile = this.index.getFile();
        File physFile = this.phys.getFile();
        if (this.index instanceof FileChannelVol) {
            rafMode = 2;
        } else if ((this.index instanceof MappedFileVol) && (this.phys instanceof FileChannelVol)) {
            rafMode = 1;
        } else {
            rafMode = 0;
        }
        lockAllWrite();
        File f1del = null;
        try {
            Object obj;
            StringBuilder stringBuilder = new StringBuilder();
            if (indexFile != null) {
                obj = indexFile;
            } else {
                f1del = File.createTempFile("mapdb", "compact");
                File file = f1del;
            }
            File compactedFile = new File(stringBuilder.append(obj).append(".compact").toString());
            boolean asyncWriteEnabled = ((this.index instanceof ByteBufferVol) && ((ByteBufferVol) this.index).asyncWriteEnabled) ? true : $assertionsDisabled;
            StoreDirect store2 = new StoreDirect(Volume.fileFactory(compactedFile, rafMode, $assertionsDisabled, this.sizeLimit, 20, 0, new File(compactedFile.getPath() + DATA_FILE_EXT), new File(compactedFile.getPath() + StoreWAL.TRANS_LOG_FILE_EXT), asyncWriteEnabled), $assertionsDisabled, $assertionsDisabled, 5, $assertionsDisabled, 0, this.checksum, this.compress, this.password, $assertionsDisabled, 0);
            compactPreUnderLock();
            this.index.putLong(16, this.physSize);
            this.index.putLong(MASK_LINKED, this.indexSize);
            this.index.putLong(24, this.freeSize);
            store2.lockAllWrite();
            long recid = longStackTake(120, $assertionsDisabled);
            while (recid != 0) {
                store2.longStackPut(120, recid, $assertionsDisabled);
                recid = longStackTake(120, $assertionsDisabled);
            }
            store2.index.putLong(MASK_LINKED, this.indexSize);
            for (long ioRecid = 32896; ioRecid < this.indexSize; ioRecid += MASK_LINKED) {
                byte[] bb = (byte[]) get2(ioRecid, Serializer.BYTE_ARRAY_NOSIZE);
                store2.index.ensureAvailable(MASK_LINKED + ioRecid);
                if (bb == null || bb.length == 0) {
                    store2.index.putLong(ioRecid, 0);
                } else {
                    DataOutput2 out = serialize(bb, Serializer.BYTE_ARRAY_NOSIZE);
                    store2.put2(out, ioRecid, store2.physAllocate(out.pos, true, $assertionsDisabled));
                }
            }
            File indexFile2 = store2.index.getFile();
            File physFile2 = store2.phys.getFile();
            store2.unlockAllWrite();
            boolean useDirectBuffer = ((this.index instanceof MemoryVol) && ((MemoryVol) this.index).useDirectBuffer) ? true : $assertionsDisabled;
            this.index.sync();
            this.index.close();
            this.index = null;
            this.phys.sync();
            this.phys.close();
            this.phys = null;
            if (indexFile != null) {
                File file2;
                File indexFile_;
                File physFile_;
                long time = System.currentTimeMillis();
                if (indexFile != null) {
                    file2 = new File(indexFile.getPath() + EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR + time + "_orig");
                } else {
                    indexFile_ = null;
                }
                if (physFile != null) {
                    file2 = new File(physFile.getPath() + EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR + time + "_orig");
                } else {
                    physFile_ = null;
                }
                store2.close();
                if (!indexFile.renameTo(indexFile_)) {
                    throw new AssertionError("could not rename file");
                } else if (!physFile.renameTo(physFile_)) {
                    throw new AssertionError("could not rename file");
                } else if (!indexFile2.renameTo(indexFile)) {
                    throw new AssertionError("could not rename file");
                } else if (physFile2.renameTo(physFile)) {
                    Factory fac2 = Volume.fileFactory(indexFile, rafMode, $assertionsDisabled, this.sizeLimit, 20, 0, new File(indexFile.getPath() + DATA_FILE_EXT), new File(indexFile.getPath() + StoreWAL.TRANS_LOG_FILE_EXT), asyncWriteEnabled);
                    this.index = fac2.createIndexVolume();
                    this.phys = fac2.createPhysVolume();
                    indexFile_.delete();
                    physFile_.delete();
                } else {
                    throw new AssertionError("could not rename file");
                }
            }
            Volume memoryVol = new MemoryVol(useDirectBuffer, this.sizeLimit, 20);
            Volume.volumeTransfer(this.indexSize, store2.index, memoryVol);
            memoryVol = new MemoryVol(useDirectBuffer, this.sizeLimit, 20);
            Volume.volumeTransfer(store2.physSize, store2.phys, memoryVol);
            File f1 = store2.index.getFile();
            File f2 = store2.phys.getFile();
            store2.close();
            f1.delete();
            f2.delete();
            this.index = memoryVol;
            this.phys = memoryVol;
            if (f1del != null) {
                f1del.delete();
            }
            this.physSize = store2.physSize;
            this.freeSize = store2.freeSize;
            this.index.putLong(16, this.physSize);
            this.index.putLong(MASK_LINKED, this.indexSize);
            this.index.putLong(24, this.freeSize);
            this.index.putLong(32, indexHeaderChecksum());
            this.maxUsedIoList = 32888;
            while (this.index.getLong(this.maxUsedIoList) != 0 && this.maxUsedIoList > 120) {
                this.maxUsedIoList -= MASK_LINKED;
            }
            compactPostUnderLock();
            unlockAllWrite();
        } catch (Throwable e) {
            throw new IOError(e);
        } catch (Throwable th) {
            unlockAllWrite();
        }
    }

    protected void compactPreUnderLock() {
    }

    protected void compactPostUnderLock() {
    }

    protected long longStackTake(long ioList, boolean recursive) {
        if (!$assertionsDisabled) {
            if (!this.structuralLock.isHeldByCurrentThread()) {
                throw new AssertionError();
            }
        }
        if ($assertionsDisabled || (ioList >= 120 && ioList < 32896)) {
            long dataOffset = this.index.getLong(ioList);
            if (dataOffset == 0) {
                return 0;
            }
            long pos = dataOffset >>> 48;
            dataOffset &= MASK_OFFSET;
            if (pos < MASK_LINKED) {
                throw new AssertionError();
            }
            long ret = this.phys.getSixLong(dataOffset + pos);
            if (pos == MASK_LINKED) {
                long next = this.phys.getLong(dataOffset);
                long size = next >>> 48;
                next &= MASK_OFFSET;
                if (next == 0) {
                    this.index.putLong(ioList, 0);
                    if (this.maxUsedIoList == ioList) {
                        while (true) {
                            if (this.index.getLong(this.maxUsedIoList) != 0) {
                                break;
                            }
                            if (this.maxUsedIoList <= 120) {
                                break;
                            }
                            this.maxUsedIoList -= MASK_LINKED;
                        }
                    }
                } else {
                    long nextSize = (long) this.phys.getUnsignedShort(next);
                    if ($assertionsDisabled || (nextSize - MASK_LINKED) % 6 == 0) {
                        this.index.putLong(ioList, ((nextSize - 6) << 48) | next);
                    } else {
                        throw new AssertionError();
                    }
                }
                freePhysPut((size << 48) | dataOffset, true);
                return ret;
            }
            pos -= 6;
            this.index.putLong(ioList, (pos << 48) | dataOffset);
            return ret;
        }
        throw new AssertionError("wrong ioList: " + ioList);
    }

    protected void longStackPut(long ioList, long offset, boolean recursive) {
        if (!$assertionsDisabled) {
            if (!this.structuralLock.isHeldByCurrentThread()) {
                throw new AssertionError();
            }
        }
        if (!$assertionsDisabled && (offset >>> 48) != 0) {
            throw new AssertionError();
        } else if ($assertionsDisabled || (ioList >= 120 && ioList <= 32896)) {
            long dataOffset = this.index.getLong(ioList);
            long pos = dataOffset >>> 48;
            dataOffset &= MASK_OFFSET;
            long listPhysid;
            if (dataOffset == 0) {
                listPhysid = freePhysTake(1232, true, true) & MASK_OFFSET;
                if (listPhysid == 0) {
                    throw new AssertionError();
                }
                this.phys.putLong(listPhysid, 346777171307528192L);
                this.phys.putSixLong(MASK_LINKED + listPhysid, offset);
                this.index.putLong(ioList, 2251799813685248L | listPhysid);
                if (this.maxUsedIoList <= ioList) {
                    this.maxUsedIoList = ioList;
                    return;
                }
                return;
            }
            long next = this.phys.getLong(dataOffset);
            long size = next >>> 48;
            next &= MASK_OFFSET;
            if (!$assertionsDisabled && 6 + pos > size) {
                throw new AssertionError();
            } else if (6 + pos == size) {
                long newPageSize = LONG_STACK_PREF_SIZE;
                if (ioList == size2ListIoRecid(LONG_STACK_PREF_SIZE)) {
                    newPageSize = LONG_STACK_PREF_SIZE_ALTER;
                }
                listPhysid = freePhysTake((int) newPageSize, true, true) & MASK_OFFSET;
                if (listPhysid == 0) {
                    throw new AssertionError();
                }
                this.phys.putLong(listPhysid, (newPageSize << 48) | (MASK_OFFSET & dataOffset));
                this.phys.putSixLong(MASK_LINKED + listPhysid, offset);
                this.index.putLong(ioList, 2251799813685248L | listPhysid);
            } else {
                pos += 6;
                this.phys.putSixLong(dataOffset + pos, offset);
                this.index.putLong(ioList, (pos << 48) | dataOffset);
            }
        } else {
            throw new AssertionError("wrong ioList: " + ioList);
        }
    }

    protected void freeIoRecidPut(long ioRecid) {
        if (!$assertionsDisabled && ioRecid <= 32896) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && !this.locks[Store.lockPos(ioRecid)].writeLock().isHeldByCurrentThread()) {
            throw new AssertionError();
        } else if (this.spaceReclaimTrack) {
            longStackPut(120, ioRecid, $assertionsDisabled);
        }
    }

    protected long freeIoRecidTake(boolean ensureAvail) {
        if (this.spaceReclaimTrack) {
            long longStackTake = longStackTake(120, $assertionsDisabled);
            if (longStackTake != 0) {
                if ($assertionsDisabled || longStackTake > 32896) {
                    return longStackTake;
                }
                throw new AssertionError();
            }
        }
        this.indexSize += MASK_LINKED;
        if (ensureAvail) {
            this.index.ensureAvailable(this.indexSize);
        }
        if ($assertionsDisabled || this.indexSize - MASK_LINKED > 32896) {
            return this.indexSize - MASK_LINKED;
        }
        throw new AssertionError();
    }

    protected static long size2ListIoRecid(long size) {
        return 128 + (((size - 1) / 16) * MASK_LINKED);
    }

    protected void freePhysPut(long indexVal, boolean recursive) {
        if ($assertionsDisabled || this.structuralLock.isHeldByCurrentThread()) {
            long size = indexVal >>> 48;
            if ($assertionsDisabled || size != 0) {
                indexVal &= MASK_OFFSET;
                if (this.physSize == roundTo16(size) + indexVal) {
                    this.physSize = indexVal;
                    return;
                }
                this.freeSize += roundTo16(size);
                longStackPut(size2ListIoRecid(size), indexVal, recursive);
                return;
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    protected long freePhysTake(int size, boolean ensureAvail, boolean recursive) {
        if (!$assertionsDisabled) {
            if (!this.structuralLock.isHeldByCurrentThread()) {
                throw new AssertionError();
            }
        }
        if ($assertionsDisabled || size > 0) {
            long ret;
            if (this.spaceReclaimReuse) {
                ret = longStackTake(size2ListIoRecid((long) size), recursive);
                if (ret != 0) {
                    this.freeSize -= roundTo16((long) size);
                    return ret;
                }
            }
            if (!recursive && this.spaceReclaimSplit) {
                for (long s = roundTo16((long) size) + 16; s < 65535; s += 16) {
                    long ioList = size2ListIoRecid(s);
                    if (ioList > this.maxUsedIoList) {
                        break;
                    }
                    ret = longStackTake(ioList, recursive);
                    if (ret != 0) {
                        long offset = ret & MASK_OFFSET;
                        long remaining = s - roundTo16((long) size);
                        freePhysPut((remaining << 48) | ((offset + s) - remaining), recursive);
                        this.freeSize -= roundTo16(s);
                        return (((long) size) << 48) | offset;
                    }
                }
            }
            if ((this.physSize & 1048575) + ((long) size) > 1048576) {
                this.physSize += 1048576 - (this.physSize & 1048575);
            }
            long physSize2 = this.physSize;
            this.physSize = roundTo16(this.physSize + ((long) size));
            if (ensureAvail) {
                this.phys.ensureAvailable(this.physSize);
            }
            return physSize2;
        }
        throw new AssertionError();
    }

    public long getMaxRecid() {
        return (this.indexSize - 32896) / MASK_LINKED;
    }

    public ByteBuffer getRaw(long recid) {
        byte[] bb = (byte[]) get(recid, Serializer.BYTE_ARRAY_NOSIZE);
        if (bb == null) {
            return null;
        }
        return ByteBuffer.wrap(bb);
    }

    public Iterator<Long> getFreeRecids() {
        return Fun.EMPTY_ITERATOR;
    }

    public void updateRaw(long recid, ByteBuffer data) {
        long ioRecid = (recid * MASK_LINKED) + 32896;
        if (ioRecid >= this.indexSize) {
            this.indexSize = ioRecid + MASK_LINKED;
            this.index.ensureAvailable(this.indexSize);
        }
        byte[] b = null;
        if (data != null) {
            data = data.duplicate();
            b = new byte[data.remaining()];
            data.get(b);
        }
        update(recid, b, Serializer.BYTE_ARRAY_NOSIZE);
    }

    public long getSizeLimit() {
        return this.sizeLimit;
    }

    public long getCurrSize() {
        return this.physSize;
    }

    public long getFreeSize() {
        return this.freeSize;
    }

    public String calculateStatistics() {
        String s = ((((((Table.STRING_DEFAULT_VALUE + getClass().getName() + "\n") + "volume: \n") + "  " + this.phys + "\n") + "indexSize=" + this.indexSize + "\n") + "physSize=" + this.physSize + "\n") + "freeSize=" + this.freeSize + "\n") + "num of freeRecids: " + countLongStackItems(120) + "\n";
        for (int size = IO_PHYS_SIZE; size < 65545; size *= 2) {
            long sum = 0;
            for (int ss = size / 2; ss < size; ss += IO_PHYS_SIZE) {
                sum += countLongStackItems(size2ListIoRecid((long) ss)) * ((long) ss);
            }
            s = s + "Size occupied by free records (size=" + size + ") = " + sum;
        }
        return s;
    }

    protected long countLongStackItems(long ioList) {
        long ret = 0;
        long v = this.index.getLong(ioList);
        while (true) {
            long next = v & MASK_OFFSET;
            if (next == 0) {
                return ret;
            }
            ret += v >>> 48;
            v = this.phys.getLong(next);
        }
    }
}
