package com.douban.book.reader.network.client;

import android.net.Uri;
import android.os.NetworkOnMainThreadException;
import com.douban.book.reader.network.EntityIterator;
import com.douban.book.reader.network.Request;
import com.douban.book.reader.network.Request.Method;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.network.exception.RestParseException;
import com.douban.book.reader.network.param.QueryString;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.UriUtils;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;

public class RestClient<T> {
    protected final String TAG;
    private Uri mBaseUri;
    private HashMap<String, String> mHeaderMap;
    protected Class<T> mType;

    public class Lister {
        private QueryString mFilter;
        private int mLoadedCount;
        private Date mServerTime;
        private int mTotalCount;

        public Lister() {
            this.mTotalCount = -1;
            this.mLoadedCount = 0;
            this.mFilter = null;
            this.mServerTime = null;
        }

        public Lister filter(QueryString filter) {
            this.mFilter = filter;
            return this;
        }

        public Date getServerTimeOnFirstLoad() {
            return this.mServerTime;
        }

        public boolean hasMore() {
            return this.mTotalCount < 0 || this.mLoadedCount < this.mTotalCount;
        }

        public List<T> loadMore() throws RestException {
            List<T> result = list();
            this.mLoadedCount += result.size();
            return result;
        }

        public void reset() {
            this.mTotalCount = -1;
            this.mLoadedCount = 0;
        }

        private EntityIterator<T> iterate() throws RestException {
            Request request;
            HttpURLConnection conn;
            try {
                if (this.mFilter == null) {
                    this.mFilter = new QueryString();
                }
                this.mFilter.append("start", Integer.valueOf(this.mLoadedCount));
                request = new Request(Method.GET, RestClient.this.getUri(), this.mFilter);
                request.addHeaders(RestClient.this.mHeaderMap);
                conn = request.openConnection();
                this.mTotalCount = StringUtils.toInt(conn.getHeaderField("X-Count"));
                if (this.mServerTime == null) {
                    if (StringUtils.isNotEmpty(conn.getHeaderField("X-Server-Time"))) {
                        this.mServerTime = DateUtils.parseIso8601(conn.getHeaderField("X-Server-Time"));
                    }
                }
            } catch (ParseException e) {
                Logger.e(RestClient.this.TAG, e, "error parsing X-Server-Time: %s", serverTime);
            } catch (Throwable e2) {
                RestException access$100 = RestClient.this.wrapRestException(e2);
            }
            return new EntityIterator(conn, request.getResponseDataReader(), RestClient.this.mType);
        }

        public List<T> list() throws RestException {
            List<T> list = new ArrayList();
            EntityIterator<T> iterator = iterate();
            while (iterator.hasNext()) {
                try {
                    list.add(iterator.next());
                } catch (Throwable e) {
                    throw new RestParseException(e);
                } catch (Throwable th) {
                    iterator.closeSilently();
                }
            }
            iterator.closeSilently();
            return list;
        }
    }

    public RestClient(String relativeUri, Class<T> type) {
        this.TAG = RestClient.class.getSimpleName();
        this.mHeaderMap = null;
        this.mBaseUri = UriUtils.resolveRelativeUri(relativeUri);
        this.mType = type;
        Logger.d(this.TAG, "Created RestClient for %s", this.mBaseUri);
    }

    public RestClient(Uri uri, Class<T> type) {
        this.TAG = RestClient.class.getSimpleName();
        this.mHeaderMap = null;
        this.mBaseUri = uri;
        this.mType = type;
        Logger.d(this.TAG, "Created RestClient for %s", this.mBaseUri);
    }

    public void addHeader(String key, String value) {
        if (this.mHeaderMap == null) {
            this.mHeaderMap = new HashMap();
        }
        this.mHeaderMap.put(key, value);
    }

    public void addHeaders(Map<String, String> headers) {
        if (headers != null) {
            if (this.mHeaderMap == null) {
                this.mHeaderMap = new HashMap();
            }
            this.mHeaderMap.putAll(headers);
        }
    }

    public Map<String, String> getHeaders() {
        return this.mHeaderMap;
    }

    public JsonClient getSubClient(String subUri) {
        return new JsonClient(Uri.withAppendedPath(this.mBaseUri, subUri));
    }

    public <SubType> RestClient<SubType> getSubClient(String subUri, Class<SubType> subType) {
        return new RestClient(Uri.withAppendedPath(this.mBaseUri, subUri), (Class) subType);
    }

