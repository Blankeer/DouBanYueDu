package com.douban.book.reader.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.content.paragraph.RichTextParagraph;
import com.douban.book.reader.content.touchable.LinkTouchable;
import com.douban.book.reader.content.touchable.Touchable;
import com.douban.book.reader.util.AttrUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.LineBreakIterator;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.Utils;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import u.aly.dx;

public class ParagraphView extends TextView {
    private static final int BITMAP_SLICE_MAX_SIZE = 2048;
    private static final String TAG;
    Bitmap mBitmap;
    Bitmap[] mBitmapSlice;
    private boolean mBlockQuote;
    private int mBlockQuoteDecoratorResId;
    private Touchable mClickedTouchable;
    private boolean mExpanded;
    private Indent mFirstLineIndent;
    private boolean mInvalidated;
    private boolean mLinkEnabled;
    private boolean mLocalDrawingCacheEnabled;
    private OnExpandStatusChangedListener mOnExpandStatusChangedListener;
    Paint mPaint;
    List<Paragraph> mParagraphList;
    private boolean mShowBullet;
    private List<Touchable> mTouchableArray;
    private float mTouchedDownX;
    private float mTouchedDownY;
    private int mVisibleLineCount;

    public enum Indent {
        NONE,
        AUTO,
        ALL_BUT_FIRST_PARAGRAPH,
        ALL
    }

    public interface OnExpandStatusChangedListener {
        void onExpandNeededChanged(boolean z);

        void onExpandedStatusChanged(boolean z);
    }

    static {
        TAG = ParagraphView.class.getSimpleName();
    }

    public ParagraphView(Context context) {
        super(context);
        this.mParagraphList = null;
        this.mBitmap = null;
        this.mBitmapSlice = null;
        this.mPaint = new Paint();
        this.mFirstLineIndent = Indent.AUTO;
        this.mVisibleLineCount = AdvancedShareActionProvider.WEIGHT_MAX;
        this.mLocalDrawingCacheEnabled = false;
        this.mInvalidated = true;
        this.mTouchableArray = null;
        this.mClickedTouchable = null;
        this.mOnExpandStatusChangedListener = null;
        this.mExpanded = false;
        this.mBlockQuoteDecoratorResId = -1;
        this.mLinkEnabled = true;
        init(null);
    }

