package com.alipay.android.phone.mrpc.core;

import android.net.SSLCertificateSocketFactory;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Base64;
import android.util.Log;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.network.ConnectionUtils;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.sina.weibo.sdk.exception.WeiboAuthException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.Security;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

public final class i implements HttpClient {
    public static long a;
    private static String[] c;
    private static final HttpRequestInterceptor d;
    final HttpClient b;
    private RuntimeException e;
    private volatile b f;

    private class a implements HttpRequestInterceptor {
        final /* synthetic */ i a;

        private a(i iVar) {
            this.a = iVar;
        }

        public final void process(HttpRequest httpRequest, HttpContext httpContext) {
            b a = this.a.f;
            if (a != null && Log.isLoggable(a.a, a.b) && (httpRequest instanceof HttpUriRequest)) {
                Log.println(a.b, a.a, i.a((HttpUriRequest) httpRequest));
            }
        }
    }

    private static class b {
        final String a;
        final int b;
    }

    static {
        a = 160;
        c = new String[]{"text/", "application/xml", io.fabric.sdk.android.services.network.HttpRequest.CONTENT_TYPE_JSON};
        d = new j();
    }

    private i(ClientConnectionManager clientConnectionManager, HttpParams httpParams) {
        this.e = new IllegalStateException("AndroidHttpClient created and never closed");
        this.b = new k(this, clientConnectionManager, httpParams);
    }

    public static i a(String str) {
        HttpParams basicHttpParams = new BasicHttpParams();
        HttpProtocolParams.setVersion(basicHttpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUseExpectContinue(basicHttpParams, false);
        HttpConnectionParams.setStaleCheckingEnabled(basicHttpParams, false);
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, BaseImageDownloader.DEFAULT_HTTP_READ_TIMEOUT);
        HttpConnectionParams.setSoTimeout(basicHttpParams, ConnectionUtils.CONNECTION_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(basicHttpParams, AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD);
        HttpClientParams.setRedirecting(basicHttpParams, true);
        HttpClientParams.setAuthenticating(basicHttpParams, false);
        HttpProtocolParams.setUserAgent(basicHttpParams, str);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme(Constants.API_SCHEME, SSLCertificateSocketFactory.getHttpSocketFactory(ConnectionUtils.CONNECTION_TIMEOUT, null), 443));
        ClientConnectionManager threadSafeClientConnManager = new ThreadSafeClientConnManager(basicHttpParams, schemeRegistry);
        ConnManagerParams.setTimeout(basicHttpParams, 60000);
        ConnManagerParams.setMaxConnectionsPerRoute(basicHttpParams, new ConnPerRouteBean(10));
        ConnManagerParams.setMaxTotalConnections(basicHttpParams, 50);
        Security.setProperty("networkaddress.cache.ttl", WeiboAuthException.DEFAULT_AUTH_ERROR_CODE);
        HttpsURLConnection.setDefaultHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
        return new i(threadSafeClientConnManager, basicHttpParams);
    }

    public static InputStream a(HttpEntity httpEntity) {
        InputStream content = httpEntity.getContent();
        if (content == null) {
            return content;
        }
        Header contentEncoding = httpEntity.getContentEncoding();
        if (contentEncoding == null) {
            return content;
        }
        String value = contentEncoding.getValue();
        if (value == null) {
            return content;
        }
        return value.contains(io.fabric.sdk.android.services.network.HttpRequest.ENCODING_GZIP) ? new GZIPInputStream(content) : content;
    }

