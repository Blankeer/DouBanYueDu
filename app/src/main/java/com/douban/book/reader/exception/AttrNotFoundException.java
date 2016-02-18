package com.douban.book.reader.exception;

public class AttrNotFoundException extends Exception {
    public AttrNotFoundException(Throwable cause) {
        super(cause);
    }

    public AttrNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public AttrNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
