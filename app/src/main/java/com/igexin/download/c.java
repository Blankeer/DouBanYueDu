package com.igexin.download;

import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;

class c extends CursorWrapper implements CrossProcessCursor {
    final /* synthetic */ DownloadProvider a;
    private CrossProcessCursor b;

    public c(DownloadProvider downloadProvider, Cursor cursor) {
        this.a = downloadProvider;
        super(cursor);
        this.b = (CrossProcessCursor) cursor;
    }

    public void fillWindow(int i, CursorWindow cursorWindow) {
        this.b.fillWindow(i, cursorWindow);
    }

    public CursorWindow getWindow() {
        return this.b.getWindow();
    }

    public boolean onMove(int i, int i2) {
        return this.b.onMove(i, i2);
    }
}
