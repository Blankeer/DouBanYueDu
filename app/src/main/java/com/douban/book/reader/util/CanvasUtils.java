package com.douban.book.reader.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import com.douban.book.reader.constant.Dimen;

public class CanvasUtils {
    public static void drawMultiline(Canvas canvas, Paint paint, float x, float y, String text) {
        for (String line : text.split("\n")) {
            canvas.drawText(line, x, y, paint);
            y += paint.getTextSize() + 2.0f;
        }
    }

    public static void drawTextCenteredOnPoint(Canvas canvas, Paint paint, float x, float y, CharSequence text) {
        paint.setTextAlign(Align.CENTER);
        drawTextVerticalCentered(canvas, paint, x, y, text);
    }

    public static void drawTextVerticalCentered(Canvas canvas, Paint paint, float x, float y, CharSequence text) {
        if (!StringUtils.isEmpty(text)) {
            Rect bounds = new Rect();
            String str = String.valueOf(text);
            paint.getTextBounds(str, 0, str.length(), bounds);
            canvas.drawText(str, x, ((((float) bounds.height()) / 2.0f) + y) - ((float) bounds.bottom), paint);
        }
    }

    public static void drawFocusPoint(Canvas canvas, float x, float y) {
        Paint paint = PaintUtils.obtainPaint();
        paint.setColor(SupportMenu.CATEGORY_MASK);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(0.0f);
        canvas.drawLine(x - ((float) 20), y, x + ((float) 20), y, paint);
        canvas.drawLine(x, y - ((float) 20), x, y + ((float) 20), paint);
        PaintUtils.recyclePaint(paint);
    }

    public static void drawHorizontalLine(Canvas canvas, float y, int color) {
        Paint paint = PaintUtils.obtainPaint();
        paint.setColor(color);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(0.0f);
        canvas.drawLine(0.0f, y, 2000.0f, y, paint);
        PaintUtils.recyclePaint(paint);
    }

    public static void drawHorizontalLine(Canvas canvas, float y) {
        drawHorizontalLine(canvas, y, SupportMenu.CATEGORY_MASK);
    }

    public static void drawVerticalLine(Canvas canvas, float x, int color) {
        Paint paint = PaintUtils.obtainPaint();
        paint.setColor(color);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(0.0f);
        canvas.drawLine(x, 0.0f, x, 2000.0f, paint);
        PaintUtils.recyclePaint(paint);
    }

    public static void drawVerticalLine(Canvas canvas, float x) {
        drawVerticalLine(canvas, x, SupportMenu.CATEGORY_MASK);
    }

    public static void drawGridLines(Canvas canvas, RectF bounds, int interval) {
        int i = 0;
        float x = bounds.left;
        while (x <= bounds.right) {
            int i2;
            if (i % 5 == 0) {
                i2 = -16776961;
            } else {
                i2 = SupportMenu.CATEGORY_MASK;
            }
            drawVerticalLine(canvas, x, i2);
            x += (float) interval;
            i++;
        }
        i = 0;
        float y = bounds.top;
        while (y <= bounds.bottom) {
            if (i % 5 == 0) {
                i2 = -16776961;
            } else {
                i2 = SupportMenu.CATEGORY_MASK;
            }
            drawHorizontalLine(canvas, y, i2);
            y += (float) interval;
            i++;
        }
    }

    public static RectF rectToRectF(Rect rect) {
        return new RectF(rect);
    }

    public static Rect rectFToRect(RectF rect) {
        Rect ret = new Rect();
        rect.roundOut(ret);
        return ret;
    }

    public static RectF rectFromCenterAndRadius(float centerX, float centerY, float radius) {
        return rectFromCenterAndRadius(centerX, centerY, radius, radius);
    }

    public static RectF rectFromCenterAndRadius(float centerX, float centerY, float radiusX, float radiusY) {
        return new RectF(centerX - radiusX, centerY - radiusY, centerX + radiusX, centerY + radiusY);
    }

    public static RectF enlargeRectTo(RectF rect, float enlargedSize) {
        if (rect == null || rect.isEmpty() || enlargedSize <= 0.0f) {
            throw new IllegalArgumentException(String.format("rect=%s, enlargedSize=%s", new Object[]{rect, Float.valueOf(enlargedSize)}));
        } else if (rect.width() >= enlargedSize && rect.height() >= enlargedSize) {
            return rect;
        } else {
            float radius = enlargedSize / 2.0f;
            return new RectF(rect.centerX() - radius, rect.centerY() - radius, rect.centerX() + radius, rect.centerY() + radius);
        }
    }

    public static RectF enlargeRectBy(RectF rect, float sizeToEnlarge) {
        if (rect != null && !rect.isEmpty() && sizeToEnlarge > 0.0f) {
            return new RectF(rect.left - sizeToEnlarge, rect.top - sizeToEnlarge, rect.right + sizeToEnlarge, rect.bottom + sizeToEnlarge);
        }
        throw new IllegalArgumentException(String.format("rect=%s, enlargedSize=%s", new Object[]{rect, Float.valueOf(sizeToEnlarge)}));
    }

    public static Rect enlargeRectBy(Rect rect, int sizeToEnlarge) {
        if (rect != null && !rect.isEmpty() && sizeToEnlarge > 0) {
            return new Rect(rect.left - sizeToEnlarge, rect.top - sizeToEnlarge, rect.right + sizeToEnlarge, rect.bottom + sizeToEnlarge);
        }
        throw new IllegalArgumentException(String.format("rect=%s, enlargedSize=%s", new Object[]{rect, Integer.valueOf(sizeToEnlarge)}));
    }

