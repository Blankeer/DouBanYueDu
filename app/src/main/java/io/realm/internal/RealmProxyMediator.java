package io.realm.internal;

import android.util.JsonReader;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.exceptions.RealmException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class RealmProxyMediator {
    public abstract <E extends RealmObject> E copyOrUpdate(Realm realm, E e, boolean z, Map<RealmObject, RealmObjectProxy> map);

    public abstract <E extends RealmObject> E createOrUpdateUsingJsonObject(Class<E> cls, Realm realm, JSONObject jSONObject, boolean z) throws JSONException;

    public abstract Table createTable(Class<? extends RealmObject> cls, ImplicitTransaction implicitTransaction);

    public abstract <E extends RealmObject> E createUsingJsonStream(Class<E> cls, Realm realm, JsonReader jsonReader) throws IOException;

    public abstract Map<String, Long> getColumnIndices(Class<? extends RealmObject> cls);

    public abstract List<String> getFieldNames(Class<? extends RealmObject> cls);

    public abstract List<Class<? extends RealmObject>> getModelClasses();

    public abstract String getTableName(Class<? extends RealmObject> cls);

    public abstract <E extends RealmObject> E newInstance(Class<E> cls);

    public abstract void validateTable(Class<? extends RealmObject> cls, ImplicitTransaction implicitTransaction);

    public boolean equals(Object o) {
        if (!(o instanceof RealmProxyMediator)) {
            return false;
        }
        return getModelClasses().equals(((RealmProxyMediator) o).getModelClasses());
    }

    public int hashCode() {
        return getModelClasses().hashCode();
    }

    protected static void checkClass(Class<? extends RealmObject> clazz) {
        if (clazz == null) {
            throw new NullPointerException("A class extending RealmObject must be provided");
        }
    }

    protected static RealmException getMissingProxyClassException(Class<? extends RealmObject> clazz) {
        return new RealmException(clazz + " is not part of the schema for this Realm.");
    }
}
