package com.tencent.a.a.a.a;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import com.tencent.wxop.stat.b.g;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import org.json.JSONObject;

public final class h {
    private static void a(String str, Throwable th) {
        Log.e("MID", str, th);
    }

    static void a(JSONObject jSONObject, String str, String str2) {
        if (d(str2)) {
            jSONObject.put(str, str2);
        }
    }

    static boolean a(Context context, String str) {
        try {
            return context.getPackageManager().checkPermission(str, context.getPackageName()) == 0;
        } catch (Throwable th) {
            a("checkPermission error", th);
            return false;
        }
    }

    static String b(Context context) {
        try {
            if (a(context, "android.permission.READ_PHONE_STATE")) {
                String deviceId = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
                if (deviceId != null) {
                    return deviceId;
                }
            }
            Log.i("MID", "Could not get permission of android.permission.READ_PHONE_STATE");
        } catch (Throwable th) {
            Log.w("MID", th);
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    static String c(Context context) {
        if (a(context, "android.permission.ACCESS_WIFI_STATE")) {
            try {
                WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
                return wifiManager == null ? Table.STRING_DEFAULT_VALUE : wifiManager.getConnectionInfo().getMacAddress();
            } catch (Exception e) {
                Log.i("MID", "get wifi address error" + e);
                return Table.STRING_DEFAULT_VALUE;
            }
        }
        Log.i("MID", "Could not get permission of android.permission.ACCESS_WIFI_STATE");
        return Table.STRING_DEFAULT_VALUE;
    }

    static boolean d(String str) {
        return (str == null || str.trim().length() == 0) ? false : true;
    }

    public static boolean e(String str) {
        return str != null && str.trim().length() >= 40;
    }

    static String f(String str) {
        if (str == null) {
            return null;
        }
        if (VERSION.SDK_INT < 8) {
            return str;
        }
        try {
            return new String(g.c(Base64.decode(str.getBytes(HttpRequest.CHARSET_UTF8), 0)), HttpRequest.CHARSET_UTF8).trim().replace("\t", Table.STRING_DEFAULT_VALUE).replace("\n", Table.STRING_DEFAULT_VALUE).replace("\r", Table.STRING_DEFAULT_VALUE);
        } catch (Throwable th) {
            a("decode error", th);
            return str;
        }
    }

    static String g(String str) {
        if (str == null) {
            return null;
        }
        if (VERSION.SDK_INT < 8) {
            return str;
        }
        try {
            return new String(Base64.encode(g.b(str.getBytes(HttpRequest.CHARSET_UTF8)), 0), HttpRequest.CHARSET_UTF8).trim().replace("\t", Table.STRING_DEFAULT_VALUE).replace("\n", Table.STRING_DEFAULT_VALUE).replace("\r", Table.STRING_DEFAULT_VALUE);
        } catch (Throwable th) {
            a("decode error", th);
            return str;
        }
    }
}
