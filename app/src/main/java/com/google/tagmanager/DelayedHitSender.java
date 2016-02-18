package com.google.tagmanager;

import android.content.Context;
import com.google.android.gms.common.util.VisibleForTesting;
import com.j256.ormlite.stmt.query.SimpleComparison;
import io.fabric.sdk.android.services.network.HttpRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class DelayedHitSender implements HitSender {
    private static DelayedHitSender sInstance;
    private static final Object sInstanceLock;
    private RateLimiter mRateLimiter;
    private HitSendingThread mSendingThread;
    private String mWrapperQueryParameter;
    private String mWrapperUrl;

    static {
        sInstanceLock = new Object();
    }

    private DelayedHitSender(Context context) {
        this(HitSendingThreadImpl.getInstance(context), new SendHitRateLimiter());
    }

    @VisibleForTesting
    DelayedHitSender(HitSendingThread thread, RateLimiter rateLimiter) {
        this.mSendingThread = thread;
        this.mRateLimiter = rateLimiter;
    }

    public static HitSender getInstance(Context context) {
        HitSender hitSender;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new DelayedHitSender(context);
            }
            hitSender = sInstance;
        }
        return hitSender;
    }

    public void setUrlWrapModeForTesting(String url, String queryParameter) {
        this.mWrapperUrl = url;
        this.mWrapperQueryParameter = queryParameter;
    }

    public boolean sendHit(String url) {
        if (this.mRateLimiter.tokenAvailable()) {
            if (!(this.mWrapperUrl == null || this.mWrapperQueryParameter == null)) {
                try {
                    url = this.mWrapperUrl + "?" + this.mWrapperQueryParameter + SimpleComparison.EQUAL_TO_OPERATION + URLEncoder.encode(url, HttpRequest.CHARSET_UTF8);
                    Log.v("Sending wrapped url hit: " + url);
                } catch (UnsupportedEncodingException e) {
                    Log.w("Error wrapping URL for testing.", e);
                    return false;
                }
            }
            this.mSendingThread.sendHit(url);
            return true;
        }
        Log.w("Too many urls sent too quickly with the TagManagerSender, rate limiting invoked.");
        return false;
    }
}
