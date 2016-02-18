package com.alipay.security.mobile.module.commonutils.crypto;

import com.alipay.security.mobile.module.commonutils.a;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class c {
    public static final String a = "HmacSHA1";
    public static final String b = "HMAC-SHA-1";
    public static final String c = "RAW";
    public static final byte[] d;

    static {
        d = e.a("7B726A5DDD72CBF8D1700FB6EB278AFD7559C40A3761E5A71614D0AC9461ED8EE9F6AAEB443CD648");
    }

    private c() {
    }

    private static String a(String str) {
        String str2 = null;
        if (!a.a(str)) {
            Mac instance;
            try {
                instance = Mac.getInstance(a);
            } catch (NoSuchAlgorithmException e) {
                try {
                    instance = Mac.getInstance(b);
                } catch (NoSuchAlgorithmException e2) {
                }
            }
            try {
                instance.init(new SecretKeySpec(d, c));
                byte[] doFinal = instance.doFinal(str.getBytes(HttpRequest.CHARSET_UTF8));
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < 16; i++) {
                    stringBuilder.append(String.format("%02x", new Object[]{Byte.valueOf(doFinal[i])}));
                }
                str2 = stringBuilder.toString();
            } catch (Exception e3) {
            }
        }
        return str2;
    }

    public static byte[] a(byte[] bArr, byte[] bArr2) {
        Mac instance;
        try {
            instance = Mac.getInstance(a);
        } catch (NoSuchAlgorithmException e) {
            instance = Mac.getInstance(b);
        }
        instance.init(new SecretKeySpec(bArr2, c));
        return instance.doFinal(bArr);
    }
}
