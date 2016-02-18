package com.umeng.analytics;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.douban.book.reader.helper.AppUri;
import io.realm.internal.Table;
import u.aly.bs;

public class AnalyticsConfig {
    public static boolean ACTIVITY_DURATION_OPEN;
    public static boolean CATCH_EXCEPTION;
    public static boolean COMPRESS_DATA;
    public static boolean ENABLE_MEMORY_BUFFER;
    public static String GPU_RENDERER;
    public static String GPU_VENDER;
    private static String a;
    private static String b;
    private static double[] c;
    public static long kContinueSessionMillis;
    public static int mVerticalType;
    public static String mWrapperType;
    public static String mWrapperVersion;
    public static boolean sEncrypt;
    public static int sLatentWindow;

    static {
        a = null;
        b = null;
        mWrapperType = null;
        mWrapperVersion = null;
        GPU_VENDER = Table.STRING_DEFAULT_VALUE;
        GPU_RENDERER = Table.STRING_DEFAULT_VALUE;
        sEncrypt = false;
        c = null;
        ACTIVITY_DURATION_OPEN = true;
        COMPRESS_DATA = true;
        ENABLE_MEMORY_BUFFER = true;
        CATCH_EXCEPTION = true;
        kContinueSessionMillis = 30000;
    }

    public static void enableEncrypt(boolean z) {
        sEncrypt = z;
    }

    public static void setAppkey(Context context, String str) {
        if (context == null) {
            a = str;
            return;
        }
        String o = bs.o(context);
        if (TextUtils.isEmpty(o)) {
            Object c = h.a(context).c();
            if (TextUtils.isEmpty(c)) {
                h.a(context).a(str);
            } else if (!c.equals(str)) {
                Log.w(a.e, "Appkey\u548c\u4e0a\u6b21\u914d\u7f6e\u7684\u4e0d\u4e00\u81f4 ");
                h.a(context).a(str);
            }
            a = str;
            return;
        }
        a = o;
        if (!o.equals(str)) {
            Log.w(a.e, "Appkey\u548cAndroidManifest.xml\u4e2d\u914d\u7f6e\u7684\u4e0d\u4e00\u81f4 ");
        }
    }

    public static void setChannel(String str) {
        b = str;
    }

    public static String getAppkey(Context context) {
        if (TextUtils.isEmpty(a)) {
            a = bs.o(context);
            if (TextUtils.isEmpty(a)) {
                a = h.a(context).c();
            }
        }
        return a;
    }

    public static String getChannel(Context context) {
        if (TextUtils.isEmpty(b)) {
            b = bs.t(context);
        }
        return b;
    }

    public static String getSDKVersion() {
        if (mVerticalType == 1) {
            return a.d;
        }
        return a.c;
    }

    public static double[] getLocation() {
        return c;
    }

    public static void setLocation(double d, double d2) {
        if (c == null) {
            c = new double[2];
        }
        c[0] = d;
        c[1] = d2;
    }

    public static void setLatencyWindow(long j) {
        sLatentWindow = ((int) j) * AppUri.OPEN_URL;
    }
}
