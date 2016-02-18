package com.douban.book.reader.data;

import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.util.FileUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Utils;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.mapdb.Bind.MapListener;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

public class DataStore {
    private static final String TAG;
    private DB mDb;
    private File mFile;

    static {
        TAG = DataStore.class.getSimpleName();
    }

    public static DataStore ofWorks(int worksId) {
        return Book.get(worksId).getDataStore();
    }

    public DataStore(File file) {
        this.mFile = file;
        try {
            FileUtils.createParentDirIfNeeded(file);
        } catch (IOException e) {
            Logger.e(TAG, e);
        }
        open();
    }

    public synchronized <Key, Value> Map<Key, Value> getTreeMap(String name) {
        Map<Key, Value> treeMap;
        RuntimeException runtimeException;
        if (this.mDb != null) {
            try {
                treeMap = this.mDb.getTreeMap(name);
            } catch (Throwable e1) {
                runtimeException = new RuntimeException(String.format("Failed to get %s from %s (even after reopening db).", new Object[]{name, this}), e1);
            }
        } else {
            throw new RuntimeException(String.format("Failed to get %s from %s. mDb==null", new Object[]{name, this}));
        }
        return treeMap;
    }

    public void commit() {
        if (this.mDb != null) {
            this.mDb.commit();
        }
    }

    public void rollback() {
        if (this.mDb != null) {
            this.mDb.rollback();
        }
    }

    public synchronized void close() {
        if (this.mDb != null) {
            try {
                this.mDb.close();
            } catch (Throwable th) {
            }
            Logger.dc(TAG, "closed %s", this);
            this.mDb = null;
        }
    }

    private synchronized void open() {
        if (this.mDb != null) {
            close();
        }
        this.mDb = DBMaker.newFileDB(this.mFile).compressionEnable().asyncWriteEnable().closeOnJvmShutdown().encryptionEnable(Constants.MAGIC).mmapFileEnableIfSupported().make();
        Logger.dc(TAG, "opened %s", this);
    }

    public synchronized void openIfNeeded() {
        Logger.d(TAG, "openIfNeeded. mDb=%s", this.mDb);
        if (this.mDb == null) {
            open();
        }
    }

    public synchronized void setMapMeta(String mapName, Object metaData) {
        Map<String, Object> validMap = getMetaMap();
        if (validMap != null) {
            validMap.put(mapName, metaData);
        }
    }

    public synchronized void clearMapMeta(String mapName) {
        Map<String, Object> validMap = getMetaMap();
        if (validMap != null) {
            validMap.remove(mapName);
        }
    }

    public synchronized Object getMapMeta(String mapName) {
        Object obj;
        Map<String, Object> validMap = getMetaMap();
        if (validMap != null) {
            obj = validMap.get(mapName);
        } else {
            obj = null;
        }
        return obj;
    }

    private Map<String, Object> getMetaMap() {
        return getTreeMap("meta_map");
    }

    public <T> HTreeMap<Object, T> getCacheHashMap(String name, Class<T> cls) {
        HTreeMap<Object, T> map = this.mDb.createHashMap(name).expireMaxSize(1000).keySerializer(Serializer.BASIC).valueSerializer(new JsonSerializer(cls)).expireAfterWrite(604800000).expireAfterAccess(604800000).makeOrGet();
        map.modificationListenerAdd(new MapListener<Object, T>() {
            public void update(Object key, Object oldVal, Object newVal) {
                DataStore.this.mDb.commit();
            }
        });
        return map;
    }

    public String toString() {
        return String.format("%s file=%s mDb=%s", new Object[]{Utils.formatObject(this), FileUtils.formatExternalRelativePath(this.mFile), this.mDb});
    }
}
