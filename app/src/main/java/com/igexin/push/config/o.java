package com.igexin.push.config;

import android.os.Bundle;
import android.support.v4.media.TransportMediator;
import com.igexin.a.a.c.a;
import com.igexin.push.core.g;

public class o {
    public static void a() {
        try {
            Bundle bundle = g.g.getPackageManager().getApplicationInfo(g.g.getPackageName(), TransportMediator.FLAG_KEY_MEDIA_NEXT).metaData;
            if (bundle != null) {
                for (String str : bundle.keySet()) {
                    if (str.equals("PUSH_DOMAIN")) {
                        a.b("PUSH_DOMAIN:" + bundle.getString(str));
                        a(bundle.getString(str));
                        return;
                    }
                }
            }
        } catch (Exception e) {
            a.b(e.toString());
        }
    }

    private static void a(String str) {
        SDKUrlConfig.XFR_ADDRESS_IPS = new String[]{"socket://xfr." + str + ":5224"};
        a.b("XFR_ADDRESS_IPS:" + SDKUrlConfig.XFR_ADDRESS_IPS[0]);
        SDKUrlConfig.XFR_ADDRESS_IPS_BAK = new String[]{"socket://xfr_bak." + str + ":5224"};
        a.b("XFR_ADDRESS_IPS_BAK:" + SDKUrlConfig.XFR_ADDRESS_IPS_BAK[0]);
        SDKUrlConfig.BI_ADDRESS_IPS = new String[]{"http://bi." + str + "/api.php"};
        a.b("BI_ADDRESS_IPS:" + SDKUrlConfig.BI_ADDRESS_IPS[0]);
        SDKUrlConfig.CONFIG_ADDRESS_IPS = new String[]{"http://config." + str + "/api.php"};
        a.b("CONFIG_ADDRESS_IPS:" + SDKUrlConfig.CONFIG_ADDRESS_IPS[0]);
        SDKUrlConfig.STATE_ADDRESS_IPS = new String[]{"http://stat." + str + "/api.php"};
        a.b("STATE_ADDRESS_IPS:" + SDKUrlConfig.STATE_ADDRESS_IPS[0]);
        SDKUrlConfig.LOG_ADDRESS_IPS = new String[]{"http://log." + str + "/api.php"};
        a.b("LOG_ADDRESS_IPS:" + SDKUrlConfig.LOG_ADDRESS_IPS[0]);
        SDKUrlConfig.AMP_ADDRESS_IPS = new String[]{"http://amp." + str + "/api.htm"};
        a.b("AMP_ADDRESS_IPS:" + SDKUrlConfig.AMP_ADDRESS_IPS[0]);
        SDKUrlConfig.LBS_ADDRESS_IPS = new String[]{"http://lbs." + str + "/api.htm"};
        a.b("LBS_ADDRESS_IPS:" + SDKUrlConfig.LBS_ADDRESS_IPS[0]);
        SDKUrlConfig.INC_ADDRESS_IPS = new String[]{"http://inc." + str + "/api.php"};
        a.b("INC_ADDRESS_IPS:" + SDKUrlConfig.INC_ADDRESS_IPS[0]);
    }
}
