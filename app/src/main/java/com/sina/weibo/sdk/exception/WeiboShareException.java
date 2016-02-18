package com.sina.weibo.sdk.exception;

public class WeiboShareException extends WeiboException {
    private static final long serialVersionUID = 1;

    public WeiboShareException(String message) {
        super(message);
    }

    public WeiboShareException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public WeiboShareException(Throwable throwable) {
        super(throwable);
    }
}
