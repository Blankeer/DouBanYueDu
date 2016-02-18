package com.douban.book.reader.util;

import android.util.SparseArray;
import com.douban.book.reader.activity.BaseActivity;
import java.lang.ref.WeakReference;

public class WindowActivityCache {
    private static SparseArray<WeakReference<BaseActivity>> mMap;

    static {
        mMap = new SparseArray();
    }

    public static void add(int token, BaseActivity activity) {
        mMap.put(token, new WeakReference(activity));
    }

    public static void remove(int token) {
        mMap.remove(token);
    }

    public static BaseActivity get(int token) {
        WeakReference<BaseActivity> ref = (WeakReference) mMap.get(token);
        if (ref != null) {
            return (BaseActivity) ref.get();
        }
        return null;
    }
}
