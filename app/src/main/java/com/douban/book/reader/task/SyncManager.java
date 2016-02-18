package com.douban.book.reader.task;

import com.douban.book.reader.executor.TaggedRunnableExecutor;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Tag;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class SyncManager {
    private static TaggedRunnableExecutor sExecutor;

    static {
        sExecutor = new TaggedRunnableExecutor("Sync", 1, 2);
    }

    public static void sync(int worksId) {
        scheduleSync(worksId, 0);
    }

    public static void scheduleSync(int worksId, int delay) {
        if (!shouldDisableSync()) {
            try {
                if (sExecutor.isScheduled(Integer.valueOf(worksId))) {
                    Logger.d(Tag.SYNC, "scheduleSync cancelled for %s because has already been scheduled.", Integer.valueOf(worksId));
                    return;
                }
                sExecutor.schedule(new WorksSyncTask(worksId), (long) delay, TimeUnit.MILLISECONDS);
            } catch (RejectedExecutionException e) {
                Logger.e(Tag.SYNC, e);
            }
        }
    }

    public static boolean isSyncing(int worksId) {
        return sExecutor.isRunning(Integer.valueOf(worksId));
    }

    public static void cancelSync(int worksId) {
        sExecutor.cancelByTag(Integer.valueOf(worksId));
    }

    public static void cancelSyncAll() {
        sExecutor.cancelAll();
    }

    private static boolean shouldDisableSync() {
        return false;
    }
}
