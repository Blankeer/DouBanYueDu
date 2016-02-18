package com.douban.book.reader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.alipay.security.mobile.module.deviceinfo.constant.a;
import com.douban.book.reader.app.App;
import com.douban.book.reader.network.client.SyncedRestClient;
import com.douban.book.reader.service.ExecutePendingRequestsService_;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Utils;
import java.util.Date;

public class ConnectionChangeReceiver extends BroadcastReceiver {
    private static final int MIN_SYNC_INTERVAL = 300000;
    private static final String TAG;
    private static Date sLastSyncedTime;

    static {
        TAG = ConnectionChangeReceiver.class.getSimpleName();
    }

    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "onReceive, intent=%s", intent);
        if (DateUtils.millisElapsed(sLastSyncedTime) < a.b) {
            Logger.d(TAG, "skipped.", new Object[0]);
        } else if (Utils.isNetworkAvailable() && SyncedRestClient.hasPendingRequests()) {
            ExecutePendingRequestsService_.intent(App.get()).start();
            sLastSyncedTime = new Date();
        }
    }
}
