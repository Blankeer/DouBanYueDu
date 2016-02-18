package com.douban.book.reader.drawable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import com.douban.book.reader.R;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewStateUtils;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public class RoundCornerColorDrawable extends ColorDrawable {
    private static final int DEFAULT_CORNER_RADIUS;
    private static final int DEFAULT_STROKE_COLOR;
    private static final int DEFAULT_STROKE_WIDTH = 0;
    private int mRadius;
    private int mStrokeColor;
    private boolean mStrokeEnabled;
    private int mStrokeWidth;

    static {
        DEFAULT_STROKE_COLOR = Res.getColor(R.array.btn_stroke_color);
        DEFAULT_CORNER_RADIUS = Res.getDimensionPixelSize(R.dimen.btn_round_corner_radius);
    }

    public static RoundCornerColorDrawable create(Context context, AttributeSet attributeSet, int defaultColor, int defaultStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.RoundCorner, defaultStyleAttr, 0);
        if (a != null) {
            if (a.getBoolean(1, false)) {
                int color = a.getColor(0, defaultColor);
                if (color != 1) {
                    return new RoundCornerColorDrawable(color).setStrokeEnabled(a.getBoolean(3, false)).setStrokeColor(a.getColor(4, DEFAULT_STROKE_COLOR)).setStrokeWidth(a.getDimensionPixelSize(5, 0)).setCornerRadius(a.getDimensionPixelSize(2, DEFAULT_CORNER_RADIUS));
                }
            }
            a.recycle();
        }
        return null;
    }

    public RoundCornerColorDrawable(int color) {
        super(color);
        this.mStrokeEnabled = false;
        this.mStrokeColor = DEFAULT_STROKE_COLOR;
        this.mStrokeWidth = 0;
        this.mRadius = DEFAULT_CORNER_RADIUS;
    }

    public RoundCornerColorDrawable setStrokeEnabled(boolean strokeEnabled) {
        this.mStrokeEnabled = strokeEnabled;
        return this;
    }

    public RoundCornerColorDrawable setStrokeColor(int color) {
        this.mStrokeColor = color;
        return this;
    }

    public RoundCornerColorDrawable setStrokeWidth(int widthPixel) {
        this.mStrokeWidth = widthPixel;
        return this;
    }

    public RoundCornerColorDrawable setCornerRadius(int radius) {
        this.mRadius = radius;
        return this;
    }

    public int getOpacity() {
        return -3;
    }

    public void draw(Canvas canvas) {
        int height = getBounds().height();
        int width = getBounds().width();
        Paint paint = PaintUtils.obtainPaint();
        RectF rectF = new RectF(0.0f, 0.0f, (float) width, (float) height);
        paint.setColor(getColor());
        canvas.drawRoundRect(rectF, (float) this.mRadius, (float) this.mRadius, paint);
        PaintUtils.recyclePaint(paint);
        if (this.mStrokeEnabled) {
            float inset = this.mStrokeWidth > 0 ? ((float) this.mStrokeWidth) * 0.5f : 1.0f;
            rectF.inset(inset, inset);
            paint = PaintUtils.obtainPaint();
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth((float) this.mStrokeWidth);
            paint.setColor(this.mStrokeColor);
            canvas.drawRoundRect(rectF, (float) this.mRadius, (float) this.mRadius, paint);
            PaintUtils.recyclePaint(paint);
        }
    }

    public boolean isStateful() {
        return true;
    }

    protected boolean onStateChange(int[] states) {
        if (ViewStateUtils.isSelectedOrPressed(states)) {
            setAlpha(Header.MA_VAR);
            return true;
        } else if (ViewStateUtils.isDisabled(states)) {
            setAlpha(64);
            return true;
        } else {
            setAlpha(SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
            return super.onStateChange(states);
        }
    }
}
