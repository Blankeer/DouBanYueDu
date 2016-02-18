package com.douban.book.reader.view.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.Dimen;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.SelectionManager;
import com.douban.book.reader.content.SelectionManager_;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.event.ActiveNoteChangedEvent;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.SelectionUpdatedEvent;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import java.lang.ref.WeakReference;

public class FrontLayerView extends View {
    private static final int SELECTION_HANDLE_SIZE;
    private static final String TAG;
    private static Bitmap sPinTopBitmap;
    private Range mActiveNoteRange;
    Book mBook;
    Position mLastEndPosition;
    RectF mLastRedrawRect;
    Position mLastStartPosition;
    int mPageNo;
    private SelectionManager mSelectionManager;
    WeakReference<TextPageView> mTextPageViewRef;

    static {
        TAG = FrontLayerView.class.getSimpleName();
        SELECTION_HANDLE_SIZE = Utils.dp2pixel(15.0f);
        sPinTopBitmap = null;
    }

    public FrontLayerView(Context context) {
        super(context);
        this.mLastStartPosition = new Position();
        this.mLastEndPosition = new Position();
        this.mSelectionManager = SelectionManager_.getInstance_(App.get());
    }

    public FrontLayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLastStartPosition = new Position();
        this.mLastEndPosition = new Position();
        this.mSelectionManager = SelectionManager_.getInstance_(App.get());
    }

    public void setPageView(TextPageView pageView) {
        this.mTextPageViewRef = new WeakReference(pageView);
    }

    public void setPage(int bookId, int pageNo) {
        this.mBook = Book.get(bookId);
        this.mPageNo = pageNo;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBusUtils.register(this);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusUtils.unregister(this);
    }

    public String toString() {
        return String.format("FrontLayerView: page %s", new Object[]{Integer.valueOf(this.mPageNo)});
    }

    public void onEventMainThread(SelectionUpdatedEvent event) {
        if (this.mBook != null && event.isValidForBook(this.mBook.getBookId())) {
            updateSelection();
        }
    }

    public void onEventMainThread(ActiveNoteChangedEvent event) {
        if (event.isValidFor(this.mBook.getBookId())) {
            invalidate();
        }
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        sPinTopBitmap = null;
    }

    public void updateSelection() {
        if (this.mBook != null) {
            Range range = this.mSelectionManager.getSelectionRange();
            Position startPosition = range.startPosition;
            Position endPosition = range.endPosition;
            if (!(startPosition == null || startPosition.equals(this.mLastStartPosition))) {
                redraw(startPosition);
                this.mLastStartPosition.set(startPosition);
            }
            if (!(endPosition == null || endPosition.equals(this.mLastEndPosition))) {
                redraw(endPosition);
                this.mLastEndPosition.set(endPosition);
            }
            if (startPosition == null && endPosition == null && this.mLastStartPosition != null && this.mLastEndPosition != null) {
                invalidate();
            }
        }
    }

    public void cleanSelection() {
        this.mSelectionManager.clearSelection();
        this.mLastStartPosition = new Position();
        this.mLastEndPosition = new Position();
        invalidate();
    }

    private void redraw(Position position) {
        TextPageView pageView = (TextPageView) this.mTextPageViewRef.get();
        if (pageView != null) {
            Paragraph paragraph = this.mBook.getParagraph(position.packageIndex, position.paragraphIndex);
            if (paragraph != null) {
                redraw(paragraph.getRectByOffset(position.paragraphOffset, (float) pageView.getPaddingLeft(), pageView.getParagraphOffsetY(position.paragraphIndex), pageView.mPageInfo.getStartLine(position.paragraphIndex)));
            }
        }
    }

    private void redraw(RectF rect) {
        if (rect != null) {
            if (this.mLastRedrawRect == null) {
                this.mLastRedrawRect = new RectF(rect);
            }
            Rect drawRect = new Rect();
            RectF drawRectF = new RectF(rect);
            drawRectF.union(this.mLastRedrawRect);
            drawRectF.roundOut(drawRect);
            invalidate(drawRect);
            this.mLastRedrawRect.set(rect);
        }
    }

    private void drawPinTop(Canvas canvas, float x, float y) {
        Bitmap bitmap = getPinTopBitmap();
        if (bitmap != null) {
            CanvasUtils.drawBitmapCenteredOnPoint(canvas, bitmap, x, y);
        }
    }

    private static Bitmap getPinTopBitmap() {
        if (sPinTopBitmap == null) {
            try {
                int size = SELECTION_HANDLE_SIZE + (Dimen.SHADOW_WIDTH * 4);
                sPinTopBitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
                Canvas canvas = new Canvas(sPinTopBitmap);
                float centerX = ((float) size) / 2.0f;
                float centerY = ((float) size) / 2.0f;
                float handleRadius = ((float) SELECTION_HANDLE_SIZE) / 2.0f;
                Paint paint = PaintUtils.obtainPaint();
                canvas.drawColor(0);
                PaintUtils.addShadowLayer(paint);
                paint.setColor(Res.getColor(R.array.light_stroke_color));
                canvas.drawCircle(centerX, centerY, handleRadius, paint);
                paint.setColor(Res.getColor(R.array.reader_selection_pin_color));
                paint.clearShadowLayer();
                canvas.drawCircle(centerX, centerY, handleRadius - ((float) Utils.dp2pixel(1.0f)), paint);
                PaintUtils.recyclePaint(paint);
            } catch (OutOfMemoryError e) {
                Logger.e(TAG, e);
            }
        }
        return sPinTopBitmap;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mBook != null) {
            TextPageView pageView = (TextPageView) this.mTextPageViewRef.get();
            if (pageView != null) {
                Range range = this.mSelectionManager.getSelectionRange();
                Paint paint = PaintUtils.obtainPaint();
                paint.setColor(Res.getColor(R.color.reader_selection_mask));
                pageView.drawSelectionRange(canvas, paint, range);
                pageView.drawParagraphNoteMark(canvas);
                pageView.drawUnderline(canvas);
                pageView.drawActiveNote(canvas);
                if (range.isValid()) {
                    paint.setColor(Res.getColor(R.array.reader_selection_pin_color));
                    float radius = ((float) SELECTION_HANDLE_SIZE) / 2.0f;
                    if (pageView.isPositionInThisPage(range.startPosition)) {
                        RectF startPin = pageView.getPinRectByPosition(range.startPosition, false);
                        if (!startPin.equals(Constants.DUMMY_RECT)) {
                            canvas.drawRect(startPin, paint);
                            drawPinTop(canvas, (startPin.left + startPin.right) / 2.0f, startPin.top - radius);
                        }
                    }
                    if (pageView.isPositionInThisPage(range.endPosition)) {
                        RectF endPin = pageView.getPinRectByPosition(range.endPosition, true);
                        if (!endPin.equals(Constants.DUMMY_RECT)) {
                            canvas.drawRect(endPin, paint);
                            drawPinTop(canvas, (endPin.left + endPin.right) / 2.0f, endPin.bottom + radius);
                        }
                    }
                }
                if (DebugSwitch.on(Key.APP_DEBUG_SHOW_SELECTION_RANGE_INFO)) {
                    paint.setTextSize(Res.getDimension(R.dimen.general_font_size_tiny));
                    paint.setColor(SupportMenu.CATEGORY_MASK);
                    paint.setTypeface(Typeface.MONOSPACE);
                    StringBuilder builder = new StringBuilder();
                    builder.append(pageView.getPageInfo());
                    if (Range.isValid(range)) {
                        builder.append("\nSelection: ").append(range).append("\nSelection Points: ").append(pageView.getPinRectByPosition(range.startPosition, false)).append("\n                  ").append(pageView.getPinRectByPosition(range.endPosition, true)).append("\nSelected Text:\n").append(this.mBook.getText(range));
                    }
                    CanvasUtils.drawMultiline(canvas, paint, 20.0f, 30.0f, builder.toString());
                }
                PaintUtils.recyclePaint(paint);
            }
        }
    }
}