    static /* synthetic */ String a(HttpUriRequest httpUriRequest) {
        Object uri;
        HttpEntity entity;
        OutputStream byteArrayOutputStream;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("curl ");
        for (Header header : httpUriRequest.getAllHeaders()) {
            if (!(header.getName().equals(io.fabric.sdk.android.services.network.HttpRequest.HEADER_AUTHORIZATION) || header.getName().equals("Cookie"))) {
                stringBuilder.append("--header \"");
                stringBuilder.append(header.toString().trim());
                stringBuilder.append("\" ");
            }
        }
        URI uri2 = httpUriRequest.getURI();
        if (httpUriRequest instanceof RequestWrapper) {
            HttpRequest original = ((RequestWrapper) httpUriRequest).getOriginal();
            if (original instanceof HttpUriRequest) {
                uri = ((HttpUriRequest) original).getURI();
                stringBuilder.append("\"");
                stringBuilder.append(uri);
                stringBuilder.append("\"");
                if (httpUriRequest instanceof HttpEntityEnclosingRequest) {
                    entity = ((HttpEntityEnclosingRequest) httpUriRequest).getEntity();
                    if (entity != null && entity.isRepeatable()) {
                        if (entity.getContentLength() >= PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
                            byteArrayOutputStream = new ByteArrayOutputStream();
                            entity.writeTo(byteArrayOutputStream);
                            if (b(httpUriRequest)) {
                                stringBuilder.append(" --data-ascii \"").append(byteArrayOutputStream.toString()).append("\"");
                            } else {
                                stringBuilder.insert(0, "echo '" + Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2) + "' | base64 -d > /tmp/$$.bin; ");
                                stringBuilder.append(" --data-binary @/tmp/$$.bin");
                            }
                        } else {
                            stringBuilder.append(" [TOO MUCH DATA TO INCLUDE]");
                        }
                    }
                }
                return stringBuilder.toString();
            }
        }
        URI uri3 = uri2;
        stringBuilder.append("\"");
        stringBuilder.append(uri);
        stringBuilder.append("\"");
        if (httpUriRequest instanceof HttpEntityEnclosingRequest) {
            entity = ((HttpEntityEnclosingRequest) httpUriRequest).getEntity();
            if (entity.getContentLength() >= PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
                stringBuilder.append(" [TOO MUCH DATA TO INCLUDE]");
            } else {
                byteArrayOutputStream = new ByteArrayOutputStream();
                entity.writeTo(byteArrayOutputStream);
                if (b(httpUriRequest)) {
                    stringBuilder.append(" --data-ascii \"").append(byteArrayOutputStream.toString()).append("\"");
                } else {
                    stringBuilder.insert(0, "echo '" + Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2) + "' | base64 -d > /tmp/$$.bin; ");
                    stringBuilder.append(" --data-binary @/tmp/$$.bin");
                }
            }
        }
        return stringBuilder.toString();
    }

    public static AbstractHttpEntity a(byte[] bArr) {
        if (((long) bArr.length) < a) {
            return new ByteArrayEntity(bArr);
        }
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gZIPOutputStream.write(bArr);
        gZIPOutputStream.close();
        AbstractHttpEntity byteArrayEntity = new ByteArrayEntity(byteArrayOutputStream.toByteArray());
        byteArrayEntity.setContentEncoding(io.fabric.sdk.android.services.network.HttpRequest.ENCODING_GZIP);
        new StringBuilder("gzip size:").append(bArr.length).append("->").append(byteArrayEntity.getContentLength());
        return byteArrayEntity;
    }

    public static void a(HttpRequest httpRequest) {
        httpRequest.addHeader(io.fabric.sdk.android.services.network.HttpRequest.HEADER_ACCEPT_ENCODING, io.fabric.sdk.android.services.network.HttpRequest.ENCODING_GZIP);
    }

    private void a(HttpRequestRetryHandler httpRequestRetryHandler) {
        ((DefaultHttpClient) this.b).setHttpRequestRetryHandler(httpRequestRetryHandler);
    }

    public static long b(String str) {
        return r.a(str);
    }

    public static void b(HttpRequest httpRequest) {
        httpRequest.addHeader("Connection", "Keep-Alive");
    }

    private static boolean b(HttpUriRequest httpUriRequest) {
        Header[] headers = httpUriRequest.getHeaders("content-encoding");
        if (headers != null) {
            for (Header value : headers) {
                if (io.fabric.sdk.android.services.network.HttpRequest.ENCODING_GZIP.equalsIgnoreCase(value.getValue())) {
                    return true;
                }
            }
        }
        Header[] headers2 = httpUriRequest.getHeaders("content-type");
        if (headers2 == null) {
            return true;
        }
        for (Header header : headers2) {
            for (String startsWith : c) {
                if (header.getValue().startsWith(startsWith)) {
                    return false;
                }
            }
        }
        return true;
    }

    public final <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler) {
        return this.b.execute(httpHost, httpRequest, responseHandler);
    }

    public final <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) {
        return this.b.execute(httpHost, httpRequest, responseHandler, httpContext);
    }

    public final <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler) {
        return this.b.execute(httpUriRequest, responseHandler);
    }

    public final <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) {
        return this.b.execute(httpUriRequest, responseHandler, httpContext);
    }

    public final HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) {
        return this.b.execute(httpHost, httpRequest);
    }

    public final HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
        return this.b.execute(httpHost, httpRequest, httpContext);
    }

    public final HttpResponse execute(HttpUriRequest httpUriRequest) {
        return this.b.execute(httpUriRequest);
    }

    public final HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) {
        return this.b.execute(httpUriRequest, httpContext);
    }

    public final ClientConnectionManager getConnectionManager() {
        return this.b.getConnectionManager();
    }

    public final HttpParams getParams() {
        return this.b.getParams();
    }
}
