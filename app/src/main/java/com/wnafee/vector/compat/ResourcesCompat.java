package com.wnafee.vector.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;

public class ResourcesCompat {
    public static final boolean LOLLIPOP;

    static {
        LOLLIPOP = VERSION.SDK_INT >= 21;
    }

    @TargetApi(21)
    public static Drawable getDrawable(Context c, int resId) {
        try {
            if (LOLLIPOP) {
                return c.getResources().getDrawable(resId, null);
            }
            return c.getResources().getDrawable(resId);
        } catch (NotFoundException e) {
            try {
                return VectorDrawable.getDrawable(c, resId);
            } catch (IllegalArgumentException e2) {
                try {
                    return AnimatedVectorDrawable.getDrawable(c, resId);
                } catch (IllegalArgumentException e3) {
                    throw e;
                }
            }
        }
    }
}
