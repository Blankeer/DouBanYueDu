package com.crashlytics.android.core;

import com.tencent.connect.common.Constants;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.BackgroundPriorityRunnable;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ReportUploader {
    private static final String CLS_FILE_EXT = ".cls";
    static final Map<String, String> HEADER_INVALID_CLS_FILE;
    private static final short[] RETRY_INTERVALS;
    private static final FilenameFilter crashFileFilter;
    private final CreateReportSpiCall createReportCall;
    private final Object fileAccessLock;
    private Thread uploadThread;

    private class Worker extends BackgroundPriorityRunnable {
        private final float delay;

        Worker(float delay) {
            this.delay = delay;
        }

        public void onRun() {
            try {
                attemptUploadWithRetry();
            } catch (Exception e) {
                Fabric.getLogger().e(CrashlyticsCore.TAG, "An unexpected error occurred while attempting to upload crash reports.", e);
            }
            ReportUploader.this.uploadThread = null;
        }

        private void attemptUploadWithRetry() {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Starting report processing in " + this.delay + " second(s)...");
            if (this.delay > 0.0f) {
                try {
                    Thread.sleep((long) (this.delay * 1000.0f));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            CrashlyticsCore crashlyticsCore = CrashlyticsCore.getInstance();
            CrashlyticsUncaughtExceptionHandler handler = crashlyticsCore.getHandler();
            List<Report> reports = ReportUploader.this.findReports();
            if (!handler.isHandlingException()) {
                if (reports.isEmpty() || crashlyticsCore.canSendWithUserApproval()) {
                    int retryCount = 0;
                    while (!reports.isEmpty() && !CrashlyticsCore.getInstance().getHandler().isHandlingException()) {
                        Fabric.getLogger().d(CrashlyticsCore.TAG, "Attempting to send " + reports.size() + " report(s)");
                        for (Report report : reports) {
                            ReportUploader.this.forceUpload(report);
                        }
                        reports = ReportUploader.this.findReports();
                        if (!reports.isEmpty()) {
                            int retryCount2 = retryCount + 1;
                            long interval = (long) ReportUploader.RETRY_INTERVALS[Math.min(retryCount, ReportUploader.RETRY_INTERVALS.length - 1)];
                            Fabric.getLogger().d(CrashlyticsCore.TAG, "Report submisson: scheduling delayed retry in " + interval + " seconds");
                            try {
                                Thread.sleep(1000 * interval);
                                retryCount = retryCount2;
                            } catch (InterruptedException e2) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                    }
                    return;
                }
                Fabric.getLogger().d(CrashlyticsCore.TAG, "User declined to send. Removing " + reports.size() + " Report(s).");
                for (Report report2 : reports) {
                    report2.remove();
                }
            }
        }
    }

    static {
        crashFileFilter = new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(ReportUploader.CLS_FILE_EXT) && !filename.contains("Session");
            }
        };
        HEADER_INVALID_CLS_FILE = Collections.singletonMap("X-CRASHLYTICS-INVALID-SESSION", Constants.VIA_TO_TYPE_QQ_GROUP);
        RETRY_INTERVALS = new short[]{(short) 10, (short) 20, (short) 30, (short) 60, (short) 120, (short) 300};
    }

    public ReportUploader(CreateReportSpiCall createReportCall) {
        this.fileAccessLock = new Object();
        if (createReportCall == null) {
            throw new IllegalArgumentException("createReportCall must not be null.");
        }
        this.createReportCall = createReportCall;
    }

    public void uploadReports() {
        uploadReports(0.0f);
    }

    public synchronized void uploadReports(float delay) {
        if (this.uploadThread == null) {
            this.uploadThread = new Thread(new Worker(delay), "Crashlytics Report Uploader");
            this.uploadThread.start();
        }
    }

    boolean isUploading() {
        return this.uploadThread != null;
    }

    boolean forceUpload(Report report) {
        boolean removed = false;
        synchronized (this.fileAccessLock) {
            try {
                boolean sent = this.createReportCall.invoke(new CreateReportRequest(new ApiKey().getValue(CrashlyticsCore.getInstance().getContext()), report));
                Fabric.getLogger().i(CrashlyticsCore.TAG, "Crashlytics report upload " + (sent ? "complete: " : "FAILED: ") + report.getFileName());
                if (sent) {
                    report.remove();
                    removed = true;
                }
            } catch (Exception e) {
                Fabric.getLogger().e(CrashlyticsCore.TAG, "Error occurred sending report " + report, e);
            }
        }
        return removed;
    }

    List<Report> findReports() {
        Fabric.getLogger().d(CrashlyticsCore.TAG, "Checking for crash reports...");
        synchronized (this.fileAccessLock) {
            File[] clsFiles = CrashlyticsCore.getInstance().getSdkDirectory().listFiles(crashFileFilter);
        }
        List<Report> reports = new LinkedList();
        for (File file : clsFiles) {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Found crash report " + file.getPath());
            reports.add(new SessionReport(file));
        }
        if (reports.isEmpty()) {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "No reports found.");
        }
        return reports;
    }
}
