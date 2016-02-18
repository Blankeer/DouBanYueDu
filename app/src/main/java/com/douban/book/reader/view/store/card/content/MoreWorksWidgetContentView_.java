package com.douban.book.reader.view.store.card.content;

import android.content.Context;
import android.util.AttributeSet;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class MoreWorksWidgetContentView_ extends MoreWorksWidgetContentView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public MoreWorksWidgetContentView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public MoreWorksWidgetContentView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public MoreWorksWidgetContentView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static MoreWorksWidgetContentView build(Context context) {
        MoreWorksWidgetContentView_ instance = new MoreWorksWidgetContentView_(context);
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

    public static MoreWorksWidgetContentView build(Context context, AttributeSet attrs) {
        MoreWorksWidgetContentView_ instance = new MoreWorksWidgetContentView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static MoreWorksWidgetContentView build(Context context, AttributeSet attrs, int defStyle) {
        MoreWorksWidgetContentView_ instance = new MoreWorksWidgetContentView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }
}
