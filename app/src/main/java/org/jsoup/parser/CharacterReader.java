package org.jsoup.parser;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import io.realm.internal.Table;
import java.util.Arrays;
import java.util.Locale;
import org.jsoup.helper.Validate;

final class CharacterReader {
    static final char EOF = '\uffff';
    private static final int maxCacheLen = 12;
    private final char[] input;
    private final int length;
    private int mark;
    private int pos;
    private final String[] stringCache;

    CharacterReader(String input) {
        this.pos = 0;
        this.mark = 0;
        this.stringCache = new String[AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY];
        Validate.notNull(input);
        this.input = input.toCharArray();
        this.length = this.input.length;
    }

    int pos() {
        return this.pos;
    }

    boolean isEmpty() {
        return this.pos >= this.length;
    }

    char current() {
        return this.pos >= this.length ? EOF : this.input[this.pos];
    }

    char consume() {
        char val = this.pos >= this.length ? EOF : this.input[this.pos];
        this.pos++;
        return val;
    }

    void unconsume() {
        this.pos--;
    }

    void advance() {
        this.pos++;
    }

    void mark() {
        this.mark = this.pos;
    }

    void rewindToMark() {
        this.pos = this.mark;
    }

    String consumeAsString() {
        char[] cArr = this.input;
        int i = this.pos;
        this.pos = i + 1;
        return new String(cArr, i, 1);
    }

    int nextIndexOf(char c) {
        for (int i = this.pos; i < this.length; i++) {
            if (c == this.input[i]) {
                return i - this.pos;
            }
        }
        return -1;
    }

    int nextIndexOf(CharSequence seq) {
        char startChar = seq.charAt(0);
        int offset = this.pos;
        while (offset < this.length) {
            if (startChar != this.input[offset]) {
                do {
                    offset++;
                    if (offset >= this.length) {
                        break;
                    }
                } while (startChar != this.input[offset]);
            }
            int i = offset + 1;
            int last = (seq.length() + i) - 1;
            if (offset < this.length && last <= this.length) {
                int j = 1;
                while (i < last && seq.charAt(j) == this.input[i]) {
                    i++;
                    j++;
                }
                if (i == last) {
                    return offset - this.pos;
                }
            }
            offset++;
        }
        return -1;
    }

    String consumeTo(char c) {
        int offset = nextIndexOf(c);
        if (offset == -1) {
            return consumeToEnd();
        }
        String consumed = cacheString(this.pos, offset);
        this.pos += offset;
        return consumed;
    }

    String consumeTo(String seq) {
        int offset = nextIndexOf((CharSequence) seq);
        if (offset == -1) {
            return consumeToEnd();
        }
        String consumed = cacheString(this.pos, offset);
        this.pos += offset;
        return consumed;
    }

