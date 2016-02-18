package com.alipay.sdk.app;

import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public enum m {
    SUCCEEDED(9000, "\u5904\u7406\u6210\u529f"),
    FAILED(4000, "\u7cfb\u7edf\u7e41\u5fd9\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5"),
    CANCELED(6001, "\u7528\u6237\u53d6\u6d88"),
    NETWORK_ERROR(6002, "\u7f51\u7edc\u8fde\u63a5\u5f02\u5e38"),
    PARAMS_ERROR(4001, "\u53c2\u6570\u9519\u8bef"),
    PAY_WAITTING(SettingsJsonConstants.ANALYTICS_MAX_BYTE_SIZE_PER_FILE_DEFAULT, "\u652f\u4ed8\u7ed3\u679c\u786e\u8ba4\u4e2d");
    
    int g;
    String h;

    private m(int i, String str) {
        this.g = i;
        this.h = str;
    }

    private void b(int i) {
        this.g = i;
    }

    private int a() {
        return this.g;
    }

    private void a(String str) {
        this.h = str;
    }

    private String b() {
        return this.h;
    }

    public static m a(int i) {
        switch (i) {
            case 4001:
                return PARAMS_ERROR;
            case 6001:
                return CANCELED;
            case 6002:
                return NETWORK_ERROR;
            case SettingsJsonConstants.ANALYTICS_MAX_BYTE_SIZE_PER_FILE_DEFAULT /*8000*/:
                return PAY_WAITTING;
            case 9000:
                return SUCCEEDED;
            default:
                return FAILED;
        }
    }
}