    public <SubType> RestClient<SubType> getSubClientWithId(Object id, String subUri, Class<SubType> subType) {
        return new RestClient(Uri.withAppendedPath(Uri.withAppendedPath(this.mBaseUri, String.valueOf(id)), subUri), (Class) subType);
    }

    public Lister lister() {
        return new Lister();
    }

    public T get(Object id) throws RestException {
        HttpURLConnection conn = null;
        try {
            Request request = new Request(Method.GET, getUriWithId(id));
            request.addHeaders(this.mHeaderMap);
            conn = request.openConnection();
            T parseResponse = parseResponse(request);
            if (conn != null) {
                conn.disconnect();
            }
            return parseResponse;
        } catch (Throwable th) {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public T get(RequestParam param) throws RestException {
        HttpURLConnection conn = null;
        try {
            Request request = new Request(Method.GET, getUri(), param);
            request.addHeaders(this.mHeaderMap);
            conn = request.openConnection();
            T parseResponse = parseResponse(request);
            if (conn != null) {
                conn.disconnect();
            }
            return parseResponse;
        } catch (Throwable th) {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public T getEntity() throws RestException {
        HttpURLConnection conn = null;
        try {
            Request request = new Request(Method.GET, getUri());
            request.addHeaders(this.mHeaderMap);
            conn = request.openConnection();
            T parseResponse = parseResponse(request);
            if (conn != null) {
                conn.disconnect();
            }
            return parseResponse;
        } catch (Throwable th) {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void put() throws RestException {
        put(null);
    }

    public void put(RequestParam<?> param) throws RestException {
        HttpURLConnection conn = null;
        try {
            Request request = new Request(Method.PUT, getUri(), param);
            request.addHeaders(this.mHeaderMap);
            conn = request.openConnection();
            if (conn != null) {
                conn.disconnect();
            }
        } catch (Throwable th) {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void put(Object id, RequestParam<?> param) throws RestException {
        HttpURLConnection conn = null;
        try {
            Request request = new Request(Method.PUT, getUriWithId(id), param);
            request.addHeaders(this.mHeaderMap);
            conn = request.openConnection();
            if (conn != null) {
                conn.disconnect();
            }
        } catch (Throwable th) {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public T post(T entity) throws RestException {
        try {
            return post(RequestParam.json(entity));
        } catch (JSONException e) {
            throw wrapRestException(e);
        }
    }

    public T post(RequestParam<?> param) throws RestException {
        HttpURLConnection conn = null;
        try {
            Request request = new Request(Method.POST, getUri(), param);
            request.addHeaders(this.mHeaderMap);
            conn = request.openConnection();
            T parseResponse = parseResponse(request);
            if (conn != null) {
                conn.disconnect();
            }
            return parseResponse;
        } catch (Throwable th) {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void deleteEntity() throws RestException {
        deleteEntity(null);
    }

    public void deleteEntity(RequestParam<?> param) throws RestException {
        doDelete(getUri(), param);
    }

    public void delete(Object id) throws RestException {
        delete(id, null);
    }

    public void delete(Object id, RequestParam<?> param) throws RestException {
        doDelete(getUriWithId(id), param);
    }

    private void doDelete(Uri uri, RequestParam<?> param) throws RestException {
        HttpURLConnection conn = null;
        try {
            Request request = new Request(Method.DELETE, uri, param);
            request.addHeaders(this.mHeaderMap);
            conn = request.openConnection();
            conn.getResponseCode();
            if (conn != null) {
                conn.disconnect();
            }
        } catch (Throwable th) {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    protected T parseResponse(Request request) throws IOException, JSONException {
        return request.parseResponseAs(this.mType);
    }

    public String toString() {
        return String.format("RestClient: %s", new Object[]{this.mBaseUri});
    }

    public Uri getUri() {
        return this.mBaseUri;
    }

    protected Uri getUriWithId(Object id) {
        if (id != null) {
            return Uri.withAppendedPath(this.mBaseUri, String.valueOf(id));
        }
        throw new IllegalArgumentException("id cannot be null");
    }

    private RestException wrapRestException(Throwable e) {
        if (e instanceof RestException) {
            return (RestException) e;
        }
        if (e instanceof JsonParseException) {
            return new RestParseException(toString(), e);
        }
        if (!(e instanceof NetworkOnMainThreadException) || !AppInfo.isDebug()) {
            return new RestException(toString(), e);
        }
        throw ((NetworkOnMainThreadException) e);
    }
}
