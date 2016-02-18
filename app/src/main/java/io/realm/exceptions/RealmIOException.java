package io.realm.exceptions;

public class RealmIOException extends RuntimeException {
    public RealmIOException(Throwable cause) {
        super(cause);
    }

    public RealmIOException(String message) {
        super(message);
    }

    public RealmIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
