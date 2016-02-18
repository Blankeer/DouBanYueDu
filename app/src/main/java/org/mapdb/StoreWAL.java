package org.mapdb;

import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.zip.CRC32;

import org.mapdb.LongMap.LongMapIterator;
import org.mapdb.Volume.Factory;

public class StoreWAL extends StoreDirect {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected static final long LOG_MASK_OFFSET = 281474976710655L;
    protected static final long LOG_SEAL = 4566556446554645L;
    protected static final long[] PREALLOC;
    protected static final long[] TOMBSTONE;
    public static final String TRANS_LOG_FILE_EXT = ".t";
    protected static final byte WAL_INDEX_LONG = (byte) 101;
    protected static final byte WAL_LONGSTACK_PAGE = (byte) 102;
    protected static final byte WAL_PHYS_ARRAY = (byte) 104;
    protected static final byte WAL_PHYS_ARRAY_ONE_LONG = (byte) 103;
    protected static final byte WAL_SEAL = (byte) 111;
    protected static final byte WAL_SKIP_REST_OF_BLOCK = (byte) 105;
    protected final long[] indexVals;
    protected final boolean[] indexValsModified;
    protected Volume log;
    protected final AtomicInteger logChecksum;
    protected volatile long logSize;
    protected final LongMap<byte[]> longStackPages;
    protected final LongConcurrentHashMap<long[]> modified;
    protected boolean replayPending;
    protected final Factory volFac;

