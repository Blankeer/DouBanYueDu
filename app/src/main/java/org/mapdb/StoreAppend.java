package org.mapdb;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import org.mapdb.Fun.Tuple2;
import org.mapdb.LongMap.LongMapIterator;
import org.mapdb.Volume.MemoryVol;

@Deprecated
class StoreAppend extends Store {
    static final /* synthetic */ boolean $assertionsDisabled;
    protected static final long END = -2;
    protected static final long FILE_MASK = 16777215;
    protected static final int FILE_SHIFT = 24;
    protected static final long HEADER = 1239900952130003033L;
    protected static final int MAX_FILE_SIZE_SHIFT = 20;
    protected static final long RECIDP = 3;
    protected static final long SIZEP = 2;
    protected static final long SKIP = -1;
    protected volatile boolean closed;
    protected long currFileNum;
    protected long currPos;
    protected Volume currVolume;
    protected final boolean deleteFilesAfterClose;
    protected final File file;
    protected Volume index;
    protected final LongMap<Long> indexInTx;
    protected long maxRecid;
    protected volatile boolean modified;
    protected final boolean readOnly;
    protected long rollbackCurrFileNum;
    protected long rollbackCurrPos;
    protected long rollbackMaxRecid;
    protected final boolean syncOnCommit;
    protected final boolean tx;
    protected final boolean useRandomAccessFile;
    protected final LongConcurrentHashMap<Volume> volumes;

    static {
        $assertionsDisabled = !StoreAppend.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public StoreAppend(File file, boolean useRandomAccessFile, boolean readOnly, boolean transactionDisabled, boolean deleteFilesAfterClose, boolean syncOnCommitDisabled, boolean checksum, boolean compress, byte[] password, boolean disableLocks) {
        super(checksum, compress, password, disableLocks);
        this.closed = $assertionsDisabled;
        this.modified = $assertionsDisabled;
        this.volumes = new LongConcurrentHashMap();
        this.index = new MemoryVol($assertionsDisabled, 0, MAX_FILE_SIZE_SHIFT);
        this.file = file;
        this.useRandomAccessFile = useRandomAccessFile;
        this.readOnly = readOnly;
        this.deleteFilesAfterClose = deleteFilesAfterClose;
        this.syncOnCommit = !syncOnCommitDisabled ? true : $assertionsDisabled;
        this.tx = !transactionDisabled ? true : $assertionsDisabled;
        this.indexInTx = this.tx ? new LongConcurrentHashMap() : null;
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent.exists() && parent.isDirectory()) {
            File f;
            SortedSet<Tuple2<Long, File>> sortedFiles = new TreeSet();
            String prefix = file.getName();
            for (File f2 : parent.listFiles()) {
                String name = f2.getName();
                if (name.startsWith(prefix) && name.length() > prefix.length() + 1) {
                    String number = name.substring(prefix.length() + 1, name.length());
                    if (number.matches("^[0-9]+$")) {
                        sortedFiles.add(Fun.t2(Long.valueOf(number), f2));
                    }
                }
            }
            long pos;
            long recid;
            if (sortedFiles.isEmpty()) {
                Volume zero = Volume.volumeForFile(getFileFromNum(0), useRandomAccessFile, readOnly, 0, MAX_FILE_SIZE_SHIFT, 0);
                zero.ensureAvailable(64);
                zero.putLong(0, HEADER);
                pos = 8;
                for (recid = 1; recid <= 7; recid++) {
                    pos += (long) zero.putPackedLong(pos, RECIDP + recid);
                    pos += (long) zero.putPackedLong(pos, SIZEP);
                }
                this.maxRecid = 7;
                this.index.ensureAvailable(64);
                this.volumes.put(0, zero);
                if (this.tx) {
                    this.rollbackCurrPos = pos;
                    this.rollbackMaxRecid = this.maxRecid;
                    this.rollbackCurrFileNum = 0;
                    zero.putUnsignedByte(pos, 1);
                    pos++;
                }
                this.currVolume = zero;
                this.currPos = pos;
                return;
            }
            Iterator<Volume> vols;
            Volume next;
            for (Tuple2<Long, File> t : sortedFiles) {
                Long num = t.a;
                f2 = (File) t.b;
                Volume vol = Volume.volumeForFile(f2, useRandomAccessFile, readOnly, 0, MAX_FILE_SIZE_SHIFT, 0);
                if (vol.isEmpty() || vol.getLong(0) != HEADER) {
                    vol.sync();
                    vol.close();
                    vols = this.volumes.valuesIterator();
                    while (vols.hasNext()) {
                        next = (Volume) vols.next();
                        next.sync();
                        next.close();
                    }
                    throw new IOError(new IOException("File corrupted: " + f2));
                }
                this.volumes.put(num.longValue(), vol);
                pos = 8;
                while (pos <= FILE_MASK) {
                    recid = vol.getPackedLong(pos);
                    pos += (long) packedLongSize(recid);
                    recid -= RECIDP;
                    this.maxRecid = Math.max(recid, this.maxRecid);
                    if (recid == END) {
                        this.currVolume = vol;
                        this.currPos = pos;
                        this.currFileNum = num.longValue();
                        this.rollbackCurrFileNum = num.longValue();
                        this.rollbackMaxRecid = this.maxRecid;
                        this.rollbackCurrPos = pos - 1;
                        return;
                    } else if (recid != SKIP) {
                        if (recid <= 0) {
                            vols = this.volumes.valuesIterator();
                            while (vols.hasNext()) {
                                next = (Volume) vols.next();
                                next.sync();
                                next.close();
                            }
                            throw new IOError(new IOException("File corrupted: " + f2));
                        }
                        this.index.ensureAvailable((8 * recid) + 8);
                        long indexVal = (num.longValue() << FILE_SHIFT) | pos;
                        long size = vol.getPackedLong(pos);
                        pos += (long) packedLongSize(size);
                        size -= SIZEP;
                        if (size == 0) {
                            this.index.putLong(8 * recid, 0);
                        } else if (size > 0) {
                            pos += size;
                            this.index.putLong(8 * recid, indexVal);
                        } else {
                            this.index.putLong(8 * recid, Long.MIN_VALUE);
                        }
                    }
                }
            }
            vols = this.volumes.valuesIterator();
            while (vols.hasNext()) {
                next = (Volume) vols.next();
                next.sync();
                next.close();
            }
            throw new IOError(new IOException("File not sealed, data possibly corrupted"));
        }
        throw new IllegalArgumentException("Parent dir does not exist: " + file);
    }

