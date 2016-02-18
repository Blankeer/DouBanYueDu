package com.sina.weibo.sdk.component;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.text.TextUtils;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;

public class GameRequestParam extends BrowserRequestParamBase {
    private String mAppKey;
    private WeiboAuthListener mAuthListener;
    private String mAuthListenerKey;
    private String mToken;

    public interface WidgetRequestCallback {
        void onWebViewResult(String str);
    }

    public GameRequestParam(Context context) {
        super(context);
        this.mLaucher = BrowserLauncher.WIDGET;
    }

    protected void onSetupRequestParam(Bundle data) {
        this.mAppKey = data.getString(ShareRequestParam.REQ_PARAM_SOURCE);
        this.mToken = data.getString(ShareRequestParam.REQ_PARAM_TOKEN);
        this.mAuthListenerKey = data.getString(AuthRequestParam.EXTRA_KEY_LISTENER);
        if (!TextUtils.isEmpty(this.mAuthListenerKey)) {
            this.mAuthListener = WeiboCallbackManager.getInstance(this.mContext).getWeiboAuthListener(this.mAuthListenerKey);
        }
        this.mUrl = buildUrl(this.mUrl);
    }

    public void onCreateRequestParamBundle(Bundle data) {
        data.putString(ShareRequestParam.REQ_PARAM_TOKEN, this.mToken);
        data.putString(ShareRequestParam.REQ_PARAM_SOURCE, this.mAppKey);
        WeiboCallbackManager manager = WeiboCallbackManager.getInstance(this.mContext);
        if (this.mAuthListener != null) {
            this.mAuthListenerKey = manager.genCallbackKey();
            manager.setWeiboAuthListener(this.mAuthListenerKey, this.mAuthListener);
            data.putString(AuthRequestParam.EXTRA_KEY_LISTENER, this.mAuthListenerKey);
        }
    }

    private String buildUrl(String baseUrl) {
        Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendQueryParameter(ShareRequestParam.REQ_PARAM_VERSION, WBConstants.WEIBO_SDK_VERSION_CODE);
        if (!TextUtils.isEmpty(this.mAppKey)) {
            builder.appendQueryParameter(ShareRequestParam.REQ_PARAM_SOURCE, this.mAppKey);
        }
        if (!TextUtils.isEmpty(this.mToken)) {
            builder.appendQueryParameter(ShareRequestParam.REQ_PARAM_TOKEN, this.mToken);
        }
        return builder.build().toString();
    }

    public String getToken() {
        return this.mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }

    public String getAppKey() {
        return this.mAppKey;
    }

    public void setAppKey(String mAppKey) {
        this.mAppKey = mAppKey;
    }

    public WeiboAuthListener getAuthListener() {
        return this.mAuthListener;
    }

    public String getAuthListenerKey() {
        return this.mAuthListenerKey;
    }

    public void setAuthListener(WeiboAuthListener mAuthListener) {
        this.mAuthListener = mAuthListener;
    }

    public void execRequest(Activity act, int action) {
    }
}
