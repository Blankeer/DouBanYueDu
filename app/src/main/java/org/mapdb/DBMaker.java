package org.mapdb;

import android.support.v4.media.TransportMediator;
import com.douban.book.reader.entity.DbCacheEntity;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import io.fabric.sdk.android.services.network.UrlUtils;
import io.realm.internal.Table;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import org.mapdb.Caches.HardRef;
import org.mapdb.Caches.HashTable;
import org.mapdb.Caches.LRU;
import org.mapdb.Caches.WeakSoftRef;
import org.mapdb.EngineWrapper.CloseOnJVMShutdown;
import org.mapdb.EngineWrapper.ReadOnlyEngine;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Volume.Factory;

public class DBMaker<DBMakerT extends DBMaker<DBMakerT>> {
    protected final String TRUE;
    protected Properties props;

    protected interface Keys {
        public static final String asyncWrite = "asyncWrite";
        public static final String asyncWriteFlushDelay = "asyncWriteFlushDelay";
        public static final String asyncWriteQueueSize = "asyncWriteQueueSize";
        public static final String cache = "cache";
        public static final String cacheSize = "cacheSize";
        public static final String cache_disable = "disable";
        public static final String cache_hardRef = "hardRef";
        public static final String cache_hashTable = "hashTable";
        public static final String cache_lru = "lru";
        public static final String cache_softRef = "softRef";
        public static final String cache_weakRef = "weakRef";
        public static final String checksum = "checksum";
        public static final String closeOnJvmShutdown = "closeOnJvmShutdown";
        public static final String commitFileSyncDisable = "commitFileSyncDisable";
        public static final String compression = "compression";
        public static final String compression_lzf = "lzf";
        public static final String deleteFilesAfterClose = "deleteFilesAfterClose";
        public static final String encryption = "encryption";
        public static final String encryptionKey = "encryptionKey";
        public static final String encryption_xtea = "xtea";
        public static final String file = "file";
        public static final String freeSpaceReclaimQ = "freeSpaceReclaimQ";
        public static final String fullTx = "fullTx";
        public static final String readOnly = "readOnly";
        public static final String sizeLimit = "sizeLimit";
        public static final String snapshots = "snapshots";
        public static final String store = "store";
        public static final String store_append = "append";
        public static final String store_direct = "direct";
        public static final String store_heap = "heap";
        public static final String store_wal = "wal";
        public static final String strictDBGet = "strictDBGet";
        public static final String transactionDisable = "transactionDisable";
        public static final String volume = "volume";
        public static final String volume_byteBuffer = "byteBuffer";
        public static final String volume_directByteBuffer = "directByteBuffer";
        public static final String volume_mmapf = "mmapf";
        public static final String volume_mmapfIfSupported = "mmapfIfSupported";
        public static final String volume_mmapfPartial = "mmapfPartial";
        public static final String volume_raf = "raf";
    }

    protected DBMaker() {
        this.TRUE = "true";
        this.props = new Properties();
    }

    protected DBMaker(File file) {
        this.TRUE = "true";
        this.props = new Properties();
        this.props.setProperty(Keys.file, file.getPath());
    }

    public static DBMaker newHeapDB() {
        return new DBMaker()._newHeapDB();
    }

    public DBMakerT _newHeapDB() {
        this.props.setProperty(Keys.store, Keys.store_heap);
        return getThis();
    }

    public static DBMaker newMemoryDB() {
        return new DBMaker()._newMemoryDB();
    }

    public DBMakerT _newMemoryDB() {
        this.props.setProperty(Keys.volume, Keys.volume_byteBuffer);
        return getThis();
    }

    public static DBMaker newMemoryDirectDB() {
        return new DBMaker()._newMemoryDirectDB();
    }

    public DBMakerT _newMemoryDirectDB() {
        this.props.setProperty(Keys.volume, Keys.volume_directByteBuffer);
        return getThis();
    }

    protected static DBMaker newAppendFileDB(File file) {
        return new DBMaker()._newAppendFileDB(file);
    }

    protected DBMakerT _newAppendFileDB(File file) {
        this.props.setProperty(Keys.file, file.getPath());
        this.props.setProperty(Keys.store, Keys.store_append);
        return getThis();
    }

