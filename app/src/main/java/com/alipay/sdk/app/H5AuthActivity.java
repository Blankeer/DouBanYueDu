package com.alipay.sdk.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.alipay.sdk.util.k;
import com.douban.book.reader.fragment.share.ShareUrlEditFragment_;
import io.realm.internal.Table;
import java.lang.reflect.Method;
import java.net.URLDecoder;

public class H5AuthActivity extends Activity {
    private WebView a;
    private com.alipay.sdk.widget.a b;
    private Handler c;
    private boolean d;
    private boolean e;
    private Runnable f;

    private class a extends WebViewClient {
        final /* synthetic */ H5AuthActivity a;

        private a(H5AuthActivity h5AuthActivity) {
            this.a = h5AuthActivity;
        }

        public final void onReceivedError(WebView webView, int i, String str, String str2) {
            this.a.e = true;
            super.onReceivedError(webView, i, str, str2);
        }

        public final void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            if (this.a.d) {
                sslErrorHandler.proceed();
                this.a.d = false;
                return;
            }
            this.a.runOnUiThread(new c(this, sslErrorHandler));
        }

        public final boolean shouldOverrideUrlLoading(WebView webView, String str) {
            if (TextUtils.equals(str, com.alipay.sdk.cons.a.n) || TextUtils.equals(str, com.alipay.sdk.cons.a.o)) {
                l.a = l.a();
                this.a.finish();
            } else if (str.startsWith(com.alipay.sdk.cons.a.m)) {
                try {
                    String substring = str.substring(str.indexOf(com.alipay.sdk.cons.a.m) + 24);
                    int parseInt = Integer.parseInt(substring.substring(substring.lastIndexOf(com.alipay.sdk.cons.a.p) + 10));
                    m a;
                    if (parseInt == m.SUCCEEDED.g) {
                        String decode = URLDecoder.decode(str);
                        decode = decode.substring(decode.indexOf(com.alipay.sdk.cons.a.m) + 24, decode.lastIndexOf(com.alipay.sdk.cons.a.p));
                        a = m.a(parseInt);
                        l.a = l.a(a.g, a.h, decode);
                    } else {
                        a = m.a(m.FAILED.g);
                        l.a = l.a(a.g, a.h, Table.STRING_DEFAULT_VALUE);
                    }
                } catch (Exception e) {
                    l.a = l.b();
                }
                this.a.runOnUiThread(new f(this));
            } else {
                webView.loadUrl(str);
            }
            return true;
        }

        public final void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            H5AuthActivity.c(this.a);
            this.a.c.postDelayed(this.a.f, 30000);
            super.onPageStarted(webView, str, bitmap);
        }

        public final void onPageFinished(WebView webView, String str) {
            H5AuthActivity.f(this.a);
            this.a.c.removeCallbacks(this.a.f);
        }
    }

    public H5AuthActivity() {
        this.f = new b(this);
    }

    static /* synthetic */ void c(H5AuthActivity h5AuthActivity) {
        if (h5AuthActivity.b == null) {
            h5AuthActivity.b = new com.alipay.sdk.widget.a(h5AuthActivity);
        }
        try {
            h5AuthActivity.b.b();
        } catch (Exception e) {
            h5AuthActivity.b = null;
        }
    }

    static /* synthetic */ void f(H5AuthActivity h5AuthActivity) {
        if (h5AuthActivity.b != null && h5AuthActivity.b.a()) {
            h5AuthActivity.b.c();
        }
        h5AuthActivity.b = null;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                finish();
                return;
            }
            try {
                String string = extras.getString(ShareUrlEditFragment_.URL_ARG);
                if (k.a(string)) {
                    Method method;
                    super.requestWindowFeature(1);
                    this.c = new Handler(getMainLooper());
                    View linearLayout = new LinearLayout(getApplicationContext());
                    LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
                    linearLayout.setOrientation(1);
                    setContentView(linearLayout, layoutParams);
                    this.a = new WebView(getApplicationContext());
                    layoutParams.weight = 1.0f;
                    this.a.setVisibility(0);
                    linearLayout.addView(this.a, layoutParams);
                    WebSettings settings = this.a.getSettings();
                    settings.setUserAgentString(settings.getUserAgentString() + k.c(getApplicationContext()));
                    settings.setRenderPriority(RenderPriority.HIGH);
                    settings.setSupportMultipleWindows(true);
                    settings.setJavaScriptEnabled(true);
                    settings.setSavePassword(false);
                    settings.setJavaScriptCanOpenWindowsAutomatically(true);
                    settings.setMinimumFontSize(settings.getMinimumFontSize() + 8);
                    settings.setAllowFileAccess(false);
                    settings.setTextSize(TextSize.NORMAL);
                    this.a.setVerticalScrollbarOverlay(true);
                    this.a.setWebViewClient(new a());
                    this.a.setDownloadListener(new a(this));
                    this.a.loadUrl(string);
                    if (VERSION.SDK_INT >= 7) {
                        try {
                            method = this.a.getSettings().getClass().getMethod("setDomStorageEnabled", new Class[]{Boolean.TYPE});
                            if (method != null) {
                                method.invoke(this.a.getSettings(), new Object[]{Boolean.valueOf(true)});
                            }
                        } catch (Exception e) {
                        }
                    }
                    try {
                        method = this.a.getClass().getMethod("removeJavascriptInterface", new Class[0]);
                        if (method != null) {
                            method.invoke(this.a, new Object[]{"searchBoxJavaBridge_"});
                            method.invoke(this.a, new Object[]{"accessibility"});
                            method.invoke(this.a, new Object[]{"accessibilityTraversal"});
                            return;
                        }
                        return;
                    } catch (Exception e2) {
                        return;
                    }
                }
                finish();
            } catch (Exception e3) {
                finish();
            }
        } catch (Exception e4) {
            finish();
        }
    }

    public void onBackPressed() {
        if (!this.a.canGoBack()) {
            l.a = l.a();
            finish();
        } else if (this.e) {
            m a = m.a(m.NETWORK_ERROR.g);
            l.a = l.a(a.g, a.h, Table.STRING_DEFAULT_VALUE);
            finish();
        }
    }

    public void finish() {
        Object obj = AuthTask.a;
        synchronized (obj) {
            try {
                obj.notify();
            } catch (Exception e) {
            }
        }
        super.finish();
    }

    private static void a() {
        Object obj = AuthTask.a;
        synchronized (obj) {
            try {
                obj.notify();
            } catch (Exception e) {
            }
        }
    }

    private void b() {
        if (this.b == null) {
            this.b = new com.alipay.sdk.widget.a(this);
        }
        try {
            this.b.b();
        } catch (Exception e) {
            this.b = null;
        }
    }

    private void c() {
        if (this.b != null && this.b.a()) {
            this.b.c();
        }
        this.b = null;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.a != null) {
            this.a.removeAllViews();
            try {
                this.a.destroy();
            } catch (Throwable th) {
            }
            this.a = null;
            this.a = null;
        }
    }
}
