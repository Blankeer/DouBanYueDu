package com.douban.book.reader.fragment;

import android.view.View;
import android.widget.RatingBar;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ShareTargetSelectorView;
import com.douban.book.reader.view.ShareTargetSelectorView_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class ReviewEditFragment extends BaseEditFragment {
    private RatingBar mRatingBar;
    @Bean
    ReviewManager mReviewManager;
    private ShareTargetSelectorView mShareTargetSelectorView;
    private Works mWorks;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    float rating;
    @FragmentArg
    int worksId;

    @AfterViews
    void init() {
        addTopView(createRatingView());
        addBottomView(createShareView());
        setHint(R.string.hint_new_review_edit);
        emptyPostAllowed(true);
    }

    protected void initData() throws DataLoadException {
        this.mWorks = this.mWorksManager.getWorks(this.worksId);
    }

    protected void onDataReady() {
        setTitle(Res.getString(R.string.title_edit_review, this.mWorks.title));
        if (this.mWorks.review != null) {
            this.mRatingBar.setRating((float) this.mWorks.review.rating);
            setContent(this.mWorks.review.content);
        }
    }

    public boolean shouldSubmit() {
        if (Math.round(this.mRatingBar.getRating()) > 0) {
            return super.shouldSubmit();
        }
        ToastUtils.showToast((int) R.string.toast_review_rating_not_zero);
        return false;
    }

    protected void postToServer(String content) throws DataLoadException {
        this.mReviewManager.addReview(this.worksId, content, Math.round(this.mRatingBar.getRating()), this.mShareTargetSelectorView != null ? this.mShareTargetSelectorView.getRequestParam() : null);
    }

    protected CharSequence getSucceedToastMessage() {
        return Res.getString(R.string.toast_post_rating_succeed);
    }

    private View createRatingView() {
        View ratingBarLayout = View.inflate(App.get(), R.layout.rating_bar_edit, null);
        this.mRatingBar = (RatingBar) ratingBarLayout.findViewById(R.id.rating_bar);
        this.mRatingBar.setRating(this.rating);
        ViewUtils.setBottomPaddingResId(this.mRatingBar, R.dimen.general_subview_vertical_padding_small);
        return ratingBarLayout;
    }

    private View createShareView() {
        this.mShareTargetSelectorView = ShareTargetSelectorView_.build(App.get());
        ViewUtils.of(this.mShareTargetSelectorView).height(-2).width(-1).commit();
        return this.mShareTargetSelectorView;
    }
}
