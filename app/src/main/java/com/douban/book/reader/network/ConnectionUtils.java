package com.douban.book.reader.network;

import android.os.Build;
import android.os.Build.VERSION;
import android.support.v4.os.EnvironmentCompat;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.manager.SessionManager_;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ConnectionUtils {
    public static final int CONNECTION_TIMEOUT = 30000;
    private static final String TAG;
    private static String sUserAgent;

    static {
        TAG = ConnectionUtils.class.getSimpleName();
        sUserAgent = null;
        if (AppInfo.isDebug()) {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            } catch (NoSuchAlgorithmException e) {
                Logger.e(TAG, e);
            } catch (KeyManagementException e2) {
                Logger.e(TAG, e2);
            }
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
    }

    public static String getUserAgent() {
        if (StringUtils.isEmpty(sUserAgent)) {
            String model = Build.MODEL;
            String androidVersion = VERSION.RELEASE;
            String arkVersion = AppInfo.getVersionName();
            String channel = AppInfo.getChannelName();
            String specialEvent = AppInfo.getSpecialEvent();
            if (StringUtils.isNotEmpty(specialEvent) && !EnvironmentCompat.MEDIA_UNKNOWN.equals(specialEvent)) {
                channel = channel + "-" + specialEvent;
            }
            sUserAgent = String.format("\"%s\" (Android %s); Ark (%s) %s", new Object[]{model, androidVersion, channel, arkVersion});
        }
        return sUserAgent;
    }

    public static void configURLConnection(HttpURLConnection conn) {
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setRequestProperty(HttpRequest.HEADER_USER_AGENT, getUserAgent());
        if (StringUtils.equals(Pref.ofApp().getString(Key.APP_API_HOST, Constants.DOUBAN_HOST), Constants.DOUBAN_HOST)) {
            conn.setRequestProperty("Host", AppUri.AUTHORITY_WEB);
        }
        conn.setRequestProperty("X-UDID", Utils.getDeviceUDID());
        conn.setRequestProperty("X-Device-Time", DateUtils.formatIso8601(new Date()));
    }

    public static void addAccessToken(HttpURLConnection conn) {
        String accessToken = SessionManager_.getInstance_(App.get()).getAccessToken();
        if (!StringUtils.isEmpty(accessToken)) {
            conn.setRequestProperty(HttpRequest.HEADER_AUTHORIZATION, "Bearer " + accessToken);
        }
    }
}
