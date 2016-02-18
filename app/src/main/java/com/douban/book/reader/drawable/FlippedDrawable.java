package com.douban.book.reader.drawable;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class FlippedDrawable extends DrawableWrapper {
    private boolean mIsVerticalFlip;

    public FlippedDrawable(Drawable drawable) {
        super(drawable);
    }

    public FlippedDrawable verticalFlip() {
        this.mIsVerticalFlip = true;
        return this;
    }

    public void draw(Canvas canvas) {
        canvas.save();
        if (this.mIsVerticalFlip) {
            canvas.scale(1.0f, -1.0f, 0.0f, ((float) getBounds().height()) * 0.5f);
        } else {
            canvas.scale(-1.0f, 1.0f, ((float) getBounds().width()) * 0.5f, 0.0f);
        }
        super.draw(canvas);
        canvas.restore();
    }
}
