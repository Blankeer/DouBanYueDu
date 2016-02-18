package com.douban.book.reader.util;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.douban.book.reader.app.App;
import com.douban.book.reader.exception.AttrNotFoundException;
import com.douban.book.reader.theme.ThemedAttrs;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dx;

public class AttrUtils {
    private static final String TAG;

    static {
        TAG = AttrUtils.class.getSimpleName();
    }

    public static int getAttrsResValue(Context context, AttributeSet attrs, int attributeId) throws AttrNotFoundException {
        try {
            int value = attrs.getAttributeResourceValue(null, context.getResources().getResourceEntryName(attributeId), -1);
            if (value > 0) {
                return value;
            }
            throw new AttrNotFoundException();
        } catch (NotFoundException e) {
            throw new AttrNotFoundException();
        }
    }

    public static String getAttrsStringValue(Context context, AttributeSet attrs, int attributeId) throws AttrNotFoundException {
        try {
            String attributeName = context.getResources().getResourceEntryName(attributeId);
            int value = attrs.getAttributeResourceValue(null, attributeName, -1);
            if (value > 0) {
                return Res.getString(value);
            }
            if (StringUtils.isNotEmpty(attrs.getAttributeValue(null, attributeName))) {
                return attrs.getAttributeValue(null, attributeName);
            }
            throw new AttrNotFoundException();
        } catch (NotFoundException e) {
            throw new AttrNotFoundException();
        }
    }

    public static boolean getBoolean(AttributeSet attrs, int attr) {
        return getBoolean(attrs, attr, false);
    }

    public static boolean getBoolean(AttributeSet attrs, int attr, boolean defValue) {
        TypedArray a = App.get().obtainStyledAttributes(attrs, new int[]{attr});
        if (a == null || a.getIndexCount() <= 0) {
            return false;
        }
        boolean result = a.getBoolean(a.getIndex(0), defValue);
        a.recycle();
        return result;
    }

    public static int getInteger(AttributeSet attrs, int attr) {
        return getInteger(attrs, attr, -1);
    }

    public static int getInteger(AttributeSet attrs, int attr, int defValue) {
        TypedArray a = App.get().obtainStyledAttributes(attrs, new int[]{attr});
        int result = defValue;
        if (a == null || a.getIndexCount() <= 0) {
            return result;
        }
        result = a.getInteger(a.getIndex(0), defValue);
        a.recycle();
        return result;
    }

    public static int getResourceId(AttributeSet attrs, int attr, int defValue) {
        TypedArray a = App.get().obtainStyledAttributes(attrs, new int[]{attr});
        int result = defValue;
        if (a == null || a.getIndexCount() <= 0) {
            return result;
        }
        result = a.getResourceId(a.getIndex(0), defValue);
        a.recycle();
        return result;
    }

    private static String getAttrName(AttributeSet attrs, int attr) throws AttrNotFoundException {
        try {
            return Res.get().getResourceEntryName(attr);
        } catch (Throwable e) {
            throw new AttrNotFoundException(e);
        }
    }

    public static void dumpAttr(AttributeSet attrs) {
        Logger.d(TAG, "--- begin dump %s", attrs);
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            Logger.d(TAG, "    %s %s", attrs.getAttributeName(i), attrs.getAttributeValue(i));
        }
        Logger.d(TAG, "--- end", new Object[0]);
    }

    public static void dumpTypedArray(TypedArray array) {
        Logger.d(TAG, "--- begin dump TypedArray@0x%x", Integer.valueOf(array.hashCode()));
        for (int i = 0; i < array.getIndexCount(); i++) {
            int index = array.getIndex(i);
            Logger.d(TAG, "    [%d] %s", Integer.valueOf(index), array.peekValue(index).coerceToString().toString());
        }
        Logger.d(TAG, "--- end", new Object[0]);
    }

    public static void dumpTypedArray(TintTypedArray array) {
        Logger.d(TAG, "--- begin dump TypedArray@0x%x", Integer.valueOf(array.hashCode()));
        for (int i = 0; i < array.getIndexCount(); i++) {
            int index = array.getIndex(i);
            Logger.d(TAG, "    [%d] %s", Integer.valueOf(index), array.peekValue(index).coerceToString().toString());
        }
        Logger.d(TAG, "--- end", new Object[0]);
    }

    public static void loadTypedArrayToThemedAttrs(TypedArray array, int[] attrs, ThemedAttrs themedAttrs) {
        int indexCount = array.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int index = array.getIndex(i);
            TypedValue typedValue = array.peekValue(index);
            if (typedValue != null) {
                Object value = null;
                switch (typedValue.type) {
                    case dx.b /*1*/:
                        int resId = array.getResourceId(index, -1);
                        if (resId != -1) {
                            value = Integer.valueOf(resId);
                            break;
                        }
                        break;
                    case R.styleable.StickyListHeadersListView_android_fastScrollEnabled /*18*/:
                        value = Boolean.valueOf(array.getBoolean(index, false));
                        break;
                }
                if (value != null) {
                    themedAttrs.append(attrs[index], value);
                }
            }
        }
    }
}
