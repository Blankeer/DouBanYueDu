package com.igexin.push.config;

import com.igexin.sdk.PushBuildConfig;

public class p {
    public static String a;
    public static String b;

    static {
        a = PushBuildConfig.sdk_conf_debug_level;
        b = "/sdcard/libs/";
    }
}
