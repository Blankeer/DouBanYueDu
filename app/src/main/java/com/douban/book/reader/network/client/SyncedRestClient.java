package com.douban.book.reader.network.client;

import android.net.Uri;
import com.alipay.sdk.util.h;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.douban.book.reader.data.realm.RealmDataConsumer;
import com.douban.book.reader.data.realm.RealmDataException;
import com.douban.book.reader.data.realm.RealmDataFilter;
import com.douban.book.reader.data.realm.RealmDataStore;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.manager.sync.PendingRequest;
import com.douban.book.reader.network.Request;
import com.douban.book.reader.network.Request.Method;
import com.douban.book.reader.network.exception.NetworkRequestPostponedException;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.network.param.RequestParam.Type;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.UriUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import java.net.HttpURLConnection;
import org.json.JSONException;

public class SyncedRestClient<T extends Identifiable> extends RestClient<T> {
    private static RealmDataConsumer<PendingRequest> mConsumer;
    private String mResourceType;

    /* renamed from: com.douban.book.reader.network.client.SyncedRestClient.1 */
    class AnonymousClass1 implements RealmDataFilter<PendingRequest> {
        final /* synthetic */ Object val$id;

        AnonymousClass1(Object obj) {
            this.val$id = obj;
        }

        public RealmResults<PendingRequest> filter(Realm realm) {
            return realm.where(PendingRequest.class).equalTo("resourceType", SyncedRestClient.this.getResourceType()).equalTo("resourceId", SyncedRestClient.this.formatResourceId(this.val$id)).findAllSorted("createTime");
        }
    }

    public SyncedRestClient(String relativeUri, Class<T> type) {
        super(relativeUri, (Class) type);
    }

    public SyncedRestClient(Uri uri, Class<T> type) {
        super(uri, (Class) type);
    }

    public T postOnline(T entity) throws RestException {
        executePendingRequestsForId(entity.getId());
        return (Identifiable) super.post((Object) entity);
    }

    public T post(T entity) throws RestException {
        try {
            return postOnline(entity);
        } catch (Throwable e) {
            postLater(entity);
            throw new NetworkRequestPostponedException(e);
        }
    }

    public T post(T entity, RequestParam<?> onlineOnlyParam) throws RestException {
        Throwable e;
        executePendingRequestsForId(entity.getId());
        try {
            return (Identifiable) super.post((JsonRequestParam) RequestParam.json(entity).append(onlineOnlyParam));
        } catch (RestException e2) {
            e = e2;
            postLater(entity);
            throw new NetworkRequestPostponedException(e);
        } catch (JSONException e3) {
            e = e3;
            postLater(entity);
            throw new NetworkRequestPostponedException(e);
        }
    }

    private void postLater(T entity) throws RestException {
        try {
            appendRequest(Method.POST, getUri(), entity.getId(), RequestParam.json(entity));
        } catch (Throwable e) {
            throw new RestException(e);
        }
    }

    public void put(Object id, RequestParam<?> param) throws RestException {
        try {
            putOnline(id, param);
        } catch (Throwable e) {
            putLater(id, param);
            throw new NetworkRequestPostponedException(e);
        }
    }

    public void putOnline(Object id, RequestParam<?> param) throws RestException {
        executePendingRequestsForId(id);
        super.put(id, param);
    }

    private void putLater(Object id, RequestParam<?> param) throws RestException {
        appendRequest(Method.PUT, getUriWithId(id), id, param);
    }

    public void delete(Object id) throws RestException {
        try {
            executePendingRequestsForId(id);
            super.delete(id);
        } catch (Throwable e) {
            deleteLater(id);
            throw new NetworkRequestPostponedException(e);
        }
    }

    public void delete(Object id, RequestParam<?> param) throws RestException {
        try {
            executePendingRequestsForId(id);
            super.delete(id, param);
        } catch (Throwable e) {
            deleteLater(id);
            throw new NetworkRequestPostponedException(e);
        }
    }

    private void deleteLater(Object id) throws RestException {
        appendRequest(Method.DELETE, getUriWithId(id), id, null);
    }

