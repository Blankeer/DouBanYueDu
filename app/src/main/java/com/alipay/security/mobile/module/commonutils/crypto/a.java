package com.alipay.security.mobile.module.commonutils.crypto;

import com.douban.book.reader.constant.Char;
import com.igexin.download.Downloads;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import u.aly.dp;

public final class a {
    public static final String a = "iso8859-1";
    public static final String b = "US-ASCII";
    private static char[] c;
    private static byte[] d;

    static {
        c = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', Char.SLASH};
        d = new byte[]{(byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 62, (byte) -1, (byte) -1, (byte) -1, (byte) 63, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 59, (byte) 60, (byte) 61, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, dp.i, dp.j, dp.k, dp.l, dp.m, dp.n, (byte) 17, (byte) 18, (byte) 19, (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24, (byte) 25, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 26, (byte) 27, (byte) 28, (byte) 29, (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34, (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44, (byte) 45, (byte) 46, (byte) 47, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1};
    }

    private a() {
    }

    public static String a(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        int length = bArr.length;
        int i = 0;
        while (i < length) {
            int i2 = i + 1;
            int i3 = bArr[i] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            if (i2 == length) {
                stringBuffer.append(c[i3 >>> 2]);
                stringBuffer.append(c[(i3 & 3) << 4]);
                stringBuffer.append("==");
                break;
            }
            int i4 = i2 + 1;
            i2 = bArr[i2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            if (i4 == length) {
                stringBuffer.append(c[i3 >>> 2]);
                stringBuffer.append(c[((i3 & 3) << 4) | ((i2 & 240) >>> 4)]);
                stringBuffer.append(c[(i2 & 15) << 2]);
                stringBuffer.append(SimpleComparison.EQUAL_TO_OPERATION);
                break;
            }
            i = i4 + 1;
            i4 = bArr[i4] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            stringBuffer.append(c[i3 >>> 2]);
            stringBuffer.append(c[((i3 & 3) << 4) | ((i2 & 240) >>> 4)]);
            stringBuffer.append(c[((i2 & 15) << 2) | ((i4 & Downloads.STATUS_RUNNING) >>> 6)]);
            stringBuffer.append(c[i4 & 63]);
        }
        return stringBuffer.toString();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] a(java.lang.String r9) {
        /*
        r8 = 61;
        r7 = -1;
        r2 = new java.lang.StringBuffer;
        r2.<init>();
        r0 = "US-ASCII";
        r3 = r9.getBytes(r0);
        r4 = r3.length;
        r0 = 0;
    L_0x0010:
        if (r0 >= r4) goto L_0x0086;
    L_0x0012:
        r5 = d;
        r1 = r0 + 1;
        r0 = r3[r0];
        r5 = r5[r0];
        if (r1 >= r4) goto L_0x001e;
    L_0x001c:
        if (r5 == r7) goto L_0x0097;
    L_0x001e:
        if (r5 == r7) goto L_0x0086;
    L_0x0020:
        r6 = d;
        r0 = r1 + 1;
        r1 = r3[r1];
        r6 = r6[r1];
        if (r0 >= r4) goto L_0x002c;
    L_0x002a:
        if (r6 == r7) goto L_0x0095;
    L_0x002c:
        if (r6 == r7) goto L_0x0086;
    L_0x002e:
        r1 = r5 << 2;
        r5 = r6 & 48;
        r5 = r5 >>> 4;
        r1 = r1 | r5;
        r1 = (char) r1;
        r2.append(r1);
    L_0x0039:
        r1 = r0 + 1;
        r0 = r3[r0];
        if (r0 != r8) goto L_0x004a;
    L_0x003f:
        r0 = r2.toString();
        r1 = "iso8859-1";
        r0 = r0.getBytes(r1);
    L_0x0049:
        return r0;
    L_0x004a:
        r5 = d;
        r5 = r5[r0];
        if (r1 >= r4) goto L_0x0052;
    L_0x0050:
        if (r5 == r7) goto L_0x0093;
    L_0x0052:
        if (r5 == r7) goto L_0x0086;
    L_0x0054:
        r0 = r6 & 15;
        r0 = r0 << 4;
        r6 = r5 & 60;
        r6 = r6 >>> 2;
        r0 = r0 | r6;
        r0 = (char) r0;
        r2.append(r0);
    L_0x0061:
        r0 = r1 + 1;
        r1 = r3[r1];
        if (r1 != r8) goto L_0x0072;
    L_0x0067:
        r0 = r2.toString();
        r1 = "iso8859-1";
        r0 = r0.getBytes(r1);
        goto L_0x0049;
    L_0x0072:
        r6 = d;
        r1 = r6[r1];
        if (r0 >= r4) goto L_0x007a;
    L_0x0078:
        if (r1 == r7) goto L_0x0091;
    L_0x007a:
        if (r1 == r7) goto L_0x0086;
    L_0x007c:
        r5 = r5 & 3;
        r5 = r5 << 6;
        r1 = r1 | r5;
        r1 = (char) r1;
        r2.append(r1);
        goto L_0x0010;
    L_0x0086:
        r0 = r2.toString();
        r1 = "iso8859-1";
        r0 = r0.getBytes(r1);
        goto L_0x0049;
    L_0x0091:
        r1 = r0;
        goto L_0x0061;
    L_0x0093:
        r0 = r1;
        goto L_0x0039;
    L_0x0095:
        r1 = r0;
        goto L_0x0020;
    L_0x0097:
        r0 = r1;
        goto L_0x0012;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alipay.security.mobile.module.commonutils.crypto.a.a(java.lang.String):byte[]");
    }
}
