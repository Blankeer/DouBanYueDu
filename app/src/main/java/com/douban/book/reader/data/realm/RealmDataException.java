package com.douban.book.reader.data.realm;

public class RealmDataException extends Exception {
    public RealmDataException(Throwable cause) {
        super(cause);
    }

    public RealmDataException(String detailMessage) {
        super(detailMessage);
    }

    public RealmDataException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
