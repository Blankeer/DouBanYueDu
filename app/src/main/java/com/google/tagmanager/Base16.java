package com.google.tagmanager;

import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

class Base16 {
    Base16() {
    }

    public static String encode(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            if ((b & 240) == 0) {
                sb.append(Constants.VIA_RESULT_SUCCESS);
            }
            sb.append(Integer.toHexString(b & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT));
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] decode(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("purported base16 string has odd number of characters");
        }
        byte[] result = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            int c1 = Character.digit(s.charAt(i), 16);
            int c2 = Character.digit(s.charAt(i + 1), 16);
            if (c1 == -1 || c2 == -1) {
                throw new IllegalArgumentException("purported base16 string has illegal char");
            }
            result[i / 2] = (byte) ((c1 << 4) + c2);
        }
        return result;
    }
}
