package com.sina.weibo.sdk.net.openapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.helper.WorksListUri;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.LogUtil;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public class ShareWeiboApi {
    private static final String REPOST_URL = "https://api.weibo.com/2/statuses/repost.json";
    private static final String TAG;
    private static final String UPDATE_URL = "https://api.weibo.com/2/statuses/update.json";
    private static final String UPLOAD_URL = "https://api.weibo.com/2/statuses/upload.json";
    private String mAccessToken;
    private String mAppKey;
    private Context mContext;

    static {
        TAG = ShareWeiboApi.class.getName();
    }

    private ShareWeiboApi(Context context, String appKey, String token) {
        this.mContext = context.getApplicationContext();
        this.mAppKey = appKey;
        this.mAccessToken = token;
    }

    public static ShareWeiboApi create(Context context, String appKey, String token) {
        return new ShareWeiboApi(context, appKey, token);
    }

    public void update(String content, String lat, String lon, RequestListener listener) {
        requestAsync(UPDATE_URL, buildUpdateParams(content, lat, lon), HttpRequest.METHOD_POST, listener);
    }

    public void upload(String content, Bitmap bitmap, String lat, String lon, RequestListener listener) {
        WeiboParameters params = buildUpdateParams(content, lat, lon);
        params.put("pic", bitmap);
        requestAsync(UPLOAD_URL, params, HttpRequest.METHOD_POST, listener);
    }

    public void repost(String repostBlogId, String repostContent, int comment, RequestListener listener) {
        WeiboParameters params = buildUpdateParams(repostContent, null, null);
        params.put(WorksListUri.KEY_ID, repostBlogId);
        params.put("is_comment", String.valueOf(comment));
        requestAsync(REPOST_URL, params, HttpRequest.METHOD_POST, listener);
    }

    private void requestAsync(String url, WeiboParameters params, String httpMethod, RequestListener listener) {
        if (TextUtils.isEmpty(this.mAccessToken) || TextUtils.isEmpty(url) || params == null || TextUtils.isEmpty(httpMethod) || listener == null) {
            LogUtil.e(TAG, "Argument error!");
            return;
        }
        params.put(ShareRequestParam.REQ_PARAM_TOKEN, this.mAccessToken);
        new AsyncWeiboRunner(this.mContext).requestAsync(url, params, httpMethod, listener);
    }

    private WeiboParameters buildUpdateParams(String content, String lat, String lon) {
        WeiboParameters params = new WeiboParameters(this.mAppKey);
        params.put(SettingsJsonConstants.APP_STATUS_KEY, content);
        if (!TextUtils.isEmpty(lon)) {
            params.put("long", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            params.put(StatConstant.JSON_KEY_LAC, lat);
        }
        if (!TextUtils.isEmpty(this.mAppKey)) {
            params.put(ShareRequestParam.REQ_PARAM_SOURCE, this.mAppKey);
        }
        return params;
    }
}