    static {
        boolean z;
        if (StoreWAL.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        TOMBSTONE = new long[0];
        PREALLOC = new long[0];
    }

    public StoreWAL(Factory volFac) {
        this(volFac, $assertionsDisabled, $assertionsDisabled, 5, $assertionsDisabled, 0, $assertionsDisabled, $assertionsDisabled, null, $assertionsDisabled, 0);
    }

    public StoreWAL(Factory volFac, boolean readOnly, boolean deleteFilesAfterClose, int spaceReclaimMode, boolean syncOnCommitDisabled, long sizeLimit, boolean checksum, boolean compress, byte[] password, boolean disableLocks, int sizeIncrement) {
        super(volFac, readOnly, deleteFilesAfterClose, spaceReclaimMode, syncOnCommitDisabled, sizeLimit, checksum, compress, password, disableLocks, sizeIncrement);
        this.modified = new LongConcurrentHashMap();
        this.longStackPages = new LongHashMap();
        this.indexVals = new long[4112];
        this.indexValsModified = new boolean[this.indexVals.length];
        this.replayPending = true;
        this.logChecksum = new AtomicInteger();
        this.volFac = volFac;
        this.log = volFac.createTransLogVolume();
        boolean allGood = $assertionsDisabled;
        this.structuralLock.lock();
        try {
            reloadIndexFile();
            if (verifyLogFile()) {
                replayLogFile();
            }
            this.replayPending = $assertionsDisabled;
            checkHeaders();
            if (!readOnly) {
                logReset();
            }
            allGood = true;
        } finally {
            if (!allGood) {
                if (this.log != null) {
                    this.log.close();
                    this.log = null;
                }
                if (this.index != null) {
                    this.index.close();
                    this.index = null;
                }
                if (this.phys != null) {
                    this.phys.close();
                    this.phys = null;
                }
            }
            this.structuralLock.unlock();
        }
    }

    protected void checkHeaders() {
        if (!this.replayPending) {
            super.checkHeaders();
        }
    }

    protected void reloadIndexFile() {
        if ($assertionsDisabled || this.structuralLock.isHeldByCurrentThread()) {
            this.logSize = 16;
            this.modified.clear();
            this.longStackPages.clear();
            this.indexSize = this.index.getLong(8);
            this.physSize = this.index.getLong(16);
            this.freeSize = this.index.getLong(24);
            for (int i = 0; i < 32896; i += 8) {
                this.indexVals[i / 8] = this.index.getLong((long) i);
            }
            Arrays.fill(this.indexValsModified, $assertionsDisabled);
            this.logChecksum.set(0);
            this.maxUsedIoList = 32888;
            while (this.indexVals[(int) (this.maxUsedIoList / 8)] != 0 && this.maxUsedIoList > 120) {
                this.maxUsedIoList -= 8;
            }
            return;
        }
        throw new AssertionError();
    }

    protected void logReset() {
        if ($assertionsDisabled || this.structuralLock.isHeldByCurrentThread()) {
            this.log.truncate(16);
            this.log.ensureAvailable(16);
            this.log.putInt(0, 234243482);
            this.log.putUnsignedShort(4, AbstractSpiCall.DEFAULT_TIMEOUT);
            this.log.putUnsignedShort(6, expectedMasks());
            this.log.putLong(8, 0);
            this.logSize = 16;
            return;
        }
        throw new AssertionError();
    }

    public long preallocate() {
        this.newRecidLock.readLock().lock();
        Lock lock;
        try {
            this.structuralLock.lock();
            checkLogRounding();
            long ioRecid = freeIoRecidTake($assertionsDisabled);
            long logPos = this.logSize;
            this.logSize += 17;
            this.log.ensureAvailable(this.logSize);
            this.structuralLock.unlock();
            lock = this.locks[Store.lockPos(ioRecid)].writeLock();
            lock.lock();
            walIndexVal(logPos, ioRecid, 4);
            this.modified.put(ioRecid, PREALLOC);
            lock.unlock();
            this.newRecidLock.readLock().unlock();
            long recid = (ioRecid - 32896) / 8;
            if ($assertionsDisabled || recid > 0) {
                return recid;
            }
            throw new AssertionError();
        } catch (Throwable th) {
            this.newRecidLock.readLock().unlock();
        }
    }

    public void preallocate(long[] recids) {
        for (int i = 0; i < recids.length; i++) {
            recids[i] = preallocate();
        }
    }

    public <A> long put(A value, Serializer<A> serializer) {
        if ($assertionsDisabled || value != null) {
            DataOutput2 out = serialize(value, serializer);
            this.newRecidLock.readLock().lock();
            Lock lock;
            try {
                this.structuralLock.lock();
                long ioRecid = freeIoRecidTake($assertionsDisabled);
                long[] physPos = physAllocate(out.pos, $assertionsDisabled, $assertionsDisabled);
                long[] logPos = logAllocate(physPos);
                this.structuralLock.unlock();
                lock = this.locks[Store.lockPos(ioRecid)].writeLock();
                lock.lock();
                walIndexVal((((((logPos[0] & LOG_MASK_OFFSET) - 1) - 8) - 8) - 1) - 8, ioRecid, physPos[0] | 2);
                walPhysArray(out, physPos, logPos);
                this.modified.put(ioRecid, logPos);
                this.recycledDataOuts.offer(out);
                lock.unlock();
                this.newRecidLock.readLock().unlock();
                long recid = (ioRecid - 32896) / 8;
                if ($assertionsDisabled || recid > 0) {
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

    protected void walPhysArray(DataOutput2 out, long[] physPos, long[] logPos) {
        int outPos = 0;
        int logC = 0;
        CRC32 crc32 = new CRC32();
        int i = 0;
        while (i < logPos.length) {
            int c = i == logPos.length + -1 ? 0 : 8;
            long pos = logPos[i] & LOG_MASK_OFFSET;
            int size = (int) (logPos[i] >>> 48);
            byte header = c == 0 ? WAL_PHYS_ARRAY : WAL_PHYS_ARRAY_ONE_LONG;
            this.log.putByte((pos - 8) - 1, header);
            this.log.putLong(pos - 8, physPos[i]);
            if (c > 0) {
                this.log.putLong(pos, physPos[i + 1]);
            }
            this.log.putData(((long) c) + pos, out.buf, outPos, size - c);
            crc32.reset();
            crc32.update(out.buf, outPos, size - c);
            logC |= LongHashMap.longHash(((c > 0 ? physPos[i + 1] : 0) | (physPos[i] | (((long) header) | pos))) | crc32.getValue());
            outPos += size - c;
            if (!$assertionsDisabled) {
                if (this.logSize < ((long) outPos)) {
                    throw new AssertionError();
                }
            }
            i++;
        }
        logChecksumAdd(logC);
        if (!$assertionsDisabled && outPos != out.pos) {
            throw new AssertionError();
        }
    }

    protected void walIndexVal(long logPos, long ioRecid, long indexVal) {
        if (!$assertionsDisabled && !this.locks[Store.lockPos(ioRecid)].writeLock().isHeldByCurrentThread()) {
            throw new AssertionError();
        } else if ($assertionsDisabled || this.logSize >= ((logPos + 1) + 8) + 8) {
            this.log.putByte(logPos, WAL_INDEX_LONG);
            this.log.putLong(logPos + 1, ioRecid);
            this.log.putLong(9 + logPos, indexVal);
            logChecksumAdd(LongHashMap.longHash(((101 | logPos) | ioRecid) | indexVal));
        } else {
            throw new AssertionError();
        }
    }

    protected long[] logAllocate(long[] physPos) {
        if ($assertionsDisabled || this.structuralLock.isHeldByCurrentThread()) {
            this.logSize += 17;
            long[] ret = new long[physPos.length];
            for (int i = 0; i < physPos.length; i++) {
                long size = physPos[i] >>> 48;
                this.logSize += 9;
                ret[i] = (size << 48) | this.logSize;
                this.logSize += size;
                checkLogRounding();
            }
            this.log.ensureAvailable(this.logSize);
            return ret;
        }
        throw new AssertionError();
    }

    protected void checkLogRounding() {
        if (!$assertionsDisabled && !this.structuralLock.isHeldByCurrentThread()) {
            throw new AssertionError();
        } else if ((this.logSize & 1048575) + 131070 > 1048576) {
            this.log.ensureAvailable(this.logSize + 1);
            this.log.putByte(this.logSize, WAL_SKIP_REST_OF_BLOCK);
            this.logSize += 1048576 - (this.logSize & 1048575);
        }
    }

    public <A> A get(long recid, Serializer<A> serializer) {
        if ($assertionsDisabled || recid > 0) {
            long ioRecid = 32896 + (8 * recid);
            Lock lock = this.locks[Store.lockPos(ioRecid)].readLock();
            lock.lock();
            try {
                A a2 = get2(ioRecid, serializer);
                lock.unlock();
                return a2;
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
            long[] r = (long[]) this.modified.get(ioRecid);
            if (r == null) {
                return super.get2(ioRecid, serializer);
            }
            if (r == TOMBSTONE || r == PREALLOC || r.length == 0) {
                return null;
            }
            int size;
            if (r.length == 1) {
                size = (int) (r[0] >>> 48);
                return deserialize(serializer, size, (DataInput2) this.log.getDataInput(r[0] & LOG_MASK_OFFSET, size));
            }
            int totalSize = 0;
            int i = 0;
            while (i < r.length) {
                totalSize += ((int) (r[i] >>> 48)) - (i == r.length + -1 ? 0 : 8);
                i++;
            }
            byte[] b = new byte[totalSize];
            int pos = 0;
            i = 0;
            while (i < r.length) {
                int c = i == r.length + -1 ? 0 : 8;
                size = ((int) (r[i] >>> 48)) - c;
                this.log.getDataInput((r[i] & LOG_MASK_OFFSET) + ((long) c), size).readFully(b, pos, size);
                pos += size;
                i++;
            }
            if (pos != totalSize) {
                throw new AssertionError();
            }
            return deserialize(serializer, totalSize, new DataInput2(b));
        }
        throw new AssertionError();
    }

    public <A> void update(long recid, A value, Serializer<A> serializer) {
        if (!$assertionsDisabled && recid <= 0) {
            throw new AssertionError();
        } else if ($assertionsDisabled || value != null) {
            DataOutput2 out = serialize(value, serializer);
            long ioRecid = 32896 + (8 * recid);
            Lock lock = this.locks[Store.lockPos(ioRecid)].writeLock();
            lock.lock();
            long indexVal = 0;
            try {
                long[] linkedRecords = getLinkedRecordsFromLog(ioRecid);
                if (linkedRecords == null) {
                    indexVal = this.index.getLong(ioRecid);
                    linkedRecords = getLinkedRecordsIndexVals(indexVal);
                } else if (linkedRecords == PREALLOC) {
                    linkedRecords = null;
                }
                this.structuralLock.lock();
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
                long[] physPos = physAllocate(out.pos, $assertionsDisabled, $assertionsDisabled);
                Object logPos = logAllocate(physPos);
                this.structuralLock.unlock();
                walIndexVal((((((logPos[0] & LOG_MASK_OFFSET) - 1) - 8) - 8) - 1) - 8, ioRecid, physPos[0] | 2);
                walPhysArray(out, physPos, logPos);
                this.modified.put(ioRecid, logPos);
                lock.unlock();
                this.recycledDataOuts.offer(out);
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
        if (!$assertionsDisabled && recid <= 0) {
            throw new AssertionError();
        } else if ($assertionsDisabled || !(expectedOldValue == null || newValue == null)) {
            long ioRecid = 32896 + (8 * recid);
            Lock lock = this.locks[Store.lockPos(ioRecid)].writeLock();
            lock.lock();
            try {
                A oldVal = get2(ioRecid, serializer);
                if ((oldVal != null || expectedOldValue == null) && (oldVal == null || oldVal.equals(expectedOldValue))) {
                    DataOutput2 out = serialize(newValue, serializer);
                    long indexVal = 0;
                    long[] linkedRecords = getLinkedRecordsFromLog(ioRecid);
                    if (linkedRecords == null) {
                        indexVal = this.index.getLong(ioRecid);
                        linkedRecords = getLinkedRecordsIndexVals(indexVal);
                    }
                    this.structuralLock.lock();
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
                    long[] physPos = physAllocate(out.pos, $assertionsDisabled, $assertionsDisabled);
                    Object logPos = logAllocate(physPos);
                    this.structuralLock.unlock();
                    walIndexVal((((((logPos[0] & LOG_MASK_OFFSET) - 1) - 8) - 8) - 1) - 8, ioRecid, physPos[0] | 2);
                    walPhysArray(out, physPos, logPos);
                    this.modified.put(ioRecid, logPos);
                    lock.unlock();
                    this.recycledDataOuts.offer(out);
                    return true;
                }
                lock.unlock();
                return $assertionsDisabled;
            } catch (IOException e) {
                try {
                    throw new IOError(e);
                } catch (Throwable th) {
                    lock.unlock();
                }
            } catch (Throwable th2) {
                this.structuralLock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public <A> void delete(long recid, Serializer<A> serializer) {
        if ($assertionsDisabled || recid > 0) {
            long ioRecid = 32896 + (8 * recid);
            Lock lock = this.locks[Store.lockPos(ioRecid)].writeLock();
            lock.lock();
            long indexVal = 0;
            try {
                long[] linkedRecords = getLinkedRecordsFromLog(ioRecid);
                if (linkedRecords == null) {
                    indexVal = this.index.getLong(ioRecid);
                    if (indexVal == 4) {
                        lock.unlock();
                        return;
                    }
                    linkedRecords = getLinkedRecordsIndexVals(indexVal);
                }
                this.structuralLock.lock();
                checkLogRounding();
                long logPos = this.logSize;
                this.logSize += 17;
                this.log.ensureAvailable(this.logSize);
                longStackPut(120, ioRecid, $assertionsDisabled);
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
                walIndexVal(logPos, ioRecid, 2);
                this.modified.put(ioRecid, TOMBSTONE);
                lock.unlock();
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public void commit() {
        lockAllWrite();
        try {
            if (this.serializerPojo != null && this.serializerPojo.hasUnsavedChanges()) {
                this.serializerPojo.save(this);
            }
            if (logDirty()) {
                int crc = 0;
                LongMapIterator<byte[]> iter = this.longStackPages.longMapIterator();
                while (iter.moveToNext()) {
                    if ($assertionsDisabled || (iter.key() >>> 48) == 0) {
                        byte[] array = (byte[]) iter.value();
                        long pageSize = (long) (((array[0] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 8) | (array[1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT));
                        if ($assertionsDisabled || ((long) array.length) == pageSize) {
                            long firstVal = (pageSize << 48) | iter.key();
                            this.log.ensureAvailable(((this.logSize + 1) + 8) + pageSize);
                            crc |= LongHashMap.longHash((this.logSize | 102) | firstVal);
                            this.log.putByte(this.logSize, WAL_LONGSTACK_PAGE);
                            this.logSize++;
                            this.log.putLong(this.logSize, firstVal);
                            this.logSize += 8;
                            CRC32 crc32 = new CRC32();
                            crc32.update(array);
                            crc = (int) (((long) crc) | crc32.getValue());
                            this.log.putData(this.logSize, array, 0, array.length);
                            this.logSize += (long) array.length;
                            checkLogRounding();
                        } else {
                            throw new AssertionError();
                        }
                    }
                    throw new AssertionError();
                }
                for (int i = Header.ARRAY_LONG_BYTE; i < 32896; i += 8) {
                    if (this.indexValsModified[i / 8]) {
                        this.log.ensureAvailable(this.logSize + 17);
                        this.logSize += 17;
                        walIndexVal(this.logSize - 17, (long) i, this.indexVals[i / 8]);
                    }
                }
                this.log.ensureAvailable((((this.logSize + 1) + 18) + 8) + 4);
                long indexChecksum = indexHeaderChecksumUncommited();
                crc |= LongHashMap.longHash(((((this.logSize | 111) | this.indexSize) | this.physSize) | this.freeSize) | indexChecksum);
                this.log.putByte(this.logSize, WAL_SEAL);
                this.logSize++;
                this.log.putSixLong(this.logSize, this.indexSize);
                this.logSize += 6;
                this.log.putSixLong(this.logSize, this.physSize);
                this.logSize += 6;
                this.log.putSixLong(this.logSize, this.freeSize);
                this.logSize += 6;
                this.log.putLong(this.logSize, indexChecksum);
                this.logSize += 8;
                this.log.putInt(this.logSize, this.logChecksum.get() | crc);
                this.logSize += 4;
                this.log.putLong(8, LOG_SEAL);
                if (!this.syncOnCommitDisabled) {
                    this.log.sync();
                }
                replayLogFile();
                reloadIndexFile();
                unlockAllWrite();
            }
        } finally {
            unlockAllWrite();
        }
    }

    protected boolean logDirty() {
        if (this.logSize != 16 || !this.longStackPages.isEmpty() || !this.modified.isEmpty()) {
            return true;
        }
        for (boolean b : this.indexValsModified) {
            if (b) {
                return true;
            }
        }
        return $assertionsDisabled;
    }

    protected long indexHeaderChecksumUncommited() {
        long ret = 0;
        for (int offset = 0; offset < 32896; offset += 8) {
            if (offset != 32) {
                long indexVal;
                if (offset == 8) {
                    indexVal = this.indexSize;
                } else if (offset == 16) {
                    indexVal = this.physSize;
                } else if (offset == 24) {
                    indexVal = this.freeSize;
                } else {
                    indexVal = this.indexVals[offset / 8];
                }
                ret |= ((long) LongHashMap.longHash(((long) offset) | indexVal)) | indexVal;
            }
        }
        return ret;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected boolean verifyLogFile() {
        /*
        r40 = this;
        r31 = $assertionsDisabled;
        if (r31 != 0) goto L_0x0016;
    L_0x0004:
        r0 = r40;
        r0 = r0.structuralLock;
        r31 = r0;
        r31 = r31.isHeldByCurrentThread();
        if (r31 != 0) goto L_0x0016;
    L_0x0010:
        r31 = new java.lang.AssertionError;
        r31.<init>();
        throw r31;
    L_0x0016:
        r0 = r40;
        r0 = r0.readOnly;
        r31 = r0;
        if (r31 == 0) goto L_0x0029;
    L_0x001e:
        r0 = r40;
        r0 = r0.log;
        r31 = r0;
        if (r31 != 0) goto L_0x0029;
    L_0x0026:
        r31 = 0;
    L_0x0028:
        return r31;
    L_0x0029:
        r32 = 0;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;
        r0 = r40;
        r0 = r0.log;
        r31 = r0;
        r31 = r31.isEmpty();
        if (r31 != 0) goto L_0x0087;
    L_0x003d:
        r0 = r40;
        r0 = r0.log;
        r31 = r0;
        r31 = r31.getFile();
        if (r31 == 0) goto L_0x005d;
    L_0x0049:
        r0 = r40;
        r0 = r0.log;
        r31 = r0;
        r31 = r31.getFile();
        r32 = r31.length();
        r34 = 16;
        r31 = (r32 > r34 ? 1 : (r32 == r34 ? 0 : -1));
        if (r31 < 0) goto L_0x0087;
    L_0x005d:
        r0 = r40;
        r0 = r0.log;
        r31 = r0;
        r32 = 0;
        r31 = r31.getInt(r32);
        r32 = 234243482; // 0xdf6459a float:1.51776765E-30 double:1.15731657E-315;
        r0 = r31;
        r1 = r32;
        if (r0 != r1) goto L_0x0087;
    L_0x0072:
        r0 = r40;
        r0 = r0.log;
        r31 = r0;
        r32 = 8;
        r32 = r31.getLong(r32);
        r34 = 4566556446554645; // 0x10394246d7fa15 float:27645.041 double:2.2561786600375285E-308;
        r31 = (r32 > r34 ? 1 : (r32 == r34 ? 0 : -1));
        if (r31 == 0) goto L_0x008a;
    L_0x0087:
        r31 = 0;
        goto L_0x0028;
    L_0x008a:
        r0 = r40;
        r0 = r0.log;
        r31 = r0;
        r32 = 4;
        r31 = r31.getUnsignedShort(r32);
        r32 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r0 = r31;
        r1 = r32;
        if (r0 <= r1) goto L_0x00ab;
    L_0x009e:
        r31 = new java.io.IOError;
        r32 = new java.io.IOException;
        r33 = "New store format version, please use newer MapDB version";
        r32.<init>(r33);
        r31.<init>(r32);
        throw r31;
    L_0x00ab:
        r0 = r40;
        r0 = r0.log;
        r31 = r0;
        r32 = 6;
        r31 = r31.getUnsignedShort(r32);
        r32 = r40.expectedMasks();
        r0 = r31;
        r1 = r32;
        if (r0 == r1) goto L_0x00c9;
    L_0x00c1:
        r31 = new java.lang.IllegalArgumentException;
        r32 = "Log file created with different features. Please check compression, checksum or encryption";
        r31.<init>(r32);
        throw r31;
    L_0x00c9:
        r6 = new java.util.zip.CRC32;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r6.<init>();	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = 16;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r16 = r31.getByte(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 1;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r5 = 0;
    L_0x00f7:
        r31 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        r0 = r16;
        r1 = r31;
        if (r0 == r1) goto L_0x038d;
    L_0x00ff:
        r31 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        r0 = r16;
        r1 = r31;
        if (r0 != r1) goto L_0x018e;
    L_0x0107:
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r18 = r31.getLong(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 8;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r14 = r31.getLong(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 8;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 1;
        r32 = r32 - r34;
        r34 = 8;
        r32 = r32 - r34;
        r34 = 8;
        r32 = r32 - r34;
        r34 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        r32 = r32 | r34;
        r32 = r32 | r18;
        r32 = r32 | r14;
        r31 = org.mapdb.LongHashMap.longHash(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r5 = r5 | r31;
    L_0x0167:
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r16 = r31.getByte(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 1;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        goto L_0x00f7;
    L_0x0189:
        r7 = move-exception;
        r31 = 0;
        goto L_0x0028;
    L_0x018e:
        r31 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        r0 = r16;
        r1 = r31;
        if (r0 != r1) goto L_0x0216;
    L_0x0196:
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r24 = r31.getLong(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 8;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = 48;
        r32 = r24 >>> r31;
        r0 = r32;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r30 = r0;
        r0 = r30;
        r4 = new byte[r0];	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r0 = r31;
        r1 = r32;
        r3 = r30;
        r31 = r0.getDataInput(r1, r3);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r31;
        r0.readFully(r4);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r6.reset();	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r6.update(r4);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        r32 = r32 | r34;
        r32 = r32 | r24;
        r34 = r6.getValue();	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r32 | r34;
        r31 = org.mapdb.LongHashMap.longHash(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r5 = r5 | r31;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r0 = r30;
        r0 = (long) r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r34 = r0;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        goto L_0x0167;
    L_0x0211:
        r7 = move-exception;
        r31 = 0;
        goto L_0x0028;
    L_0x0216:
        r31 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        r0 = r16;
        r1 = r31;
        if (r0 != r1) goto L_0x02bd;
    L_0x021e:
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r24 = r31.getLong(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 8;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = 48;
        r32 = r24 >>> r31;
        r0 = r32;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r30 = r31 + -8;
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r20 = r31.getLong(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 8;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r30;
        r4 = new byte[r0];	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r0 = r31;
        r1 = r32;
        r3 = r30;
        r31 = r0.getDataInput(r1, r3);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r31;
        r0.readFully(r4);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r6.reset();	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r6.update(r4);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        r32 = r32 | r34;
        r32 = r32 | r24;
        r32 = r32 | r20;
        r34 = r6.getValue();	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r32 | r34;
        r31 = org.mapdb.LongHashMap.longHash(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r5 = r5 | r31;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r0 = r30;
        r0 = (long) r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r34 = r0;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        goto L_0x0167;
    L_0x02bd:
        r31 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        r0 = r16;
        r1 = r31;
        if (r0 != r1) goto L_0x0361;
    L_0x02c5:
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r22 = r31.getLong(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 8;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r26 = r0;
        r31 = 48;
        r32 = r22 >>> r31;
        r0 = r32;
        r0 = (int) r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r30 = r0;
        r32 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        r32 = r32 | r26;
        r32 = r32 | r22;
        r31 = org.mapdb.LongHashMap.longHash(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r5 = r5 | r31;
        r0 = r30;
        r4 = new byte[r0];	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r0 = r31;
        r1 = r32;
        r3 = r30;
        r31 = r0.getDataInput(r1, r3);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r31;
        r0.readFully(r4);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r6.reset();	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r6.update(r4);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = (long) r5;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = r6.getValue();	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r32 | r34;
        r0 = r32;
        r5 = (int) r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r0 = r31;
        r1 = r32;
        r3 = r30;
        r31 = r0.getDataInput(r1, r3);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r31;
        r0.readFully(r4);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r0 = r30;
        r0 = (long) r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r34 = r0;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        goto L_0x0167;
    L_0x0361:
        r31 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        r0 = r16;
        r1 = r31;
        if (r0 != r1) goto L_0x0389;
    L_0x0369:
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r36 = r0;
        r38 = 1048575; // 0xfffff float:1.469367E-39 double:5.18065E-318;
        r36 = r36 & r38;
        r34 = r34 - r36;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        goto L_0x0167;
    L_0x0389:
        r31 = 0;
        goto L_0x0028;
    L_0x038d:
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r10 = r31.getSixLong(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 6;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r28 = r31.getSixLong(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 6;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r8 = r31.getSixLong(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 6;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r12 = r31.getLong(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 8;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 1;
        r32 = r32 - r34;
        r34 = 18;
        r32 = r32 - r34;
        r34 = 8;
        r32 = r32 - r34;
        r32 = r32 | r10;
        r32 = r32 | r28;
        r32 = r32 | r8;
        r32 = r32 | r12;
        r31 = org.mapdb.LongHashMap.longHash(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r5 = r5 | r31;
        r0 = r40;
        r0 = r0.log;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r17 = r31.getInt(r32);	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r0 = r40;
        r0 = r0.logSize;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = r0;
        r34 = 4;
        r32 = r32 + r34;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r32 = 0;
        r0 = r32;
        r2 = r40;
        r2.logSize = r0;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = $assertionsDisabled;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        if (r31 != 0) goto L_0x046b;
    L_0x0459:
        r0 = r40;
        r0 = r0.structuralLock;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31 = r0;
        r31 = r31.isHeldByCurrentThread();	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        if (r31 != 0) goto L_0x046b;
    L_0x0465:
        r31 = new java.lang.AssertionError;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        r31.<init>();	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
        throw r31;	 Catch:{ IOException -> 0x0189, IOError -> 0x0211 }
    L_0x046b:
        r31 = 1;
        goto L_0x0028;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mapdb.StoreWAL.verifyLogFile():boolean");
    }

    protected void replayLogFile() {
        if (!$assertionsDisabled && !this.structuralLock.isHeldByCurrentThread()) {
            throw new AssertionError();
        } else if (!this.readOnly || this.log != null) {
            this.logSize = 0;
            if (!this.log.isEmpty() && this.log.getInt(0) == 234243482 && this.log.getUnsignedShort(4) <= AbstractSpiCall.DEFAULT_TIMEOUT && this.log.getLong(8) == LOG_SEAL && this.log.getUnsignedShort(6) == expectedMasks()) {
                this.logSize = 16;
                byte ins = this.log.getByte(this.logSize);
                this.logSize++;
                while (ins != 111) {
                    if (ins == 101) {
                        long ioRecid = this.log.getLong(this.logSize);
                        this.logSize += 8;
                        long indexVal = this.log.getLong(this.logSize);
                        this.logSize += 8;
                        this.index.ensureAvailable(8 + ioRecid);
                        this.index.putLong(ioRecid, indexVal);
                    } else if (ins == 104 || ins == 102 || ins == 103) {
                        long offset = this.log.getLong(this.logSize);
                        this.logSize += 8;
                        int size = (int) (offset >>> 48);
                        offset &= 281474976710640L;
                        DataInput2 input = (DataInput2) this.log.getDataInput(this.logSize, size);
                        ByteBuffer buf = input.buf.duplicate();
                        buf.position(input.pos);
                        buf.limit(input.pos + size);
                        this.phys.ensureAvailable(((long) size) + offset);
                        this.phys.putData(offset, buf);
                        this.logSize += (long) size;
                    } else if (ins == 105) {
                        this.logSize += 1048576 - (this.logSize & 1048575);
                    } else {
                        throw new AssertionError("unknown trans log instruction '" + ins + "' at log offset: " + (this.logSize - 1));
                    }
                    ins = this.log.getByte(this.logSize);
                    this.logSize++;
                }
                this.index.putLong(8, this.log.getSixLong(this.logSize));
                this.logSize += 6;
                this.index.putLong(16, this.log.getSixLong(this.logSize));
                this.logSize += 6;
                this.index.putLong(24, this.log.getSixLong(this.logSize));
                this.logSize += 6;
                this.index.putLong(32, this.log.getLong(this.logSize));
                this.logSize += 8;
                if (!this.syncOnCommitDisabled) {
                    this.phys.sync();
                    this.index.sync();
                }
                logReset();
                if (!$assertionsDisabled && !this.structuralLock.isHeldByCurrentThread()) {
                    throw new AssertionError();
                }
                return;
            }
            logReset();
        }
    }

    public void rollback() throws UnsupportedOperationException {
        lockAllWrite();
        try {
            logReset();
            reloadIndexFile();
        } finally {
            unlockAllWrite();
        }
    }

    protected long[] getLinkedRecordsFromLog(long ioRecid) {
        if ($assertionsDisabled || this.locks[Store.lockPos(ioRecid)].writeLock().isHeldByCurrentThread()) {
            long[] ret0 = (long[]) this.modified.get(ioRecid);
            if (ret0 == PREALLOC) {
                return ret0;
            }
            if (ret0 == null || ret0 == TOMBSTONE) {
                return null;
            }
            long[] ret = new long[ret0.length];
            for (int i = 0; i < ret0.length; i++) {
                ret[i] = this.log.getLong((ret0[i] & LOG_MASK_OFFSET) - 8);
            }
            return ret;
        }
        throw new AssertionError();
    }

    protected long longStackTake(long ioList, boolean recursive) {
        if (!$assertionsDisabled) {
            if (!this.structuralLock.isHeldByCurrentThread()) {
                throw new AssertionError();
            }
        }
        if ($assertionsDisabled || (ioList >= 120 && ioList < 32896)) {
            long dataOffset = this.indexVals[((int) ioList) / 8];
            if (dataOffset == 0) {
                return 0;
            }
            long pos = dataOffset >>> 48;
            dataOffset &= 281474976710640L;
            byte[] page = longStackGetPage(dataOffset);
            if (pos < 8) {
                throw new AssertionError();
            }
            long ret = longStackGetSixLong(page, (int) pos);
            if (pos == 8) {
                long next = longStackGetSixLong(page, 2);
                int i = page[0] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                long size = (long) ((r0 << 8) | (page[1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT));
                if (!$assertionsDisabled) {
                    if (size != ((long) page.length)) {
                        throw new AssertionError();
                    }
                }
                if (next == 0) {
                    this.indexVals[((int) ioList) / 8] = 0;
                    this.indexValsModified[((int) ioList) / 8] = true;
                    if (this.maxUsedIoList == ioList) {
                        while (true) {
                            if (this.indexVals[((int) this.maxUsedIoList) / 8] != 0) {
                                break;
                            }
                            if (this.maxUsedIoList <= 120) {
                                break;
                            }
                            this.maxUsedIoList -= 8;
                        }
                    }
                } else {
                    byte[] nextPage = longStackGetPage(next);
                    i = nextPage[0] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                    long nextSize = (long) ((r0 << 8) | (nextPage[1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT));
                    if ($assertionsDisabled || (nextSize - 8) % 6 == 0) {
                        this.indexVals[((int) ioList) / 8] = ((nextSize - 6) << 48) | next;
                        this.indexValsModified[((int) ioList) / 8] = true;
                    } else {
                        throw new AssertionError();
                    }
                }
                freePhysPut((size << 48) | dataOffset, true);
                if ($assertionsDisabled || (dataOffset >>> 48) == 0) {
                    this.longStackPages.remove(dataOffset);
                    return ret;
                }
                throw new AssertionError();
            }
            pos -= 6;
            this.indexVals[((int) ioList) / 8] = (pos << 48) | dataOffset;
            this.indexValsModified[((int) ioList) / 8] = true;
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
            long dataOffset = this.indexVals[((int) ioList) / 8];
            long pos = dataOffset >>> 48;
            dataOffset &= 281474976710640L;
            long listPhysid;
            byte[] page;
            if (dataOffset == 0) {
                listPhysid = freePhysTake(1232, true, true) & 281474976710640L;
                if (listPhysid == 0) {
                    throw new AssertionError();
                } else if ($assertionsDisabled || (listPhysid >>> 48) == 0) {
                    page = new byte[1232];
                    page[0] = (byte) ((page.length >>> 8) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
                    page[1] = (byte) (page.length & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
                    longStackPutSixLong(page, 2, 0);
                    longStackPutSixLong(page, 8, offset);
                    this.indexVals[((int) ioList) / 8] = 2251799813685248L | listPhysid;
                    this.indexValsModified[((int) ioList) / 8] = true;
                    if (this.maxUsedIoList <= ioList) {
                        this.maxUsedIoList = ioList;
                    }
                    this.longStackPages.put(listPhysid, page);
                    return;
                } else {
                    throw new AssertionError();
                }
            }
            page = longStackGetPage(dataOffset);
            int i = page[0] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            long size = (long) ((r0 << 8) | (page[1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT));
            if (!$assertionsDisabled && 6 + pos > size) {
                throw new AssertionError();
            } else if (6 + pos == size) {
                long newPageSize = 1232;
                if (ioList == StoreDirect.size2ListIoRecid(1232)) {
                    newPageSize = 1280;
                }
                listPhysid = freePhysTake((int) newPageSize, true, true) & 281474976710640L;
                if (listPhysid == 0) {
                    throw new AssertionError();
                }
                byte[] newPage = new byte[((int) newPageSize)];
                newPage[0] = (byte) ((int) (255 & (newPageSize >>> 8)));
                newPage[1] = (byte) ((int) (255 & newPageSize));
                longStackPutSixLong(newPage, 2, 281474976710640L & dataOffset);
                longStackPutSixLong(newPage, 8, offset);
                if ($assertionsDisabled || (listPhysid >>> 48) == 0) {
                    this.longStackPages.put(listPhysid, newPage);
                    this.indexVals[((int) ioList) / 8] = 2251799813685248L | listPhysid;
                    this.indexValsModified[((int) ioList) / 8] = true;
                    return;
                }
                throw new AssertionError();
            } else {
                pos += 6;
                longStackPutSixLong(page, (int) pos, offset);
                this.indexVals[((int) ioList) / 8] = (pos << 48) | dataOffset;
                this.indexValsModified[((int) ioList) / 8] = true;
            }
        } else {
            throw new AssertionError("wrong ioList: " + ioList);
        }
    }

    protected static long longStackGetSixLong(byte[] page, int pos) {
        return (((((((long) (page[pos + 0] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 40) | (((long) (page[pos + 1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 32)) | (((long) (page[pos + 2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 24)) | (((long) (page[pos + 3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 16)) | (((long) (page[pos + 4] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 8)) | (((long) (page[pos + 5] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << null);
    }

    protected static void longStackPutSixLong(byte[] page, int pos, long value) {
        if ($assertionsDisabled || (value >= 0 && (value >>> 48) == 0)) {
            page[pos + 0] = (byte) ((int) ((value >> 40) & 255));
            page[pos + 1] = (byte) ((int) ((value >> 32) & 255));
            page[pos + 2] = (byte) ((int) ((value >> 24) & 255));
            page[pos + 3] = (byte) ((int) ((value >> 16) & 255));
            page[pos + 4] = (byte) ((int) ((value >> 8) & 255));
            page[pos + 5] = (byte) ((int) ((value >> null) & 255));
            return;
        }
        throw new AssertionError("value does not fit");
    }

    protected byte[] longStackGetPage(long offset) {
        if (!$assertionsDisabled && offset < 16) {
            throw new AssertionError();
        } else if ($assertionsDisabled || (offset >>> 48) == 0) {
            byte[] ret = (byte[]) this.longStackPages.get(offset);
            if (ret == null) {
                int size = this.phys.getUnsignedShort(offset);
                if ($assertionsDisabled || size >= 14) {
                    ret = new byte[size];
                    try {
                        this.phys.getDataInput(offset, size).readFully(ret);
                        this.longStackPages.put(offset, ret);
                    } catch (IOException e) {
                        throw new IOError(e);
                    }
                }
                throw new AssertionError();
            }
            return ret;
        } else {
            throw new AssertionError();
        }
    }

    public void close() {
        for (Runnable closeListener : this.closeListeners) {
            closeListener.run();
        }
        if (this.serializerPojo != null && this.serializerPojo.hasUnsavedChanges()) {
            this.serializerPojo.save(this);
        }
        lockAllWrite();
        try {
            if (this.log != null) {
                this.log.sync();
                this.log.close();
                if (this.deleteFilesAfterClose) {
                    this.log.deleteFile();
                }
            }
            this.index.sync();
            this.phys.sync();
            this.index.close();
            this.phys.close();
            if (this.deleteFilesAfterClose) {
                this.index.deleteFile();
                this.phys.deleteFile();
            }
            this.index = null;
            this.phys = null;
        } finally {
            unlockAllWrite();
        }
    }

    protected void compactPreUnderLock() {
        if (!$assertionsDisabled && !this.structuralLock.isLocked()) {
            throw new AssertionError();
        } else if (logDirty()) {
            throw new IllegalAccessError("WAL not empty; commit first, than compact");
        }
    }

    protected void compactPostUnderLock() {
        if ($assertionsDisabled || this.structuralLock.isLocked()) {
            reloadIndexFile();
            return;
        }
        throw new AssertionError();
    }

    public boolean canRollback() {
        return true;
    }

    protected void logChecksumAdd(int cs) {
        int old;
        do {
            old = this.logChecksum.get();
        } while (!this.logChecksum.compareAndSet(old, old | cs));
    }
}
