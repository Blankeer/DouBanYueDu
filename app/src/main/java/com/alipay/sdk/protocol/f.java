package com.alipay.sdk.protocol;

import com.tencent.connect.common.Constants;

public enum f {
    SUCCESS(Constants.VIA_RESULT_SUCCESS),
    TID_REFRESH("tid_refresh_invalid"),
    POP_TYPE("pop_type"),
    NOT_POP_TYPE("not_pop_type");
    
    private String e;

    private f(String str) {
        this.e = str;
    }

    private String a() {
        return this.e;
    }

    public static f a(String str) {
        f fVar = null;
        f[] values = values();
        int length = values.length;
        int i = 0;
        while (i < length) {
            f fVar2 = values[i];
            if (!str.startsWith(fVar2.e)) {
                fVar2 = fVar;
            }
            i++;
            fVar = fVar2;
        }
        return fVar;
    }
}
