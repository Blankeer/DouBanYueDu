package com.douban.book.reader.util;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.ViewTagKey;
import com.douban.book.reader.drawable.RoundCornerColorDrawable;
import com.douban.book.reader.exception.AttrNotFoundException;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.theme.Theme.Name;
import com.douban.book.reader.theme.ThemedAttrs;
import io.github.zzn2.colorize.Colorize;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ThemedUtils {
    public static ColorFilter NIGHT_MODE_COLOR_FILTER;
    public static final int NIGHT_MODE_MASK_COLOR;
    private static final String TAG;

    static {
        TAG = ThemedUtils.class.getSimpleName();
        NIGHT_MODE_MASK_COLOR = Res.getColorOverridingAlpha(17170444, CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO);
        NIGHT_MODE_COLOR_FILTER = new PorterDuffColorFilter(NIGHT_MODE_MASK_COLOR, Mode.SRC_ATOP);
    }

    public static void updateViewTreeIfThemeChanged(View view) {
        if (view != null) {
            Name currentTheme = Theme.getCurrent();
            Name tag = view.getTag(ViewTagKey.CURRENT_THEME);
            if (tag instanceof Name) {
                if (tag != currentTheme) {
                    updateViewTree(view);
                } else {
                    return;
                }
            }
            view.setTag(ViewTagKey.CURRENT_THEME, currentTheme);
        }
    }

    public static void updateViewTree(View view) {
        if (view != null) {
            if (view instanceof ViewGroup) {
                int childCount = ((ViewGroup) view).getChildCount();
                for (int index = 0; index < childCount; index++) {
                    updateViewTree(((ViewGroup) view).getChildAt(index));
                }
            }
            updateView(view);
        }
    }

    public static void updateView(View view) {
        if (view != null) {
            Object tag = view.getTag(ViewTagKey.THEME);
            if (tag instanceof ThemedAttrs) {
                applyThemeToView(view, Theme.getMultiThemed(), (ThemedAttrs) tag);
            }
        }
    }

    public static void applyThemeToView(View view, Theme theme, ThemedAttrs themedAttrs) {
        for (Integer intValue : themedAttrs.attrs()) {
            int attr = intValue.intValue();
            Drawable[] drawables;
            switch (attr) {
                case R.attr.autoDimInNightMode /*2130772203*/:
                    try {
                        if (!(view instanceof ImageView)) {
                            if (!(view instanceof ProgressBar)) {
                                if (themedAttrs.getBoolean(attr) && view != null) {
                                    setAutoDimInNightMode(view.getBackground());
                                    break;
                                }
                            } else if (VERSION.SDK_INT >= 21) {
                                ViewCompat.setProgressBarTint((ProgressBar) view, Theme.isNight() ? ColorStateList.valueOf(NIGHT_MODE_MASK_COLOR) : null, Mode.SRC_ATOP);
                                break;
                            } else {
                                break;
                            }
                        } else if (!themedAttrs.getBoolean(attr)) {
                            break;
                        } else {
                            ColorFilter colorFilter;
                            ImageView imageView = (ImageView) view;
                            if (Theme.isNight()) {
                                colorFilter = NIGHT_MODE_COLOR_FILTER;
                            } else {
                                colorFilter = null;
                            }
                            imageView.setColorFilter(colorFilter);
                            break;
                        }
                    } catch (AttrNotFoundException e) {
                        break;
                    }
                case R.attr.backgroundColorArray /*2130772204*/:
                    Drawable background = view.getBackground();
                    int color = theme.getThemedColor(themedAttrs.getResourceId(attr));
                    if (!(background instanceof RoundCornerColorDrawable)) {
                        view.setBackgroundColor(color);
                        break;
                    } else {
                        ((RoundCornerColorDrawable) background).setColor(color);
                        break;
                    }
                case R.attr.backgroundDrawableArray /*2130772205*/:
                    view.setBackgroundResource(theme.getThemedResId(themedAttrs.getResourceId(attr)));
                    break;
                case R.attr.textColorArray /*2130772206*/:
                    if (!(view instanceof TextView)) {
                        break;
                    }
                    ((TextView) view).setTextColor(theme.getThemedColorStateList(themedAttrs.getResourceId(attr)));
                    if (!themedAttrs.getBoolean(R.attr.colorizeDrawableEnabled)) {
                        break;
                    }
                    Colorize.applyTo(view);
                    break;
                case R.attr.textColorHintArray /*2130772207*/:
                    if (!(view instanceof TextView)) {
                        break;
                    }
                    ((TextView) view).setHintTextColor(theme.getThemedColorStateList(themedAttrs.getResourceId(attr)));
                    break;
                case R.attr.drawableTopArray /*2130772208*/:
                    if (!(view instanceof TextView)) {
                        break;
                    }
                    drawables = ((TextView) view).getCompoundDrawables();
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(drawables[0], theme.getThemedDrawable(themedAttrs.getResourceId(attr)), drawables[2], drawables[3]);
                    break;
                case R.attr.drawableLeftArray /*2130772209*/:
                    if (!(view instanceof TextView)) {
                        break;
                    }
                    drawables = ((TextView) view).getCompoundDrawables();
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(theme.getThemedDrawable(themedAttrs.getResourceId(attr)), drawables[1], drawables[2], drawables[3]);
                    break;
                case R.attr.srcArray /*2130772210*/:
                    if (!(view instanceof ImageView)) {
                        break;
                    }
                    ((ImageView) view).setImageResource(theme.getThemedResId(themedAttrs.getResourceId(attr)));
                    break;
                case R.attr.dividerArray /*2130772211*/:
                    Drawable drawable = theme.getThemedDrawable(themedAttrs.getResourceId(attr));
                    if (!(view instanceof LinearLayout)) {
                        if (!(view instanceof ListView)) {
                            if (!(view instanceof StickyListHeadersListView)) {
                                break;
                            }
                            ((StickyListHeadersListView) view).setDivider(drawable);
                            ((StickyListHeadersListView) view).setDividerHeight(Res.getDimensionPixelSize(R.dimen.height_horizontal_divider));
                            break;
                        }
                        ((ListView) view).setDivider(drawable);
                        break;
                    }
                    ((LinearLayout) view).setDividerDrawable(drawable);
                    break;
                case R.attr.roundCornerStrokeColorArray /*2130772212*/:
                    Drawable viewBackground = view.getBackground();
                    if (!(viewBackground instanceof RoundCornerColorDrawable)) {
                        break;
                    }
                    ((RoundCornerColorDrawable) viewBackground).setStrokeColor(theme.getThemedColor(themedAttrs.getResourceId(attr)));
                    break;
                default:
                    break;
            }
        }
    }

    public static void applyThemeToView(View view, int resId) {
        if (view != null && resId > 0) {
            int[] attrs = R.styleable.ThemedView;
            TypedArray a = App.get().obtainStyledAttributes(resId, attrs);
            ThemedAttrs themedAttrs = ThemedAttrs.ofView(view);
            AttrUtils.loadTypedArrayToThemedAttrs(a, attrs, themedAttrs);
            themedAttrs.updateView();
        }
    }

    public static void setAutoDimInNightMode(Drawable drawable) {
        if (drawable != null) {
            drawable.setColorFilter(Theme.isNight() ? NIGHT_MODE_COLOR_FILTER : null);
        }
    }
}
