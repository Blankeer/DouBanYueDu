package com.douban.book.reader.content.paragraph.decorator;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.util.PaintUtils;

public class BulletDecorator extends Decorator {
    public float getInsetLeft() {
        return getParagraph().getTextSize();
    }

    public void draw(Canvas canvas, int startLine, int endLine) {
        if (startLine == 0) {
            Paragraph paragraph = getParagraph();
            float textSize = paragraph.getTextSize();
            Paint paint = PaintUtils.obtainPaint(textSize);
            paint.setColor(paragraph.getTextColor());
            canvas.drawCircle(textSize / 2.0f, ((textSize - paint.getFontMetrics().bottom) / 2.0f) + paragraph.getPaddingTop(), textSize / 6.0f, paint);
            PaintUtils.recyclePaint(paint);
        }
    }
}
