package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.manager.ReviewManager_;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ReviewDetailView_ extends ReviewDetailView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.ReviewDetailView_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Review val$review;

        AnonymousClass1(Review review) {
            this.val$review = review;
        }

        public void run() {
            super.refreshViews(this.val$review);
        }
    }

    /* renamed from: com.douban.book.reader.view.ReviewDetailView_.2 */
    class AnonymousClass2 extends Task {
        AnonymousClass2(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadReview();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public ReviewDetailView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReviewDetailView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReviewDetailView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ReviewDetailView build(Context context) {
        ReviewDetailView_ instance = new ReviewDetailView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_review_detail, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mReviewManager = ReviewManager_.getInstance_(getContext());
        this.mWorksManager = WorksManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static ReviewDetailView build(Context context, AttributeSet attrs) {
        ReviewDetailView_ instance = new ReviewDetailView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ReviewDetailView build(Context context, AttributeSet attrs, int defStyle) {
        ReviewDetailView_ instance = new ReviewDetailView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mReviewSummary = (ReviewSummaryView) hasViews.findViewById(R.id.review_summary);
        this.mDividerAboveVote = hasViews.findViewById(R.id.divider_above_vote);
        this.mVoteView = (VoteView) hasViews.findViewById(R.id.vote_view);
        init();
    }

    public void refreshViews(Review review) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(review), 0);
    }

    void loadReview() {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
