package com.alipay.apmobilesecuritysdk.f;

import android.support.v4.media.TransportMediator;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import java.util.Map;
import java.util.Set;

public final class a {
    public static String a(Map<String, Integer> map, String str, String str2) {
        if (str2 == null || str2.length() <= 0) {
            return Table.STRING_DEFAULT_VALUE;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str2 + ":");
        if (str == null || str.length() <= 0) {
            return stringBuilder.toString();
        }
        String[] split = str.split(",");
        if (split == null || split.length <= 0) {
            return stringBuilder.toString();
        }
        if (map == null || map.size() <= 0) {
            return stringBuilder.toString();
        }
        Set keySet = map.keySet();
        if (keySet == null || keySet.size() <= 0) {
            return stringBuilder.toString();
        }
        try {
            int i;
            byte[] bArr = new byte[((split.length / 8) + 1)];
            for (i = 0; i < bArr.length; i++) {
                bArr[i] = (byte) 0;
            }
            int i2 = 0;
            for (Object obj : split) {
                int i3 = bArr[i2 / 8];
                if (keySet.contains(obj)) {
                    i3 |= TransportMediator.FLAG_KEY_MEDIA_NEXT >> (i2 % 8);
                }
                bArr[i2 / 8] = (byte) (i3 & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
                i2++;
            }
            stringBuilder.append(com.alipay.security.mobile.module.commonutils.crypto.a.a(bArr));
        } catch (Throwable th) {
        }
        return stringBuilder.toString();
    }
}
