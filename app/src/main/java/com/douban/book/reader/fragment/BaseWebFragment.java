package com.douban.book.reader.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.UriUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.LoadErrorPageView.RefreshClickListener;
import com.douban.book.reader.view.LoadErrorPageView_;
import com.douban.book.reader.view.ReaderWebView;

public class BaseWebFragment extends BaseRefreshFragment {
    private static final String DARK_THEME_CLASS = "theme-dark";
    private ViewGroup mLayoutBase;
    private boolean mTitleOverridden;
    private EditText mUrl;
    private ReaderWebView mWebView;

    /* renamed from: com.douban.book.reader.fragment.BaseWebFragment.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ String val$functionName;
        final /* synthetic */ Object[] val$params;

        AnonymousClass2(String str, Object[] objArr) {
            this.val$functionName = str;
            this.val$params = objArr;
        }

        public void run() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.val$functionName).append("(").append(StringUtils.join((CharSequence) ", ", this.val$params)).append(")");
            BaseWebFragment.this.mWebView.loadUrl("javascript:" + builder.toString());
        }
    }

    /* renamed from: com.douban.book.reader.fragment.BaseWebFragment.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ String val$jsCode;

        AnonymousClass3(String str) {
            this.val$jsCode = str;
        }

        public void run() {
            BaseWebFragment.this.mWebView.loadUrl(this.val$jsCode);
        }
    }

    public class BaseWebChromeClient extends WebChromeClient {

        /* renamed from: com.douban.book.reader.fragment.BaseWebFragment.BaseWebChromeClient.1 */
        class AnonymousClass1 implements OnClickListener {
            final /* synthetic */ JsResult val$result;

            AnonymousClass1(JsResult jsResult) {
                this.val$result = jsResult;
            }

            public void onClick(DialogInterface dialog, int which) {
                this.val$result.confirm();
            }
        }

        /* renamed from: com.douban.book.reader.fragment.BaseWebFragment.BaseWebChromeClient.2 */
        class AnonymousClass2 implements OnClickListener {
            final /* synthetic */ JsResult val$result;

            AnonymousClass2(JsResult jsResult) {
                this.val$result = jsResult;
            }

            public void onClick(DialogInterface dialog, int which) {
                this.val$result.cancel();
            }
        }

        /* renamed from: com.douban.book.reader.fragment.BaseWebFragment.BaseWebChromeClient.3 */
        class AnonymousClass3 implements OnClickListener {
            final /* synthetic */ JsResult val$result;

            AnonymousClass3(JsResult jsResult) {
                this.val$result = jsResult;
            }

            public void onClick(DialogInterface dialog, int which) {
                this.val$result.confirm();
            }
        }

        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            try {
                new Builder().setMessage((CharSequence) message).setPositiveButton(17039370, new AnonymousClass1(result)).create().show();
            } catch (Exception e) {
                Logger.e(BaseWebFragment.this.TAG, e);
            }
            return true;
        }

        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            try {
                new Builder().setMessage((CharSequence) message).setPositiveButton(17039370, new AnonymousClass3(result)).setNegativeButton(17039360, new AnonymousClass2(result)).create().show();
            } catch (Exception e) {
                Logger.e(BaseWebFragment.this.TAG, e);
            }
            return true;
        }

        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (!BaseWebFragment.this.mTitleOverridden && !StringUtils.equals(BaseWebFragment.this.mWebView.getUrl(), (CharSequence) title)) {
                super.setTitle((CharSequence) title);
            }
        }

        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (consoleMessage != null) {
                Logger.d(Tag.JSCONSOLE, "[%s] %s:%s %s", consoleMessage.messageLevel(), consoleMessage.sourceId(), Integer.valueOf(consoleMessage.lineNumber()), consoleMessage.message());
            }
            return super.onConsoleMessage(consoleMessage);
        }
    }

    private class BaseWebViewClient extends WebViewClient {

        /* renamed from: com.douban.book.reader.fragment.BaseWebFragment.BaseWebViewClient.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ String val$desc;
            final /* synthetic */ int val$errorCode;
            final /* synthetic */ String val$failingUrl;

            AnonymousClass1(int i, String str, String str2) {
                this.val$errorCode = i;
                this.val$desc = str;
                this.val$failingUrl = str2;
            }

            public void run() {
                BaseWebFragment.this.onReceivedError(this.val$errorCode, this.val$desc, this.val$failingUrl);
            }
        }

        private BaseWebViewClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return BaseWebFragment.this.shouldOverrideUrlLoading(url);
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (VERSION.SDK_INT < 11 && BaseWebFragment.this.shouldOverrideUrlLoading(url)) {
                view.stopLoading();
            }
            BaseWebFragment.this.showLoadingDialog();
            if (DebugSwitch.on(Key.APP_DEBUG_SHOW_WEBVIEW_URL) && BaseWebFragment.this.mUrl != null) {
                BaseWebFragment.this.mUrl.setText(url);
            }
            BaseWebFragment.this.onPageStarted(url);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            BaseWebFragment.this.dismissLoadingDialog();
            BaseWebFragment.this.onPageFinished(url);
        }

        public void onReceivedError(WebView view, int errorCode, String desc, String failingUrl) {
            Logger.d(BaseWebFragment.this.TAG, "Error %d: %s url=%s", Integer.valueOf(errorCode), desc, failingUrl);
            App.get().runOnUiThread(new AnonymousClass1(errorCode, desc, failingUrl));
        }
    }

    public BaseFragment setTitle(CharSequence title) {
        this.mTitleOverridden = true;
        return super.setTitle(title);
    }

    public String getCurrentUrl() {
        return this.mWebView.getUrl();
    }

    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_web, container, false);
        if (view != null) {
            this.mLayoutBase = (ViewGroup) view.findViewById(R.id.layout_base);
            this.mWebView = (ReaderWebView) view.findViewById(R.id.webview);
            this.mWebView.setWebViewClient(new BaseWebViewClient());
            this.mWebView.setWebChromeClient(new BaseWebChromeClient());
            setScrollableChild(this.mWebView);
            if (DebugSwitch.on(Key.APP_DEBUG_SHOW_WEBVIEW_URL)) {
                view.findViewById(R.id.url_bar).setVisibility(0);
                this.mUrl = (EditText) view.findViewById(R.id.input_url);
                view.findViewById(R.id.btn_go).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        BaseWebFragment.this.mWebView.loadUrl(BaseWebFragment.this.mUrl.getText().toString());
                    }
                });
            } else {
                view.findViewById(R.id.url_bar).setVisibility(8);
            }
        }
        enableJavascript("key_bridge");
        return view;
    }

    protected void loadUrl(Uri url) {
        loadUrl(String.valueOf(url));
    }

    protected void loadUrl(String url) {
        if (this.mWebView == null) {
            return;
        }
        if (UriUtils.isInDoubanDomain(url)) {
            if (!Theme.isNight()) {
                ViewUtils.visible(this.mWebView);
            }
            this.mWebView.loadUrlWithOAuthRedirect(url);
            return;
        }
        this.mWebView.loadUrl(url);
    }

    public void onRefresh() {
        this.mWebView.reload();
    }

    protected void enableJavascript(String name) {
        this.mWebView.addJavascriptInterface(this, name);
    }

    protected void invokeJs(String functionName, Object... params) {
        App.get().runOnUiThread(new AnonymousClass2(functionName, params));
    }

    protected void addArkFunction(String functionName, String functionBodyFormat, Object... params) {
        String functionBody = String.format(functionBodyFormat, params);
        App.get().runOnUiThread(new AnonymousClass3(String.format("javascript:window.ark_app=window.ark_app || {};window.ark_app.%s = function (%s) { %s }", new Object[]{functionName, StringUtils.join(',', params), functionBody})));
    }

    protected boolean shouldOverrideUrlLoading(String url) {
        if (!AppUri.canBeOpenedByApp(url) || AppUri.canOnlyBeOpenedInWebView(url)) {
            return false;
        }
        return PageOpenHelper.from((Fragment) this).open(url);
    }

    protected void onPageStarted(String url) {
    }

    protected void onPageFinished(String url) {
        updateWebContentTheme();
        postDelayed(new Runnable() {
            public void run() {
                ViewUtils.visible(BaseWebFragment.this.mWebView);
            }
        }, StatConstant.DEFAULT_MAX_EVENT_COUNT);
    }

    protected void onReceivedError(int errorCode, String desc, String failingUrl) {
        LoadErrorPageView_.build(App.get()).refreshClickListener(new RefreshClickListener() {
            public void onClick() {
                BaseWebFragment.this.mWebView.reload();
            }
        }).attachTo(this.mLayoutBase).message(String.format("%s%n%s (%s)", new Object[]{Res.getString(R.string.general_load_failed), desc, Integer.valueOf(errorCode)})).hideToShelfButton().show();
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        updateWebContentTheme();
    }

    private void updateWebContentTheme() {
        if (Theme.isNight()) {
            addClass(DARK_THEME_CLASS);
        } else {
            removeClass(DARK_THEME_CLASS);
        }
    }

    protected void addClass(String className) {
        if (this.mWebView != null) {
            this.mWebView.loadUrl(String.format("javascript:document.querySelector('html').classList.add('%s')", new Object[]{className}));
        }
    }

    protected void removeClass(String className) {
        if (this.mWebView != null) {
            this.mWebView.loadUrl(String.format("javascript:document.querySelector('html').classList.remove('%s')", new Object[]{className}));
        }
    }

    public void onBackPressed() {
        loadUrl("javascript:f = function() {    try {        var _event = $.Event('backbutton');        $(document).trigger(_event);        return _event.defaultPrevented;    } catch (error) {        return false;    }}");
        loadUrl("javascript:key_bridge.performBackKey(f())");
    }

    @JavascriptInterface
    public void performBackKey(boolean backKeyProcessed) {
        if (!backKeyProcessed) {
            App.get().runOnUiThread(new Runnable() {
                public void run() {
                    if (BaseWebFragment.this.mWebView == null || !BaseWebFragment.this.mWebView.canGoBack()) {
                        super.onBackPressed();
                    } else {
                        BaseWebFragment.this.mWebView.goBack();
                    }
                }
            });
        }
    }

    public void onDetach() {
        super.onDetach();
        if (this.mWebView != null) {
            try {
                this.mWebView.removeAllViews();
                this.mWebView.stopLoading();
                this.mWebView.destroy();
            } catch (Exception e) {
                Logger.e(this.TAG, e);
            }
        }
    }
}
