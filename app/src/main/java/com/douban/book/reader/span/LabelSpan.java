package com.douban.book.reader.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.support.annotation.ArrayRes;
import android.text.style.ReplacementSpan;
import com.douban.book.reader.R;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;

public class LabelSpan extends ReplacementSpan {
    private static final float FONT_SIZE_RATIO = 0.8f;
    private static final float LABEL_VERTICAL_PADDING;
    private static final float PADDING_RATIO = 0.25f;
    private int mBackgroundColorResId;
    private int mRadius;
    private int mTextColorResId;
    private int mWidth;

    public LabelSpan() {
        this.mBackgroundColorResId = -1;
        this.mTextColorResId = R.array.invert_text_color;
        this.mRadius = Utils.dp2pixel(1.0f);
        this.mWidth = 0;
    }

    static {
        LABEL_VERTICAL_PADDING = (float) Utils.dp2pixel(1.0f);
    }

    public LabelSpan backgroundColor(@ArrayRes int colorResId) {
        this.mBackgroundColorResId = colorResId;
        return this;
    }

    public LabelSpan textColor(@ArrayRes int colorResId) {
        this.mTextColorResId = colorResId;
        return this;
    }

    public LabelSpan noRoundCorner() {
        this.mRadius = 0;
        return this;
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        this.mWidth = Math.round((paint.measureText(text, start, end) * FONT_SIZE_RATIO) + ((paint.getTextSize() * PADDING_RATIO) * 2.0f));
        return this.mWidth;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        float centerX = x + (((float) this.mWidth) * 0.5f);
        float centerY = ((float) (top + bottom)) * 0.5f;
        float fontSize = paint.getTextSize();
        Paint innerPaint = PaintUtils.obtainPaint(paint.getTextSize() * FONT_SIZE_RATIO);
        if (this.mBackgroundColorResId > 0) {
            innerPaint.setColor(Res.getColor(this.mBackgroundColorResId));
            float radiusVertical = (0.5f * fontSize) + LABEL_VERTICAL_PADDING;
            canvas.drawRoundRect(new RectF(x, centerY - radiusVertical, ((float) this.mWidth) + x, centerY + radiusVertical), (float) this.mRadius, (float) this.mRadius, innerPaint);
        }
        innerPaint.setTypeface(Font.SANS_SERIF);
        innerPaint.setColor(Res.getColor(this.mTextColorResId));
        CanvasUtils.drawTextCenteredOnPoint(canvas, innerPaint, centerX, centerY, text.subSequence(start, end));
        PaintUtils.recyclePaint(innerPaint);
    }
}
