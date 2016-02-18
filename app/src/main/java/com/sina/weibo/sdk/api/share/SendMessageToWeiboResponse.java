package com.sina.weibo.sdk.api.share;

import android.content.Context;
import android.os.Bundle;

public class SendMessageToWeiboResponse extends BaseResponse {
    public SendMessageToWeiboResponse(Bundle data) {
        fromBundle(data);
    }

    public int getType() {
        return 1;
    }

    final boolean check(Context context, VersionCheckHandler handler) {
        return true;
    }
}
