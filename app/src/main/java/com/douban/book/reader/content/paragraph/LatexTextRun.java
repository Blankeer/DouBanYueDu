package com.douban.book.reader.content.paragraph;

import android.graphics.Canvas;
import io.realm.internal.Table;

public class LatexTextRun extends TextRun {
    float displayRatio;
    String latex;
    int totalLen;
    int totalStart;

    public int getStretchPointCount() {
        return 0;
    }

    public void draw(Canvas canvas, float x, float y, float textSize, int color) {
        Formula formula = Formula.parseLatex(this.latex);
        formula.setBaseTextSize(textSize);
        formula.setTextColor(color);
        if (this.displayRatio > 0.0f) {
            formula.setDisplayRatio(this.displayRatio);
        }
        formula.draw(canvas, x, y);
    }

    public int getOffsetByPoint(float textSize, float stretch, float x, boolean jumpByWord, boolean isOffsetAfterThisWord) {
        return isOffsetAfterThisWord ? (this.totalStart + this.totalLen) - 1 : this.totalStart;
    }

    public boolean containsOffset(int offset) {
        return offset >= this.start && offset < this.start + this.totalLen;
    }

    public float getPointByOffset(float textSize, float stretch, int offset, boolean isEndPin) {
        return (offset == this.start || !isEndPin) ? 0.0f : this.width;
    }

    public boolean canPinStopAfter() {
        return true;
    }

    private int getPartNo() {
        return this.start - this.totalStart;
    }

    public String toString() {
        String str = "%s: [%s, +%s]%s %s";
        Object[] objArr = new Object[5];
        objArr[0] = getClass().getSimpleName();
        objArr[1] = Integer.valueOf(this.totalStart);
        objArr[2] = Integer.valueOf(this.totalLen);
        objArr[3] = getPartNo() > 0 ? " (CONTINUED)" : Table.STRING_DEFAULT_VALUE;
        objArr[4] = this.latex;
        return String.format(str, objArr);
    }
}
