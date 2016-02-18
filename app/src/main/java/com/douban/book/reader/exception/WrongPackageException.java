package com.douban.book.reader.exception;

public class WrongPackageException extends Exception {
    public WrongPackageException(Throwable cause) {
        super(cause);
    }

    public WrongPackageException(String detailMessage) {
        super(detailMessage);
    }

    public WrongPackageException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
