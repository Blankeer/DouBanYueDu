package org.json.alipay;

import com.alipay.sdk.protocol.h;
import com.douban.book.reader.constant.Char;
import io.realm.internal.Table;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import se.emilsjolander.stickylistheaders.R;
import u.aly.dx;

public final class d {
    private int a;
    private Reader b;
    private char c;
    private boolean d;

    private d(Reader reader) {
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader);
        }
        this.b = reader;
        this.d = false;
        this.a = 0;
    }

    public d(String str) {
        this(new StringReader(str));
    }

    private String a(int i) {
        int i2 = 0;
        if (i == 0) {
            return Table.STRING_DEFAULT_VALUE;
        }
        char[] cArr = new char[i];
        if (this.d) {
            this.d = false;
            cArr[0] = this.c;
            i2 = 1;
        }
        while (i2 < i) {
            try {
                int read = this.b.read(cArr, i2, i - i2);
                if (read == -1) {
                    break;
                }
                i2 += read;
            } catch (Throwable e) {
                throw new a(e);
            }
        }
        this.a += i2;
        if (i2 < i) {
            throw a("Substring bounds error");
        }
        this.c = cArr[i - 1];
        return new String(cArr);
    }

    public final a a(String str) {
        return new a(str + toString());
    }

    public final void a() {
        if (this.d || this.a <= 0) {
            throw new a("Stepping back two steps is not supported");
        }
        this.a--;
        this.d = true;
    }

    public final char b() {
        if (this.d) {
            this.d = false;
            if (this.c != '\u0000') {
                this.a++;
            }
            return this.c;
        }
        try {
            int read = this.b.read();
            if (read <= 0) {
                this.c = '\u0000';
                return '\u0000';
            }
            this.a++;
            this.c = (char) read;
            return this.c;
        } catch (Throwable e) {
            throw new a(e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final char c() {
        /*
        r5 = this;
        r4 = 13;
        r3 = 10;
        r0 = 47;
    L_0x0006:
        r1 = r5.b();
        if (r1 != r0) goto L_0x003c;
    L_0x000c:
        r1 = r5.b();
        switch(r1) {
            case 42: goto L_0x002f;
            case 47: goto L_0x0017;
            default: goto L_0x0013;
        };
    L_0x0013:
        r5.a();
    L_0x0016:
        return r0;
    L_0x0017:
        r1 = r5.b();
        if (r1 == r3) goto L_0x0006;
    L_0x001d:
        if (r1 == r4) goto L_0x0006;
    L_0x001f:
        if (r1 != 0) goto L_0x0017;
    L_0x0021:
        goto L_0x0006;
    L_0x0022:
        r2 = 42;
        if (r1 != r2) goto L_0x002f;
    L_0x0026:
        r1 = r5.b();
        if (r1 == r0) goto L_0x0006;
    L_0x002c:
        r5.a();
    L_0x002f:
        r1 = r5.b();
        if (r1 != 0) goto L_0x0022;
    L_0x0035:
        r0 = "Unclosed comment";
        r0 = r5.a(r0);
        throw r0;
    L_0x003c:
        r2 = 35;
        if (r1 != r2) goto L_0x004b;
    L_0x0040:
        r1 = r5.b();
        if (r1 == r3) goto L_0x0006;
    L_0x0046:
        if (r1 == r4) goto L_0x0006;
    L_0x0048:
        if (r1 != 0) goto L_0x0040;
    L_0x004a:
        goto L_0x0006;
    L_0x004b:
        if (r1 == 0) goto L_0x0051;
    L_0x004d:
        r2 = 32;
        if (r1 <= r2) goto L_0x0006;
    L_0x0051:
        r0 = r1;
        goto L_0x0016;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.json.alipay.d.c():char");
    }

    public final Object d() {
        char c = c();
        switch (c) {
            case HeaderMapDB.HASHER_BYTE_ARRAY /*34*/:
            case HeaderMapDB.COMPARATOR_BYTE_ARRAY /*39*/:
                StringBuffer stringBuffer = new StringBuffer();
                while (true) {
                    char b = b();
                    switch (b) {
                        case dx.a /*0*/:
                        case h.i /*10*/:
                        case R.styleable.StickyListHeadersListView_android_transcriptMode /*13*/:
                            throw a("Unterminated string");
                        case Header.SHORT_1 /*92*/:
                            b = b();
                            switch (b) {
                                case Header.FLOAT_1 /*98*/:
                                    stringBuffer.append('\b');
                                    break;
                                case Header.DOUBLE_M1 /*102*/:
                                    stringBuffer.append('\f');
                                    break;
                                case Header.ARRAY_BYTE_ALL_EQUAL /*110*/:
                                    stringBuffer.append('\n');
                                    break;
                                case Header.ARRAY_FLOAT /*114*/:
                                    stringBuffer.append(Char.CARRIAGE_RETURN);
                                    break;
                                case Header.ARRAY_INT_BYTE /*116*/:
                                    stringBuffer.append('\t');
                                    break;
                                case Header.ARRAY_INT_SHORT /*117*/:
                                    stringBuffer.append((char) Integer.parseInt(a(4), 16));
                                    break;
                                case Header.ARRAY_LONG_BYTE /*120*/:
                                    stringBuffer.append((char) Integer.parseInt(a(2), 16));
                                    break;
                                default:
                                    stringBuffer.append(b);
                                    break;
                            }
                        default:
                            if (b != c) {
                                stringBuffer.append(b);
                                break;
                            }
                            return stringBuffer.toString();
                    }
                }
            case HeaderMapDB.COMPARATOR_CHAR_ARRAY /*40*/:
            case Header.SHORT_0 /*91*/:
                a();
                return new b(this);
            case Header.ARRAY_LONG_INT /*123*/:
                a();
                return new c(this);
            default:
                StringBuffer stringBuffer2 = new StringBuffer();
                char c2 = c;
                while (c2 >= Char.SPACE && ",:]}/\\\"[{;=#".indexOf(c2) < 0) {
                    stringBuffer2.append(c2);
                    c2 = b();
                }
                a();
                String trim = stringBuffer2.toString().trim();
                if (trim.equals(Table.STRING_DEFAULT_VALUE)) {
                    throw a("Missing value");
                } else if (trim.equalsIgnoreCase("true")) {
                    return Boolean.TRUE;
                } else {
                    if (trim.equalsIgnoreCase("false")) {
                        return Boolean.FALSE;
                    }
                    if (trim.equalsIgnoreCase("null")) {
                        return c.b;
                    }
                    if ((c < '0' || c > '9') && c != Char.DOT && c != Char.HYPHEN && c != '+') {
                        return trim;
                    }
                    if (c == '0') {
                        if (trim.length() <= 2 || !(trim.charAt(1) == 'x' || trim.charAt(1) == 'X')) {
                            try {
                                return new Integer(Integer.parseInt(trim, 8));
                            } catch (Exception e) {
                            }
                        } else {
                            try {
                                return new Integer(Integer.parseInt(trim.substring(2), 16));
                            } catch (Exception e2) {
                            }
                        }
                    }
                    try {
                        return new Integer(trim);
                    } catch (Exception e3) {
                        try {
                            return new Long(trim);
                        } catch (Exception e4) {
                            try {
                                return new Double(trim);
                            } catch (Exception e5) {
                                return trim;
                            }
                        }
                    }
                }
        }
    }

    public final String toString() {
        return " at character " + this.a;
    }
}
