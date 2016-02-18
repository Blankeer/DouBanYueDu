package com.douban.book.reader.view.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Dimen;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.HotArea;
import com.douban.book.reader.content.page.PageInfo;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.paragraph.IllusParagraph;
import com.douban.book.reader.content.paragraph.Line;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.content.touchable.ActiveNoteTouchable;
import com.douban.book.reader.content.touchable.LinkTouchable;
import com.douban.book.reader.content.touchable.NoteMarkTouchable;
import com.douban.book.reader.content.touchable.Touchable;
import com.douban.book.reader.content.touchable.UnderlineTouchable;
import com.douban.book.reader.entity.Annotation;
import com.douban.book.reader.event.AnnotationUpdatedEvent;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.page.AbsPageView.TextSelectable;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class TextPageView extends AbsPageView implements TextSelectable {
    public static final String ASYNC_PAGE_DRAWING_THREAD_NAME = "Async page drawing";
    private static final float NOTE_LABEL_HEIGHT;
    private static final float NOTE_MARK_HEIGHT;
    private static final float NOTE_MARK_MARGIN;
    private static final float NOTE_MARK_WIDTH;
    private static final int NOTE_TEXT_SIZE;
    private static ThreadPoolExecutor sDrawThreadPool;
    static final Stack<SoftReference<Bitmap>> sDrawingCacheBitmapStack;
    private static Bitmap sNoteMarkBg;
    int mContentTextSize;
    DrawPageRunnable mDrawCacheRunnable;
    Bitmap mDrawingCacheBitmap;
    boolean mDrawingCacheUpdated;
    FrontLayerView mFrontLayer;
    private List<Touchable> mNoteMarkTouchableArray;
    Paint mPaint;
    private List<Touchable> mParagraphTouchableArray;
    int mTextAreaMarginLeft;
    private Collection<Annotation> mUnderlines;
    private float mVerticalPadding;

    class DrawPageRunnable implements Runnable {
        RectF mRect;

        public DrawPageRunnable(RectF rect) {
            this.mRect = rect;
        }

        public void run() {
            if (TextPageView.this.mDrawingCacheBitmap != null) {
                Canvas canvas = new Canvas(TextPageView.this.mDrawingCacheBitmap);
                if (this.mRect != null) {
                    canvas.clipRect(this.mRect);
                    if (this.mRect.bottom > TextPageView.this.getHeaderHeight()) {
                        TextPageView.this.drawPage(canvas);
                    }
                } else {
                    TextPageView.this.drawPage(canvas);
                }
                TextPageView.this.drawHeader(canvas);
                TextPageView.this.mDrawingCacheUpdated = true;
                if (this.mRect != null) {
                    TextPageView.this.postInvalidate((int) this.mRect.left, (int) this.mRect.top, (int) this.mRect.right, (int) this.mRect.bottom);
                } else {
                    TextPageView.this.postInvalidate();
                }
            }
        }
    }

    interface ParagraphProcessor {
        void process(Paragraph paragraph, float f, int i, int i2);
    }

    private class ParagraphDrawingProcessor implements ParagraphProcessor {
        private final Canvas mCanvas;

        ParagraphDrawingProcessor(Canvas canvas) {
            this.mCanvas = canvas;
        }

        public void process(Paragraph paragraph, float penY, int startLine, int endLine) {
            if (paragraph instanceof IllusParagraph) {
                IllusParagraph illusParagraph = (IllusParagraph) paragraph;
                RectF layoutRect = new RectF(Res.getDimension(R.dimen.reader_horizontal_padding), paragraph.getPaddingTop() + penY, ((float) TextPageView.this.getPageWidth()) - Res.getDimension(R.dimen.reader_horizontal_padding), Math.min((illusParagraph.getHeight() + penY) - paragraph.getPaddingBottom(), ((float) TextPageView.this.getPageHeight()) - Res.getDimension(R.dimen.reader_vertical_padding)));
                illusParagraph.setOnGetDrawableListener(TextPageView.this.mOnGetDrawableListener);
                illusParagraph.setDrawLegend(true);
                illusParagraph.setLayoutRect(layoutRect);
            }
            paragraph.draw(this.mCanvas, 0.0f, penY, startLine, endLine);
            TextPageView.this.mParagraphTouchableArray.addAll(paragraph.getTouchableArray(startLine, endLine));
        }
    }

    private class ParagraphNoteMarkDrawingProcessor implements ParagraphProcessor {
        private final Canvas mCanvas;

        ParagraphNoteMarkDrawingProcessor(Canvas canvas) {
            this.mCanvas = canvas;
        }

        public void process(Paragraph paragraph, float penY, int startLine, int endLine) {
            TextPageView.this.drawNoteMark(this.mCanvas, paragraph, penY, startLine, endLine);
        }
    }

    static {
        NOTE_TEXT_SIZE = Utils.dp2pixel(11.0f);
        NOTE_MARK_HEIGHT = (float) Utils.dp2pixel(20.0f);
        NOTE_LABEL_HEIGHT = (float) Utils.dp2pixel(16.0f);
        NOTE_MARK_WIDTH = (float) Utils.dp2pixel(17.0f);
        NOTE_MARK_MARGIN = (float) Utils.dp2pixel(2.0f);
        sDrawThreadPool = null;
        sDrawingCacheBitmapStack = new Stack();
        sNoteMarkBg = null;
    }

    public TextPageView(Context context) {
        super(context);
        this.mDrawingCacheBitmap = null;
        this.mDrawingCacheUpdated = false;
        this.mPaint = new Paint();
        this.mParagraphTouchableArray = new ArrayList();
        this.mNoteMarkTouchableArray = new ArrayList();
        this.mUnderlines = null;
    }

    protected void initView() {
        super.initView();
        this.mVerticalPadding = Res.getDimension(R.dimen.reader_vertical_padding);
        this.mMarginTop = getHeaderHeight() + this.mVerticalPadding;
        this.mFrontLayer = (FrontLayerView) inflate(getContext(), R.layout.page_view_text, this).findViewById(R.id.front_layer);
        this.mFrontLayer.setPageView(this);
        ViewUtils.setEventAware(this);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mContentTextSize = Math.round(Res.getScaledDimension(R.array.font_size_content));
        int textAreaWidth = getPageWidth() - (this.mTextAreaMarginLeft * 2);
        this.mTextAreaMarginLeft = (getPageWidth() - (textAreaWidth - (textAreaWidth % this.mContentTextSize))) / 2;
        redraw();
    }

    public boolean isTextSelectable() {
        return true;
    }

    public void onEventMainThread(AnnotationUpdatedEvent event) {
        updateUnderlines();
        if (this.mBook != null && event.isValidFor(this.mBook.getBookId())) {
            this.mFrontLayer.invalidate();
        }
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        sNoteMarkBg = null;
    }

    protected void onDraw(Canvas canvas) {
        if (this.mDrawingCacheBitmap == null) {
            drawPage(canvas);
            drawHeader(canvas);
        } else if (this.mDrawingCacheUpdated) {
            canvas.drawBitmap(this.mDrawingCacheBitmap, 0.0f, 0.0f, this.mPaint);
            this.mFrontLayer.setVisibility(0);
            this.mFrontLayer.invalidate();
        } else {
            canvas.drawColor(Res.getColor(R.array.page_bg_color));
            drawHeader(canvas);
            drawWaitMessage(canvas);
        }
    }

    public void redraw() {
        redraw(null);
    }

    protected void redraw(RectF rect) {
        if (getPageWidth() <= 0) {
            Logger.d(this.TAG, "skipped redraw() because page width is unknown.", new Object[0]);
            return;
        }
        loadModelInfo();
        if (sDrawThreadPool == null || sDrawThreadPool.isShutdown()) {
            sDrawThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
            sDrawThreadPool.setThreadFactory(new ThreadFactory() {
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable);
                    thread.setPriority(9);
                    thread.setName(TextPageView.ASYNC_PAGE_DRAWING_THREAD_NAME);
                    return thread;
                }
            });
        }
        checkAndEnableDrawingCache();
        if (this.mDrawingCacheBitmap != null) {
            sDrawThreadPool.remove(this.mDrawCacheRunnable);
            this.mDrawCacheRunnable = new DrawPageRunnable(rect);
            sDrawThreadPool.execute(this.mDrawCacheRunnable);
        } else if (rect == null) {
            invalidate();
        } else {
            Rect r = new Rect();
            rect.round(r);
            invalidate(r);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkAndEnableDrawingCache();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!(sDrawThreadPool == null || this.mDrawCacheRunnable == null)) {
            sDrawThreadPool.remove(this.mDrawCacheRunnable);
            this.mDrawCacheRunnable = null;
        }
        if (this.mDrawingCacheBitmap != null) {
            synchronized (sDrawingCacheBitmapStack) {
                if (sDrawingCacheBitmapStack.size() <= 1) {
                    sDrawingCacheBitmapStack.push(new SoftReference(this.mDrawingCacheBitmap));
                } else {
                    this.mDrawingCacheBitmap.recycle();
                }
                this.mDrawingCacheBitmap = null;
            }
        }
    }

    public void setPage(int bookId, int page) {
        super.setPage(bookId, page);
        redraw();
        this.mFrontLayer.setPage(bookId, page);
        updateUnderlines();
    }

    protected void onPageInvisible() {
        super.onPageInvisible();
        updateSelection();
    }

    public void updateSelection() {
        this.mFrontLayer.updateSelection();
    }

    public void cleanSelection() {
        this.mFrontLayer.cleanSelection();
    }

    private void drawPage(Canvas canvas) {
        canvas.drawColor(Res.getColor(R.array.page_bg_color));
        drawParagraphText(canvas);
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_PAGE_MARGINS)) {
            Paint marginPaint = PaintUtils.obtainPaint();
            marginPaint.setColor(-16776961);
            marginPaint.setStyle(Style.STROKE);
            canvas.drawRect(new RectF(this.mBaseMarginLeft, getHeaderHeight() + this.mVerticalPadding, ((float) getPageWidth()) - this.mBaseMarginLeft, ((float) getPageHeight()) - this.mVerticalPadding), marginPaint);
            PaintUtils.recyclePaint(marginPaint);
        }
    }

    private void doForEachParagraph(ParagraphProcessor paragraphProcessor) {
        if (this.mPageInfo != null) {
            float lastParagraphBottomPadding = this.mVerticalPadding;
            float penY = getHeaderHeight() + this.mVerticalPadding;
            for (int i = this.mPageInfo.startParaIndex; i <= this.mPageInfo.endParaIndex; i++) {
                Paragraph paragraph = this.mBook.getParagraph(this.mChapterIndex, i);
                paragraph.setWidth((float) getPageWidth());
                float paddingTop = paragraph.getPaddingTop();
                float paddingBottom = paragraph.getPaddingBottom();
                if (lastParagraphBottomPadding > 0.0f && paddingTop > 0.0f) {
                    penY -= Math.min(paddingTop, lastParagraphBottomPadding);
                }
                lastParagraphBottomPadding = paddingBottom;
                int startLine = 0;
                int endLine = 0;
                if (i == this.mPageInfo.startParaIndex) {
                    startLine = this.mPageInfo.startLine;
                }
                if (i == this.mPageInfo.endParaIndex) {
                    endLine = this.mPageInfo.endLine;
                }
                paragraphProcessor.process(paragraph, penY, startLine, endLine);
                penY += paragraph.getHeight(startLine);
            }
        }
    }

    private void drawParagraphText(Canvas canvas) {
        this.mParagraphTouchableArray.clear();
        doForEachParagraph(new ParagraphDrawingProcessor(canvas));
    }

    public void drawParagraphNoteMark(Canvas canvas) {
        this.mNoteMarkTouchableArray.clear();
        doForEachParagraph(new ParagraphNoteMarkDrawingProcessor(canvas));
    }

    public List<Touchable> getTouchableArray() {
        List<Touchable> result = new ArrayList();
        result.addAll(this.mParagraphTouchableArray);
        result.addAll(this.mNoteMarkTouchableArray);
        ActiveNoteTouchable activeNoteTouchable = getActiveNoteTouchable();
        if (activeNoteTouchable != null) {
            result.add(activeNoteTouchable);
        }
        if (!(this.mUnderlines == null || this.mUnderlines.isEmpty())) {
            for (Annotation underline : this.mUnderlines) {
                HotArea hotArea = getRangeHotArea(underline.getRange());
                UnderlineTouchable touchable = new UnderlineTouchable();
                touchable.hotArea.union(hotArea);
                touchable.id = underline.uuid;
                result.add(touchable);
            }
        }
        return result;
    }

    private ActiveNoteTouchable getActiveNoteTouchable() {
        Annotation note = AnnotationManager.ofWorks(this.mBook.getBookId()).getActiveNote();
        if (note == null || !isRangeIntersectsWithThisPage(note.getRange())) {
            return null;
        }
        ActiveNoteTouchable touchable = new ActiveNoteTouchable(note.uuid);
        touchable.setHotArea(getRangeHotAreaWithEdge(note.getRange()));
        return touchable;
    }

    public boolean hasActiveNote() {
        return getActiveNoteTouchable() != null;
    }

    public List<Touchable> getLinksInRange(Range range) {
        List<Touchable> result = new ArrayList();
        for (Touchable touchable : getTouchableArray()) {
            if ((touchable instanceof LinkTouchable) && touchable.hotArea != null) {
                PointF point = touchable.hotArea.getPointInArea();
                if (point != null && Range.intersects(range, getRangeByPoint(point))) {
                    result.add(touchable);
                }
            }
        }
        return result;
    }

    protected void drawHeader(Canvas canvas) {
        Paint paint = PaintUtils.obtainPaint();
        super.drawHeader(canvas);
        float posY = getHeaderHeight();
        paint.setColor(Res.getColor(R.array.reader_header_sep_line_color));
        canvas.drawLine(this.mBaseMarginLeft, posY, ((float) getPageWidth()) - this.mBaseMarginLeft, posY, paint);
        PaintUtils.recyclePaint(paint);
    }

    private void drawWaitMessage(Canvas canvas) {
        Drawable drawable = Res.getDrawable(R.drawable.ic_loading);
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int left = (getPageWidth() - width) / 2;
        int top = Math.round(this.mMarginTop + ((getTextAreaHeight((float) getPageHeight()) - ((float) height)) / 2.0f));
        drawable.setBounds(left, top, left + width, top + height);
        drawable.draw(canvas);
    }

    public int getChapterIndex() {
        return this.mChapterIndex;
    }

    public int getParagraphSeqByPoint(float x, float y, boolean isAfterThisWord) {
        float lastParagraphBottomPadding = this.mVerticalPadding;
        float posY = getHeaderHeight() + this.mVerticalPadding;
        int paraId = -1;
        for (int i = this.mPageInfo.startParaIndex; i <= this.mPageInfo.endParaIndex; i++) {
            Paragraph paragraph = this.mBook.getParagraph(this.mChapterIndex, i);
            float paddingTop = paragraph.getPaddingTop();
            float paddingBottom = paragraph.getPaddingBottom();
            if (lastParagraphBottomPadding > 0.0f && paddingTop > 0.0f) {
                posY -= Math.min(paddingTop, lastParagraphBottomPadding);
            }
            lastParagraphBottomPadding = paddingBottom;
            int startLine = 0;
            if (i == this.mPageInfo.startParaIndex) {
                startLine = this.mPageInfo.startLine;
            }
            if (!paragraph.canPinStop()) {
                posY += paragraph.getHeight(startLine);
                if (posY >= y && isAfterThisWord) {
                    break;
                }
            }
            paraId = i;
            if (paragraph != null) {
                posY += paragraph.getHeight(startLine);
                if (posY >= y) {
                    break;
                }
            } else {
                continue;
            }
        }
        return paraId;
    }

    public float getParagraphOffsetY(int paraSeq) {
        float lastParagraphBottomPadding = this.mVerticalPadding;
        float offsetY = getHeaderHeight() + this.mVerticalPadding;
        for (int i = this.mPageInfo.startParaIndex; i <= this.mPageInfo.endParaIndex; i++) {
            Paragraph paragraph = this.mBook.getParagraph(this.mChapterIndex, i);
            float paddingTop = paragraph.getPaddingTop();
            float paddingBottom = paragraph.getPaddingBottom();
            if (lastParagraphBottomPadding > 0.0f && paddingTop > 0.0f) {
                offsetY -= Math.min(paddingTop, lastParagraphBottomPadding);
            }
            lastParagraphBottomPadding = paddingBottom;
            if (i == paraSeq) {
                break;
            }
            int startLine = 0;
            if (i == this.mPageInfo.startParaIndex) {
                startLine = this.mPageInfo.startLine;
            }
            offsetY += paragraph.getHeight(startLine);
        }
        return offsetY;
    }

    public void drawSelectionRange(Canvas canvas, Paint paint, Range range) {
        if (isRangeIntersectsWithThisPage(range)) {
            getRangeHotArea(range).draw(canvas, paint);
        }
    }

    public HotArea getRangeHotArea(Range range) {
        return getRangeHotArea(range, false);
    }

    public HotArea getRangeHotAreaWithEdge(Range range) {
        return getRangeHotArea(range, true);
    }

    private HotArea getRangeHotArea(Range range, boolean addEdge) {
        HotArea hotArea = new HotArea();
        Position start = range.startPosition;
        Position end = range.endPosition;
        int i = this.mPageInfo.startParaIndex;
        while (i <= this.mPageInfo.endParaIndex) {
            if (i >= start.paragraphIndex && i <= end.paragraphIndex) {
                int endOffset;
                Paragraph paragraph = this.mBook.getParagraph(this.mChapterIndex, i);
                int startOffset = i == start.paragraphIndex ? start.paragraphOffset : 0;
                if (i == end.paragraphIndex) {
                    endOffset = end.paragraphOffset;
                } else {
                    endOffset = paragraph.getText().length();
                }
                float offsetX = (float) getPaddingLeft();
                float offsetY = getParagraphOffsetY(i);
                HotArea area = paragraph.getHotAreaByOffsetRange(startOffset, endOffset, this.mPageInfo.getStartLine(i), this.mPageInfo.getEndLine(i));
                if (area != null) {
                    area.offset(offsetX, offsetY);
                }
                hotArea.union(area);
                if (addEdge) {
                    if (range.startPosition.compareTo(getFirstPosition()) >= 0) {
                        hotArea.setHasLeftEdge(true);
                    }
                    if (range.endPosition.compareTo(getLastPosition()) <= 0) {
                        hotArea.setHasRightEdge(true);
                    }
                }
            }
            i++;
        }
        return hotArea;
    }

    public Position getFirstPosition() {
        return this.mPageInfo.getRange().startPosition;
    }

    public Position getLastPosition() {
        return this.mPageInfo.getRange().endPosition;
    }

    public Range getRange() {
        return this.mPageInfo.getRange();
    }

    public Position getPositionByPoint(PointF point, boolean jumpByWord, boolean isOffsetAfterThisWord) {
        int paraSeq = getParagraphSeqByPoint(point.x, point.y, isOffsetAfterThisWord);
        if (paraSeq < 0) {
            return null;
        }
        Paragraph paragraph = this.mBook.getParagraph(this.mChapterIndex, paraSeq);
        float offsetX = (float) getPaddingLeft();
        int offset = paragraph.getOffsetByPoint(point.x - offsetX, point.y - getParagraphOffsetY(paraSeq), jumpByWord, isOffsetAfterThisWord, this.mPageInfo.getStartLine(paraSeq), this.mPageInfo.getEndLine(paraSeq));
        Position position = new Position();
        position.packageId = this.mPackageId;
        position.paragraphId = paragraph.getId();
        position.packageIndex = this.mChapterIndex;
        position.paragraphIndex = paraSeq;
        position.paragraphOffset = offset;
        return position;
    }

    public Range getRangeByPoint(PointF point) {
        return new Range(getPositionByPoint(point, true, false), getPositionByPoint(point, true, true));
    }

    public boolean canShowMagnifier(float x, float y) {
        int paraId = getParagraphSeqByPoint(x, y, true);
        if (paraId >= 0) {
            return this.mBook.getParagraph(this.mChapterIndex, paraId).canPinStop();
        }
        return false;
    }

    public boolean isPositionInThisPage(Position position) {
        return position.isInRange(getRange());
    }

    public boolean isRangeIntersectsWithThisPage(Range range) {
        if (Range.isValid(range) && range.startPosition.packageId == this.mBook.getPackageId(this.mChapterIndex)) {
            return Range.intersects(range, getRange());
        }
        return false;
    }

    public PageInfo getPageInfo() {
        return this.mPageInfo;
    }

    private void updateUnderlines() {
        this.mUnderlines = AnnotationManager.ofWorks(this.mBook.getBookId()).underlinesInRange(getRange());
    }

    private void drawNoteMark(Canvas canvas, Paragraph paragraph, float offsetY, int startLine, int endLine) {
        Annotation note = AnnotationManager.ofWorks(this.mBook.getBookId()).getActiveNote();
        if (note == null || note.getRange().endPosition.paragraphId != paragraph.getId()) {
            int startOffset = 0;
            int endOffset = AdvancedShareActionProvider.WEIGHT_MAX;
            Line line = paragraph.getLine(startLine);
            if (line != null) {
                startOffset = line.startOffset();
            }
            if (endLine > 0) {
                line = paragraph.getLine(endLine - 1);
                if (line != null) {
                    endOffset = line.endOffset();
                }
            }
            long noteCount = AnnotationManager.ofWorks(this.mBook.getBookId()).getNoteCountForParagraph(paragraph.getId(), startOffset, endOffset);
            if (noteCount > 0) {
                float firstLineHeight;
                float firstLineOffset;
                try {
                    RectF lineRect = paragraph.getLineRect(startLine, startLine);
                    firstLineHeight = lineRect.height();
                    firstLineOffset = lineRect.top;
                } catch (Throwable e) {
                    Logger.e(this.TAG, e);
                    firstLineHeight = paragraph.getLineHeight();
                    firstLineOffset = 0.0f;
                }
                float centerX = ((((float) getWidth()) - paragraph.getHorizontalMargin()) + NOTE_MARK_MARGIN) + (NOTE_MARK_WIDTH / 2.0f);
                float centerY = ((((firstLineHeight - NOTE_MARK_HEIGHT) / 2.0f) + firstLineOffset) + offsetY) + (NOTE_MARK_HEIGHT / 2.0f);
                CanvasUtils.drawBitmapCenteredOnPoint(canvas, getNoteMarkBg(), centerX, centerY);
                Paint paint = PaintUtils.obtainPaint();
                paint.setTextSize((float) NOTE_TEXT_SIZE);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setColor(Res.getColor(R.array.invert_text_color));
                CanvasUtils.drawTextCenteredOnPoint(canvas, paint, centerX, centerY, String.valueOf(Math.min(noteCount, 99)));
                PaintUtils.recyclePaint(paint);
                NoteMarkTouchable touchable = new NoteMarkTouchable();
                touchable.paragraphId = paragraph.getId();
                touchable.startOffset = startOffset;
                touchable.endOffset = endOffset;
                touchable.hotArea.add(CanvasUtils.rectFromCenterAndRadius(centerX, centerY, Dimen.MIN_TOUCH_AREA_SIZE / 2.0f));
                this.mNoteMarkTouchableArray.add(touchable);
            }
        }
    }

    @Nullable
    private static Bitmap getNoteMarkBg() {
        if (sNoteMarkBg == null) {
            try {
                sNoteMarkBg = Bitmap.createBitmap(Math.round(NOTE_MARK_WIDTH), Math.round(NOTE_MARK_HEIGHT), Config.ARGB_8888);
                Canvas canvas = new Canvas(sNoteMarkBg);
                canvas.drawColor(0);
                Paint paint = PaintUtils.obtainPaint();
                paint.setStyle(Style.FILL_AND_STROKE);
                paint.setColor(Res.getColor(R.array.dark_green));
                Path path = new Path();
                path.rLineTo(NOTE_MARK_WIDTH - Dimen.CORNER_RADIUS, 0.0f);
                path.arcTo(CanvasUtils.rectFromCenterAndRadius(NOTE_MARK_WIDTH - Dimen.CORNER_RADIUS, Dimen.CORNER_RADIUS, Dimen.CORNER_RADIUS), 270.0f, 90.0f, false);
                path.rLineTo(0.0f, NOTE_LABEL_HEIGHT - (Dimen.CORNER_RADIUS * 2.0f));
                path.arcTo(CanvasUtils.rectFromCenterAndRadius(NOTE_MARK_WIDTH - Dimen.CORNER_RADIUS, NOTE_LABEL_HEIGHT - Dimen.CORNER_RADIUS, Dimen.CORNER_RADIUS), 0.0f, 90.0f, false);
                path.rLineTo(-(NOTE_MARK_WIDTH - Dimen.CORNER_RADIUS), 0.0f);
                path.close();
                path.offset(0.0f, (NOTE_MARK_HEIGHT - NOTE_LABEL_HEIGHT) / 2.0f);
                canvas.drawPath(path, paint);
                PaintUtils.addShadowLayer(paint, 0.2f, 0.0f);
                canvas.drawRect(-NOTE_MARK_WIDTH, 0.0f, 0.0f, NOTE_MARK_HEIGHT, paint);
                PaintUtils.recyclePaint(paint);
            } catch (OutOfMemoryError e) {
            }
        }
        return sNoteMarkBg;
    }

    public void drawUnderline(Canvas canvas) {
        if (this.mUnderlines != null && !this.mUnderlines.isEmpty()) {
            Paint paint = PaintUtils.obtainPaint();
            paint.setColor(Res.getColor(R.array.reader_underline_color));
            for (Annotation annotation : this.mUnderlines) {
                drawUnderline(canvas, paint, annotation);
            }
            PaintUtils.recyclePaint(paint);
        }
    }

    public void drawActiveNote(Canvas canvas) {
        Annotation note = AnnotationManager.ofWorks(this.mBook.getBookId()).getActiveNote();
        if (note != null && isRangeIntersectsWithThisPage(note.getRange())) {
            HotArea hotArea = getRangeHotAreaWithEdge(note.getRange());
            Path path = hotArea.getPath();
            if (!path.isEmpty()) {
                CanvasUtils.drawPathShadow(canvas, path);
                Paint paint = PaintUtils.obtainPaint();
                paint.setStyle(Style.FILL);
                paint.setColor(Res.getColorOverridingAlpha(R.array.blue, 0.15f));
                hotArea.draw(canvas, paint);
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(0.0f);
                paint.setColor(Res.getColor(R.array.dark_green));
                hotArea.drawUpDownLine(canvas, paint);
                paint.setStyle(Style.FILL);
                hotArea.drawLeftEdge(canvas, paint);
                hotArea.drawRightEdge(canvas, paint);
                paint.setStyle(Style.STROKE);
                hotArea.drawLeftEdge(canvas, paint);
                hotArea.drawRightEdge(canvas, paint);
                PaintUtils.recyclePaint(paint);
            }
        }
    }

    public void drawUnderline(Canvas canvas, Paint paint, Annotation underline) {
        getRangeHotArea(underline.getRange()).drawUnderline(canvas, paint);
    }

    public RectF getPinRectByPosition(Position position, boolean isEndPin) {
        Paragraph paragraph = this.mBook.getParagraph(this.mChapterIndex, position.paragraphIndex);
        int startLine = this.mPageInfo.getStartLine(position.paragraphIndex);
        int endLine = this.mPageInfo.getEndLine(position.paragraphIndex);
        float offsetX = (float) getPaddingLeft();
        float offsetY = getParagraphOffsetY(position.paragraphIndex);
        RectF rect = paragraph.getPinRectByOffset(position.paragraphOffset, isEndPin, startLine, endLine);
        rect.offset(offsetX, offsetY);
        return rect;
    }

    private void checkAndEnableDrawingCache() {
        if (VERSION.SDK_INT >= 11) {
            enableLocalBitmapCache();
        } else if (Runtime.getRuntime().freeMemory() < 2097152) {
            setDrawingCacheEnabled(false);
        } else {
            setDrawingCacheEnabled(true);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void enableLocalBitmapCache() {
        /*
        r5 = this;
        r2 = sDrawingCacheBitmapStack;	 Catch:{ OutOfMemoryError -> 0x0023 }
        monitor-enter(r2);	 Catch:{ OutOfMemoryError -> 0x0023 }
    L_0x0003:
        r1 = r5.mDrawingCacheBitmap;	 Catch:{ all -> 0x0020 }
        if (r1 != 0) goto L_0x002a;
    L_0x0007:
        r1 = sDrawingCacheBitmapStack;	 Catch:{ all -> 0x0020 }
        r1 = r1.isEmpty();	 Catch:{ all -> 0x0020 }
        if (r1 != 0) goto L_0x002a;
    L_0x000f:
        r1 = sDrawingCacheBitmapStack;	 Catch:{ all -> 0x0020 }
        r1 = r1.pop();	 Catch:{ all -> 0x0020 }
        r1 = (java.lang.ref.SoftReference) r1;	 Catch:{ all -> 0x0020 }
        r1 = r1.get();	 Catch:{ all -> 0x0020 }
        r1 = (android.graphics.Bitmap) r1;	 Catch:{ all -> 0x0020 }
        r5.mDrawingCacheBitmap = r1;	 Catch:{ all -> 0x0020 }
        goto L_0x0003;
    L_0x0020:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0020 }
        throw r1;	 Catch:{ OutOfMemoryError -> 0x0023 }
    L_0x0023:
        r0 = move-exception;
        r1 = r5.TAG;
        com.douban.book.reader.util.Logger.e(r1, r0);
    L_0x0029:
        return;
    L_0x002a:
        r1 = r5.mDrawingCacheBitmap;	 Catch:{ all -> 0x0020 }
        if (r1 == 0) goto L_0x004e;
    L_0x002e:
        r1 = r5.mDrawingCacheBitmap;	 Catch:{ all -> 0x0020 }
        r1 = r1.getWidth();	 Catch:{ all -> 0x0020 }
        r3 = com.douban.book.reader.constant.Dimen.getRealWidth();	 Catch:{ all -> 0x0020 }
        if (r1 < r3) goto L_0x0046;
    L_0x003a:
        r1 = r5.mDrawingCacheBitmap;	 Catch:{ all -> 0x0020 }
        r1 = r1.getHeight();	 Catch:{ all -> 0x0020 }
        r3 = com.douban.book.reader.constant.Dimen.getRealHeight();	 Catch:{ all -> 0x0020 }
        if (r1 >= r3) goto L_0x004e;
    L_0x0046:
        r1 = r5.mDrawingCacheBitmap;	 Catch:{ all -> 0x0020 }
        r1.recycle();	 Catch:{ all -> 0x0020 }
        r1 = 0;
        r5.mDrawingCacheBitmap = r1;	 Catch:{ all -> 0x0020 }
    L_0x004e:
        r1 = r5.mDrawingCacheBitmap;	 Catch:{ all -> 0x0020 }
        if (r1 != 0) goto L_0x0062;
    L_0x0052:
        r1 = com.douban.book.reader.constant.Dimen.getRealWidth();	 Catch:{ all -> 0x0020 }
        r3 = com.douban.book.reader.constant.Dimen.getRealHeight();	 Catch:{ all -> 0x0020 }
        r4 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0020 }
        r1 = android.graphics.Bitmap.createBitmap(r1, r3, r4);	 Catch:{ all -> 0x0020 }
        r5.mDrawingCacheBitmap = r1;	 Catch:{ all -> 0x0020 }
    L_0x0062:
        monitor-exit(r2);	 Catch:{ all -> 0x0020 }
        goto L_0x0029;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.view.page.TextPageView.enableLocalBitmapCache():void");
    }

    public static float getTextAreaHeight(float viewPortHeight) {
        return (viewPortHeight - Res.getDimension(R.dimen.reader_header_height)) - (2.0f * Res.getDimension(R.dimen.reader_vertical_padding));
    }
}
