package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.VoteView.VoteListener;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903180)
public class ReviewDetailView extends LinearLayout implements VoteListener {
    private static final String TAG;
    @ViewById(2131558925)
    View mDividerAboveVote;
    private Review mReview;
    private int mReviewId;
    @Bean
    ReviewManager mReviewManager;
    @ViewById(2131558924)
    ReviewSummaryView mReviewSummary;
    @ViewById(2131558926)
    VoteView mVoteView;
    private int mWorksId;
    @Bean
    WorksManager mWorksManager;

    static {
        TAG = ReviewDetailView.class.getSimpleName();
    }

    public ReviewDetailView(Context context) {
        super(context);
    }

    public ReviewDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReviewDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ReviewDetailView reviewId(int reviewId) {
        this.mReviewId = reviewId;
        loadReview();
        return this;
    }

    public ReviewDetailView worksId(int worksId) {
        this.mWorksId = worksId;
        return this;
    }

    @AfterViews
    void init() {
        this.mVoteView.setVoteListener(this);
    }

    @UiThread
    public void refreshViews(Review review) {
        this.mReview = review;
        this.mReviewSummary.reviewId(review.id).worksId(this.mWorksId);
        this.mVoteView.displayForReview(review);
        ViewUtils.showIf(StringUtils.isNotEmpty(this.mReview.content), this.mDividerAboveVote, this.mVoteView);
    }

    @Background
    void loadReview() {
        try {
            refreshViews((Review) this.mReviewManager.get((Object) Integer.valueOf(this.mReviewId)));
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    public void performVote(boolean isVoteUp) {
        try {
            toggleVoteStatus(isVoteUp);
            loadReview();
        } catch (Throwable e) {
            ToastUtils.showToast(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.toast_general_op_failed));
        }
    }

    private void toggleVoteStatus(boolean isVoteUp) throws DataLoadException {
        if (this.mReview != null) {
            if (isVoteUp) {
                if (this.mReview.isUpVoted()) {
                    this.mReviewManager.deleteUpVote(this.mReview.id);
                } else {
                    this.mReviewManager.addUpVote(this.mReviewId);
                }
            } else if (this.mReview.isDownVoted()) {
                this.mReviewManager.deleteDownVote(this.mReview.id);
            } else {
                this.mReviewManager.addDownVote(this.mReviewId);
            }
        }
    }
}
