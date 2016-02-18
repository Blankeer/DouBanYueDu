package com.douban.book.reader.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.ViewTagKey;
import com.douban.book.reader.drawable.RoundCornerColorDrawable;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.AttrUtils;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;

public class ArkLayoutInflaterFactory implements LayoutInflaterFactory {
    private static final String[] sClassPrefixList;
    private LayoutInflaterFactory mWrappedFactory;

    static {
        sClassPrefixList = new String[]{"android.widget.", "android.webkit."};
    }

    public ArkLayoutInflaterFactory(LayoutInflaterFactory wrapped) {
        this.mWrappedFactory = wrapped;
    }

    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = null;
        if (this.mWrappedFactory != null) {
            view = this.mWrappedFactory.onCreateView(parent, name, context, attrs);
        }
        if (view == null) {
            view = createViewOrFailQuietly(name, context, attrs);
        }
        if (view != null) {
            onViewCreated(view, name, context, attrs);
        }
        return view;
    }

    protected View createViewOrFailQuietly(String name, Context context, AttributeSet attrs) {
        if (name.contains(".")) {
            return createViewOrFailQuietly(name, null, context, attrs);
        }
        for (String prefix : sClassPrefixList) {
            View view = createViewOrFailQuietly(name, prefix, context, attrs);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    protected View createViewOrFailQuietly(String name, String prefix, Context context, AttributeSet attrs) {
        try {
            return LayoutInflater.from(context).createView(name, prefix, attrs);
        } catch (Exception e) {
            return null;
        }
    }

    protected void onViewCreated(View view, String name, Context context, AttributeSet attrs) {
        initThemedAttrs(view, context, attrs);
        initRoundCornerStyle(view, context, attrs);
        if (view instanceof TextView) {
            Utils.changeFonts(view);
        }
    }

    private boolean isRoundEdged(View view, Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCorner, getDefaultStyleAttr(view), 0);
        if (a == null) {
            return false;
        }
        boolean isRoundEdged = a.getBoolean(1, false);
        a.recycle();
        return isRoundEdged;
    }

    private void initRoundCornerStyle(View view, Context context, AttributeSet attrs) {
        if (isRoundEdged(view, context, attrs)) {
            Drawable background = view.getBackground();
            int defaultColor = 1;
            if (background instanceof ColorDrawable) {
                defaultColor = ((ColorDrawable) background).getColor();
            }
            RoundCornerColorDrawable drawable = RoundCornerColorDrawable.create(context, attrs, defaultColor, getDefaultStyleAttr(view));
            if (drawable != null) {
                ViewUtils.setBackground(view, drawable);
            }
            initThemedAttrs(view, context, attrs);
        }
    }

    private void initThemedAttrs(View view, Context context, AttributeSet attrs) {
        ThemedAttrs themedAttrs = ThemedAttrs.ofView(view);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ThemedView, getDefaultStyleAttr(view), 0);
        if (array != null) {
            AttrUtils.loadTypedArrayToThemedAttrs(array, R.styleable.ThemedView, themedAttrs);
            array.recycle();
        }
        view.setTag(ViewTagKey.THEME, themedAttrs);
        ThemedUtils.updateView(view);
    }

    private int getDefaultStyleAttr(View view) {
        if (view instanceof EditText) {
            return com.wnafee.vector.R.attr.editTextStyle;
        }
        if ((view instanceof Button) || !(view instanceof TextView)) {
            return 0;
        }
        return 16842884;
    }
}
