package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RatingBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class CoverLeftWorksView_ extends CoverLeftWorksView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public CoverLeftWorksView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public CoverLeftWorksView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public CoverLeftWorksView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static CoverLeftWorksView build(Context context) {
        CoverLeftWorksView_ instance = new CoverLeftWorksView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_cover_left_works, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static CoverLeftWorksView build(Context context, AttributeSet attrs) {
        CoverLeftWorksView_ instance = new CoverLeftWorksView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static CoverLeftWorksView build(Context context, AttributeSet attrs, int defStyle) {
        CoverLeftWorksView_ instance = new CoverLeftWorksView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mCover = (WorksCoverView) hasViews.findViewById(R.id.cover);
        this.mTitle = (TextView) hasViews.findViewById(com.wnafee.vector.R.id.title);
        this.mSubTitle = (TextView) hasViews.findViewById(R.id.sub_title);
        this.mAuthor = (TextView) hasViews.findViewById(R.id.author);
        this.mPrice = (PriceLabelView) hasViews.findViewById(R.id.works_price);
        this.mAbstractText = (TextView) hasViews.findViewById(R.id.abstract_text);
        this.mRatingLayout = hasViews.findViewById(R.id.rating_layout);
        this.mAverageRate = (TextView) hasViews.findViewById(R.id.rating_info);
        this.mRatingBar = (RatingBar) hasViews.findViewById(R.id.rating_bar);
        this.mRootView = hasViews.findViewById(R.id.root);
    }
}
