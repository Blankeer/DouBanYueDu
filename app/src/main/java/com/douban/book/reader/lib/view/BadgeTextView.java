package com.douban.book.reader.lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;

public class BadgeTextView extends TextView {
    private static final int BADGE_RADIUS_INNER;
    private static final int BADGE_RADIUS_OUTER;
    private float mBadgeCenterX;
    private float mBadgeCenterY;
    private boolean mBadgeVisible;

    static {
        BADGE_RADIUS_OUTER = Utils.dp2pixel(5.0f);
        BADGE_RADIUS_INNER = Utils.dp2pixel(4.0f);
    }

    public BadgeTextView(Context context) {
        super(context);
    }

    public BadgeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BadgeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBadgeCenterXY(float x, float y) {
        this.mBadgeCenterX = x;
        this.mBadgeCenterY = y;
        invalidate();
    }

    public void setBadgeVisible(boolean badgeVisible) {
        this.mBadgeVisible = badgeVisible;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mBadgeVisible) {
            Drawable[] drawables = getCompoundDrawables();
            if (drawables[0] != null) {
                Rect rect = drawables[0].getBounds();
                if (this.mBadgeCenterX <= 0.0f) {
                    this.mBadgeCenterX = ((float) rect.right) - (((float) rect.width()) / 4.0f);
                }
                if (this.mBadgeCenterY <= 0.0f) {
                    this.mBadgeCenterY = ((float) rect.top) + (((float) rect.height()) / 4.0f);
                }
                Paint paint = PaintUtils.obtainPaint();
                paint.setColor(Res.getColor(R.array.light_stroke_color));
                canvas.drawCircle(this.mBadgeCenterX, this.mBadgeCenterY, (float) BADGE_RADIUS_OUTER, paint);
                paint.setColor(Res.getColor(R.array.red));
                canvas.drawCircle(this.mBadgeCenterX, this.mBadgeCenterY, (float) BADGE_RADIUS_INNER, paint);
                PaintUtils.recyclePaint(paint);
            }
        }
    }
}
