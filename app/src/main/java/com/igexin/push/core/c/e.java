package com.igexin.push.core.c;

import android.content.ContentValues;
import com.igexin.push.b.d;

class e extends d {
    final /* synthetic */ long a;
    final /* synthetic */ c b;

    e(c cVar, ContentValues contentValues, long j) {
        this.b = cVar;
        this.a = j;
        super(contentValues);
    }

    public void a() {
        this.c.delete("ral", "id=?", new String[]{String.valueOf(this.a)});
    }
}
