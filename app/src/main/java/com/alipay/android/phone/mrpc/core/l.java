package com.alipay.android.phone.mrpc.core;

import com.douban.book.reader.helper.AppUri;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

final class l extends DefaultRedirectHandler {
    int a;
    final /* synthetic */ k b;

    l(k kVar) {
        this.b = kVar;
    }

    public final boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
        this.a++;
        boolean isRedirectRequested = super.isRedirectRequested(httpResponse, httpContext);
        if (isRedirectRequested || this.a >= 5) {
            return isRedirectRequested;
        }
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        return (statusCode == AppUri.READER_COLUMN || statusCode == AppUri.READER_COLUMN_CHAPTER) ? true : isRedirectRequested;
    }
}
