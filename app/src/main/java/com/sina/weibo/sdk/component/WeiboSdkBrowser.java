package com.sina.weibo.sdk.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.component.ShareRequestParam.UploadPicResult;
import com.sina.weibo.sdk.component.view.LoadingBar;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.NetworkHelper;
import com.sina.weibo.sdk.utils.ResourceManager;
import com.sina.weibo.sdk.utils.Utility;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;

public class WeiboSdkBrowser extends Activity implements BrowserRequestCallBack {
    public static final String BROWSER_CLOSE_SCHEME = "sinaweibo://browser/close";
    public static final String BROWSER_WIDGET_SCHEME = "sinaweibo://browser/datatransfer";
    private static final String CANCEL_EN = "Close";
    private static final String CANCEL_ZH_CN = "\u5173\u95ed";
    private static final String CANCEL_ZH_TW = "\u5173\u95ed";
    private static final String CHANNEL_DATA_ERROR_EN = "channel_data_error";
    private static final String CHANNEL_DATA_ERROR_ZH_CN = "\u91cd\u65b0\u52a0\u8f7d";
    private static final String CHANNEL_DATA_ERROR_ZH_TW = "\u91cd\u65b0\u8f09\u5165";
    private static final String EMPTY_PROMPT_BAD_NETWORK_UI_EN = "A network error occurs, please tap the button to reload";
    private static final String EMPTY_PROMPT_BAD_NETWORK_UI_ZH_CN = "\u7f51\u7edc\u51fa\u9519\u5566\uff0c\u8bf7\u70b9\u51fb\u6309\u94ae\u91cd\u65b0\u52a0\u8f7d";
    private static final String EMPTY_PROMPT_BAD_NETWORK_UI_ZH_TW = "\u7db2\u8def\u51fa\u932f\u5566\uff0c\u8acb\u9ede\u64ca\u6309\u9215\u91cd\u65b0\u8f09\u5165";
    private static final String LOADINFO_EN = "Loading....";
    private static final String LOADINFO_ZH_CN = "\u52a0\u8f7d\u4e2d....";
    private static final String LOADINFO_ZH_TW = "\u8f09\u5165\u4e2d....";
    private static final String TAG;
    private static final String WEIBOBROWSER_NO_TITLE_EN = "No Title";
    private static final String WEIBOBROWSER_NO_TITLE_ZH_CN = "\u65e0\u6807\u9898";
    private static final String WEIBOBROWSER_NO_TITLE_ZH_TW = "\u7121\u6a19\u984c";
    private boolean isErrorPage;
    private Boolean isFromGame;
    private boolean isLoading;
    private String mHtmlTitle;
    private TextView mLeftBtn;
    private Button mLoadErrorRetryBtn;
    private LinearLayout mLoadErrorView;
    private LoadingBar mLoadingBar;
    private BrowserRequestParamBase mRequestParam;
    private String mSpecifyTitle;
    private TextView mTitleText;
    private String mUrl;
    private WebView mWebView;
    private WeiboWebViewClient mWeiboWebViewClient;

    private class WeiboChromeClient extends WebChromeClient {
        private WeiboChromeClient() {
        }

        public void onProgressChanged(WebView view, int newProgress) {
            WeiboSdkBrowser.this.mLoadingBar.drawProgress(newProgress);
            if (newProgress == 100) {
                WeiboSdkBrowser.this.isLoading = false;
                WeiboSdkBrowser.this.refreshAllViews();
            } else if (!WeiboSdkBrowser.this.isLoading) {
                WeiboSdkBrowser.this.isLoading = true;
                WeiboSdkBrowser.this.refreshAllViews();
            }
        }

        public void onReceivedTitle(WebView view, String title) {
            if (!WeiboSdkBrowser.this.isWeiboCustomScheme(WeiboSdkBrowser.this.mUrl) && !WeiboSdkBrowser.this.isFromGame.booleanValue()) {
                WeiboSdkBrowser.this.mHtmlTitle = title;
                WeiboSdkBrowser.this.updateTitleName();
            }
        }
    }

    /* renamed from: com.sina.weibo.sdk.component.WeiboSdkBrowser.1 */
    class AnonymousClass1 implements RequestListener {
        private final /* synthetic */ ShareRequestParam val$req;

        AnonymousClass1(ShareRequestParam shareRequestParam) {
            this.val$req = shareRequestParam;
        }

