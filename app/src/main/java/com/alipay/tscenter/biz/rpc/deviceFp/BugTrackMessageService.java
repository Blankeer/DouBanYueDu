package com.alipay.tscenter.biz.rpc.deviceFp;

import com.alipay.mobile.framework.service.annotation.a;

public interface BugTrackMessageService {
    @a(a = "alipay.security.errorLog.collect")
    String logCollect(String str);
}
