package com.sina.weibo.sdk.cmd;

import com.sina.weibo.sdk.exception.WeiboException;
import org.json.JSONException;
import org.json.JSONObject;

class BaseCmd {
    private long mNotificationDelay;
    private String mNotificationText;
    private String mNotificationTitle;

    public BaseCmd(String jsonStr) throws WeiboException {
        initFromJsonStr(jsonStr);
    }

    public BaseCmd(JSONObject jsonObj) {
        initFromJsonObj(jsonObj);
    }

    protected void initFromJsonStr(String jsonStr) throws WeiboException {
        JSONException e;
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                try {
                    initFromJsonObj(jsonObj);
                } catch (JSONException e2) {
                    e = e2;
                    JSONObject jSONObject = jsonObj;
                    e.printStackTrace();
                    throw new WeiboException("pase cmd has error !!!");
                }
            } catch (JSONException e3) {
                e = e3;
                e.printStackTrace();
                throw new WeiboException("pase cmd has error !!!");
            }
        }
    }

    protected void initFromJsonObj(JSONObject jsonObj) {
        this.mNotificationText = jsonObj.optString("notification_text");
        this.mNotificationTitle = jsonObj.optString("notification_title");
        this.mNotificationDelay = jsonObj.optLong("notification_delay");
    }

    public String getNotificationText() {
        return this.mNotificationText;
    }

    public void setNotificationText(String mNotificationText) {
        this.mNotificationText = mNotificationText;
    }

    public String getNotificationTitle() {
        return this.mNotificationTitle;
    }

    public void setNotificationTitle(String mNotificationTitle) {
        this.mNotificationTitle = mNotificationTitle;
    }

    public long getNotificationDelay() {
        return this.mNotificationDelay;
    }

    public void setNotificationDelay(long mNotificationDelay) {
        this.mNotificationDelay = mNotificationDelay;
    }
}