    String consumeToAny(char... chars) {
        int start = this.pos;
        int remaining = this.length;
        loop0:
        while (this.pos < remaining) {
            for (char c : chars) {
                if (this.input[this.pos] == c) {
                    break loop0;
                }
            }
            this.pos++;
        }
        if (this.pos > start) {
            return cacheString(start, this.pos - start);
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    String consumeToAnySorted(char... chars) {
        int start = this.pos;
        int remaining = this.length;
        char[] val = this.input;
        while (this.pos < remaining && Arrays.binarySearch(chars, val[this.pos]) < 0) {
            this.pos++;
        }
        return this.pos > start ? cacheString(start, this.pos - start) : Table.STRING_DEFAULT_VALUE;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    java.lang.String consumeData() {
        /*
        r5 = this;
        r2 = r5.pos;
        r1 = r5.length;
        r3 = r5.input;
    L_0x0006:
        r4 = r5.pos;
        if (r4 >= r1) goto L_0x0018;
    L_0x000a:
        r4 = r5.pos;
        r0 = r3[r4];
        r4 = 38;
        if (r0 == r4) goto L_0x0018;
    L_0x0012:
        r4 = 60;
        if (r0 == r4) goto L_0x0018;
    L_0x0016:
        if (r0 != 0) goto L_0x0024;
    L_0x0018:
        r4 = r5.pos;
        if (r4 <= r2) goto L_0x002b;
    L_0x001c:
        r4 = r5.pos;
        r4 = r4 - r2;
        r4 = r5.cacheString(r2, r4);
    L_0x0023:
        return r4;
    L_0x0024:
        r4 = r5.pos;
        r4 = r4 + 1;
        r5.pos = r4;
        goto L_0x0006;
    L_0x002b:
        r4 = "";
        goto L_0x0023;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.CharacterReader.consumeData():java.lang.String");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    java.lang.String consumeTagName() {
        /*
        r5 = this;
        r2 = r5.pos;
        r1 = r5.length;
        r3 = r5.input;
    L_0x0006:
        r4 = r5.pos;
        if (r4 >= r1) goto L_0x002c;
    L_0x000a:
        r4 = r5.pos;
        r0 = r3[r4];
        r4 = 9;
        if (r0 == r4) goto L_0x002c;
    L_0x0012:
        r4 = 10;
        if (r0 == r4) goto L_0x002c;
    L_0x0016:
        r4 = 13;
        if (r0 == r4) goto L_0x002c;
    L_0x001a:
        r4 = 12;
        if (r0 == r4) goto L_0x002c;
    L_0x001e:
        r4 = 32;
        if (r0 == r4) goto L_0x002c;
    L_0x0022:
        r4 = 47;
        if (r0 == r4) goto L_0x002c;
    L_0x0026:
        r4 = 62;
        if (r0 == r4) goto L_0x002c;
    L_0x002a:
        if (r0 != 0) goto L_0x0038;
    L_0x002c:
        r4 = r5.pos;
        if (r4 <= r2) goto L_0x003f;
    L_0x0030:
        r4 = r5.pos;
        r4 = r4 - r2;
        r4 = r5.cacheString(r2, r4);
    L_0x0037:
        return r4;
    L_0x0038:
        r4 = r5.pos;
        r4 = r4 + 1;
        r5.pos = r4;
        goto L_0x0006;
    L_0x003f:
        r4 = "";
        goto L_0x0037;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.CharacterReader.consumeTagName():java.lang.String");
    }

    String consumeToEnd() {
        String data = cacheString(this.pos, this.length - this.pos);
        this.pos = this.length;
        return data;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    java.lang.String consumeLetterSequence() {
        /*
        r4 = this;
        r1 = r4.pos;
    L_0x0002:
        r2 = r4.pos;
        r3 = r4.length;
        if (r2 >= r3) goto L_0x0025;
    L_0x0008:
        r2 = r4.input;
        r3 = r4.pos;
        r0 = r2[r3];
        r2 = 65;
        if (r0 < r2) goto L_0x0016;
    L_0x0012:
        r2 = 90;
        if (r0 <= r2) goto L_0x001e;
    L_0x0016:
        r2 = 97;
        if (r0 < r2) goto L_0x0025;
    L_0x001a:
        r2 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        if (r0 > r2) goto L_0x0025;
    L_0x001e:
        r2 = r4.pos;
        r2 = r2 + 1;
        r4.pos = r2;
        goto L_0x0002;
    L_0x0025:
        r2 = r4.pos;
        r2 = r2 - r1;
        r2 = r4.cacheString(r1, r2);
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.CharacterReader.consumeLetterSequence():java.lang.String");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    java.lang.String consumeLetterThenDigitSequence() {
        /*
        r4 = this;
        r1 = r4.pos;
    L_0x0002:
        r2 = r4.pos;
        r3 = r4.length;
        if (r2 >= r3) goto L_0x0025;
    L_0x0008:
        r2 = r4.input;
        r3 = r4.pos;
        r0 = r2[r3];
        r2 = 65;
        if (r0 < r2) goto L_0x0016;
    L_0x0012:
        r2 = 90;
        if (r0 <= r2) goto L_0x001e;
    L_0x0016:
        r2 = 97;
        if (r0 < r2) goto L_0x0025;
    L_0x001a:
        r2 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        if (r0 > r2) goto L_0x0025;
    L_0x001e:
        r2 = r4.pos;
        r2 = r2 + 1;
        r4.pos = r2;
        goto L_0x0002;
    L_0x0025:
        r2 = r4.isEmpty();
        if (r2 != 0) goto L_0x0040;
    L_0x002b:
        r2 = r4.input;
        r3 = r4.pos;
        r0 = r2[r3];
        r2 = 48;
        if (r0 < r2) goto L_0x0040;
    L_0x0035:
        r2 = 57;
        if (r0 > r2) goto L_0x0040;
    L_0x0039:
        r2 = r4.pos;
        r2 = r2 + 1;
        r4.pos = r2;
        goto L_0x0025;
    L_0x0040:
        r2 = r4.pos;
        r2 = r2 - r1;
        r2 = r4.cacheString(r1, r2);
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.CharacterReader.consumeLetterThenDigitSequence():java.lang.String");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    java.lang.String consumeHexSequence() {
        /*
        r4 = this;
        r1 = r4.pos;
    L_0x0002:
        r2 = r4.pos;
        r3 = r4.length;
        if (r2 >= r3) goto L_0x002d;
    L_0x0008:
        r2 = r4.input;
        r3 = r4.pos;
        r0 = r2[r3];
        r2 = 48;
        if (r0 < r2) goto L_0x0016;
    L_0x0012:
        r2 = 57;
        if (r0 <= r2) goto L_0x0026;
    L_0x0016:
        r2 = 65;
        if (r0 < r2) goto L_0x001e;
    L_0x001a:
        r2 = 70;
        if (r0 <= r2) goto L_0x0026;
    L_0x001e:
        r2 = 97;
        if (r0 < r2) goto L_0x002d;
    L_0x0022:
        r2 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        if (r0 > r2) goto L_0x002d;
    L_0x0026:
        r2 = r4.pos;
        r2 = r2 + 1;
        r4.pos = r2;
        goto L_0x0002;
    L_0x002d:
        r2 = r4.pos;
        r2 = r2 - r1;
        r2 = r4.cacheString(r1, r2);
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.CharacterReader.consumeHexSequence():java.lang.String");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    java.lang.String consumeDigitSequence() {
        /*
        r4 = this;
        r1 = r4.pos;
    L_0x0002:
        r2 = r4.pos;
        r3 = r4.length;
        if (r2 >= r3) goto L_0x001d;
    L_0x0008:
        r2 = r4.input;
        r3 = r4.pos;
        r0 = r2[r3];
        r2 = 48;
        if (r0 < r2) goto L_0x001d;
    L_0x0012:
        r2 = 57;
        if (r0 > r2) goto L_0x001d;
    L_0x0016:
        r2 = r4.pos;
        r2 = r2 + 1;
        r4.pos = r2;
        goto L_0x0002;
    L_0x001d:
        r2 = r4.pos;
        r2 = r2 - r1;
        r2 = r4.cacheString(r1, r2);
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.CharacterReader.consumeDigitSequence():java.lang.String");
    }

    boolean matches(char c) {
        return !isEmpty() && this.input[this.pos] == c;
    }

    boolean matches(String seq) {
        int scanLength = seq.length();
        if (scanLength > this.length - this.pos) {
            return false;
        }
        for (int offset = 0; offset < scanLength; offset++) {
            if (seq.charAt(offset) != this.input[this.pos + offset]) {
                return false;
            }
        }
        return true;
    }

    boolean matchesIgnoreCase(String seq) {
        int scanLength = seq.length();
        if (scanLength > this.length - this.pos) {
            return false;
        }
        for (int offset = 0; offset < scanLength; offset++) {
            if (Character.toUpperCase(seq.charAt(offset)) != Character.toUpperCase(this.input[this.pos + offset])) {
                return false;
            }
        }
        return true;
    }

    boolean matchesAny(char... seq) {
        if (isEmpty()) {
            return false;
        }
        char c = this.input[this.pos];
        for (char seek : seq) {
            if (seek == c) {
                return true;
            }
        }
        return false;
    }

    boolean matchesAnySorted(char[] seq) {
        return !isEmpty() && Arrays.binarySearch(seq, this.input[this.pos]) >= 0;
    }

    boolean matchesLetter() {
        if (isEmpty()) {
            return false;
        }
        char c = this.input[this.pos];
        if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) {
            return false;
        }
        return true;
    }

    boolean matchesDigit() {
        if (isEmpty()) {
            return false;
        }
        char c = this.input[this.pos];
        if (c < '0' || c > '9') {
            return false;
        }
        return true;
    }

    boolean matchConsume(String seq) {
        if (!matches(seq)) {
            return false;
        }
        this.pos += seq.length();
        return true;
    }

    boolean matchConsumeIgnoreCase(String seq) {
        if (!matchesIgnoreCase(seq)) {
            return false;
        }
        this.pos += seq.length();
        return true;
    }

    boolean containsIgnoreCase(String seq) {
        return nextIndexOf(seq.toLowerCase(Locale.ENGLISH)) > -1 || nextIndexOf(seq.toUpperCase(Locale.ENGLISH)) > -1;
    }

    public String toString() {
        return new String(this.input, this.pos, this.length - this.pos);
    }

    private String cacheString(int start, int count) {
        char[] val = this.input;
        String[] cache = this.stringCache;
        if (count > maxCacheLen) {
            return new String(val, start, count);
        }
        int hash = 0;
        int i = 0;
        int offset = start;
        while (i < count) {
            hash = (hash * 31) + val[offset];
            i++;
            offset++;
        }
        int index = hash & (cache.length - 1);
        String cached = cache[index];
        if (cached == null) {
            cached = new String(val, start, count);
            cache[index] = cached;
            return cached;
        } else if (rangeEquals(start, count, cached)) {
            return cached;
        } else {
            return new String(val, start, count);
        }
    }

    boolean rangeEquals(int start, int count, String cached) {
        if (count != cached.length()) {
            return false;
        }
        char[] one = this.input;
        int j = 0;
        int i = start;
        int count2 = count;
        while (true) {
            count = count2 - 1;
            if (count2 == 0) {
                return true;
            }
            int i2 = i + 1;
            int j2 = j + 1;
            if (one[i] != cached.charAt(j)) {
                return false;
            }
            j = j2;
            i = i2;
            count2 = count;
        }
    }
}
