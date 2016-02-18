package natalya.graphics;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import com.alipay.sdk.protocol.h;
import com.douban.book.reader.util.WorksIdentity;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import u.aly.ci;
import u.aly.dx;

public class BitmapUtils {
    public static Bitmap rotate(Bitmap bmp, float angle) {
        Matrix matrixRotateLeft = new Matrix();
        matrixRotateLeft.setRotate(angle);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrixRotateLeft, true);
    }

    public static Bitmap scale(ContentResolver contentResolver, Uri uri, int maxWidth, int maxHeight) {
        String tag = "SCALE";
        Log.d(tag, "uri=" + uri.toString());
        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            InputStream input = contentResolver.openInputStream(uri);
            BitmapFactory.decodeStream(input, null, options);
            int sourceWidth = options.outWidth;
            int sourceHeight = options.outHeight;
            Log.d(tag, "sourceWidth=" + sourceWidth + ", sourceHeight=" + sourceHeight);
            Log.d(tag, "maxWidth=" + maxWidth + ", maxHeight=" + maxHeight);
            input.close();
            float rate = Math.max(((float) sourceWidth) / ((float) maxWidth), ((float) sourceHeight) / ((float) maxHeight));
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) rate;
            Log.d(tag, "rate=" + rate + ", inSampleSize=" + options.inSampleSize);
            input = contentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            int w0 = bitmap.getWidth();
            int h0 = bitmap.getHeight();
            Log.d(tag, "w0=" + w0 + ", h0=" + h0);
            float scaleWidth = ((float) maxWidth) / ((float) w0);
            float scaleHeight = ((float) maxHeight) / ((float) h0);
            float maxScale = Math.min(scaleWidth, scaleHeight);
            Log.d(tag, "scaleWidth=" + scaleWidth + ", scaleHeight=" + scaleHeight);
            Matrix matrix = new Matrix();
            matrix.reset();
            if (maxScale < 1.0f) {
                matrix.postScale(maxScale, maxScale);
            }
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w0, h0, matrix, true);
            input.close();
            return resizedBitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static Bitmap getBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != -1 ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap drawShadow(Bitmap map, int radius) {
        if (map == null) {
            return null;
        }
        BlurMaskFilter blurFilter = new BlurMaskFilter((float) radius, Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);
        int[] offsetXY = new int[2];
        Bitmap shadowImage = map.extractAlpha(shadowPaint, offsetXY).copy(Config.ARGB_8888, true);
        if (VERSION.SDK_INT > 18) {
            shadowImage.setPremultiplied(true);
        }
        new Canvas(shadowImage).drawBitmap(map, (float) (-offsetXY[0]), (float) (-offsetXY[1]), null);
        return shadowImage;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static int getExifOrientation(String filepath) {
        try {
            Class cls = Class.forName("android.media.ExifInterface");
            Constructor cons = cls.getConstructor(new Class[]{String.class});
            Method method = cls.getMethod("getAttributeInt", new Class[]{String.class, Integer.TYPE});
            Object exif = cons.newInstance(new Object[]{filepath});
            if (exif == null) {
                return 0;
            }
            int orientation = ((Integer) method.invoke(exif, new Object[]{"Orientation", Integer.valueOf(-1)})).intValue();
            if (orientation == -1) {
                return 0;
            }
            switch (orientation) {
                case dx.d /*3*/:
                    return Header.MA_VAR;
                case ci.g /*6*/:
                    return 90;
                case h.g /*8*/:
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static byte[] generateBitstream(Bitmap src, CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);
        return os.toByteArray();
    }

    public static Bitmap fastblur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return null;
        }
        int i;
        int y;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[(w * h)];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = (radius + radius) + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int[] vmin = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[(divsum * WorksIdentity.ID_BIT_FINALIZE)];
        for (i = 0; i < divsum * WorksIdentity.ID_BIT_FINALIZE; i++) {
            dv[i] = i / divsum;
        }
        int yi = 0;
        int yw = 0;
        int[][] stack = (int[][]) Array.newInstance(Integer.TYPE, new int[]{div, 3});
        int r1 = radius + 1;
        for (y = 0; y < h; y++) {
            int x;
            int bsum = 0;
            int gsum = 0;
            int rsum = 0;
            int boutsum = 0;
            int goutsum = 0;
            int routsum = 0;
            int binsum = 0;
            int ginsum = 0;
            int rinsum = 0;
            for (i = -radius; i <= radius; i++) {
                int p = pix[Math.min(wm, Math.max(i, 0)) + yi];
                int[] sir = stack[i + radius];
                sir[0] = (16711680 & p) >> 16;
                sir[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & p) >> 8;
                sir[2] = p & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                int rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            int stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                sir = stack[((stackpointer - radius) + div) % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min((x + radius) + 1, wm);
                }
                p = pix[vmin[x] + yw];
                sir[0] = (16711680 & p) >> 16;
                sir[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & p) >> 8;
                sir[2] = p & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            bsum = 0;
            gsum = 0;
            rsum = 0;
            boutsum = 0;
            goutsum = 0;
            routsum = 0;
            binsum = 0;
            ginsum = 0;
            rinsum = 0;
            int yp = (-radius) * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = ((ViewCompat.MEASURED_STATE_MASK | (dv[rsum] << 16)) | (dv[gsum] << 8)) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                sir = stack[((stackpointer - radius) + div) % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public static Bitmap getReflectBitmap(Bitmap originalImage, float rate) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int reflectHeight = Math.round(((float) height) * rate);
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height - reflectHeight, width, reflectHeight, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, height + reflectHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(originalImage, 0.0f, 0.0f, null);
        canvas.drawRect(0.0f, (float) height, (float) width, (float) (height + 4), new Paint());
        canvas.drawBitmap(reflectionImage, 0.0f, (float) (height + 4), null);
        Paint paint = new Paint();
        Paint paint2 = paint;
        paint2.setShader(new LinearGradient(0.0f, (float) originalImage.getHeight(), 0.0f, (float) (bitmapWithReflection.getHeight() + 4), 1895825407, ViewCompat.MEASURED_SIZE_MASK, TileMode.CLAMP));
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, (float) height, (float) width, (float) (bitmapWithReflection.getHeight() + 4), paint);
        return bitmapWithReflection;
    }

    public static Bitmap getSquareBitmap(Bitmap src) {
        return getSquareBitmap(src, 0.1f);
    }

    public static Bitmap getSquareBitmap(Bitmap src, float rate) {
        Bitmap ret = src;
        int w = src.getWidth();
        int h = src.getHeight();
        int min = Math.min(w, h);
        float r = ((float) (Math.max(w, h) - min)) / ((float) min);
        if (w == h || r <= rate) {
            return ret;
        }
        int max = Math.round((1.0f + rate) * ((float) min));
        if (w > h) {
            return Bitmap.createBitmap(src, (w - max) / 2, 0, max, min);
        }
        return Bitmap.createBitmap(src, 0, (h - max) / 2, min, max);
    }
}
