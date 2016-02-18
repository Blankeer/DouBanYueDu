package com.douban.book.reader.drawable;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import com.douban.book.reader.R;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ThemedUtils;

public class BadgeDrawable extends DrawableWrapper {
    private Drawable mBadge;
    private int mBadgeGravity;
    private float mBadgeHorizontalOffsetRatio;
    private float mBadgeVerticalOffsetRatio;
    private float mOffsetRatio;
    private boolean mShowBadge;

    public BadgeDrawable(Drawable drawable) {
        super(drawable);
        this.mBadge = Res.getDrawable(R.drawable.ic_checkmark);
        this.mBadgeGravity = 85;
        this.mShowBadge = true;
        this.mOffsetRatio = 0.0f;
        this.mBadgeHorizontalOffsetRatio = 0.0f;
        this.mBadgeVerticalOffsetRatio = 0.0f;
    }

    public BadgeDrawable(@DrawableRes int resId) {
        this(Res.getDrawable(resId));
    }

    public BadgeDrawable showBadge(boolean show) {
        this.mShowBadge = show;
        return this;
    }

    public BadgeDrawable gravity(int gravity) {
        this.mBadgeGravity = gravity;
        return this;
    }

    public BadgeDrawable offsetWhenBadged(float offsetRatio) {
        this.mOffsetRatio = offsetRatio;
        return this;
    }

    public BadgeDrawable badgeHorizontalOffset(float offsetRatio) {
        this.mBadgeHorizontalOffsetRatio = offsetRatio;
        return this;
    }

    public BadgeDrawable badgeVerticalOffset(float offsetRatio) {
        this.mBadgeVerticalOffsetRatio = offsetRatio;
        return this;
    }

    public void draw(Canvas canvas) {
        canvas.save();
        if (this.mShowBadge && this.mBadge != null) {
            canvas.translate((float) Math.round(this.mOffsetRatio * ((float) getBounds().width())), 0.0f);
        }
        super.draw(canvas);
        if (this.mShowBadge && this.mBadge != null) {
            int drawableSize = getBounds().width();
            this.mBadge.setColorFilter(Theme.isNight() ? ThemedUtils.NIGHT_MODE_COLOR_FILTER : null);
            float badgeOffset = ((float) drawableSize) * 0.25f;
            float centerX = ((float) drawableSize) * (this.mBadgeHorizontalOffsetRatio + 0.5f);
            float centerY = ((float) drawableSize) * (this.mBadgeVerticalOffsetRatio + 0.5f);
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
        canvas.restore();
    }
}
