package com.douban.book.reader.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class StoreViewPager extends ViewPager {
    public StoreViewPager(Context context) {
        super(context);
    }

    public StoreViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean canScrollHorizontally(int direction) {
        return true;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, 0));
            height = Math.max(height, child.getMeasuredHeight());
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, 1073741824));
    }
}
