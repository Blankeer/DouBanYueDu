package com.douban.book.reader.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.annotation.DimenRes;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.ViewTagKey;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.view.ParagraphView;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.androidannotations.api.view.HasViews;

public class ViewUtils {
    private static final List<String> IGNORE_FROM_VIEW_PATH;

    public static class Builder {
        private static final int BIT_HEIGHT = 2;
        private static final int BIT_MARGIN_BOTTOM = 32;
        private static final int BIT_MARGIN_LEFT = 4;
        private static final int BIT_MARGIN_RIGHT = 16;
        private static final int BIT_MARGIN_TOP = 8;
        private static final int BIT_WEIGHT = 64;
        private static final int BIT_WIDTH = 1;
        private int mBitsChanged;
        private LayoutParams mLayoutParams;
        private Map<Integer, Object> mMap;
        private Collection<Integer> mRuleToRemove;
        private View mView;

        public Builder(View view) {
            this.mMap = new HashMap();
            this.mRuleToRemove = new ArrayList();
            this.mLayoutParams = null;
            this.mView = null;
            this.mBitsChanged = 0;
            this.mView = view;
            this.mLayoutParams = new LinearLayout.LayoutParams(-2, -2);
        }

        public Builder leftMargin(int margin) {
            if (this.mLayoutParams != null && (this.mLayoutParams instanceof MarginLayoutParams)) {
                ((MarginLayoutParams) this.mLayoutParams).leftMargin = margin;
                setBit(BIT_MARGIN_LEFT);
            }
            return this;
        }

        public Builder leftMarginResId(@DimenRes int resId) {
            leftMargin(Res.getDimensionPixelSize(resId));
            return this;
        }

        public Builder topMargin(int margin) {
            if (this.mLayoutParams != null && (this.mLayoutParams instanceof MarginLayoutParams)) {
                ((MarginLayoutParams) this.mLayoutParams).topMargin = margin;
                setBit(BIT_MARGIN_TOP);
            }
            return this;
        }

        public Builder topMarginResId(@DimenRes int resId) {
            topMargin(Res.getDimensionPixelSize(resId));
            return this;
        }

        public Builder bottomMargin(int margin) {
            if (this.mLayoutParams != null && (this.mLayoutParams instanceof MarginLayoutParams)) {
                ((MarginLayoutParams) this.mLayoutParams).bottomMargin = margin;
                setBit(BIT_MARGIN_BOTTOM);
            }
            return this;
        }

        public Builder bottomMarginResId(@DimenRes int resId) {
            bottomMargin(Res.getDimensionPixelSize(resId));
            return this;
        }

        public Builder verticalMargin(int margin) {
            if (this.mLayoutParams != null && (this.mLayoutParams instanceof MarginLayoutParams)) {
                ((MarginLayoutParams) this.mLayoutParams).bottomMargin = margin;
                ((MarginLayoutParams) this.mLayoutParams).topMargin = margin;
                setBit(40);
            }
            return this;
        }

        public Builder height(int height) {
            if (this.mLayoutParams != null) {
                this.mLayoutParams.height = height;
                setBit(BIT_HEIGHT);
            }
            return this;
        }

        public Builder width(int width) {
            if (this.mLayoutParams != null) {
                this.mLayoutParams.width = width;
                setBit(BIT_WIDTH);
            }
            return this;
        }

        public Builder widthMatchParent() {
            return width(-1);
        }

        public Builder widthWrapContent() {
            return width(-2);
        }

        public Builder heightMatchParent() {
            return height(-1);
        }

        public Builder heightWrapContent() {
            return height(-2);
        }

