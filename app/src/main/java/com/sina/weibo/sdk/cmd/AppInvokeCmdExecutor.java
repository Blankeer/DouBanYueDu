package com.sina.weibo.sdk.cmd;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import com.douban.book.reader.constant.Constants;
import com.sina.weibo.sdk.utils.SDKNotification.SDKNotificationBuilder;
import java.util.List;

class AppInvokeCmdExecutor implements CmdExecutor<AppInvokeCmd> {
    private static final int NOTIFICATION_ID = 2;
    private static final int SHOW_NOTICIATION = 1;
    private Context mContext;
    private NotificationHandler mHandler;

    private class NotificationHandler extends Handler {
        public NotificationHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppInvokeCmdExecutor.SHOW_NOTICIATION /*1*/:
                    AppInvokeCmdExecutor.showNotification(AppInvokeCmdExecutor.this.mContext, (AppInvokeCmd) msg.obj);
                default:
            }
        }
    }

    public AppInvokeCmdExecutor(Context ctx) {
        this.mContext = ctx.getApplicationContext();
        this.mHandler = new NotificationHandler(this.mContext.getMainLooper());
    }

    public boolean doExecutor(AppInvokeCmd cmd) {
        if (cmd == null || TextUtils.isEmpty(cmd.getNotificationText()) || TextUtils.isEmpty(cmd.getScheme())) {
            return false;
        }
        Message msg = this.mHandler.obtainMessage();
        msg.what = SHOW_NOTICIATION;
        msg.obj = cmd;
        this.mHandler.sendMessageDelayed(msg, cmd.getNotificationDelay());
        return true;
    }

    private static void showNotification(Context ctx, AppInvokeCmd cmd) {
        SDKNotificationBuilder.buildUpon().setNotificationContent(cmd.getNotificationText()).setNotificationPendingIntent(buildInvokePendingIntent(ctx, cmd)).setNotificationTitle(cmd.getNotificationTitle()).setTickerText(cmd.getNotificationText()).build(ctx).show(NOTIFICATION_ID);
    }

    private static PendingIntent buildInvokePendingIntent(Context ctx, AppInvokeCmd cmd) {
        String scheme = cmd.getScheme();
        String url = cmd.getUrl();
        Intent invokeIntent = null;
        Intent intent = buildOpenSchemeIntent(scheme, cmd.getAppPackage());
        if (intent != null) {
            List<ResolveInfo> activities = ctx.getPackageManager().queryIntentActivities(intent, AccessibilityNodeInfoCompat.ACTION_CUT);
            if (!(activities == null || activities.isEmpty())) {
                invokeIntent = intent;
            }
        }
        if (invokeIntent == null) {
            invokeIntent = buildOpenUrlIntent(url);
        }
        if (invokeIntent == null) {
            return null;
        }
        invokeIntent.setFlags(268435456);
        return PendingIntent.getActivity(ctx, 0, invokeIntent, 134217728);
    }

    private static Intent buildOpenSchemeIntent(String scheme, String packageName) {
        if (TextUtils.isEmpty(scheme) || !Uri.parse(scheme).isHierarchical()) {
            return null;
        }
        Uri uri = Uri.parse(scheme);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        intent.setPackage(packageName);
        return intent;
    }

    private static Intent buildOpenUrlIntent(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Uri urlUri = Uri.parse(url);
        String urlScheme = urlUri.getScheme();
        if (!urlScheme.equalsIgnoreCase("http") && !urlScheme.equalsIgnoreCase(Constants.API_SCHEME)) {
            return null;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(urlUri);
        return intent;
    }
}
