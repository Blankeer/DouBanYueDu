package com.douban.book.reader.util;

import android.content.Intent;
import android.os.Bundle;
import com.j256.ormlite.stmt.query.SimpleComparison;

public class Dumper {
    public static CharSequence dump(Object object) {
        if (object instanceof Intent) {
            return dumpIntent((Intent) object);
        }
        return String.valueOf(object);
    }

    private static CharSequence dumpIntent(Intent i) {
        StringBuilder result = new StringBuilder();
        result.append(i);
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            result.append("\nExtras:");
            for (String key : bundle.keySet()) {
                result.append("\n  ").append(key).append(SimpleComparison.EQUAL_TO_OPERATION).append(bundle.get(key));
            }
        }
        return result;
    }
}
