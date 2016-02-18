package com.alipay.security.mobile.module.a.c;

import android.content.Context;
import com.alipay.security.mobile.module.a.a;
import com.alipay.security.mobile.module.a.b.c;
import com.alipay.security.mobile.module.a.b.d;
import com.alipay.tscenter.biz.rpc.vkeydfp.request.DeviceDataReportRequest;
import com.alipay.tscenter.biz.rpc.vkeydfp.result.AppListResult;
import com.alipay.tscenter.biz.rpc.vkeydfp.result.DeviceDataReportResult;
import java.util.HashMap;

public final class b implements a {
    private static a a;
    private static a b;

    static {
        a = null;
        b = null;
    }

    public static a a(Context context) {
        a aVar = null;
        if (context == null) {
            return null;
        }
        if (a == null) {
            if (context != null) {
                aVar = com.alipay.security.mobile.module.a.b.a(context);
            }
            b = aVar;
            a = new b();
        }
        return a;
    }

    public final com.alipay.security.mobile.module.a.b.a a(String str, String str2, String str3, String str4) {
        AppListResult a = b.a(str, str2, str3, str4);
        if (a == null) {
            return null;
        }
        com.alipay.security.mobile.module.a.b.a aVar = new com.alipay.security.mobile.module.a.b.a(a.appListData, a.appListVer);
        aVar.c = a.success;
        aVar.d = a.resultCode;
        return aVar;
    }

    public final c a(d dVar) {
        DeviceDataReportRequest deviceDataReportRequest = new DeviceDataReportRequest();
        deviceDataReportRequest.os = com.alipay.security.mobile.module.commonutils.a.c(dVar.a);
        deviceDataReportRequest.apdid = com.alipay.security.mobile.module.commonutils.a.c(dVar.b);
        deviceDataReportRequest.pubApdid = com.alipay.security.mobile.module.commonutils.a.c(dVar.c);
        deviceDataReportRequest.priApdid = com.alipay.security.mobile.module.commonutils.a.c(dVar.d);
        deviceDataReportRequest.token = com.alipay.security.mobile.module.commonutils.a.c(dVar.e);
        deviceDataReportRequest.umidToken = com.alipay.security.mobile.module.commonutils.a.c(dVar.f);
        deviceDataReportRequest.version = com.alipay.security.mobile.module.commonutils.a.c(dVar.g);
        deviceDataReportRequest.lastTime = com.alipay.security.mobile.module.commonutils.a.c(dVar.h);
        deviceDataReportRequest.dataMap = dVar.i == null ? new HashMap() : dVar.i;
        DeviceDataReportResult a = b.a(deviceDataReportRequest);
        c cVar = new c();
        if (a == null) {
            return null;
        }
        cVar.c = a.success;
        cVar.d = a.resultCode;
        cVar.a = a.apdid;
        cVar.b = a.token;
        cVar.e = a.currentTime;
        cVar.f = a.version;
        cVar.g = a.vkeySwitch;
        cVar.h = a.bugTrackSwitch;
        cVar.i = a.appListVer;
        return cVar;
    }

    public final boolean a(String str) {
        return b.a(str);
    }
}
