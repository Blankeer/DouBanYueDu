package com.douban.book.reader.exception;

public class DataException extends WorksException {
    public DataException(Throwable cause) {
        super(cause);
    }

    public DataException(String detailMessage) {
        super(detailMessage);
    }

    public DataException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
