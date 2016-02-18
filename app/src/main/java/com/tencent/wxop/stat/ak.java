package com.tencent.wxop.stat;

import android.content.Context;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.igexin.download.Downloads;
import com.tencent.a.a.a.a.h;
import com.tencent.wxop.stat.a.d;
import com.tencent.wxop.stat.b.b;
import com.tencent.wxop.stat.b.f;
import com.tencent.wxop.stat.b.g;
import com.tencent.wxop.stat.b.l;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.helper.HttpConnection;

class ak {
    private static b cx;
    private static ak dj;
    private static Context dk;
    private long cv;
    DefaultHttpClient dg;
    f dh;
    StringBuilder di;

    static {
        cx = l.av();
        dj = null;
        dk = null;
    }

    private ak(Context context) {
        this.dg = null;
        this.dh = null;
        this.di = new StringBuilder(CodedOutputStream.DEFAULT_BUFFER_SIZE);
        this.cv = 0;
        try {
            dk = context.getApplicationContext();
            this.cv = System.currentTimeMillis() / 1000;
            this.dh = new f();
            if (c.k()) {
                try {
                    Logger.getLogger("org.apache.http.wire").setLevel(Level.FINER);
                    Logger.getLogger("org.apache.http.headers").setLevel(Level.FINER);
                    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
                    System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
                    System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
                    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
                    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
                } catch (Throwable th) {
                }
            }
            HttpParams basicHttpParams = new BasicHttpParams();
            HttpConnectionParams.setStaleCheckingEnabled(basicHttpParams, false);
            HttpConnectionParams.setConnectionTimeout(basicHttpParams, AbstractSpiCall.DEFAULT_TIMEOUT);
            HttpConnectionParams.setSoTimeout(basicHttpParams, AbstractSpiCall.DEFAULT_TIMEOUT);
            this.dg = new DefaultHttpClient(basicHttpParams);
            this.dg.setKeepAliveStrategy(new al(this));
        } catch (Throwable th2) {
            cx.b(th2);
        }
    }

    static ak Z(Context context) {
        if (dj == null) {
            synchronized (ak.class) {
                if (dj == null) {
                    dj = new ak(context);
                }
            }
        }
        return dj;
    }

    static Context aB() {
        return dk;
    }

    static void j(Context context) {
        dk = context.getApplicationContext();
    }

    final void a(d dVar, aj ajVar) {
        b(Arrays.asList(new String[]{dVar.af()}), ajVar);
    }

