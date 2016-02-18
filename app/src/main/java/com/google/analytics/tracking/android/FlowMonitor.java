package com.google.analytics.tracking.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.util.VisibleForTesting;
import com.j256.ormlite.stmt.query.SimpleComparison;
import java.util.HashMap;
import java.util.Map;

class FlowMonitor {
    static final String HITS_DISPATCHED = "hitsDispatched";
    static final String HITS_DISPATCHED_PARAM = "_s";
    static final String HITS_GENERATED = "hitsGenerated";
    static final String HITS_GENERATED_PARAM = "_g";
    static final String HITS_PAST_PROXY = "hitsPastProxy";
    static final String HITS_PAST_PROXY_PARAM = "_p";
    static final String HITS_STORED_IN_DB = "hitsStoredInDb";
    static final String HITS_STORED_IN_DB_PARAM = "_d";
    private static final int HIT_INTERVAL = 50;
    static final String LAST_QUERY_TIMESTAMP = "lastQueryTimestamp";
    static final String LAST_QUERY_TIMESTAMP_PARAM = "_ts";
    static final String MONITOR_TYPE = "flow";
    static final String MONITOR_TYPE_PARAM = "_t";
    static final String PREFERENCES_LABEL = "GoogleAnalytics";
    private static final long TIME_INTERVAL = 14400000;
    private static FlowMonitor sInstance;
    Editor mEditor;
    private int mHitsDispatched;
    private int mHitsGenerated;
    private int mHitsPastProxy;
    private int mHitsStoredInDb;
    private long mLastQueryTimeStamp;
    SharedPreferences mPersistentStore;

    public static FlowMonitor getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new FlowMonitor(context);
        }
        return sInstance;
    }

    @VisibleForTesting
    FlowMonitor(Context context) {
        this.mPersistentStore = context.getSharedPreferences(PREFERENCES_LABEL, 0);
        this.mEditor = this.mPersistentStore.edit();
        this.mHitsGenerated = this.mPersistentStore.getInt(HITS_GENERATED, 0);
        this.mHitsPastProxy = this.mPersistentStore.getInt(HITS_PAST_PROXY, 0);
        this.mHitsStoredInDb = this.mPersistentStore.getInt(HITS_STORED_IN_DB, 0);
        this.mHitsDispatched = this.mPersistentStore.getInt(HITS_DISPATCHED, 0);
        this.mLastQueryTimeStamp = this.mPersistentStore.getLong(LAST_QUERY_TIMESTAMP, 0);
        if (this.mLastQueryTimeStamp == 0) {
            this.mLastQueryTimeStamp = System.currentTimeMillis();
            this.mEditor.putLong(LAST_QUERY_TIMESTAMP, this.mLastQueryTimeStamp);
            this.mEditor.commit();
        }
    }

    public boolean shouldReport() {
        return this.mHitsGenerated >= HIT_INTERVAL && System.currentTimeMillis() - this.mLastQueryTimeStamp >= TIME_INTERVAL;
    }

    public Map<String, String> generateHit() {
        Map<String, String> hit = new HashMap();
        hit.put(MONITOR_TYPE_PARAM, MONITOR_TYPE);
        hit.put(LAST_QUERY_TIMESTAMP_PARAM, String.valueOf(this.mLastQueryTimeStamp));
        this.mLastQueryTimeStamp = System.currentTimeMillis();
        this.mEditor.putLong(LAST_QUERY_TIMESTAMP, this.mLastQueryTimeStamp);
        hit.put(HITS_GENERATED_PARAM, getAndResetHitsGeneratedParameter());
        return hit;
    }

    public void incrementHitsStoredInDb(int value) {
        this.mHitsStoredInDb += value;
        this.mEditor.putInt(HITS_STORED_IN_DB, value);
        this.mEditor.commit();
    }

    public String getAndResetHitsStoredInDb() {
        String result = String.valueOf(this.mHitsStoredInDb);
        this.mHitsStoredInDb = 0;
        this.mEditor.putInt(HITS_STORED_IN_DB, this.mHitsStoredInDb);
        this.mEditor.commit();
        return result;
    }

    public void incrementHitsDispatched(int value) {
        this.mHitsDispatched += value;
        this.mEditor.putInt(HITS_DISPATCHED, value);
        this.mEditor.commit();
    }

    public String buildAndResetHitsDispatched() {
        StringBuilder paramBuilder = new StringBuilder();
        paramBuilder.append(HITS_DISPATCHED_PARAM).append(SimpleComparison.EQUAL_TO_OPERATION).append(this.mHitsDispatched);
        this.mHitsDispatched = 0;
        this.mEditor.putInt(HITS_DISPATCHED, this.mHitsDispatched);
        this.mEditor.commit();
        return paramBuilder.toString();
    }

    public void incrementHitsPastProxy(int value) {
        this.mHitsPastProxy += value;
        this.mEditor.putInt(HITS_PAST_PROXY, value);
        this.mEditor.commit();
    }

    public String getAndResetHitsPastProxy() {
        String result = String.valueOf(this.mHitsPastProxy);
        this.mHitsPastProxy = 0;
        this.mEditor.putInt(HITS_PAST_PROXY, this.mHitsPastProxy);
        this.mEditor.commit();
        return result;
    }

    public void incrementHitsGenerated(int value) {
        this.mHitsGenerated += value;
        this.mEditor.putInt(HITS_GENERATED, value);
        this.mEditor.commit();
    }

    String getAndResetHitsGeneratedParameter() {
        String result = String.valueOf(this.mHitsGenerated);
        this.mHitsGenerated = 0;
        this.mEditor.putInt(HITS_GENERATED, 0);
        this.mEditor.commit();
        return result;
    }
}
