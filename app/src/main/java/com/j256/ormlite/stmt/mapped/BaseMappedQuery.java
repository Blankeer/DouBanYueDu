package com.j256.ormlite.stmt.mapped;

import com.j256.ormlite.dao.BaseForeignCollection;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.TableInfo;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseMappedQuery<T, ID> extends BaseMappedStatement<T, ID> implements GenericRowMapper<T> {
    private Map<String, Integer> columnPositions;
    private Object parent;
    private Object parentId;
    protected final FieldType[] resultsFieldTypes;

    protected BaseMappedQuery(TableInfo<T, ID> tableInfo, String statement, FieldType[] argFieldTypes, FieldType[] resultsFieldTypes) {
        super(tableInfo, statement, argFieldTypes);
        this.columnPositions = null;
        this.parent = null;
        this.parentId = null;
        this.resultsFieldTypes = resultsFieldTypes;
    }

    public T mapRow(DatabaseResults results) throws SQLException {
        Map<String, Integer> colPosMap;
        if (this.columnPositions == null) {
            colPosMap = new HashMap();
        } else {
            colPosMap = this.columnPositions;
        }
        ObjectCache objectCache = results.getObjectCache();
        if (objectCache != null) {
            T cachedInstance = objectCache.get(this.clazz, this.idField.resultToJava(results, colPosMap));
            if (cachedInstance != null) {
                return cachedInstance;
            }
        }
        T instance = this.tableInfo.createObject();
        Object id = null;
        boolean foreignCollections = false;
        for (FieldType fieldType : this.resultsFieldTypes) {
            if (fieldType.isForeignCollection()) {
                foreignCollections = true;
            } else {
                Object val = fieldType.resultToJava(results, colPosMap);
                if (val == null || this.parent == null || fieldType.getField().getType() != this.parent.getClass() || !val.equals(this.parentId)) {
                    fieldType.assignField(instance, val, false, objectCache);
                } else {
                    fieldType.assignField(instance, this.parent, true, objectCache);
                }
                if (fieldType == this.idField) {
                    id = val;
                }
            }
        }
        if (foreignCollections) {
            for (FieldType fieldType2 : this.resultsFieldTypes) {
                if (fieldType2.isForeignCollection()) {
                    BaseForeignCollection<?, ?> collection = fieldType2.buildForeignCollection(instance, id);
                    if (collection != null) {
                        fieldType2.assignField(instance, collection, false, objectCache);
                    }
                }
            }
        }
        if (!(objectCache == null || id == null)) {
            objectCache.put(this.clazz, id, instance);
        }
        if (this.columnPositions == null) {
            this.columnPositions = colPosMap;
        }
        return instance;
    }

    public void setParentInformation(Object parent, Object parentId) {
        this.parent = parent;
        this.parentId = parentId;
    }
}
