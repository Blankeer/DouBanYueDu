package com.douban.book.reader.util;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.Uri.Builder;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.helper.WorksListUri;

public class PackageUtils {
    public static boolean isInstalled(String packageName) {
        try {
            PackageManager pm = App.get().getPackageManager();
            if (pm != null) {
                pm.getPackageInfo(packageName, 64);
                return true;
            }
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    public static boolean canBeOpenedByReader(Intent intent) {
        PackageManager pm = App.get().getPackageManager();
        if (pm == null) {
            return false;
        }
        for (ResolveInfo info : pm.queryIntentActivities(intent, 0)) {
            if (info.activityInfo != null && StringUtils.equalsIgnoreCase(info.activityInfo.packageName, App.get().getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void openReaderMarketStore() {
        Intent intent;
        try {
            intent = new Intent("android.intent.action.VIEW");
            intent.setData(new Builder().scheme("market").authority("details").appendQueryParameter(WorksListUri.KEY_ID, App.get().getPackageName()).build());
            intent.addFlags(268435456);
            App.get().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            try {
                intent = new Intent("android.intent.action.VIEW", Uri.parse("http://read.douban.com/app"));
                intent.addFlags(268435456);
                App.get().startActivity(intent);
            } catch (Exception e2) {
                ToastUtils.showToast((int) R.string.toast_no_market_app);
            }
        }
    }
}
