package com.douban.book.reader.content.touchable;

import android.graphics.PointF;

public class PinTouchable extends Touchable {
    public PointF basePoint;
    public boolean isStartPin;

    public PinTouchable() {
        this.isStartPin = false;
        this.basePoint = new PointF();
    }
}
