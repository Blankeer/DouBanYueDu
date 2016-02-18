package com.douban.book.reader.job;

import com.douban.book.reader.app.App;
import com.douban.book.reader.util.Logger;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration.Builder;
import com.path.android.jobqueue.log.CustomLogger;

public class JobUtils {
    public static final int PRIORITY_NORMAL = 1;
    private static final String TAG;
    private static JobManager sJobManager;

    static {
        TAG = JobUtils.class.getSimpleName();
        sJobManager = new JobManager(App.get(), new Builder(App.get()).minConsumerCount(PRIORITY_NORMAL).maxConsumerCount(3).loadFactor(3).consumerKeepAlive(Header.ARRAY_LONG_BYTE).customLogger(new CustomLogger() {
            public boolean isDebugEnabled() {
                return Logger.isLogEnabled();
            }

            public void d(String text, Object... args) {
                Logger.d(JobUtils.TAG, text, args);
            }

            public void e(Throwable t, String text, Object... args) {
                Logger.e(JobUtils.TAG, t, text, args);
            }

            public void e(String text, Object... args) {
                Logger.e(JobUtils.TAG, text, args);
            }
        }).build());
        sJobManager.start();
    }

    public static void addJob(Job job) {
        sJobManager.addJob(job);
    }

    public static boolean hasJobs() {
        return sJobManager.count() > 0;
    }

    public static void clearJobs() {
        sJobManager.clear();
    }

    public static void runOrSchedule(Job job) {
        if (job != null) {
            try {
                job.onRun();
            } catch (Throwable e) {
                Logger.e(TAG, e);
                addJob(job);
            }
        }
    }
}
