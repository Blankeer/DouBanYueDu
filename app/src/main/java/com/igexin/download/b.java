package com.igexin.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final class b extends SQLiteOpenHelper {
    final /* synthetic */ DownloadProvider a;

    public b(DownloadProvider downloadProvider, Context context) {
        this.a = downloadProvider;
        super(context, DownloadProvider.a, null, Header.FLOAT);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        this.a.a(sQLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i != 31 || i2 != 100) {
            this.a.b(sQLiteDatabase);
            this.a.a(sQLiteDatabase);
        }
    }
}
