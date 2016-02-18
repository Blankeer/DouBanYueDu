package com.douban.book.reader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.fragment.WorksListFragment_;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.Dumper;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;

public class NotificationActionReceiver extends BroadcastReceiver {
    private static final String TAG;

    static {
        TAG = NotificationActionReceiver.class.getSimpleName();
    }

    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "received: %s", Dumper.dump(intent));
        if (intent == null) {
            try {
                throw new Exception("intent == null");
            } catch (Exception e) {
                Logger.ec(TAG, e, "Failed to open notification. intent=%s", Dumper.dump(intent));
                Analysis.sendEventWithExtra("Push", "OPEN_NOTIFY_FAILED", String.valueOf(e));
                return;
            }
        }
        CharSequence action = intent.getAction();
        JsonRequestParam extra = (JsonRequestParam) RequestParam.json().append("nid", Long.valueOf(intent.getLongExtra(Constants.KEY_NOTIFICATION_ID, -1)));
        Uri uri = null;
        Intent forwardIntent = (Intent) intent.getParcelableExtra(Constants.KEY_FORWARD_INTENT);
        if (forwardIntent != null) {
            uri = forwardIntent.getData();
            if (uri != null) {
                extra.append(WorksListFragment_.URI_ARG, uri);
            }
        }
        if (StringUtils.equals(action, Constants.ACTION_OPEN_NOTIFICATION)) {
            Logger.d(TAG, "open notification", new Object[0]);
            boolean succeed = false;
            if (!(uri == null || !StringUtils.equals(forwardIntent.getAction(), (CharSequence) "android.intent.action.VIEW") || forwardIntent.getData() == null)) {
                succeed = PageOpenHelper.fromApp("PushNotification").preferInternalWebView().open(forwardIntent.getData());
            }
            extra.append("succeed", Boolean.valueOf(succeed));
            Analysis.sendEventWithExtra("Push", "OPEN_NOTIFY", extra.getJsonObject());
        } else if (StringUtils.equals(action, Constants.ACTION_DELETE_NOTIFICATION)) {
            Logger.d(TAG, "delete notification", new Object[0]);
            Analysis.sendEventWithExtra("Push", "DEL_NOTIFY", extra.getJsonObject());
        }
    }
}
