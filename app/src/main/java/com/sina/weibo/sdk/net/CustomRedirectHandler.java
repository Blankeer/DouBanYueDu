package com.sina.weibo.sdk.net;

import android.text.TextUtils;
import com.douban.book.reader.helper.AppUri;
import com.igexin.download.Downloads;
import com.sina.weibo.sdk.utils.LogUtil;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.protocol.HttpContext;

public abstract class CustomRedirectHandler implements RedirectHandler {
    private static final int MAX_REDIRECT_COUNT = 15;
    private static final String TAG;
    int redirectCount;
    String redirectUrl;
    private String tempRedirectUrl;

    public abstract void onReceivedException();

    public abstract boolean shouldRedirectUrl(String str);

    static {
        TAG = CustomRedirectHandler.class.getCanonicalName();
    }

    public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
        LogUtil.d(TAG, "CustomRedirectHandler getLocationURI getRedirectUrl : " + this.tempRedirectUrl);
        if (TextUtils.isEmpty(this.tempRedirectUrl)) {
            return null;
        }
        return URI.create(this.tempRedirectUrl);
    }

    public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == AppUri.READER_COLUMN || statusCode == AppUri.READER_COLUMN_CHAPTER) {
            this.tempRedirectUrl = response.getFirstHeader(HttpRequest.HEADER_LOCATION).getValue();
            if (!TextUtils.isEmpty(this.tempRedirectUrl) && this.redirectCount < MAX_REDIRECT_COUNT && shouldRedirectUrl(this.tempRedirectUrl)) {
                this.redirectCount++;
                return true;
            }
        } else if (statusCode == Downloads.STATUS_SUCCESS) {
            this.redirectUrl = this.tempRedirectUrl;
        } else {
            onReceivedException();
        }
        return false;
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }

    public int getRedirectCount() {
        return this.redirectCount;
    }
}
