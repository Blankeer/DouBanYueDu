package io.realm;

public interface RealmMigration {
    long execute(Realm realm, long j);
}
