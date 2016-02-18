package org.androidannotations.api.builder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public abstract class ServiceIntentBuilder<I extends ServiceIntentBuilder<I>> extends IntentBuilder<I> {
    public ServiceIntentBuilder(Context context, Class<?> clazz) {
        super(context, (Class) clazz);
    }

    public ServiceIntentBuilder(Context context, Intent intent) {
        super(context, intent);
    }

    public ComponentName start() {
        return this.context.startService(this.intent);
    }

    public boolean stop() {
        return this.context.stopService(this.intent);
    }
}
