package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import u.aly.ci;

public class TouchImageView extends ImageView {
    static final int CLICK = 30;
    static final int DRAG = 1;
    static final int NONE = 0;
    static final int ZOOM = 2;
    Context context;
    PointF last;
    float[] m;
    ScaleGestureDetector mScaleDetector;
    Matrix matrix;
    float maxScale;
    float minScale;
    int mode;
    int oldMeasuredHeight;
    int oldMeasuredWidth;
    protected float origHeight;
    protected float origWidth;
    float saveScale;
    PointF start;
    int viewHeight;
    int viewWidth;

    private class ScaleListener extends SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            TouchImageView.this.mode = TouchImageView.ZOOM;
            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();
            float origScale = TouchImageView.this.saveScale;
            TouchImageView touchImageView = TouchImageView.this;
            touchImageView.saveScale *= mScaleFactor;
            if (TouchImageView.this.saveScale > TouchImageView.this.maxScale) {
                TouchImageView.this.saveScale = TouchImageView.this.maxScale;
                mScaleFactor = TouchImageView.this.maxScale / origScale;
            } else if (TouchImageView.this.saveScale < TouchImageView.this.minScale) {
                TouchImageView.this.saveScale = TouchImageView.this.minScale;
                mScaleFactor = TouchImageView.this.minScale / origScale;
            }
            if (TouchImageView.this.origWidth * TouchImageView.this.saveScale <= ((float) TouchImageView.this.viewWidth) || TouchImageView.this.origHeight * TouchImageView.this.saveScale <= ((float) TouchImageView.this.viewHeight)) {
                TouchImageView.this.matrix.postScale(mScaleFactor, mScaleFactor, (float) (TouchImageView.this.viewWidth / TouchImageView.ZOOM), (float) (TouchImageView.this.viewHeight / TouchImageView.ZOOM));
            } else {
                TouchImageView.this.matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
            }
            TouchImageView.this.fixTrans();
            return true;
        }
    }

    public TouchImageView(Context context) {
        super(context);
        this.mode = NONE;
        this.last = new PointF();
        this.start = new PointF();
        this.minScale = 1.0f;
        this.maxScale = 3.0f;
        this.saveScale = 1.0f;
        sharedConstructing(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mode = NONE;
        this.last = new PointF();
        this.start = new PointF();
        this.minScale = 1.0f;
        this.maxScale = 3.0f;
        this.saveScale = 1.0f;
        sharedConstructing(context);
    }

    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.matrix = new Matrix();
        this.m = new float[9];
        setImageMatrix(this.matrix);
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                TouchImageView.this.mScaleDetector.onTouchEvent(event);
                PointF curr = new PointF(event.getX(), event.getY());
                switch (event.getAction()) {
                    case TouchImageView.NONE /*0*/:
                        TouchImageView.this.last.set(curr);
                        TouchImageView.this.start.set(TouchImageView.this.last);
                        TouchImageView.this.mode = TouchImageView.DRAG;
                        break;
                    case TouchImageView.DRAG /*1*/:
                        TouchImageView.this.mode = TouchImageView.NONE;
                        int yDiff = (int) Math.abs(curr.y - TouchImageView.this.start.y);
                        if (((int) Math.abs(curr.x - TouchImageView.this.start.x)) < TouchImageView.CLICK && yDiff < TouchImageView.CLICK) {
                            TouchImageView.this.performClick();
                            break;
                        }
                    case TouchImageView.ZOOM /*2*/:
                        if (TouchImageView.this.mode == TouchImageView.DRAG) {
                            float deltaY = curr.y - TouchImageView.this.last.y;
                            TouchImageView.this.matrix.postTranslate(TouchImageView.this.getFixDragTrans(curr.x - TouchImageView.this.last.x, (float) TouchImageView.this.viewWidth, TouchImageView.this.origWidth * TouchImageView.this.saveScale), TouchImageView.this.getFixDragTrans(deltaY, (float) TouchImageView.this.viewHeight, TouchImageView.this.origHeight * TouchImageView.this.saveScale));
                            TouchImageView.this.fixTrans();
                            TouchImageView.this.last.set(curr.x, curr.y);
                            break;
                        }
                        break;
                    case ci.g /*6*/:
                        TouchImageView.this.mode = TouchImageView.NONE;
                        break;
                }
                TouchImageView.this.setImageMatrix(TouchImageView.this.matrix);
                TouchImageView.this.invalidate();
                return true;
            }
        });
    }

    public void setMaxZoom(float x) {
        this.maxScale = x;
    }

    void fixTrans() {
        this.matrix.getValues(this.m);
        float transX = this.m[ZOOM];
        float transY = this.m[5];
        float fixTransX = getFixTrans(transX, (float) this.viewWidth, this.origWidth * this.saveScale);
        float fixTransY = getFixTrans(transY, (float) this.viewHeight, this.origHeight * this.saveScale);
        if (fixTransX != 0.0f || fixTransY != 0.0f) {
            this.matrix.postTranslate(fixTransX, fixTransY);
        }
    }

    float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans;
        float maxTrans;
        if (contentSize <= viewSize) {
            minTrans = 0.0f;
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0.0f;
        }
        if (trans < minTrans) {
            return (-trans) + minTrans;
        }
        if (trans > maxTrans) {
            return (-trans) + maxTrans;
        }
        return 0.0f;
    }

    float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) {
            return 0.0f;
        }
        return delta;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        if ((this.oldMeasuredHeight != this.viewWidth || this.oldMeasuredHeight != this.viewHeight) && this.viewWidth != 0 && this.viewHeight != 0) {
            this.oldMeasuredHeight = this.viewHeight;
            this.oldMeasuredWidth = this.viewWidth;
            if (this.saveScale == 1.0f) {
                Drawable drawable = getDrawable();
                if (drawable != null && drawable.getIntrinsicWidth() != 0 && drawable.getIntrinsicHeight() != 0) {
                    int bmWidth = drawable.getIntrinsicWidth();
                    int bmHeight = drawable.getIntrinsicHeight();
                    float scale = Math.min(((float) this.viewWidth) / ((float) bmWidth), ((float) this.viewHeight) / ((float) bmHeight));
                    this.matrix.setScale(scale, scale);
                    float redundantYSpace = (((float) this.viewHeight) - (((float) bmHeight) * scale)) / 2.0f;
                    float redundantXSpace = (((float) this.viewWidth) - (((float) bmWidth) * scale)) / 2.0f;
                    this.matrix.postTranslate(redundantXSpace, redundantYSpace);
                    this.origWidth = ((float) this.viewWidth) - (2.0f * redundantXSpace);
                    this.origHeight = ((float) this.viewHeight) - (2.0f * redundantYSpace);
                    setImageMatrix(this.matrix);
                } else {
                    return;
                }
            }
            fixTrans();
        }
    }
}
