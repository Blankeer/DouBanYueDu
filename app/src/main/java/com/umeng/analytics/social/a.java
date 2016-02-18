package com.umeng.analytics.social;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import io.realm.internal.Table;

/* compiled from: UMException */
public class a extends RuntimeException {
    private static final long b = -4656673116019167471L;
    protected int a;
    private String c;

    public int a() {
        return this.a;
    }

    public a(int i, String str) {
        super(str);
        this.a = BaseImageDownloader.DEFAULT_HTTP_CONNECT_TIMEOUT;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.a = i;
        this.c = str;
    }

    public a(String str, Throwable th) {
        super(str, th);
        this.a = BaseImageDownloader.DEFAULT_HTTP_CONNECT_TIMEOUT;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.c = str;
    }

    public a(String str) {
        super(str);
        this.a = BaseImageDownloader.DEFAULT_HTTP_CONNECT_TIMEOUT;
        this.c = Table.STRING_DEFAULT_VALUE;
        this.c = str;
    }

    public String getMessage() {
        return this.c;
    }
}
