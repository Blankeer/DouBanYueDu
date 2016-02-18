package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.douban.book.reader.R;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;

public class DoubanLogoView extends BaseThemedView {
    private static final int PADDING;
    private final RectF mBounds;
    private float mCornerRadius;
    private final Rect mDrawableBounds;

    static {
        PADDING = Utils.dp2pixel(2.0f);
    }

    public DoubanLogoView(Context context) {
        super(context);
        this.mBounds = new RectF();
        this.mDrawableBounds = new Rect();
    }

    public DoubanLogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBounds = new RectF();
        this.mDrawableBounds = new Rect();
    }

    public DoubanLogoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBounds = new RectF();
        this.mDrawableBounds = new Rect();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        int radius = (Math.min(width, height) / 2) - PADDING;
        int drawableRadius = Math.round(((float) radius) * 0.7f);
        int centerX = width / 2;
        int centerY = height / 2;
        this.mBounds.set((float) (centerX - radius), (float) (centerY - radius), (float) (centerX + radius), (float) (centerY + radius));
        this.mDrawableBounds.set(centerX - drawableRadius, centerY - drawableRadius, centerX + drawableRadius, centerY + drawableRadius);
        this.mCornerRadius = ((float) radius) * 0.5f;
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Res.getColor(R.array.page_bg_color));
        Paint paint = PaintUtils.obtainStrokePaint(Res.getColor(R.array.btn_stroke_color));
        canvas.drawRoundRect(this.mBounds, this.mCornerRadius, this.mCornerRadius, paint);
        PaintUtils.recyclePaint(paint);
        Drawable logo = Res.getDrawable(R.drawable.v_douban);
        if (logo != null) {
            logo.setBounds(this.mDrawableBounds);
            logo.setColorFilter(Theme.isNight() ? ThemedUtils.NIGHT_MODE_COLOR_FILTER : null);
            logo.draw(canvas);
        }
    }
}
