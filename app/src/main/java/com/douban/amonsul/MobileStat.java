package com.douban.amonsul;

import android.content.Context;
import com.douban.amonsul.core.MobileStatManager;
import com.douban.amonsul.device.AppInfo;
import com.douban.amonsul.device.DeviceInfo;
import com.douban.book.reader.constant.Char;
import io.realm.internal.Table;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MobileStat {
    public static volatile boolean DEBUG = false;
    public static final String KEY_STAT_EVENT_ONLAUNCH = "onLaunch";
    public static final String KEY_STAT_EVENT_ONPAUSE = "pause";
    public static final String KEY_STAT_EVENT_ONRESUME = "resume";
    private static final String TAG;

    static {
        DEBUG = false;
        TAG = MobileStat.class.getSimpleName();
    }

    public static void init(Context context, long userId) {
        try {
            AppInfo.loadMetaData(context);
            MobileStatManager manager = MobileStatManager.getInstance(context);
            if (manager != null) {
                manager.onCreate(context);
            }
            if (userId > 0) {
                setUserId(userId);
            }
            DeviceInfo.get(context);
            AppInfo.get(context);
            if (DEBUG) {
                StatLogger.v(TAG, "DeviceInfo: " + String.valueOf(DeviceInfo.get(context)));
                StatLogger.v(TAG, "AppInfo: " + String.valueOf(AppInfo.get(context)));
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public static void onBind(Context context, long userId) {
        AppInfo.setUserId(userId);
    }

    public static void unBind(Context context) {
        setUserId(0);
    }

    public static void onBindLocation(double aLat, double aLng) {
        AppInfo.bindLocation(aLat, aLng);
    }

    public static void onCreate(Context context) {
        onEvent(context, KEY_STAT_EVENT_ONLAUNCH);
    }

    public static void onResume(Context context) {
        onEvent(context, KEY_STAT_EVENT_ONRESUME);
    }

    public static void onPause(Context context) {
        onEvent(context, KEY_STAT_EVENT_ONPAUSE);
    }

    @Deprecated
    public static void onLaunch(Context context) {
        onEvent(context, KEY_STAT_EVENT_ONLAUNCH);
    }

    public static void onEvent(Context context, String eventId) {
        onEvent(context, eventId, 0);
    }

    public static void onEvent(Context context, String eventId, int count) {
        onEvent(context, eventId, Table.STRING_DEFAULT_VALUE, count);
    }

    public static void onEvent(Context context, String eventId, String extra) {
        onEvent(context, eventId, extra, 1);
    }

    public static void onEvent(Context context, String eventId, String extra, String attributes) {
        onEvent(context, eventId, extra, 1, Table.STRING_DEFAULT_VALUE, false, attributes);
    }

    public static void onEvent(Context context, String eventId, String extra, int count) {
        onEvent(context, eventId, extra, count, Table.STRING_DEFAULT_VALUE, false);
    }

    public static void onRealTimeEvent(Context context, String eventId) {
        onRealTimeEvent(context, eventId, Table.STRING_DEFAULT_VALUE);
    }

    public static void onRealTimeEvent(Context context, String eventId, String extra) {
        onRealTimeEvent(context, eventId, extra, 1, Table.STRING_DEFAULT_VALUE);
    }

    public static void onRealTimeEvent(Context context, String eventId, String extra, String attributes) {
        onEvent(context, eventId, extra, 1, Table.STRING_DEFAULT_VALUE, true, attributes);
    }

    public static void onRealTimeEvent(Context context, String eventId, String extra, int count, String action) {
        onEvent(context, eventId, extra, count, action, true);
    }

    public static void onEvent(Context context, String eventId, String extra, int count, String action, boolean realTime) {
        onEvent(context, eventId, extra, count, action, realTime, Table.STRING_DEFAULT_VALUE);
    }

    public static void onEvent(Context context, String eventId, String extra, int count, String action, boolean realTime, String attributes) {
        try {
            MobileStatManager manager = MobileStatManager.getInstance(context);
            if (manager != null) {
                manager.onEvent(context, eventId, extra, count, action, realTime, attributes);
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public static void onEventBegin(Context context, String eventId) {
        onEventBegin(context, eventId, Table.STRING_DEFAULT_VALUE);
    }

    public static void onEventBegin(Context context, String eventId, String extra) {
        onEvent(context, eventId, extra, 1, StatConstant.STAT_EVENT_ACTION_BEGIN, false);
    }

    public static void onEventEnd(Context context, String eventId) {
        onEventEnd(context, eventId, Table.STRING_DEFAULT_VALUE);
    }

    public static void onEventEnd(Context context, String eventId, String extra) {
        onEvent(context, eventId, extra, 1, StatConstant.STAT_EVENT_ACTION_END, false);
    }

    public static void onPageStart(Context context, String pageName, Map<String, String> params) {
        onPageEvent(context, StatConstant.STAT_EVENT_PAGE_START, pageName, params);
    }

    public static void onPageStart(Context context, String pageName) {
        onPageStart(context, pageName, null);
    }

    public static void onPageEnd(Context context, String pageName, Map<String, String> params) {
        onPageEvent(context, StatConstant.STAT_EVENT_PAGE_END, pageName, params);
    }

    public static void onPageEnd(Context context, String pageName) {
        onPageEnd(context, pageName, null);
    }

    private static void onPageEvent(Context context, String eventId, String pageName, Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        if (params != null) {
            Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = (Entry) iterator.next();
                builder.append((String) entry.getKey()).append('=').append((String) entry.getValue());
                if (iterator.hasNext()) {
                    builder.append(Char.PIPE);
                }
            }
        }
        onEvent(context, eventId, pageName, builder.toString());
    }

    public static void setHost(String host) {
        StatConstant.BASE_HOST = host;
    }

    public static void setUserId(long userId) {
        AppInfo.setUserId(userId);
    }

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    public static String getDeviceId(Context context) {
        return StatPrefs.getInstance(context).getDeviceId();
    }

    public static void setAppName(String appName) {
        AppInfo.setAppName(appName);
    }

    public static void setAppChannel(String appChannel) {
        AppInfo.setChannel(appChannel);
    }

    public static void setApiKey(String apiKey) {
        AppInfo.setApiKey(apiKey);
    }

    public static void setAppInfo(String apiKey, String appName, String channel) {
        AppInfo.setApiKey(apiKey);
        AppInfo.setAppName(appName);
        AppInfo.setChannel(channel);
    }

    public static void useMetaData(Context context) {
        AppInfo.loadMetaData(context);
    }
}
