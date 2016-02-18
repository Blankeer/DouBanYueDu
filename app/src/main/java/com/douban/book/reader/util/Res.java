package com.douban.book.reader.util;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build.VERSION;
import android.support.annotation.AnimRes;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.douban.book.reader.app.App;
import com.douban.book.reader.drawable.DrawableWrapper;
import com.douban.book.reader.manager.FontScaleManager;
import com.douban.book.reader.manager.FontScaleManager_;
import com.douban.book.reader.theme.Theme;
import com.wnafee.vector.compat.ResourcesCompat;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.List;

public class Res {
    private static final String TAG;
    private static LruCache<Integer, Animation> sAnimationCache;
    private static final Resources sRes;
    private static final FontScaleManager sScaleManager;

    /* renamed from: com.douban.book.reader.util.Res.1 */
    static class AnonymousClass1 extends LruCache<Integer, Animation> {
        AnonymousClass1(int x0) {
            super(x0);
        }

        protected Animation create(Integer key) {
            return AnimationUtils.loadAnimation(App.get(), key.intValue());
        }
    }

    static {
        TAG = Res.class.getSimpleName();
        sRes = App.get().getResources();
        sScaleManager = FontScaleManager_.getInstance_(App.get());
        sAnimationCache = new AnonymousClass1(5);
    }

    public static Resources get() {
        return sRes;
    }

    public static ColorDrawable getColorDrawable(int colorResId) {
        return new ColorDrawable(sRes.getColor(colorResId));
    }

    public static int getColor(@ColorRes @ArrayRes int id) {
        if (isThemedResId(id)) {
            return Theme.getColor(id);
        }
        return sRes.getColor(id);
    }

    public static ColorStateList getColorStateList(@ColorRes @ArrayRes int id) {
        if (isThemedResId(id)) {
            return Theme.getColorStateList(id);
        }
        return sRes.getColorStateList(id);
    }

    public static float getDimension(@DimenRes int id) {
        return sRes.getDimension(id);
    }

    public static float getScaledDimension(@ArrayRes int id) {
        TypedArray array = sRes.obtainTypedArray(id);
        float result = array.getDimension(sScaleManager.getScale(), 0.0f);
        array.recycle();
        return result;
    }

    public static int getDimensionPixelSize(@DimenRes int id) {
        return sRes.getDimensionPixelSize(id);
    }

    public static Drawable getDrawable(@DrawableRes @ArrayRes int id) {
        if (id <= 0) {
            return null;
        }
        if (isThemedResId(id)) {
            return Theme.getDrawable(id);
        }
        return ResourcesCompat.getDrawable(App.get(), id);
    }

    public static Drawable getDrawableWithTint(@DrawableRes @ArrayRes int id, @ColorRes @ArrayRes int tintRes) {
        return getDrawableWithTint(getDrawable(id), tintRes);
    }

    public static Drawable getDrawableWithTint(Drawable drawable, @ColorRes @ArrayRes int tintRes) {
        if (tintRes <= 0) {
            return drawable;
        }
        Drawable result = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(result, getColor(tintRes));
        return result;
    }

    public static Animation getAnimation(@AnimRes int id) {
        return (Animation) sAnimationCache.get(Integer.valueOf(id));
    }

    public static DisplayMetrics getDisplayMetrics() {
        return sRes.getDisplayMetrics();
    }

    public static String getString(@StringRes int id) {
        try {
            return sRes.getString(id);
        } catch (NotFoundException e) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    public static String getString(@StringRes int id, Object... formatArgs) {
        return sRes.getString(id, formatArgs);
    }

    public static List<String> getStringArray(int id) {
        List<String> itemStrings = new ArrayList();
        TypedArray array = sRes.obtainTypedArray(id);
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                itemStrings.add(array.getString(i));
            }
            array.recycle();
        }
        return itemStrings;
    }

    public static List<Integer> getIntegerArray(int id) {
        List<Integer> itemIds = new ArrayList();
        TypedArray array = sRes.obtainTypedArray(id);
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                itemIds.add(Integer.valueOf(array.getResourceId(i, 0)));
            }
            array.recycle();
        }
        return itemIds;
    }

    public static int parseColor(String color) {
        return parseColor(color, 17170445);
    }

    public static int parseColor(String color, int defaultColorResId) {
        try {
            return Color.parseColor(color);
        } catch (Exception e) {
            Logger.e(TAG, e);
            return getColor(defaultColorResId);
        }
    }

    public static int parseColorOverridingAlpha(String color, float alpha, int defaultColorResId) {
        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException("alpha must between 0 and 1.");
        }
        try {
            int result = Color.parseColor(color);
            return Color.argb(Math.round(255.0f * alpha), Color.red(result), Color.green(result), Color.blue(result));
        } catch (Exception e) {
            Logger.e(TAG, e);
            return getColor(defaultColorResId);
        }
    }

    public static int getColorOverridingAlpha(@ColorRes @ArrayRes int resId, float alpha) {
        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException("alpha must between 0 and 1.");
        }
        int result = getColor(resId);
        return Color.argb(Math.round(255.0f * alpha), Color.red(result), Color.green(result), Color.blue(result));
    }

    public static boolean isVectorDrawable(Drawable drawable) {
        while (drawable instanceof DrawableWrapper) {
            drawable = ((DrawableWrapper) drawable).getWrappedDrawable();
        }
        if (VERSION.SDK_INT >= 21) {
            return drawable instanceof VectorDrawable;
        }
        return drawable instanceof com.wnafee.vector.compat.VectorDrawable;
    }

    private static boolean isThemedResId(int resId) {
        if (resId <= 0) {
            return false;
        }
        return StringUtils.equals(sRes.getResourceTypeName(resId), (CharSequence) "array");
    }
}
