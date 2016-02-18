package com.igexin.push.b;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.igexin.push.config.k;
import com.igexin.push.core.f;

public abstract class d extends com.igexin.a.a.d.d {
    private static final String a;
    protected SQLiteDatabase c;
    protected Cursor d;
    protected ContentValues e;
    public c f;

    static {
        a = k.a;
    }

    public d() {
        super(1);
    }

    public d(ContentValues contentValues) {
        super(1);
        this.e = contentValues;
    }

    public abstract void a();

    public void a_() {
        super.a_();
        this.c = f.a().k().getWritableDatabase();
        a();
        if (this.f != null) {
            com.igexin.a.a.b.d.c().a((Object) this.f);
            com.igexin.a.a.b.d.c().d();
        }
    }

    public final int b() {
        return -2147483640;
    }

    public void c() {
        super.c();
        if (this.d != null && !this.d.isClosed()) {
            try {
                this.d.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void d() {
        this.y = true;
        this.T = true;
    }

    protected void e() {
    }
}
