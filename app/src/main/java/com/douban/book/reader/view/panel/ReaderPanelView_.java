package com.douban.book.reader.view.panel;

import android.content.Context;
import android.util.AttributeSet;
import com.douban.book.reader.R;
import com.douban.book.reader.lib.view.DraggableLayout;
import com.douban.book.reader.view.IndexedSeekBar;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ReaderPanelView_ extends ReaderPanelView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public ReaderPanelView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReaderPanelView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReaderPanelView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ReaderPanelView build(Context context) {
        ReaderPanelView_ instance = new ReaderPanelView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_reader_panel, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static ReaderPanelView build(Context context, AttributeSet attrs) {
        ReaderPanelView_ instance = new ReaderPanelView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ReaderPanelView build(Context context, AttributeSet attrs, int defStyle) {
        ReaderPanelView_ instance = new ReaderPanelView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mBackground = hasViews.findViewById(R.id.background);
        this.mFitSystemWindowBaseView = hasViews.findViewById(R.id.fit_system_window_base);
        this.mCommandBar = (CommandBarView) hasViews.findViewById(R.id.command_bar);
        this.mSeekTip = (SeekTipView) hasViews.findViewById(R.id.seek_tip);
        this.mSettingView = (ReaderSettingView) hasViews.findViewById(R.id.reader_setting);
        this.mSeekBar = (IndexedSeekBar) hasViews.findViewById(R.id.reader_seek_bar);
        this.mRightDrawerBase = (DraggableLayout) hasViews.findViewById(R.id.right_drawer_frag_container);
        init();
    }
}
