package com.douban.book.reader.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.Theme;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.os.StrictMode.VmPolicy;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewConfiguration;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.douban.book.reader.activity.QQAuthActivity;
import com.douban.book.reader.activity.WeiboAuthActivity;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.Dimen;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.database.DatabaseHelper;
import com.douban.book.reader.event.ApiConfigChangedEvent;
import com.douban.book.reader.event.AppContentClearEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.LoggedInEvent;
import com.douban.book.reader.event.LoggedOutEvent;
import com.douban.book.reader.helper.DataHelper;
import com.douban.book.reader.job.JobUtils;
import com.douban.book.reader.lib.hyphenate.Hyphenate;
import com.douban.book.reader.manager.FontScaleManager_;
import com.douban.book.reader.manager.ReviewManager_;
import com.douban.book.reader.manager.SearchHistoryManager_;
import com.douban.book.reader.manager.ShelfManager_;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.WorksKindManager_;
import com.douban.book.reader.manager.WorksManager_;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.task.PagingTaskManager;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.AppInfo;
import com.douban.book.reader.util.FilePath;
import com.douban.book.reader.util.FileUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.LoginPrompt;
import com.douban.book.reader.util.NotificationUtils;
import com.douban.book.reader.util.PushManager;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.Utils;
import com.mcxiaoke.next.ui.R;
import io.fabric.sdk.android.Fabric;
import java.lang.reflect.Field;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.SystemService;

@EApplication
public class App extends Application {
    private static final String TAG = "ReaderApplication";
    private static App sInstance;
    private static boolean sIsImmersiveMode;
    @SystemService
    ActivityManager mActivityManager;
    BroadcastReceiver mExternalStorageReceiver;
    boolean mExternalStorageWritable;
    final Handler mHandler;
    private Theme mTheme;
    private LayoutInflater mThemedLayoutInflater;
    private Thread mUiThread;

    public App() {
        this.mUiThread = Thread.currentThread();
        this.mHandler = new Handler();
        this.mExternalStorageWritable = false;
    }

    static {
        sIsImmersiveMode = false;
        sInstance = null;
    }

    public static App get() {
        if (sInstance == null) {
            Logger.ec(TAG, new IllegalStateException("App instance is null"));
        }
        return sInstance;
    }

    public static ContextThemeWrapper getThemed() {
        return new ContextThemeWrapper(get(), com.douban.book.reader.theme.Theme.isNight() ? R.style.AppBaseTheme : com.douban.book.reader.R.style.AppBaseTheme_Light);
    }

