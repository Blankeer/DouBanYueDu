package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class FeedManager_ extends FeedManager {
    private static FeedManager_ instance_;
    private Context context_;

    private FeedManager_(Context context) {
        this.context_ = context;
    }

    public static FeedManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new FeedManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
        this.mUnreadCountManager = UnreadCountManager_.getInstance_(this.context_);
    }
}
