package com.douban.book.reader.view.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.view.IndexedSeekBar;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class SeekBarView_ extends SeekBarView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public SeekBarView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public SeekBarView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public SeekBarView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static SeekBarView build(Context context) {
        SeekBarView_ instance = new SeekBarView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.panel_seek_bar, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static SeekBarView build(Context context, AttributeSet attrs) {
        SeekBarView_ instance = new SeekBarView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static SeekBarView build(Context context, AttributeSet attrs, int defStyle) {
        SeekBarView_ instance = new SeekBarView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mSeekBar = (IndexedSeekBar) hasViews.findViewById(R.id.reader_seek_bar);
        this.mBtnJump = (LinearLayout) hasViews.findViewById(R.id.reader_btn_jump);
        this.mBtnJumpImage = (ImageView) hasViews.findViewById(R.id.reader_btn_jump_image);
        if (this.mBtnJump != null) {
            this.mBtnJump.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    SeekBarView_.this.onJumpBtnClicked();
                }
            });
        }
    }
}
