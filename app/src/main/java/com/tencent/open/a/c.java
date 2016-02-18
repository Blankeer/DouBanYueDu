package com.tencent.open.a;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import java.io.File;

/* compiled from: ProGuard */
public class c {
    public static int a;
    public static int b;
    public static boolean c;
    public static boolean d;
    public static boolean e;
    public static String f;
    public static String g;
    public static String h;
    public static String i;
    public static String j;
    public static long k;
    public static int l;
    public static int m;
    public static int n;
    public static String o;
    public static String p;
    public static String q;
    public static int r;
    public static long s;

    static {
        a = 60;
        b = 32;
        c = false;
        d = false;
        e = false;
        f = "OpenSDK.File.Tracer";
        g = "OpenSDK.Client.File.Tracer";
        h = "Tencent" + File.separator + "OpenSDK" + File.separator + "Logs";
        i = ".OpenSDK.log";
        j = ".app.log";
        k = 8388608;
        l = AccessibilityNodeInfoCompat.ACTION_EXPAND;
        m = AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD;
        n = AbstractSpiCall.DEFAULT_TIMEOUT;
        o = "debug.file.blockcount";
        p = "debug.file.keepperiod";
        q = "debug.file.tracelevel";
        r = 24;
        s = 604800000;
    }
}
