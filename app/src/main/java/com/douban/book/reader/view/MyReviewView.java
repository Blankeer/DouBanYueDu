package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.ReviewChangedEvent;
import com.douban.book.reader.fragment.ReviewDetailFragment_;
import com.douban.book.reader.fragment.ReviewEditFragment_;
import com.douban.book.reader.fragment.interceptor.ForcedLoginInterceptor;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903167)
public class MyReviewView extends RelativeLayout {
    private static final String TAG;
    @ViewById(2131558879)
    ProgressBar mProgressBar;
    @ViewById(2131558549)
    RatingBar mRatingBar;
    @ViewById(2131558878)
    ReviewItemView mReviewItemView;
    @ViewById(2131558877)
    View mReviewLayout;
    @ViewById(2131558586)
    View mRootView;
    private Works mWorks;
    private int mWorksId;
    @Bean
    WorksManager mWorksManager;

    static {
        TAG = MyReviewView.class.getSimpleName();
    }

    public MyReviewView(Context context) {
        super(context);
    }

    public MyReviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyReviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        ViewUtils.setEventAware(this);
    }

    public MyReviewView worksId(int worksId) {
        this.mWorksId = worksId;
        loadWorks();
        return this;
    }

    @UiThread
    public void startLoading() {
        this.mRatingBar.setVisibility(8);
        this.mRatingBar.setIsIndicator(true);
        this.mProgressBar.setVisibility(0);
        this.mReviewItemView.setVisibility(8);
    }

    @UiThread
    public void finishLoading() {
        this.mProgressBar.setVisibility(8);
    }

    @UiThread
    public void updateViews(Works works) {
        this.mWorks = works;
        this.mReviewItemView.noBackground();
        if (works.review != null) {
            this.mReviewLayout.setVisibility(0);
            this.mRatingBar.setVisibility(8);
            this.mReviewItemView.setVisibility(0);
            this.mReviewItemView.reviewId(works.review.id).addReviewHintVisible(true);
            return;
        }
        this.mRatingBar.setRating(0.0f);
        this.mReviewLayout.setVisibility(8);
        this.mRatingBar.setVisibility(0);
        this.mRatingBar.setIsIndicator(false);
        this.mRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    ReviewEditFragment_.builder().worksId(MyReviewView.this.mWorksId).rating(ratingBar.getRating()).build().setShowInterceptor(new ForcedLoginInterceptor()).showAsActivity(MyReviewView.this);
                }
            }
        });
        this.mReviewItemView.setVisibility(8);
    }

    @Background
    void loadWorks() {
        startLoading();
        try {
            updateViews((Works) this.mWorksManager.get((Object) Integer.valueOf(this.mWorksId)));
        } catch (Exception e) {
            Logger.e(TAG, e);
        } finally {
            finishLoading();
        }
    }

    @Click({2131558878})
    void onReviewItemClicked() {
        if (this.mWorks != null) {
            if (this.mWorks.review == null || !this.mWorks.review.hasContent()) {
                ReviewEditFragment_.builder().worksId(this.mWorks.id).build().setShowInterceptor(new ForcedLoginInterceptor()).showAsActivity((View) this);
            } else {
                ReviewDetailFragment_.builder().reviewId(this.mWorks.review.id).worksId(this.mWorks.id).build().showAsActivity((View) this);
            }
        }
    }

    public void onEventMainThread(ReviewChangedEvent event) {
        if (event.isValidForWorks(this.mWorksId)) {
            loadWorks();
        }
    }
}
