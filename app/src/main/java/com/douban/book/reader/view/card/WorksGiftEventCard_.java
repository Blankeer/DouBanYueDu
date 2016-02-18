package com.douban.book.reader.view.card;

import android.content.Context;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksGiftEventCard_ extends WorksGiftEventCard implements HasViews {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public WorksGiftEventCard_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static WorksGiftEventCard build(Context context) {
        WorksGiftEventCard_ instance = new WorksGiftEventCard_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier.replaceNotifier(OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_));
    }
}
