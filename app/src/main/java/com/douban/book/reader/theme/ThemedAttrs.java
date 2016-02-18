package com.douban.book.reader.theme;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import com.douban.book.reader.constant.ViewTagKey;
import com.douban.book.reader.exception.AttrNotFoundException;
import com.douban.book.reader.util.ThemedUtils;
import java.util.HashMap;
import java.util.Set;

public class ThemedAttrs {
    private HashMap<Integer, Object> mThemedArraySet;
    private View mView;

    public static ThemedAttrs ofView(View view) {
        ThemedAttrs themedViewTag;
        if (view.getTag(ViewTagKey.THEME) == null) {
            themedViewTag = new ThemedAttrs();
            view.setTag(ViewTagKey.THEME, themedViewTag);
        } else {
            themedViewTag = (ThemedAttrs) view.getTag(ViewTagKey.THEME);
        }
        view.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View v) {
                ThemedUtils.updateViewTree(v);
            }

            public void onViewDetachedFromWindow(View v) {
            }
        });
        themedViewTag.setView(view);
        return themedViewTag;
    }

    public ThemedAttrs() {
        this.mThemedArraySet = new HashMap();
    }

    void setView(View view) {
        this.mView = view;
    }

    public ThemedAttrs append(int attr, Object value) {
        this.mThemedArraySet.put(Integer.valueOf(attr), value);
        return this;
    }

    public void updateView() {
        if (this.mView != null) {
            ThemedUtils.updateView(this.mView);
        }
    }

    public Set<Integer> attrs() {
        return this.mThemedArraySet.keySet();
    }

    public boolean getBoolean(int attr) {
        return getBoolean(attr, false);
    }

    public boolean getBoolean(int attr, boolean defValue) {
        try {
            defValue = Boolean.valueOf(getAttrValue(attr).toString()).booleanValue();
        } catch (AttrNotFoundException e) {
        }
        return defValue;
    }

    public int getResourceId(int attr) throws AttrNotFoundException {
        try {
            return Integer.valueOf(getAttrValue(attr).toString()).intValue();
        } catch (Exception e) {
            throw new AttrNotFoundException();
        }
    }

    public boolean isEmpty() {
        return this.mThemedArraySet.isEmpty();
    }

    private Object getAttrValue(int attr) throws AttrNotFoundException {
        if (this.mThemedArraySet.containsKey(Integer.valueOf(attr))) {
            return this.mThemedArraySet.get(Integer.valueOf(attr));
        }
        throw new AttrNotFoundException();
    }
}
