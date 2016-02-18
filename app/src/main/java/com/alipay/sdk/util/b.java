package com.alipay.sdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public final class b {
    private static final String b = "00:00:00:00:00:00";
    private static b e;
    public String a;
    private String c;
    private String d;

    static {
        e = null;
    }

    public static b a(Context context) {
        if (e == null) {
            e = new b(context);
        }
        return e;
    }

    private b(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            b(telephonyManager.getDeviceId());
            String subscriberId = telephonyManager.getSubscriberId();
            if (subscriberId != null) {
                subscriberId = (subscriberId + "000000000000000").substring(0, 15);
            }
            this.c = subscriberId;
            this.a = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getMacAddress();
            if (TextUtils.isEmpty(this.a)) {
                this.a = b;
            }
        } catch (Exception e) {
            if (TextUtils.isEmpty(this.a)) {
                this.a = b;
            }
        } catch (Throwable th) {
            if (TextUtils.isEmpty(this.a)) {
                this.a = b;
            }
        }
    }

    public final String a() {
        if (TextUtils.isEmpty(this.c)) {
            this.c = "000000000000000";
        }
        return this.c;
    }

    public final String b() {
        if (TextUtils.isEmpty(this.d)) {
            this.d = "000000000000000";
        }
        return this.d;
    }

    private void a(String str) {
        if (str != null) {
            str = (str + "000000000000000").substring(0, 15);
        }
        this.c = str;
    }

    private void b(String str) {
        if (str != null) {
            byte[] bytes = str.getBytes();
            int i = 0;
            while (i < bytes.length) {
                if (bytes[i] < (byte) 48 || bytes[i] > 57) {
                    bytes[i] = (byte) 48;
                }
                i++;
            }
            str = (new String(bytes) + "000000000000000").substring(0, 15);
        }
        this.d = str;
    }

    private String c() {
        String str = b() + "|";
        Object a = a();
        if (TextUtils.isEmpty(a)) {
            return str + "000000000000000";
        }
        return str + a;
    }

    private String d() {
        return this.a;
    }

    public static g b(Context context) {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.getType() == 0) {
                return g.a(activeNetworkInfo.getSubtype());
            }
            if (activeNetworkInfo == null || activeNetworkInfo.getType() != 1) {
                return g.NONE;
            }
            return g.WIFI;
        } catch (Exception e) {
            return g.NONE;
        }
    }

    public static String c(Context context) {
        b a = a(context);
        String str = a.b() + "|";
        Object a2 = a.a();
        return (TextUtils.isEmpty(a2) ? str + "000000000000000" : str + a2).substring(0, 8);
    }
}
