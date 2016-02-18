package com.douban.amonsul.device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.StatPrefs;
import com.douban.amonsul.StatUtils;
import com.douban.amonsul.network.NetWorker;
import com.douban.book.reader.constant.Constants;
import io.fabric.sdk.android.services.common.AbstractSpiCall;
import io.realm.internal.Table;
import java.util.Locale;
import java.util.TimeZone;
import org.json.JSONObject;

public class DeviceInfo {
    private static final String TAG;
    private static JSONObject sDeviceInfo;

    static {
        TAG = DeviceInfo.class.getSimpleName();
    }

    public static synchronized JSONObject get(Context context) {
        JSONObject jSONObject;
        synchronized (DeviceInfo.class) {
            if (sDeviceInfo == null) {
                sDeviceInfo = init(context);
            }
            jSONObject = sDeviceInfo;
        }
        return jSONObject;
    }

    private static JSONObject init(Context context) {
        JSONObject json = new JSONObject();
        try {
            initDeviceInfo(context, json);
            initPhoneInfo(context, json);
        } catch (Throwable ex) {
            if (MobileStat.DEBUG) {
                ex.printStackTrace();
            }
        }
        return json;
    }

    private static void initDeviceInfo(Context context, JSONObject json) {
        try {
            Locale locale = Locale.getDefault();
            String country = locale.getCountry();
            String language = locale.getLanguage();
            String timeZone = String.valueOf(TimeZone.getDefault().getRawOffset() / Constants.TIME_1HOUR_MILLISECOND);
            String os = AbstractSpiCall.ANDROID_CLIENT_TYPE;
            String osVer = VERSION.RELEASE;
            String model = Build.MODEL;
            String screen = getScreenSize(context);
            String deviceId = StatPrefs.getInstance(context).getDeviceId();
            String oldDeviceId = String.valueOf(deviceId.hashCode());
            String macAddress = getMacAddress(context);
            json.put(StatConstant.JSON_KEY_COUNTRY, country);
            json.put(StatConstant.JSON_KEY_LANGUAGE, language);
            json.put(StatConstant.JSON_KEY_TIMEZONE, timeZone);
            json.put(StatConstant.JSON_KEY_OS, os);
            json.put(StatConstant.JSON_KEY_OS_VERSION, osVer);
            json.put(StatConstant.JSON_KEY_DEVICE, model);
            json.put(StatConstant.JSON_KEY_RESOLUTION, screen);
            json.put(NetWorker.PARAM_KEY_DEVICE_ID, deviceId);
            json.put(StatConstant.JSON_KEY_DID_OLD, oldDeviceId);
            json.put(StatConstant.JSON_KEY_MAC, macAddress);
        } catch (Throwable ex) {
            if (MobileStat.DEBUG) {
                ex.printStackTrace();
            }
        }
    }

    private static void initPhoneInfo(Context context, JSONObject json) {
        if (StatUtils.hasPermission(context, "android.permission.READ_PHONE_STATE")) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
            try {
                String carrier = tm.getNetworkOperatorName();
                String imei = tm.getDeviceId();
                String imsi = tm.getSubscriberId();
                String str = StatConstant.JSON_KEY_CARRIER;
                if (carrier == null) {
                    carrier = Table.STRING_DEFAULT_VALUE;
                }
                json.put(str, carrier);
                str = StatConstant.JSON_KEY_IMEI;
                if (imei == null) {
                    imei = Table.STRING_DEFAULT_VALUE;
                }
                json.put(str, imei);
                str = StatConstant.JSON_KEY_IMSI;
                if (imsi == null) {
                    imsi = Table.STRING_DEFAULT_VALUE;
                }
                json.put(str, imsi);
            } catch (Throwable ex) {
                if (MobileStat.DEBUG) {
                    ex.printStackTrace();
                }
            }
            if (StatUtils.hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION")) {
                try {
                    int locId;
                    int cellId;
                    if (tm.getCellLocation() instanceof GsmCellLocation) {
                        locId = ((GsmCellLocation) tm.getCellLocation()).getLac();
                        cellId = ((GsmCellLocation) tm.getCellLocation()).getCid();
                    } else if (tm.getCellLocation() instanceof CdmaCellLocation) {
                        locId = ((CdmaCellLocation) tm.getCellLocation()).getNetworkId();
                        cellId = ((CdmaCellLocation) tm.getCellLocation()).getBaseStationId();
                    } else {
                        locId = 0;
                        cellId = 0;
                    }
                    if (locId > 0) {
                        json.put(StatConstant.JSON_KEY_LOCID, String.valueOf(locId));
                        json.put(StatConstant.JSON_KEY_CELLID, String.valueOf(cellId));
                    }
                } catch (Throwable ex2) {
                    if (MobileStat.DEBUG) {
                        ex2.printStackTrace();
                    }
                }
            }
        }
    }

    private static String getMacAddress(Context context) {
        if (!StatUtils.hasPermission(context, "android.permission.ACCESS_WIFI_STATE")) {
            return Table.STRING_DEFAULT_VALUE;
        }
        try {
            WifiInfo wifiInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getMacAddress();
            }
        } catch (Throwable ex) {
            if (MobileStat.DEBUG) {
                ex.printStackTrace();
            }
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    private static String getScreenSize(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(dm);
            return dm.widthPixels + " x " + dm.heightPixels;
        } catch (Throwable th) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    private static String getNetworkType(Context context) {
        if (!StatUtils.hasPermission(context, "android.permission.ACCESS_NETWORK_STATE")) {
            return Table.STRING_DEFAULT_VALUE;
        }
        String typeName = Table.STRING_DEFAULT_VALUE;
        try {
            NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (info == null) {
                return typeName;
            }
            if (info.getType() == 1) {
                return "wifi";
            }
            return info.getExtraInfo();
        } catch (Throwable e) {
            if (!MobileStat.DEBUG) {
                return typeName;
            }
            e.printStackTrace();
            return typeName;
        }
    }
}
