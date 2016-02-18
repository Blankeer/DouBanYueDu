package com.douban.book.reader.util;

public class DebugSwitch {
    public static boolean on(String name) {
        return AppInfo.isDebug() && Pref.ofApp().getBoolean(name, false);
    }
}
