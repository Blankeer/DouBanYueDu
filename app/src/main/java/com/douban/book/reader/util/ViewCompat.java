package com.douban.book.reader.util;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.os.Build.VERSION;
import android.widget.AbsSeekBar;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ViewCompat {
    private static ViewCompatImpl IMPL;

    private static class ViewCompatImpl {
        private ViewCompatImpl() {
        }

        public ColorFilter getColorFilter(ImageView view) {
            return null;
        }

        public void setProgressBarTint(ProgressBar view, ColorStateList tint, Mode mode) {
        }
    }

    @TargetApi(16)
    private static class ViewCompatImpl16 extends ViewCompatImpl {
        private ViewCompatImpl16() {
            super();
        }

        public ColorFilter getColorFilter(ImageView view) {
            if (view != null) {
                return view.getColorFilter();
            }
            return null;
        }
    }

    @TargetApi(21)
    private static class ViewCompatImpl21 extends ViewCompatImpl16 {
        private ViewCompatImpl21() {
            super();
        }

        public void setProgressBarTint(ProgressBar view, ColorStateList tint, Mode mode) {
            view.setProgressBackgroundTintMode(mode);
            view.setProgressBackgroundTintList(tint);
            view.setProgressTintMode(mode);
            view.setProgressTintList(tint);
            view.setIndeterminateTintMode(mode);
            view.setIndeterminateTintList(tint);
            if (view instanceof AbsSeekBar) {
                ((AbsSeekBar) view).setThumbTintMode(mode);
                ((AbsSeekBar) view).setThumbTintList(tint);
            }
        }
    }

    static {
        int version = VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new ViewCompatImpl21();
        } else if (version >= 16) {
            IMPL = new ViewCompatImpl16();
        } else {
            IMPL = new ViewCompatImpl();
        }
    }

    public static ColorFilter getColorFilter(ImageView view) {
        return IMPL.getColorFilter(view);
    }

    public static void setProgressBarTint(ProgressBar view, ColorStateList tint, Mode mode) {
        IMPL.setProgressBarTint(view, tint, mode);
    }
}
