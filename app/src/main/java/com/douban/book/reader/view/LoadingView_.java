package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class LoadingView_ extends LoadingView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public LoadingView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public LoadingView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public LoadingView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static LoadingView build(Context context) {
        LoadingView_ instance = new LoadingView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_loading, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static LoadingView build(Context context, AttributeSet attrs) {
        LoadingView_ instance = new LoadingView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static LoadingView build(Context context, AttributeSet attrs, int defStyle) {
        LoadingView_ instance = new LoadingView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mBlockProgressBar = (ProgressBar) hasViews.findViewById(R.id.block_progress_bar);
        this.mNormalProgressBar = (ProgressBar) hasViews.findViewById(R.id.normal_progress_bar);
        init();
    }
}
