package com.mcxiaoke.next.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;

public class CheckableFrameLayout extends FrameLayout implements Checkable {
    private static final int[] CheckedStateSet;
    private boolean mChecked;

    static {
        CheckedStateSet = new int[]{16842912};
    }

    public CheckableFrameLayout(Context context) {
        super(context);
        this.mChecked = false;
    }

    public CheckableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mChecked = false;
    }

    public CheckableFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mChecked = false;
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public void setChecked(boolean b) {
        if (b != this.mChecked) {
            this.mChecked = b;
            refreshDrawableState();
        }
    }

    public void toggle() {
        setChecked(!this.mChecked);
    }

    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CheckedStateSet);
        }
        return drawableState;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }
}
