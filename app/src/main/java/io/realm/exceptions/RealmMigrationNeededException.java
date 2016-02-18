package io.realm.exceptions;

public class RealmMigrationNeededException extends RuntimeException {
    private final String canonicalRealmPath;

    public RealmMigrationNeededException(String canonicalRealmPath, String detailMessage) {
        super(detailMessage);
        this.canonicalRealmPath = canonicalRealmPath;
    }

    public RealmMigrationNeededException(String canonicalRealmPath, String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        this.canonicalRealmPath = canonicalRealmPath;
    }

    public String getPath() {
        return this.canonicalRealmPath;
    }
}
