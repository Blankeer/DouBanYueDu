package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ArrayRes;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ScrollView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Dimen;
import com.douban.book.reader.content.HotArea;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.Utils;
import u.aly.dx;

public class BalloonFrame extends ScrollView {
    private static final int INDICATOR_HEIGHT;
    private static final int INDICATOR_WIDTH;
    private static final int MAX_HEIGHT;
    private static final int MAX_WIDTH;
    private static final int PADDING;
    private static final String TAG;
    private int mBgColorResId;
    private HotArea mHotArea;
    private int mIndicatorOffset;
    private boolean mIndicatorOnTop;
    LayoutParams mLayoutParams;
    private int mLeftMinMargin;
    private int mMaxHeight;
    private Path mPath;
    private boolean mShowIndicator;
    private int mTopMinMargin;

    static {
        TAG = BalloonFrame.class.getSimpleName();
        MAX_WIDTH = Utils.dp2pixel(500.0f);
        MAX_HEIGHT = Utils.dp2pixel(300.0f);
        INDICATOR_HEIGHT = Utils.dp2pixel(10.0f);
        INDICATOR_WIDTH = Utils.dp2pixel(20.0f);
        PADDING = INDICATOR_HEIGHT + Dimen.SHADOW_WIDTH;
    }

    public BalloonFrame(Context context) {
        super(context);
        this.mTopMinMargin = 0;
        this.mLeftMinMargin = 0;
        this.mMaxHeight = -1;
        this.mBgColorResId = R.array.page_highlight_bg_color;
        this.mHotArea = null;
        this.mIndicatorOnTop = false;
        this.mShowIndicator = true;
        this.mIndicatorOffset = 0;
        this.mPath = null;
        this.mLayoutParams = null;
        setWillNotDraw(false);
        setVerticalFadingEdgeEnabled(false);
        setOverScrollMode(2);
        setDrawAreaPadding(0, 0, 0, 0);
    }

    public void setBackgroundResource(@ArrayRes int resId) {
        this.mBgColorResId = resId;
    }

    public void setDrawAreaPadding(int left, int top, int right, int bottom) {
        setPadding(PADDING + left, PADDING + top, PADDING + right, PADDING + bottom);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mHotArea == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        if (this.mLayoutParams == null) {
            this.mLayoutParams = new LayoutParams(getLayoutParams());
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (this.mMaxHeight <= 0) {
            this.mMaxHeight = MAX_HEIGHT;
        }
        int height = Math.min(Math.min(this.mMaxHeight, getMeasuredHeight()), parentHeight);
        int width = Math.min(Math.min(MAX_WIDTH, getMeasuredWidth()), parentWidth);
        if ((this.mHotArea.getTop() - ((float) height)) + ((float) this.mIndicatorOffset) < ((float) this.mTopMinMargin)) {
            this.mIndicatorOnTop = true;
            this.mLayoutParams.y = ((int) this.mHotArea.getBottom()) + this.mIndicatorOffset;
        } else {
            this.mLayoutParams.y = (((int) this.mHotArea.getTop()) - height) - this.mIndicatorOffset;
            this.mIndicatorOnTop = false;
        }
        if (this.mLayoutParams.y + height > parentHeight) {
            this.mLayoutParams.x = (parentWidth - width) / 2;
            this.mLayoutParams.y = (parentHeight - height) / 2;
            this.mShowIndicator = false;
        } else {
            if ((parentWidth - width) / 2 > this.mLeftMinMargin) {
                this.mLayoutParams.x = Math.round((this.mIndicatorOnTop ? this.mHotArea.getCenterXOnBottom() : this.mHotArea.getCenterXOnTop()) - (((float) width) / 2.0f));
                if (this.mLayoutParams.x < this.mLeftMinMargin) {
                    this.mLayoutParams.x = this.mLeftMinMargin;
                } else if (this.mLayoutParams.x + width > parentWidth - this.mLeftMinMargin) {
                    this.mLayoutParams.x = (parentWidth - this.mLeftMinMargin) - width;
                }
            } else {
                this.mLayoutParams.x = (parentWidth - width) / 2;
            }
            this.mShowIndicator = true;
        }
        setLayoutParams(this.mLayoutParams);
        setMeasuredDimension(width, height);
        measureChildren(MeasureSpec.makeMeasureSpec(width, 1073741824), MeasureSpec.makeMeasureSpec(height, 0));
    }

    protected void onDraw(Canvas canvas) {
        if (this.mHotArea == null) {
            throw new IllegalStateException("setHotArea() must be called");
        }
        Paint paint = PaintUtils.obtainPaint();
        paint.setColor(Res.getColor(this.mBgColorResId));
        canvas.save();
        canvas.translate(0.0f, (float) getScrollY());
        Path path = getPath();
        CanvasUtils.drawPathShadow(canvas, path);
        canvas.drawPath(path, paint);
        canvas.restore();
        PaintUtils.recyclePaint(paint);
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        Logger.d(Tag.TOUCHEVENT, "BalloonFrame.onInterceptTouchEvent: " + event, new Object[0]);
        switch (event.getAction()) {
            case dx.a /*0*/:
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                    Logger.d(Tag.TOUCHEVENT, "BalloonFrame.parent.requestDisallowInterceptTouchEvent true", new Object[0]);
                    break;
                }
                break;
        }
        Logger.d(Tag.TOUCHEVENT, "BalloonFrame.onInterceptTouchEvent returned %s", Boolean.valueOf(super.onInterceptTouchEvent(event)));
        return super.onInterceptTouchEvent(event);
    }

