package com.alipay.security.mobile.module.commonutils.crypto;

import android.annotation.SuppressLint;
import android.support.v4.media.TransportMediator;
import io.realm.internal.Table;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class i {
    public static final String a = "SecurityUtils";
    private static String b;

    static {
        b = new String("idnjfhncnsfuobcnt847y929o449u474w7j3h22aoddc98euk#%&&)*&^%#");
    }

    public static String a() {
        String str = new String();
        for (int i = 0; i < b.length() - 1; i += 4) {
            str = str + b.charAt(i);
        }
        return str;
    }

    private static String a(String str) {
        return b(str.getBytes());
    }

    public static String a(String str, String str2) {
        byte[] bArr = null;
        try {
            byte[] a = a(str.getBytes());
            byte[] bytes = str2.getBytes();
            Key secretKeySpec = new SecretKeySpec(a, "AES");
            Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
            instance.init(1, secretKeySpec, new IvParameterSpec(new byte[instance.getBlockSize()]));
            bArr = instance.doFinal(bytes);
        } catch (Exception e) {
        }
        return b(bArr);
    }

    @SuppressLint({"TrulyRandom"})
    private static byte[] a(byte[] bArr) {
        KeyGenerator instance = KeyGenerator.getInstance("AES");
        Object instance2 = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        h.a(instance2, h.a(new String(a.a("c2VlZA==")), SecureRandom.class, bArr.getClass()), new Object[]{bArr});
        instance.init(TransportMediator.FLAG_KEY_MEDIA_NEXT, instance2);
        return instance.generateKey().getEncoded();
    }

    private static String b(String str) {
        return new String(c(str));
    }

    public static String b(String str, String str2) {
        try {
            byte[] a = a(str.getBytes());
            byte[] c = c(str2);
            Key secretKeySpec = new SecretKeySpec(a, "AES");
            Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
            instance.init(2, secretKeySpec, new IvParameterSpec(new byte[instance.getBlockSize()]));
            return new String(instance.doFinal(c));
        } catch (Exception e) {
            return null;
        }
    }

    private static String b(byte[] bArr) {
        if (bArr == null) {
            return Table.STRING_DEFAULT_VALUE;
        }
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (byte b : bArr) {
            stringBuffer.append("0123456789ABCDEF".charAt((b >> 4) & 15)).append("0123456789ABCDEF".charAt(b & 15));
        }
        return stringBuffer.toString();
    }

    private static byte[] c(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr[i] = Integer.valueOf(str.substring(i * 2, (i * 2) + 2), 16).byteValue();
        }
        return bArr;
    }
}
