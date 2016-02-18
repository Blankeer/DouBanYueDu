package com.douban.book.reader.content.paragraph.decorator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;

public class IconDecorator extends Decorator {
    private int mColorRes;
    private int mIconRes;
    private float mIconSizeRatio;
    private float mMarginRatio;
    private float mVerticalOffsetRatio;

    public IconDecorator() {
        this.mIconSizeRatio = 1.0f;
    }

    public IconDecorator icon(@DrawableRes @ArrayRes int iconRes) {
        this.mIconRes = iconRes;
        return this;
    }

    public IconDecorator color(@ColorRes @ArrayRes int color) {
        this.mColorRes = color;
        return this;
    }

    public IconDecorator marginWithText(float marginRatio) {
        this.mMarginRatio = marginRatio;
        return this;
    }

    public IconDecorator iconSizeRatio(float sizeRatio) {
        this.mIconSizeRatio = sizeRatio;
        return this;
    }

    public IconDecorator verticalOffsetRatio(float offsetRatio) {
        this.mVerticalOffsetRatio = offsetRatio;
        return this;
    }

    public float getInsetLeft() {
        return getParagraph().getTextSize() * (1.0f + this.mMarginRatio);
    }

    public void draw(Canvas canvas, int startLine, int endLine) {
        if (startLine == 0) {
            Paragraph paragraph = getParagraph();
            float textSize = paragraph.getTextSize();
            Paint paint = PaintUtils.obtainPaint(textSize);
            paint.setColor(paragraph.getTextColor());
            CanvasUtils.drawDrawableCenteredInArea(canvas, Res.getDrawableWithTint(this.mIconRes, this.mColorRes), CanvasUtils.rectFromCenterAndRadius(textSize / 2.0f, (((textSize - paint.getFontMetrics().bottom) / 2.0f) + paragraph.getPaddingTop()) + (this.mVerticalOffsetRatio * textSize), (this.mIconSizeRatio * textSize) / 2.0f));
            PaintUtils.recyclePaint(paint);
        }
    }
}
