package com.douban.book.reader.util;

import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.span.IconFontSpan;
import com.douban.book.reader.span.LinkTextSpan;
import java.util.Locale;

public class RichText extends SpannableStringBuilder {
    @NonNull
    public RichText append(@NonNull CharSequence text) {
        super.append(text);
        return this;
    }

    @NonNull
    public RichText appendObject(@NonNull Object text) {
        return append(text instanceof CharSequence ? (CharSequence) text : String.valueOf(text));
    }

    public RichText appendObjects(@NonNull Object... texts) {
        for (Object text : texts) {
            appendObject(text);
        }
        return this;
    }

    @NonNull
    public RichText appendIf(boolean condition, @NonNull Object... texts) {
        if (condition) {
            for (Object text : texts) {
                appendObject(text);
            }
        }
        return this;
    }

    public RichText appendIfNotEmpty(CharSequence text) {
        return appendIf(StringUtils.isNotEmpty(text), text);
    }

    public RichText appendAsNewLine(Object... objects) {
        if (length() > 0) {
            append('\n');
        }
        for (Object object : objects) {
            appendObject(object);
        }
        return this;
    }

    public RichText appendAsNewLineIf(boolean condition, Object... objects) {
        if (condition) {
            if (length() > 0) {
                append('\n');
            }
            for (Object object : objects) {
                appendObject(object);
            }
        }
        return this;
    }

    public RichText appendAsNewLineIfNotEmpty(@NonNull Object object) {
        if (StringUtils.isNotEmpty(StringUtils.toStr(object))) {
            if (length() > 0) {
                append('\n');
            }
            appendObject(object);
        }
        return this;
    }

    public RichText appendWithSpans(CharSequence text, Object... spans) {
        append(SpanUtils.applySpan(text, spans));
        return this;
    }

    public RichText appendWithSpans(@StringRes int resId, Object... spans) {
        return appendWithSpans(Res.getString(resId), spans);
    }

    public RichText appendWithSpansIf(boolean condition, CharSequence text, Object... spans) {
        if (condition) {
            appendWithSpans(text, spans);
        }
        return this;
    }

    public RichText appendWithSpansIf(boolean condition, @StringRes int resId, Object... spans) {
        return appendWithSpansIf(condition, Res.getString(resId), spans);
    }

    public RichText append(@StringRes int resId) {
        return append(Res.getString(resId));
    }

    @NonNull
    public RichText append(char ch) {
        super.append(ch);
        return this;
    }

    public RichText appendLink(@StringRes int resId) {
        return appendLink(Res.getString(resId));
    }

    public RichText appendLink(CharSequence text) {
        return appendWithSpans(text, new LinkTextSpan());
    }

    public RichText appendLink(@StringRes int resId, Uri uri) {
        return appendLink(Res.getString(resId), uri);
    }

    public RichText appendLink(CharSequence text, Uri uri) {
        return appendWithSpans(text, new LinkTextSpan(uri));
    }

    public RichText appendIcon(@DrawableRes int resId) {
        return appendIcon(new IconFontSpan(resId));
    }

    public RichText appendIcon(IconFontSpan span) {
        return appendWithSpans((CharSequence) "x", span);
    }

    public static RichText buildUpon(@StringRes int resId) {
        return new RichText().append(resId);
    }

    public static RichText buildUpon(CharSequence cs) {
        return new RichText().append(cs);
    }

    public static SpannedString format(int formatResId, Object... args) {
        return format(Res.getString(formatResId), args);
    }

    public static SpannedString format(CharSequence format, Object... args) {
        return SpanFormatter.format(Locale.getDefault(), format, args);
    }

    public static RichText linkify(@StringRes int resId) {
        return new RichText().appendLink(resId);
    }

    public static RichText linkify(@StringRes int resId, Uri uri) {
        return new RichText().appendLink(resId, uri);
    }

    public static RichText singleIcon(@DrawableRes int iconResId) {
        return new RichText().append((char) Char.SPACE).append(textWithIcon(iconResId, (CharSequence) " "));
    }

    public static RichText textWithIcon(@DrawableRes int iconResId, @StringRes int strResId) {
        return textWithIcon(iconResId, Res.getString(strResId));
    }

    public static RichText textWithIcon(@DrawableRes int iconResId, CharSequence str) {
        return textWithColoredIcon(iconResId, 0, str);
    }

    public static RichText textWithColoredIcon(@DrawableRes int iconResId, @ColorRes @ArrayRes int iconColorResId, CharSequence str) {
        return textWithColoredIcon(iconResId, iconColorResId, str, false);
    }

    public static RichText textWithColoredIcon(@DrawableRes int iconResId, @ColorRes @ArrayRes int iconColorResId, @StringRes int strResId) {
        return textWithColoredIcon(iconResId, iconColorResId, Res.getString(strResId));
    }

    public static RichText textWithOriginalColoredIcon(@DrawableRes int iconResId, @StringRes int strResId) {
        return textWithColoredIcon(iconResId, -1, Res.getString(strResId), true);
    }

    private static RichText textWithColoredIcon(@DrawableRes int iconResId, @ColorRes @ArrayRes int iconColorResId, CharSequence str, boolean useOriginalIconColor) {
        RichText richText = new RichText();
        if (iconResId > 0) {
            IconFontSpan iconFontSpan = new IconFontSpan(iconResId).ratio(1.5f).verticalOffsetRatio(-0.03f);
            if (useOriginalIconColor) {
                iconFontSpan.useOriginalColor();
            } else {
                iconFontSpan.color(iconColorResId);
            }
            richText.appendIcon(iconFontSpan).appendWithSpans((CharSequence) " ", new Object[0]);
        }
        richText.append(str);
        return richText;
    }
}
