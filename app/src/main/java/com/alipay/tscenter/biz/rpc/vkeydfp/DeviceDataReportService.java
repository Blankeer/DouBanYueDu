package com.alipay.tscenter.biz.rpc.vkeydfp;

import com.alipay.mobile.framework.service.annotation.a;
import com.alipay.tscenter.biz.rpc.vkeydfp.request.DeviceDataReportRequest;
import com.alipay.tscenter.biz.rpc.vkeydfp.result.AppListResult;
import com.alipay.tscenter.biz.rpc.vkeydfp.result.DeviceDataReportResult;

public interface DeviceDataReportService {
    @a(a = "alipay.security.vkeyDFP.appList.get")
    AppListResult getAppList(String str);

    @a(a = "alipay.security.vkeyDFP.staticData.report")
    DeviceDataReportResult reportStaticData(DeviceDataReportRequest deviceDataReportRequest);
}
