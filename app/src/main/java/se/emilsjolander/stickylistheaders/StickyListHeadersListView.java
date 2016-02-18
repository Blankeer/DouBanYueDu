package se.emilsjolander.stickylistheaders;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import com.douban.book.reader.util.WorksIdentity;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public class StickyListHeadersListView extends FrameLayout {
    private AdapterWrapper mAdapter;
    private boolean mAreHeadersSticky;
    private boolean mClippingToPadding;
    private AdapterWrapperDataSetObserver mDataSetObserver;
    private Drawable mDivider;
    private int mDividerHeight;
    private float mDownY;
    private View mHeader;
    private Long mHeaderId;
    private Integer mHeaderOffset;
    private boolean mHeaderOwnsTouch;
    private Integer mHeaderPosition;
    private boolean mIsDrawingListUnderStickyHeader;
    private WrapperViewList mList;
    private OnHeaderClickListener mOnHeaderClickListener;
    private OnScrollListener mOnScrollListenerDelegate;
    private OnStickyHeaderChangedListener mOnStickyHeaderChangedListener;
    private OnStickyHeaderOffsetChangedListener mOnStickyHeaderOffsetChangedListener;
    private int mPaddingBottom;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mStickyHeaderTopOffset;
    private float mTouchSlop;

    /* renamed from: se.emilsjolander.stickylistheaders.StickyListHeadersListView.3 */
    class AnonymousClass3 implements OnTouchListener {
        final /* synthetic */ OnTouchListener val$l;

        AnonymousClass3(OnTouchListener onTouchListener) {
            this.val$l = onTouchListener;
        }

        public boolean onTouch(View v, MotionEvent event) {
            return this.val$l.onTouch(StickyListHeadersListView.this, event);
        }
    }

    private class AdapterWrapperDataSetObserver extends DataSetObserver {
        private AdapterWrapperDataSetObserver() {
        }

        public void onChanged() {
            StickyListHeadersListView.this.clearHeader();
        }

        public void onInvalidated() {
            StickyListHeadersListView.this.clearHeader();
        }
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(StickyListHeadersListView stickyListHeadersListView, View view, int i, long j, boolean z);
    }

    public interface OnStickyHeaderChangedListener {
        void onStickyHeaderChanged(StickyListHeadersListView stickyListHeadersListView, View view, int i, long j);
    }

    public interface OnStickyHeaderOffsetChangedListener {
        void onStickyHeaderOffsetChanged(StickyListHeadersListView stickyListHeadersListView, View view, int i);
    }

    private class WrapperListScrollListener implements OnScrollListener {
        private WrapperListScrollListener() {
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (StickyListHeadersListView.this.mOnScrollListenerDelegate != null) {
                StickyListHeadersListView.this.mOnScrollListenerDelegate.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            StickyListHeadersListView.this.updateOrClearHeader(StickyListHeadersListView.this.mList.getFixedFirstVisibleItem());
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (StickyListHeadersListView.this.mOnScrollListenerDelegate != null) {
                StickyListHeadersListView.this.mOnScrollListenerDelegate.onScrollStateChanged(view, scrollState);
            }
        }
    }

    private class AdapterWrapperHeaderClickHandler implements OnHeaderClickListener {
        private AdapterWrapperHeaderClickHandler() {
        }

        public void onHeaderClick(View header, int itemPosition, long headerId) {
            StickyListHeadersListView.this.mOnHeaderClickListener.onHeaderClick(StickyListHeadersListView.this, header, itemPosition, headerId, false);
        }
    }

    private class WrapperViewListLifeCycleListener implements LifeCycleListener {
        private WrapperViewListLifeCycleListener() {
        }

        public void onDispatchDrawOccurred(Canvas canvas) {
            if (VERSION.SDK_INT < 8) {
                StickyListHeadersListView.this.updateOrClearHeader(StickyListHeadersListView.this.mList.getFixedFirstVisibleItem());
            }
            if (StickyListHeadersListView.this.mHeader == null) {
                return;
            }
            if (StickyListHeadersListView.this.mClippingToPadding) {
                canvas.save();
                canvas.clipRect(0, StickyListHeadersListView.this.mPaddingTop, StickyListHeadersListView.this.getRight(), StickyListHeadersListView.this.getBottom());
                StickyListHeadersListView.this.drawChild(canvas, StickyListHeadersListView.this.mHeader, 0);
                canvas.restore();
                return;
            }
            StickyListHeadersListView.this.drawChild(canvas, StickyListHeadersListView.this.mHeader, 0);
        }
    }

    public StickyListHeadersListView(Context context) {
        this(context, null);
    }

    public StickyListHeadersListView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.stickyListHeadersListViewStyle);
    }

    @TargetApi(11)
    public StickyListHeadersListView(Context context, AttributeSet attrs, int defStyle) {
        boolean z = true;
        super(context, attrs, defStyle);
        this.mAreHeadersSticky = true;
        this.mClippingToPadding = true;
        this.mIsDrawingListUnderStickyHeader = true;
        this.mStickyHeaderTopOffset = 0;
        this.mPaddingLeft = 0;
        this.mPaddingTop = 0;
        this.mPaddingRight = 0;
        this.mPaddingBottom = 0;
        this.mTouchSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.mList = new WrapperViewList(context);
        this.mDivider = this.mList.getDivider();
        this.mDividerHeight = this.mList.getDividerHeight();
        this.mList.setDivider(null);
        this.mList.setDividerHeight(0);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StickyListHeadersListView, defStyle, 0);
            try {
                boolean z2;
                int padding = a.getDimensionPixelSize(R.styleable.StickyListHeadersListView_android_padding, 0);
                this.mPaddingLeft = a.getDimensionPixelSize(R.styleable.StickyListHeadersListView_android_paddingLeft, padding);
                this.mPaddingTop = a.getDimensionPixelSize(R.styleable.StickyListHeadersListView_android_paddingTop, padding);
                this.mPaddingRight = a.getDimensionPixelSize(R.styleable.StickyListHeadersListView_android_paddingRight, padding);
                this.mPaddingBottom = a.getDimensionPixelSize(R.styleable.StickyListHeadersListView_android_paddingBottom, padding);
                setPadding(this.mPaddingLeft, this.mPaddingTop, this.mPaddingRight, this.mPaddingBottom);
                this.mClippingToPadding = a.getBoolean(R.styleable.StickyListHeadersListView_android_clipToPadding, true);
                super.setClipToPadding(true);
                this.mList.setClipToPadding(this.mClippingToPadding);
                int scrollBars = a.getInt(R.styleable.StickyListHeadersListView_android_scrollbars, AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY);
                WrapperViewList wrapperViewList = this.mList;
                if ((scrollBars & AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) != 0) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                wrapperViewList.setVerticalScrollBarEnabled(z2);
                WrapperViewList wrapperViewList2 = this.mList;
                if ((scrollBars & WorksIdentity.ID_BIT_FINALIZE) == 0) {
                    z = false;
                }
                wrapperViewList2.setHorizontalScrollBarEnabled(z);
                if (VERSION.SDK_INT >= 9) {
                    this.mList.setOverScrollMode(a.getInt(R.styleable.StickyListHeadersListView_android_overScrollMode, 0));
                }
                this.mList.setFadingEdgeLength(a.getDimensionPixelSize(R.styleable.StickyListHeadersListView_android_fadingEdgeLength, this.mList.getVerticalFadingEdgeLength()));
                int fadingEdge = a.getInt(R.styleable.StickyListHeadersListView_android_requiresFadingEdge, 0);
                if (fadingEdge == CodedOutputStream.DEFAULT_BUFFER_SIZE) {
                    this.mList.setVerticalFadingEdgeEnabled(false);
                    this.mList.setHorizontalFadingEdgeEnabled(true);
                } else if (fadingEdge == AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD) {
                    this.mList.setVerticalFadingEdgeEnabled(true);
                    this.mList.setHorizontalFadingEdgeEnabled(false);
                } else {
                    this.mList.setVerticalFadingEdgeEnabled(false);
                    this.mList.setHorizontalFadingEdgeEnabled(false);
                }
                this.mList.setCacheColorHint(a.getColor(R.styleable.StickyListHeadersListView_android_cacheColorHint, this.mList.getCacheColorHint()));
                if (VERSION.SDK_INT >= 11) {
                    this.mList.setChoiceMode(a.getInt(R.styleable.StickyListHeadersListView_android_choiceMode, this.mList.getChoiceMode()));
                }
                this.mList.setDrawSelectorOnTop(a.getBoolean(R.styleable.StickyListHeadersListView_android_drawSelectorOnTop, false));
                this.mList.setFastScrollEnabled(a.getBoolean(R.styleable.StickyListHeadersListView_android_fastScrollEnabled, this.mList.isFastScrollEnabled()));
                if (VERSION.SDK_INT >= 11) {
                    this.mList.setFastScrollAlwaysVisible(a.getBoolean(R.styleable.StickyListHeadersListView_android_fastScrollAlwaysVisible, this.mList.isFastScrollAlwaysVisible()));
                }
                this.mList.setScrollBarStyle(a.getInt(R.styleable.StickyListHeadersListView_android_scrollbarStyle, 0));
                if (a.hasValue(R.styleable.StickyListHeadersListView_android_listSelector)) {
                    this.mList.setSelector(a.getDrawable(R.styleable.StickyListHeadersListView_android_listSelector));
                }
                this.mList.setScrollingCacheEnabled(a.getBoolean(R.styleable.StickyListHeadersListView_android_scrollingCache, this.mList.isScrollingCacheEnabled()));
                if (a.hasValue(R.styleable.StickyListHeadersListView_android_divider)) {
                    this.mDivider = a.getDrawable(R.styleable.StickyListHeadersListView_android_divider);
                }
                this.mList.setStackFromBottom(a.getBoolean(R.styleable.StickyListHeadersListView_android_stackFromBottom, false));
                this.mDividerHeight = a.getDimensionPixelSize(R.styleable.StickyListHeadersListView_android_dividerHeight, this.mDividerHeight);
                this.mList.setTranscriptMode(a.getInt(R.styleable.StickyListHeadersListView_android_transcriptMode, 0));
                this.mAreHeadersSticky = a.getBoolean(R.styleable.StickyListHeadersListView_hasStickyHeaders, true);
                this.mIsDrawingListUnderStickyHeader = a.getBoolean(R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader, true);
            } finally {
                a.recycle();
            }
        }
        this.mList.setLifeCycleListener(new WrapperViewListLifeCycleListener());
        this.mList.setOnScrollListener(new WrapperListScrollListener());
        addView(this.mList);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureHeader(this.mHeader);
    }

    private void ensureHeaderHasCorrectLayoutParams(View header) {
        LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            header.setLayoutParams(new FrameLayout.LayoutParams(-1, -2));
        } else if (lp.height == -1 || lp.width == -2) {
            lp.height = -2;
            lp.width = -1;
            header.setLayoutParams(lp);
        }
    }

    private void measureHeader(View header) {
        if (header != null) {
            measureChild(header, MeasureSpec.makeMeasureSpec((getMeasuredWidth() - this.mPaddingLeft) - this.mPaddingRight, 1073741824), MeasureSpec.makeMeasureSpec(0, 0));
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mList.layout(0, 0, this.mList.getMeasuredWidth(), getHeight());
        if (this.mHeader != null) {
            int headerTop = ((MarginLayoutParams) this.mHeader.getLayoutParams()).topMargin;
            this.mHeader.layout(this.mPaddingLeft, headerTop, this.mHeader.getMeasuredWidth() + this.mPaddingLeft, this.mHeader.getMeasuredHeight() + headerTop);
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        if (this.mList.getVisibility() == 0 || this.mList.getAnimation() != null) {
            drawChild(canvas, this.mList, 0);
        }
    }

    private void clearHeader() {
        if (this.mHeader != null) {
            removeView(this.mHeader);
            this.mHeader = null;
            this.mHeaderId = null;
            this.mHeaderPosition = null;
            this.mHeaderOffset = null;
            this.mList.setTopClippingLength(0);
            updateHeaderVisibilities();
        }
    }

    private void updateOrClearHeader(int firstVisiblePosition) {
        boolean isHeaderPositionOutsideAdapterRange = false;
        int adapterCount = this.mAdapter == null ? 0 : this.mAdapter.getCount();
        if (adapterCount != 0 && this.mAreHeadersSticky) {
            boolean doesListHaveChildren;
            int headerPosition = firstVisiblePosition - this.mList.getHeaderViewsCount();
            if (this.mList.getChildCount() > 0 && this.mList.getChildAt(0).getBottom() < stickyHeaderTop()) {
                headerPosition++;
            }
            if (this.mList.getChildCount() != 0) {
                doesListHaveChildren = true;
            } else {
                doesListHaveChildren = false;
            }
            boolean isFirstViewBelowTop;
            if (doesListHaveChildren && this.mList.getFirstVisiblePosition() == 0 && this.mList.getChildAt(0).getTop() >= stickyHeaderTop()) {
                isFirstViewBelowTop = true;
            } else {
                isFirstViewBelowTop = false;
            }
            if (headerPosition > adapterCount - 1 || headerPosition < 0) {
                isHeaderPositionOutsideAdapterRange = true;
            }
            if (!doesListHaveChildren || isHeaderPositionOutsideAdapterRange || isFirstViewBelowTop) {
                clearHeader();
            } else {
                updateHeader(headerPosition);
            }
        }
    }

    private void updateHeader(int headerPosition) {
        if (this.mHeaderPosition == null || this.mHeaderPosition.intValue() != headerPosition) {
            this.mHeaderPosition = Integer.valueOf(headerPosition);
            long headerId = this.mAdapter.getHeaderId(headerPosition);
            if (this.mHeaderId == null || this.mHeaderId.longValue() != headerId) {
                this.mHeaderId = Long.valueOf(headerId);
                View header = this.mAdapter.getHeaderView(this.mHeaderPosition.intValue(), this.mHeader, this);
                if (this.mHeader != header) {
                    if (header == null) {
                        throw new NullPointerException("header may not be null");
                    }
                    swapHeader(header);
                }
                ensureHeaderHasCorrectLayoutParams(this.mHeader);
                measureHeader(this.mHeader);
                if (this.mOnStickyHeaderChangedListener != null) {
                    this.mOnStickyHeaderChangedListener.onStickyHeaderChanged(this, this.mHeader, headerPosition, this.mHeaderId.longValue());
                }
                this.mHeaderOffset = null;
            }
        }
        int headerOffset = stickyHeaderTop();
        for (int i = 0; i < this.mList.getChildCount(); i++) {
            View child = this.mList.getChildAt(i);
            boolean doesChildHaveHeader = (child instanceof WrapperView) && ((WrapperView) child).hasHeader();
            boolean isChildFooter = this.mList.containsFooterView(child);
            if (child.getTop() >= stickyHeaderTop() && (doesChildHaveHeader || isChildFooter)) {
                headerOffset = Math.min(child.getTop() - this.mHeader.getMeasuredHeight(), headerOffset);
                break;
            }
        }
        setHeaderOffet(headerOffset);
        if (!this.mIsDrawingListUnderStickyHeader) {
            this.mList.setTopClippingLength(this.mHeader.getMeasuredHeight() + this.mHeaderOffset.intValue());
        }
        updateHeaderVisibilities();
    }

    private void swapHeader(View newHeader) {
        if (this.mHeader != null) {
            removeView(this.mHeader);
        }
        this.mHeader = newHeader;
        addView(this.mHeader);
        if (this.mOnHeaderClickListener != null) {
            this.mHeader.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    StickyListHeadersListView.this.mOnHeaderClickListener.onHeaderClick(StickyListHeadersListView.this, StickyListHeadersListView.this.mHeader, StickyListHeadersListView.this.mHeaderPosition.intValue(), StickyListHeadersListView.this.mHeaderId.longValue(), true);
                }
            });
        }
        this.mHeader.setClickable(true);
    }

    private void updateHeaderVisibilities() {
        int top = stickyHeaderTop();
        int childCount = this.mList.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.mList.getChildAt(i);
            if (child instanceof WrapperView) {
                WrapperView wrapperViewChild = (WrapperView) child;
                if (wrapperViewChild.hasHeader()) {
                    View childHeader = wrapperViewChild.mHeader;
                    if (wrapperViewChild.getTop() < top) {
                        if (childHeader.getVisibility() != 4) {
                            childHeader.setVisibility(4);
                        }
                    } else if (childHeader.getVisibility() != 0) {
                        childHeader.setVisibility(0);
                    }
                }
            }
        }
    }

    @SuppressLint({"NewApi"})
    private void setHeaderOffet(int offset) {
        if (this.mHeaderOffset == null || this.mHeaderOffset.intValue() != offset) {
            this.mHeaderOffset = Integer.valueOf(offset);
            if (VERSION.SDK_INT >= 11) {
                this.mHeader.setTranslationY((float) this.mHeaderOffset.intValue());
            } else {
                MarginLayoutParams params = (MarginLayoutParams) this.mHeader.getLayoutParams();
                params.topMargin = this.mHeaderOffset.intValue();
                this.mHeader.setLayoutParams(params);
            }
            if (this.mOnStickyHeaderOffsetChangedListener != null) {
                this.mOnStickyHeaderOffsetChangedListener.onStickyHeaderOffsetChanged(this, this.mHeader, -this.mHeaderOffset.intValue());
            }
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if ((ev.getAction() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) == 0) {
            boolean z;
            this.mDownY = ev.getY();
            if (this.mHeader == null || this.mDownY > ((float) (this.mHeader.getHeight() + this.mHeaderOffset.intValue()))) {
                z = false;
            } else {
                z = true;
            }
            this.mHeaderOwnsTouch = z;
        }
        if (!this.mHeaderOwnsTouch) {
            return this.mList.dispatchTouchEvent(ev);
        }
        if (this.mHeader != null && Math.abs(this.mDownY - ev.getY()) <= this.mTouchSlop) {
            return this.mHeader.dispatchTouchEvent(ev);
        }
        if (this.mHeader != null) {
            MotionEvent cancelEvent = MotionEvent.obtain(ev);
            cancelEvent.setAction(3);
            this.mHeader.dispatchTouchEvent(cancelEvent);
            cancelEvent.recycle();
        }
        MotionEvent downEvent = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), ev.getAction(), ev.getX(), this.mDownY, ev.getMetaState());
        downEvent.setAction(0);
        boolean handled = this.mList.dispatchTouchEvent(downEvent);
        downEvent.recycle();
        this.mHeaderOwnsTouch = false;
        return handled;
    }

    private boolean isStartOfSection(int position) {
        return position == 0 || this.mAdapter.getHeaderId(position) != this.mAdapter.getHeaderId(position - 1);
    }

    public int getHeaderOverlap(int position) {
        if (isStartOfSection(Math.max(0, position - getHeaderViewsCount()))) {
            return 0;
        }
        View header = this.mAdapter.getHeaderView(position, null, this.mList);
        if (header == null) {
            throw new NullPointerException("header may not be null");
        }
        ensureHeaderHasCorrectLayoutParams(header);
        measureHeader(header);
        return header.getMeasuredHeight();
    }

    private int stickyHeaderTop() {
        return (this.mClippingToPadding ? this.mPaddingTop : 0) + this.mStickyHeaderTopOffset;
    }

    public void setAreHeadersSticky(boolean areHeadersSticky) {
        this.mAreHeadersSticky = areHeadersSticky;
        if (areHeadersSticky) {
            updateOrClearHeader(this.mList.getFixedFirstVisibleItem());
        } else {
            clearHeader();
        }
        this.mList.invalidate();
    }

    public boolean areHeadersSticky() {
        return this.mAreHeadersSticky;
    }

    @Deprecated
    public boolean getAreHeadersSticky() {
        return areHeadersSticky();
    }

    public void setStickyHeaderTopOffset(int stickyHeaderTopOffset) {
        this.mStickyHeaderTopOffset = stickyHeaderTopOffset;
        updateOrClearHeader(this.mList.getFixedFirstVisibleItem());
    }

    public int getStickyHeaderTopOffset() {
        return this.mStickyHeaderTopOffset;
    }

    public void setDrawingListUnderStickyHeader(boolean drawingListUnderStickyHeader) {
        this.mIsDrawingListUnderStickyHeader = drawingListUnderStickyHeader;
        this.mList.setTopClippingLength(0);
    }

    public boolean isDrawingListUnderStickyHeader() {
        return this.mIsDrawingListUnderStickyHeader;
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        this.mOnHeaderClickListener = listener;
        if (this.mAdapter == null) {
            return;
        }
        if (this.mOnHeaderClickListener != null) {
            this.mAdapter.setOnHeaderClickListener(new AdapterWrapperHeaderClickHandler());
            if (this.mHeader != null) {
                this.mHeader.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        StickyListHeadersListView.this.mOnHeaderClickListener.onHeaderClick(StickyListHeadersListView.this, StickyListHeadersListView.this.mHeader, StickyListHeadersListView.this.mHeaderPosition.intValue(), StickyListHeadersListView.this.mHeaderId.longValue(), true);
                    }
                });
                return;
            }
            return;
        }
        this.mAdapter.setOnHeaderClickListener(null);
    }

    public void setOnStickyHeaderOffsetChangedListener(OnStickyHeaderOffsetChangedListener listener) {
        this.mOnStickyHeaderOffsetChangedListener = listener;
    }

    public void setOnStickyHeaderChangedListener(OnStickyHeaderChangedListener listener) {
        this.mOnStickyHeaderChangedListener = listener;
    }

    public View getListChildAt(int index) {
        return this.mList.getChildAt(index);
    }

    public int getListChildCount() {
        return this.mList.getChildCount();
    }

    public ListView getWrappedList() {
        return this.mList;
    }

    private boolean requireSdkVersion(int versionCode) {
        if (VERSION.SDK_INT >= versionCode) {
            return true;
        }
        Log.e("StickyListHeaders", "Api lvl must be at least " + versionCode + " to call this method");
        return false;
    }

    public void setAdapter(StickyListHeadersAdapter adapter) {
        if (adapter == null) {
            if (this.mAdapter instanceof SectionIndexerAdapterWrapper) {
                ((SectionIndexerAdapterWrapper) this.mAdapter).mSectionIndexerDelegate = null;
            }
            if (this.mAdapter != null) {
                this.mAdapter.mDelegate = null;
            }
            this.mList.setAdapter(null);
            clearHeader();
            return;
        }
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
        }
        if (adapter instanceof SectionIndexer) {
            this.mAdapter = new SectionIndexerAdapterWrapper(getContext(), adapter);
        } else {
            this.mAdapter = new AdapterWrapper(getContext(), adapter);
        }
        this.mDataSetObserver = new AdapterWrapperDataSetObserver();
        this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
        if (this.mOnHeaderClickListener != null) {
            this.mAdapter.setOnHeaderClickListener(new AdapterWrapperHeaderClickHandler());
        } else {
            this.mAdapter.setOnHeaderClickListener(null);
        }
        this.mAdapter.setDivider(this.mDivider, this.mDividerHeight);
        this.mList.setAdapter(this.mAdapter);
        clearHeader();
    }

    public StickyListHeadersAdapter getAdapter() {
        return this.mAdapter == null ? null : this.mAdapter.mDelegate;
    }

    public void setDivider(Drawable divider) {
        this.mDivider = divider;
        if (this.mAdapter != null) {
            this.mAdapter.setDivider(this.mDivider, this.mDividerHeight);
        }
    }

    public void setDividerHeight(int dividerHeight) {
        this.mDividerHeight = dividerHeight;
        if (this.mAdapter != null) {
            this.mAdapter.setDivider(this.mDivider, this.mDividerHeight);
        }
    }

    public Drawable getDivider() {
        return this.mDivider;
    }

    public int getDividerHeight() {
        return this.mDividerHeight;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListenerDelegate = onScrollListener;
    }

    public void setOnTouchListener(OnTouchListener l) {
        if (l != null) {
            this.mList.setOnTouchListener(new AnonymousClass3(l));
        } else {
            this.mList.setOnTouchListener(null);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mList.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mList.setOnItemLongClickListener(listener);
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        this.mList.addHeaderView(v, data, isSelectable);
    }

    public void addHeaderView(View v) {
        this.mList.addHeaderView(v);
    }

    public void removeHeaderView(View v) {
        this.mList.removeHeaderView(v);
    }

    public int getHeaderViewsCount() {
        return this.mList.getHeaderViewsCount();
    }

    public void addFooterView(View v, Object data, boolean isSelectable) {
        this.mList.addFooterView(v, data, isSelectable);
    }

    public void addFooterView(View v) {
        this.mList.addFooterView(v);
    }

    public void removeFooterView(View v) {
        this.mList.removeFooterView(v);
    }

    public int getFooterViewsCount() {
        return this.mList.getFooterViewsCount();
    }

    public void setEmptyView(View v) {
        this.mList.setEmptyView(v);
    }

    public View getEmptyView() {
        return this.mList.getEmptyView();
    }

    public boolean isVerticalScrollBarEnabled() {
        return this.mList.isVerticalScrollBarEnabled();
    }

    public boolean isHorizontalScrollBarEnabled() {
        return this.mList.isHorizontalScrollBarEnabled();
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        this.mList.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    }

    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        this.mList.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled);
    }

    @TargetApi(9)
    public int getOverScrollMode() {
        if (requireSdkVersion(9)) {
            return this.mList.getOverScrollMode();
        }
        return 0;
    }

    @TargetApi(9)
    public void setOverScrollMode(int mode) {
        if (requireSdkVersion(9) && this.mList != null) {
            this.mList.setOverScrollMode(mode);
        }
    }

    @TargetApi(8)
    public void smoothScrollBy(int distance, int duration) {
        if (requireSdkVersion(8)) {
            this.mList.smoothScrollBy(distance, duration);
        }
    }

    @TargetApi(11)
    public void smoothScrollByOffset(int offset) {
        if (requireSdkVersion(11)) {
            this.mList.smoothScrollByOffset(offset);
        }
    }

    @TargetApi(8)
    @SuppressLint({"NewApi"})
    public void smoothScrollToPosition(int position) {
        int i = 0;
        if (!requireSdkVersion(8)) {
            return;
        }
        if (VERSION.SDK_INT < 11) {
            this.mList.smoothScrollToPosition(position);
            return;
        }
        int offset = this.mAdapter == null ? 0 : getHeaderOverlap(position);
        if (!this.mClippingToPadding) {
            i = this.mPaddingTop;
        }
        this.mList.smoothScrollToPositionFromTop(position, offset - i);
    }

    @TargetApi(8)
    public void smoothScrollToPosition(int position, int boundPosition) {
        if (requireSdkVersion(8)) {
            this.mList.smoothScrollToPosition(position, boundPosition);
        }
    }

    @TargetApi(11)
    public void smoothScrollToPositionFromTop(int position, int offset) {
        int i = 0;
        if (requireSdkVersion(11)) {
            offset += this.mAdapter == null ? 0 : getHeaderOverlap(position);
            if (!this.mClippingToPadding) {
                i = this.mPaddingTop;
            }
            this.mList.smoothScrollToPositionFromTop(position, offset - i);
        }
    }

    @TargetApi(11)
    public void smoothScrollToPositionFromTop(int position, int offset, int duration) {
        int i = 0;
        if (requireSdkVersion(11)) {
            offset += this.mAdapter == null ? 0 : getHeaderOverlap(position);
            if (!this.mClippingToPadding) {
                i = this.mPaddingTop;
            }
            this.mList.smoothScrollToPositionFromTop(position, offset - i, duration);
        }
    }

    public void setSelection(int position) {
        setSelectionFromTop(position, 0);
    }

    public void setSelectionAfterHeaderView() {
        this.mList.setSelectionAfterHeaderView();
    }

    public void setSelectionFromTop(int position, int y) {
        int i = 0;
        y += this.mAdapter == null ? 0 : getHeaderOverlap(position);
        if (!this.mClippingToPadding) {
            i = this.mPaddingTop;
        }
        this.mList.setSelectionFromTop(position, y - i);
    }

    public void setSelector(Drawable sel) {
        this.mList.setSelector(sel);
    }

    public void setSelector(int resID) {
        this.mList.setSelector(resID);
    }

    public int getFirstVisiblePosition() {
        return this.mList.getFirstVisiblePosition();
    }

    public int getLastVisiblePosition() {
        return this.mList.getLastVisiblePosition();
    }

    @TargetApi(11)
    public void setChoiceMode(int choiceMode) {
        this.mList.setChoiceMode(choiceMode);
    }

    @TargetApi(11)
    public void setItemChecked(int position, boolean value) {
        this.mList.setItemChecked(position, value);
    }

    @TargetApi(11)
    public int getCheckedItemCount() {
        if (requireSdkVersion(11)) {
            return this.mList.getCheckedItemCount();
        }
        return 0;
    }

    @TargetApi(8)
    public long[] getCheckedItemIds() {
        if (requireSdkVersion(8)) {
            return this.mList.getCheckedItemIds();
        }
        return null;
    }

    @TargetApi(11)
    public int getCheckedItemPosition() {
        return this.mList.getCheckedItemPosition();
    }

    @TargetApi(11)
    public SparseBooleanArray getCheckedItemPositions() {
        return this.mList.getCheckedItemPositions();
    }

    public int getCount() {
        return this.mList.getCount();
    }

    public Object getItemAtPosition(int position) {
        return this.mList.getItemAtPosition(position);
    }

    public long getItemIdAtPosition(int position) {
        return this.mList.getItemIdAtPosition(position);
    }

    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
        this.mList.setOnCreateContextMenuListener(l);
    }

    public boolean showContextMenu() {
        return this.mList.showContextMenu();
    }

    public void invalidateViews() {
        this.mList.invalidateViews();
    }

    public void setClipToPadding(boolean clipToPadding) {
        if (this.mList != null) {
            this.mList.setClipToPadding(clipToPadding);
        }
        this.mClippingToPadding = clipToPadding;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
        if (this.mList != null) {
            this.mList.setPadding(left, top, right, bottom);
        }
        super.setPadding(0, 0, 0, 0);
        requestLayout();
    }

    protected void recomputePadding() {
        setPadding(this.mPaddingLeft, this.mPaddingTop, this.mPaddingRight, this.mPaddingBottom);
    }

    public int getPaddingLeft() {
        return this.mPaddingLeft;
    }

    public int getPaddingTop() {
        return this.mPaddingTop;
    }

    public int getPaddingRight() {
        return this.mPaddingRight;
    }

    public int getPaddingBottom() {
        return this.mPaddingBottom;
    }

    public void setFastScrollEnabled(boolean fastScrollEnabled) {
        this.mList.setFastScrollEnabled(fastScrollEnabled);
    }

    @TargetApi(11)
    public void setFastScrollAlwaysVisible(boolean alwaysVisible) {
        if (requireSdkVersion(11)) {
            this.mList.setFastScrollAlwaysVisible(alwaysVisible);
        }
    }

    @TargetApi(11)
    public boolean isFastScrollAlwaysVisible() {
        if (VERSION.SDK_INT < 11) {
            return false;
        }
        return this.mList.isFastScrollAlwaysVisible();
    }

    public void setScrollBarStyle(int style) {
        this.mList.setScrollBarStyle(style);
    }

    public int getScrollBarStyle() {
        return this.mList.getScrollBarStyle();
    }

    public int getPositionForView(View view) {
        return this.mList.getPositionForView(view);
    }

    @TargetApi(11)
    public void setMultiChoiceModeListener(MultiChoiceModeListener listener) {
        if (requireSdkVersion(11)) {
            this.mList.setMultiChoiceModeListener(listener);
        }
    }

    public Parcelable onSaveInstanceState() {
        if (super.onSaveInstanceState() == BaseSavedState.EMPTY_STATE) {
            return this.mList.onSaveInstanceState();
        }
        throw new IllegalStateException("Handling non empty state of parent class is not implemented");
    }

    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE);
        this.mList.onRestoreInstanceState(state);
    }

    @TargetApi(14)
    public boolean canScrollVertically(int direction) {
        return this.mList.canScrollVertically(direction);
    }

    public void setTranscriptMode(int mode) {
        this.mList.setTranscriptMode(mode);
    }

    public void setBlockLayoutChildren(boolean blockLayoutChildren) {
        this.mList.setBlockLayoutChildren(blockLayoutChildren);
    }

    public void setStackFromBottom(boolean stackFromBottom) {
        this.mList.setStackFromBottom(stackFromBottom);
    }

    public boolean isStackFromBottom() {
        return this.mList.isStackFromBottom();
    }
}
