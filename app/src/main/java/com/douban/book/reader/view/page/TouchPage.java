package com.douban.book.reader.view.page;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.HotArea;
import com.douban.book.reader.content.SelectionManager;
import com.douban.book.reader.content.SelectionManager_;
import com.douban.book.reader.content.chapter.Chapter;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.touchable.ActiveNoteTouchable;
import com.douban.book.reader.content.touchable.FootnoteTouchable;
import com.douban.book.reader.content.touchable.IllusTouchable;
import com.douban.book.reader.content.touchable.LinkTouchable;
import com.douban.book.reader.content.touchable.NoteMarkTouchable;
import com.douban.book.reader.content.touchable.PinTouchable;
import com.douban.book.reader.content.touchable.Touchable;
import com.douban.book.reader.content.touchable.UnderlineTouchable;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.PageFlipEvent;
import com.douban.book.reader.event.PagingEndedEvent;
import com.douban.book.reader.event.ReaderPanelShowNoteDetailRequest;
import com.douban.book.reader.exception.ManifestException;
import com.douban.book.reader.fragment.NoteEditFragment_;
import com.douban.book.reader.fragment.PageFragment;
import com.douban.book.reader.fragment.share.CorrectRangeEditFragment_;
import com.douban.book.reader.fragment.share.ShareRangeEditFragment_;
import com.douban.book.reader.helper.AppUri;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.manager.BookmarkManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.ClipboardUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.RichText;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.UriUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.BalloonFrame;
import com.douban.book.reader.view.FlexibleScrollView;
import com.douban.book.reader.view.MagnifierView;
import com.douban.book.reader.view.ParagraphView;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import u.aly.ci;
import u.aly.dx;

public class TouchPage extends RelativeLayout implements OnClickListener, AnimatorListener {
    private static final int FLIP_AREA_HEIGHT;
    private static final int FLIP_AREA_WIDTH;
    private static final int INVALID_POINTER = -1;
    private static final long LONG_PRESSED_PERIOD;
    private static final float MAX_DRAGGABLE_SLOP;
    private static final String TAG;
    private static final int TOGGLE_STATUS_SLOP;
    private static final int TOUCH_SLOP;
    int mActivePointerId;
    Touchable mActiveTouchable;
    private boolean mAllowFlipNext;
    private boolean mAllowFlipPrev;
    Book mBook;
    int mBookId;
    ImageView mBookmarkOn;
    float mCurrentMotionX;
    float mCurrentMotionY;
    float mDraggedY;
    private Runnable mFireFlipNext;
    private Runnable mFireFlipPrevious;
    private Runnable mFireTextSelect;
    private boolean mFlipEventPosted;
    private RectF mFlipNextArea;
    private RectF mFlipPrevArea;
    BalloonFrame mFootnoteFrame;
    ParagraphView mFootnoteText;
    public boolean mHasTextSelection;
    boolean mIsBeingDragged;
    ImageView mIvBookmarkEgg;
    public boolean mJumpByWord;
    float mLastDragX;
    float mLastDragY;
    private Handler mLongPressedHandler;
    MagnifierView mMagnifierView;
    int mPackageId;
    float mPageHeight;
    int mPageNo;
    AbsPageView mPageView;
    FrameLayout mPageViewPlaceholder;
    float mPageWidth;
    float mPinOffsetX;
    float mPinOffsetY;
    ArrayList<Touchable> mPinTouchableArray;
    AbsoluteLayout mPopupBase;
    private SelectionManager mSelectionManager;
    boolean mShouldToggleBookmarkStatus;
    private int mShowPageNo;
    boolean mTextSelectable;
    private HashMap<Integer, TouchPage> mTouchPages;
    float mTouchedDownX;
    float mTouchedDownY;
    TextView mTvBookmarkGuide;
    TextView mTvBookmarkPage;
    TouchPage mViewAfterFlip;

    /* renamed from: com.douban.book.reader.view.page.TouchPage.5 */
    class AnonymousClass5 implements OnClickListener {
        final /* synthetic */ Uri val$uri;

        AnonymousClass5(Uri uri) {
            this.val$uri = uri;
        }

        public void onClick(View v) {
            PageOpenHelper.from(TouchPage.this).open(this.val$uri);
            TouchPage.this.hidePopups();
            TouchPage.this.dismissTextSelection();
        }
    }

