package com.sina.weibo.sdk.api;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import com.sina.weibo.sdk.utils.LogUtil;
import io.realm.internal.Table;
import org.json.JSONException;
import org.json.JSONObject;

public class VoiceObject extends BaseMediaObject {
    public static final Creator<VoiceObject> CREATOR;
    public static final String EXTRA_KEY_DEFAULTTEXT = "extra_key_defaulttext";
    public String dataHdUrl;
    public String dataUrl;
    public String defaultText;
    public int duration;
    public String h5Url;

    static {
        CREATOR = new Creator<VoiceObject>() {
            public VoiceObject createFromParcel(Parcel in) {
                return new VoiceObject(in);
            }

            public VoiceObject[] newArray(int size) {
                return new VoiceObject[size];
            }
        };
    }

    public VoiceObject(Parcel in) {
        super(in);
        this.h5Url = in.readString();
        this.dataUrl = in.readString();
        this.dataHdUrl = in.readString();
        this.duration = in.readInt();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.h5Url);
        dest.writeString(this.dataUrl);
        dest.writeString(this.dataHdUrl);
        dest.writeInt(this.duration);
    }

    public boolean checkArgs() {
        if (!super.checkArgs()) {
            return false;
        }
        if (this.dataUrl != null && this.dataUrl.length() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) {
            LogUtil.e("Weibo.VoiceObject", "checkArgs fail, dataUrl is invalid");
            return false;
        } else if (this.dataHdUrl != null && this.dataHdUrl.length() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) {
            LogUtil.e("Weibo.VoiceObject", "checkArgs fail, dataHdUrl is invalid");
            return false;
        } else if (this.duration > 0) {
            return true;
        } else {
            LogUtil.e("Weibo.VoiceObject", "checkArgs fail, duration is invalid");
            return false;
        }
    }

    public int getObjType() {
        return 6;
    }

    protected BaseMediaObject toExtraMediaObject(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                this.defaultText = new JSONObject(str).optString(EXTRA_KEY_DEFAULTTEXT);
            } catch (JSONException e) {
            }
        }
        return this;
    }

    protected String toExtraMediaString() {
        try {
            JSONObject json = new JSONObject();
            if (!TextUtils.isEmpty(this.defaultText)) {
                json.put(EXTRA_KEY_DEFAULTTEXT, this.defaultText);
            }
            return json.toString();
        } catch (JSONException e) {
            return Table.STRING_DEFAULT_VALUE;
        }
    }
}
