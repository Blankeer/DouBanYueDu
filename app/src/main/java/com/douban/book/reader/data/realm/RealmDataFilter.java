package com.douban.book.reader.data.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public interface RealmDataFilter<T extends RealmObject> {
    RealmResults<T> filter(Realm realm);
}
