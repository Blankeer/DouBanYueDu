package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.ReviewManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ReviewItemView_ extends ReviewItemView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.ReviewItemView_.1 */
    class AnonymousClass1 extends Task {
        AnonymousClass1(String x0, long x1, String x2) {
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

    public ReviewItemView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReviewItemView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public ReviewItemView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static ReviewItemView build(Context context) {
        ReviewItemView_ instance = new ReviewItemView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_review_item, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mReviewManager = ReviewManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static ReviewItemView build(Context context, AttributeSet attrs) {
        ReviewItemView_ instance = new ReviewItemView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static ReviewItemView build(Context context, AttributeSet attrs, int defStyle) {
        ReviewItemView_ instance = new ReviewItemView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mRate = (RatingBar) hasViews.findViewById(R.id.rate);
        this.mContent = (TextView) hasViews.findViewById(R.id.content);
        this.mAuthor = (TextView) hasViews.findViewById(R.id.author);
        this.mCreateDate = (TextView) hasViews.findViewById(R.id.create_date);
        this.mCommentInfo = (TextView) hasViews.findViewById(R.id.comment_info);
        this.mRootView = (ViewGroup) hasViews.findViewById(R.id.root);
    }

    void loadReview() {
        BackgroundExecutor.execute(new AnonymousClass1(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
