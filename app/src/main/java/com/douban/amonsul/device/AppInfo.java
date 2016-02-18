package com.douban.amonsul.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.media.TransportMediator;
import android.text.TextUtils;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatUtils;
import io.realm.internal.Table;
import java.util.Arrays;
import org.json.JSONObject;

public class AppInfo {
    private static final String TAG;
    private static String sApiKey;
    private static String sAppName;
    private static double[] sBindLocation;
    private static String sChannel;
    private static double[] sLocation;
    private static String sToken;
    private static long sUserId;
    private static String sVersionName;

    static {
        TAG = AppInfo.class.getSimpleName();
        sUserId = 0;
        sToken = Table.STRING_DEFAULT_VALUE;
        sApiKey = Table.STRING_DEFAULT_VALUE;
        sVersionName = Table.STRING_DEFAULT_VALUE;
        sAppName = Table.STRING_DEFAULT_VALUE;
        sChannel = Table.STRING_DEFAULT_VALUE;
    }

    public static void checkAppInfo() {
        StringBuilder errorMessage = new StringBuilder();
        if (TextUtils.isEmpty(sApiKey)) {
            errorMessage.append("apiKey");
        }
        if (TextUtils.isEmpty(sChannel)) {
            errorMessage.append(" and channel");
        }
        if (TextUtils.isEmpty(sAppName)) {
            errorMessage.append(" and appName");
        }
        if (errorMessage.length() > 0) {
            errorMessage.append(" not set, please using MobileStat.setAppInfo(apiKey,channel,appName).");
            StatLogger.e(TAG, errorMessage.toString());
        }
    }

    public static long getUserId() {
        return sUserId;
    }

    public static void setUserId(long userId) {
        if (userId > 0) {
            sUserId = userId;
        }
    }

    public static String getToken() {
        return sToken;
    }

    public static void setToken(String token) {
        if (token != null) {
            sToken = token;
        }
    }

    public static void setAppName(String appName) {
        if (appName != null) {
            sAppName = appName;
        }
    }

    public static void setChannel(String channel) {
        if (channel != null) {
            sChannel = channel;
        }
    }

    public static void setApiKey(String apiKey) {
        if (apiKey != null) {
            sApiKey = apiKey;
        }
    }

    public static String getApikey(Context context) {
        return sApiKey;
    }

    public static String getAppName(Context context) {
        return sAppName;
    }

    public static synchronized String getVersionName(Context context) {
        String str;
        synchronized (AppInfo.class) {
            if (TextUtils.isEmpty(sVersionName)) {
                sVersionName = AppInfoUtils.getVersionName(context);
            }
            str = sVersionName;
        }
        return str;
    }

    public static String getNetworkType(Context context) {
        String net = AppInfoUtils.getNetworkType(context);
        if (TextUtils.isEmpty(net)) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return net;
    }

    public static String getSdkVersion() {
        return StatConstant.SDK_VERSION;
    }

    public static void bindLocation(double lat, double lng) {
        if (StatUtils.validLatitude(lat) && StatUtils.validLongitude(lng)) {
            sBindLocation = new double[]{lat, lng};
            sLocation = null;
        }
    }

    public static String getAppChannel(Context context) {
        if (TextUtils.isEmpty(sChannel)) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return sChannel;
    }

    public static double[] getLocation(Context context) {
        if (sBindLocation != null) {
            return sBindLocation;
        }
        if (sLocation != null) {
            return sLocation;
        }
        Location loc = AppInfoUtils.getLocation(context);
        if (MobileStat.DEBUG) {
            StatLogger.v(TAG, "getLocation() " + loc);
        }
        if (loc == null) {
            return new double[]{0.0d, 0.0d};
        }
        double lat = StatUtils.latitudeFormat(loc.getLatitude());
        double lng = StatUtils.longitudeFormat(loc.getLongitude());
        sLocation = new double[]{lat, lng};
        return sLocation;
    }

    public static JSONObject get(Context context) {
        JSONObject json = new JSONObject();
        try {
            json.put(StatConstant.JSON_KEY_APP_VERSION, getVersionName(context));
            json.put(StatConstant.JSON_KEY_USERID, getUserId());
            json.put(StatConstant.JSON_KEY_SDK_VERSION, getSdkVersion());
            json.put(StatConstant.JSON_KEY_NET, getNetworkType(context));
            json.put(StatConstant.JSON_KEY_CHANNEL, getAppChannel(context));
            json.put(StatConstant.JSON_KEY_APP_NAME, getAppName(context));
            double[] loc = getLocation(context);
            json.put(StatConstant.JSON_KEY_LAC, loc[0]);
            json.put(StatConstant.JSON_KEY_LNG, loc[1]);
        } catch (Throwable e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
        }
        return json;
    }

    @TargetApi(12)
    public static void loadMetaData(Context context) {
        try {
            Bundle metaData = context.getPackageManager().getApplicationInfo(context.getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT).metaData;
            if (metaData != null) {
                setApiKey(metaData.getString(StatConstant.DOUBAN_APIKEY));
                setAppName(metaData.getString(StatConstant.DOUBAN_APP_NAME));
                setChannel(metaData.getString(StatConstant.DOUBAN_CHANNEL));
            }
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public static String dump() {
        return "AppInfo: { ApiKey:" + sApiKey + " AppName:" + sAppName + " Channel:" + sChannel + " Version:" + sVersionName + " UserId:" + sUserId + " Location:" + Arrays.toString(sLocation) + " }";
    }
}
