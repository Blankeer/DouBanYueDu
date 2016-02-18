package com.google.tagmanager;

import android.annotation.TargetApi;
import android.util.LruCache;
import com.google.tagmanager.CacheFactory.CacheSizeManager;

@TargetApi(12)
class LRUCache<K, V> implements Cache<K, V> {
    private LruCache<K, V> lruCache;

    /* renamed from: com.google.tagmanager.LRUCache.1 */
    class AnonymousClass1 extends LruCache<K, V> {
        final /* synthetic */ CacheSizeManager val$sizeManager;

        AnonymousClass1(int x0, CacheSizeManager cacheSizeManager) {
            this.val$sizeManager = cacheSizeManager;
            super(x0);
        }

        protected int sizeOf(K key, V value) {
            return this.val$sizeManager.sizeOf(key, value);
        }
    }

    LRUCache(int maxSize, CacheSizeManager<K, V> sizeManager) {
        this.lruCache = new AnonymousClass1(maxSize, sizeManager);
    }

    public void put(K key, V value) {
        this.lruCache.put(key, value);
    }

    public V get(K key) {
        return this.lruCache.get(key);
    }
}
