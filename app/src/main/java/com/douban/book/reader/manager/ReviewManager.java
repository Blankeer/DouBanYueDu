package com.douban.book.reader.manager;

import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.ShareTo;
import com.douban.book.reader.entity.Bookmark.Column;
import com.douban.book.reader.entity.Comment;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.event.CommentCreatedEvent;
import com.douban.book.reader.event.CommentDeletedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.ReviewChangedEvent;
import com.douban.book.reader.event.ReviewDeletedEvent;
import com.douban.book.reader.fragment.ReviewEditFragment_;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.client.RestClient;
import com.douban.book.reader.network.param.JsonRequestParam;
import com.douban.book.reader.network.param.RequestParam;
import com.sina.weibo.sdk.component.WidgetRequestParam;
import com.sina.weibo.sdk.constant.WBConstants;
import java.util.List;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class ReviewManager extends BaseManager<Review> {
    @Bean
    WorksManager mWorksManager;

    public static synchronized ReviewManager getInstance() {
        ReviewManager instance_;
        synchronized (ReviewManager.class) {
            instance_ = ReviewManager_.getInstance_(App.get());
        }
        return instance_;
    }

    public ReviewManager() {
        super("reviews", Review.class);
    }

    public Lister<Review> listForWorks(int worksId) {
        return defaultLister().filter(new DataFilter().append(Column.WORKS_ID, Integer.valueOf(worksId)));
    }

    public Review addReview(int worksId, String comment, int rating, RequestParam<?> shareParam) throws DataLoadException {
        if (shareParam == null) {
            shareParam = RequestParam.json();
        }
        shareParam.append(Column.WORKS_ID, Integer.valueOf(worksId)).append("comment", comment).append(ReviewEditFragment_.RATING_ARG, Integer.valueOf(rating));
        Review review = (Review) post(shareParam);
        this.mWorksManager.getFromRemote(Integer.valueOf(worksId));
        EventBusUtils.post(new ReviewChangedEvent(worksId, review.id));
        return review;
    }

    public void deleteReview(Review review) throws DataLoadException {
        if (review != null) {
            delete(Integer.valueOf(review.id));
            this.mWorksManager.getFromRemote(Integer.valueOf(review.worksId));
            EventBusUtils.post(new ReviewChangedEvent(review.worksId, review.id));
            EventBusUtils.post(new ReviewDeletedEvent(review.id));
        }
    }

    public void deleteComment(int reviewId, int commentId) throws DataLoadException {
        getCommentManager(reviewId).delete(Integer.valueOf(commentId));
        EventBusUtils.post(new CommentDeletedEvent(reviewId, commentId));
    }

    public void shareReview(int reviewId, ShareTo shareTo, String text) throws DataLoadException {
        getSubManagerForId(Integer.valueOf(reviewId), "rec").post(((JsonRequestParam) RequestParam.json().appendShareTo(shareTo)).appendIfNotEmpty("text", text));
    }

    public Lister<Comment> listComments(int reviewId) {
        return getCommentManager(reviewId).list();
    }

    public void newComment(int reviewId, String content) throws DataLoadException {
        getCommentManager(reviewId).post(RequestParam.json().append(WidgetRequestParam.REQ_PARAM_COMMENT_CONTENT, content));
        EventBusUtils.post(new CommentCreatedEvent(reviewId));
    }

    public void addUpVote(int reviewId) throws DataLoadException {
        addVote(reviewId, true);
    }

    public void deleteUpVote(int reviewId) throws DataLoadException {
        deleteVote(reviewId, true);
    }

    public void addDownVote(int reviewId) throws DataLoadException {
        addVote(reviewId, false);
    }

    public void deleteDownVote(int reviewId) throws DataLoadException {
        deleteVote(reviewId, false);
    }

    private void addVote(int reviewId, boolean isUseful) throws DataLoadException {
        try {
            getVoteClient(reviewId).put(RequestParam.json().append("is_useful", Boolean.valueOf(isUseful)));
            getFromRemote(Integer.valueOf(reviewId));
            EventBusUtils.post(new ReviewChangedEvent(0, reviewId));
        } catch (Exception e) {
            throw wrapDataLoadException(e);
        }
    }

    private void deleteVote(int reviewId, boolean isUseful) throws DataLoadException {
        try {
            getVoteClient(reviewId).deleteEntity(RequestParam.queryString().append("is_useful", Boolean.valueOf(isUseful)));
            getFromRemote(Integer.valueOf(reviewId));
            EventBusUtils.post(new ReviewChangedEvent(0, reviewId));
        } catch (Exception e) {
            throw wrapDataLoadException(e);
        }
    }

    public List<Review> getHottestReviewsForWorks(int worksId, int limit) throws DataLoadException {
        return defaultLister().filter(new DataFilter().append(Column.WORKS_ID, Integer.valueOf(worksId)).append("order", WBConstants.GAME_PARAMS_SCORE).append("limit", Integer.valueOf(limit))).loadMore();
    }

    public Review getByLegacyId(int reviewId) throws DataLoadException {
        return (Review) getSubManager("mapping", Review.class).get(RequestParam.queryString().append(WorksListUri.KEY_ID, Integer.valueOf(reviewId)));
    }

    private RestClient<Review> getVoteClient(int reviewId) {
        return getRestClient().getSubClientWithId(Integer.valueOf(reviewId), "vote", Review.class);
    }

    private BaseManager<Comment> getCommentManager(int reviewId) {
        return getSubManagerForId(Integer.valueOf(reviewId), "comments", Comment.class);
    }
}
