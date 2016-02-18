package com.igexin.push.config;

import android.text.TextUtils;
import com.igexin.a.a.c.a;
import com.igexin.push.core.g;

public class SDKUrlConfig {
    public static String[] AMP_ADDRESS_IPS;
    public static String[] BI_ADDRESS_IPS;
    public static String[] CONFIG_ADDRESS_IPS;
    public static String[] INC_ADDRESS_IPS;
    public static String[] LBS_ADDRESS_IPS;
    public static String[] LOG_ADDRESS_IPS;
    public static String[] STATE_ADDRESS_IPS;
    public static String[] XFR_ADDRESS_IPS;
    public static String[] XFR_ADDRESS_IPS_BAK;
    private static String[] a;
    private static String b;
    private static String c;

    static {
        b = "HZ";
        XFR_ADDRESS_IPS = new String[]{"socket://sdk.open.talk.igexin.com:5224", "socket://sdk.open.talk.getui.net:5224", "socket://sdk.open.talk.gepush.com:5224"};
        XFR_ADDRESS_IPS_BAK = new String[]{"socket://sdk.open.talk.igexin.com:5224"};
        BI_ADDRESS_IPS = new String[]{"http://sdk.open.phone.igexin.com/api.php"};
        CONFIG_ADDRESS_IPS = new String[]{"http://sdk.open.phone.igexin.com/api.php"};
        STATE_ADDRESS_IPS = new String[]{"http://sdk.open.phone.igexin.com/api.php"};
        LOG_ADDRESS_IPS = new String[]{"http://sdk.open.phone.igexin.com/api.php"};
        AMP_ADDRESS_IPS = new String[]{"http://sdk.open.amp.igexin.com/api.htm"};
        LBS_ADDRESS_IPS = new String[]{"http://sdk.open.lbs.igexin.com/api.htm"};
        INC_ADDRESS_IPS = new String[]{"http://sdk.open.inc2.igexin.com/api.php"};
    }

    public static String getAmpServiceUrl() {
        return AMP_ADDRESS_IPS[0] + "?format=json&t=1";
    }

    public static String getBiUploadServiceUrl() {
        return BI_ADDRESS_IPS[0] + "?format=json&t=1";
    }

    public static String getCmAddress() {
        if (c == null) {
            a.b(k.a + " get cm address : " + XFR_ADDRESS_IPS[0]);
            return XFR_ADDRESS_IPS[0];
        }
        a.b(k.a + " get cm address : " + c);
        return c;
    }

    public static String getConfigServiceUrl() {
        return CONFIG_ADDRESS_IPS[0] + "?format=json&t=1";
    }

    public static String[] getIdcConfigUrl() {
        return a;
    }

    public static String getIncreaseServiceUrl() {
        return INC_ADDRESS_IPS[0] + "?format=json&t=1";
    }

    public static String getLbsServiceUrl() {
        return LBS_ADDRESS_IPS[0] + "?format=json&t=1";
    }

    public static String getLocation() {
        return b;
    }

    public static String getLogServiceUrl() {
        return LOG_ADDRESS_IPS[0] + "?format=json&t=1";
    }

    public static String getStatServiceUrl() {
        return STATE_ADDRESS_IPS[0] + "?format=json&t=1";
    }

    public static void setCmAddress(String str) {
        a.b(k.a + " set cm address : " + str);
        c = str;
    }

    public static void setIdcConfigUrl(String[] strArr) {
        a = strArr;
    }

    public static void setLocation(String str) {
        if (!TextUtils.isEmpty(str)) {
            g.d = str;
            b = str;
        }
    }
}