    static {
        TOUCH_SLOP = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(App.get()));
        TOGGLE_STATUS_SLOP = Utils.dp2pixel(60.0f);
        MAX_DRAGGABLE_SLOP = (float) (App.get().getPageHeight() / 2);
        LONG_PRESSED_PERIOD = (long) ViewConfiguration.getLongPressTimeout();
        TAG = TouchPage.class.getSimpleName();
        FLIP_AREA_WIDTH = Utils.dp2pixel(60.0f);
        FLIP_AREA_HEIGHT = Utils.dp2pixel(60.0f);
    }

    public TouchPage(Context context) {
        super(context);
        this.mTouchPages = new HashMap();
        this.mMagnifierView = null;
        this.mPinTouchableArray = new ArrayList();
        this.mActiveTouchable = null;
        this.mHasTextSelection = false;
        this.mJumpByWord = true;
        this.mIsBeingDragged = false;
        this.mViewAfterFlip = null;
        this.mPageWidth = 0.0f;
        this.mPageHeight = 0.0f;
        this.mFlipEventPosted = false;
        this.mAllowFlipPrev = true;
        this.mAllowFlipNext = true;
        this.mFlipPrevArea = new RectF();
        this.mFlipNextArea = new RectF();
        this.mActivePointerId = INVALID_POINTER;
        this.mShouldToggleBookmarkStatus = false;
        this.mPopupBase = null;
        this.mFootnoteFrame = null;
        this.mFootnoteText = null;
        this.mSelectionManager = SelectionManager_.getInstance_(App.get());
        this.mLongPressedHandler = new Handler();
        this.mFireTextSelect = new Runnable() {
            public void run() {
                if (TouchPage.this.mActiveTouchable == null || TouchPage.this.mActiveTouchable.canTriggerSelect) {
                    TouchPage.this.dismissTextSelection();
                    TouchPage.this.hidePopups();
                    TouchPage.this.startTextSelection(TouchPage.this.mCurrentMotionX, TouchPage.this.mCurrentMotionY);
                }
                TouchPage.this.mActiveTouchable = null;
            }
        };
        this.mFireFlipNext = new Runnable() {
            public void run() {
                TouchPage.this.mFlipEventPosted = false;
                TouchPage.this.mAllowFlipNext = false;
                TouchPage.this.mAllowFlipPrev = true;
                TouchPage.this.mShowPageNo = TouchPage.this.mShowPageNo + 1;
                TouchPage.this.mViewAfterFlip = TouchPage.this.getPage(TouchPage.this.mShowPageNo);
                if (TouchPage.this.mViewAfterFlip != null && TouchPage.this.mViewAfterFlip.getChapterIndex() == TouchPage.this.getChapterIndex() && TouchPage.this.selectWholePage(TouchPage.this.mViewAfterFlip, true)) {
                    TouchPage.this.hideMagnifier();
                    TouchPage.this.mViewAfterFlip.mJumpByWord = true;
                    TouchPage.this.mViewAfterFlip.mHasTextSelection = true;
                    EventBusUtils.post(new PageFlipEvent(TouchPage.this.mBookId, 1));
                    return;
                }
                TouchPage.this.mShowPageNo = TouchPage.this.mShowPageNo + TouchPage.INVALID_POINTER;
                TouchPage.this.mViewAfterFlip = null;
            }
        };
        this.mFireFlipPrevious = new Runnable() {
            public void run() {
                TouchPage.this.mFlipEventPosted = false;
                TouchPage.this.mAllowFlipPrev = false;
                TouchPage.this.mAllowFlipNext = true;
                this.this$0.mShowPageNo = TouchPage.this.mShowPageNo + TouchPage.INVALID_POINTER;
                TouchPage.this.mViewAfterFlip = TouchPage.this.getPage(TouchPage.this.mShowPageNo);
                if (TouchPage.this.mViewAfterFlip != null && TouchPage.this.mViewAfterFlip.getChapterIndex() == TouchPage.this.getChapterIndex() && TouchPage.this.selectWholePage(TouchPage.this.mViewAfterFlip, false)) {
                    TouchPage.this.hideMagnifier();
                    TouchPage.this.mViewAfterFlip.mJumpByWord = true;
                    TouchPage.this.mViewAfterFlip.mHasTextSelection = true;
                    EventBusUtils.post(new PageFlipEvent(TouchPage.this.mBookId, TouchPage.INVALID_POINTER));
                    return;
                }
                this.this$0.mShowPageNo = TouchPage.this.mShowPageNo + 1;
                TouchPage.this.mViewAfterFlip = null;
            }
        };
        initView(context);
    }

    private void initView(Context context) {
        ViewUtils.setEventAware(this);
        View content = View.inflate(context, R.layout.view_page, this);
        Utils.changeFonts(content);
        this.mBookmarkOn = (ImageView) content.findViewById(R.id.icon_bookmark_on);
        this.mTvBookmarkGuide = (TextView) content.findViewById(R.id.text_bookmark_guide);
        this.mTvBookmarkPage = (TextView) content.findViewById(R.id.text_bookmark_pagenum);
        this.mIvBookmarkEgg = (ImageView) content.findViewById(R.id.bookmark_holy_egg);
        updateColorTheme();
        this.mPopupBase = (AbsoluteLayout) content.findViewById(R.id.absolutelayout);
        this.mPageViewPlaceholder = (FrameLayout) content.findViewById(R.id.pageview_placeholder);
        setWillNotDraw(false);
    }

    public void onEventMainThread(PagingEndedEvent event) {
        if (event.isValidFor(this.mBookId)) {
            redrawHeader();
        }
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        redraw();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mPageWidth = (float) (right - left);
        this.mPageHeight = (float) (bottom - top);
        this.mFlipPrevArea.set(0.0f, 0.0f, (float) FLIP_AREA_WIDTH, (float) FLIP_AREA_HEIGHT);
        this.mFlipNextArea.set(this.mPageWidth - ((float) FLIP_AREA_WIDTH), this.mPageHeight - ((float) FLIP_AREA_HEIGHT), this.mPageWidth, this.mPageHeight);
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_PAGE_FLIP_AREA)) {
            Paint paint = PaintUtils.obtainPaint();
            paint.setColor(SupportMenu.CATEGORY_MASK);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(0.0f);
            canvas.drawRect(this.mFlipNextArea, paint);
            canvas.drawRect(this.mFlipPrevArea, paint);
            PaintUtils.recyclePaint(paint);
        }
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_PAGE_GRID_LINES)) {
            CanvasUtils.drawGridLines(canvas, new RectF(0.0f, 0.0f, (float) getWidth(), (float) getHeight()), 100);
        }
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_TOUCHABLE)) {
            Paint touchablePaint = PaintUtils.obtainPaint();
            touchablePaint.setStyle(Style.STROKE);
            for (Touchable touchable : this.mPageView.getTouchableArray()) {
                touchablePaint.setColor(Touchable.getColor(touchable));
                touchable.hotArea.drawWithLineSpacing(canvas, touchablePaint);
            }
            PaintUtils.recyclePaint(touchablePaint);
        }
    }

    public void setPage(int bookId, int pageNo) {
        this.mBookId = bookId;
        this.mBook = Book.get(this.mBookId);
        this.mPageNo = pageNo;
        this.mShowPageNo = this.mPageNo;
        Chapter chapter = this.mBook.getChapterByPage(this.mPageNo);
        if (chapter != null) {
            this.mPackageId = chapter.getPackageId();
            this.mPageView = chapter.getPageView(getContext(), this.mPageNo);
            this.mPageView.setPage(this.mBookId, this.mPageNo);
            this.mTextSelectable = this.mPageView.isTextSelectable();
            if (this.mTextSelectable) {
                setPageSelectionState();
            }
            this.mPageView.setLayoutParams(new LayoutParams(INVALID_POINTER, INVALID_POINTER));
            this.mPageViewPlaceholder.addView(this.mPageView);
            updateBookmarkIcon();
            View btnGalleryMore = findViewById(R.id.gallery_more);
            if (btnGalleryMore != null) {
                btnGalleryMore.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        TouchPage.this.showMenuPopup(v);
                    }
                });
            }
        }
    }

    private void setPageSelectionState() {
        Position startPosition = this.mSelectionManager.getSelectionStart();
        Position endPosition = this.mSelectionManager.getSelectionEnd();
        this.mHasTextSelection = false;
        boolean containStartPosition = false;
        boolean containEndPosition = false;
        if (startPosition != null) {
            containStartPosition = ((TextPageView) this.mPageView).isPositionInThisPage(startPosition);
        }
        if (endPosition != null) {
            containEndPosition = ((TextPageView) this.mPageView).isPositionInThisPage(endPosition);
        }
        if (containStartPosition || containEndPosition) {
            this.mHasTextSelection = true;
        }
        if (this.mHasTextSelection) {
            popupOperationMenu();
            this.mAllowFlipNext = containStartPosition;
            this.mAllowFlipPrev = containEndPosition;
            return;
        }
        hidePopups();
        this.mAllowFlipPrev = true;
        this.mAllowFlipNext = true;
    }

    public int getChapterIndex() {
        if (this.mPageView != null) {
            return this.mPageView.getChapterIndex();
        }
        return INVALID_POINTER;
    }

    public void redraw() {
        if (this.mPageView != null) {
            this.mPageView.redraw();
            if (this.mTextSelectable) {
                updateColorTheme();
            }
        }
        redrawFootnote();
    }

    public void redrawHeader() {
        updateBookmarkIcon();
        if (this.mPageView != null) {
            this.mPageView.redrawHeader();
        }
    }

    private void updateColorTheme() {
        setBackgroundColor(Res.getColor(R.array.reader_page_bookmark_bg));
        this.mTvBookmarkGuide.setTextColor(Res.getColor(R.array.reader_add_bookmark_hint_text_color));
        this.mIvBookmarkEgg.setImageDrawable(Res.getDrawable(R.array.reader_page_bg_decor));
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void startTextSelection(float x, float y) {
        this.mAllowFlipPrev = true;
        this.mAllowFlipNext = true;
        this.mHasTextSelection = true;
        initSelectionRange(x, y);
        requestDisallowInterceptTouchEvent(true);
        EventBusUtils.post(ArkRequest.READER_PANEL_HIDE_ALL);
        if (this.mPageView instanceof TextPageView) {
            ((TextPageView) this.mPageView).updateSelection();
            initMagnifier();
            if (((TextPageView) this.mPageView).canShowMagnifier(x, y)) {
                this.mMagnifierView.moveTo(this.mPinOffsetX + x, this.mPinOffsetY + y);
                this.mMagnifierView.setVisibility(FLIP_AREA_WIDTH);
            }
        }
        this.mShowPageNo = this.mPageNo;
    }

    public void doDismissTextSelection() {
        this.mHasTextSelection = false;
        this.mPinTouchableArray.clear();
        this.mPinOffsetX = 0.0f;
        this.mPinOffsetY = 0.0f;
        if (this.mPageView instanceof TextPageView) {
            ((TextPageView) this.mPageView).cleanSelection();
            hideMagnifier();
        }
        this.mViewAfterFlip = null;
    }

    private void dismissTextSelection() {
        for (TouchPage touchPage : this.mTouchPages.values()) {
            touchPage.doDismissTextSelection();
        }
    }

    private void initSelectionRange(float x, float y) {
        this.mJumpByWord = true;
        this.mSelectionManager.setSelectionRange(((TextPageView) this.mPageView).getRangeByPoint(new PointF(this.mPinOffsetX + x, this.mPinOffsetY + y)));
        this.mSelectionManager.editEndPosition();
    }

    private void updateTextSelectPosition(float x, float y) {
        this.mSelectionManager.moveTo(((TextPageView) this.mPageView).getPositionByPoint(new PointF(this.mPinOffsetX + x, this.mPinOffsetY + y), this.mJumpByWord, this.mSelectionManager.isEnlargingAfterwards()));
    }

    private void updateTextSelection(float x, float y) {
        initMagnifier();
        if (x < 0.0f) {
            x += this.mPageWidth;
        } else if (x > this.mPageWidth) {
            x -= this.mPageWidth;
        }
        updateTextSelectPosition(x, y);
        ((TextPageView) this.mPageView).updateSelection();
        this.mMagnifierView.moveTo(this.mPinOffsetX + x, this.mPinOffsetY + y);
        if (((TextPageView) this.mPageView).canShowMagnifier(x, y)) {
            this.mMagnifierView.setVisibility(FLIP_AREA_WIDTH);
        }
        hidePopups();
    }

    private void setCrossPageSelection(TouchPage touchPage) {
        touchPage.updateTextSelection(this.mCurrentMotionX, this.mCurrentMotionY);
        ((TextPageView) this.mPageView).updateSelection();
    }

    private boolean selectWholePage(TouchPage touchPage, boolean flipNext) {
        Position position;
        if (flipNext) {
            position = ((TextPageView) touchPage.mPageView).getLastPosition();
        } else {
            position = ((TextPageView) touchPage.mPageView).getFirstPosition();
        }
        if (!Position.isValid(position)) {
            return false;
        }
        this.mSelectionManager.moveTo(position);
        ((TextPageView) this.mPageView).updateSelection();
        return true;
    }

    private void popupOperationMenu() {
        hideMagnifier();
        Range range = this.mSelectionManager.getSelectionRange();
        CharSequence charSequence = this.mBook.getText(range);
        if (!range.isValid() || TextUtils.isEmpty(charSequence)) {
            dismissTextSelection();
            return;
        }
        this.mPinTouchableArray.clear();
        if (((TextPageView) this.mPageView).isPositionInThisPage(range.endPosition)) {
            this.mPinTouchableArray.add(getPinTouchable(range.endPosition, false));
        }
        if (((TextPageView) this.mPageView).isPositionInThisPage(range.startPosition)) {
            this.mPinTouchableArray.add(getPinTouchable(range.startPosition, true));
        }
        showTextSelectionMenu(range);
    }

    private void initMagnifier() {
        if (this.mMagnifierView == null) {
            this.mMagnifierView = new MagnifierView(getContext());
            this.mMagnifierView.setView(this.mPageView);
            this.mMagnifierView.setVisibility(4);
            addView(this.mMagnifierView);
        }
        if (this.mMagnifierView.getVisibility() == 0 && isPopupShowing()) {
            hidePopups();
        }
    }

    private void hideMagnifier() {
        if (this.mMagnifierView != null) {
            this.mMagnifierView.setVisibility(4);
        }
    }

    private TouchPage getPage(int pageNo) {
        return (TouchPage) this.mTouchPages.get(Integer.valueOf(pageNo));
    }

    private PinTouchable getPinTouchable(Position position, boolean isStartPin) {
        PinTouchable touchable = new PinTouchable();
        RectF pinRect = ((TextPageView) this.mPageView).getPinRectByPosition(position, !isStartPin);
        float width = (float) Utils.dp2pixel(40.0f);
        float height = pinRect.height() * 2.0f;
        float x = pinRect.centerX();
        float y = isStartPin ? pinRect.top : pinRect.bottom;
        touchable.basePoint.set(pinRect.centerX(), pinRect.centerY());
        touchable.hotArea.add(new RectF(x - (width / 2.0f), y - (height / 2.0f), (width / 2.0f) + x, (height / 2.0f) + y));
        touchable.isStartPin = isStartPin;
        return touchable;
    }

    private Touchable findTouchableAtPoint(int x, int y) {
        Touchable clickedTouchable = null;
        for (Touchable touchable : this.mPageView.getTouchableArray()) {
            if (touchable.hotArea.contains((float) x, (float) y)) {
                if (clickedTouchable == null) {
                    clickedTouchable = touchable;
                } else if (clickedTouchable.priority == touchable.priority) {
                    if (Utils.getDistance((float) x, (float) y, touchable.hotArea.centerX(), touchable.hotArea.centerY()) > Utils.getDistance((float) x, (float) y, clickedTouchable.hotArea.centerX(), clickedTouchable.hotArea.centerY())) {
                        clickedTouchable = touchable;
                    }
                } else if (clickedTouchable.priority < touchable.priority) {
                    clickedTouchable = touchable;
                }
            }
        }
        return clickedTouchable;
    }

    private void onTouchableClicked(MotionEvent event, Touchable clickedTouchable) {
        if (clickedTouchable != null) {
            EventBusUtils.post(ArkRequest.READER_PANEL_HIDE_ALL);
            if (clickedTouchable instanceof FootnoteTouchable) {
                showFootnote((FootnoteTouchable) clickedTouchable);
            } else if (clickedTouchable instanceof LinkTouchable) {
                if (((LinkTouchable) clickedTouchable).link != null) {
                    this.mSelectionManager.setSelectionRange(((TextPageView) this.mPageView).getRangeByPoint(new PointF(event.getX(), event.getY())));
                    setPageSelectionState();
                }
            } else if (clickedTouchable instanceof IllusTouchable) {
                if (!(this.mPageView instanceof CenterGalleryPageView)) {
                    this.mPageView.showOriginImage((IllusTouchable) clickedTouchable);
                }
            } else if (clickedTouchable instanceof UnderlineTouchable) {
                try {
                    Annotation annotation = (Annotation) AnnotationManager.ofWorks(this.mBookId).getFromCache(((UnderlineTouchable) clickedTouchable).id);
                    if (annotation.mergeAllowed()) {
                        this.mSelectionManager.setSelectionRange(AnnotationManager.ofWorks(this.mBookId).getGroupedRange(annotation.getRange()));
                        setPageSelectionState();
                    }
                } catch (DataLoadException e) {
                    Logger.e(TAG, e);
                }
            } else if (clickedTouchable instanceof NoteMarkTouchable) {
                note = AnnotationManager.ofWorks(this.mBookId).getFirstNoteInParagraph(((NoteMarkTouchable) clickedTouchable).paragraphId, ((NoteMarkTouchable) clickedTouchable).startOffset, ((NoteMarkTouchable) clickedTouchable).endOffset);
                if (note != null) {
                    EventBusUtils.post(new ReaderPanelShowNoteDetailRequest(note));
                }
            } else if (clickedTouchable instanceof ActiveNoteTouchable) {
                try {
                    note = (Annotation) AnnotationManager.ofWorks(this.mBookId).getFromCache(((ActiveNoteTouchable) clickedTouchable).uuid);
                    if (note != null) {
                        EventBusUtils.post(new ReaderPanelShowNoteDetailRequest(note));
                    }
                } catch (DataLoadException e2) {
                    Logger.e(TAG, e2);
                }
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        Logger.d(Tag.TOUCHEVENT, "TouchPage.onInterceptTouchEvent: " + event, new Object[FLIP_AREA_WIDTH]);
        if (isPopupShowing()) {
            boolean handled = this.mPopupBase.dispatchTouchEvent(event);
            Logger.d(Tag.TOUCHEVENT, "TouchPage.onInterceptTouchEvent.dispatchToPopupBase returned " + handled, new Object[FLIP_AREA_WIDTH]);
            if (!handled) {
                hidePopups();
                requestDisallowInterceptTouchEvent(true);
            }
        }
        switch (event.getAction() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) {
            case FLIP_AREA_WIDTH:
                this.mTouchedDownX = event.getX();
                this.mTouchedDownY = event.getY();
                this.mCurrentMotionX = this.mTouchedDownX;
                this.mCurrentMotionY = this.mTouchedDownY;
                this.mActivePointerId = MotionEventCompat.getPointerId(event, FLIP_AREA_WIDTH);
                break;
        }
        if (this.mPageView == null || !this.mPageView.mAllowInterceptScroll) {
            return false;
        }
        onTouchEvent(event);
        this.mPageView.mAllowInterceptScroll = false;
        return this.mIsBeingDragged;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex;
        Logger.d(Tag.TOUCHEVENT, "TouchPage.onTouchEvent: " + event, new Object[FLIP_AREA_WIDTH]);
        switch (event.getAction() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) {
            case FLIP_AREA_WIDTH:
                this.mTouchedDownX = event.getX();
                this.mTouchedDownY = event.getY();
                this.mCurrentMotionX = this.mTouchedDownX;
                this.mCurrentMotionY = this.mTouchedDownY;
                this.mActivePointerId = MotionEventCompat.getPointerId(event, FLIP_AREA_WIDTH);
                this.mViewAfterFlip = null;
                this.mFlipEventPosted = false;
                if (this.mTextSelectable) {
                    if (this.mHasTextSelection) {
                        Iterator it = this.mPinTouchableArray.iterator();
                        while (it.hasNext()) {
                            Touchable touchable = (Touchable) it.next();
                            if (touchable.hotArea.contains((float) ((int) this.mTouchedDownX), (float) ((int) this.mTouchedDownY))) {
                                requestDisallowInterceptTouchEvent(true);
                                this.mJumpByWord = false;
                                if (((PinTouchable) touchable).isStartPin) {
                                    this.mSelectionManager.editStartPosition();
                                } else {
                                    this.mSelectionManager.editEndPosition();
                                }
                                this.mPinOffsetX = ((PinTouchable) touchable).basePoint.x - this.mTouchedDownX;
                                this.mPinOffsetY = ((PinTouchable) touchable).basePoint.y - this.mTouchedDownY;
                                if (this.mSelectionManager.isEnlargingBackwards()) {
                                    this.mAllowFlipPrev = ((TextPageView) this.mPageView).isPositionInThisPage(this.mSelectionManager.getSelectionEnd());
                                    this.mAllowFlipNext = true;
                                } else {
                                    this.mAllowFlipNext = ((TextPageView) this.mPageView).isPositionInThisPage(this.mSelectionManager.getSelectionStart());
                                    this.mAllowFlipPrev = true;
                                }
                                return true;
                            }
                        }
                        dismissTextSelection();
                        return false;
                    }
                    this.mLongPressedHandler.postDelayed(this.mFireTextSelect, LONG_PRESSED_PERIOD);
                }
                this.mActiveTouchable = findTouchableAtPoint((int) this.mTouchedDownX, (int) this.mTouchedDownY);
                if (this.mActiveTouchable != null) {
                    requestDisallowInterceptTouchEvent(true);
                }
                if ((this.mPageView instanceof TextPageView) && !(this.mActiveTouchable instanceof ActiveNoteTouchable) && ((TextPageView) this.mPageView).hasActiveNote()) {
                    AnnotationManager.ofWorks(this.mBookId).setActiveNote(null);
                    requestDisallowInterceptTouchEvent(true);
                    this.mActiveTouchable = null;
                }
                return true;
            case dx.b /*1*/:
                this.mLongPressedHandler.removeCallbacks(this.mFireTextSelect);
                this.mLongPressedHandler.removeCallbacks(this.mFireFlipNext);
                this.mLongPressedHandler.removeCallbacks(this.mFireFlipPrevious);
                if (this.mTextSelectable) {
                    this.mSelectionManager.commit();
                    if (!StringUtils.hasReadableText(this.mBook.getText(this.mSelectionManager.getSelectionRange()))) {
                        dismissTextSelection();
                        hidePopups();
                    }
                    if (this.mHasTextSelection) {
                        if (this.mViewAfterFlip == null) {
                            if (!((TextPageView) this.mPageView).isPositionInThisPage(this.mSelectionManager.getSelectionStart())) {
                                this.mViewAfterFlip = getPage(this.mPageNo + INVALID_POINTER);
                            } else if (!((TextPageView) this.mPageView).isPositionInThisPage(this.mSelectionManager.getSelectionEnd())) {
                                this.mViewAfterFlip = getPage(this.mPageNo + 1);
                            }
                        }
                        if (this.mViewAfterFlip != null) {
                            ((TextPageView) this.mViewAfterFlip.mPageView).updateSelection();
                            this.mViewAfterFlip.popupOperationMenu();
                            this.mViewAfterFlip.mHasTextSelection = true;
                        }
                        ((TextPageView) this.mPageView).updateSelection();
                        popupOperationMenu();
                    }
                }
                hideMagnifier();
                if (this.mIsBeingDragged) {
                    endDrag();
                }
                this.mIsBeingDragged = false;
                if (this.mActiveTouchable != null) {
                    onTouchableClicked(event, this.mActiveTouchable);
                    this.mActiveTouchable = null;
                    break;
                }
                break;
            case dx.c /*2*/:
                try {
                    pointerIndex = MotionEventCompat.findPointerIndex(event, this.mActivePointerId);
                    this.mCurrentMotionX = MotionEventCompat.getX(event, pointerIndex);
                    this.mCurrentMotionY = MotionEventCompat.getY(event, pointerIndex);
                } catch (Exception e) {
                    pointerIndex = MotionEventCompat.getActionIndex(event);
                    this.mActivePointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                    this.mTouchedDownX = MotionEventCompat.getX(event, pointerIndex);
                    this.mTouchedDownY = MotionEventCompat.getY(event, pointerIndex);
                    this.mCurrentMotionX = this.mTouchedDownX;
                    this.mCurrentMotionY = this.mTouchedDownY;
                }
                if (this.mTextSelectable && this.mHasTextSelection) {
                    if (this.mCurrentMotionX >= 0.0f && this.mCurrentMotionX <= this.mPageWidth) {
                        updateTextSelection(this.mCurrentMotionX, this.mCurrentMotionY);
                    } else if (this.mCurrentMotionX < 0.0f) {
                        this.mCurrentMotionX += this.mPageWidth;
                        if (this.mViewAfterFlip != null) {
                            setCrossPageSelection(this.mViewAfterFlip);
                        }
                    } else {
                        this.mCurrentMotionX -= this.mPageWidth;
                        if (this.mViewAfterFlip != null) {
                            setCrossPageSelection(this.mViewAfterFlip);
                        }
                    }
                    boolean gonnaFlipNext = this.mFlipNextArea.contains(this.mCurrentMotionX, this.mCurrentMotionY);
                    boolean gonnaFlipPrev = this.mFlipPrevArea.contains(this.mCurrentMotionX, this.mCurrentMotionY);
                    if (!this.mFlipEventPosted) {
                        if (this.mAllowFlipNext && gonnaFlipNext) {
                            this.mFlipEventPosted = true;
                            this.mLongPressedHandler.postDelayed(this.mFireFlipNext, LONG_PRESSED_PERIOD);
                        }
                        if (this.mAllowFlipPrev && gonnaFlipPrev) {
                            this.mFlipEventPosted = true;
                            this.mLongPressedHandler.postDelayed(this.mFireFlipPrevious, LONG_PRESSED_PERIOD);
                        }
                    } else if (gonnaFlipNext || gonnaFlipPrev) {
                        return true;
                    } else {
                        this.mLongPressedHandler.removeCallbacks(this.mFireFlipPrevious);
                        this.mLongPressedHandler.removeCallbacks(this.mFireFlipNext);
                        this.mFlipEventPosted = false;
                    }
                    return true;
                }
                if (this.mIsBeingDragged) {
                    performDrag(this.mCurrentMotionY - this.mLastDragY);
                    this.mLastDragX = this.mCurrentMotionX;
                    this.mLastDragY = this.mCurrentMotionY;
                } else {
                    float dx = this.mCurrentMotionX - this.mTouchedDownX;
                    float dy = this.mCurrentMotionY - this.mTouchedDownY;
                    if (Math.abs(dx) > ((float) TOUCH_SLOP) || Math.abs(dy) > ((float) TOUCH_SLOP)) {
                        this.mActiveTouchable = null;
                        requestDisallowInterceptTouchEvent(false);
                        if (this.mTextSelectable) {
                            this.mLongPressedHandler.removeCallbacks(this.mFireTextSelect);
                        }
                    }
                    if (this.mTouchedDownY < ((float) TOUCH_SLOP)) {
                        String str = Tag.TOUCHEVENT;
                        r14 = new Object[2];
                        r14[FLIP_AREA_WIDTH] = Float.valueOf(this.mTouchedDownY);
                        r14[1] = Integer.valueOf(TOUCH_SLOP);
                        Logger.d(str, String.format("interrupt drag from top area touchDownY %f touch slop %d", r14), new Object[FLIP_AREA_WIDTH]);
                    } else if (Math.abs(dy) > ((float) TOUCH_SLOP) && Math.abs(dy) * 0.5f > Math.abs(dx) && this.mPageView.isDraggable()) {
                        this.mLastDragX = this.mCurrentMotionX;
                        this.mLastDragY = this.mCurrentMotionY;
                        startDrag();
                    }
                }
                return true;
            case dx.d /*3*/:
                if (this.mIsBeingDragged) {
                    endDrag();
                }
                if (this.mTextSelectable && this.mHasTextSelection) {
                    dismissTextSelection();
                }
                hidePopups();
                hideMagnifier();
                this.mIsBeingDragged = false;
                this.mActiveTouchable = null;
                this.mLongPressedHandler.removeCallbacks(this.mFireTextSelect);
                this.mLongPressedHandler.removeCallbacks(this.mFireFlipNext);
                this.mLongPressedHandler.removeCallbacks(this.mFireFlipPrevious);
                break;
            case ci.g /*6*/:
                int releasedPointerIndex = MotionEventCompat.getActionIndex(event);
                if (MotionEventCompat.getPointerId(event, releasedPointerIndex) == this.mActivePointerId) {
                    int newPointerIndex = releasedPointerIndex == 0 ? 1 : FLIP_AREA_WIDTH;
                    this.mLastDragX = MotionEventCompat.getX(event, newPointerIndex);
                    this.mLastDragY = MotionEventCompat.getY(event, newPointerIndex);
                    this.mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                    break;
                }
                break;
        }
        return false;
    }

    private void startDrag() {
        this.mIsBeingDragged = true;
        EventBusUtils.post(ArkRequest.READER_PANEL_HIDE_ALL);
        this.mShouldToggleBookmarkStatus = false;
        updateViews(false);
        this.mDraggedY = 0.0f;
    }

    private void performDrag(float dY) {
        this.mDraggedY += dY / 2.0f;
        if (this.mDraggedY > MAX_DRAGGABLE_SLOP) {
            this.mDraggedY = MAX_DRAGGABLE_SLOP;
        } else if (this.mDraggedY < 0.0f) {
            this.mDraggedY = 0.0f;
        }
        this.mPageView.setY(this.mDraggedY);
        if (this.mDraggedY > ((float) TOGGLE_STATUS_SLOP)) {
            setShouldToggleBookmarkStatus(true);
        } else {
            setShouldToggleBookmarkStatus(false);
        }
    }

    private void endDrag() {
        this.mIsBeingDragged = false;
        this.mPageView.animate().y(0.0f).setDuration(200).setListener(this);
    }

    public void onAnimationStart(Animator animator) {
    }

    public void onAnimationEnd(Animator animator) {
        if (!this.mIsBeingDragged) {
            if (this.mShouldToggleBookmarkStatus) {
                try {
                    BookmarkManager.ofWorks(this.mBookId).toggleBookmarkStatusForPage(this.mPageNo);
                } catch (DataLoadException e) {
                    Logger.e(TAG, e);
                }
            }
            if (this.mPageView != null) {
                this.mPageView.redrawHeader();
            }
            updateBookmarkIcon();
        }
    }

    public void onAnimationCancel(Animator animator) {
    }

    public void onAnimationRepeat(Animator animator) {
    }

    private void setShouldToggleBookmarkStatus(boolean shouldToggleStatus) {
        if (this.mShouldToggleBookmarkStatus != shouldToggleStatus) {
            this.mShouldToggleBookmarkStatus = shouldToggleStatus;
            updateViews(shouldToggleStatus);
        }
    }

    private void updateViews(boolean shouldToggleStatus) {
        if (shouldToggleStatus) {
            if (BookmarkManager.ofWorks(this.mBookId).hasBookmarkForPage(this.mPageNo)) {
                this.mTvBookmarkGuide.setText(R.string.text_release_rm_bookmark);
                hideBookmark();
            } else {
                this.mTvBookmarkGuide.setText(R.string.text_release_add_bookmark);
                showBookmark();
            }
        } else if (BookmarkManager.ofWorks(this.mBookId).hasBookmarkForPage(this.mPageNo)) {
            this.mTvBookmarkGuide.setText(R.string.text_pull_rm_bookmark);
            showBookmark();
        } else {
            this.mTvBookmarkGuide.setText(R.string.text_pull_add_bookmark);
            hideBookmark();
        }
        this.mPageView.redrawHeader();
    }

    private void updateBookmarkIcon() {
        if (BookmarkManager.ofWorks(this.mBookId).hasBookmarkForPage(this.mPageNo)) {
            showBookmark();
        } else {
            hideBookmark();
        }
    }

    private void showBookmark() {
        if (this.mBookmarkOn != null) {
            this.mBookmarkOn.setVisibility(FLIP_AREA_WIDTH);
        }
        if (this.mTvBookmarkPage != null) {
            int displayPageNo = this.mPageNo + 1;
            this.mTvBookmarkPage.setTextSize(FLIP_AREA_WIDTH, Res.getDimension(R.dimen.font_size_reader_header) * (displayPageNo >= AppUri.OPEN_URL ? 0.75f : 1.0f));
            this.mTvBookmarkPage.setText(Integer.toString(displayPageNo));
            this.mTvBookmarkPage.setVisibility(FLIP_AREA_WIDTH);
        }
        if (this.mPageView != null) {
            this.mPageView.setPageNumHidden(true);
        }
    }

    private void hideBookmark() {
        if (this.mBookmarkOn != null) {
            this.mBookmarkOn.setVisibility(8);
        }
        if (this.mTvBookmarkPage != null) {
            this.mTvBookmarkPage.setVisibility(8);
        }
        if (this.mPageView != null) {
            this.mPageView.setPageNumHidden(false);
        }
    }

    public void onVisible(int pageNo) {
        this.mShowPageNo = pageNo;
        if (this.mTextSelectable && (this.mPageView instanceof TextPageView) && this.mViewAfterFlip == null) {
            setPageSelectionState();
            ((TextPageView) this.mPageView).updateSelection();
        }
    }

    public void onInvisible() {
        if (this.mPageView != null) {
            this.mPageView.onPageInvisible();
        }
        hideMagnifier();
        hidePopups();
    }

    public void updateTouchPages(HashMap<Integer, PageFragment> pageFragments) {
        this.mTouchPages.clear();
        for (Entry<Integer, PageFragment> entry : pageFragments.entrySet()) {
            TouchPage touchPage = ((PageFragment) entry.getValue()).getTouchPage();
            if (touchPage != null) {
                this.mTouchPages.put(entry.getKey(), touchPage);
            }
        }
    }

    private void showFootnote(FootnoteTouchable footnoteTouchable) {
        if (isPopupShowing()) {
            hidePopups();
        }
        float textSize = Res.getScaledDimension(R.array.font_size_content) - ((float) Utils.dp2pixel(2.0f));
        float lineSpacingExtra = (Res.getScaledDimension(R.array.line_height_content) - Res.getScaledDimension(R.array.font_size_content)) - ((float) Utils.dp2pixel(6.0f));
        int horizontalPadding = Math.round(1.5f * textSize);
        int verticalPadding = Math.round(1.1f * textSize);
        this.mFootnoteFrame = new BalloonFrame(App.get());
        this.mFootnoteFrame.setDrawAreaPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        this.mFootnoteFrame.setScrollBarStyle(FLIP_AREA_WIDTH);
        this.mFootnoteFrame.setTopMinMargin(Utils.dp2pixel(70.0f));
        this.mFootnoteFrame.setLeftMinMargin(FLIP_AREA_WIDTH);
        if (this.mPopupBase != null) {
            this.mPopupBase.addView(this.mFootnoteFrame);
        }
        this.mFootnoteText = new ParagraphView(App.get());
        this.mFootnoteText.setTextSize(FLIP_AREA_WIDTH, textSize);
        this.mFootnoteText.setTypeface(Font.SANS_SERIF);
        this.mFootnoteText.setLineSpacing(lineSpacingExtra, 1.0f);
        this.mFootnoteFrame.setMaxHeight(this.mFootnoteText.getLineHeight() * 10);
        this.mFootnoteFrame.addView(this.mFootnoteText);
        this.mFootnoteFrame.scrollTo(FLIP_AREA_WIDTH, FLIP_AREA_WIDTH);
        this.mFootnoteText.setTextColor(Res.getColor(R.array.reader_text_color));
        this.mFootnoteText.setParagraphText(footnoteTouchable.str);
        this.mFootnoteFrame.setHotArea(new HotArea(footnoteTouchable.dispArea));
    }

    private void hidePopups() {
        if (this.mPopupBase != null) {
            this.mPopupBase.removeAllViews();
        }
    }

    private boolean isPopupShowing() {
        if (this.mPopupBase == null || this.mPopupBase.getChildCount() <= 0) {
            return false;
        }
        return true;
    }

    private void redrawFootnote() {
        if (this.mFootnoteFrame != null) {
            this.mFootnoteFrame.redraw();
        }
        if (this.mFootnoteText != null) {
            this.mFootnoteText.setTextColor(Res.getColor(R.array.reader_text_color));
        }
    }

    private BalloonFrame createPopupFrame(int layoutResId) {
        if (isPopupShowing()) {
            hidePopups();
        }
        BalloonFrame frame = new BalloonFrame(getContext());
        frame.setIndicatorOffset(Utils.dp2pixel(6.0f));
        View.inflate(getContext(), layoutResId, frame);
        Utils.changeFonts(frame);
        if (this.mPopupBase != null) {
            this.mPopupBase.addView(frame);
        }
        frame.setBackgroundResource(R.array.blue);
        return frame;
    }

    private void showMenuPopup(View anchor) {
        BalloonFrame frame = createPopupFrame(R.layout.popup_gallery_more_menu);
        frame.setHotArea(anchor);
        TextView tvCorrect = (TextView) frame.findViewById(R.id.gallery_popup_menu_correct);
        tvCorrect.setText(RichText.textWithIcon((int) R.drawable.v_correction, (int) R.string.menu_text_selection_correct));
        tvCorrect.setOnClickListener(this);
    }

    private void showTextSelectionMenu(Range range) {
        boolean z;
        BalloonFrame frame = createPopupFrame(R.layout.popup_text_selection_menu);
        frame.setHotArea(((TextPageView) this.mPageView).getRangeHotArea(range));
        TextView tvNewUnderline = (TextView) frame.findViewById(R.id.popup_menu_add_underline);
        ViewUtils.setDrawableTop(tvNewUnderline, (int) R.drawable.v_underline);
        tvNewUnderline.setOnClickListener(this);
        TextView tvDeleteUnderline = (TextView) frame.findViewById(R.id.popup_menu_delete_underline);
        ViewUtils.setDrawableTop(tvDeleteUnderline, (int) R.drawable.v_underline_erase);
        tvDeleteUnderline.setOnClickListener(this);
        boolean hasUnderlineFillsRange = AnnotationManager.ofWorks(this.mBookId).hasUnderlinesFillsRange(range);
        ViewUtils.showIf(hasUnderlineFillsRange, tvDeleteUnderline);
        if (hasUnderlineFillsRange) {
            z = false;
        } else {
            z = true;
        }
        ViewUtils.showIf(z, tvNewUnderline);
        TextView tvNewNote = (TextView) frame.findViewById(R.id.popup_menu_new_note);
        ViewUtils.setDrawableTop(tvNewNote, (int) R.drawable.v_note);
        tvNewNote.setOnClickListener(this);
        TextView tvShare = (TextView) frame.findViewById(R.id.popup_menu_share);
        ViewUtils.setDrawableTop(tvShare, (int) R.drawable.v_share);
        tvShare.setOnClickListener(this);
        TextView tvCopy = (TextView) frame.findViewById(R.id.popup_menu_copy);
        ViewUtils.setDrawableTop(tvCopy, (int) R.drawable.v_copy);
        tvCopy.setOnClickListener(this);
        TextView tvCorrect = (TextView) frame.findViewById(R.id.popup_menu_correct);
        ViewUtils.setDrawableTop(tvCorrect, (int) R.drawable.v_correction);
        tvCorrect.setOnClickListener(this);
        addExtraCommands(frame, ((TextPageView) this.mPageView).getLinksInRange(range));
    }

    private void addExtraCommands(BalloonFrame frame, Collection<Touchable> touchables) {
        if (!touchables.isEmpty() && frame != null) {
            View divider = frame.findViewById(R.id.divider);
            FlexibleScrollView scrollView = (FlexibleScrollView) frame.findViewById(R.id.extra_commands_scroll_parent);
            if (scrollView != null) {
                scrollView.setMaxHeight((float) Utils.dp2pixel(200.0f));
            }
            ViewUtils.visible(divider, scrollView);
            ViewGroup layoutBase = (ViewGroup) frame.findViewById(R.id.extra_commands_base);
            if (layoutBase != null) {
                Collection<Uri> urisAlreadyAdded = new ArrayList();
                for (Touchable touchable : touchables) {
                    if (touchable instanceof LinkTouchable) {
                        Uri uri = ((LinkTouchable) touchable).link;
                        if (!urisAlreadyAdded.contains(uri)) {
                            addExtraCommand(layoutBase, UriUtils.toStringRemovingScheme(uri), new AnonymousClass5(uri));
                            urisAlreadyAdded.add(uri);
                        }
                    }
                }
            }
        }
    }

    private static void addExtraCommand(ViewGroup parent, CharSequence text, OnClickListener listener) {
        TextView textView = new TextView(App.get());
        textView.setSingleLine();
        textView.setEllipsize(TruncateAt.MIDDLE);
        textView.setText(text);
        ViewUtils.of(textView).widthMatchParent().heightWrapContent().horizontalPaddingResId(R.dimen.general_subview_horizontal_padding_normal).verticalPaddingResId(R.dimen.general_subview_vertical_padding_normal).commit();
        ThemedAttrs.ofView(textView).append(R.attr.textColorArray, Integer.valueOf(R.array.invert_text_color)).updateView();
        textView.setOnClickListener(listener);
        parent.addView(textView);
    }

    public void onClick(View v) {
        Range range = this.mSelectionManager.getSelectionRange();
        switch (v.getId()) {
            case R.id.gallery_popup_menu_correct /*2131558840*/:
                Position position = this.mBook.getPositionForPage(this.mPageNo);
                if (!Utils.isNetworkAvailable()) {
                    ToastUtils.showToast((int) R.string.toast_general_error_no_network);
                    break;
                } else {
                    CorrectRangeEditFragment_.builder().worksId(this.mBookId).range(new Range(position, position)).build().showAsActivity((View) this);
                    break;
                }
            case R.id.popup_menu_add_underline /*2131558842*/:
                try {
                    AnnotationManager.ofWorks(this.mBookId).newUnderline(range);
                    this.mSelectionManager.clearSelection();
                    break;
                } catch (DataLoadException e) {
                    Logger.e(TAG, e);
                    ToastUtils.showToast((int) R.string.general_load_failed);
                    break;
                }
            case R.id.popup_menu_delete_underline /*2131558843*/:
                try {
                    AnnotationManager.ofWorks(this.mBookId).cutUnderlinesInRange(range);
                    break;
                } catch (DataLoadException e2) {
                    Logger.e(TAG, e2);
                    break;
                }
            case R.id.popup_menu_new_note /*2131558844*/:
                NoteEditFragment_.builder().worksId(this.mBookId).range(range).build().showAsActivity((View) this);
                break;
            case R.id.popup_menu_share /*2131558845*/:
                ShareRangeEditFragment_.builder().worksId(this.mBookId).range(range).build().showAsActivity(PageOpenHelper.from((View) this));
                break;
            case R.id.popup_menu_copy /*2131558846*/:
                CharSequence selectedText = StringUtils.truncate(this.mBook.getText(range), AppUri.READER);
                if (selectedText.length() > 50) {
                    RichText builder = new RichText().append((char) Char.LEFT_COMER_BRACKET).append(selectedText).append((char) Char.RIGHT_COMER_BRACKET);
                    try {
                        Works works = (Works) WorksManager.getInstance().getFromCache(Integer.valueOf(this.mBookId));
                        if (works != null) {
                            builder.append('\n').append('\n').append(works.getQuotedCopyright());
                        }
                    } catch (Exception e3) {
                    }
                    try {
                        builder.append('\n').append('\n').append(Manifest.get(this.mBookId).getAcknowledgements());
                    } catch (ManifestException e4) {
                    }
                    selectedText = builder;
                }
                ClipboardUtils.copy(selectedText);
                ToastUtils.showToast((int) R.string.toast_text_copy);
                break;
            case R.id.popup_menu_correct /*2131558847*/:
                if (!Utils.isNetworkAvailable()) {
                    ToastUtils.showToast((int) R.string.toast_general_error_no_network);
                    break;
                } else {
                    CorrectRangeEditFragment_.builder().worksId(this.mBookId).range(range).build().showAsActivity((View) this);
                    break;
                }
        }
        hidePopups();
        dismissTextSelection();
    }
}
