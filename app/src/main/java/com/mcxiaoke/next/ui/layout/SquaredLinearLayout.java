package com.mcxiaoke.next.ui.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquaredLinearLayout extends LinearLayout {
    public SquaredLinearLayout(Context context) {
        super(context);
    }

    public SquaredLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public SquaredLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalMeasureSpec = Utils.getSquaredMeasureSpec(this, widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(finalMeasureSpec, finalMeasureSpec);
    }
}
