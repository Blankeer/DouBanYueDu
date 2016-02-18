package com.sina.weibo.sdk.auth.sso;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.component.AuthRequestParam;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.component.WeiboSdkBrowser;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.sina.weibo.sdk.utils.NetworkHelper;
import com.sina.weibo.sdk.utils.UIUtils;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.common.Constants;

class WebAuthHandler {
    private static final String NETWORK_NOT_AVAILABLE_EN = "Network is not available";
    private static final String NETWORK_NOT_AVAILABLE_ZH_CN = "\u65e0\u6cd5\u8fde\u63a5\u5230\u7f51\u7edc\uff0c\u8bf7\u68c0\u67e5\u7f51\u7edc\u914d\u7f6e";
    private static final String NETWORK_NOT_AVAILABLE_ZH_TW = "\u7121\u6cd5\u9023\u63a5\u5230\u7db2\u7edc\uff0c\u8acb\u6aa2\u67e5\u7db2\u7edc\u914d\u7f6e";
    private static final String OAUTH2_BASE_URL = "https://open.weibo.cn/oauth2/authorize?";
    private static final int OBTAIN_AUTH_CODE = 0;
    private static final int OBTAIN_AUTH_TOKEN = 1;
    private static final String TAG;
    private AuthInfo mAuthInfo;
    private Context mContext;

    static {
        TAG = WebAuthHandler.class.getName();
    }

    public WebAuthHandler(Context context, AuthInfo authInfo) {
        this.mContext = context;
        this.mAuthInfo = authInfo;
    }

    public AuthInfo getAuthInfo() {
        return this.mAuthInfo;
    }

    public void anthorize(WeiboAuthListener listener) {
        authorize(listener, OBTAIN_AUTH_TOKEN);
    }

    public void authorize(WeiboAuthListener listener, int type) {
        startDialog(listener, type);
    }

    private void startDialog(WeiboAuthListener listener, int type) {
        if (listener != null) {
            WeiboParameters requestParams = new WeiboParameters(this.mAuthInfo.getAppKey());
            requestParams.put(Constants.PARAM_CLIENT_ID, this.mAuthInfo.getAppKey());
            requestParams.put(WBConstants.AUTH_PARAMS_REDIRECT_URL, this.mAuthInfo.getRedirectUrl());
            requestParams.put(Constants.PARAM_SCOPE, this.mAuthInfo.getScope());
            requestParams.put(WBConstants.AUTH_PARAMS_RESPONSE_TYPE, SelectCountryActivity.EXTRA_COUNTRY_CODE);
            requestParams.put(ShareRequestParam.REQ_PARAM_VERSION, WBConstants.WEIBO_SDK_VERSION_CODE);
            String aid = Utility.getAid(this.mContext, this.mAuthInfo.getAppKey());
            if (!TextUtils.isEmpty(aid)) {
                requestParams.put(ShareRequestParam.REQ_PARAM_AID, aid);
            }
            if (OBTAIN_AUTH_TOKEN == type) {
                requestParams.put(ShareRequestParam.REQ_PARAM_PACKAGENAME, this.mAuthInfo.getPackageName());
                requestParams.put(ShareRequestParam.REQ_PARAM_KEY_HASH, this.mAuthInfo.getKeyHash());
            }
            String url = new StringBuilder(OAUTH2_BASE_URL).append(requestParams.encodeUrl()).toString();
            if (NetworkHelper.hasInternetPermission(this.mContext)) {
                AuthRequestParam req = new AuthRequestParam(this.mContext);
                req.setAuthInfo(this.mAuthInfo);
                req.setAuthListener(listener);
                req.setUrl(url);
                req.setSpecifyTitle("\u5fae\u535a\u767b\u5f55");
                Bundle data = req.createRequestParamBundle();
                Intent intent = new Intent(this.mContext, WeiboSdkBrowser.class);
                intent.putExtras(data);
                this.mContext.startActivity(intent);
                return;
            }
            UIUtils.showAlert(this.mContext, "Error", "Application requires permission to access the Internet");
        }
    }
}
