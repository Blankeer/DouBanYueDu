package com.alipay.android.phone.mrpc.core;

import org.apache.http.client.RedirectHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;

final class k extends DefaultHttpClient {
    final /* synthetic */ i a;

    k(i iVar, ClientConnectionManager clientConnectionManager, HttpParams httpParams) {
        this.a = iVar;
        super(clientConnectionManager, httpParams);
    }

    protected final ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy() {
        return new m(this);
    }

    protected final HttpContext createHttpContext() {
        HttpContext basicHttpContext = new BasicHttpContext();
        basicHttpContext.setAttribute("http.authscheme-registry", getAuthSchemes());
        basicHttpContext.setAttribute("http.cookiespec-registry", getCookieSpecs());
        basicHttpContext.setAttribute("http.auth.credentials-provider", getCredentialsProvider());
        return basicHttpContext;
    }

    protected final BasicHttpProcessor createHttpProcessor() {
        BasicHttpProcessor createHttpProcessor = super.createHttpProcessor();
        createHttpProcessor.addRequestInterceptor(i.d);
        createHttpProcessor.addRequestInterceptor(new a((byte) 0));
        return createHttpProcessor;
    }

    protected final RedirectHandler createRedirectHandler() {
        return new l(this);
    }
}
