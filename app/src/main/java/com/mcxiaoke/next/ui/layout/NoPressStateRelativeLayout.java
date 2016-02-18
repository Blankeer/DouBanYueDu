package com.mcxiaoke.next.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class NoPressStateRelativeLayout extends RelativeLayout {
    public NoPressStateRelativeLayout(Context context) {
        super(context);
    }

    public NoPressStateRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPressed(boolean pressed) {
    }
}
