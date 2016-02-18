package com.douban.book.reader.task;

import android.net.Uri;
import com.douban.book.reader.content.Book;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.executor.TaggedRunnable;
import com.douban.book.reader.util.ReaderUriUtils;

public class DownloadTask extends TaggedRunnable {
    private int mBookId;
    private Uri mUri;
    private boolean mWithSizeLimit;

    public DownloadTask(Uri uri, boolean withSizeLimit) {
        super(uri);
        this.mUri = uri;
        this.mWithSizeLimit = withSizeLimit;
        this.mBookId = ReaderUriUtils.getWorksId(uri);
    }

    public void run() {
        RuntimeException runtimeException;
        int type = ReaderUriUtils.getType(this.mUri);
        int packageId = ReaderUriUtils.getPackageId(this.mUri);
        try {
            WorksData.get(this.mBookId).setIsDownloadPaused(false);
            Manifest.loadFromNetwork(this.mBookId);
            if (type == 0) {
                WorksData.get(this.mBookId).download(this.mWithSizeLimit);
            } else if (type == 2) {
                WorksData.get(this.mBookId).getPackage(packageId).download();
            }
            WorksData.get(this.mBookId).setIsPartial(Manifest.get(this.mBookId).isPartial);
            Book.clearCacheData(this.mBookId);
        } catch (Throwable th) {
            runtimeException = new RuntimeException(e);
        }
    }
}
