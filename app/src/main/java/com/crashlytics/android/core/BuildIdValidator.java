package com.crashlytics.android.core;

import android.util.Log;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.CommonUtils;

class BuildIdValidator {
    private static final String MESSAGE = "This app relies on Crashlytics. Please sign up for access at https://fabric.io/sign_up,\ninstall an Android build tool and ask a team member to invite you to this app's organization.";
    private final String buildId;
    private final boolean requiringBuildId;

    public BuildIdValidator(String buildId, boolean requiringBuildId) {
        this.buildId = buildId;
        this.requiringBuildId = requiringBuildId;
    }

    public void validate(String apiKey, String appId) {
        if (CommonUtils.isNullOrEmpty(this.buildId) && this.requiringBuildId) {
            String message = getMessage(apiKey, appId);
            Log.e(CrashlyticsCore.TAG, ".");
            Log.e(CrashlyticsCore.TAG, ".     |  | ");
            Log.e(CrashlyticsCore.TAG, ".     |  |");
            Log.e(CrashlyticsCore.TAG, ".     |  |");
            Log.e(CrashlyticsCore.TAG, ".   \\ |  | /");
            Log.e(CrashlyticsCore.TAG, ".    \\    /");
            Log.e(CrashlyticsCore.TAG, ".     \\  /");
            Log.e(CrashlyticsCore.TAG, ".      \\/");
            Log.e(CrashlyticsCore.TAG, ".");
            Log.e(CrashlyticsCore.TAG, message);
            Log.e(CrashlyticsCore.TAG, ".");
            Log.e(CrashlyticsCore.TAG, ".      /\\");
            Log.e(CrashlyticsCore.TAG, ".     /  \\");
            Log.e(CrashlyticsCore.TAG, ".    /    \\");
            Log.e(CrashlyticsCore.TAG, ".   / |  | \\");
            Log.e(CrashlyticsCore.TAG, ".     |  |");
            Log.e(CrashlyticsCore.TAG, ".     |  |");
            Log.e(CrashlyticsCore.TAG, ".     |  |");
            Log.e(CrashlyticsCore.TAG, ".");
            throw new CrashlyticsMissingDependencyException(message);
        } else if (!this.requiringBuildId) {
            Fabric.getLogger().d(CrashlyticsCore.TAG, "Configured not to require a build ID.");
        }
    }

    protected String getMessage(String apiKey, String appId) {
        return MESSAGE;
    }
}
