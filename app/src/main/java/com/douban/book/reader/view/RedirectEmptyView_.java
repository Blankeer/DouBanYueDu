package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class RedirectEmptyView_ extends RedirectEmptyView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public RedirectEmptyView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public RedirectEmptyView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public RedirectEmptyView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static RedirectEmptyView build(Context context) {
        RedirectEmptyView_ instance = new RedirectEmptyView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_redirect_empty, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static RedirectEmptyView build(Context context, AttributeSet attrs) {
        RedirectEmptyView_ instance = new RedirectEmptyView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static RedirectEmptyView build(Context context, AttributeSet attrs, int defStyle) {
        RedirectEmptyView_ instance = new RedirectEmptyView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mBtnRedirect = (Button) hasViews.findViewById(R.id.btn_redirect);
        this.mHint = (TextView) hasViews.findViewById(R.id.hint);
        if (this.mBtnRedirect != null) {
            this.mBtnRedirect.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    RedirectEmptyView_.this.onBtnRedirectClicked();
                }
            });
        }
        init();
    }
}
