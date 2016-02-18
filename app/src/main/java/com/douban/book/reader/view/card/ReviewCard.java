package com.douban.book.reader.view.card;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.event.WorksUpdatedEvent;
import com.douban.book.reader.fragment.ReviewDetailFragment_;
import com.douban.book.reader.fragment.ReviewListFragment_;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.manager.exception.NoSuchDataException;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ReviewItemView;
import com.douban.book.reader.view.ReviewItemView_;
import java.util.List;
import java.util.concurrent.Callable;
import natalya.os.TaskExecutor.TaskCallback;

public class ReviewCard extends Card<ReviewCard> {
    private Button mBtnToReviewList;
    private LinearLayout mReviewItemsLayoutBase;
    private int mWorksId;

    public ReviewCard(Context context) {
        super(context);
        init();
    }

    public ReviewCard worksId(int worksId) {
        this.mWorksId = worksId;
        loadReview();
        return this;
    }

    private void init() {
        ViewUtils.setBottomPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        content((int) R.layout.card_item_list_with_bottom_button);
        ViewUtils.setEventAware(this);
        this.mReviewItemsLayoutBase = (LinearLayout) findViewById(R.id.item_list_layout_base);
        this.mBtnToReviewList = (Button) findViewById(R.id.btn_bottom);
        this.mBtnToReviewList.setText(RichText.textWithIcon((int) R.drawable.v_review, (int) R.string.btn_more_reviews));
        this.mBtnToReviewList.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ReviewListFragment_.builder().worksId(ReviewCard.this.mWorksId).build().showAsActivity(ReviewCard.this);
            }
        });
        noContentPadding();
    }

    public void onEventMainThread(WorksUpdatedEvent event) {
        if (event.isValidFor(this.mWorksId)) {
            loadReview();
        }
    }

    private void loadReview() {
        TaskController.getInstance().execute(new Callable<List<Review>>() {
            public List<Review> call() throws DataLoadException {
                return ReviewManager.getInstance().getHottestReviewsForWorks(ReviewCard.this.mWorksId, 3);
            }
        }, new TaskCallback<List<Review>>() {

            /* renamed from: com.douban.book.reader.view.card.ReviewCard.3.1 */
            class AnonymousClass1 implements OnClickListener {
                final /* synthetic */ Review val$review;

                AnonymousClass1(Review review) {
                    this.val$review = review;
                }

                public void onClick(View v) {
                    ReviewDetailFragment_.builder().worksId(ReviewCard.this.mWorksId).reviewId(this.val$review.id).build().showAsActivity(ReviewCard.this);
                }
            }

            public void onTaskSuccess(List<Review> reviewList, Bundle extras, Object object) {
                for (Review review : reviewList) {
                    ReviewItemView reviewItemView = ReviewItemView_.build(App.get());
                    reviewItemView.reviewId(review.id);
                    reviewItemView.verticalPadding(Res.getDimensionPixelSize(R.dimen.general_subview_vertical_padding_normal));
                    reviewItemView.setOnClickListener(new AnonymousClass1(review));
                    ReviewCard.this.mReviewItemsLayoutBase.addView(reviewItemView);
                }
                ReviewCard.this.mBtnToReviewList.setText(RichText.textWithIcon((int) R.drawable.v_review, reviewList.isEmpty() ? R.string.msg_no_reviews_for_works : R.string.btn_more_reviews));
            }

            public void onTaskFailure(Throwable e, Bundle extras) {
                Logger.e(ReviewCard.this.TAG, e);
                if (ExceptionUtils.isCausedBy(e, NoSuchDataException.class)) {
                    ReviewCard.this.mBtnToReviewList.setText(RichText.textWithIcon((int) R.drawable.v_review, (int) R.string.msg_no_reviews_for_works));
                } else {
                    Logger.e(ReviewCard.this.TAG, e);
                }
            }
        }, this);
    }
}
