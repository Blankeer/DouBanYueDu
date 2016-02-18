package com.douban.book.reader.exception;

public class PagingException extends WorksException {
    public PagingException(Throwable cause) {
        super(cause);
    }

    public PagingException(String detailMessage) {
        super(detailMessage);
    }

    public PagingException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
