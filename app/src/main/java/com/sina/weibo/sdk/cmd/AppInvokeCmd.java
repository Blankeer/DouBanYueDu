package com.sina.weibo.sdk.cmd;

import com.douban.book.reader.fragment.share.ShareUrlEditFragment_;
import com.sina.weibo.sdk.exception.WeiboException;
import org.json.JSONObject;

class AppInvokeCmd extends BaseCmd {
    private String appPackage;
    private String scheme;
    private String url;

    public AppInvokeCmd(String jsonStr) throws WeiboException {
        super(jsonStr);
    }

    public AppInvokeCmd(JSONObject JsonObj) {
        super(JsonObj);
    }

    public void initFromJsonObj(JSONObject jsonObj) {
        super.initFromJsonObj(jsonObj);
        this.appPackage = jsonObj.optString("package");
        this.scheme = jsonObj.optString("scheme");
        this.url = jsonObj.optString(ShareUrlEditFragment_.URL_ARG);
    }

    public String getAppPackage() {
        return this.appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
