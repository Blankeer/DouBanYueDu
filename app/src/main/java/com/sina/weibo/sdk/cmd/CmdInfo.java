package com.sina.weibo.sdk.cmd;

import com.douban.amonsul.StatConstant;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.LogUtil;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class CmdInfo {
    private static final String TAG;
    private int frequency;
    private List<AppInstallCmd> mInstallCmds;
    private List<AppInvokeCmd> mInvokeCmds;

    static {
        TAG = BaseCmd.class.getName();
    }

    public CmdInfo(String jsonStr) throws WeiboException {
        initFromJsonStr(jsonStr);
    }

    private void initFromJsonStr(String jsonStr) throws WeiboException {
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                if (jsonObj.has(StatConstant.STAT_EVENT_ID_ERROR) || jsonObj.has("error_code")) {
                    LogUtil.w(TAG, "load cmd api has error !!!");
                    throw new WeiboException("load cmd api has error !!!");
                }
                JSONObject cmdJsonObj = jsonObj.optJSONObject("cmd");
                if (cmdJsonObj != null) {
                    int i;
                    this.frequency = cmdJsonObj.optInt("frequency");
                    JSONArray installCmdArray = cmdJsonObj.optJSONArray("app_install");
                    if (installCmdArray != null) {
                        this.mInstallCmds = new ArrayList();
                        for (i = 0; i < installCmdArray.length(); i++) {
                            this.mInstallCmds.add(new AppInstallCmd(installCmdArray.getJSONObject(i)));
                        }
                    }
                    JSONArray invokeCmdArray = cmdJsonObj.optJSONArray("app_invoke");
                    if (invokeCmdArray != null) {
                        this.mInvokeCmds = new ArrayList();
                        for (i = 0; i < invokeCmdArray.length(); i++) {
                            this.mInvokeCmds.add(new AppInvokeCmd(invokeCmdArray.getJSONObject(i)));
                        }
                    }
                }
            } catch (JSONException e) {
                LogUtil.d(TAG, "parse NotificationInfo error: " + e.getMessage());
            }
        }
    }

    public List<AppInstallCmd> getInstallCmds() {
        return this.mInstallCmds;
    }

    public void setInstallCmds(List<AppInstallCmd> mInstallCmds) {
        this.mInstallCmds = mInstallCmds;
    }

    public List<AppInvokeCmd> getInvokeCmds() {
        return this.mInvokeCmds;
    }

    public void setInvokeCmds(List<AppInvokeCmd> mInvokeCmds) {
        this.mInvokeCmds = mInvokeCmds;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
