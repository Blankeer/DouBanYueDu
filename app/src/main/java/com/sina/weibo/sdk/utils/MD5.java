package com.sina.weibo.sdk.utils;

import io.fabric.sdk.android.services.common.CommonUtils;
import java.security.MessageDigest;

public class MD5 {
    private static final char[] hexDigits;

    static {
        hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    }

    public static String hexdigest(String string) {
        String s = null;
        try {
            s = hexdigest(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String hexdigest(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(CommonUtils.MD5_INSTANCE);
            md.update(bytes);
            byte[] tmp = md.digest();
            char[] str = new char[32];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                int i2 = k + 1;
                str[k] = hexDigits[(byte0 >>> 4) & 15];
                k = i2 + 1;
                str[i2] = hexDigits[byte0 & 15];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(hexdigest("c"));
    }
}
