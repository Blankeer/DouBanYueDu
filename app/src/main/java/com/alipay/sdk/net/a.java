package com.alipay.sdk.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.alipay.sdk.data.c;
import com.alipay.sdk.exception.NetErrorException;
import com.douban.book.reader.constant.Constants;
import com.igexin.sdk.PushBuildConfig;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpParams;

public final class a {
    String a;
    private Context b;

    private a(Context context) {
        this(context, null);
    }

    public a(Context context, String str) {
        this.b = context;
        this.a = str;
    }

    private void a(String str) {
        this.a = str;
    }

    private String a() {
        return this.a;
    }

    private URL b() {
        try {
            return new URL(this.a);
        } catch (Exception e) {
            return null;
        }
    }

    private static ByteArrayEntity a(c cVar, String str) throws IOException {
        String str2 = null;
        if (cVar != null) {
            str2 = cVar.c;
            if (!TextUtils.isEmpty(cVar.d)) {
                str = cVar.d + SimpleComparison.EQUAL_TO_OPERATION + str;
            }
        }
        if (TextUtils.isEmpty(str2)) {
            str2 = c.a;
        }
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(str.getBytes("utf-8"));
        byteArrayEntity.setContentType(str2);
        return byteArrayEntity;
    }

    private HttpResponse b(String str) throws NetErrorException {
        return a(str, null);
    }

    public final HttpResponse a(String str, c cVar) throws NetErrorException {
        HttpResponse httpResponse = null;
        b a = b.a();
        if (a != null) {
            try {
                String g;
                Object httpHost;
                HttpUriRequest httpGet;
                HttpParams params = a.c.getParams();
                if (VERSION.SDK_INT >= 11) {
                    g = g();
                    if (g == null || g.contains("wap")) {
                        URL b = b();
                        if (b != null) {
                            Constants.API_SCHEME.equalsIgnoreCase(b.getProtocol());
                            Object property = System.getProperty("https.proxyHost");
                            String property2 = System.getProperty("https.proxyPort");
                            if (!TextUtils.isEmpty(property)) {
                                httpHost = new HttpHost(property, Integer.parseInt(property2));
                            }
                        }
                        httpHost = null;
                    } else {
                        httpHost = null;
                    }
                } else {
                    NetworkInfo f = f();
                    if (f != null && f.isAvailable() && f.getType() == 0) {
                        String defaultHost = Proxy.getDefaultHost();
                        int defaultPort = Proxy.getDefaultPort();
                        if (defaultHost != null) {
                            httpHost = new HttpHost(defaultHost, defaultPort);
                        }
                    }
                    httpHost = null;
                }
                if (httpHost != null) {
                    params.setParameter("http.route.default-proxy", httpHost);
                }
                new StringBuilder("requestUrl : ").append(this.a);
                if (TextUtils.isEmpty(str)) {
                    httpGet = new HttpGet(this.a);
                } else {
                    httpGet = new HttpPost(this.a);
                    if (cVar != null) {
                        g = cVar.c;
                        if (!TextUtils.isEmpty(cVar.d)) {
                            str = cVar.d + SimpleComparison.EQUAL_TO_OPERATION + str;
                        }
                    } else {
                        g = null;
                    }
                    if (TextUtils.isEmpty(g)) {
                        g = c.a;
                    }
                    HttpEntity byteArrayEntity = new ByteArrayEntity(str.getBytes("utf-8"));
                    byteArrayEntity.setContentType(g);
                    ((HttpPost) httpGet).setEntity(byteArrayEntity);
                    httpGet.addHeader(HttpRequest.HEADER_ACCEPT_CHARSET, HttpRequest.CHARSET_UTF8);
                    httpGet.addHeader(HttpRequest.HEADER_ACCEPT_ENCODING, HttpRequest.ENCODING_GZIP);
                    httpGet.addHeader("Connection", "Keep-Alive");
                    httpGet.addHeader("Keep-Alive", "timeout=180, max=100");
                }
                if (cVar != null) {
                    ArrayList a2 = cVar.a();
                    if (a2 != null) {
                        Iterator it = a2.iterator();
                        while (it.hasNext()) {
                            httpGet.addHeader((Header) it.next());
                        }
                    }
                }
                httpResponse = a.a(httpGet);
                Header[] headers = httpResponse.getHeaders("X-Hostname");
                if (!(headers == null || headers.length <= 0 || headers[0] == null)) {
                    httpResponse.getHeaders("X-Hostname")[0].toString();
                }
                headers = httpResponse.getHeaders("X-ExecuteTime");
                if (!(headers == null || headers.length <= 0 || headers[0] == null)) {
                    httpResponse.getHeaders("X-ExecuteTime")[0].toString();
                }
            } catch (NetErrorException e) {
                throw e;
            } catch (ConnectTimeoutException e2) {
                if (a != null) {
                    a.b();
                }
                throw new NetErrorException();
            } catch (SocketException e3) {
                throw new NetErrorException();
            } catch (SocketTimeoutException e4) {
                if (a != null) {
                    a.b();
                }
                throw new NetErrorException();
            } catch (Exception e5) {
                throw new NetErrorException();
            }
        }
        return httpResponse;
    }

    private HttpHost c() {
        String g;
        if (VERSION.SDK_INT >= 11) {
            g = g();
            if (g != null && !g.contains("wap")) {
                return null;
            }
            URL b = b();
            if (b == null) {
                return null;
            }
            Constants.API_SCHEME.equalsIgnoreCase(b.getProtocol());
            Object property = System.getProperty("https.proxyHost");
            String property2 = System.getProperty("https.proxyPort");
            if (TextUtils.isEmpty(property)) {
                return null;
            }
            return new HttpHost(property, Integer.parseInt(property2));
        }
        NetworkInfo f = f();
        if (f == null || !f.isAvailable() || f.getType() != 0) {
            return null;
        }
        g = Proxy.getDefaultHost();
        int defaultPort = Proxy.getDefaultPort();
        if (g != null) {
            return new HttpHost(g, defaultPort);
        }
        return null;
    }

    private HttpHost d() {
        NetworkInfo f = f();
        if (f == null || !f.isAvailable() || f.getType() != 0) {
            return null;
        }
        String defaultHost = Proxy.getDefaultHost();
        int defaultPort = Proxy.getDefaultPort();
        if (defaultHost != null) {
            return new HttpHost(defaultHost, defaultPort);
        }
        return null;
    }

    private HttpHost e() {
        String g = g();
        if (g != null && !g.contains("wap")) {
            return null;
        }
        URL b = b();
        if (b == null) {
            return null;
        }
        Constants.API_SCHEME.equalsIgnoreCase(b.getProtocol());
        Object property = System.getProperty("https.proxyHost");
        String property2 = System.getProperty("https.proxyPort");
        if (TextUtils.isEmpty(property)) {
            return null;
        }
        return new HttpHost(property, Integer.parseInt(property2));
    }

    private NetworkInfo f() {
        try {
            return ((ConnectivityManager) this.b.getSystemService("connectivity")).getActiveNetworkInfo();
        } catch (Exception e) {
            return null;
        }
    }

    private String g() {
        try {
            NetworkInfo f = f();
            if (f == null || !f.isAvailable()) {
                return PushBuildConfig.sdk_conf_debug_level;
            }
            if (f.getType() == 1) {
                return "wifi";
            }
            return f.getExtraInfo().toLowerCase();
        } catch (Exception e) {
            return PushBuildConfig.sdk_conf_debug_level;
        }
    }
}
