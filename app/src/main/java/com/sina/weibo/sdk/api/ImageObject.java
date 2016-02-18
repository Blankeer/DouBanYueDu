package com.sina.weibo.sdk.api;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.sina.weibo.sdk.utils.LogUtil;
import io.realm.internal.Table;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageObject extends BaseMediaObject {
    public static final Creator<ImageObject> CREATOR;
    private static final int DATA_SIZE = 2097152;
    public byte[] imageData;
    public String imagePath;

    static {
        CREATOR = new Creator<ImageObject>() {
            public ImageObject createFromParcel(Parcel in) {
                return new ImageObject(in);
            }

            public ImageObject[] newArray(int size) {
                return new ImageObject[size];
            }
        };
    }

    public ImageObject(Parcel in) {
        this.imageData = in.createByteArray();
        this.imagePath = in.readString();
    }

    public final void setImageObject(Bitmap bitmap) {
        Exception e;
        Throwable th;
        ByteArrayOutputStream os = null;
        try {
            ByteArrayOutputStream os2 = new ByteArrayOutputStream();
            try {
                bitmap.compress(CompressFormat.JPEG, 85, os2);
                this.imageData = os2.toByteArray();
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
                    LogUtil.e("Weibo.ImageObject", "put thumb failed");
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
            LogUtil.e("Weibo.ImageObject", "put thumb failed");
            if (os != null) {
                os.close();
            }
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.imageData);
        dest.writeString(this.imagePath);
    }

    public boolean checkArgs() {
        if (this.imageData == null && this.imagePath == null) {
            LogUtil.e("Weibo.ImageObject", "imageData and imagePath are null");
            return false;
        } else if (this.imageData != null && this.imageData.length > DATA_SIZE) {
            LogUtil.e("Weibo.ImageObject", "imageData is too large");
            return false;
        } else if (this.imagePath == null || this.imagePath.length() <= AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) {
            if (this.imagePath != null) {
                File file = new File(this.imagePath);
                try {
                    if (!file.exists() || file.length() == 0 || file.length() > 10485760) {
                        LogUtil.e("Weibo.ImageObject", "checkArgs fail, image content is too large or not exists");
                        return false;
                    }
                } catch (SecurityException e) {
                    LogUtil.e("Weibo.ImageObject", "checkArgs fail, image content is too large or not exists");
                    return false;
                }
            }
            return true;
        } else {
            LogUtil.e("Weibo.ImageObject", "imagePath is too length");
            return false;
        }
    }

    public int getObjType() {
        return 2;
    }

    protected BaseMediaObject toExtraMediaObject(String str) {
        return this;
    }

    protected String toExtraMediaString() {
        return Table.STRING_DEFAULT_VALUE;
    }
}
