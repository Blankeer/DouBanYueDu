package com.douban.book.reader.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.GridView;

public class ExpandableHeightGridView extends GridView {
    boolean expanded;

    public ExpandableHeightGridView(Context context) {
        super(context);
        this.expanded = false;
        init();
    }

    public ExpandableHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.expanded = false;
        init();
    }

    public ExpandableHeightGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.expanded = false;
        init();
    }

    private void init() {
        setOverScrollMode(2);
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isExpanded()) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(ViewCompat.MEASURED_SIZE_MASK, ExploreByTouchHelper.INVALID_ID));
            getLayoutParams().height = getMeasuredHeight();
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
