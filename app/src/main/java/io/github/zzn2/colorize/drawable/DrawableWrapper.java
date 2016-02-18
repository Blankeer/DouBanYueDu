package io.github.zzn2.colorize.drawable;

import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import io.fabric.sdk.android.services.common.AbstractSpiCall;

public class DrawableWrapper extends ClipDrawable {
    public DrawableWrapper(Drawable drawable) {
        super(drawable, 17, 1);
        setLevel(AbstractSpiCall.DEFAULT_TIMEOUT);
    }
}
