package com.douban.book.reader.data.realm;

import io.realm.Realm;
import io.realm.RealmObject;

public interface RealmDataConsumer<T extends RealmObject> {
    void consume(Realm realm, T t) throws RealmDataException;
}
