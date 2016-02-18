package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RatingBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WorksSummaryView_ extends WorksSummaryView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public WorksSummaryView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public WorksSummaryView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public WorksSummaryView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static WorksSummaryView build(Context context) {
        WorksSummaryView_ instance = new WorksSummaryView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_works_summary, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static WorksSummaryView build(Context context, AttributeSet attrs) {
        WorksSummaryView_ instance = new WorksSummaryView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static WorksSummaryView build(Context context, AttributeSet attrs, int defStyle) {
        WorksSummaryView_ instance = new WorksSummaryView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mCover = (WorksCoverView) hasViews.findViewById(R.id.cover);
        this.mTitle = (TextView) hasViews.findViewById(com.wnafee.vector.R.id.title);
        this.mSubTitle = (TextView) hasViews.findViewById(R.id.sub_title);
        this.mAuthors = (TextView) hasViews.findViewById(R.id.author);
        this.mTranslator = (TextView) hasViews.findViewById(R.id.translator);
        this.mRatingBar = (RatingBar) hasViews.findViewById(R.id.rating_bar);
        this.mRatingInfo = (TextView) hasViews.findViewById(R.id.rating_info);
        this.mCategory = (TextView) hasViews.findViewById(R.id.category);
        this.mContentSpace = (TextView) hasViews.findViewById(R.id.content_space);
        this.mWorksType = (TextView) hasViews.findViewById(R.id.works_type);
    }
}
