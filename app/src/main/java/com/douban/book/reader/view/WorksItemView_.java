package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksItemView_ extends WorksItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public WorksItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public WorksItemView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public WorksItemView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static WorksItemView build(Context context) {
        WorksItemView_ instance = new WorksItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_works_item, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static WorksItemView build(Context context, AttributeSet attrs) {
        WorksItemView_ instance = new WorksItemView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static WorksItemView build(Context context, AttributeSet attrs, int defStyle) {
        WorksItemView_ instance = new WorksItemView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mCover = (WorksCoverView) hasViews.findViewById(R.id.cover);
        this.mTitle = (TextView) hasViews.findViewById(com.wnafee.vector.R.id.title);
        this.mAuthors = (TextView) hasViews.findViewById(R.id.authors);
        this.mOwnedDate = (TextView) hasViews.findViewById(R.id.owned_date);
        this.mIconGift = (ImageView) hasViews.findViewById(R.id.ic_gift);
        this.mWorksUCGView = (WorksUGCView) hasViews.findViewById(R.id.works_ugc);
        this.mWorksStatusView = (WorksStatusView) hasViews.findViewById(R.id.works_status);
        this.mDividerToLeftOfWorksStatus = hasViews.findViewById(R.id.divider_to_left_of_works_status);
        init();
    }
}
