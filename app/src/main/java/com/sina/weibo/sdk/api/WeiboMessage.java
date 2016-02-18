package com.sina.weibo.sdk.api;

import android.os.Bundle;
import com.sina.weibo.sdk.constant.WBConstants.Msg;
import com.sina.weibo.sdk.utils.LogUtil;

public final class WeiboMessage {
    public BaseMediaObject mediaObject;

    public WeiboMessage(Bundle data) {
        toBundle(data);
    }

    public Bundle toBundle(Bundle data) {
        if (this.mediaObject != null) {
            data.putParcelable(Msg.MEDIA, this.mediaObject);
            data.putString(Msg.MEDIA_EXTRA, this.mediaObject.toExtraMediaString());
        }
        return data;
    }

    public WeiboMessage toObject(Bundle data) {
        this.mediaObject = (BaseMediaObject) data.getParcelable(Msg.MEDIA);
        if (this.mediaObject != null) {
            this.mediaObject.toExtraMediaObject(data.getString(Msg.MEDIA_EXTRA));
        }
        return this;
    }

    public boolean checkArgs() {
        if (this.mediaObject == null) {
            LogUtil.e("Weibo.WeiboMessage", "checkArgs fail, mediaObject is null");
            return false;
        } else if (this.mediaObject == null || this.mediaObject.checkArgs()) {
            return true;
        } else {
            LogUtil.e("Weibo.WeiboMessage", "checkArgs fail, mediaObject is invalid");
            return false;
        }
    }
}
