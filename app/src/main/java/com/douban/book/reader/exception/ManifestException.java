package com.douban.book.reader.exception;

public class ManifestException extends DataException {
    public ManifestException(Throwable cause) {
        super(cause);
    }

    public ManifestException(String detailMessage) {
        super(detailMessage);
    }

    public ManifestException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
