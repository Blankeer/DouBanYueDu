package com.douban.book.reader.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import java.io.ByteArrayOutputStream;

public class BitmapUtils {
    public static Bitmap scaleToWidth(Bitmap bm, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (width <= 0) {
            return null;
        }
        float scaleRate = ((float) newWidth) / ((float) width);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRate, scaleRate);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    public static byte[] getCompressedBits(Bitmap src, CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);
        return os.toByteArray();
    }
}
