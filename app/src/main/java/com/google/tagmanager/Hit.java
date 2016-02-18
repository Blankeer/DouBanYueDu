package com.google.tagmanager;

import android.text.TextUtils;

class Hit {
    private final long mHitFirstDispatchTime;
    private final long mHitId;
    private final long mHitTime;
    private String mHitUrl;

    long getHitId() {
        return this.mHitId;
    }

    long getHitTime() {
        return this.mHitTime;
    }

    long getHitFirstDispatchTime() {
        return this.mHitFirstDispatchTime;
    }

    Hit(long hitId, long hitTime, long firstDispatchTime) {
        this.mHitId = hitId;
        this.mHitTime = hitTime;
        this.mHitFirstDispatchTime = firstDispatchTime;
    }

    Hit(long hitId, long hitTime) {
        this.mHitId = hitId;
        this.mHitTime = hitTime;
        this.mHitFirstDispatchTime = 0;
    }

    String getHitUrl() {
        return this.mHitUrl;
    }

    void setHitUrl(String hitUrl) {
        if (hitUrl != null && !TextUtils.isEmpty(hitUrl.trim())) {
            this.mHitUrl = hitUrl;
        }
    }
}
