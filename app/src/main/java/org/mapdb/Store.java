package org.mapdb;

import android.support.v4.media.TransportMediator;
import java.io.DataInput;
import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import org.mapdb.TxEngine.Tx;

public abstract class Store implements Engine {
    protected static final int CHECKSUM_FLAG_MASK = 1;
    protected static final int CHUNK_SIZE = 1048576;
    protected static final int CHUNK_SIZE_MOD_MASK = 1048575;
    protected static final int COMPRESS_FLAG_MASK = 4;
    protected static final int ENCRYPT_FLAG_MASK = 8;
    private static final int LOCK_MASK = 127;
    protected static final Logger LOG;
    protected final ThreadLocal<CompressLZF> LZF;
    protected final boolean checksum;
    List<Runnable> closeListeners;
    protected final boolean compress;
    protected final boolean encrypt;
    protected final EncryptionXTEA encryptionXTEA;
    protected final ReentrantReadWriteLock[] locks;
    protected final ReentrantReadWriteLock newRecidLock;
    protected final byte[] password;
    protected final Queue<DataOutput2> recycledDataOuts;
    protected SerializerPojo serializerPojo;
    protected Lock serializerPojoInitLock;
    protected final ReentrantLock structuralLock;

    public abstract String calculateStatistics();

    public abstract long getCurrSize();

    public abstract Iterator<Long> getFreeRecids();

    public abstract long getFreeSize();

    public abstract long getMaxRecid();

    public abstract ByteBuffer getRaw(long j);

    public abstract long getSizeLimit();

    public abstract void updateRaw(long j, ByteBuffer byteBuffer);

    static {
        LOG = Logger.getLogger(Store.class.getName());
    }

    protected Store(boolean checksum, boolean compress, byte[] password, boolean disableLocks) {
        ThreadLocal threadLocal = null;
        boolean z = false;
        this.serializerPojoInitLock = new ReentrantLock(false);
        this.structuralLock = new ReentrantLock(false);
        this.newRecidLock = new ReentrantReadWriteLock(false);
        this.locks = new ReentrantReadWriteLock[TransportMediator.FLAG_KEY_MEDIA_NEXT];
        for (int i = 0; i < this.locks.length; i += CHECKSUM_FLAG_MASK) {
            this.locks[i] = new ReentrantReadWriteLock(false);
        }
        this.recycledDataOuts = new ArrayBlockingQueue(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        this.closeListeners = new CopyOnWriteArrayList();
        this.checksum = checksum;
        this.compress = compress;
        if (password != null) {
            z = true;
        }
        this.encrypt = z;
        this.password = password;
        this.encryptionXTEA = !this.encrypt ? null : new EncryptionXTEA(password);
        if (compress) {
            threadLocal = new ThreadLocal<CompressLZF>() {
                protected CompressLZF initialValue() {
                    return new CompressLZF();
                }
            };
        }
        this.LZF = threadLocal;
    }

    public void printStatistics() {
        System.out.println(calculateStatistics());
    }

    public SerializerPojo getSerializerPojo() {
        Lock pojoLock = this.serializerPojoInitLock;
        if (pojoLock != null) {
            pojoLock.lock();
            try {
                if (this.serializerPojo == null) {
                    this.serializerPojo = new SerializerPojo((CopyOnWriteArrayList) get(2, SerializerPojo.serializer));
                    this.serializerPojoInitLock = null;
                }
                pojoLock.unlock();
            } catch (Throwable th) {
                pojoLock.unlock();
            }
        }
        return this.serializerPojo;
    }

    protected void lockAllWrite() {
        this.newRecidLock.writeLock().lock();
        ReentrantReadWriteLock[] arr$ = this.locks;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += CHECKSUM_FLAG_MASK) {
            arr$[i$].writeLock().lock();
        }
        this.structuralLock.lock();
    }

