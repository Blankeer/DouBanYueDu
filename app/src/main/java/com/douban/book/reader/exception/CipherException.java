package com.douban.book.reader.exception;

public class CipherException extends DataException {
    public CipherException(Throwable cause) {
        super(cause);
    }

    public CipherException(String detailMessage) {
        super(detailMessage);
    }

    public CipherException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
