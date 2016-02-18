package com.douban.book.reader.util;

import android.text.SpannableString;
import com.douban.book.reader.content.paragraph.Paragraph.BaseSpan;
import java.util.List;

public class SpanUtils {
    public static void removeSpan(SpannableString str, Class<?> cls) {
        int i = 0;
        Object[] spans = str.getSpans(0, str.length(), cls);
        int length = spans.length;
        while (i < length) {
            Object obj = spans[i];
            if (ReflectionUtils.isInstanceOf(obj, (Class) cls)) {
                str.removeSpan(obj);
            }
            i++;
        }
    }

    public static boolean hasSpan(List<BaseSpan> styleList, Class<?> cls) {
        if (styleList == null) {
            return false;
        }
        for (Object style : styleList) {
            if (ReflectionUtils.isInstanceOf(style, (Class) cls)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAnySpan(List<BaseSpan> styleList, Class<?>[] clsList) {
        if (styleList == null || clsList == null) {
            return false;
        }
        for (Class<?> cls : clsList) {
            if (hasSpan(styleList, cls)) {
                return true;
            }
        }
        return false;
    }

    public static BaseSpan getSpan(List<BaseSpan> styleList, Class<?> cls) {
        if (styleList == null) {
            return null;
        }
        for (Object style : styleList) {
            if (ReflectionUtils.isInstanceOf(style, (Class) cls)) {
                return style;
            }
        }
        return null;
    }

    public static CharSequence getSubStringWithSpan(SpannableString str, BaseSpan span) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        int start = str.getSpanStart(span);
        int end = str.getSpanEnd(span);
        if (start < end) {
            return str.subSequence(start, end);
        }
        return null;
    }

    public static CharSequence applySpan(CharSequence str, Object... spans) {
        SpannableString spannableString = new SpannableString(str);
        for (Object span : spans) {
            if (span != null) {
                spannableString.setSpan(span, 0, str.length(), 33);
            }
        }
        return spannableString;
    }
}
