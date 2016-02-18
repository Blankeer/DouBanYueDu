package com.sina.weibo.sdk.api;

import android.os.Bundle;
import com.sina.weibo.sdk.constant.WBConstants.Msg;
import com.sina.weibo.sdk.utils.LogUtil;

public final class WeiboMultiMessage {
    private static final String TAG = "WeiboMultiMessage";
    public ImageObject imageObject;
    public BaseMediaObject mediaObject;
    public TextObject textObject;

    public WeiboMultiMessage(Bundle data) {
        toBundle(data);
    }

    public Bundle toBundle(Bundle data) {
        if (this.textObject != null) {
            data.putParcelable(Msg.TEXT, this.textObject);
            data.putString(Msg.TEXT_EXTRA, this.textObject.toExtraMediaString());
        }
        if (this.imageObject != null) {
            data.putParcelable(Msg.IMAGE, this.imageObject);
            data.putString(Msg.IMAGE_EXTRA, this.imageObject.toExtraMediaString());
        }
        if (this.mediaObject != null) {
            data.putParcelable(Msg.MEDIA, this.mediaObject);
            data.putString(Msg.MEDIA_EXTRA, this.mediaObject.toExtraMediaString());
        }
        return data;
    }

    public WeiboMultiMessage toObject(Bundle data) {
        this.textObject = (TextObject) data.getParcelable(Msg.TEXT);
        if (this.textObject != null) {
            this.textObject.toExtraMediaObject(data.getString(Msg.TEXT_EXTRA));
        }
        this.imageObject = (ImageObject) data.getParcelable(Msg.IMAGE);
        if (this.imageObject != null) {
            this.imageObject.toExtraMediaObject(data.getString(Msg.IMAGE_EXTRA));
        }
        this.mediaObject = (BaseMediaObject) data.getParcelable(Msg.MEDIA);
        if (this.mediaObject != null) {
            this.mediaObject.toExtraMediaObject(data.getString(Msg.MEDIA_EXTRA));
        }
        return this;
    }

    public boolean checkArgs() {
        if (this.textObject != null && !this.textObject.checkArgs()) {
            LogUtil.e(TAG, "checkArgs fail, textObject is invalid");
            return false;
        } else if (this.imageObject != null && !this.imageObject.checkArgs()) {
            LogUtil.e(TAG, "checkArgs fail, imageObject is invalid");
            return false;
        } else if (this.mediaObject != null && !this.mediaObject.checkArgs()) {
            LogUtil.e(TAG, "checkArgs fail, mediaObject is invalid");
            return false;
        } else if (this.textObject != null || this.imageObject != null || this.mediaObject != null) {
            return true;
        } else {
            LogUtil.e(TAG, "checkArgs fail, textObject and imageObject and mediaObject is null");
            return false;
        }
    }
}
