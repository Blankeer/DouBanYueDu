package com.douban.book.reader.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class WrapContentHeightViewPager extends ViewPager {
    public WrapContentHeightViewPager(Context context) {
        super(context);
    }

    public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View maxHeightChild = null;
        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);
            if (child != null) {
                child.measure(widthMeasureSpec, heightMeasureSpec);
                if (0 < child.getMeasuredHeight()) {
                    maxHeightChild = child;
                }
            }
        }
        setMeasuredDimension(getMeasuredWidth(), measureHeight(heightMeasureSpec, maxHeightChild));
    }

    private int measureHeight(int measureSpec, View view) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == 1073741824) {
            return specSize;
        }
        if (view != null) {
            result = view.getMeasuredHeight();
        }
        if (specMode == ExploreByTouchHelper.INVALID_ID) {
            return Math.min(result, specSize);
        }
        return result;
    }
}
