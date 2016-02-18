package com.douban.book.reader.view;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.ReviewChangedEvent;
import com.douban.book.reader.fragment.WorksProfileFragment_;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.span.LinkTextSpan;
import com.douban.book.reader.util.DateUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.SpanUtils;
import com.douban.book.reader.view.ParagraphView.Indent;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903182)
public class ReviewSummaryView extends RelativeLayout {
    private static final String TAG;
    @ViewById(2131558931)
    TextView mBtnWorksProfile;
    @ViewById(2131558770)
    ParagraphView mContent;
    @ViewById(2131558933)
    TextView mCreatedDate;
    @ViewById(2131558930)
    TextView mCreator;
    @ViewById(2131558767)
    UserAvatarView mCreatorAvatar;
    @ViewById(2131558879)
    ProgressBar mProgressBar;
    @ViewById(2131558927)
    RatingBar mRateBar;
    private int mReviewId;
    @Bean
    ReviewManager mReviewManager;
    private int mWorksId;
    @Bean
    WorksManager mWorksManager;

    static {
        TAG = ReviewSummaryView.class.getSimpleName();
    }

    public ReviewSummaryView(Context context) {
        super(context);
    }

    public ReviewSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReviewSummaryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ReviewSummaryView reviewId(int reviewId) {
        this.mReviewId = reviewId;
        loadReview();
        return this;
    }

    public ReviewSummaryView worksId(int worksId) {
        this.mWorksId = worksId;
        loadWorks();
        return this;
    }

    @UiThread
    public void setProgressBarVisible(boolean visible) {
        this.mProgressBar.setVisibility(visible ? 0 : 8);
    }

    @UiThread
    public void updateWorksViews(Works works) {
        this.mBtnWorksProfile.setText(getWorksTitle(works));
        this.mBtnWorksProfile.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                WorksProfileFragment_.builder().worksId(ReviewSummaryView.this.mWorksId).build().showAsActivity(ReviewSummaryView.this);
            }
        });
    }

    @UiThread
    public void updateViews(Review review) {
        this.mCreatorAvatar.displayUserAvatar(review.author);
        this.mCreator.setText(review.getAuthorName());
        this.mCreatedDate.setText(DateUtils.formatDate(review.createTime));
        this.mRateBar.setRating((float) review.rating);
        this.mContent.setParagraphText(review.content);
        this.mContent.setFirstLineIndent(Indent.ALL);
    }

    @Background
    void loadWorks() {
        try {
            setProgressBarVisible(true);
            updateWorksViews((Works) this.mWorksManager.get((Object) Integer.valueOf(this.mWorksId)));
        } catch (Exception e) {
            Logger.e(TAG, e);
        } finally {
            setProgressBarVisible(false);
        }
    }

    @Background
    void loadReview() {
        try {
            setProgressBarVisible(true);
            updateViews((Review) this.mReviewManager.get((Object) Integer.valueOf(this.mReviewId)));
        } catch (Exception e) {
            Logger.e(TAG, e);
        } finally {
            setProgressBarVisible(false);
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

    public void setSimpleMode() {
        this.mCreatorAvatar.setVisibility(8);
        this.mContent.setVisibleLineCount(3);
    }

    private CharSequence getWorksTitle(Works works) {
        return new SpannableStringBuilder(Res.getString(R.string.text_review)).append(" ").append(SpanUtils.applySpan(Res.getString(R.string.msg_link_to_works_profile, works.title), new LinkTextSpan()));
    }
}