    public ParagraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mParagraphList = null;
        this.mBitmap = null;
        this.mBitmapSlice = null;
        this.mPaint = new Paint();
        this.mFirstLineIndent = Indent.AUTO;
        this.mVisibleLineCount = AdvancedShareActionProvider.WEIGHT_MAX;
        this.mLocalDrawingCacheEnabled = false;
        this.mInvalidated = true;
        this.mTouchableArray = null;
        this.mClickedTouchable = null;
        this.mOnExpandStatusChangedListener = null;
        this.mExpanded = false;
        this.mBlockQuoteDecoratorResId = -1;
        this.mLinkEnabled = true;
        init(attrs);
    }

    public ParagraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mParagraphList = null;
        this.mBitmap = null;
        this.mBitmapSlice = null;
        this.mPaint = new Paint();
        this.mFirstLineIndent = Indent.AUTO;
        this.mVisibleLineCount = AdvancedShareActionProvider.WEIGHT_MAX;
        this.mLocalDrawingCacheEnabled = false;
        this.mInvalidated = true;
        this.mTouchableArray = null;
        this.mClickedTouchable = null;
        this.mOnExpandStatusChangedListener = null;
        this.mExpanded = false;
        this.mBlockQuoteDecoratorResId = -1;
        this.mLinkEnabled = true;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.mVisibleLineCount = AttrUtils.getInteger(attrs, 16843091, AdvancedShareActionProvider.WEIGHT_MAX);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkAndEnableDrawingCache();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Utils.unbindDrawables(this);
        if (this.mBitmap != null) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }
        if (this.mBitmapSlice != null) {
            for (int i = 0; i < this.mBitmapSlice.length; i++) {
                if (this.mBitmapSlice[i] != null) {
                    this.mBitmapSlice[i].recycle();
                }
            }
            this.mBitmapSlice = null;
        }
    }

    public void setTextSize(float textSize) {
        super.setTextSize(0, textSize);
        requestLayout();
        clearTouchableArray();
        triggerRedraw();
    }

    public void setTextBold() {
        if (this.mParagraphList != null) {
            for (Paragraph paragraph : this.mParagraphList) {
                paragraph.setBold(true);
            }
        }
    }

    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
        if (Font.SERIF.equals(tf) && this.mParagraphList != null) {
            for (Paragraph paragraph : this.mParagraphList) {
                paragraph.setQuote(true);
            }
        }
    }

    public void setBlockQuote(boolean blockQuote) {
        if (this.mParagraphList != null) {
            throw new IllegalStateException("Must be called before text set");
        }
        this.mBlockQuote = blockQuote;
    }

    public void setBlockQuoteLineColor(@ColorRes @ArrayRes int resId) {
        this.mBlockQuoteDecoratorResId = resId;
    }

    public void setShowBullet(boolean showBullet) {
        if (this.mParagraphList != null) {
            throw new IllegalStateException("Must be called before text set");
        }
        this.mShowBullet = showBullet;
    }

    public void setLinkEnabled(boolean linkEnabled) {
        this.mLinkEnabled = linkEnabled;
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0);
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        if (this.mLocalDrawingCacheEnabled) {
            try {
                if (this.mInvalidated || this.mBitmapSlice == null) {
                    draw();
                }
                if (this.mBitmapSlice != null) {
                    for (int i = 0; i < this.mBitmapSlice.length; i++) {
                        canvas.drawBitmap(this.mBitmapSlice[i], (float) getPaddingLeft(), ((float) (getPaddingTop() + (i * BITMAP_SLICE_MAX_SIZE))) - getHalfLineSpacing(), this.mPaint);
                    }
                }
                this.mInvalidated = false;
                return;
            } catch (OutOfMemoryError e) {
                Logger.e(TAG, e);
            }
        }
        if (this.mParagraphList != null) {
            float offsetY = ((float) getPaddingTop()) - getHalfLineSpacing();
            for (Paragraph paragraph : this.mParagraphList) {
                float nextOffset = offsetY + paragraph.getHeight();
                paragraph.draw(canvas, (float) getPaddingLeft(), offsetY);
                offsetY = nextOffset;
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float parentWidth = (float) MeasureSpec.getSize(widthMeasureSpec);
        float textWidth = (parentWidth - ((float) getPaddingLeft())) - ((float) getPaddingRight());
        float height = (float) (getPaddingTop() + getPaddingBottom());
        float maxParagraphWidth = 1.0f;
        int remainedLineCount = this.mVisibleLineCount;
        boolean lineLimitExceeded = false;
        if (this.mParagraphList != null && parentWidth > 0.0f) {
            for (Paragraph paragraph : this.mParagraphList) {
                paragraph.setBaseLeftMargin(0.0f);
                paragraph.setWidth(textWidth);
                paragraph.setLineLimit(0);
            }
            for (Paragraph paragraph2 : this.mParagraphList) {
                if (remainedLineCount <= 0) {
                    lineLimitExceeded = true;
                }
                if (!this.mExpanded && lineLimitExceeded) {
                    break;
                }
                maxParagraphWidth = Math.max(maxParagraphWidth, paragraph2.getMinWidth());
                int lineCount = paragraph2.getLineCount();
                if (!this.mExpanded && remainedLineCount < lineCount) {
                    paragraph2.setLineLimit(remainedLineCount);
                    lineLimitExceeded = true;
                }
                remainedLineCount -= lineCount;
                height += paragraph2.getHeight();
            }
        }
        if (height <= 1.0f) {
            height = 1.0f;
        }
        if (MeasureSpec.getMode(widthMeasureSpec) == 1073741824) {
            setMeasuredDimension((int) Math.ceil((double) parentWidth), (int) Math.ceil((double) height));
        } else {
            setMeasuredDimension((int) Math.ceil((double) maxParagraphWidth), (int) Math.ceil((double) height));
        }
        if (this.mOnExpandStatusChangedListener != null) {
            this.mOnExpandStatusChangedListener.onExpandedStatusChanged(this.mExpanded);
            this.mOnExpandStatusChangedListener.onExpandNeededChanged(lineLimitExceeded);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        Logger.d(Tag.TOUCHEVENT, "ParagraphView.onTouchEvent: " + event, new Object[0]);
        if (!this.mLinkEnabled) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) {
            case dx.a /*0*/:
                this.mTouchedDownX = event.getX();
                this.mTouchedDownY = event.getY();
                this.mClickedTouchable = findTouchableAtPoint(this.mTouchedDownX, this.mTouchedDownY);
                if (this.mClickedTouchable instanceof LinkTouchable) {
                    Logger.d(Tag.TOUCHEVENT, "found touchable: %s", this.mClickedTouchable);
                    return true;
                }
                break;
            case dx.b /*1*/:
                if (Utils.getDistance(event.getX(), event.getY(), this.mTouchedDownX, this.mTouchedDownY) < ((float) Constants.TOUCH_SLOP) && (this.mClickedTouchable instanceof LinkTouchable)) {
                    Logger.d(Tag.TOUCHEVENT, "open touchable: %s", this.mClickedTouchable);
                    PageOpenHelper.from((View) this).open(((LinkTouchable) this.mClickedTouchable).link);
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }

    public void setParagraphText(CharSequence str, boolean trimContent) {
        ArrayList<Paragraph> paragraphs = new ArrayList();
        LineBreakIterator iterator = new LineBreakIterator(str);
        while (iterator.hasNext()) {
            Paragraph paragraph = new RichTextParagraph(this);
            paragraph.setBaseLeftMargin(0.0f);
            paragraph.setBlockQuote(this.mBlockQuote, this.mBlockQuoteDecoratorResId);
            paragraph.setIsBulletItem(this.mShowBullet);
            CharSequence text = iterator.next();
            if (trimContent) {
                text = StringUtils.trimWhitespace(text);
            }
            paragraph.setGravity(getGravity());
            paragraph.setText(text);
            if (Font.SERIF.equals(getTypeface())) {
                paragraph.setQuote(true);
            }
            paragraphs.add(paragraph);
        }
        setParagraphList(paragraphs);
    }

    public void setParagraphText(CharSequence text) {
        setParagraphText(text, true);
    }

    public void setParagraphList(List<Paragraph> paragraphList) {
        this.mParagraphList = paragraphList;
        applyIndent();
        requestLayout();
        clearTouchableArray();
        triggerRedraw();
    }

    public void setParagraph(Paragraph paragraph) {
        if (paragraph != null) {
            List<Paragraph> paragraphList = new ArrayList();
            paragraphList.add(paragraph);
            setParagraphList(paragraphList);
        }
    }

    public CharSequence getText() {
        StringBuilder builder = new StringBuilder();
        if (this.mParagraphList != null) {
            for (Paragraph paragraph : this.mParagraphList) {
                builder.append(paragraph.getPrintableText());
            }
        }
        return builder.toString();
    }

    public void setFirstLineIndent(Indent indent) {
        if (this.mFirstLineIndent != indent) {
            this.mFirstLineIndent = indent;
            applyIndent();
            requestLayout();
            clearTouchableArray();
            triggerRedraw();
        }
    }

    private void applyIndent() {
        if (this.mParagraphList != null) {
            boolean indent;
            if (this.mFirstLineIndent == Indent.ALL) {
                indent = true;
            } else if (this.mFirstLineIndent == Indent.NONE) {
                indent = false;
            } else if (this.mParagraphList.size() > 1) {
                indent = true;
            } else {
                indent = false;
            }
            int i = 0;
            for (Paragraph paragraph : this.mParagraphList) {
                paragraph.setView(this);
                if (i == 0 && this.mFirstLineIndent == Indent.ALL_BUT_FIRST_PARAGRAPH) {
                    paragraph.setFirstLineIndent(false);
                } else {
                    paragraph.setFirstLineIndent(indent);
                }
                i++;
            }
        }
    }

    private void clearTouchableArray() {
        if (this.mTouchableArray != null) {
            this.mTouchableArray.clear();
            this.mTouchableArray = null;
        }
    }

    private void initTouchableArray() {
        if (this.mTouchableArray == null) {
            this.mTouchableArray = Collections.synchronizedList(new ArrayList());
        }
        this.mTouchableArray.clear();
        if (this.mParagraphList != null) {
            for (Paragraph paragraph : this.mParagraphList) {
                this.mTouchableArray.addAll(paragraph.getTouchableArray());
            }
        }
    }

    private Touchable findTouchableAtPoint(float x, float y) {
        if (this.mTouchableArray == null) {
            initTouchableArray();
        }
        Touchable clickedTouchable = null;
        for (Touchable touchable : this.mTouchableArray) {
            if (touchable.hotArea.contains(x, y)) {
                if (clickedTouchable == null) {
                    clickedTouchable = touchable;
                } else if (clickedTouchable.priority == touchable.priority) {
                    if (Utils.getDistance(x, y, touchable.hotArea.centerX(), touchable.hotArea.centerY()) > Utils.getDistance(x, y, clickedTouchable.hotArea.centerX(), clickedTouchable.hotArea.centerY())) {
                        clickedTouchable = touchable;
                    }
                } else if (clickedTouchable.priority < touchable.priority) {
                    clickedTouchable = touchable;
                }
            }
        }
        return clickedTouchable;
    }

    public void setVisibleLineCount(int count) {
        if (this.mVisibleLineCount != count) {
            this.mVisibleLineCount = count;
            requestLayout();
            clearTouchableArray();
            triggerRedraw();
        }
    }

    public void toggleExpandedStatus() {
        this.mExpanded = !this.mExpanded;
        requestLayout();
        clearTouchableArray();
        triggerRedraw();
    }

    public void setOnExpandStatusChangedListener(OnExpandStatusChangedListener listener) {
        this.mOnExpandStatusChangedListener = listener;
    }

    public int getLineCount() {
        int result = 0;
        if (this.mParagraphList != null) {
            for (Paragraph paragraph : this.mParagraphList) {
                result += paragraph.getLineCount();
            }
        }
        return result;
    }

    public void setTextColor(int color) {
        super.setTextColor(color);
        triggerRedraw();
    }

    @TargetApi(11)
    private void draw() {
        if (this.mBitmap != null) {
            this.mBitmap.recycle();
        }
        if (this.mBitmapSlice != null) {
            for (Bitmap bitmap : this.mBitmapSlice) {
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            this.mBitmapSlice = null;
        }
        float bitmapWidth = (float) ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        float bitmapHeight = (float) ((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
        if (bitmapWidth > 0.0f && bitmapHeight > 0.0f) {
            this.mBitmap = Bitmap.createBitmap((int) Math.ceil((double) bitmapWidth), (int) Math.ceil((double) bitmapHeight), Config.ARGB_8888);
            Canvas canvas = new Canvas(this.mBitmap);
            canvas.drawColor(0);
            float offsetY = 0.0f;
            if (this.mParagraphList != null) {
                for (Paragraph paragraph : this.mParagraphList) {
                    float nextOffset = offsetY + paragraph.getHeight();
                    paragraph.draw(canvas, offsetY);
                    offsetY = nextOffset;
                }
            }
            if (this.mLocalDrawingCacheEnabled) {
                int sliceCount = (int) Math.ceil((double) (bitmapHeight / 2048.0f));
                if (sliceCount > 1) {
                    this.mBitmapSlice = new Bitmap[sliceCount];
                    float width = bitmapWidth;
                    if (width > 2048.0f) {
                        width = 2048.0f;
                    }
                    for (int i = 0; i < sliceCount; i++) {
                        float top = (float) (i * BITMAP_SLICE_MAX_SIZE);
                        float height = 2048.0f;
                        if (top + 2048.0f > bitmapHeight) {
                            height = bitmapHeight - top;
                        }
                        this.mBitmapSlice[i] = Bitmap.createBitmap(this.mBitmap, 0, (int) top, (int) width, (int) height);
                    }
                    this.mBitmap.recycle();
                }
            }
            if (this.mBitmapSlice == null) {
                this.mBitmapSlice = new Bitmap[1];
                this.mBitmapSlice[0] = this.mBitmap;
            }
        }
    }

    private void triggerRedraw() {
        invalidate();
        this.mInvalidated = true;
    }

    private void checkAndEnableDrawingCache() {
        if (VERSION.SDK_INT >= 17 && isHardwareAccelerated()) {
            return;
        }
        if (VERSION.SDK_INT >= 11) {
            this.mLocalDrawingCacheEnabled = true;
        } else {
            setDrawingCacheEnabled(true);
        }
    }

    private float getHalfLineSpacing() {
        return (((float) getLineHeight()) - getTextSize()) / 2.0f;
    }
}
