package com.douban.book.reader.manager.cache;

import com.douban.book.reader.manager.exception.DataLoadException;
import java.util.Collection;
import java.util.List;

public class PinnableCache<T extends Identifiable> implements Cache<T>, Pinnable {
    private GeneralDbCache<T> mPinnedCache;
    private Cache<T> mWrappedCache;

    public PinnableCache(Cache<T> wrapped, Class<T> cls) {
        this.mWrappedCache = null;
        this.mPinnedCache = null;
        this.mWrappedCache = wrapped;
        this.mPinnedCache = new GeneralDbCache(cls);
    }

    public T get(Object id) throws DataLoadException {
        T entity = this.mWrappedCache.get(id);
        if (entity == null) {
            return this.mPinnedCache.get(id);
        }
        return entity;
    }

    public void add(T entity) throws DataLoadException {
        this.mWrappedCache.add(entity);
        if (this.mPinnedCache.has(entity.getId())) {
            this.mPinnedCache.add(entity);
        }
    }

    public void addAll(Collection<T> dataList) throws DataLoadException {
        for (T data : dataList) {
            add(data);
        }
    }

    public void sync(Collection<T> collection) throws DataLoadException {
        throw new UnsupportedOperationException();
    }

    public void delete(Object id) throws DataLoadException {
        this.mWrappedCache.delete(id);
        this.mPinnedCache.delete(id);
    }

    public void deleteAll() throws DataLoadException {
        this.mWrappedCache.deleteAll();
        this.mPinnedCache.deleteAll();
    }

    public List<T> getAll() throws DataLoadException {
        throw new UnsupportedOperationException();
    }

    public void pin(Object id) throws DataLoadException {
        T entity = get(id);
        if (entity != null) {
            this.mPinnedCache.add(entity);
        }
    }

    public void unpin(Object id) throws DataLoadException {
        this.mPinnedCache.delete(id);
    }
}
