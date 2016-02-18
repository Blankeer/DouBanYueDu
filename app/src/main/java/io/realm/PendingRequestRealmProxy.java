package io.realm;

import android.util.JsonReader;
import android.util.JsonToken;
import com.douban.book.reader.fragment.WorksListFragment_;
import com.douban.book.reader.manager.sync.PendingRequest;
import io.realm.exceptions.RealmMigrationNeededException;
import io.realm.internal.ColumnType;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class PendingRequestRealmProxy extends PendingRequest implements RealmObjectProxy {
    private static final List<String> FIELD_NAMES;
    private static long INDEX_CREATETIME;
    private static long INDEX_LASTCONNECTTIME;
    private static long INDEX_LASTHTTPSTATUSCODE;
    private static long INDEX_METHOD;
    private static long INDEX_REQUESTPARAM;
    private static long INDEX_REQUESTPARAMTYPE;
    private static long INDEX_RESOURCEID;
    private static long INDEX_RESOURCETYPE;
    private static long INDEX_RETRYCOUNT;
    private static long INDEX_URI;
    private static Map<String, Long> columnIndices;

    static {
        List<String> fieldNames = new ArrayList();
        fieldNames.add("method");
        fieldNames.add(WorksListFragment_.URI_ARG);
        fieldNames.add("resourceType");
        fieldNames.add("resourceId");
        fieldNames.add("requestParam");
        fieldNames.add("requestParamType");
        fieldNames.add("createTime");
        fieldNames.add("lastHttpStatusCode");
        fieldNames.add("lastConnectTime");
        fieldNames.add("retryCount");
        FIELD_NAMES = Collections.unmodifiableList(fieldNames);
    }

    public String getMethod() {
        this.realm.checkIfValid();
        return this.row.getString(INDEX_METHOD);
    }

    public void setMethod(String value) {
        this.realm.checkIfValid();
        this.row.setString(INDEX_METHOD, value);
    }

    public String getUri() {
        this.realm.checkIfValid();
        return this.row.getString(INDEX_URI);
    }

    public void setUri(String value) {
        this.realm.checkIfValid();
        this.row.setString(INDEX_URI, value);
    }

    public String getResourceType() {
        this.realm.checkIfValid();
        return this.row.getString(INDEX_RESOURCETYPE);
    }

    public void setResourceType(String value) {
        this.realm.checkIfValid();
        this.row.setString(INDEX_RESOURCETYPE, value);
    }

    public String getResourceId() {
        this.realm.checkIfValid();
        return this.row.getString(INDEX_RESOURCEID);
    }

    public void setResourceId(String value) {
        this.realm.checkIfValid();
        this.row.setString(INDEX_RESOURCEID, value);
    }

    public String getRequestParam() {
        this.realm.checkIfValid();
        return this.row.getString(INDEX_REQUESTPARAM);
    }

    public void setRequestParam(String value) {
        this.realm.checkIfValid();
        this.row.setString(INDEX_REQUESTPARAM, value);
    }

    public String getRequestParamType() {
        this.realm.checkIfValid();
        return this.row.getString(INDEX_REQUESTPARAMTYPE);
    }

    public void setRequestParamType(String value) {
        this.realm.checkIfValid();
        this.row.setString(INDEX_REQUESTPARAMTYPE, value);
    }

    public long getCreateTime() {
        this.realm.checkIfValid();
        return this.row.getLong(INDEX_CREATETIME);
    }

    public void setCreateTime(long value) {
        this.realm.checkIfValid();
        this.row.setLong(INDEX_CREATETIME, value);
    }

    public int getLastHttpStatusCode() {
        this.realm.checkIfValid();
        return (int) this.row.getLong(INDEX_LASTHTTPSTATUSCODE);
    }

    public void setLastHttpStatusCode(int value) {
        this.realm.checkIfValid();
        this.row.setLong(INDEX_LASTHTTPSTATUSCODE, (long) value);
    }

    public long getLastConnectTime() {
        this.realm.checkIfValid();
        return this.row.getLong(INDEX_LASTCONNECTTIME);
    }

    public void setLastConnectTime(long value) {
        this.realm.checkIfValid();
        this.row.setLong(INDEX_LASTCONNECTTIME, value);
    }

    public int getRetryCount() {
        this.realm.checkIfValid();
        return (int) this.row.getLong(INDEX_RETRYCOUNT);
    }

    public void setRetryCount(int value) {
        this.realm.checkIfValid();
        this.row.setLong(INDEX_RETRYCOUNT, (long) value);
    }

    public static Table initTable(ImplicitTransaction transaction) {
        if (transaction.hasTable("class_PendingRequest")) {
            return transaction.getTable("class_PendingRequest");
        }
        Table table = transaction.getTable("class_PendingRequest");
        table.addColumn(ColumnType.STRING, "method");
        table.addColumn(ColumnType.STRING, WorksListFragment_.URI_ARG);
        table.addColumn(ColumnType.STRING, "resourceType");
        table.addColumn(ColumnType.STRING, "resourceId");
        table.addColumn(ColumnType.STRING, "requestParam");
        table.addColumn(ColumnType.STRING, "requestParamType");
        table.addColumn(ColumnType.INTEGER, "createTime");
        table.addColumn(ColumnType.INTEGER, "lastHttpStatusCode");
        table.addColumn(ColumnType.INTEGER, "lastConnectTime");
        table.addColumn(ColumnType.INTEGER, "retryCount");
        table.addSearchIndex(table.getColumnIndex("resourceType"));
        table.addSearchIndex(table.getColumnIndex("resourceId"));
        table.setPrimaryKey(Table.STRING_DEFAULT_VALUE);
        return table;
    }

    public static void validateTable(ImplicitTransaction transaction) {
        if (transaction.hasTable("class_PendingRequest")) {
            Table table = transaction.getTable("class_PendingRequest");
            if (table.getColumnCount() != 10) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Field count does not match - expected 10 but was " + table.getColumnCount());
            }
            Map<String, ColumnType> columnTypes = new HashMap();
            for (long i = 0; i < 10; i++) {
                columnTypes.put(table.getColumnName(i), table.getColumnType(i));
            }
            columnIndices = new HashMap();
            for (String fieldName : getFieldNames()) {
                long index = table.getColumnIndex(fieldName);
                if (index == -1) {
                    throw new RealmMigrationNeededException(transaction.getPath(), "Field '" + fieldName + "' not found for type PendingRequest");
                }
                columnIndices.put(fieldName, Long.valueOf(index));
            }
            INDEX_METHOD = table.getColumnIndex("method");
            INDEX_URI = table.getColumnIndex(WorksListFragment_.URI_ARG);
            INDEX_RESOURCETYPE = table.getColumnIndex("resourceType");
            INDEX_RESOURCEID = table.getColumnIndex("resourceId");
            INDEX_REQUESTPARAM = table.getColumnIndex("requestParam");
            INDEX_REQUESTPARAMTYPE = table.getColumnIndex("requestParamType");
            INDEX_CREATETIME = table.getColumnIndex("createTime");
            INDEX_LASTHTTPSTATUSCODE = table.getColumnIndex("lastHttpStatusCode");
            INDEX_LASTCONNECTTIME = table.getColumnIndex("lastConnectTime");
            INDEX_RETRYCOUNT = table.getColumnIndex("retryCount");
            if (!columnTypes.containsKey("method")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'method'");
            } else if (columnTypes.get("method") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'method'");
            } else if (!columnTypes.containsKey(WorksListFragment_.URI_ARG)) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'uri'");
            } else if (columnTypes.get(WorksListFragment_.URI_ARG) != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'uri'");
            } else if (!columnTypes.containsKey("resourceType")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'resourceType'");
            } else if (columnTypes.get("resourceType") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'resourceType'");
            } else if (!table.hasSearchIndex(table.getColumnIndex("resourceType"))) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Index not defined for field 'resourceType'");
            } else if (!columnTypes.containsKey("resourceId")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'resourceId'");
            } else if (columnTypes.get("resourceId") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'resourceId'");
            } else if (!table.hasSearchIndex(table.getColumnIndex("resourceId"))) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Index not defined for field 'resourceId'");
            } else if (!columnTypes.containsKey("requestParam")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'requestParam'");
            } else if (columnTypes.get("requestParam") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'requestParam'");
            } else if (!columnTypes.containsKey("requestParamType")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'requestParamType'");
            } else if (columnTypes.get("requestParamType") != ColumnType.STRING) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'String' for field 'requestParamType'");
            } else if (!columnTypes.containsKey("createTime")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'createTime'");
            } else if (columnTypes.get("createTime") != ColumnType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'long' for field 'createTime'");
            } else if (!columnTypes.containsKey("lastHttpStatusCode")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'lastHttpStatusCode'");
            } else if (columnTypes.get("lastHttpStatusCode") != ColumnType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'lastHttpStatusCode'");
            } else if (!columnTypes.containsKey("lastConnectTime")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'lastConnectTime'");
            } else if (columnTypes.get("lastConnectTime") != ColumnType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'long' for field 'lastConnectTime'");
            } else if (!columnTypes.containsKey("retryCount")) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Missing field 'retryCount'");
            } else if (columnTypes.get("retryCount") != ColumnType.INTEGER) {
                throw new RealmMigrationNeededException(transaction.getPath(), "Invalid type 'int' for field 'retryCount'");
            } else {
                return;
            }
        }
        throw new RealmMigrationNeededException(transaction.getPath(), "The PendingRequest class is missing from the schema for this Realm.");
    }

    public static String getTableName() {
        return "class_PendingRequest";
    }

    public static List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    public static Map<String, Long> getColumnIndices() {
        return columnIndices;
    }

    public static PendingRequest createOrUpdateUsingJsonObject(Realm realm, JSONObject json, boolean update) throws JSONException {
        PendingRequest obj = (PendingRequest) realm.createObject(PendingRequest.class);
        if (!json.isNull("method")) {
            obj.setMethod(json.getString("method"));
        }
        if (!json.isNull(WorksListFragment_.URI_ARG)) {
            obj.setUri(json.getString(WorksListFragment_.URI_ARG));
        }
        if (!json.isNull("resourceType")) {
            obj.setResourceType(json.getString("resourceType"));
        }
        if (!json.isNull("resourceId")) {
            obj.setResourceId(json.getString("resourceId"));
        }
        if (!json.isNull("requestParam")) {
            obj.setRequestParam(json.getString("requestParam"));
        }
        if (!json.isNull("requestParamType")) {
            obj.setRequestParamType(json.getString("requestParamType"));
        }
        if (!json.isNull("createTime")) {
            obj.setCreateTime(json.getLong("createTime"));
        }
        if (!json.isNull("lastHttpStatusCode")) {
            obj.setLastHttpStatusCode(json.getInt("lastHttpStatusCode"));
        }
        if (!json.isNull("lastConnectTime")) {
            obj.setLastConnectTime(json.getLong("lastConnectTime"));
        }
        if (!json.isNull("retryCount")) {
            obj.setRetryCount(json.getInt("retryCount"));
        }
        return obj;
    }

    public static PendingRequest createUsingJsonStream(Realm realm, JsonReader reader) throws IOException {
        PendingRequest obj = (PendingRequest) realm.createObject(PendingRequest.class);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("method") && reader.peek() != JsonToken.NULL) {
                obj.setMethod(reader.nextString());
            } else if (name.equals(WorksListFragment_.URI_ARG) && reader.peek() != JsonToken.NULL) {
                obj.setUri(reader.nextString());
            } else if (name.equals("resourceType") && reader.peek() != JsonToken.NULL) {
                obj.setResourceType(reader.nextString());
            } else if (name.equals("resourceId") && reader.peek() != JsonToken.NULL) {
                obj.setResourceId(reader.nextString());
            } else if (name.equals("requestParam") && reader.peek() != JsonToken.NULL) {
                obj.setRequestParam(reader.nextString());
            } else if (name.equals("requestParamType") && reader.peek() != JsonToken.NULL) {
                obj.setRequestParamType(reader.nextString());
            } else if (name.equals("createTime") && reader.peek() != JsonToken.NULL) {
                obj.setCreateTime(reader.nextLong());
            } else if (name.equals("lastHttpStatusCode") && reader.peek() != JsonToken.NULL) {
                obj.setLastHttpStatusCode(reader.nextInt());
            } else if (name.equals("lastConnectTime") && reader.peek() != JsonToken.NULL) {
                obj.setLastConnectTime(reader.nextLong());
            } else if (!name.equals("retryCount") || reader.peek() == JsonToken.NULL) {
                reader.skipValue();
            } else {
                obj.setRetryCount(reader.nextInt());
            }
        }
        reader.endObject();
        return obj;
    }

    public static PendingRequest copyOrUpdate(Realm realm, PendingRequest object, boolean update, Map<RealmObject, RealmObjectProxy> cache) {
        return (object.realm == null || !object.realm.getPath().equals(realm.getPath())) ? copy(realm, object, update, cache) : object;
    }

    public static PendingRequest copy(Realm realm, PendingRequest newObject, boolean update, Map<RealmObject, RealmObjectProxy> cache) {
        PendingRequest realmObject = (PendingRequest) realm.createObject(PendingRequest.class);
        cache.put(newObject, (RealmObjectProxy) realmObject);
        realmObject.setMethod(newObject.getMethod() != null ? newObject.getMethod() : Table.STRING_DEFAULT_VALUE);
        realmObject.setUri(newObject.getUri() != null ? newObject.getUri() : Table.STRING_DEFAULT_VALUE);
        realmObject.setResourceType(newObject.getResourceType() != null ? newObject.getResourceType() : Table.STRING_DEFAULT_VALUE);
        realmObject.setResourceId(newObject.getResourceId() != null ? newObject.getResourceId() : Table.STRING_DEFAULT_VALUE);
        realmObject.setRequestParam(newObject.getRequestParam() != null ? newObject.getRequestParam() : Table.STRING_DEFAULT_VALUE);
        realmObject.setRequestParamType(newObject.getRequestParamType() != null ? newObject.getRequestParamType() : Table.STRING_DEFAULT_VALUE);
        realmObject.setCreateTime(newObject.getCreateTime());
        realmObject.setLastHttpStatusCode(newObject.getLastHttpStatusCode());
        realmObject.setLastConnectTime(newObject.getLastConnectTime());
        realmObject.setRetryCount(newObject.getRetryCount());
        return realmObject;
    }

    static PendingRequest update(Realm realm, PendingRequest realmObject, PendingRequest newObject, Map<RealmObject, RealmObjectProxy> map) {
        realmObject.setMethod(newObject.getMethod() != null ? newObject.getMethod() : Table.STRING_DEFAULT_VALUE);
        realmObject.setUri(newObject.getUri() != null ? newObject.getUri() : Table.STRING_DEFAULT_VALUE);
        realmObject.setResourceType(newObject.getResourceType() != null ? newObject.getResourceType() : Table.STRING_DEFAULT_VALUE);
        realmObject.setResourceId(newObject.getResourceId() != null ? newObject.getResourceId() : Table.STRING_DEFAULT_VALUE);
        realmObject.setRequestParam(newObject.getRequestParam() != null ? newObject.getRequestParam() : Table.STRING_DEFAULT_VALUE);
        realmObject.setRequestParamType(newObject.getRequestParamType() != null ? newObject.getRequestParamType() : Table.STRING_DEFAULT_VALUE);
        realmObject.setCreateTime(newObject.getCreateTime());
        realmObject.setLastHttpStatusCode(newObject.getLastHttpStatusCode());
        realmObject.setLastConnectTime(newObject.getLastConnectTime());
        realmObject.setRetryCount(newObject.getRetryCount());
        return realmObject;
    }

    public String toString() {
        if (!isValid()) {
            return "Invalid object";
        }
        StringBuilder stringBuilder = new StringBuilder("PendingRequest = [");
        stringBuilder.append("{method:");
        stringBuilder.append(getMethod());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{uri:");
        stringBuilder.append(getUri());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{resourceType:");
        stringBuilder.append(getResourceType());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{resourceId:");
        stringBuilder.append(getResourceId());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{requestParam:");
        stringBuilder.append(getRequestParam());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{requestParamType:");
        stringBuilder.append(getRequestParamType());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{createTime:");
        stringBuilder.append(getCreateTime());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{lastHttpStatusCode:");
        stringBuilder.append(getLastHttpStatusCode());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{lastConnectTime:");
        stringBuilder.append(getLastConnectTime());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{retryCount:");
        stringBuilder.append(getRetryCount());
        stringBuilder.append("}");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public int hashCode() {
        int hashCode;
        int i = 0;
        String realmName = this.realm.getPath();
        String tableName = this.row.getTable().getName();
        long rowIndex = this.row.getIndex();
        if (realmName != null) {
            hashCode = realmName.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (hashCode + 527) * 31;
        if (tableName != null) {
            i = tableName.hashCode();
        }
        return ((hashCode + i) * 31) + ((int) ((rowIndex >>> 32) ^ rowIndex));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PendingRequestRealmProxy aPendingRequest = (PendingRequestRealmProxy) o;
        String path = this.realm.getPath();
        String otherPath = aPendingRequest.realm.getPath();
        if (path == null ? otherPath != null : !path.equals(otherPath)) {
            return false;
        }
        String tableName = this.row.getTable().getName();
        String otherTableName = aPendingRequest.row.getTable().getName();
        if (tableName == null ? otherTableName != null : !tableName.equals(otherTableName)) {
            return false;
        }
        if (this.row.getIndex() != aPendingRequest.row.getIndex()) {
            return false;
        }
        return true;
    }
}
