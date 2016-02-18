package com.douban.book.reader.util;

import android.graphics.Paint;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.lib.hyphenate.Hyphenate;
import java.text.BreakIterator;
import java.util.Iterator;
import se.emilsjolander.stickylistheaders.R;

public class CharUtils {
    public static final float FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO = 0.4f;
    private static char[] sFullWidthEndPunctuations;
    private static char[] sFullWidthStartPunctuations;

    public static int getBreakDownIndex(String str) {
        int len = str.length();
        if (len < 3) {
            return len;
        }
        BreakIterator iterator = BreakIteratorUtils.getWordBreakIterator();
        iterator.setText(str);
        int start = iterator.first();
        int end = start;
        boolean lastWordCanInEnd = false;
        while (true) {
            end = iterator.next();
            if (end != -1) {
                String word = str.substring(start, end);
                if (word.length() > 0) {
                    char c = word.charAt(0);
                    if (lastWordCanInEnd && canInStartOfLine(c)) {
                        BreakIteratorUtils.recycleWordBreakIterator(iterator);
                        return start;
                    }
                    lastWordCanInEnd = canInEndOfLine(word.charAt(word.length() - 1));
                }
                start = end;
            } else {
                BreakIteratorUtils.recycleWordBreakIterator(iterator);
                return len;
            }
        }
    }

    public static boolean shouldHyphenate(String str) {
        boolean hasLowercase = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isLetter(c)) {
                return false;
            }
            if (!hasLowercase && Character.isLowerCase(c)) {
                hasLowercase = true;
            }
        }
        if (hasLowercase) {
            return true;
        }
        return false;
    }

    public static int getHyphenateIndexByWidth(String str, Paint paint, float width) {
        float totalWidth = 0.0f;
        int index = 0;
        Iterator it = Hyphenate.hyphenateWord(str).iterator();
        while (it.hasNext()) {
            String part = (String) it.next();
            totalWidth += paint.measureText(part);
            if (totalWidth > width) {
                break;
            }
            index += part.length();
        }
        if (index <= 0) {
            return str.length();
        }
        return index;
    }

    public static boolean isPunctuation(char c) {
        switch (Character.getType(c)) {
            case R.styleable.StickyListHeadersListView_android_fastScrollAlwaysVisible /*20*/:
            case R.styleable.StickyListHeadersListView_android_requiresFadingEdge /*21*/:
            case R.styleable.StickyListHeadersListView_stickyListHeadersListViewStyle /*22*/:
            case R.styleable.StickyListHeadersListView_hasStickyHeaders /*23*/:
            case R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
            case HeaderMapDB.SERIALIZER_CHAR_ARRAY /*29*/:
            case HeaderMapDB.SERIALIZER_INT_ARRAY /*30*/:
                return true;
            default:
                return false;
        }
    }

    static {
        sFullWidthStartPunctuations = new char[]{'\u201c', '\u2018', Char.LEFT_DOUBLE_ANGLE_BRACKET, '\uff08', '\u3008', Char.LEFT_COMER_BRACKET, '\u300e', '\u3010', '\u3014', '\u3016', '\u3018', '\u301a', '\u301d', '\uff3b', '\uff5b', '\uff5f'};
        sFullWidthEndPunctuations = new char[]{'\uff0c', '\u3002', '\u3001', Char.FULLWIDTH_COLON, '\uff1b', '\uff1f', '\uff01', '\u2019', '\u201d', '\uff09', Char.RIGHT_DOUBLE_ANGLE_BRACKET, '\u3009', '\uff0e', '\u301e', '\uff07', '\u300f', Char.RIGHT_COMER_BRACKET, '\u3011', '\u3015', '\u3017', '\u3019', '\u301b', '\uff3d', '\uff5d', '\uff60'};
    }

    public static boolean isFullWidthStartPunctuation(char c) {
        if (isPunctuation(c)) {
            for (char c2 : sFullWidthStartPunctuations) {
                if (c == c2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isFullWidthEndPunctuation(char c) {
        if (isPunctuation(c)) {
            for (char c2 : sFullWidthEndPunctuations) {
                if (c == c2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canInStartOfLine(char c) {
        switch (Character.getType(c)) {
            case R.styleable.StickyListHeadersListView_stickyListHeadersListViewStyle /*22*/:
            case R.styleable.StickyListHeadersListView_isDrawingListUnderStickyHeader /*24*/:
            case HeaderMapDB.SERIALIZER_INT_ARRAY /*30*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean canInEndOfLine(char c) {
        switch (Character.getType(c)) {
            case R.styleable.StickyListHeadersListView_android_requiresFadingEdge /*21*/:
            case HeaderMapDB.TUPLE3_COMPARATOR_STATIC /*26*/:
            case HeaderMapDB.SERIALIZER_CHAR_ARRAY /*29*/:
                return false;
            default:
                return true;
        }
    }
}
