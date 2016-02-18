package com.igexin.push.c.c;

import android.support.v4.media.TransportMediator;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public class g {
    public static int a;
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public byte g;
    public byte h;
    public byte i;

    static {
        a = 5;
    }

    public void a() {
        this.g = (byte) (this.e & TransportMediator.FLAG_KEY_MEDIA_NEXT);
        this.h = (byte) (this.e & Header.ARRAY_SHORT);
        this.i = (byte) (this.e & 15);
    }

    public void a(byte b) {
        this.e = b & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
        a();
    }

    public void b() {
        this.e |= this.g;
        this.e |= this.h;
        this.e |= this.i;
    }

    public int c() {
        b();
        return this.e;
    }
}