    public StoreAppend(File file) {
        this(file, $assertionsDisabled, $assertionsDisabled, $assertionsDisabled, $assertionsDisabled, $assertionsDisabled, $assertionsDisabled, $assertionsDisabled, null, $assertionsDisabled);
    }

    protected File getFileFromNum(long fileNumber) {
        return new File(this.file.getPath() + "." + fileNumber);
    }

    protected void rollover() {
        if (this.currVolume.getLong(0) != HEADER) {
            throw new AssertionError();
        } else if (this.currPos > FILE_MASK && !this.readOnly) {
            this.currVolume.sync();
            this.currFileNum++;
            this.currVolume = Volume.volumeForFile(getFileFromNum(this.currFileNum), this.useRandomAccessFile, this.readOnly, 0, MAX_FILE_SIZE_SHIFT, 0);
            this.currVolume.ensureAvailable(8);
            this.currVolume.putLong(0, HEADER);
            this.currPos = 8;
            this.volumes.put(this.currFileNum, this.currVolume);
        }
    }

    protected long indexVal(long recid) {
        if (this.tx) {
            Long val = (Long) this.indexInTx.get(recid);
            if (val != null) {
                return val.longValue();
            }
        }
        return this.index.getLong(8 * recid);
    }

    protected void setIndexVal(long recid, long indexVal) {
        if (this.tx) {
            this.indexInTx.put(recid, Long.valueOf(indexVal));
            return;
        }
        this.index.ensureAvailable((recid * 8) + 8);
        this.index.putLong(recid * 8, indexVal);
    }

    public long preallocate() {
        Lock lock = this.locks[new Random().nextInt(this.locks.length)].readLock();
        lock.lock();
        try {
            this.structuralLock.lock();
            long recid = this.maxRecid + 1;
            this.maxRecid = recid;
            this.modified = true;
            this.structuralLock.unlock();
            if ($assertionsDisabled || recid > 0) {
                lock.unlock();
                return recid;
            }
            throw new AssertionError();
        } catch (Throwable th) {
            lock.unlock();
        }
    }

    public void preallocate(long[] recids) {
        Lock lock = this.locks[new Random().nextInt(this.locks.length)].readLock();
        lock.lock();
        try {
            this.structuralLock.lock();
            int i = 0;
            while (i < recids.length) {
                long j = this.maxRecid + 1;
                this.maxRecid = j;
                recids[i] = j;
                if ($assertionsDisabled || recids[i] > 0) {
                    i++;
                } else {
                    throw new AssertionError();
                }
            }
            this.modified = true;
            this.structuralLock.unlock();
            lock.unlock();
        } catch (Throwable th) {
            lock.unlock();
        }
    }

