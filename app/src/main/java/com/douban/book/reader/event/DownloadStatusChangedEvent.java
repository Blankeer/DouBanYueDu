package com.douban.book.reader.event;

import android.net.Uri;

public class DownloadStatusChangedEvent extends PackageEvent {
    public DownloadStatusChangedEvent(Uri uri) {
        super(uri);
    }
}
