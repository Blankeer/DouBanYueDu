package com.sina.weibo.sdk.api.share;

import android.content.Context;
import android.text.TextUtils;
import com.sina.weibo.sdk.ApiUtils;
import com.sina.weibo.sdk.WeiboAppManager;
import com.sina.weibo.sdk.WeiboAppManager.WeiboInfo;
import com.sina.weibo.sdk.api.CmdObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.utils.LogUtil;

public class VersionCheckHandler implements IVersionCheckHandler {
    private static final String TAG;

    static {
        TAG = VersionCheckHandler.class.getName();
    }

    public boolean checkRequest(Context context, WeiboInfo weiboInfo, WeiboMessage message) {
        if (weiboInfo == null || !weiboInfo.isLegal()) {
            return false;
        }
        LogUtil.d(TAG, "WeiboMessage WeiboInfo package : " + weiboInfo.getPackageName());
        LogUtil.d(TAG, "WeiboMessage WeiboInfo supportApi : " + weiboInfo.getSupportApi());
        if (weiboInfo.getSupportApi() < ApiUtils.BUILD_INT_VER_2_2 && message.mediaObject != null && (message.mediaObject instanceof VoiceObject)) {
            message.mediaObject = null;
        }
        if (weiboInfo.getSupportApi() < ApiUtils.BUILD_INT_VER_2_3 && message.mediaObject != null && (message.mediaObject instanceof CmdObject)) {
            message.mediaObject = null;
        }
        return true;
    }

    public boolean checkRequest(Context context, WeiboInfo weiboInfo, WeiboMultiMessage message) {
        if (weiboInfo == null || !weiboInfo.isLegal()) {
            return false;
        }
        LogUtil.d(TAG, "WeiboMultiMessage WeiboInfo package : " + weiboInfo.getPackageName());
        LogUtil.d(TAG, "WeiboMultiMessage WeiboInfo supportApi : " + weiboInfo.getSupportApi());
        if (weiboInfo.getSupportApi() < ApiUtils.BUILD_INT_VER_2_2) {
            return false;
        }
        if (weiboInfo.getSupportApi() < ApiUtils.BUILD_INT_VER_2_3 && message.mediaObject != null && (message.mediaObject instanceof CmdObject)) {
            message.mediaObject = null;
        }
        return true;
    }

    public boolean checkResponse(Context context, String weiboPackage, WeiboMessage message) {
        if (TextUtils.isEmpty(weiboPackage)) {
            return false;
        }
        return checkRequest(context, WeiboAppManager.getInstance(context).parseWeiboInfoByAsset(weiboPackage), message);
    }

    public boolean checkResponse(Context context, String weiboPackage, WeiboMultiMessage message) {
        if (TextUtils.isEmpty(weiboPackage)) {
            return false;
        }
        return checkRequest(context, WeiboAppManager.getInstance(context).parseWeiboInfoByAsset(weiboPackage), message);
    }
}
