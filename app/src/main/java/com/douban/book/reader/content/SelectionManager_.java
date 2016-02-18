package com.douban.book.reader.content;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class SelectionManager_ extends SelectionManager {
    private static SelectionManager_ instance_;
    private Context context_;

    private SelectionManager_(Context context) {
        this.context_ = context;
    }

    public static SelectionManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new SelectionManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
    }
}
