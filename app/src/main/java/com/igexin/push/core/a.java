package com.igexin.push.core;

import android.os.Environment;

public class a {
    public static final String a;
    public static final String b;
    public static int c;
    public static int d;
    public static int e;
    public static int f;
    public static int g;
    public static int h;
    public static int i;
    public static int j;
    public static int k;
    public static int l;
    public static int m;
    public static String n;
    public static String o;
    public static String p;

    static {
        a = Environment.getExternalStorageDirectory() + "/Sdk/ImgCache/";
        b = Environment.getExternalStorageDirectory() + "/Sdk/WebCache/";
        c = 0;
        d = 1;
        e = 2;
        f = 3;
        g = 4;
        h = 5;
        i = 6;
        j = 7;
        k = 0;
        l = 1;
        m = 2;
        n = "com.igexin.sdk.PushService";
        o = "com.igexin.sdk.coordinator.SdkMsgService";
        p = "com.igexin.sdk.coordinator.GexinMsgService";
    }
}
