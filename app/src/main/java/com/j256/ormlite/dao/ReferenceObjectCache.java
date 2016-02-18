package com.j256.ormlite.dao;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceObjectCache implements ObjectCache {
    private final ConcurrentHashMap<Class<?>, Map<Object, Reference<Object>>> classMaps;
    private final boolean useWeak;

    public ReferenceObjectCache(boolean useWeak) {
        this.classMaps = new ConcurrentHashMap();
        this.useWeak = useWeak;
    }

    public static ReferenceObjectCache makeWeakCache() {
        return new ReferenceObjectCache(true);
    }

    public static ReferenceObjectCache makeSoftCache() {
        return new ReferenceObjectCache(false);
    }

    public synchronized <T> void registerClass(Class<T> clazz) {
        if (((Map) this.classMaps.get(clazz)) == null) {
            this.classMaps.put(clazz, new ConcurrentHashMap());
        }
    }

    public <T, ID> T get(Class<T> clazz, ID id) {
        Map<Object, Reference<Object>> objectMap = getMapForClass(clazz);
        if (objectMap == null) {
            return null;
        }
        Reference<Object> ref = (Reference) objectMap.get(id);
        if (ref == null) {
            return null;
        }
        T obj = ref.get();
        if (obj != null) {
            return obj;
        }
        objectMap.remove(id);
        return null;
    }

    public <T, ID> void put(Class<T> clazz, ID id, T data) {
        Map<Object, Reference<Object>> objectMap = getMapForClass(clazz);
        if (objectMap == null) {
            return;
        }
        if (this.useWeak) {
            objectMap.put(id, new WeakReference(data));
        } else {
            objectMap.put(id, new SoftReference(data));
        }
    }

    public <T> void clear(Class<T> clazz) {
        Map<Object, Reference<Object>> objectMap = getMapForClass(clazz);
        if (objectMap != null) {
            objectMap.clear();
        }
    }

    public void clearAll() {
        for (Map<Object, Reference<Object>> objectMap : this.classMaps.values()) {
            objectMap.clear();
        }
    }

    public <T, ID> void remove(Class<T> clazz, ID id) {
        Map<Object, Reference<Object>> objectMap = getMapForClass(clazz);
        if (objectMap != null) {
            objectMap.remove(id);
        }
    }

    public <T, ID> T updateId(Class<T> clazz, ID oldId, ID newId) {
        Map<Object, Reference<Object>> objectMap = getMapForClass(clazz);
        if (objectMap == null) {
            return null;
        }
        Reference<Object> ref = (Reference) objectMap.remove(oldId);
        if (ref == null) {
            return null;
        }
        objectMap.put(newId, ref);
        return ref.get();
    }

    public <T> int size(Class<T> clazz) {
        Map<Object, Reference<Object>> objectMap = getMapForClass(clazz);
        if (objectMap == null) {
            return 0;
        }
        return objectMap.size();
    }

    public int sizeAll() {
        int size = 0;
        for (Map<Object, Reference<Object>> objectMap : this.classMaps.values()) {
            size += objectMap.size();
        }
        return size;
    }

    public <T> void cleanNullReferences(Class<T> clazz) {
        Map<Object, Reference<Object>> objectMap = getMapForClass(clazz);
        if (objectMap != null) {
            cleanMap(objectMap);
        }
    }

    public <T> void cleanNullReferencesAll() {
        for (Map<Object, Reference<Object>> objectMap : this.classMaps.values()) {
            cleanMap(objectMap);
        }
    }

    private void cleanMap(Map<Object, Reference<Object>> objectMap) {
        Iterator<Entry<Object, Reference<Object>>> iterator = objectMap.entrySet().iterator();
        while (iterator.hasNext()) {
            if (((Reference) ((Entry) iterator.next()).getValue()).get() == null) {
                iterator.remove();
            }
        }
    }

    private Map<Object, Reference<Object>> getMapForClass(Class<?> clazz) {
        Map<Object, Reference<Object>> objectMap = (Map) this.classMaps.get(clazz);
        if (objectMap == null) {
            return null;
        }
        return objectMap;
    }
}
