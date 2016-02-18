package io.fabric.sdk.android.services.network;

import io.fabric.sdk.android.DefaultLogger;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Logger;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import u.aly.dx;

public class DefaultHttpRequestFactory implements HttpRequestFactory {
    private static final String HTTPS = "https";
    private boolean attemptedSslInit;
    private final Logger logger;
    private PinningInfoProvider pinningInfo;
    private SSLSocketFactory sslSocketFactory;

    /* renamed from: io.fabric.sdk.android.services.network.DefaultHttpRequestFactory.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod;

        static {
            $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod = new int[HttpMethod.values().length];
            try {
                $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod[HttpMethod.GET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod[HttpMethod.POST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod[HttpMethod.PUT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$io$fabric$sdk$android$services$network$HttpMethod[HttpMethod.DELETE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public DefaultHttpRequestFactory() {
        this(new DefaultLogger());
    }

    public DefaultHttpRequestFactory(Logger logger) {
        this.logger = logger;
    }

    public PinningInfoProvider getPinningInfoProvider() {
        return this.pinningInfo;
    }

    public void setPinningInfoProvider(PinningInfoProvider pinningInfo) {
        if (this.pinningInfo != pinningInfo) {
            this.pinningInfo = pinningInfo;
            resetSSLSocketFactory();
        }
    }

    private synchronized void resetSSLSocketFactory() {
        this.attemptedSslInit = false;
        this.sslSocketFactory = null;
    }

    public HttpRequest buildHttpRequest(HttpMethod method, String url) {
        return buildHttpRequest(method, url, Collections.emptyMap());
    }

    public HttpRequest buildHttpRequest(HttpMethod method, String url, Map<String, String> queryParams) {
        HttpRequest httpRequest;
        switch (AnonymousClass1.$SwitchMap$io$fabric$sdk$android$services$network$HttpMethod[method.ordinal()]) {
            case dx.b /*1*/:
                httpRequest = HttpRequest.get((CharSequence) url, (Map) queryParams, true);
                break;
            case dx.c /*2*/:
                httpRequest = HttpRequest.post((CharSequence) url, (Map) queryParams, true);
                break;
            case dx.d /*3*/:
                httpRequest = HttpRequest.put((CharSequence) url);
                break;
            case dx.e /*4*/:
                httpRequest = HttpRequest.delete((CharSequence) url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method!");
        }
        if (isHttps(url) && this.pinningInfo != null) {
            SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
            if (sslSocketFactory != null) {
                ((HttpsURLConnection) httpRequest.getConnection()).setSSLSocketFactory(sslSocketFactory);
            }
        }
        return httpRequest;
    }

    private boolean isHttps(String url) {
        return url != null && url.toLowerCase(Locale.US).startsWith(HTTPS);
    }

    private synchronized SSLSocketFactory getSSLSocketFactory() {
        if (this.sslSocketFactory == null && !this.attemptedSslInit) {
            this.sslSocketFactory = initSSLSocketFactory();
        }
        return this.sslSocketFactory;
    }

    private synchronized SSLSocketFactory initSSLSocketFactory() {
        SSLSocketFactory sslSocketFactory;
        this.attemptedSslInit = true;
        try {
            sslSocketFactory = NetworkUtils.getSSLSocketFactory(this.pinningInfo);
            this.logger.d(Fabric.TAG, "Custom SSL pinning enabled");
        } catch (Exception e) {
            this.logger.e(Fabric.TAG, "Exception while validating pinned certs", e);
            sslSocketFactory = null;
        }
        return sslSocketFactory;
    }
}
