package com.douban.book.reader.drawable;

import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.PictureDrawable;

public class ScaledPictureDrawable extends PictureDrawable {
    public ScaledPictureDrawable(Picture picture) {
        super(picture);
    }

    public void draw(Canvas canvas) {
        if (getPicture() != null) {
            Rect bounds = getBounds();
            canvas.clipRect(bounds);
            canvas.drawPicture(getPicture(), bounds);
        }
    }
}
