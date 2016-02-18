package com.crashlytics.android.core;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.TransportMediator;

class ManifestUnityVersionProvider implements UnityVersionProvider {
    static final String FABRIC_UNITY_CRASHLYTICS_VERSION_KEY = "io.fabric.unity.crashlytics.version";
    private final Context context;
    private final String packageName;

    public ManifestUnityVersionProvider(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    public String getUnityVersion() {
        String unityVersion = null;
        try {
            Bundle metaData = this.context.getPackageManager().getApplicationInfo(this.packageName, TransportMediator.FLAG_KEY_MEDIA_NEXT).metaData;
            if (metaData != null) {
                unityVersion = metaData.getString(FABRIC_UNITY_CRASHLYTICS_VERSION_KEY);
            }
        } catch (Exception e) {
        }
        return unityVersion;
    }
}
