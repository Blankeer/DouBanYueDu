package com.sina.weibo.sdk.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.webkit.URLUtil;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.helper.AppUri;
import com.igexin.download.Downloads;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.exception.WeiboHttpException;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.NetworkHelper;
import com.sina.weibo.sdk.utils.Utility;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.jsoup.helper.HttpConnection;

public class HttpManager {
    private static final String BOUNDARY;
    private static final int BUFFER_SIZE = 8192;
    private static final int CONNECTION_TIMEOUT = 25000;
    private static final String END_MP_BOUNDARY;
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String MP_BOUNDARY;
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final int SOCKET_TIMEOUT = 20000;
    private static final String TAG = "HttpManager";
    private static SSLSocketFactory sSSLSocketFactory;

    private static native String calcOauthSignNative(Context context, String str, String str2);

    static {
        System.loadLibrary("weibosdkcore");
        BOUNDARY = getBoundry();
        MP_BOUNDARY = "--" + BOUNDARY;
        END_MP_BOUNDARY = "--" + BOUNDARY + "--";
    }

    public static String openUrl(Context context, String url, String method, WeiboParameters params) throws WeiboException {
        String ans = readRsponse(requestHttpExecute(context, url, method, params));
        LogUtil.d(TAG, "Response : " + ans);
        return ans;
    }

