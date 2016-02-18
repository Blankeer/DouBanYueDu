package com.igexin.download;

import com.douban.book.reader.constant.Char;
import java.util.Set;

class i {
    private final String a;
    private final Set b;
    private int c;
    private int d;
    private final char[] e;

    public i(String str, Set set) {
        this.c = 0;
        this.d = 0;
        this.a = str;
        this.b = set;
        this.e = new char[this.a.length()];
        this.a.getChars(0, this.e.length, this.e, 0);
        b();
    }

    private static final boolean a(char c) {
        return c == Char.UNDERLINE || ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'));
    }

    private static final boolean b(char c) {
        return c == Char.UNDERLINE || ((c >= 'A' && c <= 'Z') || ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')));
    }

    public int a() {
        return this.d;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b() {
        /*
        r8 = this;
        r7 = 6;
        r6 = 4;
        r5 = 39;
        r4 = 5;
        r3 = 61;
        r0 = r8.e;
    L_0x0009:
        r1 = r8.c;
        r2 = r0.length;
        if (r1 >= r2) goto L_0x001d;
    L_0x000e:
        r1 = r8.c;
        r1 = r0[r1];
        r2 = 32;
        if (r1 != r2) goto L_0x001d;
    L_0x0016:
        r1 = r8.c;
        r1 = r1 + 1;
        r8.c = r1;
        goto L_0x0009;
    L_0x001d:
        r1 = r8.c;
        r2 = r0.length;
        if (r1 != r2) goto L_0x0027;
    L_0x0022:
        r0 = 9;
        r8.d = r0;
    L_0x0026:
        return;
    L_0x0027:
        r1 = r8.c;
        r1 = r0[r1];
        r2 = 40;
        if (r1 != r2) goto L_0x0039;
    L_0x002f:
        r0 = r8.c;
        r0 = r0 + 1;
        r8.c = r0;
        r0 = 1;
        r8.d = r0;
        goto L_0x0026;
    L_0x0039:
        r1 = r8.c;
        r1 = r0[r1];
        r2 = 41;
        if (r1 != r2) goto L_0x004b;
    L_0x0041:
        r0 = r8.c;
        r0 = r0 + 1;
        r8.c = r0;
        r0 = 2;
        r8.d = r0;
        goto L_0x0026;
    L_0x004b:
        r1 = r8.c;
        r1 = r0[r1];
        r2 = 63;
        if (r1 != r2) goto L_0x005c;
    L_0x0053:
        r0 = r8.c;
        r0 = r0 + 1;
        r8.c = r0;
        r8.d = r7;
        goto L_0x0026;
    L_0x005c:
        r1 = r8.c;
        r1 = r0[r1];
        if (r1 != r3) goto L_0x007c;
    L_0x0062:
        r1 = r8.c;
        r1 = r1 + 1;
        r8.c = r1;
        r8.d = r4;
        r1 = r8.c;
        r2 = r0.length;
        if (r1 >= r2) goto L_0x0026;
    L_0x006f:
        r1 = r8.c;
        r0 = r0[r1];
        if (r0 != r3) goto L_0x0026;
    L_0x0075:
        r0 = r8.c;
        r0 = r0 + 1;
        r8.c = r0;
        goto L_0x0026;
    L_0x007c:
        r1 = r8.c;
        r1 = r0[r1];
        r2 = 62;
        if (r1 != r2) goto L_0x009e;
    L_0x0084:
        r1 = r8.c;
        r1 = r1 + 1;
        r8.c = r1;
        r8.d = r4;
        r1 = r8.c;
        r2 = r0.length;
        if (r1 >= r2) goto L_0x0026;
    L_0x0091:
        r1 = r8.c;
        r0 = r0[r1];
        if (r0 != r3) goto L_0x0026;
    L_0x0097:
        r0 = r8.c;
        r0 = r0 + 1;
        r8.c = r0;
        goto L_0x0026;
    L_0x009e:
        r1 = r8.c;
        r1 = r0[r1];
        r2 = 60;
        if (r1 != r2) goto L_0x00c9;
    L_0x00a6:
        r1 = r8.c;
        r1 = r1 + 1;
        r8.c = r1;
        r8.d = r4;
        r1 = r8.c;
        r2 = r0.length;
        if (r1 >= r2) goto L_0x0026;
    L_0x00b3:
        r1 = r8.c;
        r1 = r0[r1];
        if (r1 == r3) goto L_0x00c1;
    L_0x00b9:
        r1 = r8.c;
        r0 = r0[r1];
        r1 = 62;
        if (r0 != r1) goto L_0x0026;
    L_0x00c1:
        r0 = r8.c;
        r0 = r0 + 1;
        r8.c = r0;
        goto L_0x0026;
    L_0x00c9:
        r1 = r8.c;
        r1 = r0[r1];
        r2 = 33;
        if (r1 != r2) goto L_0x00f4;
    L_0x00d1:
        r1 = r8.c;
        r1 = r1 + 1;
        r8.c = r1;
        r8.d = r4;
        r1 = r8.c;
        r2 = r0.length;
        if (r1 >= r2) goto L_0x00ec;
    L_0x00de:
        r1 = r8.c;
        r0 = r0[r1];
        if (r0 != r3) goto L_0x00ec;
    L_0x00e4:
        r0 = r8.c;
        r0 = r0 + 1;
        r8.c = r0;
        goto L_0x0026;
    L_0x00ec:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "Unexpected character after !";
        r0.<init>(r1);
        throw r0;
    L_0x00f4:
        r1 = r8.c;
        r1 = r0[r1];
        r1 = a(r1);
        if (r1 == 0) goto L_0x016e;
    L_0x00fe:
        r1 = r8.c;
        r2 = r8.c;
        r2 = r2 + 1;
        r8.c = r2;
    L_0x0106:
        r2 = r8.c;
        r3 = r0.length;
        if (r2 >= r3) goto L_0x011c;
    L_0x010b:
        r2 = r8.c;
        r2 = r0[r2];
        r2 = b(r2);
        if (r2 == 0) goto L_0x011c;
    L_0x0115:
        r2 = r8.c;
        r2 = r2 + 1;
        r8.c = r2;
        goto L_0x0106;
    L_0x011c:
        r0 = r8.a;
        r2 = r8.c;
        r0 = r0.substring(r1, r2);
        r2 = r8.c;
        r1 = r2 - r1;
        if (r1 > r6) goto L_0x015a;
    L_0x012a:
        r1 = "IS";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0137;
    L_0x0132:
        r0 = 7;
        r8.d = r0;
        goto L_0x0026;
    L_0x0137:
        r1 = "OR";
        r1 = r0.equals(r1);
        if (r1 != 0) goto L_0x0147;
    L_0x013f:
        r1 = "AND";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x014c;
    L_0x0147:
        r0 = 3;
        r8.d = r0;
        goto L_0x0026;
    L_0x014c:
        r1 = "NULL";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x015a;
    L_0x0154:
        r0 = 8;
        r8.d = r0;
        goto L_0x0026;
    L_0x015a:
        r1 = r8.b;
        r0 = r1.contains(r0);
        if (r0 == 0) goto L_0x0166;
    L_0x0162:
        r8.d = r6;
        goto L_0x0026;
    L_0x0166:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "unrecognized column or keyword";
        r0.<init>(r1);
        throw r0;
    L_0x016e:
        r1 = r8.c;
        r1 = r0[r1];
        if (r1 != r5) goto L_0x01b8;
    L_0x0174:
        r1 = r8.c;
        r1 = r1 + 1;
        r8.c = r1;
    L_0x017a:
        r1 = r8.c;
        r2 = r0.length;
        if (r1 >= r2) goto L_0x01a1;
    L_0x017f:
        r1 = r8.c;
        r1 = r0[r1];
        if (r1 != r5) goto L_0x019a;
    L_0x0185:
        r1 = r8.c;
        r1 = r1 + 1;
        r2 = r0.length;
        if (r1 >= r2) goto L_0x01a1;
    L_0x018c:
        r1 = r8.c;
        r1 = r1 + 1;
        r1 = r0[r1];
        if (r1 != r5) goto L_0x01a1;
    L_0x0194:
        r1 = r8.c;
        r1 = r1 + 1;
        r8.c = r1;
    L_0x019a:
        r1 = r8.c;
        r1 = r1 + 1;
        r8.c = r1;
        goto L_0x017a;
    L_0x01a1:
        r1 = r8.c;
        r0 = r0.length;
        if (r1 != r0) goto L_0x01ae;
    L_0x01a6:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "unterminated string";
        r0.<init>(r1);
        throw r0;
    L_0x01ae:
        r0 = r8.c;
        r0 = r0 + 1;
        r8.c = r0;
        r8.d = r7;
        goto L_0x0026;
    L_0x01b8:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "illegal character";
        r0.<init>(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.download.i.b():void");
    }
}
