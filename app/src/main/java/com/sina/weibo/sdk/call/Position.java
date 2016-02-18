package com.sina.weibo.sdk.call;

import com.tencent.connect.common.Constants;

public class Position {
    private float mLatitude;
    private float mLongitude;
    private boolean mOffset;

    public Position(float longitude, float latitude) {
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mOffset = true;
    }

    public Position(float longitude, float latitude, boolean offset) {
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mOffset = offset;
    }

    public float getLongitude() {
        return this.mLongitude;
    }

    public float getLatitude() {
        return this.mLatitude;
    }

    public boolean isOffset() {
        return this.mOffset;
    }

    public String getStrLongitude() {
        return String.valueOf(this.mLongitude);
    }

    public String getStrLatitude() {
        return String.valueOf(this.mLatitude);
    }

    public String getStrOffset() {
        return this.mOffset ? Constants.VIA_TO_TYPE_QQ_GROUP : Constants.VIA_RESULT_SUCCESS;
    }

    boolean checkValid() {
        if (Float.isNaN(this.mLongitude) || this.mLongitude < -180.0f || this.mLongitude > 180.0f || Float.isNaN(this.mLatitude) || this.mLatitude < -180.0f || this.mLatitude > 180.0f) {
            return false;
        }
        return true;
    }
}
