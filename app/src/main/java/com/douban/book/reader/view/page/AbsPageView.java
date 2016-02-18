package com.douban.book.reader.view.page;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.activity.IllusDetailActivity;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.Book.ImageSize;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.paragraph.IllusParagraph;
import com.douban.book.reader.content.paragraph.IllusParagraph.OnGetDrawableListener;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.content.touchable.IllusTouchable;
import com.douban.book.reader.content.touchable.Touchable;
import com.douban.book.reader.location.Toc;
import com.douban.book.reader.task.PagingTaskManager;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.List;
import u.aly.dx;

public abstract class AbsPageView extends RelativeLayout {
    static final List<Touchable> EMPTY_TOUCHABLE_LIST;
    private static final float HEADER_HEIGHT;
    private static final float HEADER_TEXT_SIZE;
    private static final int TOUCH_SLOP;
    static LruCache<Integer, Drawable> sDrawableLruCache;
    protected final String TAG;
    boolean mAllowInterceptScroll;
    App mApp;
    float mBaseMarginLeft;
    Book mBook;
    int mChapterIndex;
    private List<String> mFailedUris;
    private PageViewImageLoaderListener mImageLoaderListener;
    float mMarginTop;
    OnGetDrawableListener mOnGetDrawableListener;
    private OnTouchListener mOnTouchListener;
    int mPackageId;
    int mPage;
    PageInfo mPageInfo;
    boolean mPageNumHidden;
    float mTouchedDownX;
    float mTouchedDownY;

    public interface TextSelectable {
        void cleanSelection();

        void updateSelection();
    }

