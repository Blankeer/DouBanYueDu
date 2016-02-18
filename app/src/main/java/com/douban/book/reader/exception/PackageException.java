package com.douban.book.reader.exception;

public class PackageException extends WorksException {
    public PackageException(Throwable cause) {
        super(cause);
    }

    public PackageException(String detailMessage) {
        super(detailMessage);
    }

    public PackageException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
