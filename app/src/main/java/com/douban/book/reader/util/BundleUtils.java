package com.douban.book.reader.util;

import android.os.Bundle;
import android.os.Parcelable;
import java.io.Serializable;

public class BundleUtils {
    public static void put(Bundle bundle, String key, Object value) {
        if (value instanceof Integer) {
            bundle.putInt(key, ((Integer) value).intValue());
        } else if (value instanceof Parcelable) {
            bundle.putParcelable(key, (Parcelable) value);
        } else if (value instanceof Serializable) {
            bundle.putSerializable(key, (Serializable) value);
        } else {
            bundle.putString(key, String.valueOf(value));
        }
    }
}
