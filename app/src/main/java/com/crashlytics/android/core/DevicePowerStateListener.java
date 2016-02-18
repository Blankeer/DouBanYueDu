package com.crashlytics.android.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.concurrent.atomic.AtomicBoolean;

class DevicePowerStateListener {
    private static final IntentFilter FILTER_BATTERY_CHANGED;
    private static final IntentFilter FILTER_POWER_CONNECTED;
    private static final IntentFilter FILTER_POWER_DISCONNECTED;
    private final Context context;
    private boolean isPowerConnected;
    private final BroadcastReceiver powerConnectedReceiver;
    private final BroadcastReceiver powerDisconnectedReceiver;
    private final AtomicBoolean receiversRegistered;

    static {
        FILTER_BATTERY_CHANGED = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        FILTER_POWER_CONNECTED = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
        FILTER_POWER_DISCONNECTED = new IntentFilter("android.intent.action.ACTION_POWER_DISCONNECTED");
    }

    public DevicePowerStateListener(Context context) {
        int status = -1;
        this.context = context;
        Intent statusIntent = context.registerReceiver(null, FILTER_BATTERY_CHANGED);
        if (statusIntent != null) {
            status = statusIntent.getIntExtra(SettingsJsonConstants.APP_STATUS_KEY, -1);
        }
        boolean z = status == 2 || status == 5;
        this.isPowerConnected = z;
        this.powerConnectedReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                DevicePowerStateListener.this.isPowerConnected = true;
            }
        };
        this.powerDisconnectedReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                DevicePowerStateListener.this.isPowerConnected = false;
            }
        };
        context.registerReceiver(this.powerConnectedReceiver, FILTER_POWER_CONNECTED);
        context.registerReceiver(this.powerDisconnectedReceiver, FILTER_POWER_DISCONNECTED);
        this.receiversRegistered = new AtomicBoolean(true);
    }

    public boolean isPowerConnected() {
        return this.isPowerConnected;
    }

    public void dispose() {
        if (this.receiversRegistered.getAndSet(false)) {
            this.context.unregisterReceiver(this.powerConnectedReceiver);
            this.context.unregisterReceiver(this.powerDisconnectedReceiver);
        }
    }
}
