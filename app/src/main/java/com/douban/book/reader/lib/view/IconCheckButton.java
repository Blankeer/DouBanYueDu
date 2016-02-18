package com.douban.book.reader.lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.CompoundButton;
import com.douban.book.reader.R;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;

public class IconCheckButton extends CompoundButton {
    private static final float CORNER_RADIUS;
    private static final int MIN_DRAWABLE_SIZE;
    private Drawable mButtonDrawable;
    private boolean mDrawStroke;

    static {
        CORNER_RADIUS = Res.getDimension(R.dimen.btn_round_corner_radius);
        MIN_DRAWABLE_SIZE = Utils.dp2pixel(30.0f);
    }

    public IconCheckButton(Context context) {
        super(context);
        this.mDrawStroke = true;
    }

    public IconCheckButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDrawStroke = true;
    }

    public IconCheckButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDrawStroke = true;
    }

    @Nullable
    public void setButtonDrawable(Drawable drawable) {
        this.mButtonDrawable = drawable;
        super.setButtonDrawable(drawable);
    }

    public void setDrawStroke(boolean drawStroke) {
        this.mDrawStroke = drawStroke;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) == 0 || MeasureSpec.getMode(heightMeasureSpec) == 0) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            if (this.mButtonDrawable != null) {
                setMeasuredDimension(Math.max((Math.max(this.mButtonDrawable.getIntrinsicWidth(), MIN_DRAWABLE_SIZE) + getPaddingLeft()) + getPaddingRight(), width), Math.max((Math.max(this.mButtonDrawable.getIntrinsicHeight(), MIN_DRAWABLE_SIZE) + getPaddingTop()) + getPaddingBottom(), height));
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        float centerX = ((float) getWidth()) / 2.0f;
        float centerY = ((float) getHeight()) / 2.0f;
        float radius = ((float) Math.min(getWidth(), getHeight())) * 0.5f;
        if (this.mDrawStroke) {
            Drawable checkMark = Res.getDrawable(R.drawable.ic_checkmark);
            if (checkMark != null) {
                radius -= ((float) checkMark.getIntrinsicWidth()) * 0.5f;
            }
            RectF boundary = CanvasUtils.rectFromCenterAndRadius(centerX, centerY, radius);
            Paint paint = PaintUtils.obtainStrokePaint(Res.getColor(isChecked() ? R.array.green : R.array.btn_stroke_color));
            canvas.drawRoundRect(boundary, CORNER_RADIUS, CORNER_RADIUS, paint);
            if (isChecked()) {
                PaintUtils.applyNightModeMaskIfNeeded(paint);
                CanvasUtils.drawDrawableCenteredOnPoint(canvas, checkMark, (radius * 0.9f) + centerX, (radius * 0.9f) + centerY);
            }
            PaintUtils.recyclePaint(paint);
        }
        if (this.mButtonDrawable != null) {
            this.mButtonDrawable.setColorFilter(Theme.isNight() ? ThemedUtils.NIGHT_MODE_COLOR_FILTER : null);
            CanvasUtils.drawDrawableCenteredInArea(canvas, this.mButtonDrawable, CanvasUtils.rectFromCenterAndRadius(centerX, centerY, Paragraph.CODE_TEXTSIZE_RATIO * radius));
        }
    }
}
