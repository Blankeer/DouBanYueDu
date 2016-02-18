package com.mcxiaoke.next.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class NoPressStateFrameLayout extends FrameLayout {
    public NoPressStateFrameLayout(Context context) {
        super(context);
    }

    public NoPressStateFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPressed(boolean pressed) {
    }
}
