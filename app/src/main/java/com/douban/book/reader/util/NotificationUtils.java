package com.douban.book.reader.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Constants;

public class NotificationUtils {
    private static final int DEFAULT_NID = 0;
    private static final int REQUEST_CODE_DEL_NOTIFICATION = -2;
    private static final int REQUEST_CODE_OPEN_NOTIFICATION = -1;
    private static NotificationManager mNotificationManager;

    static {
        mNotificationManager = (NotificationManager) App.get().getSystemService("notification");
    }

    public static void showMessage(String type, long nid, String title, String msg, Intent intent) {
        Intent forwardIntent = new Intent(Constants.ACTION_OPEN_NOTIFICATION);
        forwardIntent.putExtra(Constants.KEY_NOTIFICATION_ID, nid);
        forwardIntent.putExtra(Constants.KEY_FORWARD_INTENT, intent);
        PendingIntent pendingForwardIntent = PendingIntent.getBroadcast(App.get(), (int) nid, forwardIntent, 1207959552);
        Intent deleteIntent = new Intent(Constants.ACTION_DELETE_NOTIFICATION);
        deleteIntent.putExtra(Constants.KEY_NOTIFICATION_ID, nid);
        Notification notification = new Builder(App.get()).setSmallIcon(R.drawable.ic_push_notification).setTicker(msg).setContentIntent(pendingForwardIntent).setDeleteIntent(PendingIntent.getBroadcast(App.get(), REQUEST_CODE_DEL_NOTIFICATION, deleteIntent, 1073741824)).setContentText(msg).setContentTitle(title).setLights(Res.getColor(R.color.palette_day_blue), StatConstant.DEFAULT_MAX_EVENT_COUNT, 3000).setAutoCancel(true).setStyle(new BigTextStyle().bigText(msg)).build();
        mNotificationManager.cancel(type, DEFAULT_NID);
        mNotificationManager.notify(type, DEFAULT_NID, notification);
    }

    public static void cancelAll() {
        mNotificationManager.cancelAll();
    }
}