    public <A> long put(A value, Serializer<A> serializer) {
        if ($assertionsDisabled || value != null) {
            DataOutput2 out = serialize(value, serializer);
            Lock lock = this.locks[new Random().nextInt(this.locks.length)].readLock();
            lock.lock();
            try {
                this.structuralLock.lock();
                rollover();
                this.currVolume.ensureAvailable(((this.currPos + 6) + 4) + ((long) out.pos));
                long recid = this.maxRecid + 1;
                this.maxRecid = recid;
                this.currPos += (long) this.currVolume.putPackedLong(this.currPos, RECIDP + recid);
                long indexVal = (this.currFileNum << FILE_SHIFT) | this.currPos;
                this.currPos += (long) this.currVolume.putPackedLong(this.currPos, ((long) out.pos) + SIZEP);
                long oldPos = this.currPos;
                this.currPos += (long) out.pos;
                this.modified = true;
                this.structuralLock.unlock();
                this.currVolume.putData(oldPos, out.buf, 0, out.pos);
                this.recycledDataOuts.offer(out);
                setIndexVal(recid, indexVal);
                if ($assertionsDisabled || recid > 0) {
                    lock.unlock();
                    return recid;
                }
                throw new AssertionError();
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public <A> A get(long recid, Serializer<A> serializer) {
        if ($assertionsDisabled || recid > 0) {
            Lock lock = this.locks[Store.lockPos(recid)].readLock();
            lock.lock();
            try {
                A noLock = getNoLock(recid, serializer);
                lock.unlock();
                return noLock;
            } catch (IOException e) {
                throw new IOError(e);
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    protected <A> A getNoLock(long recid, Serializer<A> serializer) throws IOException {
        long indexVal = indexVal(recid);
        if (indexVal == 0) {
            return null;
        }
        Volume vol = (Volume) this.volumes.get(indexVal >>> FILE_SHIFT);
        long fileOffset = indexVal & FILE_MASK;
        long size = vol.getPackedLong(fileOffset);
        fileOffset += (long) packedLongSize(size);
        size -= SIZEP;
        if (size < 0) {
            return null;
        }
        if (size == 0) {
            return serializer.deserialize(new DataInput2(new byte[0]), 0);
        }
        return deserialize(serializer, (int) size, (DataInput2) vol.getDataInput(fileOffset, (int) size));
    }

    public <A> void update(long recid, A value, Serializer<A> serializer) {
        if (!$assertionsDisabled && value == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || recid > 0) {
            DataOutput2 out = serialize(value, serializer);
            Lock lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            try {
                updateNoLock(recid, out);
                this.recycledDataOuts.offer(out);
            } finally {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    protected void updateNoLock(long recid, DataOutput2 out) {
        this.structuralLock.lock();
        try {
            rollover();
            this.currVolume.ensureAvailable(((this.currPos + 6) + 4) + ((long) out.pos));
            this.currPos += (long) this.currVolume.putPackedLong(this.currPos, RECIDP + recid);
            long indexVal = (this.currFileNum << FILE_SHIFT) | this.currPos;
            this.currPos += (long) this.currVolume.putPackedLong(this.currPos, ((long) out.pos) + SIZEP);
            long oldPos = this.currPos;
            this.currPos += (long) out.pos;
            this.modified = true;
            this.currVolume.putData(oldPos, out.buf, 0, out.pos);
            setIndexVal(recid, indexVal);
        } finally {
            this.structuralLock.unlock();
        }
    }

    public <A> boolean compareAndSwap(long recid, A expectedOldValue, A newValue, Serializer<A> serializer) {
        if (!$assertionsDisabled && (expectedOldValue == null || newValue == null)) {
            throw new AssertionError();
        } else if ($assertionsDisabled || recid > 0) {
            DataOutput2 out = serialize(newValue, serializer);
            Lock lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            try {
                boolean ret;
                if (expectedOldValue.equals(getNoLock(recid, serializer))) {
                    updateNoLock(recid, out);
                    ret = true;
                } else {
                    ret = $assertionsDisabled;
                }
                lock.unlock();
                this.recycledDataOuts.offer(out);
                return ret;
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
            Lock lock = this.locks[Store.lockPos(recid)].writeLock();
            lock.lock();
            try {
                this.structuralLock.lock();
                rollover();
                this.currVolume.ensureAvailable((this.currPos + 6) + 0);
                this.currPos += (long) this.currVolume.putPackedLong(this.currPos, SIZEP + recid);
                setIndexVal(recid, (this.currFileNum << FILE_SHIFT) | this.currPos);
                this.currPos += (long) this.currVolume.putPackedLong(this.currPos, 1);
                this.modified = true;
                this.structuralLock.unlock();
                lock.unlock();
            } catch (Throwable th) {
                lock.unlock();
            }
        } else {
            throw new AssertionError();
        }
    }

    public void close() {
        if (!this.closed) {
            for (Runnable closeListener : this.closeListeners) {
                closeListener.run();
            }
            if (this.serializerPojo != null && this.serializerPojo.hasUnsavedChanges()) {
                this.serializerPojo.save(this);
            }
            Iterator<Volume> iter = this.volumes.valuesIterator();
            if (!this.readOnly && this.modified) {
                rollover();
                this.currVolume.putUnsignedByte(this.currPos, 1);
            }
            while (iter.hasNext()) {
                Volume v = (Volume) iter.next();
                v.sync();
                v.close();
                if (this.deleteFilesAfterClose) {
                    v.deleteFile();
                }
            }
            this.volumes.clear();
            this.closed = true;
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void commit() {
        if (this.tx) {
            lockAllWrite();
            try {
                LongMapIterator<Long> iter = this.indexInTx.longMapIterator();
                while (iter.moveToNext()) {
                    this.index.ensureAvailable((iter.key() * 8) + 8);
                    this.index.putLong(iter.key() * 8, ((Long) iter.value()).longValue());
                }
                Volume rollbackCurrVolume = (Volume) this.volumes.get(this.rollbackCurrFileNum);
                rollbackCurrVolume.putUnsignedByte(this.rollbackCurrPos, 2);
                if (this.syncOnCommit) {
                    rollbackCurrVolume.sync();
                }
                this.indexInTx.clear();
                rollover();
                this.rollbackCurrPos = this.currPos;
                this.rollbackMaxRecid = this.maxRecid;
                this.rollbackCurrFileNum = this.currFileNum;
                this.currVolume.putUnsignedByte(this.rollbackCurrPos, 1);
                this.currPos++;
                if (this.serializerPojo != null && this.serializerPojo.hasUnsavedChanges()) {
                    this.serializerPojo.save(this);
                }
                unlockAllWrite();
            } catch (Throwable th) {
                unlockAllWrite();
            }
        } else {
            this.currVolume.sync();
        }
    }

    public void rollback() throws UnsupportedOperationException {
        if (this.tx) {
            lockAllWrite();
            try {
                this.indexInTx.clear();
                this.currVolume = (Volume) this.volumes.get(this.rollbackCurrFileNum);
                this.currPos = this.rollbackCurrPos;
                this.maxRecid = this.rollbackMaxRecid;
                this.currFileNum = this.rollbackCurrFileNum;
            } finally {
                unlockAllWrite();
            }
        } else {
            throw new UnsupportedOperationException("Transactions are disabled");
        }
    }

    public boolean canRollback() {
        return this.tx;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void clearCache() {
    }

    public void compact() {
        if (this.readOnly) {
            throw new IllegalAccessError("readonly");
        }
        lockAllWrite();
        try {
            if (this.indexInTx.isEmpty()) {
                LongHashMap<Boolean> ff = new LongHashMap();
                for (long recid = 0; recid <= this.maxRecid; recid++) {
                    long indexVal = this.index.getLong(8 * recid);
                    if (indexVal != 0) {
                        ff.put(indexVal >>> FILE_SHIFT, Boolean.valueOf(true));
                    }
                }
                LongMapIterator<Volume> iter = this.volumes.longMapIterator();
                while (iter.moveToNext()) {
                    long fileNum = iter.key();
                    if (fileNum != this.currFileNum && ff.get(fileNum) == null) {
                        Volume v = (Volume) iter.value();
                        v.sync();
                        v.close();
                        v.deleteFile();
                        iter.remove();
                    }
                }
                return;
            }
            throw new IllegalAccessError("uncommited changes");
        } finally {
            unlockAllWrite();
        }
    }

    public long getMaxRecid() {
        return this.maxRecid;
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
        rollover();
        byte[] b = null;
        if (data != null) {
            data = data.duplicate();
            b = new byte[data.remaining()];
            data.get(b);
        }
        update(recid, b, Serializer.BYTE_ARRAY_NOSIZE);
        this.modified = true;
    }

    public long getSizeLimit() {
        return 0;
    }

    public long getCurrSize() {
        return this.currFileNum * FILE_MASK;
    }

    public long getFreeSize() {
        return 0;
    }

    public String calculateStatistics() {
        return null;
    }

    protected static int packedLongSize(long value) {
        int ret = 1;
        while ((-128 & value) != 0) {
            ret++;
            value >>>= 7;
        }
        return ret;
    }
}
