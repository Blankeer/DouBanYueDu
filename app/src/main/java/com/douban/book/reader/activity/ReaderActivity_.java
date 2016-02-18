package com.douban.book.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import com.douban.book.reader.R;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.manager.ShelfManager_;
import com.douban.book.reader.manager.VersionManager_;
import com.douban.book.reader.manager.WorksManager_;
import com.douban.book.reader.view.ReadViewPager;
import com.douban.book.reader.view.ReaderGuideView;
import com.douban.book.reader.view.ReaderLoadingView;
import com.douban.book.reader.view.panel.ReaderPanelView;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ReaderActivity_ extends ReaderActivity implements HasViews, OnViewChangedListener {
    public static final String CHAPTER_TO_SHOW_EXTRA = "chapterToShow";
    public static final String M_BOOK_ID_EXTRA = "mBookId";
    public static final String POSITION_TO_SHOW_EXTRA = "positionToShow";
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.activity.ReaderActivity_.6 */
    class AnonymousClass6 extends Task {
        AnonymousClass6(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadWorksMeta();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.activity.ReaderActivity_.7 */
    class AnonymousClass7 extends Task {
        AnonymousClass7(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.onWorksDataReady();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.activity.ReaderActivity_.8 */
    class AnonymousClass8 extends Task {
        AnonymousClass8(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.onWorksOpened();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class IntentBuilder_ extends ActivityIntentBuilder<IntentBuilder_> {
        private Fragment fragmentSupport_;
        private android.app.Fragment fragment_;

        public IntentBuilder_(Context context) {
            super(context, ReaderActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), ReaderActivity_.class);
            this.fragment_ = fragment;
        }

        public IntentBuilder_(Fragment fragment) {
            super(fragment.getActivity(), ReaderActivity_.class);
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

        public IntentBuilder_ mBookId(int mBookId) {
            return (IntentBuilder_) super.extra(ReaderActivity_.M_BOOK_ID_EXTRA, mBookId);
        }

        public IntentBuilder_ positionToShow(Position positionToShow) {
            return (IntentBuilder_) super.extra(ReaderActivity_.POSITION_TO_SHOW_EXTRA, (Parcelable) positionToShow);
        }

        public IntentBuilder_ chapterToShow(int chapterToShow) {
            return (IntentBuilder_) super.extra(ReaderActivity_.CHAPTER_TO_SHOW_EXTRA, chapterToShow);
        }
    }

    public ReaderActivity_() {
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
    }

    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView((int) R.layout.act_reader);
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        this.mWorksManager = WorksManager_.getInstance_(this);
        this.mShelfManager = ShelfManager_.getInstance_(this);
        this.mVersionManager = VersionManager_.getInstance_(this);
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
        this.mPager = (ReadViewPager) hasViews.findViewById(R.id.pager);
        this.mLoadingView = (ReaderLoadingView) hasViews.findViewById(R.id.loading_view);
        this.mGuideView = (ReaderGuideView) hasViews.findViewById(R.id.reader_guide);
        this.mPanelView = (ReaderPanelView) hasViews.findViewById(R.id.reader_panel);
        if (this.mGuideView != null) {
            this.mGuideView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ReaderActivity_.this.onGuideClicked();
                }
            });
        }
        if (this.mLoadingView != null) {
            this.mLoadingView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ReaderActivity_.this.onLoadingViewClicked();
                }
            });
        }
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader, menu);
        this.mMenuItemPurchase = menu.findItem(R.id.action_purchase);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId_ = item.getItemId();
        if (itemId_ == R.id.action_purchase) {
            onMenuPurchaseClicked();
            return true;
        } else if (itemId_ == R.id.action_add_shortcut) {
            onMenuAddShortcutClicked();
            return true;
        } else if (itemId_ == R.id.action_share) {
            onMenuShareClicked();
            return true;
        } else if (itemId_ != R.id.action_profile) {
            return super.onOptionsItemSelected(item);
        } else {
            onMenuProfileItemClicked();
            return true;
        }
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null) {
            if (extras_.containsKey(M_BOOK_ID_EXTRA)) {
                this.mBookId = extras_.getInt(M_BOOK_ID_EXTRA);
            }
            if (extras_.containsKey(POSITION_TO_SHOW_EXTRA)) {
                this.positionToShow = (Position) extras_.getParcelable(POSITION_TO_SHOW_EXTRA);
            }
            if (extras_.containsKey(CHAPTER_TO_SHOW_EXTRA)) {
                this.chapterToShow = extras_.getInt(CHAPTER_TO_SHOW_EXTRA);
            }
        }
    }

    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    void enterFullScreenDelayed() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.enterFullScreenDelayed();
            }
        }, 0);
    }

    void initPagerAdapter() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.initPagerAdapter();
            }
        }, 0);
    }

    void refreshShowPage() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.refreshShowPage();
            }
        }, 0);
    }

    void loadWorksMeta() {
        BackgroundExecutor.execute(new AnonymousClass6(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void onWorksDataReady() {
        BackgroundExecutor.execute(new AnonymousClass7(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void onWorksOpened() {
        BackgroundExecutor.execute(new AnonymousClass8(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