    public static RectF shrinkRectBy(RectF rect, float sizeToShrink) {
        return new RectF(rect.left + sizeToShrink, rect.top + sizeToShrink, rect.right - sizeToShrink, rect.bottom - sizeToShrink);
    }

    public static Rect shrinkRectBy(Rect rect, int sizeToShrink) {
        return new Rect(rect.left + sizeToShrink, rect.top + sizeToShrink, rect.right - sizeToShrink, rect.bottom - sizeToShrink);
    }

    public static RectF getRectFitsInArea(RectF area, float contentWidth, float contentHeight) {
        float resultWidth;
        float resultHeight;
        float areaWidth = area.width();
        float areaHeight = area.height();
        if (contentWidth * areaHeight > areaWidth * contentHeight) {
            resultWidth = areaWidth;
            resultHeight = (contentHeight * areaWidth) / contentWidth;
        } else {
            resultHeight = areaHeight;
            resultWidth = (contentWidth * areaHeight) / contentHeight;
        }
        float x = area.left + ((areaWidth - resultWidth) / 2.0f);
        float y = area.top + ((areaHeight - resultHeight) / 2.0f);
        return new RectF(x, y, x + resultWidth, y + resultHeight);
    }

    public static void drawBitmapCenteredOnPoint(Canvas canvas, Bitmap bitmap, float x, float y) {
        if (bitmap != null) {
            float xRadius = ((float) bitmap.getWidth()) / 2.0f;
            float yRadius = ((float) bitmap.getHeight()) / 2.0f;
            Paint paint = PaintUtils.obtainPaint();
            canvas.drawBitmap(bitmap, x - xRadius, y - yRadius, paint);
            PaintUtils.recyclePaint(paint);
        }
    }

    public static void drawDrawableCenteredOnPointWithAlpha(Canvas canvas, Drawable drawable, float x, float y, float alpha) {
        if (drawable != null) {
            float xRadius = ((float) drawable.getIntrinsicWidth()) / 2.0f;
            float yRadius = ((float) drawable.getIntrinsicHeight()) / 2.0f;
            Drawable drawableToDraw = drawable.mutate();
            drawableToDraw.setBounds(Math.round(x - xRadius), Math.round(y - yRadius), Math.round(x + xRadius), Math.round(y + yRadius));
            drawableToDraw.setAlpha(Utils.ratioToInt255(alpha));
            drawable.draw(canvas);
        }
    }

    public static void drawDrawableCenteredOnPoint(Canvas canvas, Drawable drawable, float x, float y) {
        drawDrawableCenteredOnPointWithAlpha(canvas, drawable, x, y, 1.0f);
    }

    public static void drawDrawableCenteredOnPoint(Canvas canvas, int resId, float x, float y) {
        drawDrawableCenteredOnPoint(canvas, Res.getDrawable(resId), x, y);
    }

    public static void drawDrawableCenteredInArea(Canvas canvas, Drawable drawable, RectF area) {
        drawable.setBounds(rectFToRect(getRectFitsInArea(area, (float) drawable.getIntrinsicWidth(), (float) drawable.getIntrinsicHeight())));
        drawable.draw(canvas);
    }

    public static void drawIconTextCenteredOnPoint(Canvas canvas, Paint paint, float centerX, float centerY, Drawable drawable, String text, float padding) {
        float iconOffset = 0.0f;
        float textOffset = 0.0f;
        if (drawable != null) {
            textOffset = (((float) drawable.getIntrinsicHeight()) + padding) / 2.0f;
        }
        if (StringUtils.isNotEmpty(text)) {
            iconOffset = 0.0f + ((paint.getTextSize() + padding) / 2.0f);
            drawTextCenteredOnPoint(canvas, paint, centerX, centerY + textOffset, text);
        }
        if (drawable != null) {
            drawDrawableCenteredOnPoint(canvas, drawable, centerX, centerY - iconOffset);
        }
    }

    public static void drawPathCenteredOnPoint(Canvas canvas, Paint paint, Path path, float centerX, float centerY, float radius) {
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        float radiusX = bounds.width() / 2.0f;
        float radiusY = bounds.height() / 2.0f;
        float scale = 1.0f;
        if (radiusX > 0.0f && radiusY > 0.0f) {
            scale = Math.min(radius / radiusX, radius / radiusY);
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(centerX - (radiusX * scale), centerY - (radiusY * scale));
        Path transformedPath = new Path();
        path.transform(matrix, transformedPath);
        canvas.drawPath(transformedPath, paint);
    }

    public static void drawPathShadow(Canvas canvas, Path path) {
        try {
            RectF bounds = new RectF();
            path.computeBounds(bounds, true);
            bounds = enlargeRectBy(bounds, (float) (Dimen.SHADOW_WIDTH * 2));
            Bitmap bitmap = Bitmap.createBitmap(Math.round(bounds.width()), Math.round(bounds.height()), Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(bitmap);
            Paint paint = PaintUtils.obtainPaint();
            paint.setStyle(Style.FILL_AND_STROKE);
            paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            PaintUtils.addShadowLayer(paint);
            Path newPath = new Path();
            path.offset(-bounds.left, -bounds.top, newPath);
            bitmapCanvas.drawPath(newPath, paint);
            paint.clearShadowLayer();
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            bitmapCanvas.drawPath(newPath, paint);
            paint.setXfermode(null);
            canvas.drawBitmap(bitmap, bounds.left, bounds.top, paint);
            PaintUtils.recyclePaint(paint);
        } catch (OutOfMemoryError e) {
        }
    }
}
