package com.douban.book.reader.app;

import android.app.ActivityManager;

public final class App_ extends App {
    private static App INSTANCE_;

    public static App getInstance() {
        return INSTANCE_;
    }

    public static void setForTesting(App application) {
        INSTANCE_ = application;
    }

    public void onCreate() {
        INSTANCE_ = this;
        init_();
        super.onCreate();
    }

    private void init_() {
        this.mActivityManager = (ActivityManager) getSystemService("activity");
    }
}
