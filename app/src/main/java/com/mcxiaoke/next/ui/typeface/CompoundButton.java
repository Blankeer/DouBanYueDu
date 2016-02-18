package com.mcxiaoke.next.ui.typeface;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CompoundButton extends android.widget.CompoundButton {
    public CompoundButton(Context context) {
        super(context);
    }

    public CompoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            FontCache.getInstance().setFont((TextView) this, attrs);
        }
    }

    public void setFont(String fontPath) {
        FontCache.getInstance().setFont((TextView) this, fontPath);
    }

    public void setFont(int resId) {
        setFont(getContext().getString(resId));
    }
}
