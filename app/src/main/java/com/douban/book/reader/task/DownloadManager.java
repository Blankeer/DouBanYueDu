package com.douban.book.reader.task;

import android.net.Uri;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.content.pack.WorksData;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.event.DownloadStatusChangedEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.executor.TaggedRunnableExecutor;
import com.douban.book.reader.executor.TaggedRunnableExecutor.StatusCallback;
import com.douban.book.reader.executor.WorksUriTagMatcher;
import com.douban.book.reader.manager.ShelfManager_;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUriUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.Utils;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class DownloadManager {
    private static final int MAX_TASKS_ALLOWED = 3;
    private static final String TAG;
    private static TaggedRunnableExecutor sExecutor;

    /* renamed from: com.douban.book.reader.task.DownloadManager.1 */
    static class AnonymousClass1 implements Runnable {
        final /* synthetic */ Uri val$uri;

        AnonymousClass1(Uri uri) {
            this.val$uri = uri;
        }

        public void run() {
            try {
                ShelfManager_.getInstance_(App.get()).addWorks(ReaderUriUtils.getWorksId(this.val$uri));
            } catch (DataLoadException e) {
                Logger.e(DownloadManager.TAG, e);
            }
        }
    }

    private static final class Callback implements StatusCallback {
        private Callback() {
        }

        public void beforeExecute(Object tag) {
            EventBusUtils.post(new DownloadStatusChangedEvent((Uri) tag));
        }

        public void afterExecute(Object tag, Throwable throwable) {
            EventBusUtils.post(new DownloadStatusChangedEvent((Uri) tag));
        }

        public void cancelledBeforeExecute(Object tag) {
            Uri uri = (Uri) tag;
            if (uri != null) {
                WorksData.get(ReaderUriUtils.getWorksId(uri)).setIsDownloadPaused(true);
            }
            EventBusUtils.post(new DownloadStatusChangedEvent(uri));
        }
    }

    static {
        TAG = DownloadManager.class.getSimpleName();
        sExecutor = new TaggedRunnableExecutor("Download", MAX_TASKS_ALLOWED, 1);
        sExecutor.setStatusCallback(new Callback());
    }

    public static void scheduleDownload(Uri uri) {
        doSchedule(uri, true, 0);
    }

    public static void scheduleDownloadSkippingSizeLimitCheck(Uri uri) {
        doSchedule(uri, false, 0);
    }

    private static void doSchedule(Uri uri, boolean withSizeLimit, int delay) {
        if (!Utils.isNetworkAvailable()) {
            ToastUtils.showToast((int) R.string.toast_failed_network_error);
        } else if (!isScheduled(uri)) {
            TaskController.run(new AnonymousClass1(uri));
            try {
                sExecutor.schedule(new DownloadTask(uri, withSizeLimit), (long) delay, TimeUnit.MILLISECONDS);
                EventBusUtils.post(new DownloadStatusChangedEvent(uri));
            } catch (RejectedExecutionException e) {
                Logger.e(TAG, e);
            }
        }
    }

    public static boolean isScheduled(Uri uri) {
        return sExecutor.isScheduled((Object) uri);
    }

    public static boolean isScheduled(int worksId) {
        return sExecutor.isScheduled(new WorksUriTagMatcher(worksId));
    }

    public static boolean isDownloading(Uri uri) {
        return sExecutor.isRunning((Object) uri);
    }

    public static boolean isDownloading(int worksId) {
        return sExecutor.isRunning(new WorksUriTagMatcher(worksId));
    }

    public static void stopDownloading(Uri uri) {
        sExecutor.cancelByTag(uri);
    }

    public static void stopDownloading(int worksId) {
        sExecutor.cancelMatched(new WorksUriTagMatcher(worksId));
    }

    public static void stopDownloading() {
        sExecutor.cancelAll();
    }
}
