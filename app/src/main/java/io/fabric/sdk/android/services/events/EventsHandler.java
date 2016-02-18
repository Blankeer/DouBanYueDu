package io.fabric.sdk.android.services.events;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;
import java.util.concurrent.ScheduledExecutorService;

public abstract class EventsHandler<T> implements EventsStorageListener {
    protected final Context context;
    protected final ScheduledExecutorService executor;
    protected EventsStrategy<T> strategy;

    /* renamed from: io.fabric.sdk.android.services.events.EventsHandler.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Object val$event;
        final /* synthetic */ boolean val$sendImmediately;

        AnonymousClass1(Object obj, boolean z) {
            this.val$event = obj;
            this.val$sendImmediately = z;
        }

        public void run() {
            try {
                EventsHandler.this.strategy.recordEvent(this.val$event);
                if (this.val$sendImmediately) {
                    EventsHandler.this.strategy.rollFileOver();
                }
            } catch (Exception e) {
                CommonUtils.logControlledError(EventsHandler.this.context, "Failed to record event.", e);
            }
        }
    }

    /* renamed from: io.fabric.sdk.android.services.events.EventsHandler.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Object val$event;

        AnonymousClass2(Object obj) {
            this.val$event = obj;
        }

        public void run() {
            try {
                EventsHandler.this.strategy.recordEvent(this.val$event);
            } catch (Exception e) {
                CommonUtils.logControlledError(EventsHandler.this.context, "Crashlytics failed to record event", e);
            }
        }
    }

    protected abstract EventsStrategy<T> getDisabledEventsStrategy();

    public EventsHandler(Context context, EventsStrategy<T> strategy, EventsFilesManager filesManager, ScheduledExecutorService executor) {
        this.context = context.getApplicationContext();
        this.executor = executor;
        this.strategy = strategy;
        filesManager.registerRollOverListener(this);
    }

    public void recordEventAsync(T event, boolean sendImmediately) {
        executeAsync(new AnonymousClass1(event, sendImmediately));
    }

    public void recordEventSync(T event) {
        executeSync(new AnonymousClass2(event));
    }

    public void onRollOver(String rolledOverFile) {
        executeAsync(new Runnable() {
            public void run() {
                try {
                    EventsHandler.this.strategy.sendEvents();
                } catch (Exception e) {
                    CommonUtils.logControlledError(EventsHandler.this.context, "Failed to send events files.", e);
                }
            }
        });
    }

    public void disable() {
        executeAsync(new Runnable() {
            public void run() {
                try {
                    EventsStrategy<T> prevStrategy = EventsHandler.this.strategy;
                    EventsHandler.this.strategy = EventsHandler.this.getDisabledEventsStrategy();
                    prevStrategy.deleteAllEvents();
                } catch (Exception e) {
                    CommonUtils.logControlledError(EventsHandler.this.context, "Failed to disable events.", e);
                }
            }
        });
    }

    protected void executeSync(Runnable runnable) {
        try {
            this.executor.submit(runnable).get();
        } catch (Exception e) {
            CommonUtils.logControlledError(this.context, "Failed to run events task", e);
        }
    }

    protected void executeAsync(Runnable runnable) {
        try {
            this.executor.submit(runnable);
        } catch (Exception e) {
            CommonUtils.logControlledError(this.context, "Failed to submit events task", e);
        }
    }
}