    private void appendRequest(Method method, Uri uri, Object id, RequestParam<?> data) throws RestException {
        String resourceType = getResourceType();
        String resourceId = formatResourceId(id);
        PendingRequest request = new PendingRequest();
        request.setMethod(String.valueOf(method));
        request.setUri(UriUtils.figureRelativeUri(uri));
        request.setResourceType(resourceType);
        request.setResourceId(resourceId);
        request.setCreateTime(System.currentTimeMillis());
        if (data != null) {
            request.setRequestParam(String.valueOf(data));
            request.setRequestParamType(String.valueOf(data.getType()));
        }
        try {
            RealmDataStore.ofRequest().add(request);
            Logger.d(Tag.NETWORK, "%nPENDED >>> %s %s%nPENDED >>> resource=%s:%s%nPENDED >>> %s", method, uri, resourceType, resourceId, data);
        } catch (Throwable e) {
            RestException restException = new RestException(e);
        }
    }

    private void executePendingRequestsForId(Object id) throws RestException {
        performRequestList(new AnonymousClass1(id));
    }

    public void executePendingRequestsForThisType() throws RestException {
        performRequestList(new RealmDataFilter<PendingRequest>() {
            public RealmResults<PendingRequest> filter(Realm realm) {
                return realm.where(PendingRequest.class).equalTo("resourceType", SyncedRestClient.this.getResourceType()).findAllSorted("createTime");
            }
        });
    }

    public static void executePendingRequestsForAll() throws RestException {
        performRequestList(new RealmDataFilter<PendingRequest>() {
            public RealmResults<PendingRequest> filter(Realm realm) {
                return realm.where(PendingRequest.class).findAllSorted("createTime");
            }
        });
    }

    public static boolean hasPendingRequests() {
        try {
            return RealmDataStore.ofRequest().countOf(PendingRequest.class) > 0;
        } catch (Throwable th) {
            return false;
        }
    }

    static {
        mConsumer = new RealmDataConsumer<PendingRequest>() {
            public void consume(Realm realm, PendingRequest pendingRequest) throws RealmDataException {
                int responseCode;
                try {
                    SyncedRestClient.performRequest(pendingRequest);
                } catch (Throwable e2) {
                    Logger.ec(Tag.NETWORK, e2, "Failed while trying to fix annotation param", new Object[0]);
                    Answers.getInstance().logCustom((CustomEvent) new CustomEvent("IllegalAnnotationOffset").putCustomAttribute("fixOffsetAndRetry", h.b));
                }
                realm.beginTransaction();
                pendingRequest.removeFromRealm();
                realm.commitTransaction();
            }
        };
    }

    private static void performRequestList(RealmDataFilter<PendingRequest> filter) throws RestException {
        try {
            RealmDataStore.ofRequest().consumeEach(filter, mConsumer);
        } catch (Throwable e) {
            Logger.ec(Tag.NETWORK, e);
            RestException restException = new RestException(e);
        }
    }

    private static int performRequest(PendingRequest pendingRequest) throws RestException {
        Method method = Method.valueOf(pendingRequest.getMethod());
        Type paramType = null;
        if (StringUtils.isNotEmpty(pendingRequest.getRequestParam())) {
            paramType = Type.valueOf(pendingRequest.getRequestParamType());
        }
        Uri uri = UriUtils.resolveRelativeUri(pendingRequest.getUri());
        String resourceType = pendingRequest.getResourceType();
        String resourceId = pendingRequest.getResourceId();
        String logHeader = String.format("RETRY (%d) >>>", new Object[]{Integer.valueOf(pendingRequest.getRetryCount())});
        Logger.d(Tag.NETWORK, "%n%s %s %s%n%s resource=%s:%s%n%s %s", logHeader, method, uri, logHeader, resourceType, resourceId, logHeader, param);
        HttpURLConnection conn = null;
        try {
            conn = new Request(method, uri, param, paramType).openConnection();
            int responseCode = conn.getResponseCode();
            if (conn != null) {
                conn.disconnect();
            }
            return responseCode;
        } catch (Throwable e) {
            throw new RestException(e);
        } catch (Throwable th) {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String getResourceType() {
        if (StringUtils.isEmpty(this.mResourceType)) {
            this.mResourceType = UriUtils.figureRelativeUri(getUri());
        }
        return this.mResourceType;
    }

    private String formatResourceId(Object id) {
        return String.valueOf(id);
    }
}
