package com.google.tagmanager;

import com.google.tagmanager.CacheFactory.CacheSizeManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

class SimpleCache<K, V> implements Cache<K, V> {
    private final Map<K, V> mHashMap;
    private final int mMaxSize;
    private final CacheSizeManager<K, V> mSizeManager;
    private int mTotalSize;

    SimpleCache(int maxSize, CacheSizeManager<K, V> sizeManager) {
        this.mHashMap = new HashMap();
        this.mMaxSize = maxSize;
        this.mSizeManager = sizeManager;
    }

    public synchronized void put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }
        this.mTotalSize += this.mSizeManager.sizeOf(key, value);
        if (this.mTotalSize > this.mMaxSize) {
            Iterator<Entry<K, V>> iter = this.mHashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<K, V> entry = (Entry) iter.next();
                this.mTotalSize -= this.mSizeManager.sizeOf(entry.getKey(), entry.getValue());
                iter.remove();
                if (this.mTotalSize <= this.mMaxSize) {
                    break;
                }
            }
        }
        this.mHashMap.put(key, value);
    }

    public synchronized V get(K key) {
        return this.mHashMap.get(key);
    }
}
