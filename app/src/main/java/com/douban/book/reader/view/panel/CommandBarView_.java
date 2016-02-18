package com.douban.book.reader.view.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class CommandBarView_ extends CommandBarView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public CommandBarView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public CommandBarView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public CommandBarView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static CommandBarView build(Context context) {
        CommandBarView_ instance = new CommandBarView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.panel_command_bar, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static CommandBarView build(Context context, AttributeSet attrs) {
        CommandBarView_ instance = new CommandBarView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static CommandBarView build(Context context, AttributeSet attrs, int defStyle) {
        CommandBarView_ instance = new CommandBarView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mBtnToc = (TextView) hasViews.findViewById(R.id.command_btn_contents);
        this.mBtnAnnotation = (TextView) hasViews.findViewById(R.id.command_btn_notes);
        this.mBtnReview = (TextView) hasViews.findViewById(R.id.command_btn_reviews);
        this.mBtnSetting = (TextView) hasViews.findViewById(R.id.command_btn_settings);
        this.mSeekBarView = (SeekBarView) hasViews.findViewById(R.id.seek_bar_panel);
        if (this.mBtnToc != null) {
            this.mBtnToc.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    CommandBarView_.this.onBtnContentsClicked();
                }
            });
        }
        if (this.mBtnReview != null) {
            this.mBtnReview.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    CommandBarView_.this.onBtnReviewsClicked();
                }
            });
        }
        if (this.mBtnAnnotation != null) {
            this.mBtnAnnotation.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    CommandBarView_.this.onBtnNotesClicked();
                }
            });
        }
        if (this.mBtnSetting != null) {
            this.mBtnSetting.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    CommandBarView_.this.onBtnSettingsClicked();
                }
            });
        }
        init();
    }
}