    class PageViewImageLoaderListener extends SimpleImageLoadingListener {
        PageViewImageLoaderListener() {
        }

        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            if (AbsPageView.this.mFailedUris == null) {
                AbsPageView.this.mFailedUris = new ArrayList();
            }
            if (!AbsPageView.this.mFailedUris.contains(imageUri)) {
                AbsPageView.this.mFailedUris.add(imageUri);
                if (failReason.getCause() instanceof OutOfMemoryError) {
                    ImageLoaderUtils.clearMemoryCache();
                }
                AbsPageView.this.invalidate();
            }
        }

        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if ((loadedImage.getWidth() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT || loadedImage.getHeight() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT) && !ViewUtils.isSoftLayerType(AbsPageView.this)) {
                ViewUtils.setSoftLayerType(AbsPageView.this);
            }
            if (AbsPageView.this.mFailedUris != null) {
                AbsPageView.this.mFailedUris.remove(imageUri);
            }
            AbsPageView.this.invalidate();
        }
    }

    static {
        TOUCH_SLOP = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(App.get()));
        HEADER_TEXT_SIZE = Res.getDimension(R.dimen.font_size_reader_header);
        HEADER_HEIGHT = Res.getDimension(R.dimen.reader_header_height);
        EMPTY_TOUCHABLE_LIST = new ArrayList();
        sDrawableLruCache = new LruCache(2);
    }

    public AbsPageView(Context context) {
        super(context);
        this.TAG = getClass().getSimpleName();
        this.mChapterIndex = 0;
        this.mPackageId = 0;
        this.mPage = 0;
        this.mBaseMarginLeft = Res.getDimension(R.dimen.reader_horizontal_padding);
        this.mAllowInterceptScroll = false;
        this.mPageNumHidden = false;
        this.mFailedUris = null;
        this.mOnGetDrawableListener = new OnGetDrawableListener() {
            public Drawable getDrawable(int seq) {
                String uri = AbsPageView.this.getDrawableUri(seq);
                Drawable drawable = (Drawable) AbsPageView.sDrawableLruCache.get(Integer.valueOf(seq));
                if (drawable == null) {
                    Bitmap bitmap = ImageLoaderUtils.loadImageInCache(uri);
                    if (bitmap != null) {
                        drawable = new BitmapDrawable(bitmap);
                        if (drawable != null) {
                            AbsPageView.sDrawableLruCache.put(Integer.valueOf(seq), drawable);
                        }
                    }
                }
                if (drawable == null) {
                    if (Looper.myLooper() != Looper.getMainLooper()) {
                        drawable = WorksData.get(AbsPageView.this.mBook.getBookId()).getPackage(AbsPageView.this.mPackageId).getIllusDrawable(seq);
                        if (drawable != null) {
                            AbsPageView.sDrawableLruCache.put(Integer.valueOf(seq), drawable);
                        }
                    } else {
                        ImageLoaderUtils.loadImageSkippingCache(uri, AbsPageView.this.mImageLoaderListener);
                    }
                }
                return drawable;
            }

            public boolean isDrawableFailedToLoad(int seq) {
                String uri = AbsPageView.this.getDrawableUri(seq);
                if (AbsPageView.this.mFailedUris == null || !AbsPageView.this.mFailedUris.contains(uri)) {
                    return false;
                }
                return true;
            }
        };
        this.mImageLoaderListener = new PageViewImageLoaderListener();
        this.mOnTouchListener = null;
        initView();
    }

    protected void initView() {
        setWillNotDraw(false);
        this.mApp = App.get();
        ThemedAttrs.ofView(this).append(R.attr.backgroundColorArray, Integer.valueOf(R.array.page_bg_color));
        ThemedUtils.updateView(this);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Utils.unbindDrawables(this);
        sDrawableLruCache.evictAll();
    }

    public String toString() {
        return String.format("%s: page %s", new Object[]{getClass().getSimpleName(), Integer.valueOf(this.mPage)});
    }

    public void showOriginImage(IllusTouchable touchable) {
        int seq = touchable.illusSeq;
        int index = 0;
        for (int i = this.mPageInfo.startParaIndex; i <= this.mPageInfo.endParaIndex; i++) {
            Paragraph paragraph = this.mBook.getParagraph(this.mChapterIndex, i);
            if (paragraph != null && (paragraph instanceof IllusParagraph) && ((IllusParagraph) paragraph).getIllusSeq() == seq) {
                index = i;
                break;
            }
        }
        Intent intent = new Intent(this.mApp, IllusDetailActivity.class);
        intent.addFlags(268435456);
        intent.putExtra(Constants.KEY_BOOK_ID, this.mBook.getBookId());
        intent.putExtra(Constants.KEY_PACKAGE_ID, this.mPackageId);
        intent.putExtra(Constants.KEY_ILLUS_SEQ, seq);
        intent.putExtra(Constants.KEY_CHAPTER_INDEX, this.mChapterIndex);
        intent.putExtra(Constants.KEY_SECTION_INDEX, index);
        getContext().startActivity(intent);
    }

    protected void onPageInvisible() {
        if (this.mFailedUris != null) {
            this.mFailedUris.clear();
        }
    }

    public void setPage(int bookId, int page) {
        this.mBook = Book.get(bookId);
        this.mPage = page;
        loadModelInfo();
    }

    protected int getPageWidth() {
        return getWidth();
    }

    protected int getPageHeight() {
        return getHeight();
    }

    protected float getHeaderHeight() {
        return HEADER_HEIGHT;
    }

    protected void loadModelInfo() {
        this.mChapterIndex = this.mBook.getChapterIndexByPage(this.mPage);
        this.mPackageId = this.mBook.getPackageId(this.mChapterIndex);
        this.mPageInfo = this.mBook.getPageInfo(this.mPage);
    }

    public boolean isTextSelectable() {
        return false;
    }

    public boolean isDraggable() {
        return true;
    }

    public List<Touchable> getTouchableArray() {
        return EMPTY_TOUCHABLE_LIST;
    }

    public void redraw() {
        invalidate();
    }

    protected void redraw(RectF rect) {
        invalidate((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
    }

    public void redrawHeader() {
        redraw(new RectF(0.0f, 0.0f, (float) getPageWidth(), getHeaderHeight()));
    }

    public void setPageNumHidden(boolean hidden) {
        if (this.mPageNumHidden != hidden) {
            this.mPageNumHidden = hidden;
        }
    }

    protected int getHeaderBgColor() {
        return Res.getColor(R.array.page_bg_color);
    }

    protected void drawHeader(Canvas canvas) {
        float pageWidth = (float) getPageWidth();
        Paint paint = PaintUtils.obtainPaint();
        paint.setColor(getHeaderBgColor());
        canvas.drawRect(0.0f, 0.0f, pageWidth, getHeaderHeight(), paint);
        paint.setTextSize(HEADER_TEXT_SIZE);
        paint.setColor(Res.getColor(R.array.reader_header_text_color));
        paint.setTypeface(Font.SANS_SERIF);
        float posY = (getHeaderHeight() - ((float) Utils.dp2pixel(5.0f))) - paint.descent();
        String pageNumText = getPageNumText();
        paint.setTextAlign(Align.RIGHT);
        float pageNumWidth = paint.measureText(pageNumText);
        if (!this.mPageNumHidden) {
            canvas.drawText(pageNumText, pageWidth - this.mBaseMarginLeft, posY, paint);
        }
        paint.setTextAlign(Align.LEFT);
        canvas.drawText(getPageHeaderText((((6.0f * pageWidth) / 7.0f) - (2.0f * this.mBaseMarginLeft)) - pageNumWidth, paint), this.mBaseMarginLeft, posY, paint);
        PaintUtils.recyclePaint(paint);
    }

    protected String getPageHeaderText(float maxWidth, Paint paint) {
        String header = Toc.get(this.mBook.getBookId()).getTitleForPage(this.mPage);
        if (StringUtils.isEmpty(header)) {
            return Table.STRING_DEFAULT_VALUE;
        }
        if (PaintUtils.breakText(paint, header, maxWidth) < header.length()) {
            header = String.format("%s ...", new Object[]{header.substring(0, PaintUtils.breakText(paint, header, maxWidth))});
        }
        return header;
    }

    protected String getPageNumText() {
        int pageNum = this.mPage + 1;
        if (PagingTaskManager.isPaging(this.mBook.getBookId())) {
            return String.format("%d / ...", new Object[]{Integer.valueOf(pageNum)});
        }
        int totalPage = this.mBook.getPageCount();
        return String.format("%d / %d", new Object[]{Integer.valueOf(pageNum), Integer.valueOf(totalPage)});
    }

    public int getChapterIndex() {
        return this.mChapterIndex;
    }

    protected String getDrawableUri(int seq) {
        return ReaderUri.illus(this.mBook.getBookId(), this.mPackageId, seq, ImageSize.NORMAL).toString();
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        Logger.d(Tag.TOUCHEVENT, "AbsPageView.onInterceptTouchEvent: " + event, new Object[0]);
        return super.onInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        Logger.d(Tag.TOUCHEVENT, "AbsPageView.onTouchEvent: " + event, new Object[0]);
        return super.onTouchEvent(event);
    }

    protected OnTouchListener getGeneralOnTouchListener() {
        if (this.mOnTouchListener == null) {
            this.mOnTouchListener = new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Logger.d(Tag.TOUCHEVENT, "GalleryPageView.Buttons.mOnTouchListener: " + event, new Object[0]);
                    switch (event.getAction()) {
                        case dx.a /*0*/:
                            AbsPageView.this.mTouchedDownX = event.getX();
                            AbsPageView.this.mTouchedDownY = event.getY();
                            AbsPageView.this.requestDisallowInterceptTouchEvent(true);
                            break;
                        case dx.c /*2*/:
                            if (Math.abs(event.getX() - AbsPageView.this.mTouchedDownX) > ((float) AbsPageView.TOUCH_SLOP) || Math.abs(event.getY() - AbsPageView.this.mTouchedDownY) > ((float) AbsPageView.TOUCH_SLOP)) {
                                AbsPageView.this.requestDisallowInterceptTouchEvent(false);
                                break;
                            }
                        case dx.d /*3*/:
                            AbsPageView.this.requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return false;
                }
            };
        }
        return this.mOnTouchListener;
    }

    protected void setGeneralTouchListener(View view) {
        if (view != null && view.isClickable()) {
            view.setOnTouchListener(getGeneralOnTouchListener());
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setGeneralTouchListener(((ViewGroup) view).getChildAt(i));
            }
        }
    }
}
