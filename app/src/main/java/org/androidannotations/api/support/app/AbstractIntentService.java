package org.androidannotations.api.support.app;

import android.app.IntentService;
import android.content.Intent;

public abstract class AbstractIntentService extends IntentService {
    public AbstractIntentService(String name) {
        super(name);
    }

    protected void onHandleIntent(Intent intent) {
    }
}
