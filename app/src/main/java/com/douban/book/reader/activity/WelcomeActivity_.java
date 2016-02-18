package com.douban.book.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.ReviewManager_;
import com.douban.book.reader.manager.UserManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WelcomeActivity_ extends WelcomeActivity implements HasViews, OnViewChangedListener {
    public static final String FORWARD_INTENT_EXTRA = "forwardIntent";
    public static final String LEGACY_REVIEW_ID_EXTRA = "legacyReviewId";
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.activity.WelcomeActivity_.6 */
    class AnonymousClass6 implements Runnable {
        final /* synthetic */ Throwable val$e;

        AnonymousClass6(Throwable th) {
            this.val$e = th;
        }

        public void run() {
            super.onAnonymousLoginError(this.val$e);
        }
    }

    /* renamed from: com.douban.book.reader.activity.WelcomeActivity_.7 */
    class AnonymousClass7 extends Task {
        AnonymousClass7(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.anonymousLogin();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.activity.WelcomeActivity_.8 */
    class AnonymousClass8 extends Task {
        final /* synthetic */ int val$legacyReviewId;

        AnonymousClass8(String x0, long x1, String x2, int i) {
            this.val$legacyReviewId = i;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.openReviewDetailPage(this.val$legacyReviewId);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class IntentBuilder_ extends ActivityIntentBuilder<IntentBuilder_> {
        private Fragment fragmentSupport_;
        private android.app.Fragment fragment_;

        public IntentBuilder_(Context context) {
            super(context, WelcomeActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), WelcomeActivity_.class);
            this.fragment_ = fragment;
        }

        public IntentBuilder_(Fragment fragment) {
            super(fragment.getActivity(), WelcomeActivity_.class);
            this.fragmentSupport_ = fragment;
        }

        public PostActivityStarter startForResult(int requestCode) {
            if (this.fragmentSupport_ != null) {
                this.fragmentSupport_.startActivityForResult(this.intent, requestCode);
            } else if (this.fragment_ != null) {
                if (VERSION.SDK_INT >= 16) {
                    this.fragment_.startActivityForResult(this.intent, requestCode, this.lastOptions);
                } else {
                    this.fragment_.startActivityForResult(this.intent, requestCode);
                }
            } else if (this.context instanceof Activity) {
                ActivityCompat.startActivityForResult(this.context, this.intent, requestCode, this.lastOptions);
            } else if (VERSION.SDK_INT >= 16) {
                this.context.startActivity(this.intent, this.lastOptions);
            } else {
                this.context.startActivity(this.intent);
            }
            return new PostActivityStarter(this.context);
        }

        public IntentBuilder_ forwardIntent(Intent forwardIntent) {
            return (IntentBuilder_) super.extra(WelcomeActivity_.FORWARD_INTENT_EXTRA, (Parcelable) forwardIntent);
        }

        public IntentBuilder_ legacyReviewId(int legacyReviewId) {
            return (IntentBuilder_) super.extra(WelcomeActivity_.LEGACY_REVIEW_ID_EXTRA, legacyReviewId);
        }
    }

    public WelcomeActivity_() {
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
    }

    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView((int) R.layout.act_welcome);
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mUserManager = UserManager_.getInstance_(this);
        this.mReviewManager = ReviewManager_.getInstance_(this);
        injectExtras_();
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public void setContentView(View view) {
        super.setContentView(view);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static IntentBuilder_ intent(Context context) {
        return new IntentBuilder_(context);
    }

    public static IntentBuilder_ intent(android.app.Fragment fragment) {
        return new IntentBuilder_(fragment);
    }

    public static IntentBuilder_ intent(Fragment supportFragment) {
        return new IntentBuilder_(supportFragment);
    }

    public void onViewChanged(HasViews hasViews) {
        this.mLayoutLoading = hasViews.findViewById(R.id.layout_loading);
        this.mLayoutRetry = hasViews.findViewById(R.id.layout_retry);
        this.mMessage = (TextView) hasViews.findViewById(R.id.message);
        this.mBtnRetry = (Button) hasViews.findViewById(R.id.btn_retry);
        this.mMarketLogo = (ImageView) hasViews.findViewById(R.id.special_logo);
        if (this.mBtnRetry != null) {
            this.mBtnRetry.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    WelcomeActivity_.this.onRetryButtonClicked();
                }
            });
        }
        init();
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey(FORWARD_INTENT_EXTRA)) {
                this.forwardIntent = (Intent) extras_.getParcelable(FORWARD_INTENT_EXTRA);
            }
            if (extras_.containsKey(LEGACY_REVIEW_ID_EXTRA)) {
                this.legacyReviewId = extras_.getInt(LEGACY_REVIEW_ID_EXTRA);
            }
        }
    }

    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    void redirectToNextPage() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.redirectToNextPage();
            }
        }, 0);
    }

    void onCheckLoggedInFinish() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.onCheckLoggedInFinish();
            }
        }, 0);
    }

    void startLoading() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.startLoading();
            }
        }, 0);
    }

    void stopLoading() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.stopLoading();
            }
        }, 0);
    }

    void onAnonymousLoginError(Throwable e) {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new AnonymousClass6(e), 0);
    }

    void anonymousLogin() {
        BackgroundExecutor.execute(new AnonymousClass7(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void openReviewDetailPage(int legacyReviewId) {
        BackgroundExecutor.execute(new AnonymousClass8(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, legacyReviewId));
    }
}
