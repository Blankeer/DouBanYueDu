package com.douban.book.reader.content.paragraph;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.support.v4.internal.view.SupportMenu;
import android.text.SpannableString;
import android.text.TextUtils;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.paragraph.Paragraph.BaseSpan;
import com.douban.book.reader.content.paragraph.Paragraph.EmailSpan;
import com.douban.book.reader.content.paragraph.Paragraph.EnglishSpan;
import com.douban.book.reader.content.paragraph.Paragraph.LinkSpan;
import com.douban.book.reader.content.paragraph.Paragraph.NoStretch;
import com.douban.book.reader.content.paragraph.Paragraph.StrikeThroughSpan;
import com.douban.book.reader.content.paragraph.Paragraph.UrlSpan;
import com.douban.book.reader.content.touchable.Touchable;
import com.douban.book.reader.util.CharUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.SpanUtils;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import io.realm.internal.Table;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

public class TextRun {
    public static final int MARK_AFTER_HYPHEN = 2;
    public static final int MARK_HYPHEN = 1;
    public static final String TAG = "TextRun";
    int len;
    List<BaseSpan> mStyleList;
    SpannableString mText;
    float mTotalWidth;
    int mark;
    int start;
    float width;

    public class StretchIterator implements Iterator<Integer> {
        int curPos;
        boolean divideByWhitespace;

        public StretchIterator() {
            this.curPos = 0;
            this.divideByWhitespace = false;
            this.curPos = 0;
            if (SpanUtils.hasSpan(TextRun.this.styles(), EnglishSpan.class)) {
                this.divideByWhitespace = true;
            } else {
                this.divideByWhitespace = false;
            }
        }

        public boolean hasNext() {
            return this.curPos < TextRun.this.len;
        }