    private static HttpResponse requestHttpExecute(Context context, String url, String method, WeiboParameters params) {
        Throwable e;
        Throwable th;
        HttpClient client = null;
        ByteArrayOutputStream baos = null;
        try {
            client = getNewHttpClient();
            client.getParams().setParameter("http.route.default-proxy", NetStateManager.getAPN());
            HttpUriRequest request = null;
            setHttpCommonParam(context, params);
            if (method.equals(HTTP_METHOD_GET)) {
                url = new StringBuilder(String.valueOf(url)).append("?").append(params.encodeUrl()).toString();
                request = new HttpGet(url);
                LogUtil.d(TAG, "requestHttpExecute GET Url : " + url);
            } else {
                if (method.equals(HTTP_METHOD_POST)) {
                    LogUtil.d(TAG, "requestHttpExecute POST Url : " + url);
                    HttpPost post = new HttpPost(url);
                    Object request2 = post;
                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                    try {
                        if (params.hasBinaryData()) {
                            post.setHeader(HttpRequest.HEADER_CONTENT_TYPE, "multipart/form-data; boundary=" + BOUNDARY);
                            buildParams(baos2, params);
                        } else {
                            Object value = params.get("content-type");
                            if (value == null || !(value instanceof String)) {
                                post.setHeader(HttpRequest.HEADER_CONTENT_TYPE, HttpRequest.CONTENT_TYPE_FORM);
                            } else {
                                params.remove("content-type");
                                post.setHeader(HttpRequest.HEADER_CONTENT_TYPE, (String) value);
                            }
                            String postParam = params.encodeUrl();
                            LogUtil.d(TAG, "requestHttpExecute POST postParam : " + postParam);
                            baos2.write(postParam.getBytes(HttpRequest.CHARSET_UTF8));
                        }
                        post.setEntity(new ByteArrayEntity(baos2.toByteArray()));
                        baos = baos2;
                    } catch (IOException e2) {
                        e = e2;
                        baos = baos2;
                        try {
                            e.printStackTrace();
                            throw new WeiboException(e);
                        } catch (Throwable th2) {
                            th = th2;
                            if (baos != null) {
                                try {
                                    baos.close();
                                } catch (IOException e3) {
                                }
                            }
                            shutdownHttpClient(client);
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        baos = baos2;
                        if (baos != null) {
                            baos.close();
                        }
                        shutdownHttpClient(client);
                        throw th;
                    }
                }
                if (method.equals(HttpRequest.METHOD_DELETE)) {
                    request = new HttpDelete(url);
                }
            }
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != Downloads.STATUS_SUCCESS) {
                throw new WeiboHttpException(readRsponse(response), statusCode);
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e4) {
                }
            }
            shutdownHttpClient(client);
            return response;
        } catch (IOException e5) {
            e = e5;
            e.printStackTrace();
            throw new WeiboException(e);
        }
    }

    private static void setHttpCommonParam(Context context, WeiboParameters params) {
        String aid = Table.STRING_DEFAULT_VALUE;
        if (!TextUtils.isEmpty(params.getAppKey())) {
            aid = Utility.getAid(context, params.getAppKey());
            if (!TextUtils.isEmpty(aid)) {
                params.put(ShareRequestParam.REQ_PARAM_AID, aid);
            }
        }
        String timestamp = getTimestamp();
        params.put("oauth_timestamp", timestamp);
        String token = Table.STRING_DEFAULT_VALUE;
        String accessToken = params.get(ShareRequestParam.REQ_PARAM_TOKEN);
        String refreshToken = params.get(Oauth2AccessToken.KEY_REFRESH_TOKEN);
        String phone = params.get("phone");
        if (accessToken != null && (accessToken instanceof String)) {
            token = accessToken;
        } else if (refreshToken != null && (refreshToken instanceof String)) {
            token = refreshToken;
        } else if (phone != null && (phone instanceof String)) {
            token = phone;
        }
        params.put("oauth_sign", getOauthSign(context, aid, token, params.getAppKey(), timestamp));
    }

    public static void shutdownHttpClient(HttpClient client) {
        if (client != null) {
            try {
                client.getConnectionManager().closeExpiredConnections();
            } catch (Exception e) {
            }
        }
    }

    public static String openUrl4RdirectURL(Context context, String url, String method, WeiboParameters params) throws WeiboException {
        DefaultHttpClient client = null;
        String result = Table.STRING_DEFAULT_VALUE;
        try {
            String redirectURL;
            client = (DefaultHttpClient) getNewHttpClient();
            client.setRedirectHandler(new RedirectHandler() {
                public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
                    LogUtil.d(HttpManager.TAG, "openUrl4RdirectURL isRedirectRequested method");
                    return false;
                }

                public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
                    LogUtil.d(HttpManager.TAG, "openUrl4RdirectURL getLocationURI method");
                    return null;
                }
            });
            setHttpCommonParam(context, params);
            HttpUriRequest request = null;
            client.getParams().setParameter("http.route.default-proxy", NetStateManager.getAPN());
            if (method.equals(HTTP_METHOD_GET)) {
                url = new StringBuilder(String.valueOf(url)).append("?").append(params.encodeUrl()).toString();
                LogUtil.d(TAG, "openUrl4RdirectURL GET url : " + url);
                request = new HttpGet(url);
            } else if (method.equals(HTTP_METHOD_POST)) {
                HttpPost post = new HttpPost(url);
                LogUtil.d(TAG, "openUrl4RdirectURL POST url : " + url);
                Object request2 = post;
            }
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == AppUri.READER_COLUMN || statusCode == AppUri.READER_COLUMN_CHAPTER) {
                redirectURL = response.getFirstHeader(HttpRequest.HEADER_LOCATION).getValue();
                LogUtil.d(TAG, "RedirectURL = " + redirectURL);
                shutdownHttpClient(client);
            } else if (statusCode == Downloads.STATUS_SUCCESS) {
                redirectURL = readRsponse(response);
                shutdownHttpClient(client);
            } else {
                throw new WeiboHttpException(readRsponse(response), statusCode);
            }
            return redirectURL;
        } catch (Throwable e) {
            throw new WeiboException(e);
        } catch (Throwable th) {
            shutdownHttpClient(client);
        }
    }

    public static String openRedirectUrl4LocationUri(Context context, String url, String method, WeiboParameters params) {
        Throwable e;
        CustomRedirectHandler customRedirectHandler;
        Throwable th;
        DefaultHttpClient client = null;
        try {
            CustomRedirectHandler redirectHandler = new CustomRedirectHandler() {
                public boolean shouldRedirectUrl(String url) {
                    return true;
                }

                public void onReceivedException() {
                }
            };
            try {
                client = (DefaultHttpClient) getNewHttpClient();
                client.setRedirectHandler(redirectHandler);
                setHttpCommonParam(context, params);
                HttpUriRequest request = null;
                client.getParams().setParameter("http.route.default-proxy", NetStateManager.getAPN());
                if (method.equals(HTTP_METHOD_GET)) {
                    request = new HttpGet(new StringBuilder(String.valueOf(url)).append("?").append(params.encodeUrl()).toString());
                } else if (method.equals(HTTP_METHOD_POST)) {
                    Object request2 = new HttpPost(url);
                }
                request.setHeader(HttpRequest.HEADER_USER_AGENT, NetworkHelper.generateUA(context));
                client.execute(request);
                String redirectUrl = redirectHandler.getRedirectUrl();
                shutdownHttpClient(client);
                return redirectUrl;
            } catch (IOException e2) {
                e = e2;
                customRedirectHandler = redirectHandler;
                try {
                    throw new WeiboException(e);
                } catch (Throwable th2) {
                    th = th2;
                    shutdownHttpClient(client);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                customRedirectHandler = redirectHandler;
                shutdownHttpClient(client);
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
            throw new WeiboException(e);
        }
    }

    public static synchronized String downloadFile(Context context, String url, String saveDir, String fileName) throws WeiboException {
        String path;
        synchronized (HttpManager.class) {
            File file = new File(saveDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            File filePath = new File(file, fileName);
            if (filePath.exists()) {
                path = filePath.getPath();
            } else if (URLUtil.isValidUrl(url)) {
                HttpClient client;
                client = getNewHttpClient();
                long tempFileLength = 0;
                file = new File(saveDir, new StringBuilder(String.valueOf(fileName)).append("_temp").toString());
                try {
                    long startPosition;
                    InputStream inputStream;
                    RandomAccessFile content;
                    byte[] sBuffer;
                    int readBytes;
                    if (file.exists()) {
                        tempFileLength = file.length();
                    } else {
                        file.createNewFile();
                    }
                    HttpGet request = new HttpGet(url);
                    request.setHeader("RANGE", "bytes=" + tempFileLength + "-");
                    HttpResponse response = client.execute(request);
                    int statusCode = response.getStatusLine().getStatusCode();
                    long totalLength = 0;
                    if (statusCode == 206) {
                        startPosition = tempFileLength;
                        Header[] rangeHeaders = response.getHeaders("Content-Range");
                        if (!(rangeHeaders == null || rangeHeaders.length == 0)) {
                            String rangValue = rangeHeaders[0].getValue();
                            totalLength = Long.parseLong(rangValue.substring(rangValue.indexOf(47) + 1));
                        }
                    } else if (statusCode == 200) {
                        startPosition = 0;
                        Header lengthHeader = response.getFirstHeader(HttpRequest.HEADER_CONTENT_LENGTH);
                        if (lengthHeader != null) {
                            totalLength = (long) Integer.valueOf(lengthHeader.getValue()).intValue();
                        }
                    } else {
                        throw new WeiboHttpException(readRsponse(response), statusCode);
                    }
                    HttpEntity entity = response.getEntity();
                    Header header = response.getFirstHeader(HttpConnection.CONTENT_ENCODING);
                    if (header != null) {
                        if (header.getValue().toLowerCase().indexOf(HttpRequest.ENCODING_GZIP) > -1) {
                            inputStream = new GZIPInputStream(entity.getContent());
                            content = new RandomAccessFile(file, "rw");
                            content.seek(startPosition);
                            sBuffer = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
                            while (true) {
                                readBytes = inputStream.read(sBuffer);
                                if (readBytes != -1) {
                                    break;
                                }
                                content.write(sBuffer, 0, readBytes);
                            }
                            content.close();
                            inputStream.close();
                            if (totalLength != 0 || file.length() < totalLength) {
                                file.delete();
                                if (client != null) {
                                    client.getConnectionManager().closeExpiredConnections();
                                    client.getConnectionManager().closeIdleConnections(300, TimeUnit.SECONDS);
                                }
                                path = Table.STRING_DEFAULT_VALUE;
                            } else {
                                file.renameTo(filePath);
                                path = filePath.getPath();
                                if (client != null) {
                                    client.getConnectionManager().closeExpiredConnections();
                                    client.getConnectionManager().closeIdleConnections(300, TimeUnit.SECONDS);
                                }
                            }
                        }
                    }
                    inputStream = entity.getContent();
                    content = new RandomAccessFile(file, "rw");
                    content.seek(startPosition);
                    sBuffer = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
                    while (true) {
                        readBytes = inputStream.read(sBuffer);
                        if (readBytes != -1) {
                            content.write(sBuffer, 0, readBytes);
                        } else {
                            break;
                            content.close();
                            inputStream.close();
                            if (totalLength != 0) {
                            }
                            file.delete();
                            if (client != null) {
                                client.getConnectionManager().closeExpiredConnections();
                                client.getConnectionManager().closeIdleConnections(300, TimeUnit.SECONDS);
                            }
                            path = Table.STRING_DEFAULT_VALUE;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    file.delete();
                    if (client != null) {
                        client.getConnectionManager().closeExpiredConnections();
                        client.getConnectionManager().closeIdleConnections(300, TimeUnit.SECONDS);
                    }
                } catch (Throwable th) {
                    if (client != null) {
                        client.getConnectionManager().closeExpiredConnections();
                        client.getConnectionManager().closeIdleConnections(300, TimeUnit.SECONDS);
                    }
                }
            } else {
                path = Table.STRING_DEFAULT_VALUE;
            }
        }
        return path;
    }

    public static HttpClient getNewHttpClient() {
        try {
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HttpRequest.CHARSET_UTF8);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme(Constants.API_SCHEME, getSSLSocketFactory(), 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static void buildParams(OutputStream baos, WeiboParameters params) throws WeiboException {
        try {
            StringBuilder sb;
            Set<String> keys = params.keySet();
            for (String key : keys) {
                if (params.get(key) instanceof String) {
                    sb = new StringBuilder(100);
                    sb.setLength(0);
                    sb.append(MP_BOUNDARY).append(Char.CRLF);
                    sb.append("content-disposition: form-data; name=\"").append(key).append("\"\r\n\r\n");
                    sb.append(params.get(key)).append(Char.CRLF);
                    baos.write(sb.toString().getBytes());
                }
            }
            for (String key2 : keys) {
                Object value = params.get(key2);
                ByteArrayOutputStream stream;
                if (value instanceof Bitmap) {
                    sb = new StringBuilder();
                    sb.append(MP_BOUNDARY).append(Char.CRLF);
                    sb.append("content-disposition: form-data; name=\"").append(key2).append("\"; filename=\"file\"\r\n");
                    sb.append("Content-Type: application/octet-stream; charset=utf-8\r\n\r\n");
                    baos.write(sb.toString().getBytes());
                    Bitmap bmp = (Bitmap) value;
                    stream = new ByteArrayOutputStream();
                    bmp.compress(CompressFormat.PNG, 100, stream);
                    baos.write(stream.toByteArray());
                    baos.write(Char.CRLF.getBytes());
                } else if (value instanceof ByteArrayOutputStream) {
                    sb = new StringBuilder();
                    sb.append(MP_BOUNDARY).append(Char.CRLF);
                    sb.append("content-disposition: form-data; name=\"").append(key2).append("\"; filename=\"file\"\r\n");
                    sb.append("Content-Type: application/octet-stream; charset=utf-8\r\n\r\n");
                    baos.write(sb.toString().getBytes());
                    stream = (ByteArrayOutputStream) value;
                    baos.write(stream.toByteArray());
                    baos.write(Char.CRLF.getBytes());
                    stream.close();
                }
            }
            baos.write(new StringBuilder(Char.CRLF).append(END_MP_BOUNDARY).toString().getBytes());
        } catch (Throwable e) {
            throw new WeiboException(e);
        }
    }

    public static String readRsponse(HttpResponse response) throws WeiboException {
        if (response == null) {
            return null;
        }
        HttpEntity entity = response.getEntity();
        InputStream inputStream = null;
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        try {
            inputStream = entity.getContent();
            Header header = response.getFirstHeader(HttpConnection.CONTENT_ENCODING);
            if (header != null && header.getValue().toLowerCase().indexOf(HttpRequest.ENCODING_GZIP) > -1) {
                inputStream = new GZIPInputStream(inputStream);
            }
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int readBytes = inputStream.read(buffer);
                if (readBytes == -1) {
                    break;
                }
                content.write(buffer, 0, readBytes);
            }
            String result = new String(content.toByteArray(), HttpRequest.CHARSET_UTF8);
            LogUtil.d(TAG, "readRsponse result : " + result);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (content == null) {
                return result;
            }
            try {
                content.close();
                return result;
            } catch (IOException e2) {
                e2.printStackTrace();
                return result;
            }
        } catch (Throwable e3) {
            throw new WeiboException(e3);
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e22) {
                    e22.printStackTrace();
                }
            }
            if (content != null) {
                try {
                    content.close();
                } catch (IOException e222) {
                    e222.printStackTrace();
                }
            }
        }
    }

    public static String getBoundry() {
        StringBuffer sb = new StringBuffer();
        for (int t = 1; t < 12; t++) {
            long time = System.currentTimeMillis() + ((long) t);
            if (time % 3 == 0) {
                sb.append(((char) ((int) time)) % 9);
            } else if (time % 3 == 1) {
                sb.append((char) ((int) (65 + (time % 26))));
            } else {
                sb.append((char) ((int) (97 + (time % 26))));
            }
        }
        return sb.toString();
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        if (sSSLSocketFactory == null) {
            try {
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                Certificate cnCertificate = getCertificate("cacert_cn.cer");
                Certificate comCertificate = getCertificate("cacert_com.cer");
                keyStore.setCertificateEntry("cnca", cnCertificate);
                keyStore.setCertificateEntry("comca", comCertificate);
                sSSLSocketFactory = new SSLSocketFactoryEx(keyStore);
                LogUtil.d(TAG, "getSSLSocketFactory noraml !!!!!");
            } catch (Exception e) {
                e.printStackTrace();
                sSSLSocketFactory = SSLSocketFactory.getSocketFactory();
                LogUtil.d(TAG, "getSSLSocketFactory error default !!!!!");
            }
        }
        return sSSLSocketFactory;
    }

    private static Certificate getCertificate(String name) throws CertificateException, IOException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream certInput = HttpManager.class.getResourceAsStream(name);
        try {
            Certificate certificate = cf.generateCertificate(certInput);
            return certificate;
        } finally {
            if (certInput != null) {
                certInput.close();
            }
        }
    }

    private static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    private static String getOauthSign(Context context, String aid, String accessToken, String appKey, String timestamp) {
        StringBuilder part1 = new StringBuilder(Table.STRING_DEFAULT_VALUE);
        if (!TextUtils.isEmpty(aid)) {
            part1.append(aid);
        }
        if (!TextUtils.isEmpty(accessToken)) {
            part1.append(accessToken);
        }
        if (!TextUtils.isEmpty(appKey)) {
            part1.append(appKey);
        }
        return calcOauthSignNative(context, part1.toString(), timestamp);
    }
}
