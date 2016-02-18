package com.igexin.a.b;

import android.content.Context;
import com.igexin.a.a.b.g;
import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.realm.internal.Table;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class a {
    private static final char[] a;

    static {
        a = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    }

    public static String a(int i) {
        int i2 = i < 0 ? 100 : i;
        if (i2 > 100) {
            i2 = 10;
        }
        Random random = new Random();
        char[] cArr = new char[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            cArr[i3] = a[random.nextInt(a.length)];
        }
        return new String(cArr);
    }

    public static String a(Context context, String str) {
        String str2 = Constants.VIA_RESULT_SUCCESS;
        File file = new File(str);
        try {
            MessageDigest instance = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
            if (instance != null) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    Object obj = new byte[10240];
                    while (true) {
                        int read = fileInputStream.read(obj);
                        if (read <= 0) {
                            break;
                        } else if (read < 10240) {
                            Object obj2 = new byte[read];
                            System.arraycopy(obj, 0, obj2, 0, read);
                            instance.update(obj2);
                        } else {
                            instance.update(obj);
                        }
                    }
                    byte[] digest = instance.digest();
                    str2 = a(digest, 0, digest.length);
                    fileInputStream.close();
                } catch (FileNotFoundException e) {
                } catch (IOException e2) {
                }
            }
        } catch (NoSuchAlgorithmException e3) {
        }
        return str2;
    }

    public static String a(String str) {
        MessageDigest instance;
        int i = 0;
        byte[] bytes = str.getBytes();
        char[] cArr = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            instance = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            instance = null;
        }
        if (instance == null) {
            return null;
        }
        instance.update(bytes);
        byte[] digest = instance.digest();
        char[] cArr2 = new char[32];
        for (int i2 = 0; i2 < 16; i2++) {
            byte b = digest[i2];
            int i3 = i + 1;
            cArr2[i] = cArr[(b >>> 4) & 15];
            i = i3 + 1;
            cArr2[i3] = cArr[b & 15];
        }
        return new String(cArr2);
    }

    public static String a(byte[] bArr, int i, int i2) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i3 = i; i3 < i + i2; i3++) {
            stringBuilder.append(String.format("%02X", new Object[]{Byte.valueOf(bArr[i3])}));
        }
        return stringBuilder.toString();
    }

    public static byte[] a(byte[] bArr) {
        MessageDigest instance;
        try {
            instance = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            instance = null;
        }
        if (instance == null) {
            return null;
        }
        instance.update(bArr);
        return instance.digest();
    }

    public static boolean b(String str) {
        return str == null ? true : Table.STRING_DEFAULT_VALUE.equals(str);
    }

    public static byte[] b(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        Object a = g.a(bArr);
        if (a == null) {
            return null;
        }
        String a2 = a(String.valueOf(System.currentTimeMillis()));
        int length = a.length;
        Object obj = new byte[(length + 16)];
        Object bytes = a2.substring(0, 8).getBytes();
        Object bytes2 = a2.substring(24, 32).getBytes();
        System.arraycopy(bytes, 0, obj, 0, 8);
        System.arraycopy(a, 0, obj, 8, length);
        System.arraycopy(bytes2, 0, obj, length + 8, 8);
        return obj;
    }

    public static byte[] c(byte[] bArr) {
        if (bArr == null || bArr.length < 16) {
            return null;
        }
        Object obj = new byte[(bArr.length - 16)];
        System.arraycopy(bArr, 8, obj, 0, bArr.length - 16);
        return g.b(obj);
    }
}
