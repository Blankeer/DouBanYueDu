package com.douban.book.reader.manager.cache;

import android.util.LruCache;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.JsonUtils;
import com.douban.book.reader.util.Pref;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PrefCache<T extends Identifiable> implements Cache<T> {
    private LruCache<Object, T> mLruCache;
    private Pref mPref;
    private Class<T> mType;

    public PrefCache(String prefName, Class<T> cls) {
        this(Pref.ofName(prefName), (Class) cls);
    }

    public PrefCache(Pref pref, Class<T> cls) {
        this.mLruCache = new LruCache(10);
        this.mPref = pref;
        this.mType = cls;
    }

    public T get(Object id) throws DataLoadException {
        Identifiable data = (Identifiable) this.mLruCache.get(id);
        return data != null ? data : (Identifiable) JsonUtils.fromJson(this.mPref.getString(getPrefKey(id)), this.mType);
    }

    public void add(T entity) throws DataLoadException {
        Object id = entity.getId();
        this.mPref.set(getPrefKey(id), JsonUtils.toJson(entity));
        this.mLruCache.put(id, entity);
    }

    public void addAll(Collection<T> dataList) throws DataLoadException {
        for (T data : dataList) {
            add(data);
        }
    }

    public void delete(Object id) throws DataLoadException {
        this.mPref.remove(getPrefKey(id));
        this.mLruCache.remove(id);
    }

    public void deleteAll() throws DataLoadException {
        this.mLruCache.evictAll();
        this.mPref.clear();
    }

    public List<T> getAll() throws DataLoadException {
        Map<String, ?> map = this.mPref.getPref().getAll();
        List<T> list = new ArrayList();
        if (map != null) {
            for (Entry<String, ?> entry : map.entrySet()) {
                list.add(JsonUtils.fromJson((String) entry.getValue(), this.mType));
            }
        }
        return list;
    }

    public void sync(Collection<T> dataList) throws DataLoadException {
        this.mLruCache.evictAll();
        this.mPref.clear();
        addAll(dataList);
    }

    private String getPrefKey(Object id) {
        return String.format("cls%s_%s", new Object[]{this.mType.getName(), id});
    }
}
