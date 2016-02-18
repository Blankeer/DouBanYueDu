package com.douban.book.reader.task;

import com.douban.book.reader.entity.Manifest;
import com.douban.book.reader.executor.TaggedRunnable;
import com.douban.book.reader.manager.AnnotationManager;
import com.douban.book.reader.manager.BookmarkManager;
import com.douban.book.reader.manager.ProgressManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Tag;

public class WorksSyncTask extends TaggedRunnable {
    int mWorksId;

    public WorksSyncTask(int worksId) {
        super(Integer.valueOf(worksId));
        this.mWorksId = worksId;
    }

    public void run() {
        try {
            Logger.t(Tag.SYNC, "sync for works %s started.", Integer.valueOf(this.mWorksId));
            ProgressManager.ofWorks(this.mWorksId).refresh();
            BookmarkManager.ofWorks(this.mWorksId).refresh();
            AnnotationManager.ofWorks(this.mWorksId).refresh();
            Manifest.loadFromNetwork(this.mWorksId);
            Logger.t(Tag.SYNC, "sync for works %s succeed.", Integer.valueOf(this.mWorksId));
        } catch (Throwable e) {
            Logger.e(Tag.SYNC, e, "sync for works %s got an error:", Integer.valueOf(this.mWorksId));
            RuntimeException runtimeException = new RuntimeException(e);
        }
    }
}