    final void a(List<?> list, aj ajVar) {
        int i = 0;
        if (list != null && !list.isEmpty()) {
            int size = list.size();
            list.get(0);
            Throwable th;
            try {
                int i2;
                String str;
                this.di.delete(0, this.di.length());
                this.di.append("[");
                String str2 = "rc4";
                for (i2 = 0; i2 < size; i2++) {
                    this.di.append(list.get(i2).toString());
                    if (i2 != size - 1) {
                        this.di.append(",");
                    }
                }
                this.di.append("]");
                String stringBuilder = this.di.toString();
                size = stringBuilder.length();
                String str3 = c.y() + "/?index=" + this.cv;
                this.cv++;
                if (c.k()) {
                    cx.b("[" + str3 + "]Send request(" + size + "bytes), content:" + stringBuilder);
                }
                HttpPost httpPost = new HttpPost(str3);
                httpPost.addHeader(HttpRequest.HEADER_ACCEPT_ENCODING, HttpRequest.ENCODING_GZIP);
                httpPost.setHeader("Connection", "Keep-Alive");
                httpPost.removeHeaders(HttpRequest.HEADER_CACHE_CONTROL);
                HttpHost V = g.r(dk).V();
                httpPost.addHeader(HttpConnection.CONTENT_ENCODING, str2);
                if (V == null) {
                    this.dg.getParams().removeParameter("http.route.default-proxy");
                } else {
                    if (c.k()) {
                        cx.e("proxy:" + V.toHostString());
                    }
                    httpPost.addHeader("X-Content-Encoding", str2);
                    this.dg.getParams().setParameter("http.route.default-proxy", V);
                    httpPost.addHeader("X-Online-Host", c.al);
                    httpPost.addHeader(HttpRequest.HEADER_ACCEPT, "*/*");
                    httpPost.addHeader(HttpRequest.HEADER_CONTENT_TYPE, "json");
                }
                OutputStream byteArrayOutputStream = new ByteArrayOutputStream(size);
                byte[] bytes = stringBuilder.getBytes(HttpRequest.CHARSET_UTF8);
                int length = bytes.length;
                if (size > c.aA) {
                    i = 1;
                }
                if (i != 0) {
                    httpPost.removeHeaders(HttpConnection.CONTENT_ENCODING);
                    str = str2 + ",gzip";
                    httpPost.addHeader(HttpConnection.CONTENT_ENCODING, str);
                    if (V != null) {
                        httpPost.removeHeaders("X-Content-Encoding");
                        httpPost.addHeader("X-Content-Encoding", str);
                    }
                    byteArrayOutputStream.write(new byte[4]);
                    GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                    gZIPOutputStream.write(bytes);
                    gZIPOutputStream.close();
                    bytes = byteArrayOutputStream.toByteArray();
                    ByteBuffer.wrap(bytes, 0, 4).putInt(length);
                    if (c.k()) {
                        cx.e("before Gzip:" + length + " bytes, after Gzip:" + bytes.length + " bytes");
                    }
                }
                httpPost.setEntity(new ByteArrayEntity(g.b(bytes)));
                HttpResponse execute = this.dg.execute(httpPost);
                HttpEntity entity = execute.getEntity();
                size = execute.getStatusLine().getStatusCode();
                long contentLength = entity.getContentLength();
                if (c.k()) {
                    cx.b("http recv response status code:" + size + ", content length:" + contentLength);
                }
                if (contentLength <= 0) {
                    cx.d("Server response no data.");
                    if (ajVar != null) {
                        ajVar.B();
                    }
                    EntityUtils.toString(entity);
                    return;
                }
                if (contentLength > 0) {
                    InputStream content = entity.getContent();
                    DataInputStream dataInputStream = new DataInputStream(content);
                    bytes = new byte[((int) entity.getContentLength())];
                    dataInputStream.readFully(bytes);
                    content.close();
                    dataInputStream.close();
                    Header firstHeader = execute.getFirstHeader(HttpConnection.CONTENT_ENCODING);
                    if (firstHeader != null) {
                        if (firstHeader.getValue().equalsIgnoreCase("gzip,rc4")) {
                            bytes = g.c(l.b(bytes));
                        } else if (firstHeader.getValue().equalsIgnoreCase("rc4,gzip")) {
                            bytes = l.b(g.c(bytes));
                        } else if (firstHeader.getValue().equalsIgnoreCase(HttpRequest.ENCODING_GZIP)) {
                            bytes = l.b(bytes);
                        } else if (firstHeader.getValue().equalsIgnoreCase("rc4")) {
                            bytes = g.c(bytes);
                        }
                    }
                    str = new String(bytes, HttpRequest.CHARSET_UTF8);
                    if (c.k()) {
                        cx.b("http get response data:" + str);
                    }
                    JSONObject jSONObject = new JSONObject(str);
                    if (size == Downloads.STATUS_SUCCESS) {
                        try {
                            stringBuilder = jSONObject.optString("mid");
                            if (h.e(stringBuilder)) {
                                if (c.k()) {
                                    cx.b("update mid:" + stringBuilder);
                                }
                                com.tencent.a.a.a.a.g.a(dk).b(stringBuilder);
                            }
                            if (!jSONObject.isNull("cfg")) {
                                c.a(dk, jSONObject.getJSONObject("cfg"));
                            }
                            if (!jSONObject.isNull("ncts")) {
                                i2 = jSONObject.getInt("ncts");
                                i = (int) (((long) i2) - (System.currentTimeMillis() / 1000));
                                if (c.k()) {
                                    cx.b("server time:" + i2 + ", diff time:" + i);
                                }
                                l.Q(dk);
                                l.a(dk, i);
                            }
                        } catch (Throwable th2) {
                            cx.c(th2);
                        }
                        if (ajVar != null) {
                            if (jSONObject.optInt("ret") == 0) {
                                ajVar.ah();
                            } else {
                                cx.error("response error data.");
                                ajVar.B();
                            }
                        }
                    } else {
                        cx.error("Server response error code:" + size + ", error:" + new String(bytes, HttpRequest.CHARSET_UTF8));
                        if (ajVar != null) {
                            ajVar.B();
                        }
                    }
                    content.close();
                } else {
                    EntityUtils.toString(entity);
                }
                byteArrayOutputStream.close();
                th2 = null;
                if (th2 != null) {
                    cx.a(th2);
                    if (ajVar != null) {
                        try {
                            ajVar.B();
                        } catch (Throwable th3) {
                            cx.b(th3);
                        }
                    }
                    if (th2 instanceof OutOfMemoryError) {
                        System.gc();
                        this.di = null;
                        this.di = new StringBuilder(AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT);
                    }
                    g.r(dk).I();
                }
            } catch (Throwable th4) {
            }
        }
    }

    final void b(List<?> list, aj ajVar) {
        if (this.dh != null) {
            this.dh.a(new am(this, list, ajVar));
        }
    }
}
