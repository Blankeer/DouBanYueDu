package com.crashlytics.android.answers;

import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.ApiKey;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.events.FilesSender;
import io.fabric.sdk.android.services.events.TimeBasedFileRollOverRunnable;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

class EnabledSessionAnalyticsManagerStrategy implements SessionAnalyticsManagerStrategy {
    static final int UNDEFINED_ROLLOVER_INTERVAL_SECONDS = -1;
    ApiKey apiKey;
    private final Context context;
    boolean customEventsEnabled;
    EventFilter eventFilter;
    private final ScheduledExecutorService executorService;
    private final SessionAnalyticsFilesManager filesManager;
    FilesSender filesSender;
    private final HttpRequestFactory httpRequestFactory;
    private final Kit kit;
    final SessionEventMetadata metadata;
    boolean predefinedEventsEnabled;
    private final AtomicReference<ScheduledFuture<?>> rolloverFutureRef;
    volatile int rolloverIntervalSeconds;

    public EnabledSessionAnalyticsManagerStrategy(Kit kit, Context context, ScheduledExecutorService executor, SessionAnalyticsFilesManager filesManager, HttpRequestFactory httpRequestFactory, SessionEventMetadata metadata) {
        this.rolloverFutureRef = new AtomicReference();
        this.apiKey = new ApiKey();
        this.eventFilter = new KeepAllEventFilter();
        this.customEventsEnabled = true;
        this.predefinedEventsEnabled = true;
        this.rolloverIntervalSeconds = UNDEFINED_ROLLOVER_INTERVAL_SECONDS;
        this.kit = kit;
        this.context = context;
        this.executorService = executor;
        this.filesManager = filesManager;
        this.httpRequestFactory = httpRequestFactory;
        this.metadata = metadata;
    }

    public void setAnalyticsSettingsData(AnalyticsSettingsData analyticsSettingsData, String protocolAndHostOverride) {
        this.filesSender = AnswersRetryFilesSender.build(new SessionAnalyticsFilesSender(this.kit, protocolAndHostOverride, analyticsSettingsData.analyticsURL, this.httpRequestFactory, this.apiKey.getValue(this.context)));
        this.filesManager.setAnalyticsSettingsData(analyticsSettingsData);
        this.customEventsEnabled = analyticsSettingsData.trackCustomEvents;
        Fabric.getLogger().d(Answers.TAG, "Custom event tracking " + (this.customEventsEnabled ? "enabled" : "disabled"));
        this.predefinedEventsEnabled = analyticsSettingsData.trackPredefinedEvents;
        Fabric.getLogger().d(Answers.TAG, "Predefined event tracking " + (this.predefinedEventsEnabled ? "enabled" : "disabled"));
        if (analyticsSettingsData.samplingRate > 1) {
            Fabric.getLogger().d(Answers.TAG, "Event sampling enabled");
            this.eventFilter = new SamplingEventFilter(analyticsSettingsData.samplingRate);
        }
        this.rolloverIntervalSeconds = analyticsSettingsData.flushIntervalSeconds;
        scheduleTimeBasedFileRollOver(0, (long) this.rolloverIntervalSeconds);
    }

    public void processEvent(Builder builder) {
        SessionEvent event = builder.build(this.metadata);
        if (!this.customEventsEnabled && Type.CUSTOM.equals(event.type)) {
            Fabric.getLogger().d(Answers.TAG, "Custom events tracking disabled - skipping event: " + event);
        } else if (!this.predefinedEventsEnabled && Type.PREDEFINED.equals(event.type)) {
            Fabric.getLogger().d(Answers.TAG, "Predefined events tracking disabled - skipping event: " + event);
        } else if (this.eventFilter.skipEvent(event)) {
            Fabric.getLogger().d(Answers.TAG, "Skipping filtered event: " + event);
        } else {
            try {
                this.filesManager.writeEvent(event);
            } catch (IOException e) {
                Fabric.getLogger().e(Answers.TAG, "Failed to write event: " + event, e);
            }
            scheduleTimeBasedRollOverIfNeeded();
        }
    }

    public void scheduleTimeBasedRollOverIfNeeded() {
        if (this.rolloverIntervalSeconds != UNDEFINED_ROLLOVER_INTERVAL_SECONDS) {
            scheduleTimeBasedFileRollOver((long) this.rolloverIntervalSeconds, (long) this.rolloverIntervalSeconds);
        }
    }

    public void sendEvents() {
        if (this.filesSender == null) {
            CommonUtils.logControlled(this.context, "skipping files send because we don't yet know the target endpoint");
            return;
        }
        CommonUtils.logControlled(this.context, "Sending all files");
        int filesSent = 0;
        List<File> batch = this.filesManager.getBatchOfFilesToSend();
        while (batch.size() > 0) {
            CommonUtils.logControlled(this.context, String.format(Locale.US, "attempt to send batch of %d files", new Object[]{Integer.valueOf(batch.size())}));
            boolean cleanup = this.filesSender.send(batch);
            if (cleanup) {
                filesSent += batch.size();
                this.filesManager.deleteSentFiles(batch);
            }
            if (!cleanup) {
                break;
            }
            try {
                batch = this.filesManager.getBatchOfFilesToSend();
            } catch (Exception e) {
                CommonUtils.logControlledError(this.context, "Failed to send batch of analytics files to server: " + e.getMessage(), e);
            }
        }
        if (filesSent == 0) {
            this.filesManager.deleteOldestInRollOverIfOverMax();
        }
    }

    public void cancelTimeBasedFileRollOver() {
        if (this.rolloverFutureRef.get() != null) {
            CommonUtils.logControlled(this.context, "Cancelling time-based rollover because no events are currently being generated.");
            ((ScheduledFuture) this.rolloverFutureRef.get()).cancel(false);
            this.rolloverFutureRef.set(null);
        }
    }

    public void deleteAllEvents() {
        this.filesManager.deleteAllEventsFiles();
    }

    public boolean rollFileOver() {
        try {
            return this.filesManager.rollFileOver();
        } catch (IOException e) {
            CommonUtils.logControlledError(this.context, "Failed to roll file over.", e);
            return false;
        }
    }

    void scheduleTimeBasedFileRollOver(long initialDelaySecs, long frequencySecs) {
        if (this.rolloverFutureRef.get() == null) {
            Runnable rollOverRunnable = new TimeBasedFileRollOverRunnable(this.context, this);
            CommonUtils.logControlled(this.context, "Scheduling time based file roll over every " + frequencySecs + " seconds");
            try {
                this.rolloverFutureRef.set(this.executorService.scheduleAtFixedRate(rollOverRunnable, initialDelaySecs, frequencySecs, TimeUnit.SECONDS));
            } catch (RejectedExecutionException e) {
                CommonUtils.logControlledError(this.context, "Failed to schedule time based file roll over", e);
            }
        }
    }
}
