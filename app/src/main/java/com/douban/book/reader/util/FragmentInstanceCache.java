package com.douban.book.reader.util;

import android.support.v4.app.Fragment;
import java.util.HashMap;
import java.util.Map;

public class FragmentInstanceCache {
    private static Map<String, Fragment> sFragmentMap;

    static {
        sFragmentMap = new HashMap();
    }

    public static String push(Fragment fragment) {
        String key = getKey(fragment);
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        sFragmentMap.put(key, fragment);
        return key;
    }

    public static Fragment pop(String key) {
        return (Fragment) sFragmentMap.remove(key);
    }

    public static void remove(Fragment fragment) {
        if (StringUtils.isNotEmpty(getKey(fragment))) {
            sFragmentMap.remove(getKey(fragment));
        }
    }

    private static String getKey(Fragment fragment) {
        if (fragment == null) {
            return null;
        }
        return String.format("%s@%s", new Object[]{fragment.getClass().getName(), Integer.valueOf(fragment.hashCode())});
    }
}
