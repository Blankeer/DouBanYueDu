package com.tencent.open.b;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.util.WorksIdentity;
import com.igexin.sdk.PushBuildConfig;
import com.tencent.open.a.f;
import io.realm.internal.Table;

/* compiled from: ProGuard */
public class a {
    protected static final String a;
    protected static final Uri b;

    static {
        a = a.class.getName();
        b = Uri.parse("content://telephony/carriers/preferapn");
    }

    public static String a(Context context) {
        int d = d(context);
        if (d == 2) {
            return "wifi";
        }
        if (d == 1) {
            return "cmwap";
        }
        if (d == 4) {
            return "cmnet";
        }
        if (d == 16) {
            return "uniwap";
        }
        if (d == 8) {
            return "uninet";
        }
        if (d == 64) {
            return "wap";
        }
        if (d == 32) {
            return StatConstant.JSON_KEY_NET;
        }
        if (d == AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) {
            return "ctwap";
        }
        if (d == WorksIdentity.ID_BIT_FINALIZE) {
            return "ctnet";
        }
        if (d == AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT) {
            return "3gnet";
        }
        if (d == AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT) {
            return "3gwap";
        }
        String b = b(context);
        if (b == null || b.length() == 0) {
            return PushBuildConfig.sdk_conf_debug_level;
        }
        return b;
    }

    public static String b(Context context) {
        try {
            Cursor query = context.getContentResolver().query(b, null, null, null, null);
            if (query == null) {
                return null;
            }
            query.moveToFirst();
            if (query.isAfterLast()) {
                if (query != null) {
                    query.close();
                }
                return null;
            }
            String string = query.getString(query.getColumnIndex(StatConstant.JSON_KEY_APP_NAME));
            if (query == null) {
                return string;
            }
            query.close();
            return string;
        } catch (SecurityException e) {
            f.e(a, "getApn has exception: " + e.getMessage());
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    public static String c(Context context) {
        try {
            Cursor query = context.getContentResolver().query(b, null, null, null, null);
            if (query == null) {
                return null;
            }
            query.moveToFirst();
            if (query.isAfterLast()) {
                if (query != null) {
                    query.close();
                }
                return null;
            }
            String string = query.getString(query.getColumnIndex("proxy"));
            if (query == null) {
                return string;
            }
            query.close();
            return string;
        } catch (SecurityException e) {
            f.e(a, "getApnProxy has exception: " + e.getMessage());
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    public static int d(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivityManager == null) {
                return TransportMediator.FLAG_KEY_MEDIA_NEXT;
            }
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null) {
                return TransportMediator.FLAG_KEY_MEDIA_NEXT;
            }
            if (activeNetworkInfo.getTypeName().toUpperCase().equals("WIFI")) {
                return 2;
            }
            String toLowerCase = activeNetworkInfo.getExtraInfo().toLowerCase();
            if (toLowerCase.startsWith("cmwap")) {
                return 1;
            }
            if (toLowerCase.startsWith("cmnet") || toLowerCase.startsWith("epc.tmobile.com")) {
                return 4;
            }
            if (toLowerCase.startsWith("uniwap")) {
                return 16;
            }
            if (toLowerCase.startsWith("uninet")) {
                return 8;
            }
            if (toLowerCase.startsWith("wap")) {
                return 64;
            }
            if (toLowerCase.startsWith(StatConstant.JSON_KEY_NET)) {
                return 32;
            }
            if (toLowerCase.startsWith("ctwap")) {
                return AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY;
            }
            if (toLowerCase.startsWith("ctnet")) {
                return WorksIdentity.ID_BIT_FINALIZE;
            }
            if (toLowerCase.startsWith("3gwap")) {
                return AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT;
            }
            if (toLowerCase.startsWith("3gnet")) {
                return AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT;
            }
            if (toLowerCase.startsWith("#777")) {
                toLowerCase = c(context);
                if (toLowerCase == null || toLowerCase.length() <= 0) {
                    return WorksIdentity.ID_BIT_FINALIZE;
                }
                return AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY;
            }
            return TransportMediator.FLAG_KEY_MEDIA_NEXT;
        } catch (Exception e) {
            f.e(a, "getMProxyType has exception: " + e.getMessage());
        }
    }

    public static String e(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null) {
            return "MOBILE";
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.getTypeName();
        }
        return "MOBILE";
    }
}
