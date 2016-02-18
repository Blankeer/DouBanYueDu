package com.douban.amonsul.core;

import android.content.Context;
import com.douban.amonsul.network.NetWorker;
import com.douban.amonsul.store.EventHandler;

public abstract class StatSender {
    public static final int STATSEND_RESULT_ERROR = 1;
    public static final int STATSEND_RESULT_SUCCESS = 0;
    protected boolean mIsWorking;

    public interface SendCallback {
        void onResult(int i);
    }

    public abstract boolean isSendStat(Context context, EventHandler eventHandler, StatConfig statConfig);

    public abstract void sendMobileStat(Context context, EventHandler eventHandler, NetWorker netWorker, SendCallback sendCallback);

    public StatSender() {
        this.mIsWorking = false;
    }

    public boolean isWorking() {
        return this.mIsWorking;
    }
}
