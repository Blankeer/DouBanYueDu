package com.douban.book.reader.content.paragraph;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.text.SpannableString;
import com.douban.book.reader.content.paragraph.Paragraph.StrikeThroughSpan;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PaintUtils;

public class CodeTextRun extends TextRun {
    float mCharWidth;
    int totalLen;
    int totalStart;

    public CodeTextRun() {
        this.mCharWidth = -1.0f;
    }

    public void draw(Canvas canvas, float x, float y, float textSize, int color, FontMetrics metrics) {
        Paint paint = PaintUtils.obtainPaint(textSize, styles());
        paint.setColor(color);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setTextSize(Paragraph.CODE_TEXTSIZE_RATIO * textSize);
        SpannableString str = (SpannableString) this.mText.subSequence(this.start, this.start + this.len);
        x += Code.DEFAULT_PADDING;
        canvas.drawText(str, 0, str.length(), x, y, paint);
        for (StrikeThroughSpan span : (StrikeThroughSpan[]) str.getSpans(0, str.length(), StrikeThroughSpan.class)) {
            this.mCharWidth = paint.measureText(" ");
            int start = str.getSpanStart(span);
            int end = str.getSpanEnd(span);
            if (end > start) {
                float lineY = y - metrics.descent;
                canvas.drawLine(x + (this.mCharWidth * ((float) start)), lineY, x + (this.mCharWidth * ((float) end)), lineY, paint);
            }
        }
        PaintUtils.recyclePaint(paint);
    }

    public int getOffsetByPoint(float textSize, float stretch, float x, boolean jumpByWord, boolean isOffsetAfterThisWord) {
        Logger.d(TextRun.TAG, "TextRun start = " + this.start, new Object[0]);
        if (jumpByWord) {
            return isOffsetAfterThisWord ? (this.totalStart + this.totalLen) - 1 : this.totalStart;
        } else {
            return Math.min((int) Math.ceil((double) ((x - Code.DEFAULT_PADDING) / getCharWidth(textSize))), this.len) + this.start;
        }
    }

    public float getPointByOffset(float textSize, float stretch, int offset, boolean isEndPin) {
        if (offset <= this.start || this.mText == null || !isEndPin) {
            return 0.0f;
        }
        if (offset > this.start + this.len) {
            offset = this.start + this.len;
        }
        return (getCharWidth(textSize) * ((float) ((offset - this.start) + 1))) + Code.DEFAULT_PADDING;
    }

    private float getCharWidth(float textSize) {
        if (this.mCharWidth < 0.0f) {
            Paint paint = PaintUtils.obtainPaint(textSize, styles());
            paint.setTypeface(Typeface.MONOSPACE);
            paint.setTextSize(Paragraph.CODE_TEXTSIZE_RATIO * textSize);
            this.mCharWidth = paint.measureText(" ");
            PaintUtils.recyclePaint(paint);
        }
        return this.mCharWidth;
    }

    public boolean canPinStopAfter() {
        return true;
    }
}
