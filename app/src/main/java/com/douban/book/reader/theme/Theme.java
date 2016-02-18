package com.douban.book.reader.theme;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.Res;

public class Theme {
    public static final int BLACK = 1;
    public static final int DEFAULT = 0;
    private static final int DEFAULT_COLOR = 0;
    private static final Theme MULTI_THEMED;
    private static final Theme SINGLE_THEMED;
    public static final int WHITE = 0;
    private static int mColorTheme;
    private static int mOverrideColorTheme;
    private static final Resources res;

    public enum Name {
        DAY,
        NIGHT
    }

    static {
        res = App.get().getResources();
        mOverrideColorTheme = -1;
        mColorTheme = -1;
        MULTI_THEMED = new Theme();
        SINGLE_THEMED = new Theme() {
            protected int getCurrentColorTheme() {
                return 0;
            }
        };
    }

    public static Theme getMultiThemed() {
        return MULTI_THEMED;
    }

    public static Theme getSingleThemed() {
        return SINGLE_THEMED;
    }

    public static void setColorTheme(int theme) {
        if (theme != mColorTheme) {
            mColorTheme = theme;
            Pref.ofApp().set(Key.SETTING_THEME, Integer.valueOf(theme));
            EventBusUtils.post(new ColorThemeChangedEvent());
            Analysis.sendPrefChangedEvent(Key.SETTING_THEME, getCurrent());
        }
    }

    public static void setOverrideColorTheme(int theme) {
    }

    public static void clearOverrideColorTheme() {
        mOverrideColorTheme = -1;
    }

    public static boolean isNight() {
        return getCurrent() == Name.NIGHT;
    }

    public static Name getCurrent() {
        return getCurrentTheme() == BLACK ? Name.NIGHT : Name.DAY;
    }

    @Deprecated
    public static int getCurrentTheme() {
        return getMultiThemed().getCurrentColorTheme();
    }

    protected int getCurrentColorTheme() {
        if (mOverrideColorTheme >= 0) {
            return mOverrideColorTheme;
        }
        if (mColorTheme < 0) {
            mColorTheme = Pref.ofApp().getInt(Key.SETTING_THEME, 0);
        }
        return mColorTheme;
    }

    public static int getColor(int resId) {
        return getMultiThemed().getThemedColor(resId);
    }

    public static ColorStateList getColorStateList(int resId) {
        return getMultiThemed().getThemedColorStateList(resId);
    }

    public static Drawable getDrawable(int resId) {
        return getMultiThemed().getThemedDrawable(resId);
    }

    public static int getResId(int resId) {
        return getMultiThemed().getThemedResId(resId);
    }

    public int getThemedColor(int resId) {
        TypedArray colors = res.obtainTypedArray(resId);
        if (colors == null) {
            return 0;
        }
        int color = colors.getColor(getCurrentColorTheme(), 0);
        colors.recycle();
        return color;
    }

    public ColorStateList getThemedColorStateList(int resId) {
        TypedArray colors = res.obtainTypedArray(resId);
        if (colors == null) {
            return ColorStateList.valueOf(Res.getColor(R.color.black));
        }
        ColorStateList colorStateList = colors.getColorStateList(getCurrentColorTheme());
        colors.recycle();
        return colorStateList;
    }

    public Drawable getThemedDrawable(int resId) {
        TypedArray drawables = res.obtainTypedArray(resId);
        if (drawables == null) {
            return null;
        }
        Drawable drawable = drawables.getDrawable(getCurrentColorTheme());
        drawables.recycle();
        return drawable;
    }

    public int getThemedResId(int resId) {
        TypedArray array = res.obtainTypedArray(resId);
        if (array == null) {
            return 0;
        }
        int result = array.getResourceId(getCurrentColorTheme(), 0);
        array.recycle();
        return result;
    }
}