        public void onWeiboException(WeiboException e) {
            LogUtil.d(WeiboSdkBrowser.TAG, "post onWeiboException " + e.getMessage());
            this.val$req.sendSdkErrorResponse(WeiboSdkBrowser.this, e.getMessage());
            WeiboSdkBrowser.this.finish();
        }

        public void onComplete(String response) {
            LogUtil.d(WeiboSdkBrowser.TAG, "post onComplete : " + response);
            UploadPicResult result = UploadPicResult.parse(response);
            if (result == null || result.getCode() != 1 || TextUtils.isEmpty(result.getPicId())) {
                this.val$req.sendSdkErrorResponse(WeiboSdkBrowser.this, "upload pic faild");
                WeiboSdkBrowser.this.finish();
                return;
            }
            WeiboSdkBrowser.this.openUrl(this.val$req.buildUrl(result.getPicId()));
        }
    }

    public WeiboSdkBrowser() {
        this.isFromGame = Boolean.valueOf(false);
    }

    static {
        TAG = WeiboSdkBrowser.class.getName();
    }

    public static void startAuth(Context context, String url, AuthInfo authInfo, WeiboAuthListener listener) {
        AuthRequestParam reqParam = new AuthRequestParam(context);
        reqParam.setLauncher(BrowserLauncher.AUTH);
        reqParam.setUrl(url);
        reqParam.setAuthInfo(authInfo);
        reqParam.setAuthListener(listener);
        Intent intent = new Intent(context, WeiboSdkBrowser.class);
        intent.putExtras(reqParam.createRequestParamBundle());
        context.startActivity(intent);
    }

    public static void startShared(Context context, String url, AuthInfo authInfo, WeiboAuthListener listener) {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (initDataFromIntent(getIntent())) {
            setContentView();
            initWebView();
            if (isWeiboShareRequestParam(this.mRequestParam)) {
                startShare();
                return;
            } else {
                openUrl(this.mUrl);
                return;
            }
        }
        finish();
    }

