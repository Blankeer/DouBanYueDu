package com.sina.weibo.sdk.component;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import com.douban.amonsul.StatConstant;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboAuthException;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.open.SocialConstants;
import io.realm.internal.Table;

class WeiboGameClient extends WeiboWebViewClient {
    private Activity mAct;
    private GameRequestParam mGameRequestParam;
    private WeiboAuthListener mListener;

    public WeiboGameClient(Activity activity, GameRequestParam requestParam) {
        this.mAct = activity;
        this.mGameRequestParam = requestParam;
        this.mListener = this.mGameRequestParam.getAuthListener();
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (this.mCallBack != null) {
            this.mCallBack.onPageStartedCallBack(view, url, favicon);
        }
        super.onPageStarted(view, url, favicon);
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (this.mCallBack != null) {
            this.mCallBack.shouldOverrideUrlLoadingCallBack(view, url);
        }
        if (!url.startsWith(WeiboSdkBrowser.BROWSER_CLOSE_SCHEME)) {
            return super.shouldOverrideUrlLoading(view, url);
        }
        Bundle bundle = Utility.parseUri(url);
        if (!(bundle.isEmpty() || this.mListener == null)) {
            this.mListener.onComplete(bundle);
        }
        WeiboSdkBrowser.closeBrowser(this.mAct, this.mGameRequestParam.getAuthListenerKey(), null);
        return true;
    }

    public void onPageFinished(WebView view, String url) {
        if (this.mCallBack != null) {
            this.mCallBack.onPageFinishedCallBack(view, url);
        }
        super.onPageFinished(view, url);
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (this.mCallBack != null) {
            this.mCallBack.onReceivedErrorCallBack(view, errorCode, description, failingUrl);
        }
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (this.mCallBack != null) {
            this.mCallBack.onReceivedSslErrorCallBack(view, handler, error);
        }
        super.onReceivedSslError(view, handler, error);
    }

    private void handleRedirectUrl(String url) {
        Bundle values = Utility.parseUrl(url);
        String errorType = values.getString(StatConstant.STAT_EVENT_ID_ERROR) == null ? Table.STRING_DEFAULT_VALUE : values.getString(StatConstant.STAT_EVENT_ID_ERROR);
        String errorCode = values.getString(SelectCountryActivity.EXTRA_COUNTRY_CODE);
        String errorDescription = values.getString(SocialConstants.PARAM_SEND_MSG);
        if (errorType == null && errorCode == null) {
            if (this.mListener != null) {
                this.mListener.onComplete(values);
            }
        } else if (this.mListener != null) {
            this.mListener.onWeiboException(new WeiboAuthException(errorCode, errorType, errorDescription));
        }
    }
}
