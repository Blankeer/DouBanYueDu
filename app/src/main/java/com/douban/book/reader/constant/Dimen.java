package com.douban.book.reader.constant;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.douban.book.reader.app.App;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import com.wnafee.vector.R;
import io.fabric.sdk.android.services.common.AbstractSpiCall;

public class Dimen {
    public static final float CORNER_RADIUS;
    public static final float MIN_TOUCH_AREA_SIZE;
    public static final int NAVIGATION_BAR_HEIGHT;
    public static final int SHADOW_WIDTH;
    public static final int STATUS_BAR_HEIGHT;
    private static final String TAG;
    public static final int VERTICAL_NAVIGATION_BAR_WIDTH;

    static {
        TAG = Dimen.class.getSimpleName();
        STATUS_BAR_HEIGHT = getStatusBarHeight();
        NAVIGATION_BAR_HEIGHT = getNavigationBarHeight();
        VERTICAL_NAVIGATION_BAR_WIDTH = getVerticalNavigationBarWidth();
        SHADOW_WIDTH = Utils.dp2pixel(3.0f);
        CORNER_RADIUS = (float) Utils.dp2pixel(2.0f);
        MIN_TOUCH_AREA_SIZE = (float) Utils.dp2pixel(48.0f);
    }

    public static boolean isSmallScreen() {
        return App.get().getPageWidth() < Utils.dp2pixel(600.0f);
    }

    public static boolean isLargeScreen() {
        return App.get().getPageWidth() >= Utils.dp2pixel(500.0f);
    }

    private static int getStatusBarHeight() {
        int result = Utils.dp2pixel(25.0f);
        int resourceId = Res.get().getIdentifier("status_bar_height", "dimen", AbstractSpiCall.ANDROID_CLIENT_TYPE);
        if (resourceId > 0) {
            return Res.get().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static int getNavigationBarHeight() {
        Resources resources = Res.get();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", AbstractSpiCall.ANDROID_CLIENT_TYPE);
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return Utils.dp2pixel(48.0f);
    }

    private static int getVerticalNavigationBarWidth() {
        Resources resources = Res.get();
        int resourceId = resources.getIdentifier("navigation_bar_width", "dimen", AbstractSpiCall.ANDROID_CLIENT_TYPE);
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return Utils.dp2pixel(42.0f);
    }

    public static int getActionBarHeight() {
        TypedArray a = App.get().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        if (a == null) {
            return 0;
        }
        int result = a.getDimensionPixelSize(0, -1);
        a.recycle();
        return result;
    }

    public static int getRealHeight() {
        int height;
        if (VERSION.SDK_INT >= 17) {
            height = getRealHeight17();
        } else {
            height = getRealHeightLegacy();
        }
        if (height <= 0) {
            return Res.getDisplayMetrics().heightPixels;
        }
        return height;
    }

    @TargetApi(17)
    private static int getRealHeight17() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) App.get().getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
        return metrics.heightPixels;
    }

    private static int getRealHeightLegacy() {
        try {
            return ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(((WindowManager) App.get().getSystemService("window")).getDefaultDisplay(), new Object[0])).intValue();
        } catch (Exception e) {
            Logger.e(TAG, e);
            return 0;
        }
    }

    public static int getRealWidth() {
        int width;
        if (VERSION.SDK_INT >= 17) {
            width = getRealWidth17();
        } else {
            width = getRealWidthLegacy();
        }
        if (width <= 0) {
            return Res.getDisplayMetrics().widthPixels;
        }
        return width;
    }

    @TargetApi(17)
    private static int getRealWidth17() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) App.get().getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
        return metrics.widthPixels;
    }

    private static int getRealWidthLegacy() {
        try {
            return ((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(((WindowManager) App.get().getSystemService("window")).getDefaultDisplay(), new Object[0])).intValue();
        } catch (Exception e) {
            Logger.e(TAG, e);
            return 0;
        }
    }
}
