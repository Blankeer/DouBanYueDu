package io.realm.internal;

public class IllegalMixedTypeException extends RuntimeException {
    public IllegalMixedTypeException(Throwable cause) {
        super(cause);
    }

    public IllegalMixedTypeException(String message) {
        super(message);
    }

    public IllegalMixedTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
