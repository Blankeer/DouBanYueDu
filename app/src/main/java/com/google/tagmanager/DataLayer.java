package com.google.tagmanager;

import com.google.android.gms.common.util.VisibleForTesting;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataLayer {
    static final String LIFETIME_KEY = "gtm.lifetime";
    static final String[] LIFETIME_KEY_COMPONENTS;
    private static final Pattern LIFETIME_PATTERN;
    static final int MAX_QUEUE_DEPTH = 500;
    public static final Object OBJECT_NOT_PRESENT;
    private final ConcurrentHashMap<Listener, Integer> mListeners;
    private final Map<Object, Object> mModel;
    private final PersistentStore mPersistentStore;
    private final CountDownLatch mPersistentStoreLoaded;
    private final ReentrantLock mPushLock;
    private final LinkedList<Map<Object, Object>> mUpdateQueue;

    static final class KeyValue {
        public final String mKey;
        public final Object mValue;

        KeyValue(String key, Object value) {
            this.mKey = key;
            this.mValue = value;
        }

        public String toString() {
            return "Key: " + this.mKey + " value: " + this.mValue.toString();
        }

        public int hashCode() {
            return Arrays.hashCode(new Integer[]{Integer.valueOf(this.mKey.hashCode()), Integer.valueOf(this.mValue.hashCode())});
        }

        public boolean equals(Object o) {
            if (!(o instanceof KeyValue)) {
                return false;
            }
            KeyValue other = (KeyValue) o;
            if (this.mKey.equals(other.mKey) && this.mValue.equals(other.mValue)) {
                return true;
            }
            return false;
        }
    }

    interface Listener {
        void changed(Map<Object, Object> map);
    }

    interface PersistentStore {

        public interface Callback {
            void onKeyValuesLoaded(List<KeyValue> list);
        }

        void clearKeysWithPrefix(String str);

        void loadSaved(Callback callback);

        void saveKeyValues(List<KeyValue> list, long j);
    }

    static {
        OBJECT_NOT_PRESENT = new Object();
        LIFETIME_KEY_COMPONENTS = LIFETIME_KEY.toString().split("\\.");
        LIFETIME_PATTERN = Pattern.compile("(\\d+)\\s*([smhd]?)");
    }

    @VisibleForTesting
    DataLayer() {
        this(new PersistentStore() {
            public void saveKeyValues(List<KeyValue> list, long lifetimeInMillis) {
            }

            public void loadSaved(Callback callback) {
                callback.onKeyValuesLoaded(new ArrayList());
            }

            public void clearKeysWithPrefix(String keyPrefix) {
            }
        });
    }

    DataLayer(PersistentStore persistentStore) {
        this.mPersistentStore = persistentStore;
        this.mListeners = new ConcurrentHashMap();
        this.mModel = new HashMap();
        this.mPushLock = new ReentrantLock();
        this.mUpdateQueue = new LinkedList();
        this.mPersistentStoreLoaded = new CountDownLatch(1);
        loadSavedMaps();
    }

    public void push(Object key, Object value) {
        push(expandKeyValue(key, value));
    }

    public void push(Map<Object, Object> update) {
        try {
            this.mPersistentStoreLoaded.await();
        } catch (InterruptedException e) {
            Log.w("DataLayer.push: unexpected InterruptedException");
        }
        pushWithoutWaitingForSaved(update);
    }

    private void pushWithoutWaitingForSaved(Map<Object, Object> update) {
        this.mPushLock.lock();
        try {
            this.mUpdateQueue.offer(update);
            if (this.mPushLock.getHoldCount() == 1) {
                processQueuedUpdates();
            }
            savePersistentlyIfNeeded(update);
        } finally {
            this.mPushLock.unlock();
        }
    }

    private void loadSavedMaps() {
        this.mPersistentStore.loadSaved(new Callback() {
            public void onKeyValuesLoaded(List<KeyValue> keyValues) {
                for (KeyValue keyValue : keyValues) {
                    DataLayer.this.pushWithoutWaitingForSaved(DataLayer.this.expandKeyValue(keyValue.mKey, keyValue.mValue));
                }
                DataLayer.this.mPersistentStoreLoaded.countDown();
            }
        });
    }

    private void savePersistentlyIfNeeded(Map<Object, Object> update) {
        Long lifetime = getLifetimeValue(update);
        if (lifetime != null) {
            List<KeyValue> flattenedMap = flattenMap(update);
            flattenedMap.remove(LIFETIME_KEY);
            this.mPersistentStore.saveKeyValues(flattenedMap, lifetime.longValue());
        }
    }

    private Long getLifetimeValue(Map<Object, Object> update) {
        Object lifetimeObject = getLifetimeObject(update);
        if (lifetimeObject == null) {
            return null;
        }
        return parseLifetime(lifetimeObject.toString());
    }

    private Object getLifetimeObject(Map<Object, Object> update) {
        Map<Object, Object> current = update;
        for (String component : LIFETIME_KEY_COMPONENTS) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = current.get(component);
        }
        return current;
    }

    void clearPersistentKeysWithPrefix(String prefix) {
        push(prefix, null);
        this.mPersistentStore.clearKeysWithPrefix(prefix);
    }

    private List<KeyValue> flattenMap(Map<Object, Object> map) {
        List<KeyValue> result = new ArrayList();
        flattenMapHelper(map, Table.STRING_DEFAULT_VALUE, result);
        return result;
    }

    private void flattenMapHelper(Map<Object, Object> map, String keyPrefix, Collection<KeyValue> accum) {
        for (Entry<Object, Object> entry : map.entrySet()) {
            String fullKey = keyPrefix + (keyPrefix.length() == 0 ? Table.STRING_DEFAULT_VALUE : ".") + entry.getKey();
            if (entry.getValue() instanceof Map) {
                flattenMapHelper((Map) entry.getValue(), fullKey, accum);
            } else if (!fullKey.equals(LIFETIME_KEY)) {
                accum.add(new KeyValue(fullKey, entry.getValue()));
            }
        }
    }

    @VisibleForTesting
    static Long parseLifetime(String lifetimeString) {
        Matcher m = LIFETIME_PATTERN.matcher(lifetimeString);
        if (m.matches()) {
            long number = 0;
            try {
                number = Long.parseLong(m.group(1));
            } catch (NumberFormatException e) {
                Log.w("illegal number in _lifetime value: " + lifetimeString);
            }
            if (number <= 0) {
                Log.i("non-positive _lifetime: " + lifetimeString);
                return null;
            }
            String unitString = m.group(2);
            if (unitString.length() == 0) {
                return Long.valueOf(number);
            }
            switch (unitString.charAt(0)) {
                case LruDiskCache.DEFAULT_COMPRESS_QUALITY /*100*/:
                    return Long.valueOf((((number * 1000) * 60) * 60) * 24);
                case Header.DOUBLE_1 /*104*/:
                    return Long.valueOf(((number * 1000) * 60) * 60);
                case Header.ARRAY_BYTE /*109*/:
                    return Long.valueOf((number * 1000) * 60);
                case Header.ARRAY_DOUBLE /*115*/:
                    return Long.valueOf(number * 1000);
                default:
                    Log.w("unknown units in _lifetime: " + lifetimeString);
                    return null;
            }
        }
        Log.i("unknown _lifetime: " + lifetimeString);
        return null;
    }

    private void processQueuedUpdates() {
        int numUpdatesProcessed = 0;
        do {
            Map<Object, Object> update = (Map) this.mUpdateQueue.poll();
            if (update != null) {
                processUpdate(update);
                numUpdatesProcessed++;
            } else {
                return;
            }
        } while (numUpdatesProcessed <= MAX_QUEUE_DEPTH);
        this.mUpdateQueue.clear();
        throw new RuntimeException("Seems like an infinite loop of pushing to the data layer");
    }

    private void processUpdate(Map<Object, Object> update) {
        synchronized (this.mModel) {
            for (Object key : update.keySet()) {
                mergeMap(expandKeyValue(key, update.get(key)), this.mModel);
            }
        }
        notifyListeners(update);
    }

    public Object get(String key) {
        synchronized (this.mModel) {
            Object target = this.mModel;
            String[] arr$ = key.split("\\.");
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                String s = arr$[i$];
                if (target instanceof Map) {
                    Object value = ((Map) target).get(s);
                    if (value == null) {
                        return null;
                    }
                    target = value;
                    i$++;
                } else {
                    return null;
                }
            }
            return target;
        }
    }

    public static Map<Object, Object> mapOf(Object... objects) {
        if (objects.length % 2 != 0) {
            throw new IllegalArgumentException("expected even number of key-value pairs");
        }
        Map<Object, Object> map = new HashMap();
        for (int i = 0; i < objects.length; i += 2) {
            map.put(objects[i], objects[i + 1]);
        }
        return map;
    }

    public static List<Object> listOf(Object... objects) {
        List<Object> list = new ArrayList();
        for (Object add : objects) {
            list.add(add);
        }
        return list;
    }

    void registerListener(Listener listener) {
        this.mListeners.put(listener, Integer.valueOf(0));
    }

    void unregisterListener(Listener listener) {
        this.mListeners.remove(listener);
    }

    private void notifyListeners(Map<Object, Object> update) {
        for (Listener listener : this.mListeners.keySet()) {
            listener.changed(update);
        }
    }

    Map<Object, Object> expandKeyValue(Object key, Object value) {
        Map<Object, Object> result = new HashMap();
        Map<Object, Object> target = result;
        String[] split = key.toString().split("\\.");
        for (int i = 0; i < split.length - 1; i++) {
            HashMap<Object, Object> map = new HashMap();
            target.put(split[i], map);
            Object target2 = map;
        }
        target.put(split[split.length - 1], value);
        return result;
    }

    @VisibleForTesting
    void mergeMap(Map<Object, Object> from, Map<Object, Object> to) {
        for (Object key : from.keySet()) {
            List<Object> fromValue = from.get(key);
            if (fromValue instanceof List) {
                if (!(to.get(key) instanceof List)) {
                    to.put(key, new ArrayList());
                }
                mergeList(fromValue, (List) to.get(key));
            } else if (fromValue instanceof Map) {
                if (!(to.get(key) instanceof Map)) {
                    to.put(key, new HashMap());
                }
                mergeMap((Map) fromValue, (Map) to.get(key));
            } else {
                to.put(key, fromValue);
            }
        }
    }

    @VisibleForTesting
    void mergeList(List<Object> from, List<Object> to) {
        while (to.size() < from.size()) {
            to.add(null);
        }
        for (int index = 0; index < from.size(); index++) {
            List<Object> fromValue = from.get(index);
            if (fromValue instanceof List) {
                if (!(to.get(index) instanceof List)) {
                    to.set(index, new ArrayList());
                }
                mergeList(fromValue, (List) to.get(index));
            } else if (fromValue instanceof Map) {
                if (!(to.get(index) instanceof Map)) {
                    to.set(index, new HashMap());
                }
                mergeMap((Map) fromValue, (Map) to.get(index));
            } else if (fromValue != OBJECT_NOT_PRESENT) {
                to.set(index, fromValue);
            }
        }
    }
}
