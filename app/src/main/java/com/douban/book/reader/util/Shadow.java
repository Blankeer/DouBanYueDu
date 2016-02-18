package com.douban.book.reader.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.LruCache;
import com.douban.book.reader.theme.Theme.Name;

public class Shadow {
    private static final int SHADOW_PADDING;
    private static LruCache<String, Bitmap> sCachedShadow;

    static {
        SHADOW_PADDING = Utils.dp2pixel(10.0f);
        sCachedShadow = new LruCache(5);
    }

    public static void draw(Canvas canvas, int left, int top, int right, int bottom) {
        drawShadowForTheme(canvas, Name.NIGHT, left, top, right, bottom);
    }

    public static void drawLightShadow(Canvas canvas, int left, int top, int right, int bottom) {
        drawShadowForTheme(canvas, Name.DAY, left, top, right, bottom);
    }

    public static void draw(Canvas canvas, Rect bounds) {
        draw(canvas, bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public static void drawLightShadow(Canvas canvas, Rect bounds) {
        drawLightShadow(canvas, bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public static void drawShadowForTheme(Canvas canvas, Name theme, int left, int top, int right, int bottom) {
        Bitmap bitmap = getShadowBitmap(right - left, bottom - top, theme);
        if (bitmap != null) {
            Paint paint = PaintUtils.obtainPaint();
            int count = canvas.save();
            canvas.translate((float) (-SHADOW_PADDING), (float) (-SHADOW_PADDING));
            canvas.drawBitmap(bitmap, (float) left, (float) top, paint);
            canvas.restoreToCount(count);
            PaintUtils.recyclePaint(paint);
        }
    }

    private static Bitmap getShadowBitmap(int width, int height, Name theme) {
        String key = String.format("%sx%s@%s", new Object[]{Integer.valueOf(width), Integer.valueOf(height), theme});
        Bitmap bitmap = (Bitmap) sCachedShadow.get(key);
        if (bitmap != null) {
            return bitmap;
        }
        try {
            bitmap = Bitmap.createBitmap((SHADOW_PADDING * 2) + width, (SHADOW_PADDING * 2) + height, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.translate((float) SHADOW_PADDING, (float) SHADOW_PADDING);
            Paint paint = PaintUtils.obtainPaint();
            PaintUtils.addShadowLayer(paint);
            canvas.drawRect(0.0f, 0.0f, (float) width, (float) height, paint);
            PaintUtils.recyclePaint(paint);
            sCachedShadow.put(key, bitmap);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return bitmap;
        }
    }
}
