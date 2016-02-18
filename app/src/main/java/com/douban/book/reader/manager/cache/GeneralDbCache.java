package com.douban.book.reader.manager.cache;

import com.douban.book.reader.entity.DbCacheEntity;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.JsonUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeneralDbCache<T extends Identifiable> implements Cache<T> {
    private DbCache<DbCacheEntity> mDbCache;
    private Class<T> mType;

    public GeneralDbCache(Class<T> cls) {
        this.mDbCache = new DbCache(DbCacheEntity.class);
        this.mType = cls;
    }

    public T get(Object id) throws DataLoadException {
        DbCacheEntity entity = (DbCacheEntity) this.mDbCache.get(getDbId(id));
        if (entity == null) {
            return null;
        }
        return (Identifiable) JsonUtils.fromJson(entity.value, this.mType);
    }

    public void add(T entity) throws DataLoadException {
        DbCacheEntity cacheEntity = new DbCacheEntity();
        cacheEntity.id = getDbId(entity.getId());
        cacheEntity.value = JsonUtils.toJson(entity);
        this.mDbCache.add(cacheEntity);
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
        this.mDbCache.delete(getDbId(id));
    }

    public void deleteAll() throws DataLoadException {
        this.mDbCache.deleteAll();
    }

    public List<T> getAll() throws DataLoadException {
        List<DbCacheEntity> entities = this.mDbCache.getAll();
        List<T> results = new ArrayList();
        for (DbCacheEntity entity : entities) {
            results.add(JsonUtils.fromJson(entity.value, this.mType));
        }
        return results;
    }

    public boolean has(Object id) {
        return this.mDbCache.has(getDbId(id));
    }

    private String getDbId(Object id) {
        return String.format("%s_%s", new Object[]{this.mType.getSimpleName(), id});
    }
}
