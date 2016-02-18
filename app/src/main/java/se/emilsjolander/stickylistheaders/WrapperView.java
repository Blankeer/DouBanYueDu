package se.emilsjolander.stickylistheaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class WrapperView extends ViewGroup {
    Drawable mDivider;
    int mDividerHeight;
    View mHeader;
    View mItem;
    int mItemTop;

    WrapperView(Context c) {
        super(c);
    }

    public boolean hasHeader() {
        return this.mHeader != null;
    }

    public View getItem() {
        return this.mItem;
    }

    public View getHeader() {
        return this.mHeader;
    }

    void update(View item, View header, Drawable divider, int dividerHeight) {
        if (item == null) {
            throw new NullPointerException("List view item must not be null.");
        }
        if (this.mItem != item) {
            removeView(this.mItem);
            this.mItem = item;
            Object parent = item.getParent();
            if (!(parent == null || parent == this || !(parent instanceof ViewGroup))) {
                ((ViewGroup) parent).removeView(item);
            }
            addView(item);
        }
        if (this.mHeader != header) {
            if (this.mHeader != null) {
                removeView(this.mHeader);
            }
            this.mHeader = header;
            if (header != null) {
                addView(header);
            }
        }
        if (this.mDivider != divider) {
            this.mDivider = divider;
            this.mDividerHeight = dividerHeight;
            invalidate();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams params;
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
        int measuredHeight = 0;
        if (this.mHeader != null) {
            params = this.mHeader.getLayoutParams();
            if (params == null || params.height <= 0) {
                this.mHeader.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(0, 0));
            } else {
                this.mHeader.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(params.height, 1073741824));
            }
            measuredHeight = 0 + this.mHeader.getMeasuredHeight();
        } else if (!(this.mDivider == null || this.mItem.getVisibility() == 8)) {
            measuredHeight = 0 + this.mDividerHeight;
        }
        params = this.mItem.getLayoutParams();
        if (this.mItem.getVisibility() == 8) {
            this.mItem.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(0, 1073741824));
        } else if (params == null || params.height < 0) {
            this.mItem.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(0, 0));
            measuredHeight += this.mItem.getMeasuredHeight();
        } else {
            this.mItem.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(params.height, 1073741824));
            measuredHeight += this.mItem.getMeasuredHeight();
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        r = getWidth();
        b = getHeight();
        if (this.mHeader != null) {
            int headerHeight = this.mHeader.getMeasuredHeight();
            this.mHeader.layout(0, 0, r, headerHeight);
            this.mItemTop = headerHeight;
            this.mItem.layout(0, headerHeight, r, b);
        } else if (this.mDivider != null) {
            this.mDivider.setBounds(0, 0, r, this.mDividerHeight);
            this.mItemTop = this.mDividerHeight;
            this.mItem.layout(0, this.mDividerHeight, r, b);
        } else {
            this.mItemTop = 0;
            this.mItem.layout(0, 0, r, b);
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mHeader == null && this.mDivider != null && this.mItem.getVisibility() != 8) {
            if (VERSION.SDK_INT < 11) {
                canvas.clipRect(0, 0, getWidth(), this.mDividerHeight);
            }
            this.mDivider.draw(canvas);
        }
    }
}
