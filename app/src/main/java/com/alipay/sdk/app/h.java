package com.alipay.sdk.app;

import android.webkit.SslErrorHandler;
import com.alipay.sdk.widget.d;

final class h implements Runnable {
    final /* synthetic */ SslErrorHandler a;
    final /* synthetic */ a b;

    h(a aVar, SslErrorHandler sslErrorHandler) {
        this.b = aVar;
        this.a = sslErrorHandler;
    }

    public final void run() {
        d.a(this.b.a, "\u5b89\u5168\u8b66\u544a", "\u7531\u4e8e\u60a8\u7684\u8bbe\u5907\u7f3a\u5c11\u6839\u8bc1\u4e66\uff0c\u5c06\u65e0\u6cd5\u6821\u9a8c\u8be5\u8bbf\u95ee\u7ad9\u70b9\u7684\u5b89\u5168\u6027\uff0c\u53ef\u80fd\u5b58\u5728\u98ce\u9669\uff0c\u8bf7\u9009\u62e9\u662f\u5426\u7ee7\u7eed\uff1f", "\u7ee7\u7eed", new i(this), "\u9000\u51fa", new j(this));
    }
}
