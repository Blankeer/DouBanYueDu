package io.realm.internal.modules;

import android.util.JsonReader;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.RealmProxyMediator;
import io.realm.internal.Table;
import io.realm.internal.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

public class FilterableMediator extends RealmProxyMediator {
    private Set<Class<? extends RealmObject>> allowedClasses;
    private RealmProxyMediator originalMediator;

    public FilterableMediator(RealmProxyMediator originalMediator, Collection<Class<? extends RealmObject>> allowedClasses) {
        this.allowedClasses = new HashSet();
        this.originalMediator = originalMediator;
        if (originalMediator != null) {
            List<Class<? extends RealmObject>> originalClasses = originalMediator.getModelClasses();
            for (Class<? extends RealmObject> clazz : allowedClasses) {
                if (originalClasses.contains(clazz)) {
                    this.allowedClasses.add(clazz);
                }
            }
        }
    }

    public RealmProxyMediator getOriginalMediator() {
        return this.originalMediator;
    }

    public Table createTable(Class<? extends RealmObject> clazz, ImplicitTransaction transaction) {
        checkSchemaHasClass(clazz);
        return this.originalMediator.createTable(clazz, transaction);
    }

    public void validateTable(Class<? extends RealmObject> clazz, ImplicitTransaction transaction) {
        checkSchemaHasClass(clazz);
        this.originalMediator.validateTable(clazz, transaction);
    }

    public List<String> getFieldNames(Class<? extends RealmObject> clazz) {
        checkSchemaHasClass(clazz);
        return this.originalMediator.getFieldNames(clazz);
    }

    public String getTableName(Class<? extends RealmObject> clazz) {
        checkSchemaHasClass(clazz);
        return this.originalMediator.getTableName(clazz);
    }

    public <E extends RealmObject> E newInstance(Class<E> clazz) {
        checkSchemaHasClass(clazz);
        return this.originalMediator.newInstance(clazz);
    }

    public List<Class<? extends RealmObject>> getModelClasses() {
        return new ArrayList(this.allowedClasses);
    }

    public Map<String, Long> getColumnIndices(Class<? extends RealmObject> clazz) {
        checkSchemaHasClass(clazz);
        return this.originalMediator.getColumnIndices(clazz);
    }

    public <E extends RealmObject> E copyOrUpdate(Realm realm, E object, boolean update, Map<RealmObject, RealmObjectProxy> cache) {
        checkSchemaHasClass(Util.getOriginalModelClass(object.getClass()));
        return this.originalMediator.copyOrUpdate(realm, object, update, cache);
    }

    public <E extends RealmObject> E createOrUpdateUsingJsonObject(Class<E> clazz, Realm realm, JSONObject json, boolean update) throws JSONException {
        checkSchemaHasClass(clazz);
        return this.originalMediator.createOrUpdateUsingJsonObject(clazz, realm, json, update);
    }

    public <E extends RealmObject> E createUsingJsonStream(Class<E> clazz, Realm realm, JsonReader reader) throws IOException {
        checkSchemaHasClass(clazz);
        return this.originalMediator.createUsingJsonStream(clazz, realm, reader);
    }

    private void checkSchemaHasClass(Class<? extends RealmObject> clazz) {
        if (!this.allowedClasses.contains(clazz)) {
            throw new IllegalArgumentException(clazz.getSimpleName() + " is not part of the schema for this Realm");
        }
    }
}
