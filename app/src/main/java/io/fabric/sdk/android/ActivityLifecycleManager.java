package io.fabric.sdk.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import java.util.HashSet;
import java.util.Set;

public class ActivityLifecycleManager {
    private final Application application;
    private ActivityLifecycleCallbacksWrapper callbacksWrapper;

    private static class ActivityLifecycleCallbacksWrapper {
        private final Application application;
        private final Set<ActivityLifecycleCallbacks> registeredCallbacks;

        /* renamed from: io.fabric.sdk.android.ActivityLifecycleManager.ActivityLifecycleCallbacksWrapper.1 */
        class AnonymousClass1 implements ActivityLifecycleCallbacks {
            final /* synthetic */ Callbacks val$callbacks;

            AnonymousClass1(Callbacks callbacks) {
                this.val$callbacks = callbacks;
            }

            public void onActivityCreated(Activity activity, Bundle bundle) {
                this.val$callbacks.onActivityCreated(activity, bundle);
            }

            public void onActivityStarted(Activity activity) {
                this.val$callbacks.onActivityStarted(activity);
            }

            public void onActivityResumed(Activity activity) {
                this.val$callbacks.onActivityResumed(activity);
            }

            public void onActivityPaused(Activity activity) {
                this.val$callbacks.onActivityPaused(activity);
            }

            public void onActivityStopped(Activity activity) {
                this.val$callbacks.onActivityStopped(activity);
            }

            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                this.val$callbacks.onActivitySaveInstanceState(activity, bundle);
            }

            public void onActivityDestroyed(Activity activity) {
                this.val$callbacks.onActivityDestroyed(activity);
            }
        }

        ActivityLifecycleCallbacksWrapper(Application application) {
            this.registeredCallbacks = new HashSet();
            this.application = application;
        }

        @TargetApi(14)
        private void clearCallbacks() {
            for (ActivityLifecycleCallbacks callback : this.registeredCallbacks) {
                this.application.unregisterActivityLifecycleCallbacks(callback);
            }
        }

        @TargetApi(14)
        private boolean registerLifecycleCallbacks(Callbacks callbacks) {
            if (this.application == null) {
                return false;
            }
            ActivityLifecycleCallbacks callbackWrapper = new AnonymousClass1(callbacks);
            this.application.registerActivityLifecycleCallbacks(callbackWrapper);
            this.registeredCallbacks.add(callbackWrapper);
            return true;
        }
    }

    public static abstract class Callbacks {
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityResumed(Activity activity) {
        }

        public void onActivityPaused(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        public void onActivityDestroyed(Activity activity) {
        }
    }

    public ActivityLifecycleManager(Context context) {
        this.application = (Application) context.getApplicationContext();
        if (VERSION.SDK_INT >= 14) {
            this.callbacksWrapper = new ActivityLifecycleCallbacksWrapper(this.application);
        }
    }

    public boolean registerCallbacks(Callbacks callbacks) {
        return this.callbacksWrapper != null && this.callbacksWrapper.registerLifecycleCallbacks(callbacks);
    }

    public void resetCallbacks() {
        if (this.callbacksWrapper != null) {
            this.callbacksWrapper.clearCallbacks();
        }
    }
}
