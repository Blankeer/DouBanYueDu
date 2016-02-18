package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class AnnotationManager_ extends AnnotationManager {
    private static AnnotationManager_ instance_;
    private Context context_;

    private AnnotationManager_(Context context) {
        this.context_ = context;
    }

    public static AnnotationManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new AnnotationManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
    }
}