    protected void unlockAllWrite() {
        this.structuralLock.unlock();
        ReentrantReadWriteLock[] arr$ = this.locks;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += CHECKSUM_FLAG_MASK) {
            arr$[i$].writeLock().unlock();
        }
        this.newRecidLock.writeLock().unlock();
    }

    protected <A> DataOutput2 serialize(A value, Serializer<A> serializer) {
        try {
            DataOutput2 out = newDataOut2();
            serializer.serialize(out, value);
            if (out.pos > 0) {
                if (this.compress) {
                    int newLen;
                    DataOutput2 tmp = newDataOut2();
                    tmp.ensureAvail(out.pos + 40);
                    try {
                        newLen = ((CompressLZF) this.LZF.get()).compress(out.buf, out.pos, tmp.buf, 0);
                    } catch (IndexOutOfBoundsException e) {
                        newLen = 0;
                    }
                    if (newLen >= out.pos) {
                        newLen = 0;
                    }
                    if (newLen == 0) {
                        this.recycledDataOuts.offer(tmp);
                        out.ensureAvail(out.pos + CHECKSUM_FLAG_MASK);
                        System.arraycopy(out.buf, 0, out.buf, CHECKSUM_FLAG_MASK, out.pos);
                        out.pos += CHECKSUM_FLAG_MASK;
                        out.buf[0] = (byte) 0;
                    } else {
                        int decompSize = out.pos;
                        out.pos = 0;
                        DataOutput2.packInt(out, decompSize);
                        out.write(tmp.buf, 0, newLen);
                        this.recycledDataOuts.offer(tmp);
                    }
                }
                if (this.encrypt) {
                    int size = out.pos;
                    if (size % 16 != 0) {
                        size += 16 - (size % 16);
                    }
                    int sizeDif = size - out.pos;
                    out.ensureAvail(sizeDif + CHECKSUM_FLAG_MASK);
                    this.encryptionXTEA.encrypt(out.buf, 0, size);
                    out.pos = size;
                    out.writeByte(sizeDif);
                }
                if (this.checksum) {
                    CRC32 crc = new CRC32();
                    crc.update(out.buf, 0, out.pos);
                    out.writeInt((int) crc.getValue());
                }
            }
            return out;
        } catch (IOException e2) {
            throw new IOError(e2);
        }
    }

    protected DataOutput2 newDataOut2() {
        DataOutput2 tmp = (DataOutput2) this.recycledDataOuts.poll();
        if (tmp == null) {
            return new DataOutput2();
        }
        tmp.pos = 0;
        return tmp;
    }

    protected <A> A deserialize(Serializer<A> serializer, int size, DataInput input) throws IOException {
        DataInput2 di = (DataInput2) input;
        if (size > 0) {
            DataOutput2 tmp;
            if (this.checksum) {
                size -= 4;
                tmp = newDataOut2();
                tmp.ensureAvail(size);
                int oldPos = di.pos;
                di.read(tmp.buf, 0, size);
                di.pos = oldPos;
                CRC32 crc = new CRC32();
                crc.update(tmp.buf, 0, size);
                this.recycledDataOuts.offer(tmp);
                if (((int) crc.getValue()) != di.buf.getInt(di.pos + size)) {
                    throw new IOException("Checksum does not match, data broken");
                }
            }
            if (this.encrypt) {
                tmp = newDataOut2();
                size--;
                tmp.ensureAvail(size);
                di.read(tmp.buf, 0, size);
                this.encryptionXTEA.decrypt(tmp.buf, 0, size);
                int cut = di.readUnsignedByte();
                di = new DataInput2(tmp.buf);
                size -= cut;
            }
            if (this.compress) {
                int decompSize = DataInput2.unpackInt(di);
                if (decompSize == 0) {
                    size--;
                } else {
                    DataOutput2 out = newDataOut2();
                    out.ensureAvail(decompSize);
                    ((CompressLZF) this.LZF.get()).expand(di.buf, di.pos, out.buf, 0, decompSize);
                    di = new DataInput2(out.buf);
                    size = decompSize;
                }
            }
        }
        int start = di.pos;
        A ret = serializer.deserialize(di, size);
        if (size + start > di.pos) {
            throw new AssertionError("data were not fully read, check your serializer ");
        } else if (size + start >= di.pos) {
            return ret;
        } else {
            throw new AssertionError("data were read beyond record size, check your serializer");
        }
    }

    public static Store forDB(DB db) {
        return forEngine(db.engine);
    }

    public static Store forEngine(Engine e) {
        if (e instanceof EngineWrapper) {
            return forEngine(((EngineWrapper) e).getWrappedEngine());
        }
        if (e instanceof Tx) {
            return forEngine(((Tx) e).getWrappedEngine());
        }
        return (Store) e;
    }

    protected int expectedMasks() {
        int i = 0;
        int i2 = (this.encrypt ? ENCRYPT_FLAG_MASK : 0) | (this.checksum ? CHECKSUM_FLAG_MASK : 0);
        if (this.compress) {
            i = COMPRESS_FLAG_MASK;
        }
        return i2 | i;
    }

    protected static int lockPos(long key) {
        int h = (int) ((key >>> 32) ^ key);
        h ^= (h >>> 20) ^ (h >>> 12);
        return (h ^ ((h >>> 7) ^ (h >>> COMPRESS_FLAG_MASK))) & LOCK_MASK;
    }

    public boolean canSnapshot() {
        return false;
    }

    public Engine snapshot() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Snapshots are not supported");
    }

    public void closeListenerRegister(Runnable closeListener) {
        this.closeListeners.add(closeListener);
    }

    public void closeListenerUnregister(Runnable closeListener) {
        this.closeListeners.remove(closeListener);
    }
}
