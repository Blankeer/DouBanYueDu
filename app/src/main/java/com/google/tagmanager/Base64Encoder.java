package com.google.tagmanager;

import android.os.Build.VERSION;
import android.util.Base64;
import com.google.android.gms.common.util.VisibleForTesting;

class Base64Encoder {
    public static final int DEFAULT = 0;
    public static final int NO_PADDING = 1;
    public static final int URL_SAFE = 2;

    Base64Encoder() {
    }

    public static String encodeToString(byte[] input, int flags) {
        boolean websafeDesired = true;
        if (getSdkVersion() >= 8) {
            int newFlags = URL_SAFE;
            if ((flags & NO_PADDING) != 0) {
                newFlags = URL_SAFE | NO_PADDING;
            }
            if ((flags & URL_SAFE) != 0) {
                newFlags |= 8;
            }
            return Base64.encodeToString(input, newFlags);
        }
        boolean paddingDesired;
        if ((flags & NO_PADDING) == 0) {
            paddingDesired = true;
        } else {
            paddingDesired = false;
        }
        if ((flags & URL_SAFE) == 0) {
            websafeDesired = false;
        }
        if (websafeDesired) {
            return Base64.encodeWebSafe(input, paddingDesired);
        }
        return Base64.encode(input, paddingDesired);
    }

    public static byte[] decode(String s, int flags) {
        boolean websafeDesired = true;
        if (getSdkVersion() >= 8) {
            int newFlags = URL_SAFE;
            if ((flags & NO_PADDING) != 0) {
                newFlags = URL_SAFE | NO_PADDING;
            }
            if ((flags & URL_SAFE) != 0) {
                newFlags |= 8;
            }
            return Base64.decode(s, newFlags);
        }
        boolean paddingDesired;
        if ((flags & NO_PADDING) == 0) {
            paddingDesired = true;
        } else {
            paddingDesired = false;
        }
        if ((flags & URL_SAFE) == 0) {
            websafeDesired = false;
        }
        if (websafeDesired) {
            return Base64.decodeWebSafe(s);
        }
        return Base64.decode(s);
    }

    @VisibleForTesting
    static int getSdkVersion() {
        return VERSION.SDK_INT;
    }
}
