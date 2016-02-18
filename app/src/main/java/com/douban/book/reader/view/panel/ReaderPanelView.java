package com.douban.book.reader.view.panel;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.BaseActivity;
import com.douban.book.reader.constant.Dimen;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.ReaderPanelShowNoteDetailRequest;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.fragment.BaseFragment.OnFinishListener;
import com.douban.book.reader.fragment.NoteDetailFragment_;
import com.douban.book.reader.lib.view.DraggableLayout;
import com.douban.book.reader.lib.view.DraggableLayout.DragListener;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.manager.ProgressManager;
import com.douban.book.reader.panel.Transaction;
import com.douban.book.reader.util.MathUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.IndexedSeekBar;
import com.douban.book.reader.view.IndexedSeekBar.OnReadSeekBarChangeListener;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import u.aly.dx;

@EViewGroup(2130903177)
public class ReaderPanelView extends FrameLayout implements OnReadSeekBarChangeListener, OnTouchListener, DragListener {
    private static final float BACKGROUND_ALPHA_MAX = 0.4f;
    @ViewById(2131558916)
    View mBackground;
    @ViewById(2131558841)
    CommandBarView mCommandBar;
    @ViewById(2131558917)
    View mFitSystemWindowBaseView;
    private BaseFragment mFragmentInRightDrawer;
    @ViewById(2131558921)
    DraggableLayout mRightDrawerBase;
    @ViewById(2131558833)
    IndexedSeekBar mSeekBar;
    @ViewById(2131558919)
    SeekTipView mSeekTip;
    @ViewById(2131558920)
    ReaderSettingView mSettingView;
    private int mWorksId;

    /* renamed from: com.douban.book.reader.view.panel.ReaderPanelView.2 */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$douban$book$reader$event$ArkRequest;

