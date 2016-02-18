package com.douban.book.reader.content.touchable;

import android.graphics.RectF;

public class FootnoteTouchable extends Touchable {
    public RectF dispArea;
    public CharSequence str;

    public FootnoteTouchable() {
        setPriority(30);
    }
}
