package com.sina.weibo.sdk.api.share;

import android.content.Context;
import android.os.Bundle;
import com.sina.weibo.sdk.WeiboAppManager.WeiboInfo;
import com.sina.weibo.sdk.api.WeiboMessage;

public class SendMessageToWeiboRequest extends BaseRequest {
    public WeiboMessage message;

    public SendMessageToWeiboRequest(Bundle data) {
        fromBundle(data);
    }

    public int getType() {
        return 1;
    }

    public void fromBundle(Bundle data) {
        super.fromBundle(data);
        this.message = new WeiboMessage(data);
    }

    public void toBundle(Bundle data) {
        super.toBundle(data);
        data.putAll(this.message.toBundle(data));
    }

    final boolean check(Context context, WeiboInfo weiboInfo, VersionCheckHandler handler) {
        if (this.message == null || weiboInfo == null || !weiboInfo.isLegal()) {
            return false;
        }
        if (handler == null || handler.checkRequest(context, weiboInfo, this.message)) {
            return this.message.checkArgs();
        }
        return false;
    }
}
