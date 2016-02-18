package com.sina.weibo.sdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import io.realm.internal.Table;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    private static void revitionImageSizeHD(String picfile, int size, int quality) throws IOException {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0!");
        } else if (!isFileExisted(picfile)) {
            if (picfile == null) {
                picfile = "null";
            }
            throw new FileNotFoundException(picfile);
        } else if (BitmapHelper.verifyBitmap(picfile)) {
            int photoSizesOrg = size * 2;
            FileInputStream input = new FileInputStream(picfile);
            Options opts = new Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, opts);
            try {
                input.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            int i = 0;
            while (true) {
                if ((opts.outWidth >> i) <= photoSizesOrg) {
                    if ((opts.outHeight >> i) <= photoSizesOrg) {
                        break;
                    }
                }
                i++;
            }
            opts.inSampleSize = (int) Math.pow(2.0d, (double) i);
            opts.inJustDecodeBounds = false;
            Bitmap temp = safeDecodeBimtapFile(picfile, opts);
            if (temp == null) {
                throw new IOException("Bitmap decode error!");
            }
            deleteDependon(picfile);
            makesureFileExist(picfile);
            float rateOutPut = ((float) size) / ((float) (temp.getWidth() > temp.getHeight() ? temp.getWidth() : temp.getHeight()));
            if (rateOutPut < 1.0f) {
                Bitmap outputBitmap;
                while (true) {
                    try {
                        outputBitmap = Bitmap.createBitmap((int) (((float) temp.getWidth()) * rateOutPut), (int) (((float) temp.getHeight()) * rateOutPut), Config.ARGB_8888);
                        break;
                    } catch (OutOfMemoryError e) {
                        System.gc();
                        rateOutPut = (float) (((double) rateOutPut) * 0.8d);
                    }
                }
                if (outputBitmap == null) {
                    temp.recycle();
                }
                Canvas canvas = new Canvas(outputBitmap);
                Matrix matrix = new Matrix();
                matrix.setScale(rateOutPut, rateOutPut);
                canvas.drawBitmap(temp, matrix, new Paint());
                temp.recycle();
                temp = outputBitmap;
            }
            FileOutputStream output = new FileOutputStream(picfile);
            if (!(opts == null || opts.outMimeType == null)) {
                if (opts.outMimeType.contains("png")) {
                    temp.compress(CompressFormat.PNG, quality, output);
                    output.close();
                    temp.recycle();
                }
            }
            temp.compress(CompressFormat.JPEG, quality, output);
            try {
                output.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            temp.recycle();
        } else {
            throw new IOException(Table.STRING_DEFAULT_VALUE);
        }
    }

    private static void revitionImageSize(String picfile, int size, int quality) throws IOException {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0!");
        } else if (!isFileExisted(picfile)) {
            if (picfile == null) {
                picfile = "null";
            }
            throw new FileNotFoundException(picfile);
        } else if (BitmapHelper.verifyBitmap(picfile)) {
            FileInputStream input = new FileInputStream(picfile);
            Options opts = new Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, opts);
            try {
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int i = 0;
            while (true) {
                if ((opts.outWidth >> i) <= size && (opts.outHeight >> i) <= size) {
                    break;
                }
                i++;
            }
            opts.inSampleSize = (int) Math.pow(2.0d, (double) i);
            opts.inJustDecodeBounds = false;
            Bitmap temp = safeDecodeBimtapFile(picfile, opts);
            if (temp == null) {
                throw new IOException("Bitmap decode error!");
            }
            deleteDependon(picfile);
            makesureFileExist(picfile);
            FileOutputStream output = new FileOutputStream(picfile);
            if (opts == null || opts.outMimeType == null || !opts.outMimeType.contains("png")) {
                temp.compress(CompressFormat.JPEG, quality, output);
            } else {
                temp.compress(CompressFormat.PNG, quality, output);
            }
            try {
                output.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            temp.recycle();
        } else {
            throw new IOException(Table.STRING_DEFAULT_VALUE);
        }
    }

    public static boolean revitionPostImageSize(Context context, String picfile) {
        try {
            if (NetworkHelper.isWifiValid(context)) {
                revitionImageSizeHD(picfile, 1600, 75);
            } else {
                revitionImageSize(picfile, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, 75);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Bitmap safeDecodeBimtapFile(String bmpFile, Options opts) {
        FileInputStream input;
        OutOfMemoryError e;
        Options optsTmp = opts;
        if (optsTmp == null) {
            optsTmp = new Options();
            optsTmp.inSampleSize = 1;
        }
        Bitmap bmp = null;
        int i = 0;
        FileInputStream input2 = null;
        while (i < 5) {
            try {
                input = new FileInputStream(bmpFile);
                try {
                    bmp = BitmapFactory.decodeStream(input, null, opts);
                    try {
                        input.close();
                        break;
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        break;
                    }
                } catch (OutOfMemoryError e3) {
                    e = e3;
                } catch (FileNotFoundException e4) {
                }
            } catch (OutOfMemoryError e5) {
                e = e5;
                input = input2;
                e.printStackTrace();
                optsTmp.inSampleSize *= 2;
                try {
                    input.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                i++;
                input2 = input;
            } catch (FileNotFoundException e6) {
                input = input2;
            }
        }
        input = input2;
        return bmp;
    }

    private static void delete(File file) {
        if (file != null && file.exists() && !file.delete()) {
            throw new RuntimeException(file.getAbsolutePath() + " doesn't be deleted!");
        }
    }

    private static boolean deleteDependon(String filepath) {
        if (TextUtils.isEmpty(filepath)) {
            return false;
        }
        File file = new File(filepath);
        int retryCount = 1;
        int maxRetryCount = 0;
        if (0 < 1) {
            maxRetryCount = 5;
        }
        boolean isDeleted = false;
        if (file == null) {
            return false;
        }
        while (!isDeleted && retryCount <= maxRetryCount && file.isFile() && file.exists()) {
            isDeleted = file.delete();
            if (!isDeleted) {
                retryCount++;
            }
        }
        return isDeleted;
    }

    private static boolean isFileExisted(String filepath) {
        if (TextUtils.isEmpty(filepath)) {
            return false;
        }
        File file = new File(filepath);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    private static boolean isParentExist(File file) {
        if (file == null) {
            return false;
        }
        File parent = file.getParentFile();
        if (parent == null || parent.exists()) {
            return false;
        }
        if (file.exists() || file.mkdirs()) {
            return true;
        }
        return false;
    }

    private static void makesureFileExist(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            if (file != null && !file.exists() && isParentExist(file)) {
                if (file.exists()) {
                    delete(file);
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isWifi(Context mContext) {
        NetworkInfo activeNetInfo = ((ConnectivityManager) mContext.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetInfo == null || activeNetInfo.getType() != 1) {
            return false;
        }
        return true;
    }
}
