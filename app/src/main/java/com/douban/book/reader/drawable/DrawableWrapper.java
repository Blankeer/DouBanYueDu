package com.douban.book.reader.drawable;

import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import io.fabric.sdk.android.services.common.AbstractSpiCall;

public class DrawableWrapper extends ClipDrawable {
    private Drawable mWrappedDrawable;

    public DrawableWrapper(Drawable drawable) {
        super(drawable, 17, 1);
        setLevel(AbstractSpiCall.DEFAULT_TIMEOUT);
        this.mWrappedDrawable = drawable;
    }

    public Drawable getWrappedDrawable() {
        return this.mWrappedDrawable;
    }
}
