package io.realm.exceptions;

public class RealmException extends RuntimeException {
    public RealmException(String detailMessage) {
        super(detailMessage);
    }

    public RealmException(String detailMessage, Throwable exception) {
        super(detailMessage, exception);
    }
}
