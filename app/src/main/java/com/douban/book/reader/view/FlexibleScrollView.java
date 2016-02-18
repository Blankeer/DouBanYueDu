package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class FlexibleScrollView extends ScrollView {
    float mMaxHeight;

    public FlexibleScrollView(Context context) {
        super(context);
        this.mMaxHeight = 0.0f;
    }

    public FlexibleScrollView(Context context, AttributeSet attrset) {
        super(context, attrset);
        this.mMaxHeight = 0.0f;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (this.mMaxHeight > 0.0f && ((float) height) > this.mMaxHeight) {
            height = Math.round(this.mMaxHeight);
        }
        setMeasuredDimension(width, height);
    }

    public void setMaxHeight(float maxHeight) {
        this.mMaxHeight = maxHeight;
        requestLayout();
    }
}
