package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ReaderLoadingView_ extends ReaderLoadingView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public ReaderLoadingView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReaderLoadingView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReaderLoadingView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ReaderLoadingView build(Context context) {
        ReaderLoadingView_ instance = new ReaderLoadingView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_load_status, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static ReaderLoadingView build(Context context, AttributeSet attrs) {
        ReaderLoadingView_ instance = new ReaderLoadingView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ReaderLoadingView build(Context context, AttributeSet attrs, int defStyle) {
        ReaderLoadingView_ instance = new ReaderLoadingView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mTextStatus = (TextView) hasViews.findViewById(R.id.text_status);
        this.mProgressBar = (ProgressBar) hasViews.findViewById(R.id.download_progress);
        this.mBtnRetry = (Button) hasViews.findViewById(R.id.btn_retry);
        this.mBtnReport = (Button) hasViews.findViewById(R.id.btn_report);
        this.mGuideForShelf = (TextView) hasViews.findViewById(R.id.guide_for_shelf);
        if (this.mBtnRetry != null) {
            this.mBtnRetry.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ReaderLoadingView_.this.onBtnRetryClicked();
                }
            });
        }
        if (this.mBtnReport != null) {
            this.mBtnReport.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ReaderLoadingView_.this.onBtnReportClicked();
                }
            });
        }
        init();
    }
}
