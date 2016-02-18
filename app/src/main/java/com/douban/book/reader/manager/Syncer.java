package com.douban.book.reader.manager;

import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.util.Logger;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;

public class Syncer<T> {
    private static final String TAG;
    private Dao<T, Object> mDao;
    private RestClient<T> mRestClient;

    /* renamed from: com.douban.book.reader.manager.Syncer.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Object val$id;

        AnonymousClass1(Object obj) {
            this.val$id = obj;
        }

        public void run() {
            try {
                Syncer.this.get(this.val$id);
            } catch (Exception e) {
                Logger.e(Syncer.TAG, e, "Failed to get object %s from server.", this.val$id);
            }
        }
    }

    /* renamed from: com.douban.book.reader.manager.Syncer.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Object val$entity;

        AnonymousClass2(Object obj) {
            this.val$entity = obj;
        }

        public void run() {
            try {
                Syncer.this.add(this.val$entity);
            } catch (Exception e) {
                Logger.e(Syncer.TAG, e, "Failed to add object %s to server.", this.val$entity);
            }
        }
    }

    /* renamed from: com.douban.book.reader.manager.Syncer.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ Object val$id;

        AnonymousClass3(Object obj) {
            this.val$id = obj;
        }

        public void run() {
            try {
                Syncer.this.delete(this.val$id);
            } catch (Exception e) {
                Logger.e(Syncer.TAG, e, "Failed to delete object %s from server.", this.val$id);
            }
        }
    }

    static {
        TAG = Syncer.class.getSimpleName();
    }

    public Syncer(Dao<T, Object> dao, RestClient<T> restClient, Class<T> cls) {
        this.mDao = null;
        this.mRestClient = null;
        this.mDao = dao;
        this.mRestClient = restClient;
    }

    public T get(Object id) throws RestException, SQLException {
        Object entity = this.mRestClient.get(id);
        this.mDao.update(entity);
        return entity;
    }

    public void asyncGet(Object id) {
        TaskController.run(new AnonymousClass1(id));
    }

    public void add(T entity) throws RestException, SQLException {
        this.mDao.update(this.mRestClient.post((Object) entity));
        Logger.v(TAG, "Synced to cloud: %s", entity);
    }

    public void asyncAdd(T entity) {
        TaskController.run(new AnonymousClass2(entity));
    }

    public void delete(Object id) throws RestException {
        this.mRestClient.delete(id);
        Logger.v(TAG, "Deleted from cloud: %s", id);
    }

    public void asyncDelete(Object id) {
        TaskController.run(new AnonymousClass3(id));
    }
}
