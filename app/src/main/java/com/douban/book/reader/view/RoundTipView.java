package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import com.douban.book.reader.R;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.CharUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;

public class RoundTipView extends BaseThemedView {
    private RectF mBounds;
    private boolean mShowTipTail;
    private CharSequence mText;
    private int mTipColorResId;

    public RoundTipView(Context context) {
        super(context);
        this.mTipColorResId = R.array.red;
        this.mBounds = new RectF();
    }

    public RoundTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTipColorResId = R.array.red;
        this.mBounds = new RectF();
    }

    public RoundTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTipColorResId = R.array.red;
        this.mBounds = new RectF();
    }

    public RoundTipView text(CharSequence text) {
        this.mText = text;
        return this;
    }

    public RoundTipView tipColorResId(@ColorRes @ArrayRes int resId) {
        this.mTipColorResId = resId;
        return this;
    }

    public RoundTipView showTipTail(boolean showTipTail) {
        this.mShowTipTail = showTipTail;
        return this;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float sizeX = (float) ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
        float sizeY = (float) ((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
        float radius = Math.min(sizeX, sizeY) * 0.5f;
        float centerX = ((float) getPaddingLeft()) + (sizeX * 0.5f);
        float centerY = ((float) getPaddingTop()) + (sizeY * 0.5f);
        this.mBounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    protected void onDraw(Canvas canvas) {
        float f;
        float centerX = this.mBounds.centerX();
        float centerY = this.mBounds.centerY();
        float width = this.mBounds.width();
        if (this.mShowTipTail) {
            f = CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
        } else {
            f = 0.5f;
        }
        float radius = width * f;
        Paint paint = PaintUtils.obtainPaint();
        paint.setColor(Res.getColor(this.mTipColorResId));
        paint.setStyle(Style.FILL_AND_STROKE);
        PaintUtils.applyNightModeMaskIfNeeded(paint);
        canvas.drawCircle(centerX, centerY, radius, paint);
        if (this.mShowTipTail) {
            Path path = new Path();
            path.moveTo(centerX - radius, centerY);
            path.lineTo(centerX, (this.mBounds.width() * 0.5f) + centerY);
            path.lineTo(centerX + radius, centerY);
            path.close();
            canvas.drawPath(path, paint);
        }
        paint.setColor(Res.getColor(R.array.invert_text_color));
        paint.setTextSize(Res.getDimension(R.dimen.general_font_size_large));
        paint.setTypeface(Font.SANS_SERIF_BOLD);
        CanvasUtils.drawTextCenteredOnPoint(canvas, paint, centerX, centerY, this.mText);
        PaintUtils.recyclePaint(paint);
    }
}
