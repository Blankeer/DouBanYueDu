package com.douban.book.reader.util;

import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.support.v4.internal.view.SupportMenu;
import com.douban.book.reader.constant.Dimen;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.content.paragraph.Paragraph.BaseSpan;
import com.douban.book.reader.content.paragraph.Paragraph.CodeSpan;
import com.douban.book.reader.content.paragraph.Paragraph.EmphasizeSpan;
import com.douban.book.reader.content.paragraph.Paragraph.EnglishSpan;
import com.douban.book.reader.content.paragraph.Paragraph.RegularScriptSpan;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.view.ReadViewPager;
import java.util.List;
import java.util.Stack;

public class PaintUtils {
    public static final PaintFlagsDrawFilter sDrawFilter;
    private static Stack<Paint> sPaintCache;

    static {
        sPaintCache = new Stack();
        sDrawFilter = new PaintFlagsDrawFilter(0, 7);
    }

    public static Paint obtainPaint() {
        Paint paint;
        try {
            paint = (Paint) sPaintCache.pop();
        } catch (Exception e) {
            paint = new Paint();
        }
        paint.reset();
        paint.setTextAlign(Align.LEFT);
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setShader(null);
        paint.setHinting(1);
        paint.setDither(true);
        return paint;
    }

    public static Paint obtainStrokePaint(int color) {
        Paint paint = obtainPaint();
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(0.0f);
        paint.setColor(color);
        return paint;
    }

    public static Paint obtainStrokePaint() {
        return obtainStrokePaint(SupportMenu.CATEGORY_MASK);
    }

    public static Paint obtainPaint(float textSize) {
        Paint paint = obtainPaint();
        paint.setTextSize(textSize);
        return paint;
    }

    public static Paint obtainPaint(float textSize, boolean bold, boolean isCode, boolean isEnglish, boolean quote) {
        Paint paint = obtainPaint(textSize);
        if (isCode) {
            paint.setTypeface(Typeface.MONOSPACE);
            paint.setFakeBoldText(bold);
            paint.setTextSize(Paragraph.CODE_TEXTSIZE_RATIO * textSize);
        } else if (isEnglish) {
            paint.setTypeface(bold ? Font.ENGLISH_BOLD : Font.ENGLISH);
            paint.setFakeBoldText(false);
        } else if (quote) {
            paint.setTypeface(Font.SERIF);
            paint.setFakeBoldText(bold);
        } else {
            paint.setTypeface(bold ? Font.SANS_SERIF_BOLD : Font.SANS_SERIF);
            paint.setFakeBoldText(false);
        }
        return paint;
    }

    public static Paint obtainPaint(float textSize, List<BaseSpan> styleList) {
        boolean bold = false;
        if (SpanUtils.hasSpan(styleList, EmphasizeSpan.class)) {
            bold = true;
        }
        boolean isCode = false;
        if (SpanUtils.hasSpan(styleList, CodeSpan.class)) {
            isCode = true;
        }
        boolean isEnglish = false;
        if (SpanUtils.hasSpan(styleList, EnglishSpan.class)) {
            isEnglish = true;
        }
        boolean quote = false;
        if (SpanUtils.hasSpan(styleList, RegularScriptSpan.class)) {
            quote = true;
        }
        return obtainPaint(textSize, bold, isCode, isEnglish, quote);
    }

    public static void recyclePaint(Paint paint) {
        sPaintCache.push(paint);
    }

    public static void addShadowLayer(Paint paint) {
        addShadowLayer(paint, 0.2f, 0.5f);
    }

    public static void addShadowLayer(Paint paint, float ratioX, float ratioY) {
        paint.setShadowLayer((float) Dimen.SHADOW_WIDTH, ((float) Dimen.SHADOW_WIDTH) * ratioX, ((float) Dimen.SHADOW_WIDTH) * ratioY, Res.getColorOverridingAlpha(17170444, Theme.isNight() ? 1.0f : ReadViewPager.EDGE_RATIO));
    }

    public static void applyNightModeMaskIfNeeded(Paint paint) {
        paint.setColorFilter(Theme.isNight() ? ThemedUtils.NIGHT_MODE_COLOR_FILTER : null);
    }

    public static int breakText(Paint paint, String str, float maxWidth) {
        int pos = paint.breakText(str, true, maxWidth, null);
        if (VERSION.SDK_INT >= 20) {
            return pos;
        }
        boolean isLastCharHighSurrogate = false;
        if (pos >= 1) {
            isLastCharHighSurrogate = Character.isHighSurrogate(str.charAt(pos - 1));
        }
        if (isLastCharHighSurrogate || str.codePointCount(0, pos) < pos) {
            int actualPos = 0;
            while (actualPos < pos) {
                actualPos += isBmpCodePoint(str.codePointAt(actualPos)) ? 1 : 2;
            }
            pos = actualPos;
        }
        return pos;
    }

    private static boolean isBmpCodePoint(int codePoint) {
        return codePoint >= 0 && codePoint <= SupportMenu.USER_MASK;
    }
}
