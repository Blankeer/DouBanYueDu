package com.douban.book.reader.constant;

import android.content.Context;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.util.StringUtils;

public class DeviceType {
    public static final String ANDROID = "g";
    public static final String IOS = "p";
    public static final String IPAD = "a";
    public static final String WEB = "w";

    public static String getName(String type) {
        Context context = App.get();
        if (StringUtils.equals((CharSequence) type, IPAD)) {
            return context.getString(R.string.device_ipad);
        }
        if (StringUtils.equals((CharSequence) type, ANDROID)) {
            return context.getString(R.string.device_android);
        }
        if (StringUtils.equals((CharSequence) type, IOS)) {
            return context.getString(R.string.device_ios);
        }
        if (StringUtils.equals((CharSequence) type, WEB)) {
            return context.getString(R.string.device_webreader);
        }
        return context.getString(R.string.device_other);
    }
}
