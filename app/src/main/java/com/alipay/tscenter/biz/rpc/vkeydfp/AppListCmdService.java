package com.alipay.tscenter.biz.rpc.vkeydfp;

import com.alipay.mobile.framework.service.annotation.a;
import com.alipay.tscenter.biz.rpc.vkeydfp.request.AppListCmdRequest;
import com.alipay.tscenter.biz.rpc.vkeydfp.result.AppListCmdResult;

public interface AppListCmdService {
    @a(a = "alipay.security.vkeyDFP.appListCmd.get")
    AppListCmdResult getAppListCmd(AppListCmdRequest appListCmdRequest);

    @a(a = "alipay.security.vkeyDFP.appListCmd.reGet")
    AppListCmdResult reGetAppListCmd(AppListCmdRequest appListCmdRequest);
}
