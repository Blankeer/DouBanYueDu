package io.realm.exceptions;

public class RealmError extends Error {
    public RealmError(String detailMessage) {
        super(detailMessage);
    }
}
