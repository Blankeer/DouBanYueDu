package com.sina.weibo.sdk.exception;

public class WeiboDialogException extends WeiboException {
    private static final long serialVersionUID = 1;
    private int mErrorCode;
    private String mFailingUrl;

    public WeiboDialogException(String message, int errorCode, String failingUrl) {
        super(message);
        this.mErrorCode = errorCode;
        this.mFailingUrl = failingUrl;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public String getFailingUrl() {
        return this.mFailingUrl;
    }
}
