package io.realm;

import io.realm.annotations.RealmClass;
import io.realm.internal.InvalidRow;
import io.realm.internal.Row;

@RealmClass
public abstract class RealmObject {
    protected Realm realm;
    protected Row row;

    public void removeFromRealm() {
        if (this.row == null) {
            throw new IllegalStateException("Object malformed: missing object in Realm. Make sure to instantiate RealmObjects with Realm.createObject()");
        } else if (this.realm == null) {
            throw new IllegalStateException("Object malformed: missing Realm. Make sure to instantiate RealmObjects with Realm.createObject()");
        } else {
            this.row.getTable().moveLastOver(this.row.getIndex());
            this.row = InvalidRow.INSTANCE;
        }
    }

    public boolean isValid() {
        return this.row != null && this.row.isAttached();
    }

    protected static Realm getRealm(RealmObject obj) {
        return obj.realm;
    }

    protected static Row getRow(RealmObject obj) {
        return obj.row;
    }
}
