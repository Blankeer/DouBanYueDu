package com.j256.ormlite.android.compat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;
import com.j256.ormlite.android.compat.ApiCompatibility.CancellationHook;

public class JellyBeanApiCompatibility extends BasicApiCompatibility {

    protected static class JellyBeanCancellationHook implements CancellationHook {
        private final CancellationSignal cancellationSignal;

        public JellyBeanCancellationHook() {
            this.cancellationSignal = new CancellationSignal();
        }

        public void cancel() {
            this.cancellationSignal.cancel();
        }
    }

    public Cursor rawQuery(SQLiteDatabase db, String sql, String[] selectionArgs, CancellationHook cancellationHook) {
        if (cancellationHook == null) {
            return db.rawQuery(sql, selectionArgs);
        }
        return db.rawQuery(sql, selectionArgs, ((JellyBeanCancellationHook) cancellationHook).cancellationSignal);
    }

    public CancellationHook createCancellationHook() {
        return new JellyBeanCancellationHook();
    }
}
