package natalya.log;

import android.util.Log;

public class NLog {
    private static boolean DEBUG;

    static {
        DEBUG = false;
    }

    public static void enable() {
        DEBUG = true;
    }

    public static void disable() {
        DEBUG = false;
    }

    public static int v(String tag, String msg) {
        return DEBUG ? Log.v(tag, msg) : 0;
    }

    public static int w(String tag, String msg) {
        return DEBUG ? Log.w(tag, msg) : 0;
    }

    public static int d(String tag, String msg) {
        return DEBUG ? Log.d(tag, msg) : 0;
    }

    public static int e(String tag, String msg) {
        return DEBUG ? Log.e(tag, msg) : 0;
    }

    public static int e(String tag, Throwable e) {
        return DEBUG ? Log.e(tag, Log.getStackTraceString(e)) : 0;
    }
}
