package com.douban.amonsul.device;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.media.TransportMediator;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatUtils;
import io.realm.internal.Table;
import java.util.List;

class AppInfoUtils {
    private static final String TAG;

    AppInfoUtils() {
    }

    static {
        TAG = AppInfoUtils.class.getSimpleName();
    }

    public static String getMetaData(Context paramContext, String key) {
        String ret = Table.STRING_DEFAULT_VALUE;
        try {
            ret = paramContext.getPackageManager().getApplicationInfo(paramContext.getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT).metaData.getString(key);
            if (ret == null) {
                return Table.STRING_DEFAULT_VALUE;
            }
            return ret;
        } catch (Exception e) {
            if (!MobileStat.DEBUG) {
                return ret;
            }
            e.printStackTrace();
            return ret;
        }
    }

    public static String getVersionName(Context context) {
        String versionName = Table.STRING_DEFAULT_VALUE;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            if (versionName == null || versionName.length() <= 0) {
                return Table.STRING_DEFAULT_VALUE;
            }
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
        }
        return versionName;
    }

    public static String getNetworkType(Context context) {
        if (!StatUtils.hasPermission(context, "android.permission.ACCESS_NETWORK_STATE")) {
            return null;
        }
        try {
            NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (netInfo == null) {
                return null;
            }
            if (netInfo.getType() == 1) {
                return "wifi";
            }
            return netInfo.getExtraInfo();
        } catch (Throwable e) {
            if (!MobileStat.DEBUG) {
                return null;
            }
            e.printStackTrace();
            return null;
        }
    }

    public static Location getLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService("location");
        List<String> providers = lm.getProviders(true);
        if (providers == null || providers.isEmpty()) {
            return null;
        }
        Location bestLocation = null;
        for (String provider : providers) {
            Location loc;
            try {
                loc = lm.getLastKnownLocation(provider);
            } catch (Exception ex) {
                if (MobileStat.DEBUG) {
                    ex.printStackTrace();
                }
                loc = null;
            }
            if (loc != null && (bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy())) {
                bestLocation = loc;
            }
        }
        return bestLocation;
    }
}
