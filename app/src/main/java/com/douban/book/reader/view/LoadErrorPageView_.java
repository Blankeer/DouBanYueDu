package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class LoadErrorPageView_ extends LoadErrorPageView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public LoadErrorPageView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public LoadErrorPageView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public LoadErrorPageView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static LoadErrorPageView build(Context context) {
        LoadErrorPageView_ instance = new LoadErrorPageView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_load_error_page, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static LoadErrorPageView build(Context context, AttributeSet attrs) {
        LoadErrorPageView_ instance = new LoadErrorPageView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static LoadErrorPageView build(Context context, AttributeSet attrs, int defStyle) {
        LoadErrorPageView_ instance = new LoadErrorPageView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mImgBie = (ImageView) hasViews.findViewById(R.id.img_bie);
        this.mHintText = (TextView) hasViews.findViewById(R.id.hint_text);
        this.mBtnToShelf = (Button) hasViews.findViewById(R.id.btn_to_shelf);
        this.mBtnRefresh = (Button) hasViews.findViewById(R.id.btn_refresh);
        if (this.mBtnRefresh != null) {
            this.mBtnRefresh.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LoadErrorPageView_.this.onBtnRefreshClicked();
                }
            });
        }
        if (this.mBtnToShelf != null) {
            this.mBtnToShelf.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LoadErrorPageView_.this.onBtnToShelf();
                }
            });
        }
        init();
    }
}
