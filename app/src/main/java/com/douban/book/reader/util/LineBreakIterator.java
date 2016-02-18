package com.douban.book.reader.util;

import io.realm.internal.Table;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineBreakIterator implements Iterator<CharSequence> {
    private static Pattern sLineBreakPattern;
    int mLastPos;
    Matcher mMatcher;
    CharSequence mText;

    static {
        sLineBreakPattern = Pattern.compile("\r|\n|\r\n");
    }

    public LineBreakIterator(CharSequence text) {
        this.mLastPos = 0;
        if (text == null) {
            text = Table.STRING_DEFAULT_VALUE;
        }
        this.mText = text;
        this.mMatcher = sLineBreakPattern.matcher(text);
    }

    public boolean hasNext() {
        return StringUtils.isNotEmpty(this.mText) && this.mLastPos < this.mText.length();
    }

    public CharSequence next() {
        int start = this.mLastPos;
        int end = this.mText.length();
        if (this.mMatcher.find()) {
            start = this.mMatcher.start();
            end = this.mMatcher.end();
        } else {
            start = end;
        }
        CharSequence cs = this.mText.subSequence(this.mLastPos, start);
        this.mLastPos = end;
        return cs;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
