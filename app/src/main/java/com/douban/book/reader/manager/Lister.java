package com.douban.book.reader.manager;

import com.douban.book.reader.manager.cache.Cache;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.manager.exception.NoSuchDataException;
import com.douban.book.reader.network.client.RestClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Lister<T extends Identifiable> {
    private static final String TAG;
    private Cache<T> mCache;
    private DataFilter mDataFilter;
    private RestClient<T> mRestClient;
    private com.douban.book.reader.network.client.RestClient.Lister mRestLister;
    private Map<Object, Date> mStalenessMap;
    private Class<T> mType;

    static {
        TAG = Lister.class.getSimpleName();
    }

    public Lister(RestClient<T> restClient, Class<T> cls) {
        this.mCache = null;
        this.mRestClient = null;
        this.mRestLister = null;
        this.mDataFilter = null;
        this.mType = null;
        this.mStalenessMap = null;
        this.mRestClient = restClient;
        this.mRestLister = this.mRestClient.lister();
        this.mType = cls;
    }

    public Lister<T> restPath(String restPath) {
        Map<String, String> headers = null;
        if (this.mRestClient != null) {
            headers = this.mRestClient.getHeaders();
        }
        if (restPath.startsWith("/") || this.mRestClient == null) {
            this.mRestClient = new RestClient(restPath, this.mType);
        } else {
            this.mRestClient = this.mRestClient.getSubClient(restPath, this.mType);
        }
        this.mRestClient.addHeaders(headers);
        this.mRestLister = this.mRestClient.lister();
        return this;
    }

    public Lister<T> restPathWithId(Object id, String restPath) {
        return restPath(String.format("%s/%s", new Object[]{id, restPath}));
    }

    public Lister<T> cache(Cache<T> cache) {
        this.mCache = cache;
        return this;
    }

    @Deprecated
    public Lister<T> stalenessMap(Map<Object, Date> map) {
        this.mStalenessMap = map;
        return this;
    }

    public Lister<T> filter(DataFilter filter) {
        this.mDataFilter = filter;
        if (this.mDataFilter != null) {
            this.mRestLister.filter(this.mDataFilter.toQueryString());
        }
        return this;
    }

    public Date getServerTime() {
        return this.mRestLister.getServerTimeOnFirstLoad();
    }

    public boolean hasMore() {
        return this.mRestLister.hasMore();
    }

    public List<T> loadMore() throws DataLoadException {
        try {
            List<T> list = this.mRestLister.loadMore();
            if (this.mCache != null) {
                this.mCache.addAll(list);
            }
            if (this.mStalenessMap != null) {
                for (T entity : list) {
                    this.mStalenessMap.put(entity.getId(), new Date());
                }
            }
            return list;
        } catch (Exception e) {
            throw new DataLoadException(this.mRestClient.toString(), e);
        }
    }

    public void reset() {
        this.mRestLister.reset();
    }

    public List<T> loadAll() throws DataLoadException {
        List<T> allList = new ArrayList();
        while (hasMore()) {
            allList.addAll(loadMore());
        }
        return allList;
    }

    public List<T> sync() throws DataLoadException {
        List<T> allList = new ArrayList();
        while (hasMore()) {
            try {
                allList.addAll(this.mRestLister.loadMore());
            } catch (Exception e) {
                throw new DataLoadException(this.mRestClient.toString(), e);
            }
        }
        this.mCache.sync(allList);
        return allList;
    }

    public T getFirstItem() throws DataLoadException {
        try {
            List<T> items = loadMore();
            if (items.size() >= 1) {
                return (Identifiable) items.get(0);
            }
            throw new NoSuchDataException(String.format("No data for %s", new Object[]{this.mRestLister}));
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }
}
