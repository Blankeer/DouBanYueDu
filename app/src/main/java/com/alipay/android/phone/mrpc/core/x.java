package com.alipay.android.phone.mrpc.core;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import com.igexin.download.Downloads;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Callable;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public final class x implements Callable<ab> {
    private static final HttpRequestRetryHandler e;
    protected s a;
    protected Context b;
    protected v c;
    String d;
    private HttpUriRequest f;
    private HttpContext g;
    private CookieStore h;
    private CookieManager i;
    private AbstractHttpEntity j;
    private HttpHost k;
    private URL l;
    private int m;
    private boolean n;
    private boolean o;
    private String p;
    private String q;

    static {
        e = new h();
    }

    public x(s sVar, v vVar) {
        this.g = new BasicHttpContext();
        this.h = new BasicCookieStore();
        this.m = 0;
        this.n = false;
        this.o = false;
        this.p = null;
        this.a = sVar;
        this.b = this.a.a;
        this.c = vVar;
    }

    private static long a(String[] strArr) {
        int i = 0;
        while (i < strArr.length) {
            if ("max-age".equalsIgnoreCase(strArr[i]) && strArr[i + 1] != null) {
                try {
                    return Long.parseLong(strArr[i + 1]);
                } catch (Exception e) {
                }
            }
            i++;
        }
        return 0;
    }

    private ab a(HttpResponse httpResponse, int i, String str) {
        Throwable th;
        ByteArrayOutputStream byteArrayOutputStream;
        String str2 = null;
        new StringBuilder("\u5f00\u59cbhandle\uff0chandleResponse-1,").append(Thread.currentThread().getId());
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null && httpResponse.getStatusLine().getStatusCode() == Downloads.STATUS_SUCCESS) {
            new StringBuilder("200\uff0c\u5f00\u59cb\u5904\u7406\uff0chandleResponse-2,threadid = ").append(Thread.currentThread().getId());
            try {
                OutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                try {
                    String str3;
                    long currentTimeMillis = System.currentTimeMillis();
                    a(entity, byteArrayOutputStream2);
                    byte[] toByteArray = byteArrayOutputStream2.toByteArray();
                    this.o = false;
                    s sVar = this.a;
                    sVar.e = (System.currentTimeMillis() - currentTimeMillis) + sVar.e;
                    sVar = this.a;
                    sVar.c = ((long) toByteArray.length) + sVar.c;
                    new StringBuilder("res:").append(toByteArray.length);
                    ab wVar = new w(a(httpResponse), i, str, toByteArray);
                    currentTimeMillis = b(httpResponse);
                    Header contentType = httpResponse.getEntity().getContentType();
                    if (contentType != null) {
                        HashMap a = a(contentType.getValue());
                        str2 = (String) a.get(HttpRequest.PARAM_CHARSET);
                        str3 = (String) a.get(HttpRequest.HEADER_CONTENT_TYPE);
                    } else {
                        str3 = null;
                    }
                    wVar.a(str3);
                    wVar.c = str2;
                    wVar.a = System.currentTimeMillis();
                    wVar.b = currentTimeMillis;
                    try {
                        byteArrayOutputStream2.close();
                        return wVar;
                    } catch (IOException e) {
                        throw new RuntimeException("ArrayOutputStream close error!", e.getCause());
                    }
                } catch (Throwable th2) {
                    th = th2;
                    OutputStream outputStream = byteArrayOutputStream2;
                    if (byteArrayOutputStream != null) {
                        try {
                            byteArrayOutputStream.close();
                        } catch (IOException e2) {
                            throw new RuntimeException("ArrayOutputStream close error!", e2.getCause());
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                Throwable th4 = th3;
                byteArrayOutputStream = null;
                th = th4;
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                throw th;
            }
        } else if (entity != null) {
            return null;
        } else {
            httpResponse.getStatusLine().getStatusCode();
            return null;
        }
    }

    private static b a(HttpResponse httpResponse) {
        b bVar = new b();
        for (Header header : httpResponse.getAllHeaders()) {
            bVar.a.put(header.getName(), header.getValue());
        }
        return bVar;
    }

    private static HashMap<String, String> a(String str) {
        HashMap<String, String> hashMap = new HashMap();
        for (String str2 : str.split(";")) {
            String[] split = str2.indexOf(61) == -1 ? new String[]{HttpRequest.HEADER_CONTENT_TYPE, str2} : str2.split(SimpleComparison.EQUAL_TO_OPERATION);
            hashMap.put(split[0], split[1]);
        }
        return hashMap;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(org.apache.http.HttpEntity r7, java.io.OutputStream r8) {
        /*
        r6 = this;
        r1 = com.alipay.android.phone.mrpc.core.i.a(r7);
        r2 = r7.getContentLength();
        r0 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        r0 = new byte[r0];	 Catch:{ Exception -> 0x003a }
    L_0x000c:
        r4 = r1.read(r0);	 Catch:{ Exception -> 0x003a }
        r5 = -1;
        if (r4 == r5) goto L_0x0033;
    L_0x0013:
        r5 = r6.c;	 Catch:{ Exception -> 0x003a }
        r5 = r5.f;	 Catch:{ Exception -> 0x003a }
        if (r5 != 0) goto L_0x0033;
    L_0x0019:
        r5 = 0;
        r8.write(r0, r5, r4);	 Catch:{ Exception -> 0x003a }
        r4 = r6.c;	 Catch:{ Exception -> 0x003a }
        r4 = r4.a();	 Catch:{ Exception -> 0x003a }
        if (r4 == 0) goto L_0x000c;
    L_0x0025:
        r4 = 0;
        r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r4 <= 0) goto L_0x000c;
    L_0x002b:
        r4 = r6.c;	 Catch:{ Exception -> 0x003a }
        r4.a();	 Catch:{ Exception -> 0x003a }
        r4 = r6.c;	 Catch:{ Exception -> 0x003a }
        goto L_0x000c;
    L_0x0033:
        r8.flush();	 Catch:{ Exception -> 0x003a }
        com.alipay.android.phone.mrpc.core.y.a(r1);
        return;
    L_0x003a:
        r0 = move-exception;
        r0.getCause();	 Catch:{ all -> 0x0057 }
        r2 = new java.io.IOException;	 Catch:{ all -> 0x0057 }
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0057 }
        r4 = "HttpWorker Request Error!";
        r3.<init>(r4);	 Catch:{ all -> 0x0057 }
        r0 = r0.getLocalizedMessage();	 Catch:{ all -> 0x0057 }
        r0 = r3.append(r0);	 Catch:{ all -> 0x0057 }
        r0 = r0.toString();	 Catch:{ all -> 0x0057 }
        r2.<init>(r0);	 Catch:{ all -> 0x0057 }
        throw r2;	 Catch:{ all -> 0x0057 }
    L_0x0057:
        r0 = move-exception;
        com.alipay.android.phone.mrpc.core.y.a(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alipay.android.phone.mrpc.core.x.a(org.apache.http.HttpEntity, java.io.OutputStream):void");
    }

    private static long b(HttpResponse httpResponse) {
        long j = 0;
        Header firstHeader = httpResponse.getFirstHeader(HttpRequest.HEADER_CACHE_CONTROL);
        if (firstHeader != null) {
            String[] split = firstHeader.getValue().split(SimpleComparison.EQUAL_TO_OPERATION);
            if (split.length >= 2) {
                try {
                    return a(split);
                } catch (NumberFormatException e) {
                }
            }
        }
        firstHeader = httpResponse.getFirstHeader(HttpRequest.HEADER_EXPIRES);
        return firstHeader != null ? i.b(firstHeader.getValue()) - System.currentTimeMillis() : j;
    }

    private URI b() {
        String str = this.c.a;
        if (this.d != null) {
            str = this.d;
        }
        if (str != null) {
            return new URI(str);
        }
        throw new RuntimeException("url should not be null");
    }

    private HttpUriRequest c() {
        if (this.f != null) {
            return this.f;
        }
        if (this.j == null) {
            byte[] bArr = this.c.b;
            CharSequence a = this.c.a(HttpRequest.ENCODING_GZIP);
            if (bArr != null) {
                if (TextUtils.equals(a, "true")) {
                    this.j = i.a(bArr);
                } else {
                    this.j = new ByteArrayEntity(bArr);
                }
                this.j.setContentType(this.c.c);
            }
        }
        HttpEntity httpEntity = this.j;
        if (httpEntity != null) {
            HttpUriRequest httpPost = new HttpPost(b());
            httpPost.setEntity(httpEntity);
            this.f = httpPost;
        } else {
            this.f = new HttpGet(b());
        }
        return this.f;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.alipay.android.phone.mrpc.core.ab d() {
        /*
        r15 = this;
        r14 = 6;
        r13 = 3;
        r12 = 2;
        r4 = 1;
        r5 = 0;
    L_0x0005:
        r2 = r15.b;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = "connectivity";
        r2 = r2.getSystemService(r3);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = (android.net.ConnectivityManager) r2;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r2.getAllNetworkInfo();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r3 != 0) goto L_0x0045;
    L_0x0015:
        r2 = r5;
    L_0x0016:
        if (r2 != 0) goto L_0x005e;
    L_0x0018:
        r2 = new com.alipay.android.phone.mrpc.core.a;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = 1;
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = "The network is not available";
        r2.<init>(r3, r6);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        throw r2;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x0025:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x003c;
    L_0x0031:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = r2.k;
        r3 = r2.l;
    L_0x003c:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        throw r2;
    L_0x0045:
        r6 = r3.length;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r5;
    L_0x0047:
        if (r2 >= r6) goto L_0x04e8;
    L_0x0049:
        r7 = r3[r2];	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r7 == 0) goto L_0x005b;
    L_0x004d:
        r8 = r7.isAvailable();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r8 == 0) goto L_0x005b;
    L_0x0053:
        r7 = r7.isConnectedOrConnecting();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r7 == 0) goto L_0x005b;
    L_0x0059:
        r2 = r4;
        goto L_0x0016;
    L_0x005b:
        r2 = r2 + 1;
        goto L_0x0047;
    L_0x005e:
        r2 = r15.c;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.a();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x006d;
    L_0x0066:
        r2 = r15.c;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2.a();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r15.c;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x006d:
        r2 = r15.c;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.d;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x009e;
    L_0x0073:
        r3 = r2.isEmpty();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r3 != 0) goto L_0x009e;
    L_0x0079:
        r3 = r2.iterator();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x007d:
        r2 = r3.hasNext();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x009e;
    L_0x0083:
        r2 = r3.next();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = (org.apache.http.Header) r2;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r15.c();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6.addHeader(r2);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        goto L_0x007d;
    L_0x0091:
        r2 = move-exception;
        r3 = new java.lang.RuntimeException;
        r4 = "Url parser error!";
        r2 = r2.getCause();
        r3.<init>(r4, r2);
        throw r3;
    L_0x009e:
        r2 = r15.c();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        com.alipay.android.phone.mrpc.core.i.a(r2);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r15.c();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        com.alipay.android.phone.mrpc.core.i.b(r2);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r15.c();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = "cookie";
        r6 = r15.i();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r7 = r15.c;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r7 = r7.a;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r6.getCookie(r7);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2.addHeader(r3, r6);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r15.g;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = "http.cookie-store";
        r6 = r15.h;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2.setAttribute(r3, r6);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r15.a;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.b;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = e;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.b;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = (org.apache.http.impl.client.DefaultHttpClient) r2;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2.setHttpRequestRetryHandler(r3);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = java.lang.System.currentTimeMillis();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = new java.lang.StringBuilder;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = "By Http/Https to request. operationType=";
        r2.<init>(r3);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r15.f();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.append(r3);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = " url=";
        r2 = r2.append(r3);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r15.f;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r3.getURI();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r3.toString();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2.append(r3);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r15.a;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.b;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = r2.getParams();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r9 = "http.route.default-proxy";
        r2 = r15.b;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = 0;
        r10 = "connectivity";
        r2 = r2.getSystemService(r10);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = (android.net.ConnectivityManager) r2;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.getActiveNetworkInfo();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x04e5;
    L_0x0118:
        r2 = r2.isAvailable();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x04e5;
    L_0x011e:
        r10 = android.net.Proxy.getDefaultHost();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r11 = android.net.Proxy.getDefaultPort();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r10 == 0) goto L_0x04e5;
    L_0x0128:
        r2 = new org.apache.http.HttpHost;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2.<init>(r10, r11);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x012d:
        if (r2 == 0) goto L_0x0144;
    L_0x012f:
        r3 = r2.getHostName();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r10 = "127.0.0.1";
        r3 = android.text.TextUtils.equals(r3, r10);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r3 == 0) goto L_0x0144;
    L_0x013b:
        r3 = r2.getPort();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r10 = 8087; // 0x1f97 float:1.1332E-41 double:3.9955E-320;
        if (r3 != r10) goto L_0x0144;
    L_0x0143:
        r2 = 0;
    L_0x0144:
        r8.setParameter(r9, r2);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r15.k;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x022d;
    L_0x014b:
        r2 = r15.k;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x014d:
        r3 = r15.g();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = 80;
        if (r3 != r8) goto L_0x0162;
    L_0x0155:
        r2 = new org.apache.http.HttpHost;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r15.h();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r3.getHost();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2.<init>(r3);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x0162:
        r3 = r15.a;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r3.b;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = r15.f;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r9 = r15.g;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r3.execute(r2, r8, r9);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = java.lang.System.currentTimeMillis();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r15.a;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r8 - r6;
        r8 = r2.d;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r6 + r8;
        r2.d = r6;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r2.f;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r6 + 1;
        r2.f = r6;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r15.h;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.getCookies();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r15.c;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r6.e;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r6 == 0) goto L_0x0194;
    L_0x018d:
        r6 = r15.i();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6.removeAllCookie();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x0194:
        r6 = r2.isEmpty();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r6 != 0) goto L_0x024b;
    L_0x019a:
        r6 = r2.iterator();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x019e:
        r2 = r6.hasNext();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x024b;
    L_0x01a4:
        r2 = r6.next();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = (org.apache.http.cookie.Cookie) r2;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r7 = r2.getDomain();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r7 == 0) goto L_0x019e;
    L_0x01b0:
        r7 = new java.lang.StringBuilder;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r7.<init>();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = r2.getName();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r7 = r7.append(r8);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = "=";
        r7 = r7.append(r8);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = r2.getValue();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r7 = r7.append(r8);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = "; domain=";
        r7 = r7.append(r8);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = r2.getDomain();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r7 = r7.append(r8);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.isSecure();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x0248;
    L_0x01df:
        r2 = "; Secure";
    L_0x01e1:
        r2 = r7.append(r2);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.toString();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r7 = r15.i();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = r15.c;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = r8.a;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r7.setCookie(r8, r2);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = android.webkit.CookieSyncManager.getInstance();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2.sync();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        goto L_0x019e;
    L_0x01fc:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x0217;
    L_0x0208:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x0217:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = java.lang.Integer.valueOf(r12);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x022d:
        r2 = r15.h();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = new org.apache.http.HttpHost;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r8 = r2.getHost();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r9 = r15.g();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.getProtocol();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3.<init>(r8, r9, r2);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r15.k = r3;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r15.k;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        goto L_0x014d;
    L_0x0248:
        r2 = "";
        goto L_0x01e1;
    L_0x024b:
        r2 = r15.c;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r3.getStatusLine();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r2.getStatusCode();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r3.getStatusLine();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r7 = r2.getReasonPhrase();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r6 == r2) goto L_0x02b5;
    L_0x0261:
        r2 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
        if (r6 != r2) goto L_0x02b3;
    L_0x0265:
        r2 = r4;
    L_0x0266:
        if (r2 != 0) goto L_0x02b5;
    L_0x0268:
        r2 = new com.alipay.android.phone.mrpc.core.a;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r3.getStatusLine();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r6.getStatusCode();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r3.getStatusLine();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r3 = r3.getReasonPhrase();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2.<init>(r6, r3);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        throw r2;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x0282:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x029d;
    L_0x028e:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x029d:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = java.lang.Integer.valueOf(r12);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x02b3:
        r2 = r5;
        goto L_0x0266;
    L_0x02b5:
        r3 = r15.a(r3, r6, r7);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = -1;
        if (r3 == 0) goto L_0x02c9;
    L_0x02bd:
        r2 = r3.a();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x02c9;
    L_0x02c3:
        r2 = r3.a();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.length;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = (long) r2;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x02c9:
        r8 = -1;
        r2 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r2 != 0) goto L_0x02e6;
    L_0x02cf:
        r2 = r3 instanceof com.alipay.android.phone.mrpc.core.w;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x02e6;
    L_0x02d3:
        r0 = r3;
        r0 = (com.alipay.android.phone.mrpc.core.w) r0;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r0;
        r2 = r2.d;	 Catch:{ Exception -> 0x04e2, a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493 }
        r6 = "Content-Length";
        r2 = r2.a;	 Catch:{ Exception -> 0x04e2, a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493 }
        r2 = r2.get(r6);	 Catch:{ Exception -> 0x04e2, a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493 }
        r2 = (java.lang.String) r2;	 Catch:{ Exception -> 0x04e2, a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493 }
        java.lang.Long.parseLong(r2);	 Catch:{ Exception -> 0x04e2, a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493 }
    L_0x02e6:
        r2 = r15.c;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r2.a;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r2 == 0) goto L_0x030c;
    L_0x02ec:
        r6 = r15.f();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = android.text.TextUtils.isEmpty(r6);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        if (r6 != 0) goto L_0x030c;
    L_0x02f6:
        r6 = new java.lang.StringBuilder;	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6.<init>();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2 = r6.append(r2);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = "#";
        r2 = r2.append(r6);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r6 = r15.f();	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
        r2.append(r6);	 Catch:{ a -> 0x0025, URISyntaxException -> 0x0091, SSLHandshakeException -> 0x01fc, SSLPeerUnverifiedException -> 0x0282, SSLException -> 0x030d, ConnectionPoolTimeoutException -> 0x033e, ConnectTimeoutException -> 0x036f, SocketTimeoutException -> 0x03a0, NoHttpResponseException -> 0x03d2, HttpHostConnectException -> 0x0404, UnknownHostException -> 0x042f, IOException -> 0x0462, NullPointerException -> 0x0493, Exception -> 0x04b9 }
    L_0x030c:
        return r3;
    L_0x030d:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x0328;
    L_0x0319:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x0328:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = java.lang.Integer.valueOf(r14);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x033e:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x0359;
    L_0x034a:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x0359:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = java.lang.Integer.valueOf(r13);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x036f:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x038a;
    L_0x037b:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x038a:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = java.lang.Integer.valueOf(r13);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x03a0:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x03bb;
    L_0x03ac:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x03bb:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = 4;
        r4 = java.lang.Integer.valueOf(r4);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x03d2:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x03ed;
    L_0x03de:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x03ed:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = 5;
        r4 = java.lang.Integer.valueOf(r4);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x0404:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x041f;
    L_0x0410:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x041f:
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = 8;
        r4 = java.lang.Integer.valueOf(r4);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x042f:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x044a;
    L_0x043b:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x044a:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = 9;
        r4 = java.lang.Integer.valueOf(r4);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x0462:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x047d;
    L_0x046e:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x047d:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = java.lang.Integer.valueOf(r14);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x0493:
        r2 = move-exception;
        r15.e();
        r3 = r15.m;
        if (r3 > 0) goto L_0x04a3;
    L_0x049b:
        r2 = r15.m;
        r2 = r2 + 1;
        r15.m = r2;
        goto L_0x0005;
    L_0x04a3:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = java.lang.Integer.valueOf(r5);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x04b9:
        r2 = move-exception;
        r15.e();
        r3 = r15.c;
        r3 = r3.a();
        if (r3 == 0) goto L_0x04d4;
    L_0x04c5:
        r3 = r15.c;
        r3.a();
        r3 = r15.c;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
    L_0x04d4:
        r3 = new com.alipay.android.phone.mrpc.core.a;
        r4 = java.lang.Integer.valueOf(r5);
        r2 = java.lang.String.valueOf(r2);
        r3.<init>(r4, r2);
        throw r3;
    L_0x04e2:
        r2 = move-exception;
        goto L_0x02e6;
    L_0x04e5:
        r2 = r3;
        goto L_0x012d;
    L_0x04e8:
        r2 = r5;
        goto L_0x0016;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alipay.android.phone.mrpc.core.x.d():com.alipay.android.phone.mrpc.core.ab");
    }

    private void e() {
        if (this.f != null) {
            this.f.abort();
        }
    }

    private String f() {
        if (!TextUtils.isEmpty(this.q)) {
            return this.q;
        }
        this.q = this.c.a("operationType");
        return this.q;
    }

    private int g() {
        URL h = h();
        return h.getPort() == -1 ? h.getDefaultPort() : h.getPort();
    }

    private URL h() {
        if (this.l != null) {
            return this.l;
        }
        this.l = new URL(this.c.a);
        return this.l;
    }

    private CookieManager i() {
        if (this.i != null) {
            return this.i;
        }
        this.i = CookieManager.getInstance();
        return this.i;
    }

    public final v a() {
        return this.c;
    }

    public final /* synthetic */ Object call() {
        return d();
    }
}
