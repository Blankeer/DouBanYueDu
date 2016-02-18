package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.ReviewChangedEvent;
import com.douban.book.reader.fragment.ReviewDetailFragment_;
import com.douban.book.reader.fragment.ReviewEditFragment_;
import com.douban.book.reader.fragment.WorksUgcFragment_;
import com.douban.book.reader.fragment.interceptor.DownloadNeededInterceptor;
import com.douban.book.reader.fragment.interceptor.ForcedLoginInterceptor;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903206)
public class WorksUGCView extends FrameLayout {
    public static final int MODE_MULTILINE = 1;
    public static final int MODE_SINGLE = 0;
    private static final String TAG;
    @ViewById(2131558976)
    TextView mNoteCount;
    @ViewById(2131558974)
    LinearLayout mNoteLayout;
    @ViewById(2131558932)
    LinearLayout mRateLayout;
    @ViewById(2131558978)
    RatingBar mRatingBar;
    private Works mWorks;
    private int mWorksId;
    @ViewById(2131558965)
    WorksStatusView mWorksStatusView;

    static {
        TAG = WorksUGCView.class.getSimpleName();
    }

    public WorksUGCView(Context context) {
        super(context);
    }

    public WorksUGCView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorksUGCView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WorksUGCView worksId(int worksId) {
        this.mWorksId = worksId;
        loadWorks();
        return this;
    }

    @AfterViews
    void init() {
        ViewUtils.setEventAware(this);
    }

    @UiThread
    void refreshViews() {
        if (this.mWorks != null) {
            this.mWorksStatusView.worksId(this.mWorksId);
            this.mNoteCount.setText(StringUtils.toStr(Integer.valueOf(this.mWorks.getNoteCount())));
            Review review = this.mWorks.review;
            if (review != null) {
                this.mRatingBar.setRating((float) review.rating);
            } else {
                this.mRatingBar.setRating(0.0f);
            }
        }
    }

    @Background
    void loadWorks() {
        try {
            this.mWorks = (Works) WorksManager.getInstance().get((Object) Integer.valueOf(this.mWorksId));
            refreshViews();
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    public void setMode(int mode) {
        if (mode == MODE_MULTILINE) {
            this.mRateLayout.setOrientation(MODE_MULTILINE);
            this.mNoteLayout.setOrientation(MODE_MULTILINE);
        } else if (mode == 0) {
            this.mRateLayout.setOrientation(0);
            this.mNoteLayout.setOrientation(0);
        }
    }

    @Click({2131558974})
    void onNoteClicked() {
        if (this.mWorks != null) {
            WorksUgcFragment_.builder().worksId(this.mWorks.id).pagingNeeded(true).build().setShowInterceptor(new DownloadNeededInterceptor(this.mWorks.id)).showAsActivity((View) this);
        }
    }

    @Click({2131558932})
    void onReviewClicked() {
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
