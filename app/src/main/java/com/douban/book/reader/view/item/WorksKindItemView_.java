package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksKindItemView_ extends WorksKindItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public WorksKindItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public WorksKindItemView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public WorksKindItemView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static WorksKindItemView build(Context context) {
        WorksKindItemView_ instance = new WorksKindItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_works_kind_item, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static WorksKindItemView build(Context context, AttributeSet attrs) {
        WorksKindItemView_ instance = new WorksKindItemView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static WorksKindItemView build(Context context, AttributeSet attrs, int defStyle) {
        WorksKindItemView_ instance = new WorksKindItemView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mTitle = (TextView) hasViews.findViewById(com.wnafee.vector.R.id.title);
    }
}
