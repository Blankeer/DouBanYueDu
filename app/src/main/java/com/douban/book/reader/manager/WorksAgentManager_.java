package com.douban.book.reader.manager;

import android.content.Context;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksAgentManager_ extends WorksAgentManager {
    private static WorksAgentManager_ instance_;
    private Context context_;

    private WorksAgentManager_(Context context) {
        this.context_ = context;
    }

    public static WorksAgentManager_ getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new WorksAgentManager_(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }
        return instance_;
    }

    private void init_() {
    }
}
