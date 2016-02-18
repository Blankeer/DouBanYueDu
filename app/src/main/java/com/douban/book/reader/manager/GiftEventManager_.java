package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class GiftEventManager_ extends GiftEventManager {
    private static GiftEventManager_ instance_;
    private Context context_;

    private GiftEventManager_(Context context) {
        this.context_ = context;
    }

    public static GiftEventManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new GiftEventManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
    }
}
