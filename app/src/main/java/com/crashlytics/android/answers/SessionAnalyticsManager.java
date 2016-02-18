package com.crashlytics.android.answers;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import com.crashlytics.android.answers.BackgroundManager.Listener;
import io.fabric.sdk.android.ActivityLifecycleManager;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.common.ExecutorUtils;
import io.fabric.sdk.android.services.common.IdManager;
import io.fabric.sdk.android.services.network.DefaultHttpRequestFactory;
import io.fabric.sdk.android.services.persistence.FileStoreImpl;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import java.util.concurrent.ScheduledExecutorService;

class SessionAnalyticsManager implements Listener {
    static final String EXECUTOR_SERVICE = "Answers Events Handler";
    static final long FIRST_LAUNCH_INTERVAL_IN_MS = 3600000;
    static final String ON_CRASH_ERROR_MSG = "onCrash called from main thread!!!";
    final BackgroundManager backgroundManager;
    final AnswersEventsHandler eventsHandler;
    private final long installedAt;
    final ActivityLifecycleManager lifecycleManager;
    final AnswersPreferenceManager preferenceManager;

    public static SessionAnalyticsManager build(Kit kit, Context context, IdManager idManager, String versionCode, String versionName, long installedAt) {
        SessionMetadataCollector metadataCollector = new SessionMetadataCollector(context, idManager, versionCode, versionName);
        AnswersFilesManagerProvider filesManagerProvider = new AnswersFilesManagerProvider(context, new FileStoreImpl(kit));
        DefaultHttpRequestFactory httpRequestFactory = new DefaultHttpRequestFactory(Fabric.getLogger());
        ActivityLifecycleManager lifecycleManager = new ActivityLifecycleManager(context);
        ScheduledExecutorService executorService = ExecutorUtils.buildSingleThreadScheduledExecutorService(EXECUTOR_SERVICE);
        BackgroundManager backgroundManager = new BackgroundManager(executorService);
        return new SessionAnalyticsManager(new AnswersEventsHandler(kit, context, filesManagerProvider, metadataCollector, httpRequestFactory, executorService), lifecycleManager, backgroundManager, AnswersPreferenceManager.build(context), installedAt);
    }

    SessionAnalyticsManager(AnswersEventsHandler eventsHandler, ActivityLifecycleManager lifecycleManager, BackgroundManager backgroundManager, AnswersPreferenceManager preferenceManager, long installedAt) {
        this.eventsHandler = eventsHandler;
        this.lifecycleManager = lifecycleManager;
        this.backgroundManager = backgroundManager;
        this.preferenceManager = preferenceManager;
        this.installedAt = installedAt;
    }

    public void enable() {
        this.eventsHandler.enable();
        this.lifecycleManager.registerCallbacks(new AnswersLifecycleCallbacks(this, this.backgroundManager));
        this.backgroundManager.registerListener(this);
        if (isFirstLaunch(this.installedAt)) {
            onInstall();
            this.preferenceManager.setAnalyticsLaunched();
        }
    }

    public void disable() {
        this.lifecycleManager.resetCallbacks();
        this.eventsHandler.disable();
    }

    public void onCustom(CustomEvent event) {
        Fabric.getLogger().d(Answers.TAG, "Logged custom event: " + event);
        this.eventsHandler.processEventAsync(SessionEvent.customEventBuilder(event));
    }

    public void onPredefined(PredefinedEvent event) {
        Fabric.getLogger().d(Answers.TAG, "Logged predefined event: " + event);
        this.eventsHandler.processEventAsync(SessionEvent.predefinedEventBuilder(event));
    }

    public void onCrash(String sessionId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException(ON_CRASH_ERROR_MSG);
        }
        Fabric.getLogger().d(Answers.TAG, "Logged crash");
        this.eventsHandler.processEventSync(SessionEvent.crashEventBuilder(sessionId));
    }

    public void onError(String sessionId) {
    }

    public void onInstall() {
        Fabric.getLogger().d(Answers.TAG, "Logged install");
        this.eventsHandler.processEventAsyncAndFlush(SessionEvent.installEventBuilder());
    }

    public void onLifecycle(Activity activity, Type type) {
        Fabric.getLogger().d(Answers.TAG, "Logged lifecycle event: " + type.name());
        this.eventsHandler.processEventAsync(SessionEvent.lifecycleEventBuilder(type, activity));
    }

    public void onBackground() {
        Fabric.getLogger().d(Answers.TAG, "Flush events when app is backgrounded");
        this.eventsHandler.flushEvents();
    }

    public void setAnalyticsSettingsData(AnalyticsSettingsData analyticsSettingsData, String protocolAndHostOverride) {
        this.backgroundManager.setFlushOnBackground(analyticsSettingsData.flushOnBackground);
        this.eventsHandler.setAnalyticsSettingsData(analyticsSettingsData, protocolAndHostOverride);
    }

    boolean isFirstLaunch(long installedAt) {
        return !this.preferenceManager.hasAnalyticsLaunched() && installedRecently(installedAt);
    }

    boolean installedRecently(long installedAt) {
        return System.currentTimeMillis() - installedAt < FIRST_LAUNCH_INTERVAL_IN_MS;
    }
}