        public Integer next() {
            int subLen = 0;
            if (this.divideByWhitespace) {
                while (this.curPos + subLen < TextRun.this.len) {
                    int subLen2 = subLen + TextRun.MARK_HYPHEN;
                    if (Character.isWhitespace(TextRun.this.mText.charAt((TextRun.this.start + this.curPos) + subLen))) {
                        subLen = subLen2;
                        break;
                    }
                    subLen = subLen2;
                }
                while (this.curPos + subLen < TextRun.this.len && Character.isWhitespace(TextRun.this.mText.charAt((TextRun.this.start + this.curPos) + subLen))) {
                    subLen += TextRun.MARK_HYPHEN;
                }
            } else {
                subLen = TextRun.MARK_HYPHEN;
            }
            this.curPos += subLen;
            return Integer.valueOf(subLen);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static TextRun newInstance(List<BaseSpan> styles) {
        if (SpanUtils.hasSpan(styles, LinkSpan.class)) {
            return new LinkTextRun(styles);
        }
        return new TextRun(styles);
    }

    public TextRun() {
        this.mStyleList = null;
        this.mTotalWidth = -1.0f;
    }

    public TextRun(List<BaseSpan> styles) {
        this.mStyleList = null;
        this.mTotalWidth = -1.0f;
        this.mStyleList = styles;
    }

    public String toString() {
        String text;
        try {
            text = this.mText.subSequence(this.start, this.start + this.len).toString();
        } catch (Throwable e) {
            text = e.toString();
        }
        return String.format("%s: [%s, +%s] %s", new Object[]{getClass().getSimpleName(), Integer.valueOf(this.start), Integer.valueOf(this.len), text});
    }

    public List<BaseSpan> styles() {
        return this.mStyleList;
    }

    public boolean isStretchable() {
        return !SpanUtils.hasSpan(styles(), NoStretch.class);
    }

    public int getStretchPointCount() {
        return getStretchPointCount(this.start + this.len);
    }

    public int getStretchPointCount(int offset) {
        return getStretchPointCount(this.start, offset);
    }

    public int getStretchPointCount(int start, int offset) {
        if (!isStretchable()) {
            return 0;
        }
        int count;
        if (SpanUtils.hasSpan(styles(), EnglishSpan.class)) {
            try {
                count = this.mText.subSequence(start, offset).toString().split(" ").length - 1;
            } catch (Throwable th) {
                count = 0;
            }
        } else {
            count = (offset - start) - 1;
        }
        if (count < 0) {
            return 0;
        }
        return count;
    }

    public Iterator<Integer> stretchIterator() {
        return new StretchIterator();
    }

    public char firstChar() {
        try {
            return this.mText.charAt(this.start);
        } catch (Exception e) {
            return '\u0000';
        }
    }

    public char lastChar() {
        try {
            return this.mText.charAt((this.start + this.len) - 1);
        } catch (Exception e) {
            return '\u0000';
        }
    }

    public Touchable getTouchable() {
        return null;
    }

    public void draw(Canvas canvas, float x, float y, float stretch, float textSize, int color, FontMetrics metrics) {
        Paint paint = PaintUtils.obtainPaint(textSize, styles());
        paint.setColor(color);
        CharSequence str = getText();
        float drawnX = x;
        float penX = drawnX;
        if (!isStretchable() || stretch == 0.0f) {
            canvas.drawText(str, 0, str.length(), penX, y, paint);
            penX += this.width;
        } else {
            int begin = 0;
            CharSequence subStr = Table.STRING_DEFAULT_VALUE;
            Iterator<Integer> iterator = stretchIterator();
            while (iterator.hasNext()) {
                int wordLen = ((Integer) iterator.next()).intValue();
                subStr = str.subSequence(begin, begin + wordLen);
                canvas.drawText(subStr, 0, subStr.length(), penX, y, paint);
                penX += paint.measureText(subStr, 0, subStr.length());
                if (DebugSwitch.on(Key.APP_DEBUG_SHOW_LINE_STRETCHES)) {
                    float sLeft = penX;
                    RectF rectF = new RectF(sLeft, (metrics.ascent + y) + textSize, sLeft + stretch, (metrics.descent + y) + 6.0f);
                    int textColor = paint.getColor();
                    if (stretch > 0.0f) {
                        paint.setColor(SupportMenu.CATEGORY_MASK);
                    } else {
                        paint.setColor(-16776961);
                    }
                    canvas.drawRect(rectF, paint);
                    paint.setColor(textColor);
                }
                penX += stretch;
                begin += wordLen;
            }
            penX -= stretch;
        }
        if (this.mark == MARK_HYPHEN) {
            canvas.drawText("-", penX, y, paint);
        }
        if (SpanUtils.hasSpan(styles(), StrikeThroughSpan.class)) {
            paint.setStrokeWidth(paint.getTextSize() / 20.0f);
            float startX = drawnX;
            float endX = penX;
            if (CharUtils.isFullWidthStartPunctuation(firstChar())) {
                startX += paint.getTextSize() * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
                endX -= paint.getTextSize() * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
            }
            if (CharUtils.isFullWidthEndPunctuation(lastChar())) {
                endX -= paint.getTextSize() * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO;
            }
            float lineY = y - metrics.descent;
            canvas.drawLine(startX, lineY, endX, lineY, paint);
        }
        PaintUtils.recyclePaint(paint);
    }

    public int getOffsetByPoint(float textSize, float stretch, float x, boolean jumpByWord, boolean isOffsetAfterThisWord) {
        if (TextUtils.isEmpty(this.mText)) {
            return -1;
        }
        int offset = this.start;
        if (x >= this.width + (((float) getStretchPointCount()) * stretch)) {
            offset = (this.start + this.len) - 1;
        } else {
            boolean divideByWhitespace = SpanUtils.hasSpan(styles(), EnglishSpan.class);
            Paint paint = PaintUtils.obtainPaint(textSize, styles());
            int i = this.start;
            while (i <= (this.start + this.len) - 1) {
                if (!divideByWhitespace || !jumpByWord || i >= (this.start + this.len) - 1 || Character.isWhitespace(this.mText.charAt(i))) {
                    if (paint.measureText(this.mText, this.start, i) + (((float) getStretchPointCount(i)) * stretch) >= x) {
                        if (divideByWhitespace && isOffsetAfterThisWord) {
                            offset = i;
                        }
                        PaintUtils.recyclePaint(paint);
                    } else {
                        offset = i;
                        if (jumpByWord && !isOffsetAfterThisWord && Character.isWhitespace(this.mText.charAt(i))) {
                            offset += MARK_HYPHEN;
                        }
                    }
                }
                i += MARK_HYPHEN;
            }
            PaintUtils.recyclePaint(paint);
        }
        if (jumpByWord && this.mText != null) {
            Matcher matcher;
            if (this.mark == MARK_HYPHEN && offset == (this.start + this.len) - 1) {
                while (offset < this.mText.length() && !Character.isWhitespace(this.mText.charAt(offset))) {
                    offset += MARK_HYPHEN;
                }
                matcher = Paragraph.sWesternPattern.matcher(this.mText.subSequence((this.start + this.len) - 1, offset));
                if (matcher.find()) {
                    offset = ((this.start + this.len) - 1) + matcher.end();
                }
            } else if (this.mark == MARK_AFTER_HYPHEN && offset == this.start) {
                while (offset > 0 && !Character.isWhitespace(this.mText.charAt(offset - 1))) {
                    offset--;
                }
                matcher = Paragraph.sWesternPattern.matcher(this.mText.subSequence(offset, this.start));
                int start = 0;
                while (matcher.find()) {
                    start = matcher.start();
                }
                offset += start;
            } else if (SpanUtils.hasSpan(styles(), UrlSpan.class)) {
                Matcher urlMatcher = Paragraph.sUrlPattern.matcher(this.mText);
                while (urlMatcher.find()) {
                    if (urlMatcher.start() <= offset && urlMatcher.end() >= offset) {
                        offset = isOffsetAfterThisWord ? urlMatcher.end() - 1 : urlMatcher.start();
                    }
                }
            } else if (SpanUtils.hasSpan(styles(), EmailSpan.class)) {
                Matcher emailMatcher = Paragraph.sEmailPattern.matcher(this.mText);
                while (emailMatcher.find()) {
                    if (emailMatcher.start() <= offset && emailMatcher.end() >= offset) {
                        offset = isOffsetAfterThisWord ? emailMatcher.end() - 1 : emailMatcher.start();
                    }
                }
            }
        }
        if (jumpByWord && isOffsetAfterThisWord && offset > this.start && Character.isWhitespace(this.mText.charAt(offset))) {
            return offset - 1;
        }
        return offset;
    }

    public float getPinStopPoint(float textSize, float stretch, float x, boolean isPinStopAfterThisWord) {
        float pinStopPoint = 0.0f;
        Paint paint = PaintUtils.obtainPaint(textSize, styles());
        boolean divideByWhitespace = SpanUtils.hasSpan(styles(), EnglishSpan.class);
        int i = this.start;
        while (i < this.start + this.len) {
            if (!divideByWhitespace || Character.isWhitespace(this.mText.charAt(i))) {
                float tempPinStop = paint.measureText(this.mText, this.start, i) + (((float) getStretchPointCount(i)) * stretch);
                if (tempPinStop >= x) {
                    if (isPinStopAfterThisWord) {
                        pinStopPoint = tempPinStop;
                    }
                    PaintUtils.recyclePaint(paint);
                    return pinStopPoint;
                }
                pinStopPoint = tempPinStop;
            }
            i += MARK_HYPHEN;
        }
        PaintUtils.recyclePaint(paint);
        return pinStopPoint;
    }

    public float getPointByOffset(float textSize, float stretch, int offset, boolean pointAfterChar) {
        if (this.mText == null) {
            return 0.0f;
        }
        if (pointAfterChar && offset < AdvancedShareActionProvider.WEIGHT_MAX) {
            offset += MARK_HYPHEN;
        }
        if (offset >= this.start + this.len) {
            return this.width + (((float) getStretchPointCount()) * stretch);
        }
        Paint paint = PaintUtils.obtainPaint(textSize, styles());
        float pinStopPoint = paint.measureText(this.mText, this.start, offset) + (((float) getStretchPointCount(this.start, offset)) * stretch);
        if (!pointAfterChar && offset > this.start && (!SpanUtils.hasSpan(styles(), EnglishSpan.class) || Character.isWhitespace(this.mText.charAt(offset - 1)))) {
            pinStopPoint += stretch;
        }
        PaintUtils.recyclePaint(paint);
        return pinStopPoint;
    }

    private float getXposByOffset(int offset, float stretch, Paint paint) {
        if (this.mTotalWidth < 0.0f) {
            this.mTotalWidth = this.width + (((float) getStretchPointCount()) * stretch);
        }
        float pinStopPointFromEnd = this.mTotalWidth;
        if (offset < this.start + this.len) {
            pinStopPointFromEnd = (pinStopPointFromEnd - paint.measureText(this.mText, offset, this.start + this.len)) - (((float) getStretchPointCount(offset, this.start + this.len)) * stretch);
        }
        float pinStopPointFromStart = paint.measureText(this.mText, this.start, offset) + (((float) getStretchPointCount(offset)) * stretch);
        return this.len == 0 ? pinStopPointFromStart : ((((float) (offset - this.start)) / ((float) this.len)) * pinStopPointFromEnd) + ((1.0f - (((float) (offset - this.start)) / ((float) this.len))) * pinStopPointFromStart);
    }

    public boolean containsOffset(int offset) {
        return offset >= this.start && offset < this.start + this.len;
    }

    public boolean canPinStopAfter() {
        return true;
    }

    private CharSequence getText() {
        try {
            return this.mText.subSequence(this.start, this.start + this.len);
        } catch (Throwable th) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }
}
