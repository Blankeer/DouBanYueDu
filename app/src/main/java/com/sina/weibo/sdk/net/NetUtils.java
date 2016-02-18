package com.sina.weibo.sdk.net;

import android.content.Context;
import com.sina.weibo.sdk.exception.WeiboException;

public class NetUtils {
    public static String internalGetRedirectUri(Context context, String url, String method, WeiboParameters params) {
        return HttpManager.openRedirectUrl4LocationUri(context, url, method, params);
    }

    public static String internalDownloadFile(Context context, String url, String saveDir, String fileName) throws WeiboException {
        return HttpManager.downloadFile(context, url, saveDir, fileName);
    }

    public static String internalHttpRequest(Context context, String url, String httpMethod, WeiboParameters params) {
        return HttpManager.openUrl(context, url, httpMethod, params);
    }

    public static void internalHttpRequest(Context context, String url, WeiboParameters params, String httpMethod, RequestListener listener) {
        new RequestRunner(context, url, params, httpMethod, listener).execute(new Void[1]);
    }
}
