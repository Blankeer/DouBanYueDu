package com.douban.book.reader.helper;

import android.net.Uri;
import com.douban.book.reader.activity.ReaderActivity_;
import com.douban.book.reader.activity.ReaderActivity_.IntentBuilder_;
import com.douban.book.reader.app.App;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.content.pack.WorksData.Status;
import com.douban.book.reader.task.DownloadManager;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.ReaderUriUtils;
import u.aly.dx;

public class BookItemClickHelper {
    private PageOpenHelper mPageOpenHelper;

    /* renamed from: com.douban.book.reader.helper.BookItemClickHelper.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status;

        static {
            $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status = new int[Status.values().length];
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.DOWNLOADING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.PENDING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.READY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.EMPTY.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.FAILED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[Status.PAUSED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public BookItemClickHelper(PageOpenHelper helper) {
        this.mPageOpenHelper = helper;
    }

    public void performBookItemClick(int bookId) {
        performBookItemClick(ReaderUri.works(bookId));
    }

    public void performBookItemClick(Uri uri) {
        int bookId = ReaderUriUtils.getWorksId(uri);
        int type = ReaderUriUtils.getType(uri);
        Status status = Status.EMPTY;
        if (type == 0) {
            status = WorksData.get(bookId).getStatus();
        } else if (type == 2) {
            status = WorksData.get(bookId).getPackage(ReaderUriUtils.getPackageId(uri)).getStatus();
        }
        switch (AnonymousClass1.$SwitchMap$com$douban$book$reader$content$pack$WorksData$Status[status.ordinal()]) {
            case dx.b /*1*/:
                DownloadManager.stopDownloading(uri);
            case dx.c /*2*/:
                DownloadManager.stopDownloading(uri);
            case dx.d /*3*/:
                this.mPageOpenHelper.open(((IntentBuilder_) ReaderActivity_.intent(App.get()).flags(67108864)).mBookId(bookId).get());
            default:
                DownloadManager.scheduleDownload(uri);
        }
    }
}
