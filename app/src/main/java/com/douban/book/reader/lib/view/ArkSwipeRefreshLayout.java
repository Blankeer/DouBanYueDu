package com.douban.book.reader.lib.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import u.aly.dx;

public class ArkSwipeRefreshLayout extends SwipeRefreshLayout {
    private float mPrevX;
    private View mReferenceView;
    private int mTouchSlop;

    public ArkSwipeRefreshLayout(Context context) {
        super(context);
        init();
    }

    public ArkSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setReferenceView(View view) {
        this.mReferenceView = view;
    }

    public boolean canChildScrollUp() {
        if (this.mReferenceView != null) {
            return ViewCompat.canScrollVertically(this.mReferenceView, -1);
        }
        return super.canChildScrollUp();
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case dx.a /*0*/:
                this.mPrevX = MotionEvent.obtain(event).getX();
                break;
            case dx.c /*2*/:
                if (Math.abs(event.getX() - this.mPrevX) > ((float) this.mTouchSlop)) {
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }
}