    public void onCreate() {
        if (AppInfo.isDebug()) {
            StrictMode.setThreadPolicy(new Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new VmPolicy.Builder().detectAll().penaltyLog().build());
        }
        super.onCreate();
        sInstance = this;
        Logger.d(TAG, "\n\n--------------------------------------------------------------", new Object[0]);
        Logger.d(TAG, "%s(pid=%s) started.", AppInfo.getProcessName(), Integer.valueOf(Process.myPid()));
        Logger.d(TAG, "DisplayMetrics: %s", Utils.getFormattedDisplayMetrics());
        Logger.d(TAG, "Heap Size Limit: %sM / Large Heap Size Limit: %sM", Integer.valueOf(this.mActivityManager.getMemoryClass()), Integer.valueOf(this.mActivityManager.getLargeMemoryClass()));
        if (AppInfo.isMainAppProcess()) {
            try {
                ViewConfiguration config = ViewConfiguration.get(this);
                Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
                if (menuKeyField != null) {
                    menuKeyField.setAccessible(true);
                    menuKeyField.setBoolean(config, false);
                }
            } catch (Exception e) {
            }
            Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(AppInfo.isDebug()).build()).build());
            TaskController.run(new Runnable() {
                public void run() {
                    App.this.asyncInit();
                }
            });
            startWatchingExternalStorage();
            if (VERSION.SDK_INT >= 19 && Utils.hasSoftNavigationBar()) {
                sIsImmersiveMode = true;
            }
            EventBusUtils.register(this);
        }
    }

    public void onTerminate() {
        stopWatchingExternalStorage();
        EventBusUtils.unregister(this);
        super.onTerminate();
    }

    private void asyncInit() {
        DataHelper.checkAndUpdateData();
        Analysis.init();
        DatabaseHelper.getInstance();
        Analysis.updateBindUserInfo();
        PushManager.init();
        Hyphenate.initPatterns();
        try {
            UserManager.getInstance().getCurrentUserFromServer();
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        PagingTaskManager.schedulePagingAll(600000);
        WorksKindManager_.getInstance_(this).checkUpdateWorksKinds();
    }

    public void onEventMainThread(ApiConfigChangedEvent apiConfigChangedEvent) {
        removeAllCookie();
    }

    public void onEventMainThread(LoggedInEvent event) {
        Analysis.updateBindUserInfo();
        clearManagerCache();
        if (PushManager.isPushEnabled()) {
            PushManager.start();
        }
    }

    public void onEventMainThread(LoggedOutEvent event) {
        DownloadManager.stopDownloading();
        JobUtils.clearJobs();
        Book.clearInstances();
        PagingTaskManager.cancelPagingAll();
        LoginPrompt.reset();
        clearManagerCache();
        removeAllCookie();
        clearBindInfo();
        Analysis.updateBindUserInfo();
        NotificationUtils.cancelAll();
        try {
            SearchHistoryManager_.getInstance_(this).clear();
        } catch (Throwable th) {
        }
        PushManager.stop();
    }

    private void clearManagerCache() {
        try {
            WorksManager_.getInstance_(this).clearCache();
            ReviewManager_.getInstance_(this).clearCache();
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    public static void removeAllCookie() {
        CookieSyncManager.createInstance(get());
        CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().sync();
        Logger.d(Tag.COOKIES, "removed all cookies.", new Object[0]);
    }

    public void finishAllActivities() {
        sendBroadcast(new Intent(Constants.INTENT_FINISH_ALL_ACTIVITIES));
    }

    public void clearContents() {
        clearWorksData();
        DatabaseHelper.getInstance().clearDb();
        FileUtils.deleteDir(FilePath.userRoot());
        EventBusUtils.post(new AppContentClearEvent());
    }

    private void clearWorksData() {
        try {
            ShelfManager_.getInstance_(this).clear();
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
        }
    }

    public void clearBindInfo() {
        WeiboAuthActivity.clearToken();
        QQAuthActivity.clearToken();
    }

    @Deprecated
    public int getScale() {
        return FontScaleManager_.getInstance_(this).getScale();
    }

    public int getPageHeight() {
        int height = Res.getDisplayMetrics().heightPixels;
        if (isImmersiveMode() && getResources().getConfiguration().orientation == 1) {
            height += Dimen.NAVIGATION_BAR_HEIGHT;
        }
        int realHeight = Dimen.getRealHeight();
        return height > realHeight ? realHeight : height;
    }

    public int getPageWidth() {
        int width = Res.getDisplayMetrics().widthPixels;
        if (isImmersiveMode() && getResources().getConfiguration().orientation == 2) {
            width += Dimen.VERTICAL_NAVIGATION_BAR_WIDTH;
        }
        return Math.min(Dimen.getRealWidth(), width);
    }

    public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != this.mUiThread) {
            this.mHandler.post(action);
        } else {
            action.run();
        }
    }

    public Object getSystemService(String name) {
        if (!StringUtils.equals((CharSequence) "layout_inflater", (CharSequence) name)) {
            return super.getSystemService(name);
        }
        if (this.mThemedLayoutInflater == null) {
            LayoutInflater layoutInflater = (LayoutInflater) super.getSystemService(name);
            if (layoutInflater.getFactory() == null) {
                LayoutInflaterCompat.setFactory(layoutInflater, new ArkLayoutInflaterFactory());
            }
            this.mThemedLayoutInflater = layoutInflater;
        }
        return this.mThemedLayoutInflater;
    }

    public Theme getTheme() {
        if (this.mTheme == null) {
            this.mTheme = getResources().newTheme();
            this.mTheme.applyStyle(getApplicationInfo().theme, true);
        }
        return this.mTheme;
    }

    void updateExternalStorageState() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            this.mExternalStorageWritable = true;
        } else {
            this.mExternalStorageWritable = false;
        }
    }

    void startWatchingExternalStorage() {
        this.mExternalStorageReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Logger.d(App.TAG, "Storage: " + intent.getData(), new Object[0]);
                App.this.updateExternalStorageState();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addAction("android.intent.action.MEDIA_REMOVED");
        registerReceiver(this.mExternalStorageReceiver, filter);
        updateExternalStorageState();
    }

    private void stopWatchingExternalStorage() {
        unregisterReceiver(this.mExternalStorageReceiver);
    }

    public boolean isExternalStorageAvailable() {
        updateExternalStorageState();
        return this.mExternalStorageWritable;
    }

    public static boolean isImmersiveMode() {
        return sIsImmersiveMode;
    }
}
