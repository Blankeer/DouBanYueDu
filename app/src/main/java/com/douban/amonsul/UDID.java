package com.douban.amonsul;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import io.realm.internal.Table;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UDID {
    private static final char[] DIGITS_LOWER;
    private static final String ENC_UTF8 = "UTF-8";
    public static int LENGTH = 0;
    private static final String SHA_1 = "SHA-1";
    public static final String TAG;

    static {
        TAG = UDID.class.getSimpleName();
        LENGTH = 40;
        DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    }

    public static String generate(Context context) {
        String deviceId;
        String androidId;
        try {
            deviceId = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
        } catch (Exception ex) {
            Log.v("UDID", "No permission to read device id, error:" + ex);
            deviceId = Table.STRING_DEFAULT_VALUE;
        }
        try {
            androidId = Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception ex2) {
            Log.v("UDID", "No permission to read android id, error:" + ex2);
            androidId = Table.STRING_DEFAULT_VALUE;
        }
        String serialId = Build.SERIAL;
        if (deviceId == null) {
            deviceId = Table.STRING_DEFAULT_VALUE;
        }
        if (androidId == null) {
            androidId = Table.STRING_DEFAULT_VALUE;
        }
        if (serialId == null) {
            serialId = Table.STRING_DEFAULT_VALUE;
        }
        Log.v(TAG, "UDID.generate() deviceId=" + deviceId + " androidId=" + androidId + " serialId=" + serialId);
        return sha1(deviceId + androidId + serialId);
    }

    private static String sha1(String text) {
        return new String(binToHex(getDigest(SHA_1).digest(getRawBytes(text))));
    }

    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static char[] binToHex(byte[] data) {
        int l = data.length;
        char[] out = new char[(l << 1)];
        int j = 0;
        for (int i = 0; i < l; i++) {
            int i2 = j + 1;
            out[j] = DIGITS_LOWER[(data[i] & 240) >>> 4];
            j = i2 + 1;
            out[i2] = DIGITS_LOWER[data[i] & 15];
        }
        return out;
    }

    private static byte[] getRawBytes(String text) {
        try {
            return text.getBytes(ENC_UTF8);
        } catch (UnsupportedEncodingException e) {
            return text.getBytes();
        }
    }
}
