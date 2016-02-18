package com.sina.weibo.sdk.api.share;

import android.content.Context;
import android.os.Bundle;
import com.sina.weibo.sdk.WeiboAppManager.WeiboInfo;
import com.sina.weibo.sdk.api.WeiboMultiMessage;

public class SendMultiMessageToWeiboRequest extends BaseRequest {
    public WeiboMultiMessage multiMessage;

    public SendMultiMessageToWeiboRequest(Bundle data) {
        fromBundle(data);
    }

    public int getType() {
        return 1;
    }

    public void fromBundle(Bundle data) {
        super.fromBundle(data);
        this.multiMessage = new WeiboMultiMessage(data);
    }

    public void toBundle(Bundle data) {
        super.toBundle(data);
        data.putAll(this.multiMessage.toBundle(data));
    }

    final boolean check(Context context, WeiboInfo weiboInfo, VersionCheckHandler handler) {
        if (this.multiMessage == null || weiboInfo == null || !weiboInfo.isLegal()) {
            return false;
        }
        if (handler == null || handler.checkRequest(context, weiboInfo, this.multiMessage)) {
            return this.multiMessage.checkArgs();
        }
        return false;
    }
}
