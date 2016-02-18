package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class AccountManager_ extends AccountManager {
    private static AccountManager_ instance_;
    private Context context_;

    private AccountManager_(Context context) {
        this.context_ = context;
    }

    public static AccountManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new AccountManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
    }
}
