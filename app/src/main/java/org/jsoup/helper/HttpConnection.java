package org.jsoup.helper;

import com.douban.book.reader.constant.Char;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.content.pack.WorksData;
import com.igexin.download.Downloads;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.TokenQueue;

public class HttpConnection implements Connection {
    public static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private org.jsoup.Connection.Request req;
    private org.jsoup.Connection.Response res;

    private static abstract class Base<T extends org.jsoup.Connection.Base> implements org.jsoup.Connection.Base<T> {
        Map<String, String> cookies;
        Map<String, String> headers;
        Method method;
        URL url;

        private Base() {
            this.headers = new LinkedHashMap();
            this.cookies = new LinkedHashMap();
        }

        public URL url() {
            return this.url;
        }

        public T url(URL url) {
            Validate.notNull(url, "URL must not be null");
            this.url = url;
            return this;
        }

        public Method method() {
            return this.method;
        }

        public T method(Method method) {
            Validate.notNull(method, "Method must not be null");
            this.method = method;
            return this;
        }

        public String header(String name) {
            Validate.notNull(name, "Header name must not be null");
            return getHeaderCaseInsensitive(name);
        }

        public T header(String name, String value) {
            Validate.notEmpty(name, "Header name must not be empty");
            Validate.notNull(value, "Header value must not be null");
            removeHeader(name);
            this.headers.put(name, value);
            return this;
        }

        public boolean hasHeader(String name) {
            Validate.notEmpty(name, "Header name must not be empty");
            return getHeaderCaseInsensitive(name) != null;
        }

        public boolean hasHeaderWithValue(String name, String value) {
            return hasHeader(name) && header(name).equalsIgnoreCase(value);
        }

        public T removeHeader(String name) {
            Validate.notEmpty(name, "Header name must not be empty");
            Entry<String, String> entry = scanHeaders(name);
            if (entry != null) {
                this.headers.remove(entry.getKey());
            }
            return this;
        }

        public Map<String, String> headers() {
            return this.headers;
        }

        private String getHeaderCaseInsensitive(String name) {
            Validate.notNull(name, "Header name must not be null");
            String value = (String) this.headers.get(name);
            if (value == null) {
                value = (String) this.headers.get(name.toLowerCase());
            }
            if (value != null) {
                return value;
            }
            Entry<String, String> entry = scanHeaders(name);
            if (entry != null) {
                return (String) entry.getValue();
            }
            return value;
        }

        private Entry<String, String> scanHeaders(String name) {
            String lc = name.toLowerCase();
            for (Entry<String, String> entry : this.headers.entrySet()) {
                if (((String) entry.getKey()).toLowerCase().equals(lc)) {
                    return entry;
                }
            }
            return null;
        }

        public String cookie(String name) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            return (String) this.cookies.get(name);
        }

