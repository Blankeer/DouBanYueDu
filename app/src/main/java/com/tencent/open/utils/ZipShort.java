package com.tencent.open.utils;

import android.support.v4.view.MotionEventCompat;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

/* compiled from: ProGuard */
public final class ZipShort implements Cloneable {
    private int a;

    public ZipShort(byte[] bArr) {
        this(bArr, 0);
    }

    public ZipShort(byte[] bArr, int i) {
        this.a = (bArr[i + 1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK;
        this.a += bArr[i] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
    }

    public ZipShort(int i) {
        this.a = i;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof ZipShort) && this.a == ((ZipShort) obj).getValue()) {
            return true;
        }
        return false;
    }

    public byte[] getBytes() {
        return new byte[]{(byte) (this.a & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT), (byte) ((this.a & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8)};
    }

    public int getValue() {
        return this.a;
    }

    public int hashCode() {
        return this.a;
    }
}
