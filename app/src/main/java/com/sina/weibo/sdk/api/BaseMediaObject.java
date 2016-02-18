package com.sina.weibo.sdk.api;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.sina.weibo.sdk.utils.LogUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class BaseMediaObject implements Parcelable {
    public static final int MEDIA_TYPE_CMD = 7;
    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_MUSIC = 3;
    public static final int MEDIA_TYPE_TEXT = 1;
    public static final int MEDIA_TYPE_VIDEO = 4;
    public static final int MEDIA_TYPE_VOICE = 6;
    public static final int MEDIA_TYPE_WEBPAGE = 5;
    public String actionUrl;
    public String description;
    public String identify;
    public String schema;
    public byte[] thumbData;
    public String title;

    public abstract int getObjType();

    protected abstract BaseMediaObject toExtraMediaObject(String str);

    protected abstract String toExtraMediaString();

    public BaseMediaObject(Parcel in) {
        this.actionUrl = in.readString();
        this.schema = in.readString();
        this.identify = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.thumbData = in.createByteArray();
    }

    public final void setThumbImage(Bitmap bitmap) {
        Exception e;
        Throwable th;
        ByteArrayOutputStream os = null;
        try {
            ByteArrayOutputStream os2 = new ByteArrayOutputStream();
            try {
                bitmap.compress(CompressFormat.JPEG, 85, os2);
                this.thumbData = os2.toByteArray();
                if (os2 != null) {
                    try {
                        os2.close();
                        os = os2;
                        return;
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                os = os2;
            } catch (Exception e3) {
                e = e3;
                os = os2;
                try {
                    e.printStackTrace();
                    LogUtil.e("Weibo.BaseMediaObject", "put thumb failed");
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e22) {
                            e22.printStackTrace();
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                os = os2;
                if (os != null) {
                    os.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            e.printStackTrace();
            LogUtil.e("Weibo.BaseMediaObject", "put thumb failed");
            if (os != null) {
                os.close();
            }
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actionUrl);
        dest.writeString(this.schema);
        dest.writeString(this.identify);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeByteArray(this.thumbData);
    }

    protected boolean checkArgs() {
        if (this.actionUrl == null || this.actionUrl.length() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) {
            LogUtil.e("Weibo.BaseMediaObject", "checkArgs fail, actionUrl is invalid");
            return false;
        } else if (this.identify == null || this.identify.length() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) {
            LogUtil.e("Weibo.BaseMediaObject", "checkArgs fail, identify is invalid");
            return false;
        } else if (this.thumbData == null || this.thumbData.length > LruDiskCache.DEFAULT_BUFFER_SIZE) {
            LogUtil.e("Weibo.BaseMediaObject", "checkArgs fail, thumbData is invalid,size is " + (this.thumbData != null ? this.thumbData.length : -1) + "! more then 32768.");
            return false;
        } else if (this.title == null || this.title.length() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) {
            LogUtil.e("Weibo.BaseMediaObject", "checkArgs fail, title is invalid");
            return false;
        } else if (this.description != null && this.description.length() <= AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT) {
            return true;
        } else {
            LogUtil.e("Weibo.BaseMediaObject", "checkArgs fail, description is invalid");
            return false;
        }
    }
}
