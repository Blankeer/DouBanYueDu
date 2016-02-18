package com.sina.weibo.sdk.auth;

import android.content.Context;
import android.os.Bundle;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.common.Constants;
import io.realm.internal.Table;

public class AuthInfo {
    private String mAppKey;
    private String mKeyHash;
    private String mPackageName;
    private String mRedirectUrl;
    private String mScope;

    public AuthInfo(Context context, String appKey, String redirectUrl, String scope) {
        this.mAppKey = Table.STRING_DEFAULT_VALUE;
        this.mRedirectUrl = Table.STRING_DEFAULT_VALUE;
        this.mScope = Table.STRING_DEFAULT_VALUE;
        this.mPackageName = Table.STRING_DEFAULT_VALUE;
        this.mKeyHash = Table.STRING_DEFAULT_VALUE;
        this.mAppKey = appKey;
        this.mRedirectUrl = redirectUrl;
        this.mScope = scope;
        this.mPackageName = context.getPackageName();
        this.mKeyHash = Utility.getSign(context, this.mPackageName);
    }

    public String getAppKey() {
        return this.mAppKey;
    }

    public String getRedirectUrl() {
        return this.mRedirectUrl;
    }

    public String getScope() {
        return this.mScope;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getKeyHash() {
        return this.mKeyHash;
    }

    public Bundle getAuthBundle() {
        Bundle mBundle = new Bundle();
        mBundle.putString(WBConstants.SSO_APP_KEY, this.mAppKey);
        mBundle.putString(WBConstants.SSO_REDIRECT_URL, this.mRedirectUrl);
        mBundle.putString(Constants.PARAM_SCOPE, this.mScope);
        mBundle.putString(ShareRequestParam.REQ_PARAM_PACKAGENAME, this.mPackageName);
        mBundle.putString(ShareRequestParam.REQ_PARAM_KEY_HASH, this.mKeyHash);
        return mBundle;
    }

    public static AuthInfo parseBundleData(Context context, Bundle data) {
        return new AuthInfo(context, data.getString(WBConstants.SSO_APP_KEY), data.getString(WBConstants.SSO_REDIRECT_URL), data.getString(Constants.PARAM_SCOPE));
    }
}
