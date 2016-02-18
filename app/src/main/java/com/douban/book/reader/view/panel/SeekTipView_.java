package com.douban.book.reader.view.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class SeekTipView_ extends SeekTipView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public SeekTipView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public SeekTipView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public SeekTipView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static SeekTipView build(Context context) {
        SeekTipView_ instance = new SeekTipView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.panel_seek_tip, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static SeekTipView build(Context context, AttributeSet attrs) {
        SeekTipView_ instance = new SeekTipView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static SeekTipView build(Context context, AttributeSet attrs, int defStyle) {
        SeekTipView_ instance = new SeekTipView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mOverlay = hasViews.findViewById(R.id.reader_overlay);
        this.mOverlayGuide = hasViews.findViewById(R.id.reader_overlay_guide);
        this.mOverlayIndexScroller = (HorizontalScrollView) hasViews.findViewById(R.id.reader_overlay_index_scroller);
        this.mOverlayIndex = (LinearLayout) hasViews.findViewById(R.id.reader_overlay_index);
        this.mOverlayProgress = (TextView) hasViews.findViewById(R.id.reader_overlay_progress);
        this.mOverlayTitle = (TextView) hasViews.findViewById(R.id.reader_overlay_title);
    }
}
