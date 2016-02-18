package com.douban.book.reader.drawable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import com.douban.book.reader.R;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;

public class RoundCornerDrawable extends DrawableWrapper {
    private Bitmap mBitmap;
    private int mCornerRadius;

    public RoundCornerDrawable(Drawable drawable) {
        super(drawable);
        this.mCornerRadius = Res.getDimensionPixelSize(R.dimen.btn_round_corner_radius);
    }

    public RoundCornerDrawable cornerRadius(int radius) {
        this.mCornerRadius = radius;
        return this;
    }

    public int getOpacity() {
        return -3;
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = getCacheBitmap();
        if (bitmap == null) {
            super.draw(canvas);
            return;
        }
        Shader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        Paint paint = PaintUtils.obtainPaint();
        paint.setShader(shader);
        int height = getBounds().height();
        int width = getBounds().width();
        int radius = this.mCornerRadius;
        canvas.drawRoundRect(new RectF(0.0f, 0.0f, (float) width, (float) height), (float) radius, (float) radius, paint);
        PaintUtils.recyclePaint(paint);
    }

    @Nullable
    private Bitmap getCacheBitmap() {
        Drawable drawable = getWrappedDrawable();
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        try {
            if (this.mBitmap == null || this.mBitmap.getWidth() < width || this.mBitmap.getHeight() < height) {
                this.mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            }
            if (this.mBitmap != null) {
                drawable.draw(new Canvas(this.mBitmap));
            }
            return this.mBitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
