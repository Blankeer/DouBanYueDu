package com.tencent.wxop.stat.b;

import android.util.Base64;
import com.douban.book.reader.util.WorksIdentity;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public final class g {
    private static byte[] a(byte[] bArr, byte[] bArr2) {
        int i = 0;
        int[] iArr = new int[WorksIdentity.ID_BIT_FINALIZE];
        int[] iArr2 = new int[WorksIdentity.ID_BIT_FINALIZE];
        int length = bArr2.length;
        if (length <= 0 || length > WorksIdentity.ID_BIT_FINALIZE) {
            throw new IllegalArgumentException("key must be between 1 and 256 bytes");
        }
        int i2;
        for (i2 = 0; i2 < WorksIdentity.ID_BIT_FINALIZE; i2++) {
            iArr[i2] = i2;
            iArr2[i2] = bArr2[i2 % length];
        }
        i2 = 0;
        for (length = 0; length < WorksIdentity.ID_BIT_FINALIZE; length++) {
            i2 = ((i2 + iArr[length]) + iArr2[length]) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            int i3 = iArr[length];
            iArr[length] = iArr[i2];
            iArr[i2] = i3;
        }
        byte[] bArr3 = new byte[bArr.length];
        i2 = 0;
        length = 0;
        while (i < bArr.length) {
            i2 = (i2 + 1) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            length = (length + iArr[i2]) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            i3 = iArr[i2];
            iArr[i2] = iArr[length];
            iArr[length] = i3;
            bArr3[i] = (byte) (iArr[(iArr[i2] + iArr[length]) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT] ^ bArr[i]);
            i++;
        }
        return bArr3;
    }

    public static byte[] b(byte[] bArr) {
        return a(bArr, Base64.decode("MDNhOTc2NTExZTJjYmUzYTdmMjY4MDhmYjdhZjNjMDU=", 0));
    }

    public static byte[] c(byte[] bArr) {
        return a(bArr, Base64.decode("MDNhOTc2NTExZTJjYmUzYTdmMjY4MDhmYjdhZjNjMDU=", 0));
    }
}
