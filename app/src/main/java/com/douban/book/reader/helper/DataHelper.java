package com.douban.book.reader.helper;

import com.douban.book.reader.constant.Key;
import com.douban.book.reader.fragment.StoreTabFragment;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.FilePath;
import com.douban.book.reader.util.FileUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import java.io.File;

public class DataHelper {
    private static final String TAG;

    static {
        TAG = DataHelper.class.getSimpleName();
    }

    public static void checkAndUpdateData() {
        int oldDataVersion = Pref.ofApp().getInt(Key.APP_CURRENT_DATA_VERSION, 0);
        int currentVersionCode = AppInfo.getVersionCode();
        if (Pref.ofApp().getInt(Key.APP_FIRST_INSTALLED_VERSION, 0) == 0) {
            int firstInstalledVersion;
            if (oldDataVersion > 0) {
                firstInstalledVersion = oldDataVersion;
            } else {
                firstInstalledVersion = currentVersionCode;
            }
            Pref.ofApp().set(Key.APP_FIRST_INSTALLED_VERSION, Integer.valueOf(firstInstalledVersion));
        }
        if (currentVersionCode > oldDataVersion) {
            Logger.d(TAG, "!!! data update needed. updating from version %s to %s", Integer.valueOf(oldDataVersion), Integer.valueOf(AppInfo.getVersionCode()));
            if (oldDataVersion < 33) {
                try {
                    File folder = FilePath.originalRoot();
                    FileUtils.deleteDir(new File(folder, StoreTabFragment.STORE_TAB_BOOK));
                    FileUtils.deleteDir(new File(folder, "column"));
                } catch (Throwable e) {
                    Logger.e(TAG, e);
                    return;
                }
            }
            updateDataVersionToLatest();
        }
    }

    private static void updateDataVersionToLatest() {
        int latest = AppInfo.getVersionCode();
        if (latest > 0) {
            Pref.ofApp().set(Key.APP_CURRENT_DATA_VERSION, Integer.valueOf(latest));
        }
    }
}
