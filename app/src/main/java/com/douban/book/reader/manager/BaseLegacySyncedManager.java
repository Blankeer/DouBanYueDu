package com.douban.book.reader.manager;

import com.douban.book.reader.job.JobUtils;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.manager.sync.RestDeleteJob;
import com.douban.book.reader.manager.sync.RestPostJob;
import java.util.Date;

public class BaseLegacySyncedManager<T extends Identifiable> extends BaseManager<T> {
    public BaseLegacySyncedManager(Class<T> cls) {
        super(cls);
    }

    public BaseLegacySyncedManager(String restPath, Class<T> cls) {
        super(restPath, cls);
    }

    public Date refresh(DataFilter filter) throws DataLoadException {
        Lister<T> lister = defaultLister().filter(filter);
        while (lister.hasMore()) {
            lister.loadAll();
        }
        return lister.getServerTime();
    }

    public T get(Object id) throws DataLoadException {
        return getFromCache(id);
    }

    public void add(T entity) throws DataLoadException {
        addToCache(entity);
        asyncAddToRemote(entity);
    }

    protected void asyncAddToRemote(T entity) {
        JobUtils.addJob(new RestPostJob(getUriStr(), getType(), entity));
    }

    public void delete(Object id) throws DataLoadException {
        deleteFromCache(id);
        asyncDeleteFromRemote(id, new Date());
    }

    protected void asyncDeleteFromRemote(Object id, Date updateTime) {
        JobUtils.addJob(new RestDeleteJob(getUriStr(), getType(), id, updateTime));
    }

    private String getUriStr() {
        return getRestClient().getUri().toString();
    }
}
