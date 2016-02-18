package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.ReviewChangedEvent;
import com.douban.book.reader.fragment.ReviewEditFragment_;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.span.IconFontSpan;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903181)
public class ReviewItemView extends RelativeLayout implements ViewBinder<Review> {
    private static final String TAG;
    private boolean mAddReviewHintVisible;
    @ViewById(2131558774)
    TextView mAuthor;
    @ViewById(2131558928)
    TextView mCommentInfo;
    @ViewById(2131558770)
    TextView mContent;
    @ViewById(2131558764)
    TextView mCreateDate;
    @ViewById(2131558927)
    RatingBar mRate;
    private int mReviewId;
    @Bean
    ReviewManager mReviewManager;
    @ViewById(2131558586)
    ViewGroup mRootView;

    /* renamed from: com.douban.book.reader.view.ReviewItemView.1 */
    class AnonymousClass1 implements OnClickListener {
        final /* synthetic */ Review val$review;

        AnonymousClass1(Review review) {
            this.val$review = review;
        }

        public void onClick(View v) {
            ReviewEditFragment_.builder().worksId(this.val$review.worksId).build().showAsActivity(ReviewItemView.this);
        }
    }

    /* renamed from: com.douban.book.reader.view.ReviewItemView.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Review val$review;

        AnonymousClass2(Review review) {
            this.val$review = review;
        }

        public void run() {
            ReviewItemView.this.bindData(this.val$review);
        }
    }

    static {
        TAG = ReviewItemView.class.getSimpleName();
    }

    public ReviewItemView(Context context) {
        super(context);
        this.mAddReviewHintVisible = false;
    }

    public ReviewItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAddReviewHintVisible = false;
    }

    public ReviewItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAddReviewHintVisible = false;
    }

    public ReviewItemView reviewId(int reviewId) {
        this.mReviewId = reviewId;
        loadReview();
        return this;
    }

    public ReviewItemView noBackground() {
        this.mRootView.setBackgroundColor(Res.getColor(R.color.transparent));
        return this;
    }

    public ReviewItemView verticalPadding(int verticalPadding) {
        ViewUtils.setVerticalPadding(this.mRootView, verticalPadding);
        return this;
    }

    public ReviewItemView addReviewHintVisible(boolean visible) {
        this.mAddReviewHintVisible = visible;
        return this;
    }

    public void bindData(Review review) {
        if (review != null) {
            this.mRate.setRating((float) review.rating);
            if (StringUtils.isEmpty(review.content) && this.mAddReviewHintVisible) {
                this.mContent.setText(R.string.hint_add_review);
                ViewUtils.setTextAppearance(getContext(), this.mContent, R.style.AppWidget_Text_Link_Large);
                this.mContent.setOnClickListener(new AnonymousClass1(review));
            } else {
                ViewUtils.setTextAppearance(getContext(), this.mContent, R.style.AppWidget_Text_Content_Block);
                this.mContent.setText(review.content);
                this.mContent.setClickable(false);
                ThemedAttrs.ofView(this.mContent).append(R.attr.textColorArray, Integer.valueOf(R.array.content_text_color));
                ThemedUtils.updateView(this.mContent);
            }
            this.mAuthor.setText(review.getAuthorName());
            this.mCreateDate.setText(DateUtils.formatDate(review.createTime));
            this.mCommentInfo.setText(new RichText().appendIcon(new IconFontSpan(R.drawable.v_vote_up).ratio(1.2f)).append(String.valueOf(review.usefulCount)).append((char) Char.SPACE).append((char) Char.SPACE).appendIcon(new IconFontSpan(R.drawable.v_reply).ratio(1.2f)).append(String.valueOf(review.commentCount)));
        }
    }

    @Background
    void loadReview() {
        try {
            App.get().runOnUiThread(new AnonymousClass2((Review) this.mReviewManager.get((Object) Integer.valueOf(this.mReviewId))));
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBusUtils.register(this);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusUtils.unregister(this);
    }

    public void onEventMainThread(ReviewChangedEvent event) {
        if (event.isValidForReview(this.mReviewId)) {
            loadReview();
        }
    }
}
