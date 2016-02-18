package com.douban.book.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import com.alipay.security.mobile.module.deviceinfo.constant.a;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BookPagerAdapter;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.content.pack.WorksData.Status;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.entity.Bookmark.Column;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Progress;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.ActiveNoteChangedEvent;
import com.douban.book.reader.event.AppContentClearEvent;
import com.douban.book.reader.event.DownloadStatusChangedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.PageFlipEvent;
import com.douban.book.reader.event.PagingEndedEvent;
import com.douban.book.reader.event.PagingFailedEvent;
import com.douban.book.reader.event.PagingProgressUpdatedEvent;
import com.douban.book.reader.event.PagingStartedEvent;
import com.douban.book.reader.event.ReadingProgressUpdatedEvent;
import com.douban.book.reader.event.RemoteProgressLoadedEvent;
import com.douban.book.reader.event.WorksUpdatedEvent;
import com.douban.book.reader.exception.DataException;
import com.douban.book.reader.fragment.AlertDialogFragment.Builder;
import com.douban.book.reader.fragment.PurchaseFragment_;
import com.douban.book.reader.fragment.WorksProfileFragment_;
import com.douban.book.reader.fragment.share.FeedbackEditFragment_;
import com.douban.book.reader.fragment.share.ShareChapterEditFragment_;
import com.douban.book.reader.fragment.share.ShareWorksEditFragment_;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.manager.ProgressManager;
import com.douban.book.reader.manager.ShelfManager;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.VersionManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.task.PagingTaskManager;
import com.douban.book.reader.task.ParagraphPreloadThread;
import com.douban.book.reader.task.SyncManager;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.AnalysisUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.JsonUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PackageUtils;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.ReaderUriUtils;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ToastBuilder;
import com.douban.book.reader.util.ToastBuilder.OnCloseListener;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ReadViewPager;
import com.douban.book.reader.view.ReadViewPager.OnEdgePageArrivedListener;
import com.douban.book.reader.view.ReadViewPager.OnViewPagerClickListener;
import com.douban.book.reader.view.ReaderGuideView;
import com.douban.book.reader.view.ReaderLoadingView;
import com.douban.book.reader.view.panel.ReaderPanelView;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import u.aly.dx;

@EActivity(2130903068)
@OptionsMenu({2131623942})
public class ReaderActivity extends BaseBrightnessOverridingActivity implements OnViewPagerClickListener, OnEdgePageArrivedListener, OnPageChangeListener {
    private static final String KEY_CURRENT_PAGE = "current_page";
    private static final String PRELOAD_PARAGRAPH_THREAD_NAME = "Preload paragraph";
    private static final int SAVE_READING_PROGRESS_INTERVAL = 300000;
    @Extra
    int chapterToShow;
    private Book mBook;
    @Extra
    int mBookId;
    private BookPagerAdapter mBookPagerAdapter;
    private boolean mClosePanelsAfterPageSet;
    private boolean mDestroyed;
    @ViewById(2131558513)
    ReaderGuideView mGuideView;
    private boolean mJumpDialogShown;
    @ViewById(2131558512)
    ReaderLoadingView mLoadingView;
    private Manifest mManifest;
    @OptionsMenuItem({2131558986})
    MenuItem mMenuItemPurchase;
    private boolean mPageSet;
    @ViewById(2131558511)
    ReadViewPager mPager;
    @ViewById(2131558514)
    ReaderPanelView mPanelView;
    private ParagraphPreloadThread mParagraphPreloadThread;
    private ProgressManager mProgressManager;
    private Timer mSaveProgressTimer;
    @Bean
    ShelfManager mShelfManager;
    @Bean
    VersionManager mVersionManager;
    private Works mWorks;
    @Bean
    WorksManager mWorksManager;
    @Extra
    Position positionToShow;

    /* renamed from: com.douban.book.reader.activity.ReaderActivity.5 */
    class AnonymousClass5 implements OnClickListener {
        final /* synthetic */ int val$newPage;

        AnonymousClass5(int i) {
            this.val$newPage = i;
        }

