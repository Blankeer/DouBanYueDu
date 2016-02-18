package com.douban.book.reader.view.page;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.WorksManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class LastPageView_ extends LastPageView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.page.LastPageView_.5 */
    class AnonymousClass5 implements Runnable {
        final /* synthetic */ boolean val$isSubscribing;

        AnonymousClass5(boolean z) {
            this.val$isSubscribing = z;
        }

        public void run() {
            super.setIsSubscribing(this.val$isSubscribing);
        }
    }

    /* renamed from: com.douban.book.reader.view.page.LastPageView_.7 */
    class AnonymousClass7 extends Task {
        AnonymousClass7(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.onBtnSubscribeClicked();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.view.page.LastPageView_.8 */
    class AnonymousClass8 extends Task {
        final /* synthetic */ int val$worksId;

        AnonymousClass8(String x0, long x1, String x2, int i) {
            this.val$worksId = i;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadData(this.val$worksId);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public LastPageView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static LastPageView build(Context context) {
        LastPageView_ instance = new LastPageView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_page_last, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mWorksManager = WorksManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public void onViewChanged(HasViews hasViews) {
        this.mTitle = (TextView) hasViews.findViewById(com.wnafee.vector.R.id.title);
        this.mColumnPlanInfo = (TextView) hasViews.findViewById(R.id.column_plan_info);
        this.mTextSubscriptionInfo = (TextView) hasViews.findViewById(R.id.text_subscription_info);
        this.mBtnSubscribe = (Button) hasViews.findViewById(R.id.btn_subscribe);
        this.mSubscribeProgressBar = (ProgressBar) hasViews.findViewById(R.id.subscribe_progress_bar);
        this.mTextPurchaseTip = (TextView) hasViews.findViewById(R.id.text_purchase_tip);
        this.mBtnPurchase = (Button) hasViews.findViewById(R.id.btn_purchase);
        this.mRatingLayout = hasViews.findViewById(R.id.rating_layout);
        this.mTextRatingTip = (TextView) hasViews.findViewById(R.id.text_rating_tip);
        this.mRatingBar = (RatingBar) hasViews.findViewById(R.id.rating_bar);
        this.mBtnDownload = (Button) hasViews.findViewById(R.id.btn_download);
        this.mDownloadInfo = (TextView) hasViews.findViewById(R.id.text_download_info);
        if (this.mBtnSubscribe != null) {
            this.mBtnSubscribe.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LastPageView_.this.onBtnSubscribeClicked();
                }
            });
        }
        if (this.mBtnPurchase != null) {
            this.mBtnPurchase.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LastPageView_.this.onBtnPurchaseClicked();
                }
            });
        }
        if (this.mRatingLayout != null) {
            this.mRatingLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LastPageView_.this.onRatingLayoutClicked();
                }
            });
        }
        if (this.mBtnDownload != null) {
            this.mBtnDownload.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LastPageView_.this.onDownloadClicked();
                }
            });
        }
        init();
    }

    void setIsSubscribing(boolean isSubscribing) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass5(isSubscribing), 0);
    }

    void updateViews() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateViews();
            }
        }, 0);
    }

    void onBtnSubscribeClicked() {
        BackgroundExecutor.execute(new AnonymousClass7(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void loadData(int worksId) {
        BackgroundExecutor.execute(new AnonymousClass8(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, worksId));
    }
}
