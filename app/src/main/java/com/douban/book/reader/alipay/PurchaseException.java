package com.douban.book.reader.alipay;

public class PurchaseException extends Exception {
    public PurchaseException(Throwable cause) {
        super(cause);
    }

    public PurchaseException(String detailMessage) {
        super(detailMessage);
    }

    public PurchaseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
