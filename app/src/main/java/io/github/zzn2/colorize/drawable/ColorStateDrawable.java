package io.github.zzn2.colorize.drawable;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;

public class ColorStateDrawable extends DrawableWrapper {
    private ColorStateList mColorStateList;

    public ColorStateDrawable(Drawable drawable, ColorStateList colorStateList) {
        super(drawable.mutate());
        this.mColorStateList = colorStateList;
    }

    public void updateColor(ColorStateList newColor) {
        this.mColorStateList = newColor;
        updateColorFilter();
    }

    public boolean isStateful() {
        return true;
    }

    protected boolean onStateChange(int[] states) {
        updateColorFilter();
        return super.onStateChange(states);
    }

    private void updateColorFilter() {
        if (this.mColorStateList != null) {
            setColorFilter(this.mColorStateList.getColorForState(getState(), -1), Mode.MULTIPLY);
        }
    }
}
