package com.alipay.sdk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.TextView;
import com.alipay.sdk.cons.a;
import com.alipay.sdk.sys.b;
import com.alipay.sdk.sys.c;
import com.alipay.sdk.util.k;
import com.douban.book.reader.helper.AppUri;
import com.sina.weibo.sdk.exception.WeiboAuthException;
import com.ta.utdid2.device.UTDevice;
import com.tencent.connect.common.Constants;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Random;

public final class d {
    private static final String d = "virtualImeiAndImsi";
    private static final String e = "virtual_imei";
    private static final String f = "virtual_imsi";
    private static d g;
    String a;
    String b;
    String c;

    private String c() {
        return this.c;
    }

    private d() {
        this.b = "sdk-and-lite";
    }

    public static synchronized d a() {
        d dVar;
        synchronized (d.class) {
            if (g == null) {
                g = new d();
            }
            dVar = g;
        }
        return dVar;
    }

    public final synchronized void a(String str) {
        if (!TextUtils.isEmpty(str)) {
            PreferenceManager.getDefaultSharedPreferences(b.a().a).edit().putString(com.alipay.sdk.cons.b.i, str).commit();
            a.c = str;
        }
    }

    private static String d() {
        return Constants.VIA_TO_TYPE_QQ_GROUP;
    }

    private static String a(Context context) {
        return Float.toString(new TextView(context).getTextSize());
    }

    private String a(com.alipay.sdk.tid.b bVar) {
        String d;
        String str;
        Context context = b.a().a;
        com.alipay.sdk.util.b a = com.alipay.sdk.util.b.a(context);
        if (TextUtils.isEmpty(this.a)) {
            String a2 = k.a();
            String b = k.b();
            d = k.d(context);
            str = a.b;
            this.a = "Msp/15.0.0" + " (" + a2 + ";" + b + ";" + d + ";" + str.substring(0, str.indexOf("://")) + ";" + k.e(context) + ";" + Float.toString(new TextView(context).getTextSize());
        }
        d = com.alipay.sdk.util.b.b(context).p;
        str = "-1;-1";
        String str2 = Constants.VIA_TO_TYPE_QQ_GROUP;
        String a3 = a.a();
        String b2 = a.b();
        Context context2 = b.a().a;
        SharedPreferences sharedPreferences = context2.getSharedPreferences(d, 0);
        CharSequence string = sharedPreferences.getString(f, null);
        if (TextUtils.isEmpty(string)) {
            if (TextUtils.isEmpty(com.alipay.sdk.tid.b.a().a)) {
                Object utdid = UTDevice.getUtdid(b.a().a);
                string = TextUtils.isEmpty(utdid) ? b() : utdid.substring(3, 18);
            } else {
                string = com.alipay.sdk.util.b.a(context2).a();
            }
            sharedPreferences.edit().putString(f, string).commit();
        }
        CharSequence charSequence = string;
        Context context3 = b.a().a;
        SharedPreferences sharedPreferences2 = context3.getSharedPreferences(d, 0);
        string = sharedPreferences2.getString(e, null);
        if (TextUtils.isEmpty(string)) {
            if (TextUtils.isEmpty(com.alipay.sdk.tid.b.a().a)) {
                string = b();
            } else {
                string = com.alipay.sdk.util.b.a(context3).b();
            }
            sharedPreferences2.edit().putString(e, string).commit();
        }
        CharSequence charSequence2 = string;
        if (bVar != null) {
            this.c = bVar.b;
        }
        String replace = Build.MANUFACTURER.replace(";", " ");
        String replace2 = Build.MODEL.replace(";", " ");
        boolean c = b.c();
        String str3 = a.a;
        WifiInfo connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
        String ssid = connectionInfo != null ? connectionInfo.getSSID() : WeiboAuthException.DEFAULT_AUTH_ERROR_CODE;
        connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
        String bssid = connectionInfo != null ? connectionInfo.getBSSID() : "00";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.a).append(";").append(d).append(";").append(str).append(";").append(str2).append(";").append(a3).append(";").append(b2).append(";").append(this.c).append(";").append(replace).append(";").append(replace2).append(";").append(c).append(";").append(str3).append(";").append(c.a()).append(";").append(this.b).append(";").append(charSequence).append(";").append(charSequence2).append(";").append(ssid).append(";").append(bssid);
        if (bVar != null) {
            HashMap hashMap = new HashMap();
            hashMap.put(com.alipay.sdk.cons.b.c, bVar.a);
            hashMap.put(com.alipay.sdk.cons.b.g, UTDevice.getUtdid(b.a().a));
            utdid = a(context, hashMap);
            if (!TextUtils.isEmpty(utdid)) {
                stringBuilder.append(";").append(utdid);
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private static String e() {
        Context context = b.a().a;
        SharedPreferences sharedPreferences = context.getSharedPreferences(d, 0);
        String string = sharedPreferences.getString(e, null);
        if (TextUtils.isEmpty(string)) {
            if (TextUtils.isEmpty(com.alipay.sdk.tid.b.a().a)) {
                string = b();
            } else {
                string = com.alipay.sdk.util.b.a(context).b();
            }
            sharedPreferences.edit().putString(e, string).commit();
        }
        return string;
    }

    private static String f() {
        Context context = b.a().a;
        SharedPreferences sharedPreferences = context.getSharedPreferences(d, 0);
        String string = sharedPreferences.getString(f, null);
        if (TextUtils.isEmpty(string)) {
            if (TextUtils.isEmpty(com.alipay.sdk.tid.b.a().a)) {
                Object utdid = UTDevice.getUtdid(b.a().a);
                if (TextUtils.isEmpty(utdid)) {
                    string = b();
                } else {
                    string = utdid.substring(3, 18);
                }
            } else {
                string = com.alipay.sdk.util.b.a(context).a();
            }
            sharedPreferences.edit().putString(f, string).commit();
        }
        return string;
    }

    static String b() {
        return Long.toHexString(System.currentTimeMillis()) + (new Random().nextInt(9000) + AppUri.OPEN_URL);
    }

    private static String b(Context context) {
        WifiInfo connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
        if (connectionInfo != null) {
            return connectionInfo.getSSID();
        }
        return WeiboAuthException.DEFAULT_AUTH_ERROR_CODE;
    }

    private static String c(Context context) {
        WifiInfo connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
        if (connectionInfo != null) {
            return connectionInfo.getBSSID();
        }
        return "00";
    }

    static String a(Context context, HashMap<String, String> hashMap) {
        String str = Table.STRING_DEFAULT_VALUE;
        try {
            str = com.alipay.mobilesecuritysdk.face.a.a(context, hashMap);
        } catch (Exception e) {
        }
        return str;
    }
}
