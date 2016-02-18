package com.douban.amonsul;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import com.douban.amonsul.device.AppInfo;
import com.douban.amonsul.device.DeviceInfo;
import com.douban.amonsul.model.StatEvent;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.realm.internal.Table;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StatUtils {
    private static final String TAG;

    static {
        TAG = StatUtils.class.getSimpleName();
    }

    public static String stackTraceToString(StackTraceElement[] stackTraces) {
        StringBuffer exception = new StringBuffer();
        for (StackTraceElement element : stackTraces) {
            exception.append(element.toString());
            exception.append("\n\t");
        }
        return exception.toString();
    }

    public static String getCauseTrace(Throwable ex) {
        if (ex.getCause() == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return "Caused by: " + stackTraceToString(ex.getCause().getStackTrace()) + "\n\t" + getCauseTrace(ex.getCause());
    }

    public static String getLocalTimeStamp() {
        return (System.currentTimeMillis() / 1000) + Table.STRING_DEFAULT_VALUE;
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            if (!hasPermission(context, "android.permission.ACCESS_NETWORK_STATE")) {
                return true;
            }
            NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public static String getMD5String(String value) throws NoSuchAlgorithmException {
        byte[] hash = MessageDigest.getInstance(CommonUtils.SHA1_INSTANCE).digest(value.getBytes());
        Formatter formatter = new Formatter();
        int len$ = hash.length;
        for (int i$ = 0; i$ < len$; i$++) {
            formatter.format("%02x", new Object[]{Byte.valueOf(arr$[i$])});
        }
        return formatter.toString();
    }

    public static double latitudeFormat(double lat) {
        double finalLat = decimalFormat(lat);
        return validLatitude(finalLat) ? finalLat : 0.0d;
    }

    public static double longitudeFormat(double lng) {
        double finalLng = decimalFormat(lng);
        return validLongitude(finalLng) ? finalLng : 0.0d;
    }

    public static boolean validLatitude(double lat) {
        if (lat < -90.0d || lat > 90.0d) {
            return false;
        }
        return true;
    }

    public static boolean validLongitude(double lng) {
        if (lng < -180.0d || lng > 180.0d) {
            return false;
        }
        return true;
    }

    public static double decimalFormat(double num) {
        try {
            return Double.valueOf(new DecimalFormat("#0.###").format(num)).doubleValue();
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
            return 0.0d;
        }
    }

    public static boolean hasPermission(Context context, String permission) {
        return context.checkPermission(permission, Process.myPid(), Process.myUid()) == 0;
    }

    public static JSONArray arrayEventToJsonArray(Context context, List<StatEvent> events) {
        if (events == null) {
            return null;
        }
        JSONArray jSONArray = null;
        try {
            int nCount = events.size();
            int i = 0;
            JSONArray jArr = null;
            while (i < nCount) {
                try {
                    JSONObject evtJson = ((StatEvent) events.get(i)).toJson(context, true);
                    if (jArr == null) {
                        jSONArray = new JSONArray();
                    } else {
                        jSONArray = jArr;
                    }
                    jSONArray.put(i, evtJson);
                    i++;
                    jArr = jSONArray;
                } catch (JSONException e) {
                    return jArr;
                }
            }
            return jArr;
        } catch (JSONException e2) {
            return jSONArray;
        }
    }

    public static byte[] getEventsBytes(Context context, JSONArray events) {
        try {
            JSONObject json = new JSONObject();
            json.put(StatConstant.KEY_APP, AppInfo.get(context));
            json.put(StatConstant.KEY_DEVICE, DeviceInfo.get(context));
            json.put(StatConstant.KEY_EVENT, events);
            JSONObject j = new JSONObject();
            JSONArray json1 = new JSONArray();
            json1.put(json);
            j.put("logs", json1);
            return j.toString().getBytes();
        } catch (Throwable e) {
            StatLogger.e(TAG, e);
            return null;
        }
    }

    public static byte[] getEventsBytes(Context context, String eventsString) {
        try {
            JSONObject json = new JSONObject();
            json.put(StatConstant.KEY_APP, AppInfo.get(context));
            json.put(StatConstant.KEY_DEVICE, DeviceInfo.get(context));
            json.put(StatConstant.KEY_EVENT, new JSONArray(eventsString));
            JSONObject j = new JSONObject();
            JSONArray json1 = new JSONArray();
            json1.put(json);
            j.put("logs", json1);
            return j.toString().getBytes();
        } catch (Throwable e) {
            StatLogger.e(TAG, e);
            return null;
        }
    }
}
