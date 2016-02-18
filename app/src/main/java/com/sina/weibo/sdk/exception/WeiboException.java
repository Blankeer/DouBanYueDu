package com.sina.weibo.sdk.exception;

public class WeiboException extends RuntimeException {
    private static final long serialVersionUID = 475022994858770424L;

    public WeiboException(String message) {
        super(message);
    }

    public WeiboException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public WeiboException(Throwable throwable) {
        super(throwable);
    }
}
