package com.douban.book.reader.event;

import android.net.Uri;
import com.douban.book.reader.content.pack.Package;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.exception.PackageException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUriUtils;
import com.douban.book.reader.util.Tag;

public class DownloadProgressChangedEvent extends PackageEvent {
    public DownloadProgressChangedEvent(Uri uri) {
        super(uri);
    }

    public int getProgress() {
        if (ReaderUriUtils.getType(getUri()) == 0) {
            return WorksData.get(ReaderUriUtils.getWorksId(getUri())).getDownloadProgress();
        }
        try {
            return Package.get(getUri()).getDownloadProgress();
        } catch (PackageException e) {
            Logger.e(Tag.GENERAL, e);
            return -1;
        }
    }

    public int getProgressForWorks() {
        return WorksData.get(ReaderUriUtils.getWorksId(getUri())).getDownloadProgress();
    }
}
