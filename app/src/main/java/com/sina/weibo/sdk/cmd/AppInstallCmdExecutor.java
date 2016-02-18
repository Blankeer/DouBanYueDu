package com.sina.weibo.sdk.cmd;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;
import com.sina.weibo.sdk.WeiboAppManager;
import com.sina.weibo.sdk.WeiboAppManager.WeiboInfo;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.NetUtils;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.LogUtil;
import com.sina.weibo.sdk.utils.MD5;
import com.sina.weibo.sdk.utils.NetworkHelper;
import com.sina.weibo.sdk.utils.ResourceManager;
import com.sina.weibo.sdk.utils.SDKNotification.SDKNotificationBuilder;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.io.File;
import java.util.List;

class AppInstallCmdExecutor implements CmdExecutor<AppInstallCmd> {
    private static final int MESSAGE_DO_CMD = 1;
    private static final int MESSAGE_QUIT_LOOP = 2;
    private static final String TAG;
    private static final String WB_APK_FILE_DIR;
    private boolean isStarted;
    private Context mContext;
    private InstallHandler mHandler;
    private Looper mLooper;
    private HandlerThread thread;

    private class InstallHandler extends Handler {
        public InstallHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppInstallCmdExecutor.MESSAGE_DO_CMD /*1*/:
                    AppInstallCmdExecutor.this.handleCmd((AppInstallCmd) msg.obj);
                case AppInstallCmdExecutor.MESSAGE_QUIT_LOOP /*2*/:
                    AppInstallCmdExecutor.this.mLooper.quit();
                    AppInstallCmdExecutor.this.isStarted = false;
                default:
            }
        }
    }

    private static final class NOTIFICATION_CONSTANTS {
        private static final int NOTIFICATIONID = 1;
        private static final String WEIBO = "Weibo";
        private static final String WEIBO_ZH_CN = "\u5fae\u535a";
        private static final String WEIBO_ZH_TW = "\u5fae\u535a";

        private NOTIFICATION_CONSTANTS() {
        }
    }

    public AppInstallCmdExecutor(Context ctx) {
        this.isStarted = false;
        this.mContext = ctx.getApplicationContext();
    }

    static {
        WB_APK_FILE_DIR = Environment.getExternalStorageDirectory() + "/Android/org_share_data/";
        TAG = AppInstallCmdExecutor.class.getName();
    }

    private void handleCmd(AppInstallCmd cmd) {
        if (needActivate(this.mContext, cmd)) {
            String dir = WB_APK_FILE_DIR;
            String downloadUrl = cmd.getDownloadUrl();
            long versionCode = cmd.getAppVersion();
            Pair<Integer, File> pair = walkDir(this.mContext, dir, cmd);
            if (pair != null && pair.second != null && ((long) ((Integer) pair.first).intValue()) >= versionCode) {
                showNotification(this.mContext, cmd, ((File) pair.second).getAbsolutePath());
            } else if (NetworkHelper.isWifiValid(this.mContext) && !TextUtils.isEmpty(downloadUrl)) {
                String filePath = Table.STRING_DEFAULT_VALUE;
                try {
                    String redirectUrl = NetUtils.internalGetRedirectUri(this.mContext, downloadUrl, HttpRequest.METHOD_GET, new WeiboParameters(Table.STRING_DEFAULT_VALUE));
                    String fileName = generateSaveFileName(redirectUrl);
                    if (TextUtils.isEmpty(fileName) || !fileName.endsWith(".apk")) {
                        LogUtil.e(TAG, "redirectDownloadUrl is illeagle");
                        if (!TextUtils.isEmpty(filePath)) {
                            showNotification(this.mContext, cmd, filePath);
                            return;
                        }
                        return;
                    }
                    filePath = NetUtils.internalDownloadFile(this.mContext, redirectUrl, dir, fileName);
                    if (!TextUtils.isEmpty(filePath)) {
                        showNotification(this.mContext, cmd, filePath);
                    }
                } catch (WeiboException e) {
                    e.printStackTrace();
                    if (!TextUtils.isEmpty(filePath)) {
                        showNotification(this.mContext, cmd, filePath);
                    }
                } catch (Throwable th) {
                    if (!TextUtils.isEmpty(filePath)) {
                        showNotification(this.mContext, cmd, filePath);
                    }
                }
            }
        }
    }

    private static boolean needActivate(Context ctx, AppInstallCmd cmd) {
        List<String> packages = cmd.getAppPackage();
        if (packages == null || packages.size() == 0 || TextUtils.isEmpty(cmd.getAppSign()) || TextUtils.isEmpty(cmd.getDownloadUrl()) || TextUtils.isEmpty(cmd.getNotificationText())) {
            return false;
        }
        if (packages.contains("com.sina.weibo")) {
            WeiboInfo mWeiboInfo = WeiboAppManager.getInstance(ctx).getWeiboInfo();
            if (mWeiboInfo == null || !mWeiboInfo.isLegal()) {
                return true;
            }
            return false;
        }
        for (String packageName : packages) {
            if (checkApkInstalled(ctx, packageName)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkApkInstalled(Context ctx, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            if (ctx.getPackageManager().getPackageInfo(packageName, MESSAGE_DO_CMD) != null) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public void start() {
        if (!this.isStarted) {
            this.isStarted = true;
            this.thread = new HandlerThread(Table.STRING_DEFAULT_VALUE);
            this.thread.start();
            this.mLooper = this.thread.getLooper();
            this.mHandler = new InstallHandler(this.mLooper);
        }
    }

    public void stop() {
        if (this.thread == null || this.mHandler == null) {
            LogUtil.w(TAG, "no thread running. please call start method first!");
            return;
        }
        Message msg = this.mHandler.obtainMessage();
        msg.what = MESSAGE_QUIT_LOOP;
        this.mHandler.sendMessage(msg);
    }

    public boolean doExecutor(AppInstallCmd cmd) {
        if (this.thread == null || this.mHandler == null) {
            throw new RuntimeException("no thread running. please call start method first!");
        }
        if (cmd != null) {
            Message msg = this.mHandler.obtainMessage();
            msg.what = MESSAGE_DO_CMD;
            msg.obj = cmd;
            this.mHandler.sendMessage(msg);
        }
        return false;
    }

    private static Pair<Integer, File> walkDir(Context ctx, String dir, AppInstallCmd cmd) {
        if (TextUtils.isEmpty(dir)) {
            return null;
        }
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return null;
        }
        File[] files = dirFile.listFiles();
        if (files == null) {
            return null;
        }
        int newestVersion = 0;
        File weiboApkFile = null;
        int length = files.length;
        for (int i = 0; i < length; i += MESSAGE_DO_CMD) {
            File file = files[i];
            String fileName = file.getName();
            if (file.isFile() && fileName.endsWith(".apk")) {
                PackageInfo pkgInfo = ctx.getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), 64);
                if (isSpecifiedApk(pkgInfo, cmd.getAppPackage(), cmd.getAppSign()) && pkgInfo.versionCode > newestVersion) {
                    newestVersion = pkgInfo.versionCode;
                    weiboApkFile = file;
                }
            }
        }
        return new Pair(Integer.valueOf(newestVersion), weiboApkFile);
    }

    private static boolean isSpecifiedApk(PackageInfo pkgInfo, List<String> packageNames, String appSign) {
        boolean packageChecked = false;
        for (String packageName : packageNames) {
            if (checkPackageName(pkgInfo, packageName)) {
                packageChecked = true;
                break;
            }
        }
        boolean signChecked = checkApkSign(pkgInfo, appSign);
        if (packageChecked && signChecked) {
            return true;
        }
        return false;
    }

    private static boolean checkPackageName(PackageInfo pkgInfo, String packageName) {
        if (pkgInfo == null) {
            return false;
        }
        return packageName.equals(pkgInfo.packageName);
    }

    private static boolean checkApkSign(PackageInfo pkgInfo, String appSign) {
        if (pkgInfo == null) {
            return false;
        }
        if (pkgInfo.signatures != null) {
            String md5Sign = Table.STRING_DEFAULT_VALUE;
            for (int j = 0; j < pkgInfo.signatures.length; j += MESSAGE_DO_CMD) {
                byte[] str = pkgInfo.signatures[j].toByteArray();
                if (str != null) {
                    md5Sign = MD5.hexdigest(str);
                }
            }
            if (md5Sign != null) {
                return md5Sign.equals(appSign);
            }
            return false;
        } else if (VERSION.SDK_INT < 11) {
            return true;
        } else {
            return false;
        }
    }

    private static String generateSaveFileName(String downloadUrl) {
        String fileName = Table.STRING_DEFAULT_VALUE;
        int index = downloadUrl.lastIndexOf("/");
        if (index != -1) {
            return downloadUrl.substring(index + MESSAGE_DO_CMD, downloadUrl.length());
        }
        return fileName;
    }

    private static void showNotification(Context ctx, AppInstallCmd cmd, String apkPath) {
        SDKNotificationBuilder.buildUpon().setNotificationContent(cmd.getNotificationText()).setNotificationPendingIntent(buildInstallApkIntent(ctx, apkPath)).setNotificationTitle(getNotificationTitle(ctx, cmd.getNotificationTitle())).setTickerText(cmd.getNotificationText()).build(ctx).show(MESSAGE_DO_CMD);
    }

    private static PendingIntent buildInstallApkIntent(Context ctx, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return PendingIntent.getActivity(ctx, 0, new Intent(), 16);
        }
        Intent intentInstall = new Intent("android.intent.action.VIEW");
        intentInstall.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        return PendingIntent.getActivity(ctx, 0, intentInstall, 16);
    }

    private static String getNotificationTitle(Context ctx, String title) {
        if (TextUtils.isEmpty(title)) {
            return ResourceManager.getString(ctx, "Weibo", "\u5fae\u535a", "\u5fae\u535a");
        }
        return title;
    }
}
