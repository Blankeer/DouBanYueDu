package com.douban.book.reader.manager;

import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.client.SyncedRestClient;
import com.douban.book.reader.network.exception.NetworkRequestPostponedException;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import java.util.Date;

public class BaseSyncedManager<T extends Identifiable> extends BaseManager<T> {

    /* renamed from: com.douban.book.reader.manager.BaseSyncedManager.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Identifiable val$entity;

        AnonymousClass1(Identifiable identifiable) {
            this.val$entity = identifiable;
        }

        public void run() {
            try {
                BaseSyncedManager.this.addToRemote(this.val$entity);
            } catch (DataLoadException e) {
                Logger.e(BaseSyncedManager.this.TAG, e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.manager.BaseSyncedManager.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Object val$id;
        final /* synthetic */ RequestParam val$param;

        AnonymousClass2(Object obj, RequestParam requestParam) {
            this.val$id = obj;
            this.val$param = requestParam;
        }

        public void run() {
            try {
                BaseSyncedManager.this.getRestClient().put(this.val$id, this.val$param);
            } catch (RestException e) {
                Logger.e(BaseSyncedManager.this.TAG, e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.manager.BaseSyncedManager.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ Object val$id;
        final /* synthetic */ RequestParam val$param;

        AnonymousClass3(Object obj, RequestParam requestParam) {
            this.val$id = obj;
            this.val$param = requestParam;
        }

        public void run() {
            try {
                BaseSyncedManager.this.deleteFromRemoteWithParam(this.val$id, this.val$param);
            } catch (DataLoadException e) {
                Logger.e(BaseSyncedManager.this.TAG, e);
            }
        }
    }

    public BaseSyncedManager(Class<T> cls) {
        super(cls);
    }

    public BaseSyncedManager(String restPath, Class<T> cls) {
        super(restPath, cls);
    }

    protected RestClient<T> createRestClient(String restPath, Class<T> type) {
        return new SyncedRestClient(restPath, (Class) type);
    }

    public BaseManager<T> restClient(RestClient<T> restClient) {
        if (restClient instanceof SyncedRestClient) {
            return super.restClient(restClient);
        }
        throw new IllegalArgumentException("restClient in BaseSyncedManager must be SyncedRestClient.");
    }

    protected SyncedRestClient<T> getRestClient() {
        return (SyncedRestClient) super.getRestClient();
    }

    public Date refresh(DataFilter filter) throws DataLoadException {
        try {
            getRestClient().executePendingRequestsForThisType();
            Lister<T> lister = defaultLister().filter(filter);
            while (lister.hasMore()) {
                lister.loadAll();
            }
            return lister.getServerTime();
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public void add(T entity) throws DataLoadException {
        try {
            super.add(entity);
        } catch (DataLoadException e) {
            if (ExceptionUtils.isCausedBy(e, NetworkRequestPostponedException.class)) {
                addToCache(entity);
            }
            throw e;
        }
    }

    protected void add(T entity, RequestParam<?> onlineOnlyParam) throws DataLoadException {
        try {
            addToCache(getRestClient().post(entity, onlineOnlyParam));
        } catch (Throwable e) {
            throw new DataLoadException(e);
        } catch (Throwable th) {
            addToCache(entity);
        }
    }

    public void asyncAdd(T entity) throws DataLoadException {
        addToCache(entity);
        asyncAddToRemote(entity);
    }

    protected void asyncAddToRemote(T entity) {
        TaskController.run(new AnonymousClass1(entity));
    }

    public T onlineUpdate(Object id, RequestParam<?> param) throws DataLoadException {
        try {
            getRestClient().putOnline(id, param);
            return getFromRemote(id);
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    @Deprecated
    protected void asyncUpdateRemote(Object id, RequestParam<?> param) throws DataLoadException {
        TaskController.run(new AnonymousClass2(id, param));
    }

    public void delete(Object id) throws DataLoadException {
        try {
            super.delete(id);
        } catch (DataLoadException e) {
            if (ExceptionUtils.isCausedBy(e, NetworkRequestPostponedException.class)) {
                deleteFromCache(id);
            }
            throw e;
        }
    }

    public void asyncDelete(Object id) throws DataLoadException {
        asyncDeleteWithParam(id, null);
    }

    public void asyncDeleteWithParam(Object id, RequestParam<?> param) throws DataLoadException {
        deleteFromCache(id);
        asyncDeleteFromRemoteWithParam(id, param);
    }

    protected void asyncDeleteFromRemoteWithParam(Object id, RequestParam<?> param) {
        TaskController.run(new AnonymousClass3(id, param));
    }
}
