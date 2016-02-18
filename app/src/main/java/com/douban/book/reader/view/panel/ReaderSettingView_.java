package com.douban.book.reader.view.panel;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.widget.RadioGroup;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.FontScaleManager_;
import com.douban.book.reader.manager.WorksManager_;
import com.douban.book.reader.view.IndexedSeekBar;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ReaderSettingView_ extends ReaderSettingView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public ReaderSettingView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReaderSettingView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReaderSettingView_(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ReaderSettingView build(Context context) {
        ReaderSettingView_ instance = new ReaderSettingView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.panel_reader_setting, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mFontScaleManager = FontScaleManager_.getInstance_(getContext());
        this.mWorksManager = WorksManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static ReaderSettingView build(Context context, AttributeSet attrs) {
        ReaderSettingView_ instance = new ReaderSettingView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ReaderSettingView build(Context context, AttributeSet attrs, int defStyleAttr) {
        ReaderSettingView_ instance = new ReaderSettingView_(context, attrs, defStyleAttr);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mBrightnessSeekBar = (IndexedSeekBar) hasViews.findViewById(R.id.brightness_seekbar);
        this.mSwitchUseSystemBrightness = (SwitchCompat) hasViews.findViewById(R.id.switch_use_system_brightness);
        this.mThemeGroup = (RadioGroup) hasViews.findViewById(R.id.radio_group_theme);
        this.mTextThemeDisabledForGallery = hasViews.findViewById(R.id.text_theme_disabled_for_gallery);
        this.mFontGroup = (RadioGroup) hasViews.findViewById(R.id.radio_group_font);
    }
}
