package com.alipay.sdk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.widget.TextView;
import com.alipay.sdk.cons.a;
import com.alipay.sdk.util.c;
import com.alipay.sdk.util.k;
import com.j256.ormlite.stmt.query.SimpleComparison;
import com.sina.weibo.sdk.exception.WeiboAuthException;
import com.ta.utdid2.device.UTDevice;
import com.tencent.connect.common.Constants;
import io.realm.internal.Table;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

public final class b {
    private static final String a = "Msp-Param";

    public static e a(c cVar, String str, JSONObject jSONObject) {
        com.alipay.sdk.sys.b a = com.alipay.sdk.sys.b.a();
        com.alipay.sdk.tid.b a2 = com.alipay.sdk.tid.b.a();
        JSONObject a3 = c.a(null, jSONObject);
        try {
            String d;
            String str2;
            Object utdid;
            a3.put(com.alipay.sdk.cons.b.c, a2.a);
            String str3 = com.alipay.sdk.cons.b.b;
            d b = a.b();
            Context context = com.alipay.sdk.sys.b.a().a;
            com.alipay.sdk.util.b a4 = com.alipay.sdk.util.b.a(context);
            if (TextUtils.isEmpty(b.a)) {
                String a5 = k.a();
                String b2 = k.b();
                d = k.d(context);
                str2 = a.b;
                b.a = "Msp/15.0.0" + " (" + a5 + ";" + b2 + ";" + d + ";" + str2.substring(0, str2.indexOf("://")) + ";" + k.e(context) + ";" + Float.toString(new TextView(context).getTextSize());
            }
            d = com.alipay.sdk.util.b.b(context).p;
            str2 = "-1;-1";
            String str4 = Constants.VIA_TO_TYPE_QQ_GROUP;
            String a6 = a4.a();
            String b3 = a4.b();
            Context context2 = com.alipay.sdk.sys.b.a().a;
            SharedPreferences sharedPreferences = context2.getSharedPreferences("virtualImeiAndImsi", 0);
            CharSequence string = sharedPreferences.getString("virtual_imsi", null);
            if (TextUtils.isEmpty(string)) {
                if (TextUtils.isEmpty(com.alipay.sdk.tid.b.a().a)) {
                    utdid = UTDevice.getUtdid(com.alipay.sdk.sys.b.a().a);
                    string = TextUtils.isEmpty(utdid) ? d.b() : utdid.substring(3, 18);
                } else {
                    string = com.alipay.sdk.util.b.a(context2).a();
                }
                sharedPreferences.edit().putString("virtual_imsi", string).commit();
            }
            CharSequence charSequence = string;
            context2 = com.alipay.sdk.sys.b.a().a;
            SharedPreferences sharedPreferences2 = context2.getSharedPreferences("virtualImeiAndImsi", 0);
            string = sharedPreferences2.getString("virtual_imei", null);
            if (TextUtils.isEmpty(string)) {
                string = TextUtils.isEmpty(com.alipay.sdk.tid.b.a().a) ? d.b() : com.alipay.sdk.util.b.a(context2).b();
                sharedPreferences2.edit().putString("virtual_imei", string).commit();
            }
            CharSequence charSequence2 = string;
            if (a2 != null) {
                b.c = a2.b;
            }
            String replace = Build.MANUFACTURER.replace(";", " ");
            String replace2 = Build.MODEL.replace(";", " ");
            boolean c = com.alipay.sdk.sys.b.c();
            String str5 = a4.a;
            WifiInfo connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
            String ssid = connectionInfo != null ? connectionInfo.getSSID() : WeiboAuthException.DEFAULT_AUTH_ERROR_CODE;
            connectionInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
            String bssid = connectionInfo != null ? connectionInfo.getBSSID() : "00";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(b.a).append(";").append(d).append(";").append(str2).append(";").append(str4).append(";").append(a6).append(";").append(b3).append(";").append(b.c).append(";").append(replace).append(";").append(replace2).append(";").append(c).append(";").append(str5).append(";").append(com.alipay.sdk.sys.c.a()).append(";").append(b.b).append(";").append(charSequence).append(";").append(charSequence2).append(";").append(ssid).append(";").append(bssid);
            if (a2 != null) {
                HashMap hashMap = new HashMap();
                hashMap.put(com.alipay.sdk.cons.b.c, a2.a);
                hashMap.put(com.alipay.sdk.cons.b.g, UTDevice.getUtdid(com.alipay.sdk.sys.b.a().a));
                utdid = d.a(context, hashMap);
                if (!TextUtils.isEmpty(utdid)) {
                    stringBuilder.append(";").append(utdid);
                }
            }
            stringBuilder.append(")");
            a3.put(str3, stringBuilder.toString());
            a3.put(com.alipay.sdk.cons.b.e, k.b(a.a));
            a3.put(com.alipay.sdk.cons.b.f, k.a(a.a));
            a3.put(com.alipay.sdk.cons.b.d, str);
            a3.put(com.alipay.sdk.cons.b.h, a.d);
            a3.put(com.alipay.sdk.cons.b.g, UTDevice.getUtdid(a.a));
            a3.put(com.alipay.sdk.cons.b.j, a2.b);
        } catch (Throwable th) {
        }
        a aVar = new a();
        aVar.a = a.b;
        aVar.b = "com.alipay.mobilecashier";
        aVar.c = "/cashier/main";
        aVar.d = "4.0.2";
        e eVar = new e(aVar, a3);
        eVar.d = true;
        a(cVar, eVar, str);
        return eVar;
    }

