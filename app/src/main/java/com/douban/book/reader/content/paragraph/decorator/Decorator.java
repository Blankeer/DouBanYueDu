package com.douban.book.reader.content.paragraph.decorator;

import android.graphics.Canvas;
import com.douban.book.reader.content.paragraph.Paragraph;

public abstract class Decorator {
    private Paragraph mParagraph;

    public abstract void draw(Canvas canvas, int i, int i2);

    public abstract float getInsetLeft();

    public void setParagraph(Paragraph paragraph) {
        this.mParagraph = paragraph;
    }

    protected Paragraph getParagraph() {
        return this.mParagraph;
    }
}
