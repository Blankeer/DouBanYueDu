package com.alipay.sdk.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.methods.HttpPost;

public final class d {
    private static ThreadPoolExecutor a;
    private static Object b;
    private static HttpPost c;

    static {
        a = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(20));
        b = new Object();
        c = new HttpPost("https://mclient.alipay.com/sdkErrorlog.do");
    }

    public static void a(String str) {
        a.execute(new e(str));
    }
}
