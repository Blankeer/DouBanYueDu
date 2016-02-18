package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class UserManager_ extends UserManager {
    private static UserManager_ instance_;
    private Context context_;

    private UserManager_(Context context) {
        this.context_ = context;
    }

    public static UserManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new UserManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
        this.mSessionManager = SessionManager_.getInstance_(this.context_);
        this.mShelfManager = ShelfManager_.getInstance_(this.context_);
        this.mUnreadCountManager = UnreadCountManager_.getInstance_(this.context_);
    }
}
