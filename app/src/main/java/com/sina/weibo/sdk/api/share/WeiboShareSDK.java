package com.sina.weibo.sdk.api.share;

import android.content.Context;

public class WeiboShareSDK {
    public static IWeiboShareAPI createWeiboAPI(Context context, String appKey, boolean isDownloadWeibo) {
        return new WeiboShareAPIImpl(context, appKey, false);
    }

    public static IWeiboShareAPI createWeiboAPI(Context context, String appKey) {
        return createWeiboAPI(context, appKey, false);
    }
}