    private boolean initDataFromIntent(Intent data) {
        Bundle bundle = data.getExtras();
        this.mRequestParam = createBrowserRequestParam(bundle);
        if (this.mRequestParam != null) {
            this.mUrl = this.mRequestParam.getUrl();
            this.mSpecifyTitle = this.mRequestParam.getSpecifyTitle();
        } else {
            String url = bundle.getString("key_url");
            String specifyTitle = bundle.getString("key_specify_title");
            if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                this.mUrl = url;
                this.mSpecifyTitle = specifyTitle;
            }
        }
        if (TextUtils.isEmpty(this.mUrl)) {
            return false;
        }
        LogUtil.d(TAG, "LOAD URL : " + this.mUrl);
        return true;
    }

    private void openUrl(String url) {
        this.mWebView.loadUrl(url);
    }

    private void startShare() {
        LogUtil.d(TAG, "Enter startShare()............");
        ShareRequestParam req = this.mRequestParam;
        if (req.hasImage()) {
            LogUtil.d(TAG, "loadUrl hasImage............");
            new AsyncWeiboRunner(this).requestAsync(ShareRequestParam.UPLOAD_PIC_URL, req.buildUploadPicParam(new WeiboParameters(req.getAppKey())), HttpRequest.METHOD_POST, new AnonymousClass1(req));
            return;
        }
        openUrl(this.mUrl);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void initWebView() {
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        if (isWeiboShareRequestParam(this.mRequestParam)) {
            this.mWebView.getSettings().setUserAgentString(Utility.generateUA(this));
        }
        this.mWebView.getSettings().setSavePassword(false);
        this.mWebView.setWebViewClient(this.mWeiboWebViewClient);
        this.mWebView.setWebChromeClient(new WeiboChromeClient());
        this.mWebView.requestFocus();
        this.mWebView.setScrollBarStyle(0);
        if (VERSION.SDK_INT >= 11) {
            this.mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        } else {
            removeJavascriptInterface(this.mWebView);
        }
    }

    private void setTopNavTitle() {
        this.mTitleText.setText(this.mSpecifyTitle);
        this.mLeftBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (WeiboSdkBrowser.this.mRequestParam != null) {
                    WeiboSdkBrowser.this.mRequestParam.execRequest(WeiboSdkBrowser.this, 3);
                }
                WeiboSdkBrowser.this.finish();
            }
        });
    }

    private void updateTitleName() {
        String showTitle = Table.STRING_DEFAULT_VALUE;
        if (!TextUtils.isEmpty(this.mHtmlTitle)) {
            showTitle = this.mHtmlTitle;
        } else if (!TextUtils.isEmpty(this.mSpecifyTitle)) {
            showTitle = this.mSpecifyTitle;
        }
        this.mTitleText.setText(showTitle);
    }

    private void setContentView() {
        RelativeLayout contentLy = new RelativeLayout(this);
        contentLy.setLayoutParams(new LayoutParams(-1, -1));
        contentLy.setBackgroundColor(-1);
        LinearLayout titleBarLy = new LinearLayout(this);
        titleBarLy.setId(1);
        titleBarLy.setOrientation(1);
        titleBarLy.setLayoutParams(new LayoutParams(-1, -2));
        View titleBar = initTitleBar();
        TextView shadowBar = new TextView(this);
        shadowBar.setLayoutParams(new LinearLayout.LayoutParams(-1, ResourceManager.dp2px(this, 2)));
        shadowBar.setBackgroundDrawable(ResourceManager.getNinePatchDrawable(this, "weibosdk_common_shadow_top.9.png"));
        this.mLoadingBar = new LoadingBar(this);
        this.mLoadingBar.setBackgroundColor(0);
        this.mLoadingBar.drawProgress(0);
        LinearLayout.LayoutParams loadingBarLy = new LinearLayout.LayoutParams(-1, ResourceManager.dp2px(this, 3));
        this.mLoadingBar.setLayoutParams(loadingBarLy);
        titleBarLy.addView(titleBar);
        titleBarLy.addView(shadowBar);
        titleBarLy.addView(this.mLoadingBar);
        this.mWebView = new WebView(this);
        this.mWebView.setBackgroundColor(-1);
        RelativeLayout.LayoutParams webViewLp = new RelativeLayout.LayoutParams(-1, -1);
        webViewLp.addRule(3, 1);
        this.mWebView.setLayoutParams(webViewLp);
        this.mLoadErrorView = new LinearLayout(this);
        this.mLoadErrorView.setVisibility(8);
        this.mLoadErrorView.setOrientation(1);
        this.mLoadErrorView.setGravity(17);
        RelativeLayout.LayoutParams mLoadErrorViewLp = new RelativeLayout.LayoutParams(-1, -1);
        mLoadErrorViewLp.addRule(3, 1);
        this.mLoadErrorView.setLayoutParams(mLoadErrorViewLp);
        ImageView loadErrorImg = new ImageView(this);
        loadErrorImg.setImageDrawable(ResourceManager.getDrawable(this, "weibosdk_empty_failed.png"));
        LinearLayout.LayoutParams loadErrorImgLp = new LinearLayout.LayoutParams(-2, -2);
        int dp2px = ResourceManager.dp2px(this, 8);
        loadErrorImgLp.bottomMargin = dp2px;
        loadErrorImgLp.rightMargin = dp2px;
        loadErrorImgLp.topMargin = dp2px;
        loadErrorImgLp.leftMargin = dp2px;
        loadErrorImg.setLayoutParams(loadErrorImgLp);
        this.mLoadErrorView.addView(loadErrorImg);
        TextView loadErrorContent = new TextView(this);
        loadErrorContent.setGravity(1);
        loadErrorContent.setTextColor(-4342339);
        loadErrorContent.setTextSize(2, 14.0f);
        loadErrorContent.setText(ResourceManager.getString(this, EMPTY_PROMPT_BAD_NETWORK_UI_EN, EMPTY_PROMPT_BAD_NETWORK_UI_ZH_CN, EMPTY_PROMPT_BAD_NETWORK_UI_ZH_TW));
        loadErrorContent.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        this.mLoadErrorView.addView(loadErrorContent);
        this.mLoadErrorRetryBtn = new Button(this);
        this.mLoadErrorRetryBtn.setGravity(17);
        this.mLoadErrorRetryBtn.setTextColor(-8882056);
        this.mLoadErrorRetryBtn.setTextSize(2, 16.0f);
        this.mLoadErrorRetryBtn.setText(ResourceManager.getString(this, CHANNEL_DATA_ERROR_EN, CHANNEL_DATA_ERROR_ZH_CN, CHANNEL_DATA_ERROR_ZH_TW));
        this.mLoadErrorRetryBtn.setBackgroundDrawable(ResourceManager.createStateListDrawable(this, "weibosdk_common_button_alpha.9.png", "weibosdk_common_button_alpha_highlighted.9.png"));
        LinearLayout.LayoutParams loadErrorRetryBtnLp = new LinearLayout.LayoutParams(ResourceManager.dp2px(this, Header.UUID), ResourceManager.dp2px(this, 46));
        loadErrorRetryBtnLp.topMargin = ResourceManager.dp2px(this, 10);
        this.mLoadErrorRetryBtn.setLayoutParams(loadErrorRetryBtnLp);
        this.mLoadErrorRetryBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                WeiboSdkBrowser.this.openUrl(WeiboSdkBrowser.this.mUrl);
                WeiboSdkBrowser.this.isErrorPage = false;
            }
        });
        this.mLoadErrorView.addView(this.mLoadErrorRetryBtn);
        contentLy.addView(titleBarLy);
        contentLy.addView(this.mWebView);
        contentLy.addView(this.mLoadErrorView);
        setContentView(contentLy);
        setTopNavTitle();
    }

    private View initTitleBar() {
        RelativeLayout titleBar = new RelativeLayout(this);
        titleBar.setLayoutParams(new LayoutParams(-1, ResourceManager.dp2px(this, 45)));
        titleBar.setBackgroundDrawable(ResourceManager.getNinePatchDrawable(this, "weibosdk_navigationbar_background.9.png"));
        this.mLeftBtn = new TextView(this);
        this.mLeftBtn.setClickable(true);
        this.mLeftBtn.setTextSize(2, 17.0f);
        this.mLeftBtn.setTextColor(ResourceManager.createColorStateList(-32256, 1728020992));
        this.mLeftBtn.setText(ResourceManager.getString(this, CANCEL_EN, CANCEL_ZH_TW, CANCEL_ZH_TW));
        RelativeLayout.LayoutParams leftBtnLp = new RelativeLayout.LayoutParams(-2, -2);
        leftBtnLp.addRule(5);
        leftBtnLp.addRule(15);
        leftBtnLp.leftMargin = ResourceManager.dp2px(this, 10);
        leftBtnLp.rightMargin = ResourceManager.dp2px(this, 10);
        this.mLeftBtn.setLayoutParams(leftBtnLp);
        titleBar.addView(this.mLeftBtn);
        this.mTitleText = new TextView(this);
        this.mTitleText.setTextSize(2, 18.0f);
        this.mTitleText.setTextColor(-11382190);
        this.mTitleText.setEllipsize(TruncateAt.END);
        this.mTitleText.setSingleLine(true);
        this.mTitleText.setGravity(17);
        this.mTitleText.setMaxWidth(ResourceManager.dp2px(this, Header.ARRAYLIST_PACKED_LONG));
        RelativeLayout.LayoutParams titleTextLy = new RelativeLayout.LayoutParams(-2, -2);
        titleTextLy.addRule(13);
        this.mTitleText.setLayoutParams(titleTextLy);
        titleBar.addView(this.mTitleText);
        return titleBar;
    }

    protected void refreshAllViews() {
        if (this.isLoading) {
            setViewLoading();
        } else {
            setViewNormal();
        }
    }

    private void setViewNormal() {
        updateTitleName();
        this.mLoadingBar.setVisibility(8);
    }

    private void setViewLoading() {
        this.mTitleText.setText(ResourceManager.getString(this, LOADINFO_EN, LOADINFO_ZH_CN, LOADINFO_ZH_TW));
        this.mLoadingBar.setVisibility(0);
    }

    private void handleReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (!failingUrl.startsWith("sinaweibo")) {
            this.isErrorPage = true;
            promptError();
        }
    }

    private void promptError() {
        this.mLoadErrorView.setVisibility(0);
        this.mWebView.setVisibility(8);
    }

    private void hiddenErrorPrompt() {
        this.mLoadErrorView.setVisibility(8);
        this.mWebView.setVisibility(0);
    }

    private boolean isWeiboCustomScheme(String url) {
        if (!TextUtils.isEmpty(url) && "sinaweibo".equalsIgnoreCase(Uri.parse(url).getAuthority())) {
            return true;
        }
        return false;
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        NetworkHelper.clearCookies(this);
        super.onDestroy();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyUp(keyCode, event);
        }
        if (this.mRequestParam != null) {
            this.mRequestParam.execRequest(this, 3);
        }
        finish();
        return true;
    }

    private BrowserRequestParamBase createBrowserRequestParam(Bundle data) {
        this.isFromGame = Boolean.valueOf(false);
        BrowserRequestParamBase result = null;
        BrowserLauncher launcher = (BrowserLauncher) data.getSerializable(BrowserRequestParamBase.EXTRA_KEY_LAUNCHER);
        if (launcher == BrowserLauncher.AUTH) {
            BrowserRequestParamBase authRequestParam = new AuthRequestParam(this);
            authRequestParam.setupRequestParam(data);
            installAuthWeiboWebViewClient(authRequestParam);
            return authRequestParam;
        }
        if (launcher == BrowserLauncher.SHARE) {
            BrowserRequestParamBase shareRequestParam = new ShareRequestParam(this);
            shareRequestParam.setupRequestParam(data);
            installShareWeiboWebViewClient(shareRequestParam);
            result = shareRequestParam;
        } else if (launcher == BrowserLauncher.WIDGET) {
            BrowserRequestParamBase widgetRequestParam = new WidgetRequestParam(this);
            widgetRequestParam.setupRequestParam(data);
            installWidgetWeiboWebViewClient(widgetRequestParam);
            result = widgetRequestParam;
        } else if (launcher == BrowserLauncher.GAME) {
            this.isFromGame = Boolean.valueOf(true);
            BrowserRequestParamBase gameRequestParam = new GameRequestParam(this);
            gameRequestParam.setupRequestParam(data);
            installWeiboWebGameClient(gameRequestParam);
            result = gameRequestParam;
        }
        return result;
    }

    private boolean isWeiboShareRequestParam(BrowserRequestParamBase reqParam) {
        return reqParam != null && reqParam.getLauncher() == BrowserLauncher.SHARE;
    }

    private void installAuthWeiboWebViewClient(AuthRequestParam param) {
        this.mWeiboWebViewClient = new AuthWeiboWebViewClient(this, param);
        this.mWeiboWebViewClient.setBrowserRequestCallBack(this);
    }

    private void installShareWeiboWebViewClient(ShareRequestParam param) {
        ShareWeiboWebViewClient client = new ShareWeiboWebViewClient(this, param);
        client.setBrowserRequestCallBack(this);
        this.mWeiboWebViewClient = client;
    }

    private void installWidgetWeiboWebViewClient(WidgetRequestParam param) {
        WidgetWeiboWebViewClient client = new WidgetWeiboWebViewClient(this, param);
        client.setBrowserRequestCallBack(this);
        this.mWeiboWebViewClient = client;
    }

    private void installWeiboWebGameClient(GameRequestParam param) {
        WeiboGameClient client = new WeiboGameClient(this, param);
        client.setBrowserRequestCallBack(this);
        this.mWeiboWebViewClient = client;
    }

    public void onPageStartedCallBack(WebView view, String url, Bitmap favicon) {
        LogUtil.d(TAG, "onPageStarted URL: " + url);
        this.mUrl = url;
        if (!isWeiboCustomScheme(url)) {
            this.mHtmlTitle = Table.STRING_DEFAULT_VALUE;
        }
    }

    public boolean shouldOverrideUrlLoadingCallBack(WebView view, String url) {
        LogUtil.i(TAG, "shouldOverrideUrlLoading URL: " + url);
        return false;
    }

    public void onPageFinishedCallBack(WebView view, String url) {
        LogUtil.d(TAG, "onPageFinished URL: " + url);
        if (this.isErrorPage) {
            promptError();
            return;
        }
        this.isErrorPage = false;
        hiddenErrorPrompt();
    }

    public void onReceivedErrorCallBack(WebView view, int errorCode, String description, String failingUrl) {
        LogUtil.d(TAG, "onReceivedError: errorCode = " + errorCode + ", description = " + description + ", failingUrl = " + failingUrl);
        handleReceivedError(view, errorCode, description, failingUrl);
    }

    public void onReceivedSslErrorCallBack(WebView view, SslErrorHandler handler, SslError error) {
        LogUtil.d(TAG, "onReceivedSslErrorCallBack.........");
    }

    public static void closeBrowser(Activity act, String authListenerKey, String widgetRequestCallbackKey) {
        WeiboCallbackManager manager = WeiboCallbackManager.getInstance(act.getApplicationContext());
        if (!TextUtils.isEmpty(authListenerKey)) {
            manager.removeWeiboAuthListener(authListenerKey);
            act.finish();
        }
        if (!TextUtils.isEmpty(widgetRequestCallbackKey)) {
            manager.removeWidgetRequestCallback(widgetRequestCallbackKey);
            act.finish();
        }
    }

    public void removeJavascriptInterface(WebView webView) {
        if (VERSION.SDK_INT < 11) {
            try {
                webView.getClass().getDeclaredMethod("removeJavascriptInterface", new Class[0]).invoke("searchBoxJavaBridge_", new Object[0]);
            } catch (Exception e) {
                LogUtil.e(TAG, e.toString());
            }
        }
    }
}
