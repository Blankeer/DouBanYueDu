package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.WorksAgent;
import com.douban.book.reader.lib.view.BadgeTextView;
import com.douban.book.reader.manager.FeedManager_;
import com.douban.book.reader.manager.NotificationManager_;
import com.douban.book.reader.manager.UserManager_;
import com.douban.book.reader.manager.WorksAgentManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class UserSummaryView_ extends UserSummaryView implements HasViews, OnViewChangedListener {
    private boolean alreadyInflated_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.view.UserSummaryView_.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ WorksAgent val$worksAgent;

        AnonymousClass10(WorksAgent worksAgent) {
            this.val$worksAgent = worksAgent;
        }

        public void run() {
            super.refreshUserAgentView(this.val$worksAgent);
        }
    }

    /* renamed from: com.douban.book.reader.view.UserSummaryView_.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ boolean val$hasUnread;

        AnonymousClass11(boolean z) {
            this.val$hasUnread = z;
        }

        public void run() {
            super.updateNotificationView(this.val$hasUnread);
        }
    }

    /* renamed from: com.douban.book.reader.view.UserSummaryView_.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ int val$num;
        final /* synthetic */ TextView val$textView;

        AnonymousClass12(TextView textView, int i) {
            this.val$textView = textView;
            this.val$num = i;
        }

        public void run() {
            super.updateUnReadView(this.val$textView, this.val$num);
        }
    }

    /* renamed from: com.douban.book.reader.view.UserSummaryView_.13 */
    class AnonymousClass13 extends Task {
        AnonymousClass13(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.refreshUserAgent();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.view.UserSummaryView_.14 */
    class AnonymousClass14 extends Task {
        AnonymousClass14(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.refreshNotificationStatus();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.view.UserSummaryView_.15 */
    class AnonymousClass15 extends Task {
        AnonymousClass15(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.reloadFeedsNumber();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public UserSummaryView_(Context context) {
        super(context);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public UserSummaryView_(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public UserSummaryView_(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.alreadyInflated_ = false;
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
        init_();
    }

    public static UserSummaryView build(Context context) {
        UserSummaryView_ instance = new UserSummaryView_(context);
        instance.onFinishInflate();
        return instance;
    }

    public void onFinishInflate() {
        if (!this.alreadyInflated_) {
            this.alreadyInflated_ = true;
            inflate(getContext(), R.layout.view_user_summary, this);
            this.onViewChangedNotifier_.notifyViewChanged(this);
        }
        super.onFinishInflate();
    }

    private void init_() {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mUserManager = UserManager_.getInstance_(getContext());
        this.mFeedManager = FeedManager_.getInstance_(getContext());
        this.mNotificationManager = NotificationManager_.getInstance_(getContext());
        this.mWorksAgentManager = WorksAgentManager_.getInstance_(getContext());
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public static UserSummaryView build(Context context, AttributeSet attrs) {
        UserSummaryView_ instance = new UserSummaryView_(context, attrs);
        instance.onFinishInflate();
        return instance;
    }

    public static UserSummaryView build(Context context, AttributeSet attrs, int defStyle) {
        UserSummaryView_ instance = new UserSummaryView_(context, attrs, defStyle);
        instance.onFinishInflate();
        return instance;
    }

    public void onViewChanged(HasViews hasViews) {
        this.mUserAvatar = (UserAvatarView) hasViews.findViewById(R.id.user_avatar);
        this.mUserName = (TextView) hasViews.findViewById(R.id.user_name);
        this.mBtnLogin = (TextView) hasViews.findViewById(R.id.btn_login);
        this.mFeedsNumber = (TextView) hasViews.findViewById(R.id.feeds_number);
        this.mOwnNumber = (TextView) hasViews.findViewById(R.id.own_number);
        this.mComposedNumber = (TextView) hasViews.findViewById(R.id.composed_number);
        this.mAuthorBlock = hasViews.findViewById(R.id.author_block);
        this.mBtnNotification = (BadgeTextView) hasViews.findViewById(R.id.btn_notification);
        this.mBtnGift = (TextView) hasViews.findViewById(R.id.btn_gift);
        this.mBtnAccount = (TextView) hasViews.findViewById(R.id.btn_account);
        View view_btn_feeds = hasViews.findViewById(R.id.btn_feeds);
        View view_btn_composed = hasViews.findViewById(R.id.btn_composed);
        View view_btn_own = hasViews.findViewById(R.id.btn_own);
        if (this.mUserAvatar != null) {
            this.mUserAvatar.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserSummaryView_.this.onUserNameClicked();
                }
            });
        }
        if (this.mUserName != null) {
            this.mUserName.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserSummaryView_.this.onUserNameClicked();
                }
            });
        }
        if (this.mBtnLogin != null) {
            this.mBtnLogin.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserSummaryView_.this.onUserNameClicked();
                }
            });
        }
        if (view_btn_feeds != null) {
            view_btn_feeds.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserSummaryView_.this.onBtnFeedsClicked();
                }
            });
        }
        if (this.mBtnNotification != null) {
            this.mBtnNotification.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserSummaryView_.this.onBtnNotificationClicked();
                }
            });
        }
        if (this.mBtnGift != null) {
            this.mBtnGift.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserSummaryView_.this.onBtnGiftClicked();
                }
            });
        }
        if (view_btn_composed != null) {
            view_btn_composed.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserSummaryView_.this.onBtnComposedClicked();
                }
            });
        }
        if (view_btn_own != null) {
            view_btn_own.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserSummaryView_.this.onBtnOwn();
                }
            });
        }
        if (this.mBtnAccount != null) {
            this.mBtnAccount.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    UserSummaryView_.this.onBtnAmountLeftClicked();
                }
            });
        }
        init();
    }

    void refreshUserAgentView(WorksAgent worksAgent) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass10(worksAgent), 0);
    }

    void updateNotificationView(boolean hasUnread) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass11(hasUnread), 0);
    }

    void updateUnReadView(TextView textView, int num) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass12(textView, num), 0);
    }

    void refreshUserAgent() {
        BackgroundExecutor.execute(new AnonymousClass13(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void refreshNotificationStatus() {
        BackgroundExecutor.execute(new AnonymousClass14(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void reloadFeedsNumber() {
        BackgroundExecutor.execute(new AnonymousClass15(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
