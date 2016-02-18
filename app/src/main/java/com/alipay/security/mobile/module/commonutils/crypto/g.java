package com.alipay.security.mobile.module.commonutils.crypto;

import android.support.v4.media.TransportMediator;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public final class g {
    public static final byte[] a;
    public static final byte[] b;
    public static final int c = 8;
    public static final int d = 20;

    static {
        a = e.a("7B726A5DDD72CBF8D1700FB6EB278AFD7559C40A3761E5A71614D0AC9461ED8EE9F6AAEB443CD648");
        b = e.a("C9582A82777392CAA65AD7F5228150E3F966C09D6A00288B5C6E0CFB441E111B713B4E0822A8C830");
    }

    private g() {
    }

    private static byte[] a(byte[] bArr) {
        Object obj = new byte[d];
        b.a(obj);
        Object obj2 = new byte[d];
        b.a(obj2);
        Object a = c.a(bArr, a);
        System.arraycopy(a, 0, obj, 0, a.length);
        a = c.a(bArr, b);
        System.arraycopy(a, 0, obj2, 0, a.length);
        byte[] bArr2 = new byte[c];
        int i = obj[19] & 15;
        bArr2[3] = (byte) (obj[i] & TransportMediator.KEYCODE_MEDIA_PAUSE);
        bArr2[2] = (byte) (obj[i + 1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr2[1] = (byte) (obj[i + 2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr2[0] = (byte) (obj[i + 3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        int i2 = obj2[19] & 15;
        bArr2[4] = (byte) (obj2[i2] & TransportMediator.KEYCODE_MEDIA_PAUSE);
        bArr2[5] = (byte) (obj2[i2 + 1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr2[6] = (byte) (obj2[i2 + 2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr2[7] = (byte) (obj2[i2 + 3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        return bArr2;
    }

    private static byte[] a(byte[] bArr, int i) {
        int i2 = 0;
        Object obj = new byte[d];
        b.a(obj);
        Object obj2 = new byte[d];
        b.a(obj2);
        Object a = c.a(bArr, a);
        System.arraycopy(a, 0, obj, 0, a.length);
        a = c.a(bArr, b);
        System.arraycopy(a, 0, obj2, 0, a.length);
        byte[] bArr2 = new byte[c];
        int i3 = obj[19] & 15;
        bArr2[3] = (byte) (obj[i3] & TransportMediator.KEYCODE_MEDIA_PAUSE);
        bArr2[2] = (byte) (obj[i3 + 1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr2[1] = (byte) (obj[i3 + 2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr2[0] = (byte) (obj[i3 + 3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        int i4 = obj2[19] & 15;
        bArr2[4] = (byte) (obj2[i4] & TransportMediator.KEYCODE_MEDIA_PAUSE);
        bArr2[5] = (byte) (obj2[i4 + 1] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr2[6] = (byte) (obj2[i4 + 2] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        bArr2[7] = (byte) (obj2[i4 + 3] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        if (i <= 0) {
            return null;
        }
        if (i >= c) {
            return bArr2;
        }
        byte[] bArr3 = new byte[i];
        while (i2 < i) {
            bArr3[i2] = bArr2[i2];
            i2++;
        }
        return bArr3;
    }
}
