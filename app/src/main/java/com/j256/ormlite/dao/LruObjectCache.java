package com.j256.ormlite.dao;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class LruObjectCache implements ObjectCache {
    private final int capacity;
    private final ConcurrentHashMap<Class<?>, Map<Object, Object>> classMaps;

    private static class LimitedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = -4566528080395573236L;
        private final int capacity;

        public LimitedLinkedHashMap(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        protected boolean removeEldestEntry(Entry<K, V> entry) {
            return size() > this.capacity;
        }
    }

    public LruObjectCache(int capacity) {
        this.classMaps = new ConcurrentHashMap();
        this.capacity = capacity;
    }

    public synchronized <T> void registerClass(Class<T> clazz) {
        if (((Map) this.classMaps.get(clazz)) == null) {
            this.classMaps.put(clazz, Collections.synchronizedMap(new LimitedLinkedHashMap(this.capacity)));
        }
    }

    public <T, ID> T get(Class<T> clazz, ID id) {
        Map<Object, Object> objectMap = getMapForClass(clazz);
        if (objectMap == null) {
            return null;
        }
        return objectMap.get(id);
    }

    public <T, ID> void put(Class<T> clazz, ID id, T data) {
        Map<Object, Object> objectMap = getMapForClass(clazz);
        if (objectMap != null) {
            objectMap.put(id, data);
        }
    }

    public <T> void clear(Class<T> clazz) {
        Map<Object, Object> objectMap = getMapForClass(clazz);
        if (objectMap != null) {
            objectMap.clear();
        }
    }

    public void clearAll() {
        for (Map<Object, Object> objectMap : this.classMaps.values()) {
            objectMap.clear();
        }
    }

    public <T, ID> void remove(Class<T> clazz, ID id) {
        Map<Object, Object> objectMap = getMapForClass(clazz);
        if (objectMap != null) {
            objectMap.remove(id);
        }
    }

    public <T, ID> T updateId(Class<T> clazz, ID oldId, ID newId) {
        Map<Object, Object> objectMap = getMapForClass(clazz);
        if (objectMap == null) {
            return null;
        }
        T obj = objectMap.remove(oldId);
        if (obj == null) {
            return null;
        }
        objectMap.put(newId, obj);
        return obj;
    }

    public <T> int size(Class<T> clazz) {
        Map<Object, Object> objectMap = getMapForClass(clazz);
        if (objectMap == null) {
            return 0;
        }
        return objectMap.size();
    }

    public int sizeAll() {
        int size = 0;
        for (Map<Object, Object> objectMap : this.classMaps.values()) {
            size += objectMap.size();
        }
        return size;
    }

    private Map<Object, Object> getMapForClass(Class<?> clazz) {
        Map<Object, Object> objectMap = (Map) this.classMaps.get(clazz);
        if (objectMap == null) {
            return null;
        }
        return objectMap;
    }
}