    public void setHotArea(HotArea hotArea) {
        this.mHotArea = hotArea;
    }

    public void setHotArea(View view) {
        Rect rect = new Rect();
        if (view != null) {
            view.getGlobalVisibleRect(rect);
        }
        this.mHotArea = new HotArea(rect);
    }

    public void setTopMinMargin(int margin) {
        this.mTopMinMargin = margin;
    }

    public void setLeftMinMargin(int margin) {
        this.mLeftMinMargin = margin;
    }

    public void setMaxHeight(int height) {
        this.mMaxHeight = (getPaddingTop() + height) + getPaddingBottom();
    }

    public void setIndicatorOffset(int offset) {
        this.mIndicatorOffset = offset;
    }

    public void redraw() {
        invalidate();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).invalidate();
        }
    }

    private Path getPath() {
        if (this.mPath == null) {
            int baseWidth = getMeasuredWidth();
            int baseHeight = getMeasuredHeight();
            this.mPath = new Path();
            this.mPath.addRoundRect(new RectF((float) PADDING, (float) (PADDING + 0), (float) (baseWidth - PADDING), (float) ((baseHeight + 0) - PADDING)), (float) Utils.dp2pixel(3.0f), (float) Utils.dp2pixel(3.0f), Direction.CW);
            if (this.mShowIndicator) {
                float x = ((this.mIndicatorOnTop ? this.mHotArea.getCenterXOnBottom() : this.mHotArea.getCenterXOnTop()) - (((float) INDICATOR_WIDTH) / 2.0f)) - (this.mLayoutParams != null ? (float) this.mLayoutParams.x : 0.0f);
                float y;
                if (this.mIndicatorOnTop) {
                    y = (float) (PADDING + 0);
                    this.mPath.moveTo(x, y);
                    this.mPath.lineTo((((float) INDICATOR_WIDTH) / 2.0f) + x, y - ((float) INDICATOR_HEIGHT));
                    this.mPath.lineTo(((float) INDICATOR_WIDTH) + x, y);
                } else {
                    y = (float) (baseHeight - PADDING);
                    this.mPath.moveTo(x, y);
                    this.mPath.lineTo((((float) INDICATOR_WIDTH) / 2.0f) + x, ((float) INDICATOR_HEIGHT) + y);
                    this.mPath.lineTo(((float) INDICATOR_WIDTH) + x, y);
                }
            }
        }
        return this.mPath;
    }
}
