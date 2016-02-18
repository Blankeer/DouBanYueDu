package com.douban.book.reader.view.store;

import android.content.Context;
import android.util.AttributeSet;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksGridView_ extends WorksGridView implements HasViews {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public WorksGridView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public WorksGridView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public WorksGridView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static WorksGridView build(Context context) {
        WorksGridView_ instance = new WorksGridView_(context);
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

    public static WorksGridView build(Context context, AttributeSet attrs) {
        WorksGridView_ instance = new WorksGridView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static WorksGridView build(Context context, AttributeSet attrs, int defStyle) {
        WorksGridView_ instance = new WorksGridView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }
}
