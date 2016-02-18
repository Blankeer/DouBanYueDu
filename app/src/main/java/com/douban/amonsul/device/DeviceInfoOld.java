package com.douban.amonsul.device;

import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatPrefs;
import com.douban.amonsul.StatUtils;
import com.douban.amonsul.network.NetWorker;
import com.douban.book.reader.constant.Constants;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.realm.internal.Table;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import org.json.JSONException;
import org.json.JSONObject;

public final class DeviceInfoOld {
    private static final String TAG;

    static {
        TAG = DeviceInfoOld.class.getSimpleName();
    }

    public static String getMacAddress(Context context) {
        if (!StatUtils.hasPermission(context, "android.permission.ACCESS_WIFI_STATE")) {
            return Table.STRING_DEFAULT_VALUE;
        }
        try {
            WifiManager wm = (WifiManager) context.getSystemService("wifi");
            if (wm.isWifiEnabled()) {
                WifiInfo wifiInfo = wm.getConnectionInfo();
                if (wifiInfo != null) {
                    return wifiInfo.getMacAddress();
                }
            }
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    public static String getDeviceId(Context context) {
        if (!StatUtils.hasPermission(context, "android.permission.READ_PHONE_STATE")) {
            return Table.STRING_DEFAULT_VALUE;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        if (tm != null) {
            try {
                return tm.getDeviceId();
            } catch (Exception e) {
                if (MobileStat.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getDevice(Context context) {
        return Build.MODEL;
    }

    public static String getCarrier(Context context) {
        if (!StatUtils.hasPermission(context, "android.permission.READ_PHONE_STATE")) {
            return Table.STRING_DEFAULT_VALUE;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager != null) {
            try {
                return telephonyManager.getNetworkOperatorName();
            } catch (Exception ex) {
                if (MobileStat.DEBUG) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getOsVersion(Context context) {
        return VERSION.RELEASE;
    }

    public static String getCountry(Context context) {
        try {
            Configuration config = new Configuration();
            System.getConfiguration(context.getContentResolver(), config);
            if (config == null) {
                return null;
            }
            if (config.locale != null) {
                return config.locale.getCountry();
            }
            return Locale.getDefault().getCountry();
        } catch (Exception ex) {
            if (!MobileStat.DEBUG) {
                return null;
            }
            ex.printStackTrace();
            return null;
        }
    }

    public static String getLanguage(Context context) {
        try {
            Configuration config = new Configuration();
            System.getConfiguration(context.getContentResolver(), config);
            if (config.locale != null) {
                return config.locale.getLanguage();
            }
            return Locale.getDefault().getLanguage();
        } catch (Exception ex) {
            if (!MobileStat.DEBUG) {
                return null;
            }
            ex.printStackTrace();
            return null;
        }
    }

    public static String getTimeZone(Context context) {
        try {
            Configuration config = new Configuration();
            System.getConfiguration(context.getContentResolver(), config);
            Calendar c;
            TimeZone zone;
            if (config.locale != null) {
                c = Calendar.getInstance(config.locale);
                if (c == null) {
                    return null;
                }
                zone = c.getTimeZone();
                if (zone != null) {
                    return Table.STRING_DEFAULT_VALUE + (zone.getRawOffset() / Constants.TIME_1HOUR_MILLISECOND);
                }
                return StatConstant.DEFAULT_VALUE_ZONE;
            }
            c = Calendar.getInstance(Locale.getDefault());
            if (c == null) {
                return StatConstant.DEFAULT_VALUE_ZONE;
            }
            zone = c.getTimeZone();
            if (zone != null) {
                return Table.STRING_DEFAULT_VALUE + (zone.getRawOffset() / Constants.TIME_1HOUR_MILLISECOND);
            }
            return StatConstant.DEFAULT_VALUE_ZONE;
        } catch (Exception ex) {
            if (!MobileStat.DEBUG) {
                return null;
            }
            ex.printStackTrace();
            return null;
        }
    }

    public static String getScreenSize(Context context) {
        String ret = null;
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(dm);
            ret = String.valueOf(dm.widthPixels) + " x " + String.valueOf(dm.heightPixels);
        } catch (Exception ex) {
            if (MobileStat.DEBUG) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public static String getImsi(Context context) {
        if (!StatUtils.hasPermission(context, "android.permission.READ_PHONE_STATE")) {
            return null;
        }
        TelephonyManager manager = (TelephonyManager) context.getSystemService("phone");
        if (manager != null) {
            return manager.getNetworkOperator();
        }
        return null;
    }

    public static int getLocationId(Context context) {
        if (!StatUtils.hasPermission(context, "android.permission.READ_PHONE_STATE")) {
            return 0;
        }
        TelephonyManager manager = (TelephonyManager) context.getSystemService("phone");
        if (manager != null) {
            try {
                if (manager.getCellLocation() instanceof GsmCellLocation) {
                    return ((GsmCellLocation) manager.getCellLocation()).getLac();
                }
                if (manager.getCellLocation() instanceof CdmaCellLocation) {
                    return ((CdmaCellLocation) manager.getCellLocation()).getNetworkId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static int getCellId(Context context) {
        if (StatUtils.hasPermission(context, "android.permission.READ_PHONE_STATE") && StatUtils.hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION")) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService("phone");
            if (manager != null) {
                try {
                    if (manager.getCellLocation() instanceof GsmCellLocation) {
                        return ((GsmCellLocation) manager.getCellLocation()).getCid();
                    }
                    if (manager.getCellLocation() instanceof CdmaCellLocation) {
                        return ((CdmaCellLocation) manager.getCellLocation()).getBaseStationId();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    private static JSONObject toJson(Context context) {
        Throwable e;
        JSONObject obj = null;
        try {
            JSONObject obj2 = new JSONObject();
            try {
                String temp = StatPrefs.getInstance(context).getDeviceId();
                if (temp != null) {
                    obj2.put(NetWorker.PARAM_KEY_DEVICE_ID, temp);
                } else {
                    obj2.put(NetWorker.PARAM_KEY_DEVICE_ID, Table.STRING_DEFAULT_VALUE);
                }
                temp = String.valueOf(StatPrefs.getInstance(context).getDeviceId().hashCode());
                if (temp != null) {
                    obj2.put(StatConstant.JSON_KEY_DID_OLD, temp);
                } else {
                    obj2.put(StatConstant.JSON_KEY_DID_OLD, Table.STRING_DEFAULT_VALUE);
                }
                temp = getDevice(context);
                if (temp != null) {
                    obj2.put(StatConstant.JSON_KEY_DEVICE, temp);
                } else {
                    obj2.put(StatConstant.JSON_KEY_DEVICE, Table.STRING_DEFAULT_VALUE);
                }
                temp = getCarrier(context);
                if (temp != null) {
                    obj2.put(StatConstant.JSON_KEY_CARRIER, temp);
                } else {
                    obj2.put(StatConstant.JSON_KEY_CARRIER, Table.STRING_DEFAULT_VALUE);
                }
                temp = getTimeZone(context);
                if (temp != null) {
                    obj2.put(StatConstant.JSON_KEY_TIMEZONE, temp);
                } else {
                    obj2.put(StatConstant.JSON_KEY_TIMEZONE, StatConstant.DEFAULT_VALUE_ZONE);
                }
                temp = getCountry(context);
                if (temp != null) {
                    obj2.put(StatConstant.JSON_KEY_COUNTRY, temp);
                } else {
                    obj2.put(StatConstant.JSON_KEY_COUNTRY, StatConstant.DEFAULT_VALUE_COUNTRY);
                }
                temp = getLanguage(context);
                if (temp != null) {
                    obj2.put(StatConstant.JSON_KEY_LANGUAGE, temp);
                } else {
                    obj2.put(StatConstant.JSON_KEY_LANGUAGE, StatConstant.DEFAULT_VALUE_LANGUAGE);
                }
                temp = getOsVersion(context);
                if (temp != null) {
                    obj2.put(StatConstant.JSON_KEY_OS_VERSION, temp);
                } else {
                    obj2.put(StatConstant.JSON_KEY_OS_VERSION, Table.STRING_DEFAULT_VALUE);
                }
                temp = getScreenSize(context);
                if (temp != null) {
                    obj2.put(StatConstant.JSON_KEY_RESOLUTION, temp);
                } else {
                    obj2.put(StatConstant.JSON_KEY_RESOLUTION, Table.STRING_DEFAULT_VALUE);
                }
                temp = getDeviceId(context);
                if (TextUtils.isEmpty(temp)) {
                    obj2.put(StatConstant.JSON_KEY_IMEI, Table.STRING_DEFAULT_VALUE);
                } else {
                    obj2.put(StatConstant.JSON_KEY_IMEI, temp);
                }
                temp = getImsi(context);
                if (TextUtils.isEmpty(temp)) {
                    obj2.put(StatConstant.JSON_KEY_IMSI, Table.STRING_DEFAULT_VALUE);
                } else {
                    obj2.put(StatConstant.JSON_KEY_IMSI, temp);
                }
                obj2.put(StatConstant.JSON_KEY_MAC, getMacAddress(context));
                obj2.put(StatConstant.JSON_KEY_LOCID, String.valueOf(getLocationId(context)));
                obj2.put(StatConstant.JSON_KEY_CELLID, String.valueOf(getCellId(context)));
                obj2.put(StatConstant.JSON_KEY_OS, AbstractSpiCall.ANDROID_CLIENT_TYPE);
                return obj2;
            } catch (JSONException e2) {
                e = e2;
                obj = obj2;
                StatLogger.e(TAG, e);
                return obj;
            }
        } catch (JSONException e3) {
            e = e3;
            StatLogger.e(TAG, e);
            return obj;
        }
    }
}
