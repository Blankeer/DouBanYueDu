package com.douban.book.reader.lib.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class DraggableLayout extends FrameLayout {
    private ViewDragHelper mDragHelper;
    private DragListener mDragListener;

    public interface DragListener {
        void onPositionChanged(float f);
    }

    private class HorizontalDragHelperCallback extends Callback {
        private float mCurrentRatio;
        private float mLeftBoundRatio;
        private float mRightBoundRatio;

        private HorizontalDragHelperCallback() {
            this.mLeftBoundRatio = 0.0f;
            this.mRightBoundRatio = 1.0f;
            this.mCurrentRatio = this.mLeftBoundRatio;
        }

        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int leftBound = getPosition(this.mLeftBoundRatio);
            return Math.min(Math.max(left, leftBound), getPosition(this.mRightBoundRatio));
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            this.mCurrentRatio = ((float) left) / ((float) DraggableLayout.this.getWidth());
            if (DraggableLayout.this.mDragListener != null) {
                DraggableLayout.this.mDragListener.onPositionChanged(this.mCurrentRatio);
            }
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            float targetRatio = Math.abs(xvel) < 10.0f ? this.mCurrentRatio < (this.mRightBoundRatio - this.mLeftBoundRatio) / 2.0f ? this.mLeftBoundRatio : this.mRightBoundRatio : xvel < 0.0f ? this.mLeftBoundRatio : this.mRightBoundRatio;
            DraggableLayout.this.mDragHelper.settleCapturedViewAt(getPosition(targetRatio), releasedChild.getTop());
            DraggableLayout.this.invalidate();
        }

        public int getViewHorizontalDragRange(View child) {
            return getPosition(this.mRightBoundRatio - this.mLeftBoundRatio);
        }

        private int getPosition(float ratio) {
            return Math.round(((float) DraggableLayout.this.getWidth()) * ratio);
        }
    }

    public DraggableLayout(Context context) {
        this(context, null);
    }

    public DraggableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDragHelper = ViewDragHelper.create(this, new HorizontalDragHelperCallback());
        setClipToPadding(false);
    }

    public void setDragListener(DragListener listener) {
        this.mDragListener = listener;
    }

    public void computeScroll() {
        if (this.mDragHelper != null && this.mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action != 3 && action != 1) {
            return this.mDragHelper.shouldInterceptTouchEvent(ev);
        }
        this.mDragHelper.cancel();
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        this.mDragHelper.processTouchEvent(ev);
        return true;
    }
}
