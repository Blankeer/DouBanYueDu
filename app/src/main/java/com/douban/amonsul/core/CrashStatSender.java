package com.douban.amonsul.core;

import android.content.Context;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatUtils;
import com.douban.amonsul.core.StatSender.SendCallback;
import com.douban.amonsul.network.NetWorker;
import com.douban.amonsul.store.EventHandler;

public class CrashStatSender extends StatSender {
    private static final String TAG;

    static {
        TAG = CrashStatSender.class.getName();
    }

    public boolean isSendStat(Context context, EventHandler eventHandler, StatConfig config) {
        return eventHandler.getEventsCnt() > 0;
    }

    public void sendMobileStat(Context context, EventHandler eventHandler, NetWorker networker, SendCallback callback) {
        byte[] data = StatUtils.getEventsBytes(context, eventHandler.getEventJsonArrayStr());
        if (data != null && networker.sendCrashData(context, data) == 0) {
            StatLogger.d(TAG, " Crash info send clean all event");
            StatAccess.getInstance(context).crashUpload(eventHandler.getEventsCnt());
            eventHandler.cleanAllEvent();
        }
        if (callback != null) {
            callback.onResult(0);
        }
    }
}
