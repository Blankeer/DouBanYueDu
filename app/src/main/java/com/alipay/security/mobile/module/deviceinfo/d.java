package com.alipay.security.mobile.module.deviceinfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import com.alipay.security.mobile.module.commonutils.a;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.realm.internal.Table;
import java.io.File;

public final class d {
    private static d a;

    static {
        a = new d();
    }

    private d() {
    }

    public static d a() {
        return a;
    }

    private static String a(String str, String str2) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke(null, new Object[]{str, str2});
        } catch (Exception e) {
            return str2;
        }
    }

    public static boolean a(Context context) {
        try {
            if (Build.HARDWARE.contains("goldfish") || Build.PRODUCT.contains(CommonUtils.SDK) || Build.FINGERPRINT.contains("generic")) {
                return true;
            }
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            if (telephonyManager != null) {
                Object obj;
                String deviceId = telephonyManager.getDeviceId();
                if (deviceId != null) {
                    int length = deviceId.length();
                    if (length != 0) {
                        int i = 0;
                        while (i < length) {
                            if (!Character.isWhitespace(deviceId.charAt(i)) && deviceId.charAt(i) != '0') {
                                obj = null;
                                break;
                            }
                            i++;
                        }
                        i = 1;
                        if (obj != null) {
                            return true;
                        }
                    }
                }
                obj = 1;
                if (obj != null) {
                    return true;
                }
            }
            return a.a(Secure.getString(context.getContentResolver(), "android_id"));
        } catch (Exception e) {
            return false;
        }
    }

    public static String b() {
        return AbstractSpiCall.ANDROID_CLIENT_TYPE;
    }

    private static String b(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivityManager.getActiveNetworkInfo().isConnected()) {
                String typeName = connectivityManager.getActiveNetworkInfo().getTypeName();
                return typeName == null ? false : typeName.equalsIgnoreCase("WIFI") ? "WIFI" : connectivityManager.getActiveNetworkInfo().getExtraInfo();
            }
        } catch (Exception e) {
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    public static boolean c() {
        String[] strArr = new String[]{"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        int i = 0;
        while (i < 5) {
            try {
                if (new File(strArr[i] + "su").exists()) {
                    return true;
                }
                i++;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static String d() {
        return Build.BOARD;
    }

    public static String e() {
        return Build.BRAND;
    }

    public static String f() {
        return Build.DEVICE;
    }

    public static String g() {
        return Build.DISPLAY;
    }

    public static String h() {
        return VERSION.INCREMENTAL;
    }

    public static String i() {
        return Build.MANUFACTURER;
    }

    public static String j() {
        return Build.MODEL;
    }

    public static String k() {
        return Build.PRODUCT;
    }

    public static String l() {
        return VERSION.RELEASE;
    }

    public static String m() {
        return VERSION.SDK;
    }

    public static String n() {
        return Build.TAGS;
    }

    public static String o() {
        return a("ro.kernel.qemu", Constants.VIA_RESULT_SUCCESS);
    }

    private static String p() {
        return a("gsm.sim.state", Table.STRING_DEFAULT_VALUE);
    }

    private static String q() {
        return a("gsm.sim.state.2", Table.STRING_DEFAULT_VALUE);
    }

    private static String r() {
        return a("wifi.interface", Table.STRING_DEFAULT_VALUE);
    }

    private static String s() {
        return a("sys.usb.state", Table.STRING_DEFAULT_VALUE);
    }
}
