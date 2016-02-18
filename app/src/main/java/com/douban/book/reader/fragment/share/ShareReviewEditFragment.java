package com.douban.book.reader.fragment.share;

import android.net.Uri;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import com.douban.book.reader.helper.StoreUriHelper;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.view.ShareReviewInfoView_;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class ShareReviewEditFragment extends BaseShareEditFragment {
    private Review mReview;
    @Bean
    ReviewManager mReviewManager;
    private Works mWorks;
    @Bean
    WorksManager mWorksManager;
    @FragmentArg
    int reviewId;
    @FragmentArg
    int worksId;

    protected void initData() throws DataLoadException {
        this.mWorks = this.mWorksManager.getWorks(this.worksId);
        this.mReview = (Review) this.mReviewManager.get((Object) Integer.valueOf(this.reviewId));
    }

    protected String getContentType() {
        return BaseShareEditFragment.CONTENT_TYPE_REVIEW;
    }

    protected Object getContentId() {
        return Integer.valueOf(this.reviewId);
    }

    protected String getContentTitle() {
        return Res.getString(R.string.title_for_shared_review, this.mWorks.title, this.mReview.author.name);
    }

    protected String getComplicatedContentTitle() {
        return new RichText().append(getContentTitle()).append((CharSequence) " | ").append(Res.getString(R.string.app_name)).toString();
    }

    protected String getContentDescription() {
        return this.mReview.content;
    }

    protected String getContentThumbnailUri() {
        return this.mWorks.coverUrl;
    }

    protected Uri getContentUri() {
        return StoreUriHelper.review(this.reviewId);
    }

    protected Object getRelatedWorksId() {
        return Integer.valueOf(this.mWorks.id);
    }

    protected String getRelatedWorksTitle() {
        return this.mWorks.title;
    }

    protected void setupViews() {
        setTitle(Res.getString(R.string.title_share_review, this.mWorks.title));
        addBottomView(createBottomView());
    }

    protected void postToServer(String content) throws DataLoadException {
        this.mReviewManager.shareReview(this.reviewId, getShareTo(), content);
    }

    private View createBottomView() {
        return ShareReviewInfoView_.build(getActivity()).reviewId(this.reviewId).worksId(this.worksId).commit();
    }
}
