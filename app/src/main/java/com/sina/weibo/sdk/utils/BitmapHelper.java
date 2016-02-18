package com.sina.weibo.sdk.utils;

import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class BitmapHelper {
    public static boolean makesureSizeNotTooLarge(Rect rect) {
        if ((rect.width() * rect.height()) * 2 > 5242880) {
            return false;
        }
        return true;
    }

    public static int getSampleSizeOfNotTooLarge(Rect rect) {
        double ratio = ((((double) rect.width()) * ((double) rect.height())) * 2.0d) / 5242880.0d;
        return ratio >= 1.0d ? (int) ratio : 1;
    }

    public static int getSampleSizeAutoFitToScreen(int vWidth, int vHeight, int bWidth, int bHeight) {
        if (vHeight == 0 || vWidth == 0) {
            return 1;
        }
        return Math.min(Math.max(bWidth / vWidth, bHeight / vHeight), Math.max(bHeight / vWidth, bWidth / vHeight));
    }

    public static boolean verifyBitmap(byte[] datas) {
        return verifyBitmap(new ByteArrayInputStream(datas));
    }

    public static boolean verifyBitmap(InputStream input) {
        if (input == null) {
            return false;
        }
        Options options = new Options();
        options.inJustDecodeBounds = true;
        if (!(input instanceof BufferedInputStream)) {
            input = new BufferedInputStream(input);
        }
        BitmapFactory.decodeStream(input, null, options);
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (options.outHeight <= 0 || options.outWidth <= 0) {
            return false;
        }
        return true;
    }

    public static boolean verifyBitmap(String path) {
        try {
            return verifyBitmap(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