        static {
            $SwitchMap$com$douban$book$reader$event$ArkRequest = new int[ArkRequest.values().length];
            try {
                $SwitchMap$com$douban$book$reader$event$ArkRequest[ArkRequest.READER_PANEL_OPEN_SETTINGS_PANEL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$douban$book$reader$event$ArkRequest[ArkRequest.READER_PANEL_HIDE_ALL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public ReaderPanelView(Context context) {
        super(context);
    }

    public ReaderPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReaderPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        ViewUtils.setEventAware(this);
        if (VERSION.SDK_INT < 16) {
            ViewUtils.setTopPadding(this.mFitSystemWindowBaseView, Dimen.STATUS_BAR_HEIGHT);
        }
        setOnTouchListener(this);
        this.mRightDrawerBase.setDragListener(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (!isAnyPanelVisible()) {
            return false;
        }
        hideAllPanels();
        return true;
    }

    public void onEventMainThread(ArkRequest request) {
        switch (AnonymousClass2.$SwitchMap$com$douban$book$reader$event$ArkRequest[request.ordinal()]) {
            case dx.b /*1*/:
                showSettingView();
            case dx.c /*2*/:
                hideAllPanels();
            default:
        }
    }

    public void onEventMainThread(ReaderPanelShowNoteDetailRequest request) {
        Annotation annotationToShow = request.getAnnotationToShow();
        if (annotationToShow != null) {
            updateRightDrawer(NoteDetailFragment_.builder().uuid(annotationToShow.uuid).noteNavigationEnabled(true).build());
            AnnotationManager.ofWorks(this.mWorksId).setActiveNote(annotationToShow);
        }
        showRightDrawer();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ViewUtils.of(this.mRightDrawerBase).width(Math.round(MathUtils.roundToRange(((float) w) * 0.7f, Res.getDimension(R.dimen.navigation_right_drawer_width), ((float) w) * 0.9f))).commit();
    }

    public void setWorksId(int worksId) {
        this.mWorksId = worksId;
        this.mCommandBar.setWorksId(worksId);
        this.mSeekTip.setWorksId(worksId);
        this.mSettingView.worksId(worksId);
        this.mSeekBar.setOnReadSeekBarChangeListener(this);
    }

    public boolean isAnyPanelVisible() {
        return ViewUtils.isAnyVisible(this.mCommandBar, this.mSeekTip, this.mRightDrawerBase, this.mSettingView);
    }

    public boolean isAnyPanelButRightDrawerVisible() {
        return ViewUtils.isAnyVisible(this.mCommandBar, this.mSeekTip, this.mSettingView);
    }

    public boolean isSettingViewVisible() {
        return ViewUtils.isVisible(this.mSettingView);
    }

    public boolean isRightDrawerVisible() {
        return ViewUtils.isVisible(this.mRightDrawerBase);
    }

    public void hideAllPanels() {
        hideCommandBar();
        hideSeekTip();
        hideSettingView();
        hideRightDrawer();
    }

    public void showCommandBar() {
        ViewUtils.visibleWithAnim(R.anim.push_bottom_in, this.mCommandBar);
        BaseActivity activity = getActivity();
        if (activity != null) {
            activity.leaveFullScreenMode();
        }
    }

    public void hideCommandBar() {
        ViewUtils.goneWithAnim(R.anim.push_bottom_out, this.mCommandBar);
        BaseActivity activity = getActivity();
        if (activity != null) {
            activity.enterFullScreenMode();
        }
    }

    private void updateRightDrawer(BaseFragment fragment) {
        fragment.setVisibleLifeCycleManuallyControlled(true);
        fragment.setShouldBeConsideredAsAPage(true);
        fragment.setOnFinishListener(new OnFinishListener() {
            public boolean beforeFinish() {
                ReaderPanelView.this.hideRightDrawer();
                return false;
            }
        });
        Transaction.begin((View) this, (int) R.id.right_drawer_frag_container).replace((Fragment) fragment).commitAllowingStateLoss();
        this.mFragmentInRightDrawer = fragment;
    }

    private void showRightDrawer() {
        ViewUtils.visibleWithAnim(R.anim.push_right_in, this.mRightDrawerBase);
        this.mBackground.animate().setDuration(400).alpha(BACKGROUND_ALPHA_MAX);
        if (this.mFragmentInRightDrawer != null) {
            this.mFragmentInRightDrawer.onVisible();
        }
    }

    private void hideRightDrawer() {
        ViewUtils.goneWithAnim(R.anim.push_right_out, this.mRightDrawerBase);
        this.mBackground.animate().setDuration(400).alpha(0.0f);
        if (this.mFragmentInRightDrawer != null) {
            this.mFragmentInRightDrawer.onInvisible();
        }
    }

    private void showBottomDrawer() {
        ViewUtils.visibleWithAnim(R.anim.push_bottom_in, this.mSettingView);
        this.mBackground.animate().setDuration(400).alpha(BACKGROUND_ALPHA_MAX);
        EventBusUtils.post(ArkEvent.SLIDING_UP_PANEL_EXPANDED);
    }

    private void hideBottomDrawer() {
        ViewUtils.goneWithAnim(R.anim.push_bottom_out, this.mSettingView);
        this.mBackground.animate().setDuration(400).alpha(0.0f);
        EventBusUtils.post(ArkEvent.SLIDING_UP_PANEL_COLLAPSED);
    }

    private void showSeekTip() {
        ViewUtils.visibleWithAnim(R.anim.toast_enter, this.mSeekTip);
    }

    private void hideSeekTip() {
        ViewUtils.goneWithAnim(R.anim.toast_exit, this.mSeekTip);
    }

    private void showSettingView() {
        showBottomDrawer();
    }

    private void hideSettingView() {
        hideBottomDrawer();
    }

    public void onProgressChanged(IndexedSeekBar seekbar, int progress) {
        this.mSeekTip.updateTip(progress);
    }

    public void onStartTrackingTouch(IndexedSeekBar seekbar) {
        showSeekTip();
        this.mSeekTip.updateTip(seekbar.getProgress());
    }

    public void onStopTrackingTouch(IndexedSeekBar seekbar) {
        hideSeekTip();
        ProgressManager.ofWorks(this.mWorksId).pushProgress(seekbar.getProgress());
    }

    public void onScrollStatusChanged(IndexedSeekBar seekBar, boolean isScrollByChapter) {
        this.mSeekTip.changeScrollMode(isScrollByChapter);
    }

    private BaseActivity getActivity() {
        return (BaseActivity) ViewUtils.getAttachedActivity(this);
    }

    public void onPositionChanged(float ratio) {
        this.mBackground.setAlpha(BACKGROUND_ALPHA_MAX * (1.0f - ratio));
        if (ratio >= 1.0f || ratio <= -1.0f) {
            ViewUtils.gone(this.mRightDrawerBase);
        }
    }
}