    public static <K, V> BTreeMap<K, V> newTempTreeMap() {
        return newTempFileDB().deleteFilesAfterClose().closeOnJvmShutdown().transactionDisable().make().getTreeMap("temp");
    }

    public static <K, V> HTreeMap<K, V> newTempHashMap() {
        return newTempFileDB().deleteFilesAfterClose().closeOnJvmShutdown().transactionDisable().make().getHashMap("temp");
    }

    public static <K> NavigableSet<K> newTempTreeSet() {
        return newTempFileDB().deleteFilesAfterClose().closeOnJvmShutdown().transactionDisable().make().getTreeSet("temp");
    }

    public static <K> Set<K> newTempHashSet() {
        return newTempFileDB().deleteFilesAfterClose().closeOnJvmShutdown().transactionDisable().make().getHashSet("temp");
    }

    public static DBMaker newTempFileDB() {
        try {
            return newFileDB(File.createTempFile("mapdb-temp", "db"));
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public static <K, V> HTreeMap<K, V> newCacheDirect(double size) {
        return newMemoryDirectDB().transactionDisable().make().createHashMap(DbCacheEntity.TABLE_NAME).expireStoreSize(size).counterEnable().make();
    }

    public static <K, V> HTreeMap<K, V> newCache(double size) {
        return newMemoryDB().transactionDisable().make().createHashMap(DbCacheEntity.TABLE_NAME).expireStoreSize(size).counterEnable().make();
    }

    public static DBMaker newFileDB(File file) {
        return new DBMaker(file);
    }

    public DBMakerT _newFileDB(File file) {
        this.props.setProperty(Keys.file, file.getPath());
        return getThis();
    }

    protected DBMakerT getThis() {
        return this;
    }

    public DBMakerT transactionDisable() {
        this.props.put(Keys.transactionDisable, "true");
        return getThis();
    }

    public DBMakerT cacheDisable() {
        this.props.put(DbCacheEntity.TABLE_NAME, Keys.cache_disable);
        return getThis();
    }

    public DBMakerT cacheHardRefEnable() {
        this.props.put(DbCacheEntity.TABLE_NAME, Keys.cache_hardRef);
        return getThis();
    }

    public DBMakerT cacheWeakRefEnable() {
        this.props.put(DbCacheEntity.TABLE_NAME, Keys.cache_weakRef);
        return getThis();
    }

    public DBMakerT cacheSoftRefEnable() {
        this.props.put(DbCacheEntity.TABLE_NAME, Keys.cache_softRef);
        return getThis();
    }

    public DBMakerT cacheLRUEnable() {
        this.props.put(DbCacheEntity.TABLE_NAME, Keys.cache_lru);
        return getThis();
    }

    public DBMakerT mmapFileEnable() {
        assertNotInMemoryVolume();
        this.props.setProperty(Keys.volume, Keys.volume_mmapf);
        return getThis();
    }

    public DBMakerT mmapFileEnablePartial() {
        assertNotInMemoryVolume();
        this.props.setProperty(Keys.volume, Keys.volume_mmapfPartial);
        return getThis();
    }

    private void assertNotInMemoryVolume() {
        if (Keys.volume_byteBuffer.equals(this.props.getProperty(Keys.volume)) || Keys.volume_directByteBuffer.equals(this.props.getProperty(Keys.volume))) {
            throw new IllegalArgumentException("Can not enable mmap file for in-memory store");
        }
    }

    public DBMakerT mmapFileEnableIfSupported() {
        assertNotInMemoryVolume();
        this.props.setProperty(Keys.volume, Keys.volume_mmapfIfSupported);
        return getThis();
    }

    public DBMakerT cacheSize(int cacheSize) {
        this.props.setProperty(Keys.cacheSize, Table.STRING_DEFAULT_VALUE + cacheSize);
        return getThis();
    }

    public DBMakerT snapshotEnable() {
        this.props.setProperty(Keys.snapshots, "true");
        return getThis();
    }

    public DBMakerT asyncWriteEnable() {
        this.props.setProperty(Keys.asyncWrite, "true");
        return getThis();
    }

    public DBMakerT asyncWriteFlushDelay(int delay) {
        this.props.setProperty(Keys.asyncWriteFlushDelay, Table.STRING_DEFAULT_VALUE + delay);
        return getThis();
    }

    public DBMakerT asyncWriteQueueSize(int queueSize) {
        this.props.setProperty(Keys.asyncWriteQueueSize, Table.STRING_DEFAULT_VALUE + queueSize);
        return getThis();
    }

    public DBMakerT deleteFilesAfterClose() {
        this.props.setProperty(Keys.deleteFilesAfterClose, "true");
        return getThis();
    }

    public DBMakerT closeOnJvmShutdown() {
        this.props.setProperty(Keys.closeOnJvmShutdown, "true");
        return getThis();
    }

    public DBMakerT compressionEnable() {
        this.props.setProperty(Keys.compression, Keys.compression_lzf);
        return getThis();
    }

    public DBMakerT encryptionEnable(String password) {
        return encryptionEnable(password.getBytes(Charset.forName(UrlUtils.UTF8)));
    }

    public DBMakerT encryptionEnable(byte[] password) {
        this.props.setProperty(Keys.encryption, Keys.encryption_xtea);
        this.props.setProperty(Keys.encryptionKey, toHexa(password));
        return getThis();
    }

    public DBMakerT checksumEnable() {
        this.props.setProperty(Keys.checksum, "true");
        return getThis();
    }

    public DBMakerT strictDBGet() {
        this.props.setProperty(Keys.strictDBGet, "true");
        return getThis();
    }

    public DBMakerT readOnly() {
        this.props.setProperty(Keys.readOnly, "true");
        return getThis();
    }

    public DBMakerT freeSpaceReclaimQ(int q) {
        if (q < 0 || q > 10) {
            throw new IllegalArgumentException("wrong Q");
        }
        this.props.setProperty(Keys.freeSpaceReclaimQ, Table.STRING_DEFAULT_VALUE + q);
        return getThis();
    }

    public DBMakerT commitFileSyncDisable() {
        this.props.setProperty(Keys.commitFileSyncDisable, "true");
        return getThis();
    }

    public DBMakerT sizeLimit(double maxSize) {
        this.props.setProperty(Keys.sizeLimit, Table.STRING_DEFAULT_VALUE + ((long) (((maxSize * 1024.0d) * 1024.0d) * 1024.0d)));
        return getThis();
    }

    public DB make() {
        boolean strictGet = propsGetBool(Keys.strictDBGet);
        Engine engine = makeEngine();
        boolean dbCreated = false;
        try {
            DB db = new DB(engine, strictGet, false);
            dbCreated = true;
            return db;
        } finally {
            if (!dbCreated) {
                engine.close();
            }
        }
    }

    public TxMaker makeTxMaker() {
        this.props.setProperty(Keys.fullTx, "true");
        snapshotEnable();
        Engine e = makeEngine();
        new DB(e).commit();
        return new TxMaker(e, propsGetBool(Keys.strictDBGet), propsGetBool(Keys.snapshots));
    }

    public Engine makeEngine() {
        boolean readOnly = propsGetBool(Keys.readOnly);
        File file = this.props.containsKey(Keys.file) ? new File(this.props.getProperty(Keys.file)) : null;
        String volume = this.props.getProperty(Keys.volume);
        String store = this.props.getProperty(Keys.store);
        if (readOnly && file == null) {
            throw new UnsupportedOperationException("Can not open in-memory DB in read-only mode.");
        } else if (readOnly && !file.exists() && !Keys.store_append.equals(store)) {
            throw new UnsupportedOperationException("Can not open non-existing file in read-only mode.");
        } else if (propsGetLong(Keys.sizeLimit, 0) <= 0 || !Keys.store_append.equals(store)) {
            Engine engine;
            extendArgumentCheck();
            if (Keys.store_heap.equals(store)) {
                engine = extendHeapStore();
            } else if (!Keys.store_append.equals(store)) {
                Factory folFac = extendStoreVolumeFactory();
                if (propsGetBool(Keys.transactionDisable)) {
                    engine = extendStoreDirect(folFac);
                } else {
                    engine = extendStoreWAL(folFac);
                }
            } else if (Keys.volume_byteBuffer.equals(volume) || Keys.volume_directByteBuffer.equals(volume)) {
                throw new UnsupportedOperationException("Append Storage format is not supported with in-memory dbs");
            } else {
                engine = extendStoreAppend();
            }
            engine = extendWrapStore(engine);
            if (propsGetBool(Keys.asyncWrite) && !readOnly) {
                engine = extendAsyncWriteEngine(engine);
            }
            String cache = this.props.getProperty(DbCacheEntity.TABLE_NAME, Keys.cache_hashTable);
            if (!Keys.cache_disable.equals(cache)) {
                if (Keys.cache_hashTable.equals(cache)) {
                    engine = extendCacheHashTable(engine);
                } else if (Keys.cache_hardRef.equals(cache)) {
                    engine = extendCacheHardRef(engine);
                } else if (Keys.cache_weakRef.equals(cache)) {
                    engine = extendCacheWeakRef(engine);
                } else if (Keys.cache_softRef.equals(cache)) {
                    engine = extendCacheSoftRef(engine);
                } else if (Keys.cache_lru.equals(cache)) {
                    engine = extendCacheLRU(engine);
                } else {
                    throw new IllegalArgumentException("unknown cache type: " + cache);
                }
            }
            engine = extendWrapCache(engine);
            if (propsGetBool(Keys.snapshots)) {
                engine = extendSnapshotEngine(engine);
            }
            engine = extendWrapSnapshotEngine(engine);
            if (readOnly) {
                engine = new ReadOnlyEngine(engine);
            }
            if (propsGetBool(Keys.closeOnJvmShutdown)) {
                engine = new CloseOnJVMShutdown(engine);
            }
            try {
                Tuple2<Integer, byte[]> check = (Tuple2) engine.get(3, Serializer.BASIC);
                if (check == null || ((Integer) check.a).intValue() == Arrays.hashCode((byte[]) check.b)) {
                    if (check == null && !engine.isReadOnly()) {
                        byte[] b = new byte[TransportMediator.KEYCODE_MEDIA_PAUSE];
                        new Random().nextBytes(b);
                        engine.update(3, Fun.t2(Integer.valueOf(Arrays.hashCode(b)), b), Serializer.BASIC);
                        engine.commit();
                    }
                    return engine;
                }
                throw new RuntimeException("invalid checksum");
            } catch (Throwable e) {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Error while opening store. Make sure you have right password, compression or encryption is well configured.", e);
            }
        } else {
            throw new UnsupportedOperationException("Append-Only store does not support Size Limit");
        }
    }

    protected int propsGetInt(String key, int defValue) {
        String ret = this.props.getProperty(key);
        return ret == null ? defValue : Integer.valueOf(ret).intValue();
    }

    protected long propsGetLong(String key, long defValue) {
        String ret = this.props.getProperty(key);
        return ret == null ? defValue : Long.valueOf(ret).longValue();
    }

    protected boolean propsGetBool(String key) {
        String ret = this.props.getProperty(key);
        return ret != null && ret.equals("true");
    }

    protected byte[] propsGetXteaEncKey() {
        if (Keys.encryption_xtea.equals(this.props.getProperty(Keys.encryption))) {
            return fromHexa(this.props.getProperty(Keys.encryptionKey));
        }
        return null;
    }

    protected static boolean JVMSupportsLargeMappedFiles() {
        String prop = System.getProperty("os.arch");
        if (prop == null || !prop.contains("64")) {
            return false;
        }
        return true;
    }

    protected int propsGetRafMode() {
        String volume = this.props.getProperty(Keys.volume);
        if (volume == null || Keys.volume_raf.equals(volume)) {
            return 2;
        }
        if (Keys.volume_mmapfIfSupported.equals(volume)) {
            if (JVMSupportsLargeMappedFiles()) {
                return 0;
            }
            return 2;
        } else if (Keys.volume_mmapfPartial.equals(volume)) {
            return 1;
        } else {
            if (Keys.volume_mmapf.equals(volume)) {
                return 0;
            }
            return 2;
        }
    }

    protected Engine extendSnapshotEngine(Engine engine) {
        return new TxEngine(engine, propsGetBool(Keys.fullTx));
    }

    protected Engine extendCacheLRU(Engine engine) {
        return new LRU(engine, propsGetInt(Keys.cacheSize, LruDiskCache.DEFAULT_BUFFER_SIZE), false);
    }

    protected Engine extendCacheWeakRef(Engine engine) {
        return new WeakSoftRef(engine, true, false);
    }

    protected Engine extendCacheSoftRef(Engine engine) {
        return new WeakSoftRef(engine, false, false);
    }

    protected Engine extendCacheHardRef(Engine engine) {
        return new HardRef(engine, propsGetInt(Keys.cacheSize, LruDiskCache.DEFAULT_BUFFER_SIZE), false);
    }

    protected Engine extendCacheHashTable(Engine engine) {
        return new HashTable(engine, propsGetInt(Keys.cacheSize, LruDiskCache.DEFAULT_BUFFER_SIZE), false);
    }

    protected Engine extendAsyncWriteEngine(Engine engine) {
        return new AsyncWriteEngine(engine, propsGetInt(Keys.asyncWriteFlushDelay, 100), propsGetInt(Keys.asyncWriteQueueSize, CC.ASYNC_WRITE_QUEUE_SIZE), null);
    }

    protected void extendArgumentCheck() {
    }

    protected Engine extendWrapStore(Engine engine) {
        return engine;
    }

    protected Engine extendWrapCache(Engine engine) {
        return engine;
    }

    protected Engine extendWrapSnapshotEngine(Engine engine) {
        return engine;
    }

    protected Engine extendHeapStore() {
        return new StoreHeap();
    }

    protected Engine extendStoreAppend() {
        boolean z;
        File file = this.props.containsKey(Keys.file) ? new File(this.props.getProperty(Keys.file)) : null;
        boolean compressionEnabled = Keys.compression_lzf.equals(this.props.getProperty(Keys.compression));
        if (propsGetRafMode() > 0) {
            z = true;
        } else {
            z = false;
        }
        return new StoreAppend(file, z, propsGetBool(Keys.readOnly), propsGetBool(Keys.transactionDisable), propsGetBool(Keys.deleteFilesAfterClose), propsGetBool(Keys.commitFileSyncDisable), propsGetBool(Keys.checksum), compressionEnabled, propsGetXteaEncKey(), false);
    }

    protected Engine extendStoreDirect(Factory folFac) {
        return new StoreDirect(folFac, propsGetBool(Keys.readOnly), propsGetBool(Keys.deleteFilesAfterClose), propsGetInt(Keys.freeSpaceReclaimQ, 5), propsGetBool(Keys.commitFileSyncDisable), propsGetLong(Keys.sizeLimit, 0), propsGetBool(Keys.checksum), Keys.compression_lzf.equals(this.props.getProperty(Keys.compression)), propsGetXteaEncKey(), false, 0);
    }

    protected Engine extendStoreWAL(Factory folFac) {
        return new StoreWAL(folFac, propsGetBool(Keys.readOnly), propsGetBool(Keys.deleteFilesAfterClose), propsGetInt(Keys.freeSpaceReclaimQ, 5), propsGetBool(Keys.commitFileSyncDisable), propsGetLong(Keys.sizeLimit, -1), propsGetBool(Keys.checksum), Keys.compression_lzf.equals(this.props.getProperty(Keys.compression)), propsGetXteaEncKey(), false, 0);
    }

    protected Factory extendStoreVolumeFactory() {
        long sizeLimit = propsGetLong(Keys.sizeLimit, 0);
        String volume = this.props.getProperty(Keys.volume);
        if (Keys.volume_byteBuffer.equals(volume)) {
            return Volume.memoryFactory(false, sizeLimit, 20);
        }
        if (Keys.volume_directByteBuffer.equals(volume)) {
            return Volume.memoryFactory(true, sizeLimit, 20);
        }
        File indexFile = new File(this.props.getProperty(Keys.file));
        return Volume.fileFactory(indexFile, propsGetRafMode(), propsGetBool(Keys.readOnly), sizeLimit, 20, 0, new File(indexFile.getPath() + StoreDirect.DATA_FILE_EXT), new File(indexFile.getPath() + StoreWAL.TRANS_LOG_FILE_EXT), propsGetBool(Keys.asyncWrite));
    }

    protected static String toHexa(byte[] bb) {
        char[] HEXA_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] ret = new char[(bb.length * 2)];
        for (int i = 0; i < bb.length; i++) {
            ret[i * 2] = HEXA_CHARS[(bb[i] & 240) >> 4];
            ret[(i * 2) + 1] = HEXA_CHARS[bb[i] & 15];
        }
        return new String(ret);
    }

    protected static byte[] fromHexa(String s) {
        byte[] ret = new byte[(s.length() / 2)];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (byte) Integer.parseInt(s.substring(i * 2, (i * 2) + 2), 16);
        }
        return ret;
    }
}
