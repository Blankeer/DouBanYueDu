package com.google.analytics.tracking.android;

import android.text.TextUtils;
import com.google.analytics.tracking.android.GAUsage.Field;
import com.google.android.gms.common.util.VisibleForTesting;
import com.tencent.connect.common.Constants;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Tracker {
    private final AppFieldsDefaultProvider mAppFieldsDefaultProvider;
    private final ClientIdDefaultProvider mClientIdDefaultProvider;
    private final TrackerHandler mHandler;
    private final String mName;
    private final Map<String, String> mParams;
    private RateLimiter mRateLimiter;
    private final ScreenResolutionDefaultProvider mScreenResolutionDefaultProvider;

    Tracker(String name, String trackingId, TrackerHandler handler) {
        this(name, trackingId, handler, ClientIdDefaultProvider.getProvider(), ScreenResolutionDefaultProvider.getProvider(), AppFieldsDefaultProvider.getProvider(), new SendHitRateLimiter());
    }

    @VisibleForTesting
    Tracker(String name, String trackingId, TrackerHandler handler, ClientIdDefaultProvider clientIdDefaultProvider, ScreenResolutionDefaultProvider screenResolutionDefaultProvider, AppFieldsDefaultProvider appFieldsDefaultProvider, RateLimiter rateLimiter) {
        this.mParams = new HashMap();
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Tracker name cannot be empty.");
        }
        this.mName = name;
        this.mHandler = handler;
        this.mParams.put(Fields.TRACKING_ID, trackingId);
        this.mParams.put(Fields.USE_SECURE, Constants.VIA_TO_TYPE_QQ_GROUP);
        this.mClientIdDefaultProvider = clientIdDefaultProvider;
        this.mScreenResolutionDefaultProvider = screenResolutionDefaultProvider;
        this.mAppFieldsDefaultProvider = appFieldsDefaultProvider;
        this.mRateLimiter = rateLimiter;
    }

    public String getName() {
        GAUsage.getInstance().setUsage(Field.GET_TRACKER_NAME);
        return this.mName;
    }

    @VisibleForTesting
    RateLimiter getRateLimiter() {
        return this.mRateLimiter;
    }

    public void send(Map<String, String> params) {
        GAUsage.getInstance().setUsage(Field.SEND);
        Map<String, String> paramsToSend = new HashMap();
        paramsToSend.putAll(this.mParams);
        if (params != null) {
            paramsToSend.putAll(params);
        }
        if (TextUtils.isEmpty((CharSequence) paramsToSend.get(Fields.TRACKING_ID))) {
            Log.w(String.format("Missing tracking id (%s) parameter.", new Object[]{Fields.TRACKING_ID}));
        }
        String hitType = (String) paramsToSend.get(Fields.HIT_TYPE);
        if (TextUtils.isEmpty(hitType)) {
            Log.w(String.format("Missing hit type (%s) parameter.", new Object[]{Fields.HIT_TYPE}));
            hitType = Table.STRING_DEFAULT_VALUE;
        }
        if (hitType.equals(HitTypes.TRANSACTION) || hitType.equals(HitTypes.ITEM) || this.mRateLimiter.tokenAvailable()) {
            this.mHandler.sendHit(paramsToSend);
        } else {
            Log.w("Too many hits sent too quickly, rate limiting invoked.");
        }
    }

    public String get(String key) {
        GAUsage.getInstance().setUsage(Field.GET);
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        if (this.mParams.containsKey(key)) {
            return (String) this.mParams.get(key);
        }
        if (key.equals(Fields.LANGUAGE)) {
            return Utils.getLanguage(Locale.getDefault());
        }
        if (this.mClientIdDefaultProvider != null && this.mClientIdDefaultProvider.providesField(key)) {
            return this.mClientIdDefaultProvider.getValue(key);
        }
        if (this.mScreenResolutionDefaultProvider != null && this.mScreenResolutionDefaultProvider.providesField(key)) {
            return this.mScreenResolutionDefaultProvider.getValue(key);
        }
        if (this.mAppFieldsDefaultProvider == null || !this.mAppFieldsDefaultProvider.providesField(key)) {
            return null;
        }
        return this.mAppFieldsDefaultProvider.getValue(key);
    }

    public void set(String key, String value) {
        GAUsage.getInstance().setUsage(Field.SET);
        if (value == null) {
            this.mParams.remove(key);
        } else {
            this.mParams.put(key, value);
        }
    }
}
