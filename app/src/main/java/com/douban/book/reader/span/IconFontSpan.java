package com.douban.book.reader.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.text.style.DynamicDrawableSpan;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.drawable.FlippedDrawable;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ThemedUtils;

public class IconFontSpan extends DynamicDrawableSpan {
    private Drawable mBadge;
    private int mBadgeGravity;
    private Drawable mDrawable;
    private int mIconColorResId;
    private float mPaddingRightRatio;
    private float mRatio;
    private boolean mUseOriginalColor;
    private float mVerticalOffsetRatio;

    public IconFontSpan(int resId) {
        super(1);
        this.mRatio = 1.0f;
        this.mBadgeGravity = 85;
        this.mDrawable = Res.getDrawable(resId);
    }

    public IconFontSpan flipped() {
        this.mDrawable = new FlippedDrawable(this.mDrawable);
        return this;
    }

    public IconFontSpan verticalFlipped() {
        this.mDrawable = new FlippedDrawable(this.mDrawable).verticalFlip();
        return this;
    }

    public IconFontSpan ratio(float ratio) {
        this.mRatio = ratio;
        return this;
    }

    public IconFontSpan paddingRight(float paddingRightRatio) {
        this.mPaddingRightRatio = paddingRightRatio;
        return this;
    }

    public IconFontSpan verticalOffsetRatio(float verticalOffsetRatio) {
        this.mVerticalOffsetRatio = verticalOffsetRatio;
        return this;
    }

    public IconFontSpan color(@ColorRes @ArrayRes int resId) {
        this.mIconColorResId = resId;
        return this;
    }

    public IconFontSpan useOriginalColor() {
        this.mUseOriginalColor = true;
        return this;
    }

    public IconFontSpan badge(Drawable badge) {
        this.mBadge = badge;
        return this;
    }

    public IconFontSpan badge(@DrawableRes int resId) {
        return badge(Res.getDrawable(resId));
    }

    public IconFontSpan badgeGravity(int gravity) {
        this.mBadgeGravity = gravity;
        return this;
    }

    public Drawable getDrawable() {
        return this.mDrawable;
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        return Math.round(paint.getTextSize() * (this.mRatio + this.mPaddingRightRatio));
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        FontMetrics metrics = paint.getFontMetrics();
        float textSize = paint.getTextSize() * this.mRatio;
        int intTextSize = Math.round(textSize);
        float transY = ((((float) y) + metrics.ascent) + ((((float) (bottom - top)) - textSize) * 0.5f)) + (this.mVerticalOffsetRatio * textSize);
        canvas.save();
        canvas.translate(x, transY);
        if (this.mDrawable != null) {
            if (!this.mUseOriginalColor) {
                this.mDrawable.setColorFilter(this.mIconColorResId > 0 ? Res.getColor(this.mIconColorResId) : paint.getColor(), Mode.SRC_IN);
            }
            this.mDrawable.setBounds(0, 0, intTextSize, intTextSize);
            this.mDrawable.draw(canvas);
        }
        if (this.mBadge != null) {
            this.mBadge.setColorFilter(Theme.isNight() ? ThemedUtils.NIGHT_MODE_COLOR_FILTER : null);
            float badgeOffset = ((float) intTextSize) * 0.25f;
            float centerX = ((float) intTextSize) * 0.5f;
            float centerY = ((float) intTextSize) * 0.5f;
            int xShift = (this.mBadgeGravity >> 0) & 6;
            if (xShift == 2) {
                centerX -= badgeOffset;
            } else if (xShift == 4) {
                centerX += badgeOffset;
            }
            int yShift = (this.mBadgeGravity >> 4) & 6;
            if (yShift == 2) {
                centerY -= badgeOffset;
            } else if (yShift == 4) {
                centerY += badgeOffset;
            }
            CanvasUtils.drawDrawableCenteredOnPoint(canvas, this.mBadge, centerX, centerY);
        }
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_SVG_BORDER)) {
            Paint tempPaint = PaintUtils.obtainStrokePaint();
            canvas.drawRect(this.mDrawable.getBounds(), tempPaint);
            PaintUtils.recyclePaint(tempPaint);
        }
        canvas.restore();
    }
}
