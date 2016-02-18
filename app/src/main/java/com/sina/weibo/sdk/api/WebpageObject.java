package com.sina.weibo.sdk.api;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import io.realm.internal.Table;
import org.json.JSONException;
import org.json.JSONObject;

public class WebpageObject extends BaseMediaObject {
    public static final Creator<WebpageObject> CREATOR;
    public static final String EXTRA_KEY_DEFAULTTEXT = "extra_key_defaulttext";
    public String defaultText;

    static {
        CREATOR = new Creator<WebpageObject>() {
            public WebpageObject createFromParcel(Parcel in) {
                return new WebpageObject(in);
            }

            public WebpageObject[] newArray(int size) {
                return new WebpageObject[size];
            }
        };
    }

    public WebpageObject(Parcel in) {
        super(in);
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public boolean checkArgs() {
        if (super.checkArgs()) {
            return true;
        }
        return false;
    }

    public int getObjType() {
        return 5;
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
