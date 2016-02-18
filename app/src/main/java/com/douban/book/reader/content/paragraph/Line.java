package com.douban.book.reader.content.paragraph;

import com.douban.book.reader.constant.Char;
import com.douban.book.reader.util.CharUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Line {
    public static final String TAG;
    float lineHeight;
    final Object mTextRunLock;
    float stretch;
    List<TextRun> textrunList;
    float totalSpace;
    float x;

    public Line() {
        this.mTextRunLock = new Object();
        this.textrunList = Collections.synchronizedList(new ArrayList());
    }

    static {
        TAG = Line.class.getSimpleName();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Line: x=%.2f, stretch=%.2f", new Object[]{Float.valueOf(this.x), Float.valueOf(this.stretch)}));
        for (TextRun run : this.textrunList) {
            builder.append(", ").append(run);
        }
        return builder.toString();
    }

    public int getStretchPointCount() {
        int count = 0;
        synchronized (this.mTextRunLock) {
            for (TextRun textrun : this.textrunList) {
                count += textrun.getStretchPointCount();
            }
        }
        return count + (this.textrunList.size() - 1);
    }

    public float getLineHeight() {
        return this.lineHeight;
    }

    public float getLineWidth() {
        return getMinWidth() + (((float) getStretchPointCount()) * this.stretch);
    }

    public float getMinWidth() {
        float totalWidth = 0.0f;
        synchronized (this.mTextRunLock) {
            for (TextRun run : this.textrunList) {
                totalWidth += run.width;
            }
        }
        return totalWidth;
    }

    private float getOffsetX(TextRun textRun) {
        float totalWidth = 0.0f;
        synchronized (this.mTextRunLock) {
            for (TextRun run : this.textrunList) {
                if (run == textRun) {
                    break;
                }
                totalWidth = ((totalWidth + run.width) + (((float) run.getStretchPointCount()) * this.stretch)) + this.stretch;
            }
        }
        return totalWidth;
    }

    private TextRun getTextRunByPoint(float x) {
        TextRun textRun = null;
        float totalWidth = 0.0f;
        synchronized (this.mTextRunLock) {
            for (TextRun run : this.textrunList) {
                totalWidth = ((totalWidth + run.width) + (((float) run.getStretchPointCount()) * this.stretch)) + this.stretch;
                if (run.canPinStopAfter()) {
                    textRun = run;
                }
                if (totalWidth >= x) {
                    break;
                }
            }
        }
        if (textRun == null) {
            return new TextRun();
        }
        return textRun;
    }

    private TextRun getTextRunByOffset(int offset) {
        synchronized (this.mTextRunLock) {
            for (TextRun run : this.textrunList) {
                if (run.canPinStopAfter() && run.containsOffset(offset)) {
                    return run;
                }
            }
            return new TextRun();
        }
    }

    public float getPinStopByPoint(float textSize, float x, boolean isPinStopAfterThisWord) {
        TextRun textRun = getTextRunByPoint(x);
        float offsetX = getOffsetX(textRun);
        return textRun.getPinStopPoint(textSize, this.stretch, x - offsetX, isPinStopAfterThisWord) + offsetX;
    }

    public int getOffsetByPoint(float textSize, float x, boolean jumpByWord, boolean isOffsetAfterThisWord) {
        TextRun textRun = getTextRunByPoint(x);
        return textRun.getOffsetByPoint(textSize, this.stretch, x - getOffsetX(textRun), jumpByWord, isOffsetAfterThisWord);
    }

    public float getPinStopByOffset(float textSize, int offset, boolean isEndPin) {
        TextRun textRun = getTextRunByOffset(offset);
        float posX = getOffsetX(textRun) + textRun.getPointByOffset(textSize, this.stretch, offset, isEndPin);
        if (!isEndPin && offset == startOffset() && CharUtils.isFullWidthStartPunctuation(firstChar())) {
            return posX + (textSize * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO);
        }
        if (isEndPin && offset == endOffset() && CharUtils.isFullWidthEndPunctuation(lastChar())) {
            return posX - (textSize * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO);
        }
        return posX;
    }

    public char firstChar() {
        if (this.textrunList == null || this.textrunList.size() <= 0) {
            return Char.SPACE;
        }
        return ((TextRun) this.textrunList.get(0)).firstChar();
    }

    public char lastChar() {
        if (this.textrunList == null || this.textrunList.size() <= 0) {
            return Char.SPACE;
        }
        return ((TextRun) this.textrunList.get(this.textrunList.size() - 1)).lastChar();
    }

    public int startOffset() {
        if (this.textrunList == null || this.textrunList.size() <= 0) {
            return -1;
        }
        return ((TextRun) this.textrunList.get(0)).start;
    }

    public int endOffset() {
        if (this.textrunList == null || this.textrunList.size() <= 0) {
            return -1;
        }
        TextRun textRun = (TextRun) this.textrunList.get(this.textrunList.size() - 1);
        return (textRun.start + textRun.len) - 1;
    }

    public void addTextRun(TextRun textRun) {
        synchronized (this.mTextRunLock) {
            this.textrunList.add(textRun);
        }
    }
}
