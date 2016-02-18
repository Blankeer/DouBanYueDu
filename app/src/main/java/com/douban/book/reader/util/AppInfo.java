package com.douban.book.reader.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.media.TransportMediator;
import android.support.v4.os.EnvironmentCompat;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Char;
import com.mcxiaoke.packer.helper.PackerNg;
import io.realm.internal.Table;

public class AppInfo {
    private static final String TAG;
    private static Bundle sAppMetaDataBundle;
    private static String sChannelName;
    private static PackageInfo sPackageInfo;

    static {
        TAG = AppInfo.class.getSimpleName();
    }

    public static String getChannelName() {
        if (StringUtils.isEmpty(sChannelName)) {
            sChannelName = PackerNg.getMarket(App.get(), "Douban");
        }
        return sChannelName;
    }

    public static String getSpecialEvent() {
        String specialEvent = null;
        try {
            specialEvent = getAppMetaBundle().getString("SPECIAL_EVENT");
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        return StringUtils.isEmpty(specialEvent) ? EnvironmentCompat.MEDIA_UNKNOWN : specialEvent;
    }

    public static boolean isDebug() {
        return false;
    }

    public static int getVersionCode() {
        try {
            return getPackageInfo().versionCode;
        } catch (Exception e) {
            Logger.e(TAG, e);
            return 0;
        }
    }

    public static String getVersionName() {
        try {
            return getPackageInfo().versionName;
        } catch (Exception e) {
            Logger.e(TAG, e);
            return "0.0.0";
        }
    }

    public static String getVersionNameForWeb() {
        return getVersionName().replace(Char.DOT, Char.UNDERLINE);
    }

    public static String getPackageName() {
        try {
            return getPackageInfo().packageName;
        } catch (Exception e) {
            Logger.e(TAG, e);
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    public static String getBuild() {
        return Res.getString(R.string.githash);
    }

    public static String getInstallerPackageName() {
        try {
            PackageManager pm = App.get().getPackageManager();
            if (pm != null) {
                String result = pm.getInstallerPackageName(getPackageInfo().packageName);
                if (StringUtils.isEmpty(result)) {
                    return "(None)";
                }
                return result;
            }
        } catch (NameNotFoundException e) {
        }
        return "(Unknown)";
    }

    public static String getProcessName() {
        String result = "(Unknown)";
        int pid = Process.myPid();
        for (RunningAppProcessInfo processInfo : ((ActivityManager) App.get().getSystemService("activity")).getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return result;
    }

    public static boolean isMainAppProcess() {
        return StringUtils.equalsIgnoreCase(getProcessName(), getPackageName());
    }

    private static PackageInfo getPackageInfo() throws NameNotFoundException {
        if (sPackageInfo != null) {
            return sPackageInfo;
        }
        App app = App.get();
        sPackageInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
        return sPackageInfo;
    }

    private static Bundle getAppMetaBundle() throws NameNotFoundException {
        if (sAppMetaDataBundle != null) {
            return sAppMetaDataBundle;
        }
        App app = App.get();
        sAppMetaDataBundle = app.getPackageManager().getApplicationInfo(app.getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT).metaData;
        return sAppMetaDataBundle;
    }
}
