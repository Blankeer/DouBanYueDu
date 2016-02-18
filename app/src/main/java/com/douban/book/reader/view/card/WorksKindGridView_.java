package com.douban.book.reader.view.card;

import android.content.Context;
import android.util.AttributeSet;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksKindGridView_ extends WorksKindGridView implements HasViews {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public WorksKindGridView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public WorksKindGridView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public WorksKindGridView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static WorksKindGridView build(Context context) {
        WorksKindGridView_ instance = new WorksKindGridView_(context);
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

    public static WorksKindGridView build(Context context, AttributeSet attrs) {
        WorksKindGridView_ instance = new WorksKindGridView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static WorksKindGridView build(Context context, AttributeSet attrs, int defStyle) {
        WorksKindGridView_ instance = new WorksKindGridView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }
}
