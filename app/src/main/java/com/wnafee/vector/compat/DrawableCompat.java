package com.wnafee.vector.compat;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.support.annotation.NonNull;
import android.support.v4.media.TransportMediator;
import android.util.AttributeSet;
import com.alipay.sdk.protocol.h;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dj;
import u.aly.dx;

public abstract class DrawableCompat extends Drawable {
    int mLayoutDirection;

    public static abstract class ConstantStateCompat extends ConstantState {
        public boolean canApplyTheme() {
            return false;
        }
    }

    public boolean canApplyTheme() {
        return false;
    }

    protected static TypedArray obtainAttributes(Resources res, Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    public void getOutline(@NonNull Outline outline) {
        outline.setRect(getBounds());
        outline.setAlpha(0.0f);
    }

    public void setHotspot(float x, float y) {
    }

    public void setHotspotBounds(int left, int top, int right, int bottom) {
    }

    public void getHotspotBounds(Rect outRect) {
        outRect.set(getBounds());
    }

    public int getLayoutDirection() {
        return this.mLayoutDirection;
    }

    public void setLayoutDirection(int layoutDirection) {
        if (getLayoutDirection() != layoutDirection) {
            this.mLayoutDirection = layoutDirection;
        }
    }

    PorterDuffColorFilter updateTintFilter(PorterDuffColorFilter tintFilter, ColorStateList tint, Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }
        return new PorterDuffColorFilter(tint.getColorForState(getState(), 0), tintMode);
    }

    public static Mode parseTintMode(int value, Mode defaultMode) {
        switch (value) {
            case dx.d /*3*/:
                return Mode.SRC_OVER;
            case dj.f /*5*/:
                return Mode.SRC_IN;
            case h.h /*9*/:
                return Mode.SRC_ATOP;
            case R.styleable.StickyListHeadersListView_android_cacheColorHint /*14*/:
                return Mode.MULTIPLY;
            case R.styleable.StickyListHeadersListView_android_divider /*15*/:
                return Mode.SCREEN;
            case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                return Mode.ADD;
            default:
                return defaultMode;
        }
    }
}
