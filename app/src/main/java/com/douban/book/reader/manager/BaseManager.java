package com.douban.book.reader.manager;

import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.entity.DummyEntity;
import com.douban.book.reader.manager.cache.Cache;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.manager.cache.MemoryCache;
import com.douban.book.reader.manager.cache.Pinnable;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.manager.exception.NoSuchDataException;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseManager<T extends Identifiable> {
    private static final int MAX_STALENESS = 86400000;
    protected final String TAG;
    private Cache<T> mCache;
    private CharSequence mExcludes;
    private int mMaxStaleness;
    private RestClient<T> mRestClient;
    private Map<Object, Date> mStalenessMap;
    private Class<T> mType;

    /* renamed from: com.douban.book.reader.manager.BaseManager.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Object val$id;

        AnonymousClass1(Object obj) {
            this.val$id = obj;
        }

        public void run() {
            try {
                BaseManager.this.addToCache(this.val$id == null ? (Identifiable) BaseManager.this.mRestClient.getEntity() : (Identifiable) BaseManager.this.mRestClient.get(this.val$id));
            } catch (Exception e) {
                Logger.e(BaseManager.this.TAG, e, "Failed to get object %s from server.", this.val$id);
            }
        }
    }

    public BaseManager(Class<T> cls) {
        this.TAG = getClass().getSimpleName();
        this.mCache = null;
        this.mRestClient = null;
        this.mType = null;
        this.mExcludes = null;
        this.mStalenessMap = new HashMap();
        this.mMaxStaleness = MAX_STALENESS;
        this.mType = cls;
        this.mCache = new MemoryCache();
    }

    public BaseManager(String restPath, Class<T> cls) {
        this(cls);
        restPath(restPath);
    }

    public BaseManager<T> cache(Cache<T> cache) {
        this.mCache = cache;
        return this;
    }

    public BaseManager<T> restClient(RestClient<T> restClient) {
        this.mRestClient = restClient;
        updateRestClient();
        return this;
    }

    public BaseManager<T> restPath(String restPath) {
        this.mRestClient = createRestClient(restPath, this.mType);
        updateRestClient();
        return this;
    }

    public BaseManager<T> excludes(String... fields) {
        this.mExcludes = StringUtils.join((CharSequence) ",", Arrays.asList(fields));
        updateRestClient();
        return this;
    }

    protected RestClient<T> createRestClient(String restPath, Class<T> type) {
        return new RestClient(restPath, (Class) type);
    }

    private void updateRestClient() {
        if (this.mRestClient != null) {
            if (StringUtils.isNotEmpty(this.mExcludes)) {
                this.mRestClient.addHeader("X-Excludes", this.mExcludes.toString());
            }
        }
    }

    public BaseManager<T> maxStaleness(int milliseconds) {
        this.mMaxStaleness = milliseconds;
        return this;
    }

    public void clearCache() throws DataLoadException {
        this.mCache.deleteAll();
    }

    public void pin(Object id) throws DataLoadException {
        if (this.mCache instanceof Pinnable) {
            get(id);
            ((Pinnable) this.mCache).pin(id);
            return;
        }
        throw new UnsupportedOperationException("Operation available only when cache is Pinnable.");
    }

    public void unpin(Object id) throws DataLoadException {
        if (this.mCache instanceof Pinnable) {
            ((Pinnable) this.mCache).unpin(id);
            return;
        }
        throw new UnsupportedOperationException("Operation available only when cache is Pinnable.");
    }

    public Lister<T> list() {
        return defaultLister();
    }

    public T get() throws DataLoadException {
        try {
            T entity = getFromCache(Integer.valueOf(0));
            if (!isCacheStaled(entity)) {
                return entity;
            }
            asyncRefreshLocalCache(null);
            return entity;
        } catch (DataLoadException e) {
            return getFromRemote(null);
        }
    }

    public T get(Object id) throws DataLoadException {
        try {
            T entity = getFromCache(id);
            if (!isCacheStaled(entity)) {
                return entity;
            }
            asyncRefreshLocalCache(id);
            return entity;
        } catch (DataLoadException e) {
            return getFromRemote(id);
        }
    }

    public T get(RequestParam param) throws DataLoadException {
        try {
            Identifiable entity = (Identifiable) this.mRestClient.get(param);
            addToCache(entity);
            return entity;
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public T getFromRemote(Object id) throws DataLoadException {
        T entity;
        if (id == null) {
            try {
                entity = (Identifiable) this.mRestClient.getEntity();
            } catch (RestException e1) {
                throw wrapDataLoadException(e1);
            }
        }
        entity = (Identifiable) this.mRestClient.get(id);
        addToCache(entity);
        return entity;
    }

    public void add(T entity) throws DataLoadException {
        addToRemote(entity);
    }

    public T addToRemote(T entity) throws DataLoadException {
        try {
            Identifiable returned = (Identifiable) this.mRestClient.post((Object) entity);
            addToCache(returned);
            return returned;
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public T post(RequestParam<?> param) throws DataLoadException {
        try {
            Identifiable entity = (Identifiable) this.mRestClient.post((RequestParam) param);
            addToCache(entity);
            return entity;
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public T update(Object id, RequestParam<?> param) throws DataLoadException {
        try {
            this.mRestClient.put(id, param);
            return getFromRemote(id);
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public void delete(Object id) throws DataLoadException {
        try {
            this.mRestClient.delete(id);
            deleteFromCache(id);
        } catch (Exception e) {
            throw wrapDataLoadException(e);
        }
    }

    protected void deleteFromRemote(Object id) throws DataLoadException {
        deleteFromRemoteWithParam(id, null);
    }

    protected void deleteFromRemoteWithParam(Object id, RequestParam<?> param) throws DataLoadException {
        try {
            this.mRestClient.delete(id, param);
        } catch (Exception e) {
            throw wrapDataLoadException(e);
        }
    }

    protected void addToCache(T entity) throws DataLoadException {
        if (entity != null) {
            this.mCache.add(entity);
            this.mStalenessMap.put(entity.getId(), new Date());
        }
    }

    public T getFromCache(Object id) throws DataLoadException {
        T entity = this.mCache.get(id);
        if (entity != null) {
            return entity;
        }
        throw new NoSuchDataException();
    }

    protected List<T> getAllFromCache() throws DataLoadException {
        return this.mCache.getAll();
    }

    protected void deleteFromCache(Object id) throws DataLoadException {
        this.mCache.delete(id);
        this.mStalenessMap.remove(id);
    }

    protected void deleteFromCache(T object) throws DataLoadException {
        deleteFromCache(object.getId());
    }

    protected boolean isCacheStaled(T object) {
        if (object != null && DateUtils.millisElapsed((Date) this.mStalenessMap.get(object.getId())) < ((long) this.mMaxStaleness)) {
            return false;
        }
        return true;
    }

    protected Lister<T> defaultLister() {
        return new Lister(this.mRestClient, this.mType).cache(this.mCache).stalenessMap(this.mStalenessMap);
    }

    protected RestClient<T> getRestClient() {
        return this.mRestClient;
    }

    protected Class<T> getType() {
        return this.mType;
    }

    protected <SubType extends Identifiable> BaseManager<SubType> getSubManagerForId(Object id, String restPath, Class<SubType> cls) {
        return new BaseManager(cls).restClient(getRestClient().getSubClientWithId(id, restPath, cls));
    }

    protected BaseManager<DummyEntity> getSubManagerForId(Object id, String restPath) {
        return new BaseManager(DummyEntity.class).restClient(getRestClient().getSubClientWithId(id, restPath, DummyEntity.class));
    }

    protected <SubType extends Identifiable> BaseManager<SubType> getSubManager(String restPath, Class<SubType> cls) {
        return new BaseManager(cls).restClient(getRestClient().getSubClient(restPath, cls));
    }

    protected DataLoadException wrapDataLoadException(Throwable e) {
        if (e instanceof DataLoadException) {
            return (DataLoadException) e;
        }
        return new DataLoadException(e);
    }

    private void asyncRefreshLocalCache(Object id) {
        TaskController.run(new AnonymousClass1(id));
    }
}