        public Builder weight(int weight) {
            if (this.mLayoutParams instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) this.mLayoutParams).weight = (float) weight;
                setBit(BIT_WEIGHT);
            }
            return this;
        }

        public Builder horizontalPaddingResId(@DimenRes int resId) {
            ViewUtils.setHorizontalPaddingResId(this.mView, resId);
            return this;
        }

        public Builder verticalPaddingResId(@DimenRes int resId) {
            ViewUtils.setVerticalPaddingResId(this.mView, resId);
            return this;
        }

        public Builder alignBottom(@IdRes int id) {
            append(16843146, Integer.valueOf(id));
            return this;
        }

        public Builder append(int attr, Object value) {
            this.mMap.put(Integer.valueOf(attr), value);
            return this;
        }

        public Builder removeRule(int rule) {
            this.mRuleToRemove.add(Integer.valueOf(rule));
            return this;
        }

        public void commit() {
            if (this.mView != null && this.mLayoutParams != null) {
                LayoutParams params = this.mView.getLayoutParams();
                if (params != null) {
                    applyChangedBits(params);
                    this.mView.setLayoutParams(params);
                    this.mView.requestLayout();
                    return;
                }
                this.mView.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
                    public void onViewAttachedToWindow(View v) {
                        LayoutParams params = v.getLayoutParams();
                        Builder.this.applyChangedBits(params);
                        v.setLayoutParams(params);
                        v.removeOnAttachStateChangeListener(this);
                    }

                    public void onViewDetachedFromWindow(View v) {
                    }
                });
            }
        }

        private boolean hasBit(int bit) {
            return (this.mBitsChanged & bit) != 0;
        }

        private void setBit(int bit) {
            this.mBitsChanged |= bit;
        }

        private void applyChangedBits(LayoutParams params) {
            if (params instanceof RelativeLayout.LayoutParams) {
                for (Integer intValue : this.mRuleToRemove) {
                    int rule = intValue.intValue();
                    if (VERSION.SDK_INT >= 17) {
                        ((RelativeLayout.LayoutParams) params).removeRule(rule);
                    } else {
                        ((RelativeLayout.LayoutParams) params).addRule(rule, 0);
                    }
                }
            }
            if (hasBit(BIT_WIDTH)) {
                params.width = this.mLayoutParams.width;
            }
            if (hasBit(BIT_HEIGHT)) {
                params.height = this.mLayoutParams.height;
            }
            if ((params instanceof MarginLayoutParams) && (this.mLayoutParams instanceof MarginLayoutParams)) {
                if (hasBit(BIT_MARGIN_LEFT)) {
                    ((MarginLayoutParams) params).leftMargin = ((MarginLayoutParams) this.mLayoutParams).leftMargin;
                }
                if (hasBit(BIT_MARGIN_TOP)) {
                    ((MarginLayoutParams) params).topMargin = ((MarginLayoutParams) this.mLayoutParams).topMargin;
                }
                if (hasBit(BIT_MARGIN_RIGHT)) {
                    ((MarginLayoutParams) params).rightMargin = ((MarginLayoutParams) this.mLayoutParams).rightMargin;
                }
                if (hasBit(BIT_MARGIN_BOTTOM)) {
                    ((MarginLayoutParams) params).bottomMargin = ((MarginLayoutParams) this.mLayoutParams).bottomMargin;
                }
            }
            if ((params instanceof LinearLayout.LayoutParams) && (this.mLayoutParams instanceof LinearLayout.LayoutParams) && hasBit(BIT_WEIGHT)) {
                ((LinearLayout.LayoutParams) params).weight = ((LinearLayout.LayoutParams) this.mLayoutParams).weight;
            }
            for (Entry<Integer, Object> entry : this.mMap.entrySet()) {
                switch (((Integer) entry.getKey()).intValue()) {
                    case 16843146:
                        if (!(params instanceof RelativeLayout.LayoutParams)) {
                            break;
                        }
                        ((RelativeLayout.LayoutParams) params).addRule(BIT_MARGIN_TOP, ((Integer) entry.getValue()).intValue());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public interface ViewPathNameCustomizable {
        CharSequence getViewPathNodeName();
    }

    public static View createView(Class<?> cls, Context context) {
        try {
            if (ReflectionUtils.isInstanceOf((Class) cls, HasViews.class)) {
                return (View) cls.getDeclaredMethod("build", new Class[]{Context.class}).invoke(null, new Object[]{context});
            }
            return (View) cls.getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
        } catch (Exception e) {
            Logger.e(Tag.GENERAL, e);
            return null;
        }
    }

    public static View createDivider() {
        View view = new View(App.get());
        of(view).height(Res.getDimensionPixelSize(R.dimen.height_horizontal_divider)).widthMatchParent().commit();
        ThemedAttrs.ofView(view).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_divider_horizontal));
        return view;
    }

    public static View createVerticalDivider() {
        View view = new View(App.get());
        of(view).heightMatchParent().width(Res.getDimensionPixelSize(R.dimen.width_vertical_divider)).commit();
        ThemedAttrs.ofView(view).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.bg_divider_vertical));
        return view;
    }

    public static void setHeight(View view, int height) {
        if (view != null) {
            LayoutParams params = view.getLayoutParams();
            if (params != null) {
                params.height = height;
                view.setLayoutParams(params);
            }
        }
    }

    public static void setVerticalPaddingResId(View view, @DimenRes int resId) {
        if (view != null && resId > 0) {
            int padding = Res.getDimensionPixelSize(resId);
            view.setPadding(view.getPaddingLeft(), padding, view.getPaddingRight(), padding);
        }
    }

    public static void setVerticalPadding(View view, int padding) {
        if (view != null) {
            view.setPadding(view.getPaddingLeft(), padding, view.getPaddingRight(), padding);
        }
    }

    public static void setHorizontalPaddingResId(View view, @DimenRes int resId) {
        if (view != null && resId > 0) {
            int padding = Res.getDimensionPixelSize(resId);
            view.setPadding(padding, view.getPaddingTop(), padding, view.getPaddingBottom());
        }
    }

    public static void setHorizontalPadding(View view, int padding) {
        if (view != null) {
            view.setPadding(padding, view.getPaddingTop(), padding, view.getPaddingBottom());
        }
    }

    public static void setBottomPaddingResId(View view, @DimenRes int resId) {
        if (view != null && resId > 0) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), Res.getDimensionPixelSize(resId));
        }
    }

    public static void setBottomPadding(View view, int padding) {
        if (view != null) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), padding);
        }
    }

    public static void setPaddingResId(View view, @DimenRes int resId) {
        if (view != null && resId > 0) {
            int padding = Res.getDimensionPixelSize(resId);
            view.setPadding(padding, padding, padding, padding);
        }
    }

    public static void setPadding(View view, int padding) {
        if (view != null) {
            view.setPadding(padding, padding, padding, padding);
        }
    }

    public static void setTopPaddingResId(View view, @DimenRes int resId) {
        if (view != null && resId > 0) {
            view.setPadding(view.getPaddingLeft(), Res.getDimensionPixelSize(resId), view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    public static void setTopPadding(View view, int padding) {
        if (view != null) {
            view.setPadding(view.getPaddingLeft(), padding, view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    public static void setRightPaddingResId(View view, @DimenRes int resId) {
        if (view != null && resId > 0) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), Res.getDimensionPixelSize(resId), view.getPaddingBottom());
        }
    }

    public static void setRightPadding(View view, int padding) {
        if (view != null) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), padding, view.getPaddingBottom());
        }
    }

    public static void setLeftPadding(View view, int padding) {
        if (view != null) {
            view.setPadding(padding, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    public static void setTopMargin(View view, int topMargin) {
        if (view != null) {
            of(view).topMargin(topMargin).commit();
        }
    }

    public static void setTopMarginResId(View view, int resId) {
        if (view != null) {
            of(view).topMarginResId(resId).commit();
        }
    }

    public static void setBottomMargin(View view, int bottomMargin) {
        if (view != null) {
            of(view).bottomMargin(bottomMargin).commit();
        }
    }

    public static void setLeftMargin(View view, int leftMargin) {
        if (view != null) {
            of(view).leftMargin(leftMargin).commit();
        }
    }

    public static void setAlpha(View view, int alpha) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setAlpha(((ViewGroup) view).getChildAt(i), alpha);
                if (view.getBackground() != null) {
                    view.getBackground().setAlpha(alpha);
                }
            }
        } else if (view instanceof ImageView) {
            if (((ImageView) view).getDrawable() != null) {
                ((ImageView) view).getDrawable().setAlpha(alpha);
            }
            if (view.getBackground() != null) {
                view.getBackground().setAlpha(alpha);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(((TextView) view).getTextColors().withAlpha(alpha));
            if (view.getBackground() != null) {
                view.getBackground().setAlpha(alpha);
            }
        }
    }

    public static void setDrawableLeft(TextView textView, Drawable drawableLeft) {
        Drawable[] drawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawables[1], drawables[2], drawables[3]);
    }

    public static void setDrawableTop(TextView textView, Drawable drawableTop) {
        setDrawableTop(textView, drawableTop, 1.85f);
    }

    public static void setDrawableTopLarge(TextView textView, Drawable drawableTop) {
        setDrawableTop(textView, drawableTop, 2.75f);
    }

    public static void setDrawableTop(TextView textView, Drawable drawableTop, float ratio) {
        if (Res.isVectorDrawable(drawableTop)) {
            int size = Math.round(textView.getTextSize() * ratio);
            drawableTop.setBounds(0, 0, size, size);
            drawableTop.setColorFilter(textView.getTextColors().getDefaultColor(), Mode.SRC_IN);
        }
        Drawable[] drawables = textView.getCompoundDrawables();
        textView.setCompoundDrawables(drawables[0], drawableTop, drawables[2], drawables[3]);
    }

    public static void setImageDrawable(ImageView imageView, Drawable drawable) {
        setImageDrawable(imageView, drawable, null);
    }

    public static void setImageDrawable(ImageView imageView, Drawable drawable, ColorStateList colorStateList) {
        imageView.setImageDrawable(drawable);
    }

    public static void setDrawableTop(TextView textView, int resId) {
        setDrawableTop(textView, Res.getDrawable(resId));
    }

    public static void setDrawableLeft(TextView textView, int resId) {
        setDrawableLeft(textView, Res.getDrawable(resId));
    }

    public static void setImageResource(ImageView imageView, int resId) {
        setImageDrawable(imageView, Res.getDrawable(resId));
    }

    public static void setImageResource(ImageView imageView, int resId, ColorStateList colorStateList) {
        setImageDrawable(imageView, Res.getDrawable(resId), colorStateList);
    }

    @TargetApi(16)
    public static void setBackground(View view, Drawable background) {
        if (VERSION.SDK_INT >= 16) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    @TargetApi(11)
    public static void setSoftLayerType(View view) {
        if (VERSION.SDK_INT >= 11 && view != null) {
            view.setLayerType(1, null);
        }
    }

    @TargetApi(11)
    public static boolean isSoftLayerType(View view) {
        if (VERSION.SDK_INT < 11) {
            return true;
        }
        if (view == null || view.getLayerType() != 1) {
            return false;
        }
        return true;
    }

    public static void showTextIfNotEmpty(TextView textView, CharSequence text) {
        showTextIf(StringUtils.isNotEmpty(text), textView, text);
    }

    public static void showTextIf(boolean condition, TextView textView, CharSequence text) {
        if (condition) {
            showText(textView, text);
        } else {
            textView.setVisibility(8);
        }
    }

    public static void showText(TextView textView, CharSequence text) {
        if (textView instanceof ParagraphView) {
            ((ParagraphView) textView).setParagraphText(text);
        } else {
            textView.setText(text);
        }
        visible(textView);
    }

    public static void visibleIf(boolean condition, View... views) {
        for (View view : views) {
            view.setVisibility(condition ? 0 : 4);
        }
    }

    public static void invisibleIf(boolean condition, View... views) {
        for (View view : views) {
            int i;
            if (condition) {
                i = 4;
            } else {
                i = 0;
            }
            view.setVisibility(i);
        }
    }

    public static void showIf(boolean condition, View... views) {
        for (View view : views) {
            view.setVisibility(condition ? 0 : 8);
        }
    }

    public static void goneIf(boolean condition, View... views) {
        for (View view : views) {
            int i;
            if (condition) {
                i = 8;
            } else {
                i = 0;
            }
            view.setVisibility(i);
        }
    }

    public static void visible(View... views) {
        for (View view : views) {
            view.setVisibility(0);
        }
    }

    public static void visibleWithAnim(int animResId, View... views) {
        for (View view : views) {
            view.setVisibility(0);
            view.startAnimation(Res.getAnimation(animResId));
        }
    }

    public static boolean isVisible(View view) {
        return view != null && view.getVisibility() == 0;
    }

    public static boolean isAnyVisible(View... views) {
        for (View view : views) {
            if (isVisible(view)) {
                return true;
            }
        }
        return false;
    }

    public static void invisible(View... views) {
        for (View view : views) {
            view.setVisibility(4);
        }
    }

    public static void invisibleWithAnim(int animResId, View... views) {
        for (View view : views) {
            view.setVisibility(4);
            view.startAnimation(Res.getAnimation(animResId));
        }
    }

    public static void gone(View... views) {
        for (View view : views) {
            view.setVisibility(8);
        }
    }

    public static void goneWithAnim(int animResId, View... views) {
        for (View view : views) {
            boolean wasVisible = isVisible(view);
            view.setVisibility(8);
            if (wasVisible) {
                view.startAnimation(Res.getAnimation(animResId));
            }
        }
    }

    public static void enable(View... views) {
        for (View view : views) {
            view.setEnabled(true);
        }
    }

    public static void enableIf(boolean condition, View... views) {
        for (View view : views) {
            view.setEnabled(condition);
        }
    }

    public static void disable(View... views) {
        for (View view : views) {
            view.setEnabled(false);
        }
    }

    public static void disableIf(boolean condition, View... views) {
        for (View view : views) {
            boolean z;
            if (condition) {
                z = false;
            } else {
                z = true;
            }
            view.setEnabled(z);
        }
    }

    public static void disableChildrenIf(boolean condition, ViewGroup viewGroup) {
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                disableIf(condition, viewGroup.getChildAt(i));
            }
        }
    }

    public static Activity getAttachedActivity(View view) {
        if (view == null) {
            return null;
        }
        IBinder token = view.getWindowToken();
        if (token != null) {
            Activity activity = WindowActivityCache.get(token.hashCode());
            if (activity != null) {
                return activity;
            }
        }
        return null;
    }

    public static BaseFragment getAttachedFragment(View view) {
        for (ViewParent viewParent = view; viewParent instanceof View; viewParent = ((View) viewParent).getParent()) {
            Object obj = ((View) viewParent).getTag(ViewTagKey.ATTACHED_FRAGMENT);
            if (obj instanceof BaseFragment) {
                return (BaseFragment) obj;
            }
        }
        return null;
    }

    static {
        IGNORE_FROM_VIEW_PATH = new ArrayList();
        IGNORE_FROM_VIEW_PATH.add("LinearLayout");
        IGNORE_FROM_VIEW_PATH.add("RelativeLayout");
        IGNORE_FROM_VIEW_PATH.add("FrameLayout");
        IGNORE_FROM_VIEW_PATH.add("FlowLayout");
        IGNORE_FROM_VIEW_PATH.add("ScrollView");
    }

    public static String getViewPath(View view) {
        StringBuilder pathBuilder = new StringBuilder();
        Object obj = view;
        while ((obj instanceof View) && !(((View) obj).getTag(ViewTagKey.ATTACHED_FRAGMENT) instanceof BaseFragment)) {
            String currentNodeName = obj.getClass().getSimpleName();
            if (IterableUtils.containsNone(IGNORE_FROM_VIEW_PATH, currentNodeName)) {
                if (currentNodeName.endsWith(EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR)) {
                    currentNodeName = currentNodeName.substring(0, currentNodeName.length() - 1);
                }
                pathBuilder.insert(0, currentNodeName);
                pathBuilder.insert(0, "/");
            }
            obj = ((View) obj).getParent();
        }
        return pathBuilder.toString();
    }

    public static void setTextAppearance(Context context, TextView textView, int styleResId) {
        if (context != null && textView != null && styleResId > 0) {
            textView.setTextAppearance(context, styleResId);
            Utils.changeFonts(textView);
            ThemedUtils.applyThemeToView(textView, styleResId);
        }
    }

    public static void setEventAware(View view) {
        if (view != null) {
            view.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
                public void onViewAttachedToWindow(View v) {
                    EventBusUtils.register(v);
                }

                public void onViewDetachedFromWindow(View v) {
                    EventBusUtils.unregister(v);
                }
            });
        }
    }

    public static Builder of(View view) {
        return new Builder(view);
    }
}
