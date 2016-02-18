package org.mapdb;

import android.support.v4.media.TransportMediator;
import com.douban.book.reader.helper.AppUri;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Volume {
    static final /* synthetic */ boolean $assertionsDisabled;

    public interface Factory {
        Volume createIndexVolume();

        Volume createPhysVolume();

        Volume createTransLogVolume();
    }

    /* renamed from: org.mapdb.Volume.1 */
    static class AnonymousClass1 implements Factory {
        final /* synthetic */ boolean val$asyncWriteEnabled;
        final /* synthetic */ int val$chunkShift;
        final /* synthetic */ File val$indexFile;
        final /* synthetic */ File val$physFile;
        final /* synthetic */ int val$rafMode;
        final /* synthetic */ boolean val$readOnly;
        final /* synthetic */ int val$sizeIncrement;
        final /* synthetic */ long val$sizeLimit;
        final /* synthetic */ File val$transLogFile;

        AnonymousClass1(File file, int i, boolean z, long j, int i2, int i3, boolean z2, File file2, File file3) {
            this.val$indexFile = file;
            this.val$rafMode = i;
            this.val$readOnly = z;
            this.val$sizeLimit = j;
            this.val$chunkShift = i2;
            this.val$sizeIncrement = i3;
            this.val$asyncWriteEnabled = z2;
            this.val$physFile = file2;
            this.val$transLogFile = file3;
        }

        public Volume createIndexVolume() {
            boolean z = true;
            File file = this.val$indexFile;
            if (this.val$rafMode <= 1) {
                z = false;
            }
            return Volume.volumeForFile(file, z, this.val$readOnly, this.val$sizeLimit, this.val$chunkShift, this.val$sizeIncrement, this.val$asyncWriteEnabled);
        }

        public Volume createPhysVolume() {
            return Volume.volumeForFile(this.val$physFile, this.val$rafMode > 0, this.val$readOnly, this.val$sizeLimit, this.val$chunkShift, this.val$sizeIncrement, this.val$asyncWriteEnabled);
        }

        public Volume createTransLogVolume() {
            return Volume.volumeForFile(this.val$transLogFile, this.val$rafMode > 0, this.val$readOnly, this.val$sizeLimit, this.val$chunkShift, this.val$sizeIncrement, this.val$asyncWriteEnabled);
        }
    }

    /* renamed from: org.mapdb.Volume.2 */
    static class AnonymousClass2 implements Factory {
        final /* synthetic */ int val$chunkShift;
        final /* synthetic */ long val$sizeLimit;
        final /* synthetic */ boolean val$useDirectBuffer;

        AnonymousClass2(boolean z, long j, int i) {
            this.val$useDirectBuffer = z;
            this.val$sizeLimit = j;
            this.val$chunkShift = i;
        }

        public synchronized Volume createIndexVolume() {
            return new MemoryVol(this.val$useDirectBuffer, this.val$sizeLimit, this.val$chunkShift);
        }

        public synchronized Volume createPhysVolume() {
            return new MemoryVol(this.val$useDirectBuffer, this.val$sizeLimit, this.val$chunkShift);
        }

        public synchronized Volume createTransLogVolume() {
            return new MemoryVol(this.val$useDirectBuffer, this.val$sizeLimit, this.val$chunkShift);
        }
    }

    public static abstract class ByteBufferVol extends Volume {
        private static boolean unmapHackSupported;
        private static boolean windowsWorkaround;
        protected final boolean asyncWriteEnabled;
        protected final int chunkShift;
        protected final int chunkSize;
        protected final int chunkSizeModMask;
        protected volatile ByteBuffer[] chunks;
        protected final ReentrantLock growLock;
        protected final boolean hasLimit;
        protected final boolean readOnly;
        protected final long sizeLimit;

        protected abstract ByteBuffer makeNewBuffer(long j);

        protected ByteBufferVol(boolean readOnly, long sizeLimit, int chunkShift) {
            this(readOnly, sizeLimit, chunkShift, false);
        }

        protected ByteBufferVol(boolean readOnly, long sizeLimit, int chunkShift, boolean asyncWriteEnabled) {
            boolean z = true;
            this.growLock = new ReentrantLock(false);
            this.chunks = new ByteBuffer[0];
            this.readOnly = readOnly;
            this.sizeLimit = sizeLimit;
            this.chunkShift = chunkShift;
            this.chunkSize = 1 << chunkShift;
            this.chunkSizeModMask = this.chunkSize - 1;
            if (sizeLimit <= 0) {
                z = false;
            }
            this.hasLimit = z;
            this.asyncWriteEnabled = asyncWriteEnabled;
        }

        public final boolean tryAvailable(long offset) {
            if (this.hasLimit && offset > this.sizeLimit) {
                return false;
            }
            int chunkPos = (int) (offset >>> this.chunkShift);
            if (chunkPos < this.chunks.length) {
                return true;
            }
            this.growLock.lock();
            try {
                if (chunkPos < this.chunks.length) {
                    return true;
                }
                int oldSize = this.chunks.length;
                ByteBuffer[] chunks2 = this.chunks;
                chunks2 = (ByteBuffer[]) Arrays.copyOf(chunks2, Math.max(chunkPos + 1, chunks2.length + (chunks2.length / AppUri.OPEN_URL)));
                for (int pos = oldSize; pos < chunks2.length; pos++) {
                    chunks2[pos] = makeNewBuffer((1 * ((long) this.chunkSize)) * ((long) pos));
                }
                this.chunks = chunks2;
                this.growLock.unlock();
                return true;
            } finally {
                this.growLock.unlock();
            }
        }

        public final void putLong(long offset, long value) {
            this.chunks[(int) (offset >>> this.chunkShift)].putLong((int) (((long) this.chunkSizeModMask) & offset), value);
        }

        public final void putInt(long offset, int value) {
            this.chunks[(int) (offset >>> this.chunkShift)].putInt((int) (((long) this.chunkSizeModMask) & offset), value);
        }

        public final void putByte(long offset, byte value) {
            this.chunks[(int) (offset >>> this.chunkShift)].put((int) (((long) this.chunkSizeModMask) & offset), value);
        }

        public void putData(long offset, byte[] src, int srcPos, int srcSize) {
            ByteBuffer b1 = this.chunks[(int) (offset >>> this.chunkShift)].duplicate();
            b1.position((int) (((long) this.chunkSizeModMask) & offset));
            b1.put(src, srcPos, srcSize);
        }

        public final void putData(long offset, ByteBuffer buf) {
            ByteBuffer b1 = this.chunks[(int) (offset >>> this.chunkShift)].duplicate();
            b1.position((int) (((long) this.chunkSizeModMask) & offset));
            b1.put(buf);
        }

        public final long getLong(long offset) {
            return this.chunks[(int) (offset >>> this.chunkShift)].getLong((int) (((long) this.chunkSizeModMask) & offset));
        }

        public final int getInt(long offset) {
            return this.chunks[(int) (offset >>> this.chunkShift)].getInt((int) (((long) this.chunkSizeModMask) & offset));
        }

        public final byte getByte(long offset) {
            return this.chunks[(int) (offset >>> this.chunkShift)].get((int) (((long) this.chunkSizeModMask) & offset));
        }

        public final DataInput2 getDataInput(long offset, int size) {
            return new DataInput2(this.chunks[(int) (offset >>> this.chunkShift)], (int) (((long) this.chunkSizeModMask) & offset));
        }

        public boolean isEmpty() {
            return this.chunks.length == 0;
        }

        public boolean isSliced() {
            return true;
        }

        protected void unmap(MappedByteBuffer b) {
            try {
                if (unmapHackSupported && !this.asyncWriteEnabled) {
                    Method cleanerMethod = b.getClass().getMethod("cleaner", new Class[0]);
                    if (cleanerMethod != null) {
                        cleanerMethod.setAccessible(true);
                        Object cleaner = cleanerMethod.invoke(b, new Object[0]);
                        if (cleaner != null) {
                            Method clearMethod = cleaner.getClass().getMethod("clean", new Class[0]);
                            if (clearMethod != null) {
                                clearMethod.invoke(cleaner, new Object[0]);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                unmapHackSupported = false;
            }
        }

        static {
            boolean z = true;
            unmapHackSupported = true;
            try {
                if (SerializerPojo.classForName("sun.nio.ch.DirectBuffer") == null) {
                    z = false;
                }
                unmapHackSupported = z;
            } catch (Exception e) {
                unmapHackSupported = false;
            }
            windowsWorkaround = System.getProperty("os.name").toLowerCase().startsWith("win");
        }
    }

    public static final class FileChannelVol extends Volume {
        static final /* synthetic */ boolean $assertionsDisabled;
        protected FileChannel channel;
        protected final int chunkSize;
        protected final File file;
        protected final Object growLock;
        protected final boolean hasLimit;
        protected RandomAccessFile raf;
        protected final boolean readOnly;
        protected volatile long size;
        protected final long sizeLimit;

        static {
            $assertionsDisabled = !Volume.class.desiredAssertionStatus();
        }

        public FileChannelVol(File file, boolean readOnly, long sizeLimit, int chunkShift, int sizeIncrement) {
            this.growLock = new Object();
            this.file = file;
            this.readOnly = readOnly;
            this.sizeLimit = sizeLimit;
            this.hasLimit = sizeLimit > 0;
            this.chunkSize = 1 << chunkShift;
            try {
                checkFolder(file, readOnly);
                if (!readOnly || file.exists()) {
                    this.raf = new RandomAccessFile(file, readOnly ? "r" : "rw");
                    this.channel = this.raf.getChannel();
                    this.size = this.channel.size();
                    return;
                }
                this.raf = null;
                this.channel = null;
                this.size = 0;
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        protected static void checkFolder(File file, boolean readOnly) throws IOException {
            File parent = file.getParentFile();
            if (parent == null) {
                parent = file.getCanonicalFile().getParentFile();
            }
            if (parent == null) {
                throw new IOException("Parent folder could not be determined for: " + file);
            } else if (!parent.exists() || !parent.isDirectory()) {
                throw new IOException("Parent folder does not exist: " + file);
            } else if (!parent.canRead()) {
                throw new IOException("Parent folder is not readable: " + file);
            } else if (!readOnly && !parent.canWrite()) {
                throw new IOException("Parent folder is not writable: " + file);
            }
        }

        public boolean tryAvailable(long offset) {
            if (this.hasLimit && offset > this.sizeLimit) {
                return false;
            }
            if (offset % ((long) this.chunkSize) != 0) {
                offset += ((long) this.chunkSize) - (offset % ((long) this.chunkSize));
            }
            if (offset > this.size) {
                synchronized (this.growLock) {
                    try {
                        this.channel.truncate(offset);
                        this.size = offset;
                    } catch (IOException e) {
                        throw new IOError(e);
                    }
                }
            }
            return true;
        }

        public void truncate(long size) {
            synchronized (this.growLock) {
                try {
                    this.size = size;
                    this.channel.truncate(size);
                } catch (IOException e) {
                    throw new IOError(e);
                }
            }
        }

        protected void writeFully(long offset, ByteBuffer buf) throws IOException {
            int remaining = buf.limit() - buf.position();
            while (remaining > 0) {
                int write = this.channel.write(buf, offset);
                if (write < 0) {
                    throw new EOFException();
                }
                remaining -= write;
            }
        }

        public final void putSixLong(long offset, long value) {
            if ($assertionsDisabled || (value >= 0 && (value >>> 48) == 0)) {
                try {
                    ByteBuffer buf = ByteBuffer.allocate(6);
                    buf.put(0, (byte) ((int) ((value >> 40) & 255)));
                    buf.put(1, (byte) ((int) ((value >> 32) & 255)));
                    buf.put(2, (byte) ((int) ((value >> 24) & 255)));
                    buf.put(3, (byte) ((int) ((value >> 16) & 255)));
                    buf.put(4, (byte) ((int) ((value >> 8) & 255)));
                    buf.put(5, (byte) ((int) ((value >> null) & 255)));
                    writeFully(offset, buf);
                    return;
                } catch (IOException e) {
                    throw new IOError(e);
                }
            }
            throw new AssertionError("value does not fit");
        }

        public void putLong(long offset, long value) {
            try {
                ByteBuffer buf = ByteBuffer.allocate(8);
                buf.putLong(0, value);
                writeFully(offset, buf);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void putInt(long offset, int value) {
            try {
                ByteBuffer buf = ByteBuffer.allocate(4);
                buf.putInt(0, value);
                writeFully(offset, buf);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void putByte(long offset, byte value) {
            try {
                ByteBuffer buf = ByteBuffer.allocate(1);
                buf.put(0, value);
                writeFully(offset, buf);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void putData(long offset, byte[] src, int srcPos, int srcSize) {
            try {
                writeFully(offset, ByteBuffer.wrap(src, srcPos, srcSize));
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void putData(long offset, ByteBuffer buf) {
            try {
                writeFully(offset, buf);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        protected void readFully(long offset, ByteBuffer buf) throws IOException {
            int remaining = buf.limit() - buf.position();
            while (remaining > 0) {
                int read = this.channel.read(buf, offset);
                if (read < 0) {
                    throw new EOFException();
                }
                remaining -= read;
            }
        }

        public final long getSixLong(long offset) {
            try {
                ByteBuffer buf = ByteBuffer.allocate(6);
                readFully(offset, buf);
                return (((((((long) (buf.get(0) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 40) | (((long) (buf.get(1) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 32)) | (((long) (buf.get(2) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 24)) | (((long) (buf.get(3) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 16)) | (((long) (buf.get(4) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 8)) | (((long) (buf.get(5) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << null);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public long getLong(long offset) {
            try {
                ByteBuffer buf = ByteBuffer.allocate(8);
                readFully(offset, buf);
                return buf.getLong(0);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public int getInt(long offset) {
            try {
                ByteBuffer buf = ByteBuffer.allocate(4);
                readFully(offset, buf);
                return buf.getInt(0);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public byte getByte(long offset) {
            try {
                ByteBuffer buf = ByteBuffer.allocate(1);
                readFully(offset, buf);
                return buf.get(0);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public DataInput2 getDataInput(long offset, int size) {
            try {
                ByteBuffer buf = ByteBuffer.allocate(size);
                readFully(offset, buf);
                return new DataInput2(buf, 0);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void close() {
            try {
                if (this.channel != null) {
                    this.channel.close();
                }
                this.channel = null;
                if (this.raf != null) {
                    this.raf.close();
                }
                this.raf = null;
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void sync() {
            try {
                this.channel.force(true);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public boolean isEmpty() {
            try {
                return this.channel == null || this.channel.size() == 0;
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void deleteFile() {
            this.file.delete();
        }

        public boolean isSliced() {
            return false;
        }

        public File getFile() {
            return this.file;
        }
    }

    public static final class MappedFileVol extends ByteBufferVol {
        static final /* synthetic */ boolean $assertionsDisabled;
        protected final File file;
        protected final FileChannel fileChannel;
        protected final MapMode mapMode;
        protected final RandomAccessFile raf;

        static {
            $assertionsDisabled = !Volume.class.desiredAssertionStatus();
        }

        public MappedFileVol(File file, boolean readOnly, long sizeLimit, int chunkShift, int sizeIncrement) {
            this(file, readOnly, sizeLimit, chunkShift, sizeIncrement, false);
        }

        public MappedFileVol(File file, boolean readOnly, long sizeLimit, int chunkShift, int sizeIncrement, boolean asyncWriteEnabled) {
            super(readOnly, sizeLimit, chunkShift, asyncWriteEnabled);
            this.file = file;
            this.mapMode = readOnly ? MapMode.READ_ONLY : MapMode.READ_WRITE;
            try {
                FileChannelVol.checkFolder(file, readOnly);
                this.raf = new RandomAccessFile(file, readOnly ? "r" : "rw");
                this.fileChannel = this.raf.getChannel();
                long fileSize = this.fileChannel.size();
                if (fileSize > 0) {
                    this.chunks = new ByteBuffer[((int) (fileSize >>> chunkShift))];
                    for (int i = 0; i < this.chunks.length; i++) {
                        this.chunks[i] = makeNewBuffer((1 * ((long) i)) * ((long) this.chunkSize));
                    }
                    return;
                }
                this.chunks = new ByteBuffer[0];
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void close() {
            this.growLock.lock();
            try {
                this.fileChannel.close();
                this.raf.close();
                for (ByteBuffer b : this.chunks) {
                    if (b != null && (b instanceof MappedByteBuffer)) {
                        unmap((MappedByteBuffer) b);
                    }
                }
                this.chunks = null;
                this.growLock.unlock();
            } catch (IOException e) {
                throw new IOError(e);
            } catch (Throwable th) {
                this.growLock.unlock();
            }
        }

        public void sync() {
            if (!this.readOnly) {
                this.growLock.lock();
                try {
                    for (ByteBuffer b : this.chunks) {
                        if (b != null && (b instanceof MappedByteBuffer)) {
                            ((MappedByteBuffer) b).force();
                        }
                    }
                } finally {
                    this.growLock.unlock();
                }
            }
        }

        protected ByteBuffer makeNewBuffer(long offset) {
            try {
                if (!$assertionsDisabled && (((long) this.chunkSizeModMask) & offset) != 0) {
                    throw new AssertionError();
                } else if ($assertionsDisabled || offset >= 0) {
                    ByteBuffer ret = this.fileChannel.map(this.mapMode, offset, (long) this.chunkSize);
                    if (this.mapMode == MapMode.READ_ONLY) {
                        ret = ret.asReadOnlyBuffer();
                    }
                    return ret;
                } else {
                    throw new AssertionError();
                }
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void deleteFile() {
            this.file.delete();
        }

        public File getFile() {
            return this.file;
        }

        public void truncate(long size) {
            int maxSize = ((int) (size >>> this.chunkShift)) + 1;
            if (maxSize != this.chunks.length) {
                if (maxSize > this.chunks.length) {
                    ensureAvailable(size);
                    return;
                }
                this.growLock.lock();
                try {
                    if (maxSize >= this.chunks.length) {
                        this.growLock.unlock();
                        return;
                    }
                    int i;
                    ByteBuffer[] old = this.chunks;
                    this.chunks = (ByteBuffer[]) Arrays.copyOf(this.chunks, maxSize);
                    for (i = maxSize; i < old.length; i++) {
                        unmap((MappedByteBuffer) old[i]);
                        old[i] = null;
                    }
                    if (ByteBufferVol.windowsWorkaround) {
                        for (i = 0; i < maxSize; i++) {
                            unmap((MappedByteBuffer) old[i]);
                            old[i] = null;
                        }
                    }
                    this.fileChannel.truncate((((long) this.chunkSize) * 1) * ((long) maxSize));
                    if (ByteBufferVol.windowsWorkaround) {
                        for (int pos = 0; pos < maxSize; pos++) {
                            this.chunks[pos] = makeNewBuffer((((long) this.chunkSize) * 1) * ((long) pos));
                        }
                    }
                    this.growLock.unlock();
                } catch (IOException e) {
                    throw new IOError(e);
                } catch (Throwable th) {
                    this.growLock.unlock();
                }
            }
        }
    }

    public static final class MemoryVol extends ByteBufferVol {
        protected final boolean useDirectBuffer;

        public String toString() {
            return super.toString() + ",direct=" + this.useDirectBuffer;
        }

        public MemoryVol(boolean useDirectBuffer, long sizeLimit, int chunkShift) {
            super(false, sizeLimit, chunkShift);
            this.useDirectBuffer = useDirectBuffer;
        }

        protected ByteBuffer makeNewBuffer(long offset) {
            return this.useDirectBuffer ? ByteBuffer.allocateDirect(this.chunkSize) : ByteBuffer.allocate(this.chunkSize);
        }

        public void truncate(long size) {
            int maxSize = ((int) (size >>> this.chunkShift)) + 1;
            if (maxSize != this.chunks.length) {
                if (maxSize > this.chunks.length) {
                    ensureAvailable(size);
                    return;
                }
                this.growLock.lock();
                try {
                    if (maxSize < this.chunks.length) {
                        ByteBuffer[] old = this.chunks;
                        this.chunks = (ByteBuffer[]) Arrays.copyOf(this.chunks, maxSize);
                        for (int i = maxSize; i < old.length; i++) {
                            if (old[i] instanceof MappedByteBuffer) {
                                unmap((MappedByteBuffer) old[i]);
                            }
                            old[i] = null;
                        }
                        this.growLock.unlock();
                    }
                } finally {
                    this.growLock.unlock();
                }
            }
        }

        public void close() {
            this.growLock.lock();
            try {
                for (ByteBuffer b : this.chunks) {
                    if (b != null && (b instanceof MappedByteBuffer)) {
                        unmap((MappedByteBuffer) b);
                    }
                }
                this.chunks = null;
            } finally {
                this.growLock.unlock();
            }
        }

        public void sync() {
        }

        public void deleteFile() {
        }

        public File getFile() {
            return null;
        }
    }

    public abstract void close();

    public abstract void deleteFile();

    public abstract byte getByte(long j);

    public abstract DataInput getDataInput(long j, int i);

    public abstract File getFile();

    public abstract int getInt(long j);

    public abstract long getLong(long j);

    public abstract boolean isEmpty();

    public abstract boolean isSliced();

    public abstract void putByte(long j, byte b);

    public abstract void putData(long j, ByteBuffer byteBuffer);

    public abstract void putData(long j, byte[] bArr, int i, int i2);

    public abstract void putInt(long j, int i);

    public abstract void putLong(long j, long j2);

    public abstract void sync();

    public abstract void truncate(long j);

    public abstract boolean tryAvailable(long j);

    static {
        $assertionsDisabled = !Volume.class.desiredAssertionStatus();
    }

    public void ensureAvailable(long offset) {
        if (!tryAvailable(offset)) {
            throw new IOError(new IOException("no free space to expand Volume"));
        }
    }

    public void putUnsignedShort(long offset, int value) {
        putByte(offset, (byte) (value >> 8));
        putByte(1 + offset, (byte) value);
    }

    public int getUnsignedShort(long offset) {
        return ((getByte(offset) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 8) | (getByte(1 + offset) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
    }

    public int getUnsignedByte(long offset) {
        return getByte(offset) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
    }

    public void putUnsignedByte(long offset, int b) {
        putByte(offset, (byte) (b & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT));
    }

    public long getSixLong(long pos) {
        return (((((((long) (getByte(0 + pos) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 40) | (((long) (getByte(1 + pos) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 32)) | (((long) (getByte(2 + pos) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 24)) | (((long) (getByte(3 + pos) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 16)) | (((long) (getByte(4 + pos) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << 8)) | (((long) (getByte(5 + pos) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) << null);
    }

    public void putSixLong(long pos, long value) {
        if ($assertionsDisabled || (value >= 0 && (value >>> 48) == 0)) {
            putByte(pos + 0, (byte) ((int) ((value >> 40) & 255)));
            putByte(1 + pos, (byte) ((int) ((value >> 32) & 255)));
            putByte(2 + pos, (byte) ((int) ((value >> 24) & 255)));
            putByte(3 + pos, (byte) ((int) ((value >> 16) & 255)));
            putByte(4 + pos, (byte) ((int) ((value >> 8) & 255)));
            putByte(5 + pos, (byte) ((int) ((value >> null) & 255)));
            return;
        }
        throw new AssertionError("value does not fit");
    }

    public int putPackedLong(long pos, long value) {
        if ($assertionsDisabled || value >= 0) {
            int ret;
            int ret2 = 0;
            while ((-128 & value) != 0) {
                ret = ret2 + 1;
                putUnsignedByte(((long) ret2) + pos, (((int) value) & TransportMediator.KEYCODE_MEDIA_PAUSE) | TransportMediator.FLAG_KEY_MEDIA_NEXT);
                value >>>= 7;
                ret2 = ret;
            }
            ret = ret2 + 1;
            putUnsignedByte(((long) ret2) + pos, (byte) ((int) value));
            return ret;
        }
        throw new AssertionError("negative value");
    }

    public long getPackedLong(long pos) {
        long result = 0;
        int offset = 0;
        long pos2 = pos;
        while (offset < 64) {
            pos = pos2 + 1;
            long b = (long) getUnsignedByte(pos2);
            result |= (127 & b) << offset;
            if ((128 & b) == 0) {
                return result;
            }
            offset += 7;
            pos2 = pos;
        }
        throw new AssertionError("Malformed long.");
    }

    public static Volume volumeForFile(File f, boolean useRandomAccessFile, boolean readOnly, long sizeLimit, int chunkShift, int sizeIncrement) {
        return volumeForFile(f, useRandomAccessFile, readOnly, sizeLimit, chunkShift, sizeIncrement, false);
    }

    public static Volume volumeForFile(File f, boolean useRandomAccessFile, boolean readOnly, long sizeLimit, int chunkShift, int sizeIncrement, boolean asyncWriteEnabled) {
        return useRandomAccessFile ? new FileChannelVol(f, readOnly, sizeLimit, chunkShift, sizeIncrement) : new MappedFileVol(f, readOnly, sizeLimit, chunkShift, sizeIncrement, asyncWriteEnabled);
    }

    public static Factory fileFactory(File indexFile, int rafMode, boolean readOnly, long sizeLimit, int chunkShift, int sizeIncrement) {
        return fileFactory(indexFile, rafMode, readOnly, sizeLimit, chunkShift, sizeIncrement, new File(indexFile.getPath() + StoreDirect.DATA_FILE_EXT), new File(indexFile.getPath() + StoreWAL.TRANS_LOG_FILE_EXT));
    }

    public static Factory fileFactory(File indexFile, int rafMode, boolean readOnly, long sizeLimit, int chunkShift, int sizeIncrement, File physFile, File transLogFile) {
        return fileFactory(indexFile, rafMode, readOnly, sizeLimit, chunkShift, sizeIncrement, physFile, transLogFile, false);
    }

    public static Factory fileFactory(File indexFile, int rafMode, boolean readOnly, long sizeLimit, int chunkShift, int sizeIncrement, File physFile, File transLogFile, boolean asyncWriteEnabled) {
        return new AnonymousClass1(indexFile, rafMode, readOnly, sizeLimit, chunkShift, sizeIncrement, asyncWriteEnabled, physFile, transLogFile);
    }

    public static Factory memoryFactory(boolean useDirectBuffer, long sizeLimit, int chunkShift) {
        return new AnonymousClass2(useDirectBuffer, sizeLimit, chunkShift);
    }

    public static void volumeTransfer(long size, Volume from, Volume to) {
        for (long offset = 0; offset < size; offset += (long) 65536) {
            int bb = (int) Math.min((long) 65536, size - offset);
            DataInput2 input = (DataInput2) from.getDataInput(offset, bb);
            ByteBuffer buf = input.buf.duplicate();
            buf.position(input.pos);
            buf.limit(input.pos + bb);
            to.ensureAvailable(((long) bb) + offset);
            to.putData(offset, buf);
        }
    }
}
