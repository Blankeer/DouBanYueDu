package com.crashlytics.android.beta;

import android.annotation.TargetApi;
import android.app.Activity;
import io.fabric.sdk.android.ActivityLifecycleManager;
import io.fabric.sdk.android.ActivityLifecycleManager.Callbacks;
import java.util.concurrent.ExecutorService;

@TargetApi(14)
class ActivityLifecycleCheckForUpdatesController extends AbstractCheckForUpdatesController {
    private final Callbacks callbacks;
    private final ExecutorService executorService;

    public ActivityLifecycleCheckForUpdatesController(ActivityLifecycleManager lifecycleManager, ExecutorService executorService) {
        this.callbacks = new Callbacks() {
            public void onActivityStarted(Activity activity) {
                if (ActivityLifecycleCheckForUpdatesController.this.signalExternallyReady()) {
                    ActivityLifecycleCheckForUpdatesController.this.executorService.submit(new Runnable() {
                        public void run() {
                            ActivityLifecycleCheckForUpdatesController.this.checkForUpdates();
                        }
                    });
                }
            }
        };
        this.executorService = executorService;
        lifecycleManager.registerCallbacks(this.callbacks);
    }

    public boolean isActivityLifecycleTriggered() {
        return true;
    }
}
