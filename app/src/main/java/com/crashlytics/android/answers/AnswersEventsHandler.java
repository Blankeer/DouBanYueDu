package com.crashlytics.android.answers;

import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import io.fabric.sdk.android.services.events.EventsStorageListener;
import io.fabric.sdk.android.services.network.HttpRequestFactory;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;
import java.util.concurrent.ScheduledExecutorService;

class AnswersEventsHandler implements EventsStorageListener {
    private final Context context;
    final ScheduledExecutorService executor;
    private final AnswersFilesManagerProvider filesManagerProvider;
    private final Kit kit;
    private final SessionMetadataCollector metadataCollector;
    private final HttpRequestFactory requestFactory;
    SessionAnalyticsManagerStrategy strategy;

    /* renamed from: com.crashlytics.android.answers.AnswersEventsHandler.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ AnalyticsSettingsData val$analyticsSettingsData;
        final /* synthetic */ String val$protocolAndHostOverride;

        AnonymousClass1(AnalyticsSettingsData analyticsSettingsData, String str) {
            this.val$analyticsSettingsData = analyticsSettingsData;
            this.val$protocolAndHostOverride = str;
        }

        public void run() {
            try {
                AnswersEventsHandler.this.strategy.setAnalyticsSettingsData(this.val$analyticsSettingsData, this.val$protocolAndHostOverride);
            } catch (Exception e) {
                Fabric.getLogger().e(Answers.TAG, "Failed to set analytics settings data", e);
            }
        }
    }

    /* renamed from: com.crashlytics.android.answers.AnswersEventsHandler.6 */
    class AnonymousClass6 implements Runnable {
        final /* synthetic */ Builder val$eventBuilder;
        final /* synthetic */ boolean val$flush;

        AnonymousClass6(Builder builder, boolean z) {
            this.val$eventBuilder = builder;
            this.val$flush = z;
        }

        public void run() {
            try {
                AnswersEventsHandler.this.strategy.processEvent(this.val$eventBuilder);
                if (this.val$flush) {
                    AnswersEventsHandler.this.strategy.rollFileOver();
                }
            } catch (Exception e) {
                Fabric.getLogger().e(Answers.TAG, "Failed to process event", e);
            }
        }
    }

    public AnswersEventsHandler(Kit kit, Context context, AnswersFilesManagerProvider filesManagerProvider, SessionMetadataCollector metadataCollector, HttpRequestFactory requestFactory, ScheduledExecutorService executor) {
        this.strategy = new DisabledSessionAnalyticsManagerStrategy();
        this.kit = kit;
        this.context = context;
        this.filesManagerProvider = filesManagerProvider;
        this.metadataCollector = metadataCollector;
        this.requestFactory = requestFactory;
        this.executor = executor;
    }

    public void processEventAsync(Builder eventBuilder) {
        processEvent(eventBuilder, false, false);
    }

    public void processEventAsyncAndFlush(Builder eventBuilder) {
        processEvent(eventBuilder, false, true);
    }

    public void processEventSync(Builder eventBuilder) {
        processEvent(eventBuilder, true, false);
    }

    public void setAnalyticsSettingsData(AnalyticsSettingsData analyticsSettingsData, String protocolAndHostOverride) {
        executeAsync(new AnonymousClass1(analyticsSettingsData, protocolAndHostOverride));
    }

    public void disable() {
        executeAsync(new Runnable() {
            public void run() {
                try {
                    SessionAnalyticsManagerStrategy prevStrategy = AnswersEventsHandler.this.strategy;
                    AnswersEventsHandler.this.strategy = new DisabledSessionAnalyticsManagerStrategy();
                    prevStrategy.deleteAllEvents();
                } catch (Exception e) {
                    Fabric.getLogger().e(Answers.TAG, "Failed to disable events", e);
                }
            }
        });
    }

    public void onRollOver(String rolledOverFile) {
        executeAsync(new Runnable() {
            public void run() {
                try {
                    AnswersEventsHandler.this.strategy.sendEvents();
                } catch (Exception e) {
                    Fabric.getLogger().e(Answers.TAG, "Failed to send events files", e);
                }
            }
        });
    }

    public void enable() {
        executeAsync(new Runnable() {
            public void run() {
                try {
                    SessionEventMetadata metadata = AnswersEventsHandler.this.metadataCollector.getMetadata();
                    SessionAnalyticsFilesManager filesManager = AnswersEventsHandler.this.filesManagerProvider.getAnalyticsFilesManager();
                    filesManager.registerRollOverListener(AnswersEventsHandler.this);
                    AnswersEventsHandler.this.strategy = new EnabledSessionAnalyticsManagerStrategy(AnswersEventsHandler.this.kit, AnswersEventsHandler.this.context, AnswersEventsHandler.this.executor, filesManager, AnswersEventsHandler.this.requestFactory, metadata);
                } catch (Exception e) {
                    Fabric.getLogger().e(Answers.TAG, "Failed to enable events", e);
                }
            }
        });
    }

    public void flushEvents() {
        executeAsync(new Runnable() {
            public void run() {
                try {
                    AnswersEventsHandler.this.strategy.rollFileOver();
                } catch (Exception e) {
                    Fabric.getLogger().e(Answers.TAG, "Failed to flush events", e);
                }
            }
        });
    }

    void processEvent(Builder eventBuilder, boolean sync, boolean flush) {
        Runnable runnable = new AnonymousClass6(eventBuilder, flush);
        if (sync) {
            executeSync(runnable);
        } else {
            executeAsync(runnable);
        }
    }

    private void executeSync(Runnable runnable) {
        try {
            this.executor.submit(runnable).get();
        } catch (Exception e) {
            Fabric.getLogger().e(Answers.TAG, "Failed to run events task", e);
        }
    }

    private void executeAsync(Runnable runnable) {
        try {
            this.executor.submit(runnable);
        } catch (Exception e) {
            Fabric.getLogger().e(Answers.TAG, "Failed to submit events task", e);
        }
    }
}