        public void onClick(View v) {
            ReaderActivity.this.mProgressManager.pushProgress(this.val$newPage);
            Analysis.sendEventWithExtra(AnalysisUtils.EVENT_SYNC_PROGRESS, "confirm_dialog", "confirm");
        }
    }

    public ReaderActivity() {
        this.positionToShow = null;
        this.chapterToShow = 0;
        this.mPageSet = false;
        this.mDestroyed = false;
        this.mJumpDialogShown = false;
        this.mClosePanelsAfterPageSet = true;
        this.mSaveProgressTimer = null;
        this.mParagraphPreloadThread = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutInFullScreen();
        enterFullScreenDelayed();
    }

    @UiThread
    void enterFullScreenDelayed() {
        enterFullScreenMode();
        loadWorksMeta();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (StringUtils.equals(intent.getAction(), (CharSequence) "android.intent.action.VIEW")) {
            Uri uri = intent.getData();
            if (uri != null) {
                this.mBookId = ReaderUriUtils.getWorksId(uri);
            }
        }
        this.mPanelView.setWorksId(this.mBookId);
        if (this.mBook == null || this.mBook.getBookId() != this.mBookId) {
            finish();
            startActivity(intent);
            return;
        }
        refreshShowPage();
    }

    @AfterViews
    void init() {
        this.mDestroyed = false;
        Intent intent = getIntent();
        if (StringUtils.equals(intent.getAction(), (CharSequence) "android.intent.action.VIEW")) {
            Uri uri = intent.getData();
            if (uri != null) {
                this.mBookId = ReaderUriUtils.getWorksId(uri);
            }
        }
        this.mPanelView.setWorksId(this.mBookId);
        if (UserManager.getInstance().hasAccessToken()) {
            if (Pref.ofApp().getBoolean(Key.SETTING_PREVENET_READING_SCREENSAVE, true)) {
                getWindow().addFlags(TransportMediator.FLAG_KEY_MEDIA_NEXT);
            }
            this.mProgressManager = ProgressManager.ofWorks(this.mBookId);
            this.mPager.setOnPageChangeListener(this);
            this.mPager.setOnViewPagerClickListener(this);
            this.mPager.setOnEdgePageArrivedListener(this);
            return;
        }
        WelcomeActivity_.intent((Context) this).forwardIntent(intent).start();
        finish();
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mPager.setCurrentItem(savedInstanceState.getInt(KEY_CURRENT_PAGE, -1));
            this.mPanelView.hideAllPanels();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE, this.mPager.getCurrentItem());
    }

    public void onPause() {
        super.onPause();
        if (this.mBook != null && WorksData.get(this.mBookId).getStatus() != Status.EMPTY) {
            stopSaveProgressTimer();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.mBook != null && this.mWorks != null && this.mManifest != null) {
            if (this.mWorks.isGallery()) {
                Theme.setOverrideColorTheme(this.mManifest.getGalleryColorTheme());
            } else {
                Theme.clearOverrideColorTheme();
            }
            startSaveProgressTimer();
        }
    }

    public void onStart() {
        super.onStart();
        startPreloadThread();
    }

    protected void onStop() {
        super.onStop();
        stopPreloadThread();
        ProgressManager.ofWorks(this.mBookId).updateRemoteProgress();
    }

    public void onDestroy() {
        super.onDestroy();
        this.mDestroyed = true;
        if (isFinishing() && this.mBook != null) {
            if (this.mWorks != null && this.mWorks.isGallery()) {
                Theme.clearOverrideColorTheme();
            }
            TaskController.run(new Runnable() {
                public void run() {
                    ReaderActivity.this.mBook.closeBook();
                }
            });
        }
        EventBusUtils.unregister(this);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !this.mPanelView.isAnyPanelButRightDrawerVisible()) {
            enterFullScreenMode();
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = this.mMenuItemPurchase;
        boolean z = (this.mWorks == null || this.mWorks.hasOwned || !this.mWorks.isSalable) ? false : true;
        menuItem.setVisible(z);
        return super.onPrepareOptionsMenu(menu);
    }

    public void onBackPressed() {
        if (this.mPanelView.isSettingViewVisible() || this.mPanelView.isRightDrawerVisible()) {
            this.mPanelView.hideAllPanels();
        } else {
            super.onBackPressed();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            switch (requestCode) {
                case dx.b /*1*/:
                    finish();
                default:
            }
        }
    }

    @Background
    void loadWorksMeta() {
        try {
            showLoadingDialog();
            Logger.d(this.TAG, "show LoadingView from loadWorksData()", new Object[0]);
            this.mWorks = this.mWorksManager.getWorks(this.mBookId);
            setTitle(this.mWorks.title);
            this.mManifest = Manifest.load(this.mBookId);
            refreshActionBar();
            if (this.mWorks.isGallery()) {
                Theme.setOverrideColorTheme(this.mManifest.getGalleryColorTheme());
            } else {
                Theme.clearOverrideColorTheme();
            }
            if (this.mParagraphPreloadThread != null) {
                this.mParagraphPreloadThread.setCacheLargeImage(this.mWorks.isGallery());
            }
            if (WorksData.get(this.mBookId).getStatus() == Status.READY) {
                onWorksDataReady();
            } else {
                DownloadManager.scheduleDownload(ReaderUri.works(this.mBookId));
            }
            SyncManager.sync(this.mBookId);
        } catch (Throwable e) {
            Logger.ec(this.TAG, e);
            dismissLoadingDialog();
            Logger.d(this.TAG, "dismiss from loadWorksData()", new Object[0]);
            ToastUtils.showToast(e, (int) R.string.toast_general_load_failed);
            finish();
        }
    }

    @Background
    void onWorksDataReady() {
        try {
            this.mManifest = Manifest.load(this.mBookId);
            this.mBook = Book.get(this.mBookId);
            this.mBook.initChapters();
            this.mBook.openBook();
            initPagerAdapter();
            PagingTaskManager.foregroundPaging(this.mBookId, PageMetrics.getFromActivity(this));
            checkDataCompatibility();
            onWorksOpened();
            getWindow().getDecorView().addOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (!(left == oldLeft && top == oldTop && right == oldRight && bottom == oldBottom) && WorksData.get(ReaderActivity.this.mBookId).getStatus() == Status.READY) {
                        PagingTaskManager.foregroundPaging(ReaderActivity.this.mBookId, PageMetrics.getFromActivity(ReaderActivity.this));
                    }
                }
            });
        } catch (DataException e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void initPagerAdapter() {
        if (!this.mDestroyed) {
            this.mBookPagerAdapter = new BookPagerAdapter(getSupportFragmentManager(), this.mBookId);
            this.mPager.setAdapter(this.mBookPagerAdapter);
            if (!Pref.ofApp().getBoolean(Key.APP_READER_GUIDE_SHOWN, false)) {
                ViewUtils.visible(this.mGuideView);
                Pref.ofApp().set(Key.APP_READER_GUIDE_SHOWN, Boolean.valueOf(true));
            }
            refreshShowPage();
        }
    }

    @Background
    void onWorksOpened() {
        try {
            this.mShelfManager.worksOpened(this.mBookId);
            HashMap<String, String> params = new HashMap();
            params.put(Column.WORKS_ID, String.valueOf(this.mBookId));
            if (this.mWorks != null) {
                params.put("has_owed", String.valueOf(this.mWorks.hasOwned));
                if (this.mWorks.isColumnOrSerial()) {
                    params.put("has_subscribed", String.valueOf(this.mWorks.hasSubscribed));
                }
            }
            Analysis.sendEventWithExtra(AnalysisUtils.EVENT_OPEN_WORKS_IN_READER, Table.STRING_DEFAULT_VALUE, JsonUtils.toJson(params));
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        }
    }

    public void onPageScrollStateChanged(int state) {
        this.mClosePanelsAfterPageSet = true;
        if (state == 0) {
            ImageLoaderUtils.resume();
        } else if (state == 1) {
            ImageLoaderUtils.pause();
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int position) {
        int page = this.mPager.getCurrentItem();
        ProgressManager.ofWorks(this.mBookId).setLocalProgress(page);
        if (this.mPanelView.isAnyPanelButRightDrawerVisible() && this.mClosePanelsAfterPageSet) {
            this.mPanelView.hideAllPanels();
        }
        this.mClosePanelsAfterPageSet = true;
        if (this.mParagraphPreloadThread != null) {
            this.mParagraphPreloadThread.setCurrentPage(page);
        }
    }

    public int getBookId() {
        return this.mBookId;
    }

    public boolean onViewPagerClick(boolean isEdgeClick) {
        if (this.mPanelView.isAnyPanelVisible()) {
            this.mPanelView.hideAllPanels();
            return true;
        } else if (isEdgeClick) {
            return false;
        } else {
            this.mPanelView.showCommandBar();
            return true;
        }
    }

    private void toggleCommandBar() {
        if (this.mPanelView.isAnyPanelVisible()) {
            this.mPanelView.hideAllPanels();
        } else {
            this.mPanelView.showCommandBar();
        }
    }

    @OptionsItem({2131558986})
    void onMenuPurchaseClicked() {
        PurchaseFragment_.builder().uri(ReaderUri.works(this.mBookId)).build().showAsActivity((BaseActivity) this);
    }

    @OptionsItem({2131558987})
    void onMenuAddShortcutClicked() {
        new Builder().setMessage((int) R.string.msg_add_shortcut).setPositiveButton((int) R.string.dialog_button_add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    ReaderActivity.this.mShelfManager.addShortCut(ReaderActivity.this.mBookId);
                } catch (DataLoadException e) {
                    ToastUtils.showToast((int) R.string.toast_add_shortcut_failed);
                }
            }
        }).setNegativeButton((int) R.string.dialog_button_cancel, null).create().show();
    }

    @OptionsItem({2131558988})
    void onMenuShareClicked() {
        if (this.mWorks.isColumnOrSerial()) {
            Position position = ProgressManager.ofWorks(this.mBookId).getLocalProgress().getPosition();
            if (Position.isValid(position)) {
                ShareChapterEditFragment_.builder().worksId(this.mBookId).chapterId(position.packageId).build().showAsActivity(PageOpenHelper.from((Activity) this));
                return;
            }
        }
        ShareWorksEditFragment_.builder().worksId(this.mBookId).build().showAsActivity(PageOpenHelper.from((Activity) this));
    }

    @OptionsItem({2131558989})
    void onMenuProfileItemClicked() {
        WorksProfileFragment_.builder().worksId(this.mBookId).build().showAsActivity((BaseActivity) this);
    }

    public void onViewPagerEdgeArrived(boolean isLastPage) {
    }

    @Click({2131558513})
    void onGuideClicked() {
        ViewUtils.gone(this.mGuideView);
    }

    private void attemptJumpToRemoteProgress() {
        Logger.d(this.TAG, "attemptJumpToRemoteProgress called.", new Object[0]);
        if (this.mJumpDialogShown) {
            Logger.d(this.TAG, "return because dialog is already shown", new Object[0]);
            return;
        }
        Progress remoteProgress = ProgressManager.ofWorks(this.mBookId).getRemoteProgress();
        Logger.d(this.TAG, "loaded remote progress: %s", remoteProgress);
        if (remoteProgress == null || !remoteProgress.isValid()) {
            Logger.d(this.TAG, "return because the remote progress is invalid", new Object[0]);
        } else if (remoteProgress.wasCreatedByThisDevice()) {
            Logger.d(this.TAG, "return because there is a self uploaded progress", new Object[0]);
        } else {
            int newPage = this.mBook.getPageForPosition(remoteProgress.getPosition());
            if (newPage < 0) {
                Logger.d(this.TAG, "return because failed to resolve page for remote progress", new Object[0]);
            } else if (this.mBook.getPageCount() < newPage) {
                Logger.d(this.TAG, "return because page to show is not prepared", new Object[0]);
            } else if (newPage < this.mPager.getCurrentItem() - 1 || newPage > this.mPager.getCurrentItem() + 1) {
                new ToastBuilder().message(getString(R.string.dialog_message_sync_reading_progress, new Object[]{Integer.valueOf(newPage + 1)})).autoClose(false).attachTo(this).click(new AnonymousClass5(newPage)).onClose(new OnCloseListener() {
                    public void onClose() {
                        Analysis.sendEventWithExtra(AnalysisUtils.EVENT_SYNC_PROGRESS, "confirm_dialog", "cancel");
                    }
                }).show();
                this.mJumpDialogShown = true;
            } else {
                Logger.d(this.TAG, "return because current page is showing", new Object[0]);
            }
        }
    }

    private void startSaveProgressTimer() {
        if (this.mSaveProgressTimer == null) {
            this.mSaveProgressTimer = new Timer();
            this.mSaveProgressTimer.schedule(new TimerTask() {
                public void run() {
                    ProgressManager.ofWorks(ReaderActivity.this.mBookId).updateRemoteProgress();
                }
            }, a.b, a.b);
        }
    }

    private void stopSaveProgressTimer() {
        if (this.mSaveProgressTimer != null) {
            this.mSaveProgressTimer.cancel();
            this.mSaveProgressTimer = null;
        }
    }

    public void onEventMainThread(PageFlipEvent event) {
        if (event.isValidFor(this.mBookId)) {
            int direction = event.getDirection();
            if (direction == 1) {
                this.mPager.gotoNextPage(true);
            } else if (direction == -1) {
                this.mPager.gotoPreviousPage(true);
            }
        }
    }

    public void onEventMainThread(PagingStartedEvent event) {
        if (event.isValidFor(this.mBookId)) {
            Logger.d(this.TAG, "PagingStartedEvent for %s", Integer.valueOf(this.mBookId));
            this.mPageSet = false;
            showLoadingDialog();
            Logger.d(this.TAG, "show LoadingView from PagingStartedEvent", new Object[0]);
        }
    }

    public void onEventMainThread(RemoteProgressLoadedEvent event) {
        if (event.isValidFor(this.mBookId)) {
            attemptJumpToRemoteProgress();
        }
    }

    public void onEventMainThread(PagingProgressUpdatedEvent event) {
        if (event.isValidFor(this.mBookId)) {
            Logger.d(this.TAG, "PagingProgressUpdatedEvent for %s, mPageSet=%s", Integer.valueOf(this.mBookId), Boolean.valueOf(this.mPageSet));
            if (this.mPageSet) {
                notifyDataSetChanged();
                attemptJumpToRemoteProgress();
                return;
            }
            int pageToShow;
            if (this.positionToShow == null && this.chapterToShow != 0) {
                this.positionToShow = this.mBook.getPositionForChapter(this.chapterToShow);
                Logger.d(this.TAG, "PagingProgressUpdatedEvent, positionToShow=%s", this.positionToShow);
            }
            if (this.positionToShow != null) {
                pageToShow = this.mBook.getPageForPosition(this.positionToShow);
            } else {
                pageToShow = event.getProgress();
            }
            Logger.d(this.TAG, "PagingProgressUpdatedEvent, pageToShow=%d", Integer.valueOf(pageToShow));
            if (pageToShow >= 0) {
                this.mClosePanelsAfterPageSet = false;
                if (this.mBookPagerAdapter != null) {
                    this.mBookPagerAdapter.redrawAll();
                }
                notifyDataSetChanged();
                this.mPager.setCurrentItem(pageToShow, false);
                this.mPageSet = true;
                dismissLoadingDialog();
                Logger.d(this.TAG, "dismiss LoadingView from PagingProgressUpdatedEvent", new Object[0]);
                attemptJumpToRemoteProgress();
            }
        }
    }

    public void onEventMainThread(AppContentClearEvent event) {
        finish();
    }

    public void onEventMainThread(PagingEndedEvent event) {
        if (event.isValidFor(this.mBookId)) {
            Logger.d(this.TAG, "PagingEndedEvent for %s", Integer.valueOf(this.mBookId));
            if (!PagingTaskManager.isPaging(this.mBookId)) {
                dismissLoadingDialog();
                Logger.d(this.TAG, "dismiss LoadingView from PagingEndedEvent", new Object[0]);
                if (!this.mDestroyed) {
                    attemptJumpToRemoteProgress();
                    if (!this.mPageSet) {
                        if (this.mBookPagerAdapter != null) {
                            this.mBookPagerAdapter.redrawAll();
                        }
                        notifyDataSetChanged();
                    }
                }
            }
        }
    }

    public void onEventMainThread(PagingFailedEvent event) {
        if (event.isValidFor(this.mBookId)) {
            Logger.d(this.TAG, "PagingFailedEvent for %s", Integer.valueOf(this.mBookId));
            dismissLoadingDialog();
            Logger.d(this.TAG, "dismiss LoadingView from PagingFailedEvent", new Object[0]);
            Builder builder = new Builder().setMessage((int) R.string.dialog_message_paging_failed).setPositiveButton((int) R.string.dialog_button_download_again, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DownloadManager.scheduleDownload(ReaderUri.works(ReaderActivity.this.mBookId));
                }
            }).setNegativeButton((int) R.string.dialog_button_ignore, null);
            if (DebugSwitch.on(Key.APP_DEBUG_SEND_DIAGNOSTIC_REPORT)) {
                builder.setNeutralButton((int) R.string.dialog_button_feedback, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FeedbackEditFragment_.builder().isReport(true).build().showAsActivity(ReaderActivity.this);
                    }
                });
            }
            builder.create().show();
        }
    }

    public void onEventMainThread(ReadingProgressUpdatedEvent event) {
        if (event.isValidFor(this.mBookId)) {
            this.mPager.setCurrentItem(this.mBook.getPageForPosition(this.mProgressManager.getLocalProgress().getPosition()));
        }
    }

    public void onEventMainThread(WorksUpdatedEvent event) {
        if (event.isValidFor(this.mBookId)) {
            loadWorksMeta();
        }
    }

    public void onEventMainThread(DownloadStatusChangedEvent event) {
        if (!event.isValidFor(this.mBookId)) {
            return;
        }
        if (WorksData.get(this.mBookId).getStatus() == Status.READY) {
            onWorksDataReady();
            return;
        }
        showLoadingDialog();
        Logger.d(this.TAG, "show LoadingView from DownloadStatusChangedEvent", new Object[0]);
    }

    public void onEventMainThread(ActiveNoteChangedEvent event) {
        if (event.isValidFor(this.mBookId)) {
            Annotation note = AnnotationManager.ofWorks(this.mBookId).getActiveNote();
            if (note != null) {
                Range range = note.getRange();
                if (Range.isValid(range)) {
                    ProgressManager.ofWorks(this.mBookId).pushProgress(range.endPosition);
                }
            }
        }
    }

    private void startPreloadThread() {
        this.mParagraphPreloadThread = new ParagraphPreloadThread(this.mBookId);
        this.mParagraphPreloadThread.start();
        this.mParagraphPreloadThread.setName(PRELOAD_PARAGRAPH_THREAD_NAME);
        this.mParagraphPreloadThread.setPriority(4);
        if (this.mWorks != null) {
            this.mParagraphPreloadThread.setCacheLargeImage(this.mWorks.isGallery());
        }
    }

    private void stopPreloadThread() {
        if (this.mParagraphPreloadThread != null) {
            this.mParagraphPreloadThread.quit();
        }
    }

    @UiThread
    void refreshShowPage() {
        if (this.mBook != null && !PagingTaskManager.isPaging(this.mBookId) && !this.mDestroyed) {
            int pageToShow;
            if (this.positionToShow == null && this.chapterToShow != 0) {
                this.positionToShow = this.mBook.getPositionForChapter(this.chapterToShow);
            }
            if (this.positionToShow != null) {
                pageToShow = this.mBook.getPageForPosition(this.positionToShow);
            } else {
                pageToShow = this.mBook.getPageForPosition(this.mProgressManager.getLocalProgress().getPosition());
            }
            if (pageToShow >= 0) {
                if (this.mBookPagerAdapter != null) {
                    this.mBookPagerAdapter.redrawAll();
                }
                notifyDataSetChanged();
                this.mPager.setCurrentItem(pageToShow, false);
                dismissLoadingDialog();
                Logger.d(this.TAG, "dismiss LoadingView from refreshShowPage", new Object[0]);
            }
            resetStartPageMethod();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case se.emilsjolander.stickylistheaders.R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
            case HeaderMapDB.TUPLE2_COMPARATOR_STATIC /*25*/:
                if (Pref.ofApp().getBoolean(Key.SETTING_PAGETURN_WITH_VOLUME, false)) {
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case se.emilsjolander.stickylistheaders.R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
                if (Pref.ofApp().getBoolean(Key.SETTING_PAGETURN_WITH_VOLUME, false)) {
                    this.mPager.gotoPreviousPage();
                    return true;
                }
                break;
            case HeaderMapDB.TUPLE2_COMPARATOR_STATIC /*25*/:
                if (Pref.ofApp().getBoolean(Key.SETTING_PAGETURN_WITH_VOLUME, false)) {
                    this.mPager.gotoNextPage();
                    return true;
                }
                break;
            case Header.LONG_14 /*62*/:
                this.mPager.gotoNextPage();
                break;
            case Header.LONG_MF1 /*67*/:
                this.mPager.gotoPreviousPage();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Click({2131558512})
    void onLoadingViewClicked() {
        toggleCommandBar();
    }

    public void showLoadingDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                ReaderActivity.this.mLoadingView.setWorksId(ReaderActivity.this.mBookId);
                ViewUtils.visible(ReaderActivity.this.mLoadingView);
            }
        });
    }

    public void dismissLoadingDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                ViewUtils.gone(ReaderActivity.this.mLoadingView);
            }
        });
    }

    public void resetStartPageMethod() {
        this.positionToShow = null;
        this.chapterToShow = 0;
    }

    private void notifyDataSetChanged() {
        PagerAdapter adapter = this.mPager.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void checkDataCompatibility() {
        if (!this.mWorks.isColumnOrSerial() && !this.mManifest.isPartial && WorksData.get(this.mBookId).isPartial()) {
            new ToastBuilder().message((int) R.string.dialog_message_book_purchased_download_recommended).autoClose(false).attachTo(this).click(new OnClickListener() {
                public void onClick(View v) {
                    DownloadManager.scheduleDownload(ReaderUri.works(ReaderActivity.this.mWorks.id));
                }
            }).show();
        } else if (WorksData.get(this.mBookId).newVersionAvailable()) {
            new ToastBuilder().message((int) R.string.dialog_message_book_data_update_content_recommended).autoClose(false).attachTo(this).click(new OnClickListener() {
                public void onClick(View v) {
                    DownloadManager.scheduleDownload(ReaderUri.works(ReaderActivity.this.mWorks.id));
                }
            }).show();
        } else if (this.mManifest.isBookDataDowngraded()) {
            new ToastBuilder().message((int) R.string.dialog_message_book_data_update_format_recommended).autoClose(false).attachTo(this).click(new OnClickListener() {
                public void onClick(View v) {
                    DownloadManager.scheduleDownload(ReaderUri.works(ReaderActivity.this.mWorks.id));
                }
            }).show();
        } else if (!this.mVersionManager.featuresSupported(this.mManifest.feature)) {
            ToastBuilder builder = new ToastBuilder().message(this.mVersionManager.formatUnsupportedString(this.mManifest.feature)).autoClose(false).attachTo(this);
            if (this.mVersionManager.supportedInLatestVersion(this.mManifest.feature)) {
                builder.click(new OnClickListener() {
                    public void onClick(View v) {
                        PackageUtils.openReaderMarketStore();
                    }
                });
            }
            builder.show();
        }
    }
}
