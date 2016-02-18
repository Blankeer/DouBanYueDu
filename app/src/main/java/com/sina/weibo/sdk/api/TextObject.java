package com.sina.weibo.sdk.api;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.sina.weibo.sdk.utils.LogUtil;
import io.realm.internal.Table;

public class TextObject extends BaseMediaObject {
    public static final Creator<TextObject> CREATOR;
    public String text;

    public TextObject(Parcel in) {
        this.text = in.readString();
    }

    static {
        CREATOR = new Creator<TextObject>() {
            public TextObject createFromParcel(Parcel in) {
                return new TextObject(in);
            }

            public TextObject[] newArray(int size) {
                return new TextObject[size];
            }
        };
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
    }

    public boolean checkArgs() {
        if (this.text != null && this.text.length() != 0 && this.text.length() <= AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT) {
            return true;
        }
        LogUtil.e("Weibo.TextObject", "checkArgs fail, text is invalid");
        return false;
    }

    public int getObjType() {
        return 1;
    }

    protected BaseMediaObject toExtraMediaObject(String str) {
        return this;
    }

    protected String toExtraMediaString() {
        return Table.STRING_DEFAULT_VALUE;
    }
}
