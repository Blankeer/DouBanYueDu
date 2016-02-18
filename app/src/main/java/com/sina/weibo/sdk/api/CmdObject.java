package com.sina.weibo.sdk.api;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import io.realm.internal.Table;

public class CmdObject extends BaseMediaObject {
    public static final String CMD_HOME = "home";
    public static final Creator<CmdObject> CREATOR;
    public String cmd;

    static {
        CREATOR = new Creator<CmdObject>() {
            public CmdObject createFromParcel(Parcel in) {
                return new CmdObject(in);
            }

            public CmdObject[] newArray(int size) {
                return new CmdObject[size];
            }
        };
    }

    public CmdObject(Parcel in) {
        this.cmd = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cmd);
    }

    public boolean checkArgs() {
        if (this.cmd == null || this.cmd.length() == 0 || this.cmd.length() > AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT) {
            return false;
        }
        return true;
    }

    public int getObjType() {
        return 7;
    }

    protected BaseMediaObject toExtraMediaObject(String str) {
        return this;
    }

    protected String toExtraMediaString() {
        return Table.STRING_DEFAULT_VALUE;
    }
}
