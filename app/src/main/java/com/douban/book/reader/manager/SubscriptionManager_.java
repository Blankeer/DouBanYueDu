package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class SubscriptionManager_ extends SubscriptionManager {
    private static SubscriptionManager_ instance_;
    private Context context_;

    private SubscriptionManager_(Context context) {
        this.context_ = context;
    }

    public static SubscriptionManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new SubscriptionManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
    }
}
