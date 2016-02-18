package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ReviewManager_ extends ReviewManager {
    private static ReviewManager_ instance_;
    private Context context_;

    private ReviewManager_(Context context) {
        this.context_ = context;
    }

    public static ReviewManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new ReviewManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
        this.mWorksManager = WorksManager_.getInstance_(this.context_);
    }
}