    private static e a(JSONObject jSONObject, boolean z) {
        a aVar = new a();
        aVar.a = a.b;
        aVar.b = "com.alipay.mobilecashier";
        aVar.c = "/cashier/main";
        aVar.d = "4.0.2";
        if (jSONObject == null) {
            return null;
        }
        e eVar = new e(aVar, jSONObject);
        eVar.d = z;
        return eVar;
    }

    private static void a(c cVar, e eVar, String str) {
        if (!TextUtils.isEmpty(str)) {
            String[] split = str.split("&");
            if (split.length != 0) {
                Object obj = null;
                Object obj2 = null;
                Object obj3 = null;
                Object obj4 = null;
                for (String str2 : split) {
                    if (TextUtils.isEmpty(obj4)) {
                        obj4 = !str2.contains("biz_type") ? null : d(str2);
                    }
                    if (TextUtils.isEmpty(obj3)) {
                        obj3 = !str2.contains("biz_no") ? null : d(str2);
                    }
                    if (TextUtils.isEmpty(obj2)) {
                        obj2 = (!str2.contains("trade_no") || str2.startsWith("out_trade_no")) ? null : d(str2);
                    }
                    if (TextUtils.isEmpty(obj)) {
                        if (str2.contains("app_userid")) {
                            obj = d(str2);
                        } else {
                            obj = null;
                        }
                    }
                }
                StringBuilder stringBuilder = new StringBuilder();
                if (!TextUtils.isEmpty(obj4)) {
                    stringBuilder.append("biz_type=" + obj4 + ";");
                }
                if (!TextUtils.isEmpty(obj3)) {
                    stringBuilder.append("biz_no=" + obj3 + ";");
                }
                if (!TextUtils.isEmpty(obj2)) {
                    stringBuilder.append("trade_no=" + obj2 + ";");
                }
                if (!TextUtils.isEmpty(obj)) {
                    stringBuilder.append("app_userid=" + obj + ";");
                }
                if (stringBuilder.length() != 0) {
                    String stringBuilder2 = stringBuilder.toString();
                    if (stringBuilder2.endsWith(";")) {
                        stringBuilder2 = stringBuilder2.substring(0, stringBuilder2.length() - 1);
                    }
                    cVar.b = new Header[]{new BasicHeader(a, stringBuilder2)};
                    eVar.b = new WeakReference(cVar);
                }
            }
        }
    }

    private static String a(String str) {
        if (str.contains("biz_type")) {
            return d(str);
        }
        return null;
    }

    private static String b(String str) {
        if (str.contains("biz_no")) {
            return d(str);
        }
        return null;
    }

    private static String c(String str) {
        if (!str.contains("trade_no") || str.startsWith("out_trade_no")) {
            return null;
        }
        return d(str);
    }

    private static String d(String str) {
        String[] split = str.split(SimpleComparison.EQUAL_TO_OPERATION);
        if (split.length <= 1) {
            return null;
        }
        String str2 = split[1];
        if (str2.contains("\"")) {
            return str2.replaceAll("\"", Table.STRING_DEFAULT_VALUE);
        }
        return str2;
    }

    private static String e(String str) {
        if (str.contains("app_userid")) {
            return d(str);
        }
        return null;
    }

    private static void a(c cVar, HttpResponse httpResponse) {
        Header[] headers = httpResponse.getHeaders(a);
        if (cVar != null && headers.length > 0) {
            cVar.b = headers;
        }
    }

    public static byte[] a(byte[] bArr) {
        byte[] bArr2 = null;
        try {
            InputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
            GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
            byte[] bArr3 = new byte[CodedOutputStream.DEFAULT_BUFFER_SIZE];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (true) {
                int read = gZIPInputStream.read(bArr3, 0, CodedOutputStream.DEFAULT_BUFFER_SIZE);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr3, 0, read);
            }
            bArr2 = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            gZIPInputStream.close();
            byteArrayInputStream.close();
        } catch (Exception e) {
        }
        return bArr2;
    }

    public static e a() {
        a aVar = new a();
        aVar.a = a.b;
        aVar.b = "com.alipay.mobilecashier";
        aVar.c = "/device/findAccount";
        aVar.d = "3.0.0";
        com.alipay.sdk.sys.b a = com.alipay.sdk.sys.b.a();
        com.alipay.sdk.tid.b a2 = com.alipay.sdk.tid.b.a();
        JSONObject jSONObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(a2.a)) {
                jSONObject.put(com.alipay.sdk.cons.b.c, a2.a);
            }
            jSONObject.put(com.alipay.sdk.cons.b.g, UTDevice.getUtdid(a.a));
            jSONObject.put(com.alipay.sdk.cons.b.h, a.d);
            jSONObject.put(com.alipay.sdk.cons.b.j, a2.b);
            jSONObject.put("imei", com.alipay.sdk.util.b.a(a.a).b());
            jSONObject.put("imsi", com.alipay.sdk.util.b.a(a.a).a());
        } catch (JSONException e) {
        }
        return new e(aVar, jSONObject);
    }
}
