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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class CompositeMediator extends RealmProxyMediator {
    Map<Class<? extends RealmObject>, RealmProxyMediator> mediators;

    public CompositeMediator() {
        this.mediators = new HashMap();
    }

    public void addMediator(RealmProxyMediator mediator) {
        for (Class<? extends RealmObject> realmClass : mediator.getModelClasses()) {
            this.mediators.put(realmClass, mediator);
        }
    }

    public Table createTable(Class<? extends RealmObject> clazz, ImplicitTransaction transaction) {
        return getMediator(clazz).createTable(clazz, transaction);
    }

    public void validateTable(Class<? extends RealmObject> clazz, ImplicitTransaction transaction) {
        getMediator(clazz).validateTable(clazz, transaction);
    }

    public List<String> getFieldNames(Class<? extends RealmObject> clazz) {
        return getMediator(clazz).getFieldNames(clazz);
    }

    public String getTableName(Class<? extends RealmObject> clazz) {
        return getMediator(clazz).getTableName(clazz);
    }

    public <E extends RealmObject> E newInstance(Class<E> clazz) {
        return getMediator(clazz).newInstance(clazz);
    }

    public List<Class<? extends RealmObject>> getModelClasses() {
        List<Class<? extends RealmObject>> list = new ArrayList();
        for (RealmProxyMediator mediator : this.mediators.values()) {
            list.addAll(mediator.getModelClasses());
        }
        return list;
    }

    public Map<String, Long> getColumnIndices(Class<? extends RealmObject> clazz) {
        return getMediator(clazz).getColumnIndices(clazz);
    }

    public <E extends RealmObject> E copyOrUpdate(Realm realm, E object, boolean update, Map<RealmObject, RealmObjectProxy> cache) {
        return getMediator(Util.getOriginalModelClass(object.getClass())).copyOrUpdate(realm, object, update, cache);
    }

    public <E extends RealmObject> E createOrUpdateUsingJsonObject(Class<E> clazz, Realm realm, JSONObject json, boolean update) throws JSONException {
        return getMediator(clazz).createOrUpdateUsingJsonObject(clazz, realm, json, update);
    }

    public <E extends RealmObject> E createUsingJsonStream(Class<E> clazz, Realm realm, JsonReader reader) throws IOException {
        return getMediator(clazz).createUsingJsonStream(clazz, realm, reader);
    }

    private RealmProxyMediator getMediator(Class<? extends RealmObject> clazz) {
        RealmProxyMediator mediator = (RealmProxyMediator) this.mediators.get(clazz);
        if (mediator != null) {
            return mediator;
        }
        throw new IllegalArgumentException(clazz.getSimpleName() + " is not part of the schema for this Realm");
    }
}
