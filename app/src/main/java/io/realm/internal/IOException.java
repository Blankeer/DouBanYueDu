package io.realm.internal;

public class IOException extends RuntimeException {
    public IOException(Throwable cause) {
        super(cause);
    }

    public IOException(String message) {
        super(message);
    }

    public IOException(String message, Throwable cause) {
        super(message, cause);
    }
}
