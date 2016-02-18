package com.douban.book.reader.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.InputDeviceCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import com.douban.book.reader.R;

public class FlowLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private boolean debugDraw;
    private int horizontalSpacing;
    private int orientation;
    private int verticalSpacing;

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        private static int NO_SPACING;
        private int horizontalSpacing;
        private boolean newLine;
        private int verticalSpacing;
        private int x;
        private int y;

        static {
            NO_SPACING = -1;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.horizontalSpacing = NO_SPACING;
            this.verticalSpacing = NO_SPACING;
            this.newLine = false;
            readStyleParameters(context, attributeSet);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.horizontalSpacing = NO_SPACING;
            this.verticalSpacing = NO_SPACING;
            this.newLine = false;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.horizontalSpacing = NO_SPACING;
            this.verticalSpacing = NO_SPACING;
            this.newLine = false;
        }

        public boolean horizontalSpacingSpecified() {
            return this.horizontalSpacing != NO_SPACING;
        }

        public boolean verticalSpacingSpecified() {
            return this.verticalSpacing != NO_SPACING;
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private void readStyleParameters(Context context, AttributeSet attributeSet) {
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout_LayoutParams);
            try {
                this.horizontalSpacing = a.getDimensionPixelSize(FlowLayout.VERTICAL, NO_SPACING);
                this.verticalSpacing = a.getDimensionPixelSize(2, NO_SPACING);
                this.newLine = a.getBoolean(FlowLayout.HORIZONTAL, false);
            } finally {
                a.recycle();
            }
        }
    }

    public FlowLayout(Context context) {
        super(context);
        this.horizontalSpacing = HORIZONTAL;
        this.verticalSpacing = HORIZONTAL;
        this.orientation = HORIZONTAL;
        this.debugDraw = false;
        readStyleParameters(context, null);
    }

    public FlowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.horizontalSpacing = HORIZONTAL;
        this.verticalSpacing = HORIZONTAL;
        this.orientation = HORIZONTAL;
        this.debugDraw = false;
        readStyleParameters(context, attributeSet);
    }

    public FlowLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.horizontalSpacing = HORIZONTAL;
        this.verticalSpacing = HORIZONTAL;
        this.orientation = HORIZONTAL;
        this.debugDraw = false;
        readStyleParameters(context, attributeSet);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size;
        int mode;
        int sizeWidth = (MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight()) - getPaddingLeft();
        int sizeHeight = (MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop()) - getPaddingBottom();
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        if (this.orientation == 0) {
            size = sizeWidth;
            mode = modeWidth;
        } else {
            size = sizeHeight;
            mode = modeHeight;
        }
        int lineThicknessWithSpacing = HORIZONTAL;
        int lineThickness = HORIZONTAL;
        int lineLengthWithSpacing = HORIZONTAL;
        int prevLinePosition = HORIZONTAL;
        int controlMaxLength = HORIZONTAL;
        int controlMaxThickness = HORIZONTAL;
        int count = getChildCount();
        for (int i = HORIZONTAL; i < count; i += VERTICAL) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int childLength;
                int childThickness;
                int spacingLength;
                int spacingThickness;
                int posX;
                int posY;
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                child.measure(getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), lp.width), getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), lp.height));
                int hSpacing = getHorizontalSpacing(lp);
                int vSpacing = getVerticalSpacing(lp);
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                if (this.orientation == 0) {
                    childLength = childWidth;
                    childThickness = childHeight;
                    spacingLength = hSpacing;
                    spacingThickness = vSpacing;
                } else {
                    childLength = childHeight;
                    childThickness = childWidth;
                    spacingLength = vSpacing;
                    spacingThickness = hSpacing;
                }
                int lineLength = lineLengthWithSpacing + childLength;
                lineLengthWithSpacing = lineLength + spacingLength;
                boolean newLine = lp.newLine || (mode != 0 && lineLength > size);
                if (newLine) {
                    prevLinePosition += lineThicknessWithSpacing;
                    lineThickness = childThickness;
                    lineLength = childLength;
                    lineThicknessWithSpacing = childThickness + spacingThickness;
                    lineLengthWithSpacing = lineLength + spacingLength;
                }
                lineThicknessWithSpacing = Math.max(lineThicknessWithSpacing, childThickness + spacingThickness);
                lineThickness = Math.max(lineThickness, childThickness);
                if (this.orientation == 0) {
                    posX = (getPaddingLeft() + lineLength) - childLength;
                    posY = getPaddingTop() + prevLinePosition;
                } else {
                    posX = getPaddingLeft() + prevLinePosition;
                    posY = (getPaddingTop() + lineLength) - childHeight;
                }
                lp.setPosition(posX, posY);
                controlMaxLength = Math.max(controlMaxLength, lineLength);
                controlMaxThickness = prevLinePosition + lineThickness;
            }
        }
        if (this.orientation == 0) {
            controlMaxLength += getPaddingLeft() + getPaddingRight();
            controlMaxThickness += getPaddingBottom() + getPaddingTop();
        } else {
            controlMaxLength += getPaddingBottom() + getPaddingTop();
            controlMaxThickness += getPaddingLeft() + getPaddingRight();
        }
        if (this.orientation == 0) {
            setMeasuredDimension(resolveSize(controlMaxLength, widthMeasureSpec), resolveSize(controlMaxThickness, heightMeasureSpec));
        } else {
            setMeasuredDimension(resolveSize(controlMaxThickness, widthMeasureSpec), resolveSize(controlMaxLength, heightMeasureSpec));
        }
    }

    private int getVerticalSpacing(LayoutParams lp) {
        if (lp.verticalSpacingSpecified()) {
            return lp.verticalSpacing;
        }
        return this.verticalSpacing;
    }

    private int getHorizontalSpacing(LayoutParams lp) {
        if (lp.horizontalSpacingSpecified()) {
            return lp.horizontalSpacing;
        }
        return this.horizontalSpacing;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = HORIZONTAL; i < count; i += VERTICAL) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
        }
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean more = super.drawChild(canvas, child, drawingTime);
        drawDebugInfo(canvas, child);
        return more;
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout);
        try {
            this.horizontalSpacing = a.getDimensionPixelSize(HORIZONTAL, HORIZONTAL);
            this.verticalSpacing = a.getDimensionPixelSize(VERTICAL, HORIZONTAL);
            this.orientation = a.getInteger(2, HORIZONTAL);
            this.debugDraw = a.getBoolean(3, false);
        } finally {
            a.recycle();
        }
    }

    private void drawDebugInfo(Canvas canvas, View child) {
        if (this.debugDraw) {
            float x;
            float y;
            Paint childPaint = createPaint(InputDeviceCompat.SOURCE_ANY);
            Paint layoutPaint = createPaint(-16711936);
            Paint newLinePaint = createPaint(SupportMenu.CATEGORY_MASK);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.horizontalSpacing > 0) {
                x = (float) child.getRight();
                y = ((float) child.getTop()) + (((float) child.getHeight()) / 2.0f);
                canvas.drawLine(x, y, x + ((float) lp.horizontalSpacing), y, childPaint);
                canvas.drawLine((((float) lp.horizontalSpacing) + x) - 4.0f, y - 4.0f, x + ((float) lp.horizontalSpacing), y, childPaint);
                canvas.drawLine((((float) lp.horizontalSpacing) + x) - 4.0f, y + 4.0f, x + ((float) lp.horizontalSpacing), y, childPaint);
            } else if (this.horizontalSpacing > 0) {
                x = (float) child.getRight();
                y = ((float) child.getTop()) + (((float) child.getHeight()) / 2.0f);
                canvas.drawLine(x, y, x + ((float) this.horizontalSpacing), y, layoutPaint);
                canvas.drawLine((((float) this.horizontalSpacing) + x) - 4.0f, y - 4.0f, x + ((float) this.horizontalSpacing), y, layoutPaint);
                canvas.drawLine((((float) this.horizontalSpacing) + x) - 4.0f, y + 4.0f, x + ((float) this.horizontalSpacing), y, layoutPaint);
            }
            if (lp.verticalSpacing > 0) {
                x = ((float) child.getLeft()) + (((float) child.getWidth()) / 2.0f);
                y = (float) child.getBottom();
                canvas.drawLine(x, y, x, y + ((float) lp.verticalSpacing), childPaint);
                canvas.drawLine(x - 4.0f, (((float) lp.verticalSpacing) + y) - 4.0f, x, y + ((float) lp.verticalSpacing), childPaint);
                canvas.drawLine(x + 4.0f, (((float) lp.verticalSpacing) + y) - 4.0f, x, y + ((float) lp.verticalSpacing), childPaint);
            } else if (this.verticalSpacing > 0) {
                x = ((float) child.getLeft()) + (((float) child.getWidth()) / 2.0f);
                y = (float) child.getBottom();
                canvas.drawLine(x, y, x, y + ((float) this.verticalSpacing), layoutPaint);
                canvas.drawLine(x - 4.0f, (((float) this.verticalSpacing) + y) - 4.0f, x, y + ((float) this.verticalSpacing), layoutPaint);
                canvas.drawLine(x + 4.0f, (((float) this.verticalSpacing) + y) - 4.0f, x, y + ((float) this.verticalSpacing), layoutPaint);
            }
            if (!lp.newLine) {
                return;
            }
            if (this.orientation == 0) {
                x = (float) child.getLeft();
                y = ((float) child.getTop()) + (((float) child.getHeight()) / 2.0f);
                canvas.drawLine(x, y - 6.0f, x, y + 6.0f, newLinePaint);
                return;
            }
            x = ((float) child.getLeft()) + (((float) child.getWidth()) / 2.0f);
            y = (float) child.getTop();
            canvas.drawLine(x - 6.0f, y, x + 6.0f, y, newLinePaint);
        }
    }

    private Paint createPaint(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(2.0f);
        return paint;
    }
}
