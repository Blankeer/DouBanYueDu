package com.douban.book.reader.service;

import android.app.IntentService;
import android.content.Intent;
import com.douban.book.reader.network.client.SyncedRestClient;
import com.douban.book.reader.util.Logger;
import org.androidannotations.annotations.EService;

@EService
public class ExecutePendingRequestsService extends IntentService {
    private static final String TAG;

    static {
        TAG = ExecutePendingRequestsService.class.getSimpleName();
    }

    public ExecutePendingRequestsService() {
        super(TAG);
    }

    protected void onHandleIntent(Intent intent) {
        Logger.d(TAG, "onHandleIntent, intent=%s", intent);
        try {
            SyncedRestClient.executePendingRequestsForAll();
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
    }
}
