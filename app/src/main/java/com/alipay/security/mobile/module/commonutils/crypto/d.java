package com.alipay.security.mobile.module.commonutils.crypto;

import com.alipay.security.mobile.module.commonutils.a;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.security.MessageDigest;

public final class d {
    public static String a(String str) {
        String str2 = null;
        try {
            if (!a.a(str)) {
                MessageDigest instance = MessageDigest.getInstance(CommonUtils.SHA1_INSTANCE);
                instance.update(str.getBytes(HttpRequest.CHARSET_UTF8));
                byte[] digest = instance.digest();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    stringBuilder.append(String.format("%02x", new Object[]{Byte.valueOf(digest[i])}));
                }
                str2 = stringBuilder.toString();
            }
        } catch (Exception e) {
        }
        return str2;
    }

    private static String a(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return Table.STRING_DEFAULT_VALUE;
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA1");
            instance.update(bArr);
            byte[] digest = instance.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                stringBuilder.append(String.format("%02x", new Object[]{Byte.valueOf(digest[i])}));
            }
            return stringBuilder.toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] b(String str) {
        byte[] bArr = null;
        try {
            if (!a.a(str)) {
                MessageDigest instance = MessageDigest.getInstance(CommonUtils.SHA1_INSTANCE);
                instance.update(str.getBytes(HttpRequest.CHARSET_UTF8));
                bArr = instance.digest();
            }
        } catch (Exception e) {
        }
        return bArr;
    }

    private static String c(String str) {
        String str2 = null;
        try {
            if (!a.a(str)) {
                MessageDigest instance = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
                instance.update(str.getBytes(HttpRequest.CHARSET_UTF8));
                byte[] digest = instance.digest();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < 16; i++) {
                    stringBuilder.append(String.format("%02x", new Object[]{Byte.valueOf(digest[i])}));
                }
                str2 = stringBuilder.toString();
            }
        } catch (Exception e) {
        }
        return str2;
    }
}
