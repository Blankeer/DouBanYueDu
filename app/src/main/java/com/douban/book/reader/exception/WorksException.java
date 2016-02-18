package com.douban.book.reader.exception;

public class WorksException extends Exception {
    public WorksException(Throwable cause) {
        super(cause);
    }

    public WorksException(String detailMessage) {
        super(detailMessage);
    }

    public WorksException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
