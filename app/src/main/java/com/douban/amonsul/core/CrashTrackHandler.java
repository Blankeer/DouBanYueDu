package com.douban.amonsul.core;

import android.content.Context;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashTrackHandler implements UncaughtExceptionHandler {
    private static CrashTrackHandler mInstance;
    private CrashCallback mCallBack;
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;

    public interface CrashCallback {
        void onCrash(String str);
    }

    private CrashTrackHandler() {
    }

    public static synchronized CrashTrackHandler getInstance() {
        CrashTrackHandler crashTrackHandler;
        synchronized (CrashTrackHandler.class) {
            if (mInstance == null) {
                mInstance = new CrashTrackHandler();
            }
            crashTrackHandler = mInstance;
        }
        return crashTrackHandler;
    }

    public void init(Context context) {
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void setCrashCallback(CrashCallback crashCallback) {
        this.mCallBack = crashCallback;
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        MobileStatManager manager = MobileStatManager.getInstance(this.mContext);
        if (manager != null) {
            manager.doHandleException(ex, this.mCallBack);
        }
        this.mDefaultHandler.uncaughtException(thread, ex);
    }
}
