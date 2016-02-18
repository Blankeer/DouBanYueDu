package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class UnreadCountManager_ extends UnreadCountManager {
    private static UnreadCountManager_ instance_;
    private Context context_;

    private UnreadCountManager_(Context context) {
        this.context_ = context;
    }

    public static UnreadCountManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new UnreadCountManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
    }
}
