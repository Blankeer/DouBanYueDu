package com.google.tagmanager;

import android.os.Build.VERSION;
import com.google.android.gms.common.util.VisibleForTesting;

class CacheFactory<K, V> {
    @VisibleForTesting
    final CacheSizeManager<K, V> mDefaultSizeManager;

    public interface CacheSizeManager<K, V> {
        int sizeOf(K k, V v);
    }

    public CacheFactory() {
        this.mDefaultSizeManager = new CacheSizeManager<K, V>() {
            public int sizeOf(K k, V v) {
                return 1;
            }
        };
    }

    public Cache<K, V> createCache(int maxSize) {
        return createCache(maxSize, this.mDefaultSizeManager);
    }

    public Cache<K, V> createCache(int maxSize, CacheSizeManager<K, V> sizeManager) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else if (getSdkVersion() < 12) {
            return new SimpleCache(maxSize, sizeManager);
        } else {
            return new LRUCache(maxSize, sizeManager);
        }
    }

    @VisibleForTesting
    int getSdkVersion() {
        return VERSION.SDK_INT;
    }
}
