package com.sina.weibo.sdk.cmd;

import android.text.TextUtils;
import com.sina.weibo.sdk.exception.WeiboException;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

class AppInstallCmd extends BaseCmd {
    private List<String> appPackages;
    private String appSign;
    private long appVersion;
    private String downloadUrl;

    public AppInstallCmd(String jsonStr) throws WeiboException {
        super(jsonStr);
    }

    public AppInstallCmd(JSONObject JsonObj) {
        super(JsonObj);
    }

    public void initFromJsonObj(JSONObject jsonObj) {
        super.initFromJsonObj(jsonObj);
        this.downloadUrl = jsonObj.optString("download_url");
        String appPackage = jsonObj.optString("app_package");
        if (!TextUtils.isEmpty(appPackage)) {
            this.appPackages = Arrays.asList(appPackage.split("\\|"));
        }
        this.appSign = jsonObj.optString("app_sign");
        this.appVersion = jsonObj.optLong("app_version");
    }

    public long getAppVersion() {
        return this.appVersion;
    }

    public void setAppVersion(long appVersion) {
        this.appVersion = appVersion;
    }

    public List<String> getAppPackage() {
        return this.appPackages;
    }

    public void setAppPackage(List<String> appPackage) {
        this.appPackages = appPackage;
    }

    public String getAppSign() {
        return this.appSign;
    }

    public void setAppSign(String appSign) {
        this.appSign = appSign;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
