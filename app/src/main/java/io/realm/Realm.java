package io.realm;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.JsonReader;
import com.sina.weibo.sdk.component.ShareRequestParam;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.RealmConfiguration.Builder;
import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.ColumnIndices;
import io.realm.internal.ColumnType;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.RealmProxyMediator;
import io.realm.internal.SharedGroup;
import io.realm.internal.SharedGroup.Durability;
import io.realm.internal.Table;
import io.realm.internal.TableView.Order;
import io.realm.internal.UncheckedRow;
import io.realm.internal.Util;
import io.realm.internal.android.ReleaseAndroidLogger;
import io.realm.internal.log.RealmLog;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Realm implements Closeable {
    private static final String CLOSED_REALM_MESSAGE = "This Realm instance has already been closed, making it unusable.";
    public static final String DEFAULT_REALM_NAME = "default.realm";
    private static final String DIFFERENT_KEY_MESSAGE = "Wrong key used to decrypt Realm.";
    private static final String INCORRECT_THREAD_CLOSE_MESSAGE = "Realm access from incorrect thread. Realm instance can only be closed on the thread it was created.";
    private static final String INCORRECT_THREAD_MESSAGE = "Realm access from incorrect thread. Realm objects can only be accessed on the thread they were created.";
    private static final int REALM_CHANGED = 14930352;
    private static final long UNVERSIONED = -1;
    private static RealmConfiguration defaultConfiguration;
    private static Durability defaultDurability;
    private static final Map<String, AtomicInteger> globalOpenInstanceCounter;
    private static final Map<String, List<RealmConfiguration>> globalPathConfigurationCache;
    protected static final Map<Handler, String> handlers;
    protected static final ThreadLocal<Map<RealmConfiguration, Realm>> realmsCache;
    private static final ThreadLocal<Map<RealmConfiguration, Integer>> referenceCount;
    private boolean autoRefresh;
    private final List<WeakReference<RealmChangeListener>> changeListeners;
    private final Map<Class<? extends RealmObject>, Table> classToTable;
    final ColumnIndices columnIndices;
    private RealmConfiguration configuration;
    private Handler handler;
    private SharedGroup sharedGroup;
    private long threadId;
    private final ImplicitTransaction transaction;

    private class RealmCallback implements Callback {
        private RealmCallback() {
        }

        public boolean handleMessage(Message message) {
            if (message.what == Realm.REALM_CHANGED) {
                Realm.this.transaction.advanceRead();
                Realm.this.sendNotifications();
            }
            return true;
        }
    }

    public interface Transaction {
        void execute(Realm realm);
    }

    static {
        realmsCache = new ThreadLocal<Map<RealmConfiguration, Realm>>() {
            protected Map<RealmConfiguration, Realm> initialValue() {
                return new HashMap();
            }
        };
        referenceCount = new ThreadLocal<Map<RealmConfiguration, Integer>>() {
            protected Map<RealmConfiguration, Integer> initialValue() {
                return new HashMap();
            }
        };
        globalPathConfigurationCache = new HashMap();
        globalOpenInstanceCounter = new ConcurrentHashMap();
        handlers = new ConcurrentHashMap();
        defaultDurability = Durability.FULL;
        RealmLog.add(new ReleaseAndroidLogger());
    }

    protected void checkIfValid() {
        if (this.sharedGroup == null) {
            throw new IllegalStateException(CLOSED_REALM_MESSAGE);
        } else if (this.threadId != Thread.currentThread().getId()) {
            throw new IllegalStateException(INCORRECT_THREAD_MESSAGE);
        }
    }

    private Realm(RealmConfiguration configuration, boolean autoRefresh) {
        this.classToTable = new HashMap();
        this.changeListeners = new CopyOnWriteArrayList();
        this.columnIndices = new ColumnIndices();
        this.threadId = Thread.currentThread().getId();
        this.configuration = configuration;
        this.sharedGroup = new SharedGroup(configuration.getPath(), true, configuration.getDurability(), configuration.getEncryptionKey());
        this.transaction = this.sharedGroup.beginImplicitTransaction();
        setAutoRefresh(autoRefresh);
    }

    protected void finalize() throws Throwable {
        if (this.sharedGroup != null) {
            RealmLog.w("Remember to call close() on all Realm instances. Realm " + this.configuration.getPath() + " is being finalized without being closed, " + "this can lead to running out of native memory.");
        }
        super.finalize();
    }

    public void close() {
        if (this.threadId != Thread.currentThread().getId()) {
            throw new IllegalStateException(INCORRECT_THREAD_CLOSE_MESSAGE);
        }
        Map<RealmConfiguration, Integer> localRefCount = (Map) referenceCount.get();
        String canonicalPath = this.configuration.getPath();
        Integer references = (Integer) localRefCount.get(this.configuration);
        if (references == null) {
            references = Integer.valueOf(0);
        }
        if (this.sharedGroup != null && references.intValue() == 1) {
            ((Map) realmsCache.get()).remove(this.configuration);
            this.sharedGroup.close();
            this.sharedGroup = null;
            synchronized (Realm.class) {
                ((List) globalPathConfigurationCache.get(canonicalPath)).remove(this.configuration);
                if (((AtomicInteger) globalOpenInstanceCounter.get(canonicalPath)).decrementAndGet() == 0) {
                    globalOpenInstanceCounter.remove(canonicalPath);
                }
            }
        }
        int refCount = references.intValue() - 1;
        if (refCount < 0) {
            RealmLog.w("Calling close() on a Realm that is already closed: " + canonicalPath);
        }
        localRefCount.put(this.configuration, Integer.valueOf(Math.max(0, refCount)));
        if (this.handler != null && refCount <= 0) {
            removeHandler(this.handler);
        }
    }

    private void removeHandler(Handler handler) {
        handler.removeCallbacksAndMessages(null);
        handlers.remove(handler);
    }

    public boolean isAutoRefresh() {
        return this.autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        if (autoRefresh && Looper.myLooper() == null) {
            throw new IllegalStateException("Cannot set auto-refresh in a Thread without a Looper");
        }
        if (autoRefresh && !this.autoRefresh) {
            this.handler = new Handler(new RealmCallback());
            handlers.put(this.handler, this.configuration.getPath());
        } else if (!(autoRefresh || !this.autoRefresh || this.handler == null)) {
            removeHandler(this.handler);
        }
        this.autoRefresh = autoRefresh;
    }

    public Table getTable(Class<? extends RealmObject> clazz) {
        Table table = (Table) this.classToTable.get(clazz);
        if (table != null) {
            return table;
        }
        clazz = Util.getOriginalModelClass(clazz);
        table = this.transaction.getTable(this.configuration.getSchemaMediator().getTableName(clazz));
        this.classToTable.put(clazz, table);
        return table;
    }

    public static Realm getInstance(Context context) {
        return getInstance(context, DEFAULT_REALM_NAME);
    }

    @Deprecated
    public static Realm getInstance(Context context, String fileName) {
        return getInstance(context, fileName, null);
    }

    @Deprecated
    public static Realm getInstance(Context context, byte[] key) {
        return getInstance(context, DEFAULT_REALM_NAME, key);
    }

    @Deprecated
    public static Realm getInstance(Context context, String fileName, byte[] key) {
        Builder builder = new Builder(context).name(fileName);
        if (key != null) {
            builder.encryptionKey(key);
        }
        return create(builder.build());
    }

    @Deprecated
    public static Realm getInstance(File writableFolder) {
        return create(new Builder(writableFolder).name(DEFAULT_REALM_NAME).build());
    }

    @Deprecated
    public static Realm getInstance(File writableFolder, String fileName) {
        return create(new Builder(writableFolder).name(fileName).build());
    }

    @Deprecated
    public static Realm getInstance(File writableFolder, byte[] key) {
        return create(new Builder(writableFolder).name(DEFAULT_REALM_NAME).encryptionKey(key).build());
    }

    @Deprecated
    public static Realm getInstance(File writableFolder, String fileName, byte[] key) {
        return create(new Builder(writableFolder).name(fileName).encryptionKey(key).build());
    }

    public static Realm getDefaultInstance() {
        if (defaultConfiguration != null) {
            return create(defaultConfiguration);
        }
        throw new NullPointerException("No default RealmConfiguration was found. Call setDefaultConfiguration() first");
    }

    public static Realm getInstance(RealmConfiguration configuration) {
        if (configuration != null) {
            return create(configuration);
        }
        throw new IllegalArgumentException("A non-null RealmConfiguration must be provided");
    }

    public static void setDefaultConfiguration(RealmConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("A non-null RealmConfiguration must be provided");
        }
        defaultConfiguration = configuration;
    }

    public static void removeDefaultConfiguration() {
        defaultConfiguration = null;
    }

    private static Realm create(RealmConfiguration configuration) {
        boolean z = true;
        boolean autoRefresh = Looper.myLooper() != null ? z : false;
        try {
            return createAndValidate(configuration, true, autoRefresh);
        } catch (RealmMigrationNeededException e) {
            if (configuration.shouldDeleteRealmIfMigrationNeeded()) {
                deleteRealm(configuration);
            } else {
                migrateRealm(configuration);
            }
            return createAndValidate(configuration, z, autoRefresh);
        }
    }

    private static synchronized Realm createAndValidate(RealmConfiguration configuration, boolean validateSchema, boolean autoRefresh) {
        Object realm;
        synchronized (Realm.class) {
            String canonicalPath = configuration.getPath();
            Map<RealmConfiguration, Integer> localRefCount = (Map) referenceCount.get();
            Integer references = (Integer) localRefCount.get(configuration);
            if (references == null) {
                references = Integer.valueOf(0);
            }
            Map<RealmConfiguration, Realm> realms = (Map) realmsCache.get();
            Realm realm2 = (Realm) realms.get(configuration);
            if (realm2 != null) {
                localRefCount.put(configuration, Integer.valueOf(references.intValue() + 1));
                realm = realm2;
            } else {
                validateAgainstExistingConfigurations(configuration);
                realm2 = new Realm(configuration, autoRefresh);
                realms.put(configuration, realm2);
                localRefCount.put(configuration, Integer.valueOf(references.intValue() + 1));
                if (references.intValue() == 0) {
                    AtomicInteger counter = (AtomicInteger) globalOpenInstanceCounter.get(canonicalPath);
                    if (counter == null) {
                        globalOpenInstanceCounter.put(canonicalPath, new AtomicInteger(1));
                    } else {
                        counter.incrementAndGet();
                    }
                }
                long currentVersion = realm2.getVersion();
                long requiredVersion = configuration.getSchemaVersion();
                if (currentVersion != UNVERSIONED && currentVersion < requiredVersion && validateSchema) {
                    realm2.close();
                    throw new RealmMigrationNeededException(canonicalPath, String.format("Realm on disc need to migrate from v%s to v%s", new Object[]{Long.valueOf(currentVersion), Long.valueOf(requiredVersion)}));
                } else if (currentVersion == UNVERSIONED || requiredVersion >= currentVersion || !validateSchema) {
                    if (validateSchema) {
                        try {
                            initializeRealm(realm2);
                        } catch (RuntimeException e) {
                            realm2.close();
                            throw e;
                        }
                    }
                    Realm realm3 = realm2;
                } else {
                    realm2.close();
                    throw new IllegalArgumentException(String.format("Realm on disc is newer than the one specified: v%s vs. v%s", new Object[]{Long.valueOf(currentVersion), Long.valueOf(requiredVersion)}));
                }
            }
        }
        return realm;
    }

    private static void validateAgainstExistingConfigurations(RealmConfiguration newConfiguration) {
        String realmPath = newConfiguration.getPath();
        List<RealmConfiguration> pathConfigurationCache = (List) globalPathConfigurationCache.get(realmPath);
        if (pathConfigurationCache == null) {
            pathConfigurationCache = new CopyOnWriteArrayList();
            globalPathConfigurationCache.put(realmPath, pathConfigurationCache);
        }
        if (pathConfigurationCache.size() > 0) {
            RealmConfiguration cachedConfiguration = (RealmConfiguration) pathConfigurationCache.get(0);
            if (!Arrays.equals(cachedConfiguration.getEncryptionKey(), newConfiguration.getEncryptionKey())) {
                throw new IllegalArgumentException(DIFFERENT_KEY_MESSAGE);
            } else if (cachedConfiguration.getSchemaVersion() != newConfiguration.getSchemaVersion()) {
                throw new IllegalArgumentException(String.format("Configurations cannot have different schema versions if used to open the same file. %d vs. %d", new Object[]{Long.valueOf(cachedConfiguration.getSchemaVersion()), Long.valueOf(newConfiguration.getSchemaVersion())}));
            } else if (!cachedConfiguration.getSchemaMediator().equals(newConfiguration.getSchemaMediator())) {
                throw new IllegalArgumentException("Two configurations with different schemas are trying to open the same Realm file. Their schema must be the same: " + newConfiguration.getPath());
            } else if (!cachedConfiguration.getDurability().equals(newConfiguration.getDurability())) {
                throw new IllegalArgumentException("A Realm cannot be both in-memory and persisted. Two conflicting configurations pointing to " + newConfiguration.getPath() + " are being used.");
            }
        }
        pathConfigurationCache.add(newConfiguration);
    }

    private static void initializeRealm(Realm realm) {
        long version = realm.getVersion();
        boolean commitNeeded = false;
        try {
            realm.beginTransaction();
            if (version == UNVERSIONED) {
                commitNeeded = true;
                realm.setVersion(realm.configuration.getSchemaVersion());
            }
            RealmProxyMediator mediator = realm.configuration.getSchemaMediator();
            for (Class<? extends RealmObject> modelClass : mediator.getModelClasses()) {
                if (version == UNVERSIONED) {
                    mediator.createTable(modelClass, realm.transaction);
                }
                mediator.validateTable(modelClass, realm.transaction);
                realm.columnIndices.addClass(modelClass, mediator.getColumnIndices(modelClass));
            }
            if (commitNeeded) {
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
            }
        } catch (Throwable th) {
            if (null != null) {
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
            }
        }
    }

    public <E extends RealmObject> void createAllFromJson(Class<E> clazz, JSONArray json) {
        if (clazz != null && json != null) {
            int i = 0;
            while (i < json.length()) {
                try {
                    this.configuration.getSchemaMediator().createOrUpdateUsingJsonObject(clazz, this, json.getJSONObject(i), false);
                    i++;
                } catch (Exception e) {
                    throw new RealmException("Could not map Json", e);
                }
            }
        }
    }

    public <E extends RealmObject> void createOrUpdateAllFromJson(Class<E> clazz, JSONArray json) {
        if (clazz != null && json != null) {
            checkHasPrimaryKey((Class) clazz);
            int i = 0;
            while (i < json.length()) {
                try {
                    this.configuration.getSchemaMediator().createOrUpdateUsingJsonObject(clazz, this, json.getJSONObject(i), true);
                    i++;
                } catch (Exception e) {
                    throw new RealmException("Could not map Json", e);
                }
            }
        }
    }

    public <E extends RealmObject> void createAllFromJson(Class<E> clazz, String json) {
        if (clazz != null && json != null && json.length() != 0) {
            try {
                createAllFromJson((Class) clazz, new JSONArray(json));
            } catch (Exception e) {
                throw new RealmException("Could not create JSON array from string", e);
            }
        }
    }

    public <E extends RealmObject> void createOrUpdateAllFromJson(Class<E> clazz, String json) {
        if (clazz != null && json != null && json.length() != 0) {
            checkHasPrimaryKey((Class) clazz);
            try {
                createOrUpdateAllFromJson((Class) clazz, new JSONArray(json));
            } catch (JSONException e) {
                throw new RealmException("Could not create JSON array from string", e);
            }
        }
    }

    @TargetApi(11)
    public <E extends RealmObject> void createAllFromJson(Class<E> clazz, InputStream inputStream) throws IOException {
        if (clazz != null && inputStream != null) {
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, HttpRequest.CHARSET_UTF8));
            try {
                reader.beginArray();
                while (reader.hasNext()) {
                    this.configuration.getSchemaMediator().createUsingJsonStream(clazz, this, reader);
                }
                reader.endArray();
            } finally {
                reader.close();
            }
        }
    }

    @TargetApi(11)
    public <E extends RealmObject> void createOrUpdateAllFromJson(Class<E> clazz, InputStream in) throws IOException {
        if (clazz != null && in != null) {
            checkHasPrimaryKey((Class) clazz);
            Scanner scanner = null;
            try {
                scanner = getFullStringScanner(in);
                JSONArray json = new JSONArray(scanner.next());
                for (int i = 0; i < json.length(); i++) {
                    this.configuration.getSchemaMediator().createOrUpdateUsingJsonObject(clazz, this, json.getJSONObject(i), true);
                }
                if (scanner != null) {
                    scanner.close();
                }
            } catch (JSONException e) {
                throw new RealmException("Failed to read JSON", e);
            } catch (Throwable th) {
                if (scanner != null) {
                    scanner.close();
                }
            }
        }
    }

    public <E extends RealmObject> E createObjectFromJson(Class<E> clazz, JSONObject json) {
        if (clazz == null || json == null) {
            return null;
        }
        try {
            return this.configuration.getSchemaMediator().createOrUpdateUsingJsonObject(clazz, this, json, false);
        } catch (Exception e) {
            throw new RealmException("Could not map Json", e);
        }
    }

    public <E extends RealmObject> E createOrUpdateObjectFromJson(Class<E> clazz, JSONObject json) {
        if (clazz == null || json == null) {
            return null;
        }
        checkHasPrimaryKey((Class) clazz);
        try {
            return this.configuration.getSchemaMediator().createOrUpdateUsingJsonObject(clazz, this, json, true);
        } catch (JSONException e) {
            throw new RealmException("Could not map Json", e);
        }
    }

    public <E extends RealmObject> E createObjectFromJson(Class<E> clazz, String json) {
        if (clazz == null || json == null || json.length() == 0) {
            return null;
        }
        try {
            return createObjectFromJson((Class) clazz, new JSONObject(json));
        } catch (Exception e) {
            throw new RealmException("Could not create Json object from string", e);
        }
    }

    public <E extends RealmObject> E createOrUpdateObjectFromJson(Class<E> clazz, String json) {
        if (clazz == null || json == null || json.length() == 0) {
            return null;
        }
        checkHasPrimaryKey((Class) clazz);
        try {
            return createOrUpdateObjectFromJson((Class) clazz, new JSONObject(json));
        } catch (Exception e) {
            throw new RealmException("Could not create Json object from string", e);
        }
    }

    @TargetApi(11)
    public <E extends RealmObject> E createObjectFromJson(Class<E> clazz, InputStream inputStream) throws IOException {
        if (clazz == null || inputStream == null) {
            return null;
        }
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream, HttpRequest.CHARSET_UTF8));
        try {
            E createUsingJsonStream = this.configuration.getSchemaMediator().createUsingJsonStream(clazz, this, reader);
            return createUsingJsonStream;
        } finally {
            reader.close();
        }
    }

    @TargetApi(11)
    public <E extends RealmObject> E createOrUpdateObjectFromJson(Class<E> clazz, InputStream in) throws IOException {
        if (clazz == null || in == null) {
            return null;
        }
        checkHasPrimaryKey((Class) clazz);
        Scanner scanner = null;
        try {
            scanner = getFullStringScanner(in);
            E createOrUpdateUsingJsonObject = this.configuration.getSchemaMediator().createOrUpdateUsingJsonObject(clazz, this, new JSONObject(scanner.next()), true);
            if (scanner == null) {
                return createOrUpdateUsingJsonObject;
            }
            scanner.close();
            return createOrUpdateUsingJsonObject;
        } catch (JSONException e) {
            throw new RealmException("Failed to read JSON", e);
        } catch (Throwable th) {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private Scanner getFullStringScanner(InputStream in) {
        return new Scanner(in, HttpRequest.CHARSET_UTF8).useDelimiter("\\A");
    }

    public void writeCopyTo(File destination) throws IOException {
        writeEncryptedCopyTo(destination, null);
    }

    public void writeEncryptedCopyTo(File destination, byte[] key) throws IOException {
        if (destination == null) {
            throw new IllegalArgumentException("The destination argument cannot be null");
        }
        checkIfValid();
        this.transaction.writeToFile(destination, key);
    }

    public <E extends RealmObject> E createObject(Class<E> clazz) {
        return get(clazz, getTable(clazz).addEmptyRow());
    }

    <E extends RealmObject> E createObject(Class<E> clazz, Object primaryKeyValue) {
        return get(clazz, getTable(clazz).addEmptyRowWithPrimaryKey(primaryKeyValue));
    }

    void remove(Class<? extends RealmObject> clazz, long objectIndex) {
        getTable(clazz).moveLastOver(objectIndex);
    }

    <E extends RealmObject> E get(Class<E> clazz, long rowIndex) {
        UncheckedRow row = getTable(clazz).getUncheckedRow(rowIndex);
        E result = this.configuration.getSchemaMediator().newInstance(clazz);
        result.row = row;
        result.realm = this;
        return result;
    }

    public <E extends RealmObject> E copyToRealm(E object) {
        checkNotNullObject(object);
        return copyOrUpdate(object, false);
    }

    public <E extends RealmObject> E copyToRealmOrUpdate(E object) {
        checkNotNullObject(object);
        checkHasPrimaryKey(object.getClass());
        return copyOrUpdate(object, true);
    }

    public <E extends RealmObject> List<E> copyToRealm(Iterable<E> objects) {
        if (objects == null) {
            return new ArrayList();
        }
        List<E> realmObjects = new ArrayList();
        for (E object : objects) {
            realmObjects.add(copyToRealm((RealmObject) object));
        }
        return realmObjects;
    }

    public <E extends RealmObject> List<E> copyToRealmOrUpdate(Iterable<E> objects) {
        if (objects == null) {
            return new ArrayList();
        }
        List<E> realmObjects = new ArrayList();
        for (E object : objects) {
            realmObjects.add(copyToRealmOrUpdate((RealmObject) object));
        }
        return realmObjects;
    }

    boolean contains(Class<? extends RealmObject> clazz) {
        return this.configuration.getSchemaMediator().getModelClasses().contains(clazz);
    }

    public <E extends RealmObject> RealmQuery<E> where(Class<E> clazz) {
        checkIfValid();
        return new RealmQuery(this, (Class) clazz);
    }

    public <E extends RealmObject> RealmResults<E> allObjects(Class<E> clazz) {
        return where(clazz).findAll();
    }

    public <E extends RealmObject> RealmResults<E> allObjectsSorted(Class<E> clazz, String fieldName, boolean sortAscending) {
        checkIfValid();
        Table table = getTable(clazz);
        Order order = sortAscending ? Order.ascending : Order.descending;
        long columnIndex = this.columnIndices.getColumnIndex(clazz, fieldName);
        if (columnIndex >= 0) {
            return new RealmResults(this, table.getSortedView(columnIndex, order), clazz);
        }
        throw new IllegalArgumentException(String.format("Field name '%s' does not exist.", new Object[]{fieldName}));
    }

    public <E extends RealmObject> RealmResults<E> allObjectsSorted(Class<E> clazz, String fieldName1, boolean sortAscending1, String fieldName2, boolean sortAscending2) {
        return allObjectsSorted((Class) clazz, new String[]{fieldName1, fieldName2}, new boolean[]{sortAscending1, sortAscending2});
    }

    public <E extends RealmObject> RealmResults<E> allObjectsSorted(Class<E> clazz, String fieldName1, boolean sortAscending1, String fieldName2, boolean sortAscending2, String fieldName3, boolean sortAscending3) {
        return allObjectsSorted((Class) clazz, new String[]{fieldName1, fieldName2, fieldName3}, new boolean[]{sortAscending1, sortAscending2, sortAscending3});
    }

    public <E extends RealmObject> RealmResults<E> allObjectsSorted(Class<E> clazz, String[] fieldNames, boolean[] sortAscending) {
        if (fieldNames == null) {
            throw new IllegalArgumentException("fieldNames must be provided.");
        } else if (sortAscending == null) {
            throw new IllegalArgumentException("sortAscending must be provided.");
        } else {
            Table table = getTable(clazz);
            long[] columnIndices = new long[fieldNames.length];
            for (int i = 0; i < fieldNames.length; i++) {
                long columnIndex = table.getColumnIndex(fieldNames[i]);
                if (columnIndex == UNVERSIONED) {
                    throw new IllegalArgumentException(String.format("Field name '%s' does not exist.", new Object[]{fieldName}));
                }
                columnIndices[i] = columnIndex;
            }
            return new RealmResults(this, table.getSortedView(columnIndices, sortAscending), clazz);
        }
    }

    public void addChangeListener(RealmChangeListener listener) {
        checkIfValid();
        for (WeakReference<RealmChangeListener> ref : this.changeListeners) {
            if (ref.get() == listener) {
                return;
            }
        }
        this.changeListeners.add(new WeakReference(listener));
    }

    public void removeChangeListener(RealmChangeListener listener) {
        checkIfValid();
        WeakReference<RealmChangeListener> weakRefToRemove = null;
        for (WeakReference<RealmChangeListener> weakRef : this.changeListeners) {
            if (listener == weakRef.get()) {
                weakRefToRemove = weakRef;
                break;
            }
        }
        if (weakRefToRemove != null) {
            this.changeListeners.remove(weakRefToRemove);
        }
    }

    public void removeAllChangeListeners() {
        checkIfValid();
        this.changeListeners.clear();
    }

    protected List<WeakReference<RealmChangeListener>> getChangeListeners() {
        return this.changeListeners;
    }

    private void sendNotifications() {
        List<WeakReference<RealmChangeListener>> toRemoveList = null;
        for (WeakReference<RealmChangeListener> weakRef : this.changeListeners) {
            RealmChangeListener listener = (RealmChangeListener) weakRef.get();
            if (listener == null) {
                if (toRemoveList == null) {
                    toRemoveList = new ArrayList(this.changeListeners.size());
                }
                toRemoveList.add(weakRef);
            } else {
                listener.onChange();
            }
        }
        if (toRemoveList != null) {
            this.changeListeners.removeAll(toRemoveList);
        }
    }

    boolean hasChanged() {
        return this.sharedGroup.hasChanged();
    }

    public void refresh() {
        checkIfValid();
        this.transaction.advanceRead();
    }

    public void beginTransaction() {
        checkIfValid();
        this.transaction.promoteToWrite();
    }

    public void commitTransaction() {
        checkIfValid();
        this.transaction.commitAndContinueAsRead();
        for (Entry<Handler, String> handlerIntegerEntry : handlers.entrySet()) {
            Handler handler = (Handler) handlerIntegerEntry.getKey();
            String realmPath = (String) handlerIntegerEntry.getValue();
            if (handler.equals(this.handler)) {
                sendNotifications();
            } else if (realmPath.equals(this.configuration.getPath()) && !handler.hasMessages(REALM_CHANGED) && handler.getLooper().getThread().isAlive()) {
                handler.sendEmptyMessage(REALM_CHANGED);
            }
        }
    }

    public void cancelTransaction() {
        checkIfValid();
        this.transaction.rollbackAndContinueAsRead();
    }

    public void executeTransaction(Transaction transaction) {
        if (transaction != null) {
            beginTransaction();
            try {
                transaction.execute(this);
                commitTransaction();
            } catch (RuntimeException e) {
                cancelTransaction();
                throw new RealmException("Error during transaction.", e);
            } catch (Error e2) {
                cancelTransaction();
                throw e2;
            }
        }
    }

    public void clear(Class<? extends RealmObject> clazz) {
        getTable(clazz).clear();
    }

    Handler getHandler() {
        String realmPath = this.configuration.getPath();
        for (Entry<Handler, String> entry : handlers.entrySet()) {
            if (((String) entry.getValue()).equals(realmPath)) {
                return (Handler) entry.getKey();
            }
        }
        return null;
    }

    long getVersion() {
        if (this.transaction.hasTable("metadata")) {
            return this.transaction.getTable("metadata").getLong(0, 0);
        }
        return UNVERSIONED;
    }

    void setVersion(long version) {
        Table metadataTable = this.transaction.getTable("metadata");
        if (metadataTable.getColumnCount() == 0) {
            metadataTable.addColumn(ColumnType.INTEGER, ShareRequestParam.REQ_PARAM_VERSION);
            metadataTable.addEmptyRow();
        }
        metadataTable.setLong(0, 0, version);
    }

    private <E extends RealmObject> Class<? extends RealmObject> getRealmClassFromObject(E object) {
        if (object.realm != null) {
            return object.getClass().getSuperclass();
        }
        return object.getClass();
    }

    private <E extends RealmObject> E copyOrUpdate(E object, boolean update) {
        return this.configuration.getSchemaMediator().copyOrUpdate(this, object, update, new HashMap());
    }

    private <E extends RealmObject> void checkNotNullObject(E object) {
        if (object == null) {
            throw new IllegalArgumentException("Null objects cannot be copied into Realm.");
        }
    }

    private <E extends RealmObject> void checkHasPrimaryKey(E object) {
        Class<? extends RealmObject> objectClass = object.getClass();
        if (!getTable(objectClass).hasPrimaryKey()) {
            throw new IllegalArgumentException("RealmObject has no @PrimaryKey defined: " + objectClass.getSimpleName());
        }
    }

    private void checkHasPrimaryKey(Class<? extends RealmObject> clazz) {
        if (!getTable(clazz).hasPrimaryKey()) {
            throw new IllegalArgumentException("A RealmObject with no @PrimaryKey cannot be updated: " + clazz.toString());
        }
    }

    @Deprecated
    public static void migrateRealmAtPath(String realmPath, RealmMigration migration) {
        migrateRealmAtPath(realmPath, null, migration, true);
    }

    @Deprecated
    public static void migrateRealmAtPath(String realmPath, byte[] key, RealmMigration migration) {
        migrateRealmAtPath(realmPath, key, migration, true);
    }

    @Deprecated
    public static void migrateRealmAtPath(String realmPath, RealmMigration migration, boolean autoRefresh) {
        migrateRealmAtPath(realmPath, null, migration, autoRefresh);
    }

    @Deprecated
    public static synchronized void migrateRealmAtPath(String realmPath, byte[] key, RealmMigration migration, boolean autoUpdate) {
        synchronized (Realm.class) {
            File file = new File(realmPath);
            Builder configuration = new Builder(file.getParentFile()).name(file.getName()).migration(migration);
            if (key != null) {
                configuration.encryptionKey(key);
            }
            migrateRealm(configuration.build());
        }
    }

    public static synchronized void migrateRealm(RealmConfiguration configuration) {
        synchronized (Realm.class) {
            migrateRealm(configuration, null);
        }
    }

    public static void migrateRealm(RealmConfiguration configuration, RealmMigration migration) {
        boolean z = false;
        if (configuration == null) {
            throw new IllegalArgumentException("RealmConfiguration must be provided");
        } else if (migration == null && configuration.getMigration() == null) {
            throw new RealmMigrationNeededException(configuration.getPath(), "RealmMigration must be provided");
        } else {
            RealmMigration realmMigration;
            if (migration == null) {
                realmMigration = configuration.getMigration();
            } else {
                realmMigration = migration;
            }
            Realm realm = null;
            try {
                if (Looper.myLooper() != null) {
                    z = true;
                }
                realm = createAndValidate(configuration, false, z);
                realm.beginTransaction();
                realm.setVersion(realmMigration.execute(realm, realm.getVersion()));
                realm.commitTransaction();
                if (realm != null) {
                    realm.close();
                    realmsCache.remove();
                }
            } catch (Throwable th) {
                if (realm != null) {
                    realm.close();
                    realmsCache.remove();
                }
            }
        }
    }

    @Deprecated
    public static boolean deleteRealmFile(Context context) {
        return deleteRealmFile(context, DEFAULT_REALM_NAME);
    }

    @Deprecated
    public static boolean deleteRealmFile(Context context, String fileName) {
        return deleteRealm(new Builder(context).name(fileName).build());
    }

    public static synchronized boolean deleteRealm(RealmConfiguration configuration) {
        boolean result;
        synchronized (Realm.class) {
            result = true;
            AtomicInteger counter = (AtomicInteger) globalOpenInstanceCounter.get(configuration.getPath());
            if (counter == null || counter.get() <= 0) {
                File realmFolder = configuration.getRealmFolder();
                String realmFileName = configuration.getRealmFileName();
                for (File fileToDelete : Arrays.asList(new File[]{new File(configuration.getPath()), new File(realmFolder, realmFileName + ".lock"), new File(realmFolder, realmFileName + ".lock_a"), new File(realmFolder, realmFileName + ".lock_b"), new File(realmFolder, realmFileName + ".log")})) {
                    if (fileToDelete.exists() && !fileToDelete.delete()) {
                        result = false;
                        RealmLog.w("Could not delete the file " + fileToDelete);
                    }
                }
            } else {
                throw new IllegalStateException("It's not allowed to delete the file associated with an open Realm. Remember to close() all the instances of the Realm before deleting its file.");
            }
        }
        return result;
    }

    @Deprecated
    public static synchronized boolean compactRealmFile(Context context, String fileName) {
        boolean compactRealm;
        synchronized (Realm.class) {
            compactRealm = compactRealm(new Builder(context).name(fileName).build());
        }
        return compactRealm;
    }

    @Deprecated
    public static boolean compactRealmFile(Context context) {
        return compactRealm(new Builder(context).build());
    }

    public static boolean compactRealm(RealmConfiguration configuration) {
        Throwable th;
        if (configuration.getEncryptionKey() != null) {
            throw new IllegalArgumentException("Cannot currently compact an encrypted Realm.");
        }
        String canonicalPath = configuration.getPath();
        AtomicInteger openInstances = (AtomicInteger) globalOpenInstanceCounter.get(canonicalPath);
        if (openInstances == null || openInstances.get() <= 0) {
            SharedGroup sharedGroup = null;
            try {
                SharedGroup sharedGroup2 = new SharedGroup(canonicalPath, false, Durability.FULL, configuration.getEncryptionKey());
                try {
                    boolean result = sharedGroup2.compact();
                    if (sharedGroup2 != null) {
                        sharedGroup2.close();
                    }
                    return result;
                } catch (Throwable th2) {
                    th = th2;
                    sharedGroup = sharedGroup2;
                    if (sharedGroup != null) {
                        sharedGroup.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (sharedGroup != null) {
                    sharedGroup.close();
                }
                throw th;
            }
        }
        throw new IllegalStateException("Cannot compact an open Realm");
    }

    public String getPath() {
        return this.configuration.getPath();
    }

    public RealmConfiguration getConfiguration() {
        return this.configuration;
    }

    static String getCanonicalPath(File realmFile) {
        try {
            return realmFile.getCanonicalPath();
        } catch (IOException e) {
            throw new RealmException("Could not resolve the canonical path to the Realm file: " + realmFile.getAbsolutePath());
        }
    }

    public static Object getDefaultModule() {
        String moduleName = "io.realm.DefaultRealmModule";
        try {
            Constructor<?> constructor = Class.forName(moduleName).getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[0]);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (InvocationTargetException e2) {
            throw new RealmException("Could not create an instance of " + moduleName, e2);
        } catch (InstantiationException e3) {
            throw new RealmException("Could not create an instance of " + moduleName, e3);
        } catch (IllegalAccessException e4) {
            throw new RealmException("Could not create an instance of " + moduleName, e4);
        }
    }
}
