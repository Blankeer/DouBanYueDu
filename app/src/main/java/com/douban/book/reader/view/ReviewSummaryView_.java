package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.ReviewManager_;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ReviewSummaryView_ extends ReviewSummaryView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.ReviewSummaryView_.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ boolean val$visible;

        AnonymousClass1(boolean z) {
            this.val$visible = z;
        }

        public void run() {
            super.setProgressBarVisible(this.val$visible);
        }
    }

    /* renamed from: com.douban.book.reader.view.ReviewSummaryView_.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Works val$works;

        AnonymousClass2(Works works) {
            this.val$works = works;
        }

        public void run() {
            super.updateWorksViews(this.val$works);
        }
    }

    /* renamed from: com.douban.book.reader.view.ReviewSummaryView_.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ Review val$review;

        AnonymousClass3(Review review) {
            this.val$review = review;
        }

        public void run() {
            super.updateViews(this.val$review);
        }
    }

    /* renamed from: com.douban.book.reader.view.ReviewSummaryView_.4 */
    class AnonymousClass4 extends Task {
        AnonymousClass4(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadWorks();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.view.ReviewSummaryView_.5 */
    class AnonymousClass5 extends Task {
        AnonymousClass5(String x0, long x1, String x2) {
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

    public ReviewSummaryView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReviewSummaryView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReviewSummaryView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ReviewSummaryView build(Context context) {
        ReviewSummaryView_ instance = new ReviewSummaryView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_review_summary, this);
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

    public static ReviewSummaryView build(Context context, AttributeSet attrs) {
        ReviewSummaryView_ instance = new ReviewSummaryView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ReviewSummaryView build(Context context, AttributeSet attrs, int defStyle) {
        ReviewSummaryView_ instance = new ReviewSummaryView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mCreatorAvatar = (UserAvatarView) hasViews.findViewById(R.id.creator_avatar);
        this.mCreator = (TextView) hasViews.findViewById(R.id.creator);
        this.mCreatedDate = (TextView) hasViews.findViewById(R.id.created_date);
        this.mBtnWorksProfile = (TextView) hasViews.findViewById(R.id.btn_works_profile);
        this.mRateBar = (RatingBar) hasViews.findViewById(R.id.rate);
        this.mContent = (ParagraphView) hasViews.findViewById(R.id.content);
        this.mProgressBar = (ProgressBar) hasViews.findViewById(R.id.progress);
    }

    public void setProgressBarVisible(boolean visible) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass1(visible), 0);
    }

    public void updateWorksViews(Works works) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass2(works), 0);
    }

    public void updateViews(Review review) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass3(review), 0);
    }

    void loadWorks() {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void loadReview() {
        BackgroundExecutor.execute(new AnonymousClass5(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
