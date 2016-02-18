package com.igexin.download;

import android.database.ContentObserver;
import android.os.Handler;

class d extends ContentObserver {
    final /* synthetic */ DownloadService a;

    public d(DownloadService downloadService) {
        this.a = downloadService;
        super(new Handler());
    }

    public void onChange(boolean z) {
        this.a.a();
    }
}
