package com.sina.weibo.sdk.exception;

public class WeiboHttpException extends WeiboException {
    private static final long serialVersionUID = 1;
    private final int mStatusCode;

    public WeiboHttpException(String message, int statusCode) {
        super(message);
        this.mStatusCode = statusCode;
    }

    public int getStatusCode() {
        return this.mStatusCode;
    }
}
