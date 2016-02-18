package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ShelfManager_ extends ShelfManager {
    private static ShelfManager_ instance_;
    private Context context_;

    private ShelfManager_(Context context) {
        this.context_ = context;
    }

    public static ShelfManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new ShelfManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
        this.mWorksManager = WorksManager_.getInstance_(this.context_);
    }
}
