package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

public class WrapSeekBar extends SeekBar {
    private static final String TAG = "WrapSeekBar";
    private int mMax;
    private int mProgress;

    public WrapSeekBar(Context context) {
        super(context);
    }

    public WrapSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public synchronized void setMax(int max) {
        if (VERSION.SDK_INT <= 11) {
            super.setMax(-1);
        }
        super.setMax(max);
        this.mMax = max;
    }

    public synchronized void setProgress(int progress) {
        super.setProgress(-1);
        super.setProgress(progress);
        this.mProgress = progress;
    }

    public void setProgressDrawable(Drawable drawable) {
        if (VERSION.SDK_INT <= 11) {
            if (getProgressDrawable() == null) {
                super.setProgressDrawable(drawable);
            }
            Rect bounds = getProgressDrawable().getBounds();
            super.setProgressDrawable(drawable);
            getProgressDrawable().setBounds(bounds);
            setProgress(this.mProgress);
            return;
        }
        super.setProgressDrawable(drawable);
        setProgress(this.mProgress);
    }

    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == 0 && !isEnabled()) {
            setEnabled(false);
        }
    }
}
