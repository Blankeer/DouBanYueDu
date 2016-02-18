package com.mcxiaoke.next.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import com.mcxiaoke.next.ui.R;

public class CircularImageView extends ImageView {
    private int borderWidth;
    private int canvasSize;
    private Bitmap image;
    private Paint paint;
    private Paint paintBorder;

    public CircularImageView(Context context) {
        this(context, null);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paintBorder = new Paint();
        this.paintBorder.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView, defStyle, 0);
        if (a.getBoolean(R.styleable.CircularImageView_ci_border, true)) {
            setBorderWidth(a.getColor(R.styleable.CircularImageView_ci_border_width, 4));
            setBorderColor(a.getInt(R.styleable.CircularImageView_ci_border_color, -1));
        }
        if (a.getBoolean(R.styleable.CircularImageView_ci_shadow, false)) {
            addShadow();
        }
        a.recycle();
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        requestLayout();
        invalidate();
    }

    public void setBorderColor(int borderColor) {
        if (this.paintBorder != null) {
            this.paintBorder.setColor(borderColor);
        }
        invalidate();
    }

    @TargetApi(11)
    public void addShadow() {
        setLayerType(1, this.paintBorder);
        this.paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, ViewCompat.MEASURED_STATE_MASK);
    }

    public void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable) {
            this.image = ((BitmapDrawable) drawable).getBitmap();
        }
        if (this.image != null) {
            this.canvasSize = canvas.getWidth();
            if (canvas.getHeight() < this.canvasSize) {
                this.canvasSize = canvas.getHeight();
            }
            this.paint.setShader(new BitmapShader(Bitmap.createScaledBitmap(this.image, this.canvasSize, this.canvasSize, false), TileMode.CLAMP, TileMode.CLAMP));
            int circleCenter = (this.canvasSize - (this.borderWidth * 2)) / 2;
            canvas.drawCircle((float) (this.borderWidth + circleCenter), (float) (this.borderWidth + circleCenter), ((float) (((this.canvasSize - (this.borderWidth * 2)) / 2) + this.borderWidth)) - 4.0f, this.paintBorder);
            canvas.drawCircle((float) (this.borderWidth + circleCenter), (float) (this.borderWidth + circleCenter), ((float) ((this.canvasSize - (this.borderWidth * 2)) / 2)) - 4.0f, this.paint);
            return;
        }
        super.onDraw(canvas);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == 1073741824) {
            return specSize;
        }
        if (specMode == ExploreByTouchHelper.INVALID_ID) {
            return specSize;
        }
        return this.canvasSize;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);
        if (specMode == 1073741824) {
            result = specSize;
        } else if (specMode == ExploreByTouchHelper.INVALID_ID) {
            result = specSize;
        } else {
            result = this.canvasSize;
        }
        return result + 2;
    }
}
