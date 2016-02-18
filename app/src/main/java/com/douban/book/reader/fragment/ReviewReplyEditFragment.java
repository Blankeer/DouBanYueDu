package com.douban.book.reader.fragment;

import com.douban.book.reader.R;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Res;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
public class ReviewReplyEditFragment extends BaseEditFragment {
    private Review mReview;
    @Bean
    ReviewManager mReviewManager;
    @FragmentArg
    int reviewId;

    protected void initData() throws DataLoadException {
        this.mReview = (Review) this.mReviewManager.get((Object) Integer.valueOf(this.reviewId));
    }

    protected void onDataReady() {
        setTitle(Res.getString(R.string.title_review_comments, this.mReview.getAuthorName()));
    }

    protected void postToServer(String content) throws DataLoadException {
        this.mReviewManager.newComment(this.reviewId, content);
    }
}