        public T cookie(String name, String value) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            Validate.notNull(value, "Cookie value must not be null");
            this.cookies.put(name, value);
            return this;
        }

        public boolean hasCookie(String name) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            return this.cookies.containsKey(name);
        }

        public T removeCookie(String name) {
            Validate.notEmpty(name, "Cookie name must not be empty");
            this.cookies.remove(name);
            return this;
        }

        public Map<String, String> cookies() {
            return this.cookies;
        }
    }

    public static class KeyVal implements org.jsoup.Connection.KeyVal {
        private String key;
        private InputStream stream;
        private String value;

        public static KeyVal create(String key, String value) {
            return new KeyVal().key(key).value(value);
        }

        public static KeyVal create(String key, String filename, InputStream stream) {
            return new KeyVal().key(key).value(filename).inputStream(stream);
        }

        private KeyVal() {
        }

        public KeyVal key(String key) {
            Validate.notEmpty(key, "Data key must not be empty");
            this.key = key;
            return this;
        }

        public String key() {
            return this.key;
        }

        public KeyVal value(String value) {
            Validate.notNull(value, "Data value must not be null");
            this.value = value;
            return this;
        }

        public String value() {
            return this.value;
        }

        public KeyVal inputStream(InputStream inputStream) {
            Validate.notNull(this.value, "Data input stream must not be null");
            this.stream = inputStream;
            return this;
        }

        public InputStream inputStream() {
            return this.stream;
        }

        public boolean hasInputStream() {
            return this.stream != null;
        }

        public String toString() {
            return this.key + SimpleComparison.EQUAL_TO_OPERATION + this.value;
        }
    }

    public static class Request extends Base<org.jsoup.Connection.Request> implements org.jsoup.Connection.Request {
        private Collection<org.jsoup.Connection.KeyVal> data;
        private boolean followRedirects;
        private boolean ignoreContentType;
        private boolean ignoreHttpErrors;
        private int maxBodySizeBytes;
        private Parser parser;
        private boolean parserDefined;
        private String postDataCharset;
        private int timeoutMilliseconds;
        private boolean validateTSLCertificates;

        public /* bridge */ /* synthetic */ String cookie(String str) {
            return super.cookie(str);
        }

        public /* bridge */ /* synthetic */ Map cookies() {
            return super.cookies();
        }

        public /* bridge */ /* synthetic */ boolean hasCookie(String str) {
            return super.hasCookie(str);
        }

        public /* bridge */ /* synthetic */ boolean hasHeader(String str) {
            return super.hasHeader(str);
        }

        public /* bridge */ /* synthetic */ boolean hasHeaderWithValue(String str, String str2) {
            return super.hasHeaderWithValue(str, str2);
        }

        public /* bridge */ /* synthetic */ String header(String str) {
            return super.header(str);
        }

        public /* bridge */ /* synthetic */ Map headers() {
            return super.headers();
        }

        public /* bridge */ /* synthetic */ Method method() {
            return super.method();
        }

        public /* bridge */ /* synthetic */ URL url() {
            return super.url();
        }

        private Request() {
            super();
            this.ignoreHttpErrors = false;
            this.ignoreContentType = false;
            this.parserDefined = false;
            this.validateTSLCertificates = true;
            this.postDataCharset = HttpRequest.CHARSET_UTF8;
            this.timeoutMilliseconds = 3000;
            this.maxBodySizeBytes = WorksData.DOWNLOAD_CONFIRM_THRESHOLD;
            this.followRedirects = true;
            this.data = new ArrayList();
            this.method = Method.GET;
            this.headers.put(HttpRequest.HEADER_ACCEPT_ENCODING, HttpRequest.ENCODING_GZIP);
            this.parser = Parser.htmlParser();
        }

        public int timeout() {
            return this.timeoutMilliseconds;
        }

        public Request timeout(int millis) {
            Validate.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
            this.timeoutMilliseconds = millis;
            return this;
        }

        public int maxBodySize() {
            return this.maxBodySizeBytes;
        }

        public org.jsoup.Connection.Request maxBodySize(int bytes) {
            Validate.isTrue(bytes >= 0, "maxSize must be 0 (unlimited) or larger");
            this.maxBodySizeBytes = bytes;
            return this;
        }

        public boolean followRedirects() {
            return this.followRedirects;
        }

        public org.jsoup.Connection.Request followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public boolean ignoreHttpErrors() {
            return this.ignoreHttpErrors;
        }

        public boolean validateTLSCertificates() {
            return this.validateTSLCertificates;
        }

        public void validateTLSCertificates(boolean value) {
            this.validateTSLCertificates = value;
        }

        public org.jsoup.Connection.Request ignoreHttpErrors(boolean ignoreHttpErrors) {
            this.ignoreHttpErrors = ignoreHttpErrors;
            return this;
        }

        public boolean ignoreContentType() {
            return this.ignoreContentType;
        }

        public org.jsoup.Connection.Request ignoreContentType(boolean ignoreContentType) {
            this.ignoreContentType = ignoreContentType;
            return this;
        }

        public Request data(org.jsoup.Connection.KeyVal keyval) {
            Validate.notNull(keyval, "Key val must not be null");
            this.data.add(keyval);
            return this;
        }

        public Collection<org.jsoup.Connection.KeyVal> data() {
            return this.data;
        }

        public Request parser(Parser parser) {
            this.parser = parser;
            this.parserDefined = true;
            return this;
        }

        public Parser parser() {
            return this.parser;
        }

        public org.jsoup.Connection.Request postDataCharset(String charset) {
            Validate.notNull(charset, "Charset must not be null");
            if (Charset.isSupported(charset)) {
                this.postDataCharset = charset;
                return this;
            }
            throw new IllegalCharsetNameException(charset);
        }

        public String postDataCharset() {
            return this.postDataCharset;
        }
    }

    public static class Response extends Base<org.jsoup.Connection.Response> implements org.jsoup.Connection.Response {
        private static final String LOCATION = "Location";
        private static final int MAX_REDIRECTS = 20;
        private static SSLSocketFactory sslSocketFactory;
        private static final Pattern xmlContentTypeRxp;
        private ByteBuffer byteData;
        private String charset;
        private String contentType;
        private boolean executed;
        private int numRedirects;
        private org.jsoup.Connection.Request req;
        private int statusCode;
        private String statusMessage;

        public /* bridge */ /* synthetic */ String cookie(String str) {
            return super.cookie(str);
        }

        public /* bridge */ /* synthetic */ Map cookies() {
            return super.cookies();
        }

        public /* bridge */ /* synthetic */ boolean hasCookie(String str) {
            return super.hasCookie(str);
        }

        public /* bridge */ /* synthetic */ boolean hasHeader(String str) {
            return super.hasHeader(str);
        }

        public /* bridge */ /* synthetic */ boolean hasHeaderWithValue(String str, String str2) {
            return super.hasHeaderWithValue(str, str2);
        }

        public /* bridge */ /* synthetic */ String header(String str) {
            return super.header(str);
        }

        public /* bridge */ /* synthetic */ Map headers() {
            return super.headers();
        }

        public /* bridge */ /* synthetic */ Method method() {
            return super.method();
        }

        public /* bridge */ /* synthetic */ URL url() {
            return super.url();
        }

        static {
            xmlContentTypeRxp = Pattern.compile("(application|text)/\\w*\\+?xml.*");
        }

        Response() {
            super();
            this.executed = false;
            this.numRedirects = 0;
        }

        private Response(Response previousResponse) throws IOException {
            super();
            this.executed = false;
            this.numRedirects = 0;
            if (previousResponse != null) {
                this.numRedirects = previousResponse.numRedirects + 1;
                if (this.numRedirects >= MAX_REDIRECTS) {
                    throw new IOException(String.format("Too many redirects occurred trying to load URL %s", new Object[]{previousResponse.url()}));
                }
            }
        }

        static Response execute(org.jsoup.Connection.Request req) throws IOException {
            return execute(req, null);
        }

        static Response execute(org.jsoup.Connection.Request req, Response previousResponse) throws IOException {
            Validate.notNull(req, "Request must not be null");
            String protocol = req.url().getProtocol();
            if (protocol.equals("http") || protocol.equals(Constants.API_SCHEME)) {
                String mimeBoundary = null;
                if (!req.method().hasBody() && req.data().size() > 0) {
                    serialiseRequestUrl(req);
                } else if (req.method().hasBody()) {
                    mimeBoundary = setOutputContentType(req);
                }
                HttpURLConnection conn = createConnection(req);
                InputStream bodyStream;
                InputStream dataStream;
                try {
                    conn.connect();
                    if (conn.getDoOutput()) {
                        writePost(req, conn.getOutputStream(), mimeBoundary);
                    }
                    int status = conn.getResponseCode();
                    Response res = new Response(previousResponse);
                    res.setupFromConnection(conn, previousResponse);
                    res.req = req;
                    if (res.hasHeader(LOCATION) && req.followRedirects()) {
                        req.method(Method.GET);
                        req.data().clear();
                        String location = res.header(LOCATION);
                        if (!(location == null || !location.startsWith("http:/") || location.charAt(6) == Char.SLASH)) {
                            location = location.substring(6);
                        }
                        req.url(StringUtil.resolve(req.url(), HttpConnection.encodeUrl(location)));
                        for (Entry<String, String> cookie : res.cookies.entrySet()) {
                            req.cookie((String) cookie.getKey(), (String) cookie.getValue());
                        }
                        res = execute(req, res);
                        conn.disconnect();
                    } else {
                        if (status < Downloads.STATUS_SUCCESS || status >= Downloads.STATUS_BAD_REQUEST) {
                            if (!req.ignoreHttpErrors()) {
                                throw new HttpStatusException("HTTP error fetching URL", status, req.url().toString());
                            }
                        }
                        String contentType = res.contentType();
                        if (contentType == null || req.ignoreContentType() || contentType.startsWith("text/") || xmlContentTypeRxp.matcher(contentType).matches()) {
                            if (contentType != null && xmlContentTypeRxp.matcher(contentType).matches() && (req instanceof Request)) {
                                if (!((Request) req).parserDefined) {
                                    req.parser(Parser.xmlParser());
                                }
                            }
                            res.charset = DataUtil.getCharsetFromContentType(res.contentType);
                            if (conn.getContentLength() != 0) {
                                bodyStream = null;
                                dataStream = null;
                                dataStream = conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream();
                                bodyStream = res.hasHeaderWithValue(HttpConnection.CONTENT_ENCODING, HttpRequest.ENCODING_GZIP) ? new BufferedInputStream(new GZIPInputStream(dataStream)) : new BufferedInputStream(dataStream);
                                res.byteData = DataUtil.readToByteBuffer(bodyStream, req.maxBodySize());
                                if (bodyStream != null) {
                                    bodyStream.close();
                                }
                                if (dataStream != null) {
                                    dataStream.close();
                                }
                            } else {
                                res.byteData = DataUtil.emptyByteBuffer();
                            }
                            conn.disconnect();
                            res.executed = true;
                        } else {
                            throw new UnsupportedMimeTypeException("Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml", contentType, req.url().toString());
                        }
                    }
                    return res;
                } catch (Throwable th) {
                    conn.disconnect();
                }
            } else {
                throw new MalformedURLException("Only http & https protocols supported");
            }
        }

        public int statusCode() {
            return this.statusCode;
        }

        public String statusMessage() {
            return this.statusMessage;
        }

        public String charset() {
            return this.charset;
        }

        public String contentType() {
            return this.contentType;
        }

        public Document parse() throws IOException {
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before parsing response");
            Document doc = DataUtil.parseByteData(this.byteData, this.charset, this.url.toExternalForm(), this.req.parser());
            this.byteData.rewind();
            this.charset = doc.outputSettings().charset().name();
            return doc;
        }

        public String body() {
            String body;
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
            if (this.charset == null) {
                body = Charset.forName(HttpRequest.CHARSET_UTF8).decode(this.byteData).toString();
            } else {
                body = Charset.forName(this.charset).decode(this.byteData).toString();
            }
            this.byteData.rewind();
            return body;
        }

        public byte[] bodyAsBytes() {
            Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
            return this.byteData.array();
        }

        private static HttpURLConnection createConnection(org.jsoup.Connection.Request req) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) req.url().openConnection();
            conn.setRequestMethod(req.method().name());
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(req.timeout());
            conn.setReadTimeout(req.timeout());
            if ((conn instanceof HttpsURLConnection) && !req.validateTLSCertificates()) {
                initUnSecureTSL();
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
                ((HttpsURLConnection) conn).setHostnameVerifier(getInsecureVerifier());
            }
            if (req.method().hasBody()) {
                conn.setDoOutput(true);
            }
            if (req.cookies().size() > 0) {
                conn.addRequestProperty("Cookie", getRequestCookieString(req));
            }
            for (Entry<String, String> header : req.headers().entrySet()) {
                conn.addRequestProperty((String) header.getKey(), (String) header.getValue());
            }
            return conn;
        }

        private static HostnameVerifier getInsecureVerifier() {
            return new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
        }

        private static synchronized void initUnSecureTSL() throws IOException {
            synchronized (Response.class) {
                if (sslSocketFactory == null) {
                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }};
                    try {
                        SSLContext sslContext = SSLContext.getInstance("SSL");
                        sslContext.init(null, trustAllCerts, new SecureRandom());
                        sslSocketFactory = sslContext.getSocketFactory();
                    } catch (NoSuchAlgorithmException e) {
                        throw new IOException("Can't create unsecure trust manager");
                    } catch (KeyManagementException e2) {
                        throw new IOException("Can't create unsecure trust manager");
                    }
                }
            }
        }

        private void setupFromConnection(HttpURLConnection conn, org.jsoup.Connection.Response previousResponse) throws IOException {
            this.method = Method.valueOf(conn.getRequestMethod());
            this.url = conn.getURL();
            this.statusCode = conn.getResponseCode();
            this.statusMessage = conn.getResponseMessage();
            this.contentType = conn.getContentType();
            processResponseHeaders(conn.getHeaderFields());
            if (previousResponse != null) {
                for (Entry<String, String> prevCookie : previousResponse.cookies().entrySet()) {
                    if (!hasCookie((String) prevCookie.getKey())) {
                        cookie((String) prevCookie.getKey(), (String) prevCookie.getValue());
                    }
                }
            }
        }

        void processResponseHeaders(Map<String, List<String>> resHeaders) {
            for (Entry<String, List<String>> entry : resHeaders.entrySet()) {
                String name = (String) entry.getKey();
                if (name != null) {
                    List<String> values = (List) entry.getValue();
                    if (name.equalsIgnoreCase("Set-Cookie")) {
                        for (String value : values) {
                            if (value != null) {
                                TokenQueue cd = new TokenQueue(value);
                                String cookieName = cd.chompTo(SimpleComparison.EQUAL_TO_OPERATION).trim();
                                String cookieVal = cd.consumeTo(";").trim();
                                if (cookieName.length() > 0) {
                                    cookie(cookieName, cookieVal);
                                }
                            }
                        }
                    } else if (!values.isEmpty()) {
                        header(name, (String) values.get(0));
                    }
                }
            }
        }

        private static String setOutputContentType(org.jsoup.Connection.Request req) {
            boolean needsMulti = false;
            for (org.jsoup.Connection.KeyVal keyVal : req.data()) {
                if (keyVal.hasInputStream()) {
                    needsMulti = true;
                    break;
                }
            }
            if (needsMulti) {
                String bound = DataUtil.mimeBoundary();
                req.header(HttpConnection.CONTENT_TYPE, "multipart/form-data; boundary=" + bound);
                return bound;
            }
            req.header(HttpConnection.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=" + req.postDataCharset());
            return null;
        }

        private static void writePost(org.jsoup.Connection.Request req, OutputStream outputStream, String bound) throws IOException {
            Collection<org.jsoup.Connection.KeyVal> data = req.data();
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(outputStream, HttpRequest.CHARSET_UTF8));
            if (bound != null) {
                for (org.jsoup.Connection.KeyVal keyVal : data) {
                    w.write("--");
                    w.write(bound);
                    w.write(Char.CRLF);
                    w.write("Content-Disposition: form-data; name=\"");
                    w.write(HttpConnection.encodeMimeName(keyVal.key()));
                    w.write("\"");
                    if (keyVal.hasInputStream()) {
                        w.write("; filename=\"");
                        w.write(HttpConnection.encodeMimeName(keyVal.value()));
                        w.write("\"\r\nContent-Type: application/octet-stream\r\n\r\n");
                        w.flush();
                        DataUtil.crossStreams(keyVal.inputStream(), outputStream);
                        outputStream.flush();
                    } else {
                        w.write("\r\n\r\n");
                        w.write(keyVal.value());
                    }
                    w.write(Char.CRLF);
                }
                w.write("--");
                w.write(bound);
                w.write("--");
            } else {
                boolean first = true;
                for (org.jsoup.Connection.KeyVal keyVal2 : data) {
                    if (first) {
                        first = false;
                    } else {
                        w.append('&');
                    }
                    w.write(URLEncoder.encode(keyVal2.key(), req.postDataCharset()));
                    w.write(61);
                    w.write(URLEncoder.encode(keyVal2.value(), req.postDataCharset()));
                }
            }
            w.close();
        }

        private static String getRequestCookieString(org.jsoup.Connection.Request req) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Entry<String, String> cookie : req.cookies().entrySet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append("; ");
                }
                sb.append((String) cookie.getKey()).append('=').append((String) cookie.getValue());
            }
            return sb.toString();
        }

        private static void serialiseRequestUrl(org.jsoup.Connection.Request req) throws IOException {
            URL in = req.url();
            StringBuilder url = new StringBuilder();
            boolean first = true;
            url.append(in.getProtocol()).append("://").append(in.getAuthority()).append(in.getPath()).append("?");
            if (in.getQuery() != null) {
                url.append(in.getQuery());
                first = false;
            }
            for (org.jsoup.Connection.KeyVal keyVal : req.data()) {
                if (first) {
                    first = false;
                } else {
                    url.append('&');
                }
                url.append(URLEncoder.encode(keyVal.key(), HttpRequest.CHARSET_UTF8)).append('=').append(URLEncoder.encode(keyVal.value(), HttpRequest.CHARSET_UTF8));
            }
            req.url(new URL(url.toString()));
            req.data().clear();
        }
    }

    public static Connection connect(String url) {
        Connection con = new HttpConnection();
        con.url(url);
        return con;
    }

    public static Connection connect(URL url) {
        Connection con = new HttpConnection();
        con.url(url);
        return con;
    }

    private static String encodeUrl(String url) {
        if (url == null) {
            return null;
        }
        return url.replaceAll(" ", "%20");
    }

    private static String encodeMimeName(String val) {
        if (val == null) {
            return null;
        }
        return val.replaceAll("\"", "%22");
    }

    private HttpConnection() {
        this.req = new Request();
        this.res = new Response();
    }

    public Connection url(URL url) {
        this.req.url(url);
        return this;
    }

    public Connection url(String url) {
        Validate.notEmpty(url, "Must supply a valid URL");
        try {
            this.req.url(new URL(encodeUrl(url)));
            return this;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + url, e);
        }
    }

    public Connection userAgent(String userAgent) {
        Validate.notNull(userAgent, "User agent must not be null");
        this.req.header(HttpRequest.HEADER_USER_AGENT, userAgent);
        return this;
    }

    public Connection timeout(int millis) {
        this.req.timeout(millis);
        return this;
    }

    public Connection maxBodySize(int bytes) {
        this.req.maxBodySize(bytes);
        return this;
    }

    public Connection followRedirects(boolean followRedirects) {
        this.req.followRedirects(followRedirects);
        return this;
    }

    public Connection referrer(String referrer) {
        Validate.notNull(referrer, "Referrer must not be null");
        this.req.header(HttpRequest.HEADER_REFERER, referrer);
        return this;
    }

    public Connection method(Method method) {
        this.req.method(method);
        return this;
    }

    public Connection ignoreHttpErrors(boolean ignoreHttpErrors) {
        this.req.ignoreHttpErrors(ignoreHttpErrors);
        return this;
    }

    public Connection ignoreContentType(boolean ignoreContentType) {
        this.req.ignoreContentType(ignoreContentType);
        return this;
    }

    public Connection validateTLSCertificates(boolean value) {
        this.req.validateTLSCertificates(value);
        return this;
    }

    public Connection data(String key, String value) {
        this.req.data(KeyVal.create(key, value));
        return this;
    }

    public Connection data(String key, String filename, InputStream inputStream) {
        this.req.data(KeyVal.create(key, filename, inputStream));
        return this;
    }

    public Connection data(Map<String, String> data) {
        Validate.notNull(data, "Data map must not be null");
        for (Entry<String, String> entry : data.entrySet()) {
            this.req.data(KeyVal.create((String) entry.getKey(), (String) entry.getValue()));
        }
        return this;
    }

    public Connection data(String... keyvals) {
        Validate.notNull(keyvals, "Data key value pairs must not be null");
        Validate.isTrue(keyvals.length % 2 == 0, "Must supply an even number of key value pairs");
        for (int i = 0; i < keyvals.length; i += 2) {
            String key = keyvals[i];
            String value = keyvals[i + 1];
            Validate.notEmpty(key, "Data key must not be empty");
            Validate.notNull(value, "Data value must not be null");
            this.req.data(KeyVal.create(key, value));
        }
        return this;
    }

    public Connection data(Collection<org.jsoup.Connection.KeyVal> data) {
        Validate.notNull(data, "Data collection must not be null");
        for (org.jsoup.Connection.KeyVal entry : data) {
            this.req.data(entry);
        }
        return this;
    }

    public Connection header(String name, String value) {
        this.req.header(name, value);
        return this;
    }

    public Connection cookie(String name, String value) {
        this.req.cookie(name, value);
        return this;
    }

    public Connection cookies(Map<String, String> cookies) {
        Validate.notNull(cookies, "Cookie map must not be null");
        for (Entry<String, String> entry : cookies.entrySet()) {
            this.req.cookie((String) entry.getKey(), (String) entry.getValue());
        }
        return this;
    }

    public Connection parser(Parser parser) {
        this.req.parser(parser);
        return this;
    }

    public Document get() throws IOException {
        this.req.method(Method.GET);
        execute();
        return this.res.parse();
    }

    public Document post() throws IOException {
        this.req.method(Method.POST);
        execute();
        return this.res.parse();
    }

    public org.jsoup.Connection.Response execute() throws IOException {
        this.res = Response.execute(this.req);
        return this.res;
    }

    public org.jsoup.Connection.Request request() {
        return this.req;
    }

    public Connection request(org.jsoup.Connection.Request request) {
        this.req = request;
        return this;
    }

    public org.jsoup.Connection.Response response() {
        return this.res;
    }

    public Connection response(org.jsoup.Connection.Response response) {
        this.res = response;
        return this;
    }

    public Connection postDataCharset(String charset) {
        this.req.postDataCharset(charset);
        return this;
    }
}
