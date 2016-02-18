package io.realm;

import android.util.JsonReader;
import com.douban.book.reader.manager.sync.PendingRequest;
import io.realm.annotations.RealmModule;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.RealmProxyMediator;
import io.realm.internal.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

@RealmModule
class DefaultRealmModuleMediator extends RealmProxyMediator {
    private static final List<Class<? extends RealmObject>> MODEL_CLASSES;

    DefaultRealmModuleMediator() {
    }

    static {
        List<Class<? extends RealmObject>> modelClasses = new ArrayList();
        modelClasses.add(PendingRequest.class);
        MODEL_CLASSES = Collections.unmodifiableList(modelClasses);
    }

    public Table createTable(Class<? extends RealmObject> clazz, ImplicitTransaction transaction) {
        RealmProxyMediator.checkClass(clazz);
        if (clazz.equals(PendingRequest.class)) {
            return PendingRequestRealmProxy.initTable(transaction);
        }
        throw RealmProxyMediator.getMissingProxyClassException(clazz);
    }

    public void validateTable(Class<? extends RealmObject> clazz, ImplicitTransaction transaction) {
        RealmProxyMediator.checkClass(clazz);
        if (clazz.equals(PendingRequest.class)) {
            PendingRequestRealmProxy.validateTable(transaction);
            return;
        }
        throw RealmProxyMediator.getMissingProxyClassException(clazz);
    }

    public List<String> getFieldNames(Class<? extends RealmObject> clazz) {
        RealmProxyMediator.checkClass(clazz);
        if (clazz.equals(PendingRequest.class)) {
            return PendingRequestRealmProxy.getFieldNames();
        }
        throw RealmProxyMediator.getMissingProxyClassException(clazz);
    }

    public String getTableName(Class<? extends RealmObject> clazz) {
        RealmProxyMediator.checkClass(clazz);
        if (clazz.equals(PendingRequest.class)) {
            return PendingRequestRealmProxy.getTableName();
        }
        throw RealmProxyMediator.getMissingProxyClassException(clazz);
    }

    public <E extends RealmObject> E newInstance(Class<E> clazz) {
        RealmProxyMediator.checkClass(clazz);
        if (clazz.equals(PendingRequest.class)) {
            return (RealmObject) clazz.cast(new PendingRequestRealmProxy());
        }
        throw RealmProxyMediator.getMissingProxyClassException(clazz);
    }

    public List<Class<? extends RealmObject>> getModelClasses() {
        return MODEL_CLASSES;
    }

    public Map<String, Long> getColumnIndices(Class<? extends RealmObject> clazz) {
        RealmProxyMediator.checkClass(clazz);
        if (clazz.equals(PendingRequest.class)) {
            return PendingRequestRealmProxy.getColumnIndices();
        }
        throw RealmProxyMediator.getMissingProxyClassException(clazz);
    }

    public <E extends RealmObject> E copyOrUpdate(Realm realm, E obj, boolean update, Map<RealmObject, RealmObjectProxy> cache) {
        Class<E> clazz = obj instanceof RealmObjectProxy ? obj.getClass().getSuperclass() : obj.getClass();
        if (clazz.equals(PendingRequest.class)) {
            return (RealmObject) clazz.cast(PendingRequestRealmProxy.copyOrUpdate(realm, (PendingRequest) obj, update, cache));
        }
        throw RealmProxyMediator.getMissingProxyClassException(clazz);
    }

    public <E extends RealmObject> E createOrUpdateUsingJsonObject(Class<E> clazz, Realm realm, JSONObject json, boolean update) throws JSONException {
        RealmProxyMediator.checkClass(clazz);
        if (clazz.equals(PendingRequest.class)) {
            return (RealmObject) clazz.cast(PendingRequestRealmProxy.createOrUpdateUsingJsonObject(realm, json, update));
        }
        throw RealmProxyMediator.getMissingProxyClassException(clazz);
    }

    public <E extends RealmObject> E createUsingJsonStream(Class<E> clazz, Realm realm, JsonReader reader) throws IOException {
        RealmProxyMediator.checkClass(clazz);
        if (clazz.equals(PendingRequest.class)) {
            return (RealmObject) clazz.cast(PendingRequestRealmProxy.createUsingJsonStream(realm, reader));
        }
        throw RealmProxyMediator.getMissingProxyClassException(clazz);
    }
}
