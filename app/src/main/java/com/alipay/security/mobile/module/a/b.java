package com.alipay.security.mobile.module.a;

import android.content.Context;
import com.alipay.android.phone.mrpc.core.ad;
import com.alipay.android.phone.mrpc.core.e;
import com.alipay.android.phone.mrpc.core.o;
import com.alipay.security.mobile.module.a.a.a;
import com.alipay.security.mobile.module.commonutils.d;
import com.alipay.tscenter.biz.rpc.deviceFp.BugTrackMessageService;
import com.alipay.tscenter.biz.rpc.vkeydfp.AppListCmdService;
import com.alipay.tscenter.biz.rpc.vkeydfp.DeviceDataReportService;
import com.alipay.tscenter.biz.rpc.vkeydfp.request.AppListCmdRequest;
import com.alipay.tscenter.biz.rpc.vkeydfp.request.DeviceDataReportRequest;
import com.alipay.tscenter.biz.rpc.vkeydfp.result.AppListResult;
import com.alipay.tscenter.biz.rpc.vkeydfp.result.DeviceDataReportResult;
import org.json.JSONObject;

public final class b implements a {
    private static b f;
    private static DeviceDataReportResult g;
    private Context a;
    private ad b;
    private BugTrackMessageService c;
    private DeviceDataReportService d;
    private AppListCmdService e;

    static {
        f = null;
    }

    private b(Context context) {
        this.a = null;
        this.b = null;
        this.c = null;
        this.d = null;
        this.e = null;
        this.a = context;
        try {
            e eVar = new e();
            eVar.a = a.a();
            this.b = new o(context);
            this.c = (BugTrackMessageService) this.b.a(BugTrackMessageService.class, eVar);
            this.d = (DeviceDataReportService) this.b.a(DeviceDataReportService.class, eVar);
            this.e = (AppListCmdService) this.b.a(AppListCmdService.class, eVar);
        } catch (Throwable e) {
            d.a(e);
        }
    }

    public static synchronized b a(Context context) {
        b bVar;
        synchronized (b.class) {
            if (f == null) {
                f = new b(context);
            }
            bVar = f;
        }
        return bVar;
    }

    public final AppListResult a(String str, String str2, String str3, String str4) {
        AppListResult appListResult = null;
        try {
            AppListCmdRequest appListCmdRequest = new AppListCmdRequest();
            appListCmdRequest.os = str;
            appListCmdRequest.apdid = str4;
            appListCmdRequest.userId = str2;
            appListCmdRequest.token = str3;
            appListResult = this.e.getAppListCmd(appListCmdRequest);
        } catch (Exception e) {
        }
        return appListResult;
    }

    public final DeviceDataReportResult a(DeviceDataReportRequest deviceDataReportRequest) {
        if (this.d != null) {
            try {
                g = null;
                new Thread(new c(this, deviceDataReportRequest)).start();
                int i = 300000;
                while (g == null && i >= 0) {
                    Thread.sleep(50);
                    i -= 50;
                }
            } catch (Throwable e) {
                d.a(e);
            }
        }
        return g;
    }

    public final boolean a(String str) {
        if (com.alipay.security.mobile.module.commonutils.a.a(str)) {
            return false;
        }
        boolean booleanValue;
        if (this.c != null) {
            String str2 = null;
            try {
                str2 = this.c.logCollect(com.alipay.security.mobile.module.commonutils.a.e(str));
            } catch (Exception e) {
            }
            if (!com.alipay.security.mobile.module.commonutils.a.a(str2)) {
                try {
                    booleanValue = ((Boolean) new JSONObject(str2).get("success")).booleanValue();
                } catch (Throwable e2) {
                    d.a(e2);
                }
                return booleanValue;
            }
        }
        booleanValue = false;
        return booleanValue;
    }
}
