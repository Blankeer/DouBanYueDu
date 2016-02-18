package com.douban.amonsul;

import android.util.Log;

public class StatLogger {
    public static int v(String tag, String msg) {
        return MobileStat.DEBUG ? Log.v(tag, msg) : 0;
    }

    public static int w(String tag, String msg) {
        return MobileStat.DEBUG ? Log.w(tag, msg) : 0;
    }

    public static int d(String tag, String msg) {
        return MobileStat.DEBUG ? Log.d(tag, msg) : 0;
    }

    public static int e(String tag, String msg) {
        return MobileStat.DEBUG ? Log.e(tag, msg) : 0;
    }

    public static int e(String tag, Throwable e) {
        return MobileStat.DEBUG ? Log.e(tag, Log.getStackTraceString(e)) : 0;
    }
}
