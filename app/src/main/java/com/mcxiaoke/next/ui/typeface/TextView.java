package com.mcxiaoke.next.ui.typeface;

import android.content.Context;
import android.util.AttributeSet;

public class TextView extends android.widget.TextView {
    public TextView(Context context) {
        super(context);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            FontCache.getInstance().setFont((android.widget.TextView) this, attrs);
        }
    }

    public void setFont(String fontPath) {
        FontCache.getInstance().setFont((android.widget.TextView) this, fontPath);
    }

    public void setFont(int resId) {
        setFont(getContext().getString(resId));
    }
}
