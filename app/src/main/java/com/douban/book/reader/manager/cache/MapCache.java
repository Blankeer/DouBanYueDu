package com.douban.book.reader.manager.cache;

import com.douban.book.reader.manager.exception.DataLoadException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MapCache<T extends Identifiable> implements Cache<T> {
    private Map<Object, T> mMap;

    public MapCache(Map<Object, T> map) {
        this.mMap = map;
    }

    public T get(Object id) throws DataLoadException {
        return (Identifiable) this.mMap.get(id);
    }

    public void add(T entity) throws DataLoadException {
        this.mMap.put(entity.getId(), entity);
    }

    public void addAll(Collection<T> dataList) throws DataLoadException {
        for (T data : dataList) {
            add(data);
        }
    }

    public List<T> getAll() throws DataLoadException {
        throw new UnsupportedOperationException();
    }

    public void sync(Collection<T> collection) throws DataLoadException {
        throw new UnsupportedOperationException();
    }

    public void delete(Object id) throws DataLoadException {
        this.mMap.remove(id);
    }

    public void deleteAll() throws DataLoadException {
        this.mMap.clear();
    }
}
