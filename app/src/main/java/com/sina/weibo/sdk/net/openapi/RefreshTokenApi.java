package com.sina.weibo.sdk.net.openapi;

import android.content.Context;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.network.HttpRequest;

public class RefreshTokenApi {
    private static final String REFRESH_TOKEN_URL = "https://api.weibo.com/oauth2/access_token";
    private Context mContext;

    private RefreshTokenApi(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static RefreshTokenApi create(Context context) {
        return new RefreshTokenApi(context);
    }

    public void refreshToken(String appKey, String refreshToken, RequestListener listener) {
        WeiboParameters params = new WeiboParameters(appKey);
        params.put(Constants.PARAM_CLIENT_ID, appKey);
        params.put(WBConstants.AUTH_PARAMS_GRANT_TYPE, Oauth2AccessToken.KEY_REFRESH_TOKEN);
        params.put(Oauth2AccessToken.KEY_REFRESH_TOKEN, refreshToken);
        new AsyncWeiboRunner(this.mContext).requestAsync(REFRESH_TOKEN_URL, params, HttpRequest.METHOD_POST, listener);
    }
}
