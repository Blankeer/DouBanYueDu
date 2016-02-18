package com.ta.utdid2.android.utils;

import com.douban.book.reader.util.WorksIdentity;

public class IntUtils {
    public static byte[] getBytes(int i) {
        byte[] bArr = new byte[4];
        bArr[3] = (byte) (i % WorksIdentity.ID_BIT_FINALIZE);
        int i2 = i >> 8;
        bArr[2] = (byte) (i2 % WorksIdentity.ID_BIT_FINALIZE);
        i2 >>= 8;
        bArr[1] = (byte) (i2 % WorksIdentity.ID_BIT_FINALIZE);
        bArr[0] = (byte) ((i2 >> 8) % WorksIdentity.ID_BIT_FINALIZE);
        return bArr;
    }

    public static byte[] getBytes(byte[] bArr, int i) {
        if (bArr.length != 4) {
            return null;
        }
        bArr[3] = (byte) (i % WorksIdentity.ID_BIT_FINALIZE);
        int i2 = i >> 8;
        bArr[2] = (byte) (i2 % WorksIdentity.ID_BIT_FINALIZE);
        i2 >>= 8;
        bArr[1] = (byte) (i2 % WorksIdentity.ID_BIT_FINALIZE);
        bArr[0] = (byte) ((i2 >> 8) % WorksIdentity.ID_BIT_FINALIZE);
        return bArr;
    }
}
