package com.douban.amonsul;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import io.realm.internal.Table;

public class StatPrefs {
    private static StatPrefs sInstance;
    private Context mContext;
    private String mDeviceIdCache;
    private SharedPreferences mSp;

    public static StatPrefs getInstance(Context context) {
        if (sInstance == null) {
            synchronized (StatPrefs.class) {
                sInstance = new StatPrefs(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    private StatPrefs(Context context) {
        this.mContext = context;
        this.mSp = context.getSharedPreferences(getSpFileName(context), 0);
    }

    private static String getSpFileName(Context context) {
        return "mobilestat_info" + context.getPackageName();
    }

    public void putInt(String key, int value) {
        this.mSp.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return this.mSp.getInt(key, defaultValue);
    }

    public void putLong(String key, long value) {
        this.mSp.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defaultValue) {
        return this.mSp.getLong(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        this.mSp.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.mSp.getBoolean(key, defaultValue);
    }

    public void putString(String key, String value) {
        this.mSp.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return this.mSp.getString(key, defaultValue);
    }

    public String getDeviceId() {
        if (!TextUtils.isEmpty(this.mDeviceIdCache)) {
            return this.mDeviceIdCache;
        }
        String deviceId = getString("device_id", Table.STRING_DEFAULT_VALUE);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UDID.generate(this.mContext);
            saveDeviceId(deviceId);
        }
        this.mDeviceIdCache = deviceId;
        return this.mDeviceIdCache;
    }

    public void saveDeviceId(String deviceId) {
        if (!TextUtils.isEmpty(deviceId)) {
            putString("device_id", deviceId);
            this.mDeviceIdCache = deviceId;
        }
    }
}
