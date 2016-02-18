package com.igexin.a.a.b;

import com.douban.book.reader.constant.Char;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.OutputStream;

public class a extends OutputStream {
    private OutputStream a;
    private int b;
    private int c;
    private int d;
    private int e;

    public a(OutputStream outputStream, int i) {
        this.a = null;
        this.b = 0;
        this.c = 0;
        this.d = 0;
        this.e = 0;
        this.a = outputStream;
        this.e = i;
    }

    protected void a() {
        int i = 61;
        if (this.c > 0) {
            if (this.e > 0 && this.d == this.e) {
                this.a.write(Char.CRLF.getBytes());
                this.d = 0;
            }
            char charAt = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((this.b << 8) >>> 26);
            char charAt2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((this.b << 14) >>> 26);
            int charAt3 = this.c < 2 ? 61 : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((this.b << 20) >>> 26);
            if (this.c >= 3) {
                i = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt((this.b << 26) >>> 26);
            }
            this.a.write(charAt);
            this.a.write(charAt2);
            this.a.write(charAt3);
            this.a.write(i);
            this.d += 4;
            this.c = 0;
            this.b = 0;
        }
    }

    public void close() {
        a();
        this.a.close();
    }

    public void write(int i) {
        this.b = ((i & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) << (16 - (this.c * 8))) | this.b;
        this.c++;
        if (this.c == 3) {
            a();
        }
    }
}
