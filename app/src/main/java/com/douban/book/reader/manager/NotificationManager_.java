package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class NotificationManager_ extends NotificationManager {
    private static NotificationManager_ instance_;
    private Context context_;

    private NotificationManager_(Context context) {
        this.context_ = context;
    }

    public static NotificationManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new NotificationManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
        this.mUnreadCountManager = UnreadCountManager_.getInstance_(this.context_);
    }
}
