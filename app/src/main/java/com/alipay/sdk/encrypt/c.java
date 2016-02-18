package com.alipay.sdk.encrypt;

import io.fabric.sdk.android.services.common.CommonUtils;
import io.realm.internal.Table;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class c {
    private static String a(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
            instance.update(str.getBytes());
            return b(instance.digest());
        } catch (NoSuchAlgorithmException e) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    public static String a(byte[] bArr) {
        try {
            MessageDigest instance = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
            instance.update(bArr);
            return b(instance.digest());
        } catch (NoSuchAlgorithmException e) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    private static String b(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (int i = 0; i < bArr.length; i++) {
            stringBuffer.append(Character.forDigit((bArr[i] & 240) >> 4, 16));
            stringBuffer.append(Character.forDigit(bArr[i] & 15, 16));
        }
        return stringBuffer.toString();
    }
}
