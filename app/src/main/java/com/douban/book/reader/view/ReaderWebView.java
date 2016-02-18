package com.douban.book.reader.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.manager.SessionManager_;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.Utils;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ReaderWebView extends WebView {
    private static final String JS_FORMATTER = "javascript:$(window).trigger('%s'%s)";
    private static final String TAG;
    private ReaderWebViewLoadingListener mLoadingListener;
    private String mOriginalUA;

    public class ReaderWebViewClient extends WebViewClient {
        public ReaderWebViewClient(Context c) {
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (ReaderWebView.this.mLoadingListener != null) {
                ReaderWebView.this.mLoadingListener.onWebViewLoadingStarted();
            }
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (ReaderWebView.this.mLoadingListener != null) {
                ReaderWebView.this.mLoadingListener.onWebViewLoadingFinished();
            }
        }
    }

    public interface ReaderWebViewLoadingListener {
        void onWebViewLoadingFinished();

        void onWebViewLoadingStarted();
    }

    static {
        TAG = ReaderWebView.class.getSimpleName();
    }

    public ReaderWebView(Context context) {
        super(context);
        init();
    }

    public ReaderWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setLoadingListener(ReaderWebViewLoadingListener loadingListener) {
        this.mLoadingListener = loadingListener;
    }

    private void init() {
        initCookieIfNeeded();
        WebSettings settings = getSettings();
        this.mOriginalUA = settings.getUserAgentString();
        setOriginalUA();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(2);
        setMixedContentMode(settings);
        setWebViewClient(new ReaderWebViewClient(getContext()));
        if (DebugSwitch.on(Key.APP_DEBUG_ALLOW_WEBVIEW_DEBUGGING) && VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @TargetApi(21)
    private void setMixedContentMode(WebSettings settings) {
        if (VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(2);
        }
    }

    public void setOriginalUA() {
        getSettings().setUserAgentString(String.format("%s [Ark/%s]", new Object[]{this.mOriginalUA, AppInfo.getVersionName()}));
    }

    public void loadJS(String functionName, Object... params) {
        StringBuilder paramBuilder = new StringBuilder();
        if (params.length > 0) {
            paramBuilder.append(",[");
            for (Object param : params) {
                if (param instanceof CharSequence) {
                    paramBuilder.append(String.format("'%s'", new Object[]{param}));
                } else {
                    paramBuilder.append(param.toString());
                }
                paramBuilder.append(",");
            }
            paramBuilder.deleteCharAt(paramBuilder.length() - 1);
            paramBuilder.append("]");
        }
        loadUrl(String.format(JS_FORMATTER, new Object[]{functionName, paramBuilder.toString()}));
        Logger.d(TAG, "loadJs() %s", finalJs);
    }

    public void loadUrlWithOAuthRedirect(String url) {
        if (!StringUtils.isEmpty(url)) {
            Map<String, String> headers = new HashMap();
            headers.put(HttpRequest.HEADER_AUTHORIZATION, "Bearer " + SessionManager_.getInstance_(App.get()).getAccessToken());
            try {
                loadUrl(String.format("%s/accounts/auth2_redir?apikey=%s&url=%s", new Object[]{Pref.ofApp().getString(Key.APP_OAUTH_BASE_URL, Constants.OAUTH2_AUTH_URI_BASE), Pref.ofApp().getString(Key.APP_CLIENT_ID, Constants.APP_KEY), URLEncoder.encode(url, HttpRequest.CHARSET_UTF8)}), headers);
            } catch (UnsupportedEncodingException e) {
                loadUrl(url);
            }
        }
    }

    private void initCookieIfNeeded() {
        String domain = Pref.ofApp().getString(Key.ARK_HOST, AppUri.AUTHORITY_WEB);
        CookieManager cookieManager = CookieManager.getInstance();
        Logger.d(Tag.COOKIES, "cookie: %s", cookieManager.getCookie(domain));
        String udid = Utils.getDeviceUDID();
        int versionCode = AppInfo.getVersionCode();
        r5 = new CharSequence[3];
        r5[0] = String.format("version=%s", new Object[]{Integer.valueOf(versionCode)});
        r5[1] = "read_app=android";
        r5[2] = String.format("udid=%s", new Object[]{udid});
        if (!StringUtils.containsAll(cookieManager.getCookie(domain), r5)) {
            cookieManager.setCookie(domain, String.format("version=%s", new Object[]{Integer.valueOf(versionCode)}));
            cookieManager.setCookie(domain, String.format("udid=%s", new Object[]{udid}));
            cookieManager.setCookie(domain, "read_app=android");
            cookieManager.setCookie(domain, String.format("domain=%s", new Object[]{domain}));
            Logger.d(Tag.COOKIES, "init cookie: %s", cookieManager.getCookie(domain));
        }
    }
}
