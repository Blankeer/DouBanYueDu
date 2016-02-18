package com.igexin.push.c.c;

import com.igexin.a.a.b.g;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class i extends e {
    public long a;
    public byte b;
    public int c;
    public String d;
    public List e;

    public i() {
        this.i = 4;
        this.j = (byte) 52;
    }

    private String a(byte[] bArr, int i, int i2) {
        try {
            return new String(bArr, i, i2, HttpRequest.CHARSET_UTF8);
        } catch (Exception e) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }

    public void a(byte[] bArr) {
        int i;
        this.a = g.d(bArr, 0);
        this.b = bArr[8];
        this.c = g.c(bArr, 9) & -1;
        if (bArr.length > 13) {
            i = 14;
            int i2 = bArr[13] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
            if (i2 > 0) {
                this.e = new ArrayList();
                i2 += 14;
                while (i < i2) {
                    j jVar = new j();
                    this.e.add(jVar);
                    int i3 = i + 1;
                    i = g.a(bArr, i) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                    int i4 = i3 + 1;
                    i3 = g.a(bArr, i3) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT;
                    jVar.a = (byte) i;
                    if ((i == 1 || i == 4) && i3 > 0) {
                        try {
                            jVar.b = new String(bArr, i4, i3, HttpRequest.CHARSET_UTF8);
                        } catch (Exception e) {
                        }
                    }
                    i = i4 + i3;
                }
            }
        } else {
            i = 13;
        }
        if (bArr.length > i) {
            this.d = a(bArr, i + 1, bArr[i] & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT);
        }
    }

    public byte[] d() {
        byte[] bArr;
        int length;
        int i;
        byte[] bArr2 = null;
        if (this.e == null || this.e.size() <= 0) {
            bArr = null;
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (j d : this.e) {
                byte[] toByteArray;
                try {
                    byteArrayOutputStream.write(d.d());
                    toByteArray = byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    toByteArray = bArr2;
                }
                bArr2 = toByteArray;
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                    bArr = bArr2;
                } catch (IOException e2) {
                    bArr = bArr2;
                }
            } else {
                bArr = bArr2;
            }
        }
        if (bArr != null) {
            length = bArr.length;
            i = length + 1;
        } else {
            i = 1;
            length = 0;
        }
        byte[] bArr3 = new byte[(((i + 12) + this.d.getBytes().length) + 1)];
        i = g.a(this.a, bArr3, 0);
        i += g.a(((this.b & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << 24) | this.c, bArr3, i);
        i += g.c(length, bArr3, i);
        length = length > 0 ? g.a(bArr, 0, bArr3, i, length) + i : i;
        Object bytes = this.d.getBytes();
        int i2 = length + 1;
        g.c(bytes.length, bArr3, length);
        System.arraycopy(bytes, 0, bArr3, i2, bytes.length);
        length = bytes.length + i2;
        return bArr3;
    }
}
