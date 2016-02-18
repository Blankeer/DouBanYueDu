package com.douban.book.reader.util;

import android.graphics.Path;
import android.graphics.RectF;

public class GraphicUtils {
    public static RectF getPathBounds(Path path) {
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        return bounds;
    }

    public static boolean containsOrCloseTo(RectF rect, float x, float y, float verticalTolerance) {
        return x >= rect.left && x <= rect.right && y >= rect.top - verticalTolerance && y < rect.bottom + verticalTolerance;
    }
}
