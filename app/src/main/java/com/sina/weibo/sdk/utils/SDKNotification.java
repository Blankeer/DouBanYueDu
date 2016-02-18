package com.sina.weibo.sdk.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;

public class SDKNotification {
    private Context mContext;
    private Notification mNotification;

    public static class SDKNotificationBuilder {
        private String mNotificationContent;
        private PendingIntent mNotificationPendingIntent;
        private String mNotificationTitle;
        private Uri mSoundUri;
        private String mTickerText;
        private long[] mVibrate;

        public static SDKNotificationBuilder buildUpon() {
            return new SDKNotificationBuilder();
        }

        public SDKNotificationBuilder setTickerText(String tickerText) {
            this.mTickerText = tickerText;
            return this;
        }

        public SDKNotificationBuilder setNotificationTitle(String notificationTitle) {
            this.mNotificationTitle = notificationTitle;
            return this;
        }

        public SDKNotificationBuilder setNotificationContent(String notificationContent) {
            this.mNotificationContent = notificationContent;
            return this;
        }

        public SDKNotificationBuilder setNotificationPendingIntent(PendingIntent notificationPendingIntent) {
            this.mNotificationPendingIntent = notificationPendingIntent;
            return this;
        }

        public SDKNotificationBuilder setVibrate(long[] vibrate) {
            this.mVibrate = vibrate;
            return this;
        }

        public SDKNotificationBuilder setSoundUri(Uri soundUri) {
            this.mSoundUri = soundUri;
            return this;
        }

        public SDKNotification build(Context ctx) {
            Builder mNotificationBuilder = new Builder(ctx);
            mNotificationBuilder.setAutoCancel(true);
            mNotificationBuilder.setContentIntent(this.mNotificationPendingIntent);
            mNotificationBuilder.setTicker(this.mTickerText);
            mNotificationBuilder.setSmallIcon(getNotificationIcon(ctx));
            mNotificationBuilder.setWhen(System.currentTimeMillis());
            if (this.mSoundUri != null) {
                mNotificationBuilder.setSound(this.mSoundUri);
            }
            if (this.mVibrate != null) {
                mNotificationBuilder.setVibrate(this.mVibrate);
            }
            mNotificationBuilder.setLargeIcon(((BitmapDrawable) ResourceManager.getDrawable(ctx, "weibosdk_notification_icon.png")).getBitmap());
            mNotificationBuilder.setContentTitle(this.mNotificationTitle);
            mNotificationBuilder.setContentText(this.mNotificationContent);
            return new SDKNotification(mNotificationBuilder.build(), null);
        }

        private static int getNotificationIcon(Context ctx) {
            int resId = getResourceId(ctx, "com_sina_weibo_sdk_weibo_logo", "drawable");
            if (resId > 0) {
                return resId;
            }
            return 17301659;
        }

        private static int getResourceId(Context ctx, String name, String type) {
            String packageName = ctx.getApplicationContext().getPackageName();
            try {
                return ctx.getPackageManager().getResourcesForApplication(packageName).getIdentifier(name, type, packageName);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    private SDKNotification(Context ctx, Notification notification) {
        this.mContext = ctx.getApplicationContext();
        this.mNotification = notification;
    }

    public void show(int notificationId) {
        if (this.mNotification != null) {
            ((NotificationManager) this.mContext.getSystemService("notification")).notify(notificationId, this.mNotification);
        }
    }
}
