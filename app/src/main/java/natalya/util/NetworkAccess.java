package natalya.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import com.douban.amonsul.StatConstant;

public class NetworkAccess {
    private static final Uri PREFERRED_APN_URI;

    static {
        PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity != null) {
            NetworkInfo[] infos = connectivity.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo info : infos) {
                    if (info.getState() == State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isActiveNetWorkWifi(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean isActiveNetworkMobile(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Intent intent) {
        if (intent.getBooleanExtra("noConnectivity", false)) {
            return false;
        }
        NetworkInfo ni = getNetworkInfo(intent);
        if (ni == null || !ni.isConnected()) {
            return false;
        }
        return true;
    }

    private static NetworkInfo getNetworkInfo(Intent intent) {
        return (NetworkInfo) intent.getParcelableExtra("networkInfo");
    }

    public static boolean isNetworkWifi(Intent intent) {
        NetworkInfo ni = getNetworkInfo(intent);
        if (ni == null) {
            return false;
        }
        if (ni.getType() == 1) {
            return true;
        }
        return false;
    }

    public static boolean isNetworkMobile(Intent intent) {
        NetworkInfo ni = getNetworkInfo(intent);
        if (ni != null) {
            if (ni.getType() == 0) {
                return true;
            }
        }
        return false;
    }

    public static WifiLock acquireWifiLock(Context context, String tag) {
        WifiLock wl = ((WifiManager) context.getSystemService("wifi")).createWifiLock(tag);
        wl.acquire();
        return wl;
    }

    public static boolean isUsingWap(Context context) {
        boolean isUsingWap = false;
        int apnIndex = 0;
        Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            try {
                c.moveToFirst();
                int index = 0;
                for (String cn : c.getColumnNames()) {
                    if (cn.trim().equalsIgnoreCase(StatConstant.JSON_KEY_APP_NAME)) {
                        apnIndex = index;
                    }
                    index++;
                }
                String apn = c.getString(apnIndex);
                if (apn != null && apn.contains("wap")) {
                    isUsingWap = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            c.close();
        }
        return isUsingWap;
    }
}
