package com.douban.book.reader.view.store.card.content;

import android.content.Context;
import android.util.AttributeSet;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ChartsWidgetContentView_ extends ChartsWidgetContentView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public ChartsWidgetContentView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ChartsWidgetContentView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ChartsWidgetContentView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ChartsWidgetContentView build(Context context) {
        ChartsWidgetContentView_ instance = new ChartsWidgetContentView_(context);
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
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static ChartsWidgetContentView build(Context context, AttributeSet attrs) {
        ChartsWidgetContentView_ instance = new ChartsWidgetContentView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ChartsWidgetContentView build(Context context, AttributeSet attrs, int defStyle) {
        ChartsWidgetContentView_ instance = new ChartsWidgetContentView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }
}
