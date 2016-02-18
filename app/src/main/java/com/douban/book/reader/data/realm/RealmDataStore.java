package com.douban.book.reader.data.realm;

import com.douban.book.reader.util.FilePath;
import com.douban.book.reader.util.FileUtils;
import com.douban.book.reader.util.IOUtils;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RealmDataStore {
    private static final String STORE_NAME_PENDING_REQUESTS = "pending_requests.realm";
    private static final String TAG;
    private static final Map<String, RealmDataStore> sInstanceCache;
    private String mFileName;

    static {
        TAG = RealmDataStore.class.getSimpleName();
        sInstanceCache = new HashMap();
    }

    public static RealmDataStore ofRequest() {
        return get(STORE_NAME_PENDING_REQUESTS);
    }

    private static RealmDataStore get(String name) {
        RealmDataStore instance;
        synchronized (sInstanceCache) {
            instance = (RealmDataStore) sInstanceCache.get(name);
            if (instance == null) {
                instance = new RealmDataStore(name);
                sInstanceCache.put(name, instance);
            }
        }
        return instance;
    }

    private RealmDataStore(String name) {
        this.mFileName = name;
    }

    public <T extends RealmObject> void add(T object) throws IOException {
        Realm realm = null;
        try {
            realm = getRealm();
            realm.beginTransaction();
            realm.copyToRealm((RealmObject) object);
            realm.commitTransaction();
        } finally {
            IOUtils.closeSilently(realm);
        }
    }

    public <T extends RealmObject> void consumeEach(RealmDataFilter<T> filter, RealmDataConsumer<T> consumer) throws IOException, RealmDataException {
        Realm realm = null;
        try {
            realm = getRealm();
            RealmResults<T> results = filter.filter(realm);
            while (!results.isEmpty()) {
                consumer.consume(realm, results.get(0));
            }
        } finally {
            IOUtils.closeSilently(realm);
        }
    }

    public <T extends RealmObject> long countOf(Class<T> cls) throws IOException {
        Realm realm = null;
        try {
            realm = getRealm();
            long size = (long) realm.allObjects(cls).size();
            return size;
        } finally {
            IOUtils.closeSilently(realm);
        }
    }

    public Realm getRealm() throws IOException {
        return Realm.getInstance(FileUtils.ensureFolder(FilePath.realmRoot()), this.mFileName);
    }
}
