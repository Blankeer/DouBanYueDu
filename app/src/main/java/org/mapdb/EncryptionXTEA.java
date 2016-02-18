package org.mapdb;

import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public final class EncryptionXTEA {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int ALIGN = 16;
    private static final int DELTA = -1640531527;
    private static final int[] K;
    private final int k0;
    private final int k1;
    private final int k10;
    private final int k11;
    private final int k12;
    private final int k13;
    private final int k14;
    private final int k15;
    private final int k16;
    private final int k17;
    private final int k18;
    private final int k19;
    private final int k2;
    private final int k20;
    private final int k21;
    private final int k22;
    private final int k23;
    private final int k24;
    private final int k25;
    private final int k26;
    private final int k27;
    private final int k28;
    private final int k29;
    private final int k3;
    private final int k30;
    private final int k31;
    private final int k4;
    private final int k5;
    private final int k6;
    private final int k7;
    private final int k8;
    private final int k9;

    static {
        boolean z;
        if (EncryptionXTEA.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        K = new int[]{1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998};
    }

    public EncryptionXTEA(byte[] password) {
        byte[] b = getHash(password);
        int[] key = new int[4];
        int i = 0;
        while (i < ALIGN) {
            int i2 = i / 4;
            int i3 = i + 1;
            i = i3 + 1;
            i3 = i + 1;
            i = i3 + 1;
            key[i2] = (((b[i] << 24) + ((b[i3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << ALIGN)) + ((b[i] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 8)) + (b[i3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        }
        int[] r = new int[32];
        int sum = 0;
        i = 0;
        while (i < 32) {
            i3 = i + 1;
            r[i] = key[sum & 3] + sum;
            sum -= 1640531527;
            i = i3 + 1;
            r[i3] = key[(sum >>> 11) & 3] + sum;
        }
        this.k0 = r[0];
        this.k1 = r[1];
        this.k2 = r[2];
        this.k3 = r[3];
        this.k4 = r[4];
        this.k5 = r[5];
        this.k6 = r[6];
        this.k7 = r[7];
        this.k8 = r[8];
        this.k9 = r[9];
        this.k10 = r[10];
        this.k11 = r[11];
        this.k12 = r[12];
        this.k13 = r[13];
        this.k14 = r[14];
        this.k15 = r[15];
        this.k16 = r[ALIGN];
        this.k17 = r[17];
        this.k18 = r[18];
        this.k19 = r[19];
        this.k20 = r[20];
        this.k21 = r[21];
        this.k22 = r[22];
        this.k23 = r[23];
        this.k24 = r[24];
        this.k25 = r[25];
        this.k26 = r[26];
        this.k27 = r[27];
        this.k28 = r[28];
        this.k29 = r[29];
        this.k30 = r[30];
        this.k31 = r[31];
    }

    public void encrypt(byte[] bytes, int off, int len) {
        if ($assertionsDisabled || len % ALIGN == 0) {
            for (int i = off; i < off + len; i += 8) {
                encryptBlock(bytes, bytes, i);
            }
            return;
        }
        throw new AssertionError("unaligned len " + len);
    }

    public void decrypt(byte[] bytes, int off, int len) {
        if ($assertionsDisabled || len % ALIGN == 0) {
            for (int i = off; i < off + len; i += 8) {
                decryptBlock(bytes, bytes, i);
            }
            return;
        }
        throw new AssertionError("unaligned len " + len);
    }

    private void encryptBlock(byte[] in, byte[] out, int off) {
        int z = (((in[off + 4] << 24) | ((in[off + 5] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << ALIGN)) | ((in[off + 6] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 8)) | (in[off + 7] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        int y = ((((in[off] << 24) | ((in[off + 1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << ALIGN)) | ((in[off + 2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 8)) | (in[off + 3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) + ((((z << 4) ^ (z >>> 5)) + z) ^ this.k0);
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k1;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k2;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k3;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k4;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k5;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k6;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k7;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k8;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k9;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k10;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k11;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k12;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k13;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k14;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k15;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k16;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k17;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k18;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k19;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k20;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k21;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k22;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k23;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k24;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k25;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k26;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k27;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k28;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k29;
        y += (((z << 4) ^ (z >>> 5)) + z) ^ this.k30;
        z += (((y >>> 5) ^ (y << 4)) + y) ^ this.k31;
        out[off] = (byte) (y >> 24);
        out[off + 1] = (byte) (y >> ALIGN);
        out[off + 2] = (byte) (y >> 8);
        out[off + 3] = (byte) y;
        out[off + 4] = (byte) (z >> 24);
        out[off + 5] = (byte) (z >> ALIGN);
        out[off + 6] = (byte) (z >> 8);
        out[off + 7] = (byte) z;
    }

    private void decryptBlock(byte[] in, byte[] out, int off) {
        int y = (((in[off] << 24) | ((in[off + 1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << ALIGN)) | ((in[off + 2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 8)) | (in[off + 3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        int z = ((((in[off + 4] << 24) | ((in[off + 5] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << ALIGN)) | ((in[off + 6] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 8)) | (in[off + 7] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT)) - ((((y >>> 5) ^ (y << 4)) + y) ^ this.k31);
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k30;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k29;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k28;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k27;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k26;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k25;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k24;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k23;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k22;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k21;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k20;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k19;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k18;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k17;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k16;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k15;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k14;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k13;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k12;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k11;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k10;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k9;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k8;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k7;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k6;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k5;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k4;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k3;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k2;
        z -= (((y >>> 5) ^ (y << 4)) + y) ^ this.k1;
        y -= (((z << 4) ^ (z >>> 5)) + z) ^ this.k0;
        out[off] = (byte) (y >> 24);
        out[off + 1] = (byte) (y >> ALIGN);
        out[off + 2] = (byte) (y >> 8);
        out[off + 3] = (byte) y;
        out[off + 4] = (byte) (z >> 24);
        out[off + 5] = (byte) (z >> ALIGN);
        out[off + 6] = (byte) (z >> 8);
        out[off + 7] = (byte) z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] getHash(byte[] r30) {
        /*
        r0 = r30;
        r8 = r0.length;
        r27 = r8 + 9;
        r27 = r27 + 63;
        r27 = r27 / 64;
        r18 = r27 * 16;
        r27 = r18 * 4;
        r0 = r27;
        r9 = new byte[r0];
        r27 = 0;
        r28 = 0;
        r0 = r30;
        r1 = r27;
        r2 = r28;
        java.lang.System.arraycopy(r0, r1, r9, r2, r8);
        r27 = -128; // 0xffffffffffffff80 float:NaN double:NaN;
        r9[r8] = r27;
        r0 = r18;
        r7 = new int[r0];
        r17 = 0;
        r19 = 0;
    L_0x002a:
        r0 = r19;
        r1 = r18;
        if (r0 >= r1) goto L_0x003d;
    L_0x0030:
        r0 = r17;
        r27 = readInt(r9, r0);
        r7[r19] = r27;
        r17 = r17 + 4;
        r19 = r19 + 1;
        goto L_0x002a;
    L_0x003d:
        r27 = r18 + -2;
        r28 = r8 >>> 29;
        r7[r27] = r28;
        r27 = r18 + -1;
        r28 = r8 << 3;
        r7[r27] = r28;
        r27 = 64;
        r0 = r27;
        r0 = new int[r0];
        r25 = r0;
        r27 = 8;
        r0 = r27;
        r0 = new int[r0];
        r16 = r0;
        r16 = {1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};
        r6 = 0;
    L_0x005d:
        r0 = r18;
        if (r6 >= r0) goto L_0x0198;
    L_0x0061:
        r27 = r6 + 0;
        r28 = 0;
        r29 = 16;
        r0 = r27;
        r1 = r25;
        r2 = r28;
        r3 = r29;
        java.lang.System.arraycopy(r7, r0, r1, r2, r3);
        r17 = 16;
    L_0x0074:
        r27 = 64;
        r0 = r17;
        r1 = r27;
        if (r0 >= r1) goto L_0x00c3;
    L_0x007c:
        r27 = r17 + -2;
        r26 = r25[r27];
        r27 = 17;
        r27 = rot(r26, r27);
        r28 = 19;
        r0 = r26;
        r1 = r28;
        r28 = rot(r0, r1);
        r27 = r27 ^ r28;
        r28 = r26 >>> 10;
        r24 = r27 ^ r28;
        r27 = r17 + -15;
        r26 = r25[r27];
        r27 = 7;
        r27 = rot(r26, r27);
        r28 = 18;
        r0 = r26;
        r1 = r28;
        r28 = rot(r0, r1);
        r27 = r27 ^ r28;
        r28 = r26 >>> 3;
        r23 = r27 ^ r28;
        r27 = r17 + -7;
        r27 = r25[r27];
        r27 = r27 + r24;
        r27 = r27 + r23;
        r28 = r17 + -16;
        r28 = r25[r28];
        r27 = r27 + r28;
        r25[r17] = r27;
        r17 = r17 + 1;
        goto L_0x0074;
    L_0x00c3:
        r27 = 0;
        r4 = r16[r27];
        r27 = 1;
        r5 = r16[r27];
        r27 = 2;
        r10 = r16[r27];
        r27 = 3;
        r11 = r16[r27];
        r27 = 4;
        r12 = r16[r27];
        r27 = 5;
        r13 = r16[r27];
        r27 = 6;
        r14 = r16[r27];
        r27 = 7;
        r15 = r16[r27];
        r17 = 0;
    L_0x00e5:
        r27 = 64;
        r0 = r17;
        r1 = r27;
        if (r0 >= r1) goto L_0x0154;
    L_0x00ed:
        r27 = 6;
        r0 = r27;
        r27 = rot(r12, r0);
        r28 = 11;
        r0 = r28;
        r28 = rot(r12, r0);
        r27 = r27 ^ r28;
        r28 = 25;
        r0 = r28;
        r28 = rot(r12, r0);
        r27 = r27 ^ r28;
        r27 = r27 + r15;
        r28 = r12 & r13;
        r29 = r12 ^ -1;
        r29 = r29 & r14;
        r28 = r28 ^ r29;
        r27 = r27 + r28;
        r28 = K;
        r28 = r28[r17];
        r27 = r27 + r28;
        r28 = r25[r17];
        r21 = r27 + r28;
        r27 = 2;
        r0 = r27;
        r27 = rot(r4, r0);
        r28 = 13;
        r0 = r28;
        r28 = rot(r4, r0);
        r27 = r27 ^ r28;
        r28 = 22;
        r0 = r28;
        r28 = rot(r4, r0);
        r27 = r27 ^ r28;
        r28 = r4 & r5;
        r29 = r4 & r10;
        r28 = r28 ^ r29;
        r29 = r5 & r10;
        r28 = r28 ^ r29;
        r22 = r27 + r28;
        r15 = r14;
        r14 = r13;
        r13 = r12;
        r12 = r11 + r21;
        r11 = r10;
        r10 = r5;
        r5 = r4;
        r4 = r21 + r22;
        r17 = r17 + 1;
        goto L_0x00e5;
    L_0x0154:
        r27 = 0;
        r28 = r16[r27];
        r28 = r28 + r4;
        r16[r27] = r28;
        r27 = 1;
        r28 = r16[r27];
        r28 = r28 + r5;
        r16[r27] = r28;
        r27 = 2;
        r28 = r16[r27];
        r28 = r28 + r10;
        r16[r27] = r28;
        r27 = 3;
        r28 = r16[r27];
        r28 = r28 + r11;
        r16[r27] = r28;
        r27 = 4;
        r28 = r16[r27];
        r28 = r28 + r12;
        r16[r27] = r28;
        r27 = 5;
        r28 = r16[r27];
        r28 = r28 + r13;
        r16[r27] = r28;
        r27 = 6;
        r28 = r16[r27];
        r28 = r28 + r14;
        r16[r27] = r28;
        r27 = 7;
        r28 = r16[r27];
        r28 = r28 + r15;
        r16[r27] = r28;
        r6 = r6 + 16;
        goto L_0x005d;
    L_0x0198:
        r27 = 32;
        r0 = r27;
        r0 = new byte[r0];
        r20 = r0;
        r17 = 0;
    L_0x01a2:
        r27 = 8;
        r0 = r17;
        r1 = r27;
        if (r0 >= r1) goto L_0x01ba;
    L_0x01aa:
        r27 = r17 * 4;
        r28 = r16[r17];
        r0 = r20;
        r1 = r27;
        r2 = r28;
        writeInt(r0, r1, r2);
        r17 = r17 + 1;
        goto L_0x01a2;
    L_0x01ba:
        r27 = 0;
        r0 = r25;
        r1 = r27;
        java.util.Arrays.fill(r0, r1);
        r27 = 0;
        r0 = r27;
        java.util.Arrays.fill(r7, r0);
        r27 = 0;
        r0 = r16;
        r1 = r27;
        java.util.Arrays.fill(r0, r1);
        r27 = 0;
        r0 = r27;
        java.util.Arrays.fill(r9, r0);
        return r20;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mapdb.EncryptionXTEA.getHash(byte[]):byte[]");
    }

    private static int rot(int i, int count) {
        return (i << (32 - count)) | (i >>> count);
    }

    private static int readInt(byte[] b, int i) {
        return ((((b[i] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 24) + ((b[i + 1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << ALIGN)) + ((b[i + 2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 8)) + (b[i + 3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
    }

    private static void writeInt(byte[] b, int i, int value) {
        b[i] = (byte) (value >> 24);
        b[i + 1] = (byte) (value >> ALIGN);
        b[i + 2] = (byte) (value >> 8);
        b[i + 3] = (byte) value;
    }
}
