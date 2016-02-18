package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class SessionManager_ extends SessionManager {
    private static SessionManager_ instance_;
    private Context context_;

    private SessionManager_(Context context) {
        this.context_ = context;
    }

    public static SessionManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new SessionManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
    }
}
