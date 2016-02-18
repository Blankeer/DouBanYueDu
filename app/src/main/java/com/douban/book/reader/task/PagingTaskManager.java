package com.douban.book.reader.task;

import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.PageMetrics;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.event.PagingEndedEvent;
import com.douban.book.reader.event.PagingFailedEvent;
import com.douban.book.reader.event.PagingStartedEvent;
import com.douban.book.reader.executor.TaggedRunnableExecutor;
import com.douban.book.reader.executor.TaggedRunnableExecutor.StatusCallback;
import com.douban.book.reader.network.exception.RestException;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.Tag;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class PagingTaskManager {
    private static final long MIN_BACKGROUND_PAGING_INTERVAL = 86400000;
    private static TaggedRunnableExecutor sBackgroundExecutor;
    private static TaggedRunnableExecutor sForegroundExecutor;

    private static final class Callback implements StatusCallback {
        private Callback() {
        }

        public void beforeExecute(Object tag) {
            EventBusUtils.post(new PagingStartedEvent(((Integer) tag).intValue()));
        }

        public void afterExecute(Object tag, Throwable throwable) {
            if (throwable instanceof ExecutionException) {
                EventBusUtils.post(new PagingFailedEvent(((Integer) tag).intValue()));
                if (!ExceptionUtils.isCausedBy(throwable, RestException.class)) {
                    Logger.ec(Tag.PAGING, throwable);
                }
            }
            EventBusUtils.post(new PagingEndedEvent(((Integer) tag).intValue()));
        }

        public void cancelledBeforeExecute(Object tag) {
        }
    }

    static {
        sBackgroundExecutor = new TaggedRunnableExecutor("Background Paging", 1, 2);
        sForegroundExecutor = new TaggedRunnableExecutor("Foreground Paging", 1, 8);
        sForegroundExecutor.setStatusCallback(new Callback());
    }

    public static void schedulePagingAll(int delay) {
        if (!shouldDisableBackendPaging()) {
            if (System.currentTimeMillis() - Pref.ofApp().getLong(Key.APP_LAST_BACKGROUND_PAGING_TIME, 0) < MIN_BACKGROUND_PAGING_INTERVAL) {
                Logger.d(Tag.PAGING, "Background paging skipped.", new Object[0]);
            } else {
                TaskController.run(new Runnable() {
                    public void run() {
                        Pref.ofApp().set(Key.APP_LAST_BACKGROUND_PAGING_TIME, Long.valueOf(System.currentTimeMillis()));
                    }
                });
            }
        }
    }

    public static void schedulePaging(int bookId, int delay) {
        if (!shouldDisableBackendPaging()) {
            try {
                if (sBackgroundExecutor.isScheduled(Integer.valueOf(bookId)) || sForegroundExecutor.isScheduled(Integer.valueOf(bookId))) {
                    Logger.d(Tag.PAGING, "schedulePaging cancelled for %s because has already been scheduled.", Integer.valueOf(bookId));
                    return;
                }
                sBackgroundExecutor.schedule(new PagingTask(bookId, PageMetrics.getDefault()), (long) delay, TimeUnit.MILLISECONDS);
            } catch (RejectedExecutionException e) {
                Logger.e(Tag.PAGING, e);
            }
        }
    }

    public static void foregroundPaging(int bookId, PageMetrics pageMetrics) {
        Logger.dc(Tag.PAGING, "foregroundPaging. worksId=%s, pageMetrics=%s", Integer.valueOf(bookId), pageMetrics);
        try {
            cancelBackgroundPaging();
            cancelForegroundPaging();
            sForegroundExecutor.execute(new PagingTask(bookId, pageMetrics));
        } catch (RejectedExecutionException e) {
            Logger.e(Tag.PAGING, e);
        }
    }

    public static void cancelPagingAll() {
        cancelForegroundPaging();
        cancelBackgroundPaging();
    }

    public static void cancelBackgroundPaging() {
        Logger.dc(Tag.PAGING, "cancelBackgroundPaging", new Object[0]);
        if (sBackgroundExecutor != null) {
            sBackgroundExecutor.cancelAll();
        }
    }

    public static void cancelForegroundPaging() {
        Logger.dc(Tag.PAGING, "cancelForegroundPaging", new Object[0]);
        if (sForegroundExecutor != null) {
            sForegroundExecutor.cancelAll();
        }
    }

    public static boolean isPaging(int bookId) {
        return sBackgroundExecutor.isRunning(Integer.valueOf(bookId)) || sForegroundExecutor.isRunning(Integer.valueOf(bookId));
    }

    private static boolean shouldDisableBackendPaging() {
        return Runtime.getRuntime().maxMemory() < Constants.BACKEND_PAGING_MIN_MEMORY_REQUIREMENT;
    }
}
