package com.mcxiaoke.next.ui.layout;

import android.view.View.MeasureSpec;
import android.view.ViewGroup;

final class Utils {
    Utils() {
    }

    public static int getSquaredMeasureSpec(ViewGroup viewGroup, int widthMeasureSpec, int heightMeasureSpec) {
        int size;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == 1073741824 && widthSize > 0) {
            size = widthSize;
        } else if (heightMode != 1073741824 || heightSize <= 0) {
            size = widthSize < heightSize ? widthSize : heightSize;
        } else {
            size = heightSize;
        }
        return MeasureSpec.makeMeasureSpec(size, 1073741824);
    }
}
