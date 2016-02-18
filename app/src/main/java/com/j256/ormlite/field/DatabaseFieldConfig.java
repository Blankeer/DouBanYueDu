package com.j256.ormlite.field;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.types.VoidType;
import com.j256.ormlite.misc.JavaxPersistence;
import com.j256.ormlite.table.DatabaseTableConfig;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class DatabaseFieldConfig {
    public static final boolean DEFAULT_CAN_BE_NULL = true;
    public static final DataType DEFAULT_DATA_TYPE;
    public static final boolean DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING = true;
    private static final int DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL = 1;
    public static final Class<? extends DataPersister> DEFAULT_PERSISTER_CLASS;
    private boolean allowGeneratedIdInsert;
    private boolean canBeNull;
    private String columnDefinition;
    private String columnName;
    private DataPersister dataPersister;
    private DataType dataType;
    private String defaultValue;
    private String fieldName;
    private boolean foreign;
    private boolean foreignAutoCreate;
    private boolean foreignAutoRefresh;
    private boolean foreignCollection;
    private String foreignCollectionColumnName;
    private boolean foreignCollectionEager;
    private String foreignCollectionForeignFieldName;
    private int foreignCollectionMaxEagerLevel;
    private boolean foreignCollectionOrderAscending;
    private String foreignCollectionOrderColumnName;
    private String foreignColumnName;
    private DatabaseTableConfig<?> foreignTableConfig;
    private String format;
    private boolean generatedId;
    private String generatedIdSequence;
    private boolean id;
    private boolean index;
    private String indexName;
    private int maxForeignAutoRefreshLevel;
    private boolean persisted;
    private Class<? extends DataPersister> persisterClass;
    private boolean readOnly;
    private boolean throwIfNull;
    private boolean unique;
    private boolean uniqueCombo;
    private boolean uniqueIndex;
    private String uniqueIndexName;
    private Enum<?> unknownEnumValue;
    private boolean useGetSet;
    private boolean version;
    private int width;

    static {
        DEFAULT_PERSISTER_CLASS = VoidType.class;
        DEFAULT_DATA_TYPE = DataType.UNKNOWN;
    }

    public DatabaseFieldConfig() {
        this.dataType = DEFAULT_DATA_TYPE;
        this.canBeNull = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
        this.persisted = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
        this.maxForeignAutoRefreshLevel = -1;
        this.persisterClass = DEFAULT_PERSISTER_CLASS;
        this.foreignCollectionMaxEagerLevel = DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL;
        this.foreignCollectionOrderAscending = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
    }

    public DatabaseFieldConfig(String fieldName) {
        this.dataType = DEFAULT_DATA_TYPE;
        this.canBeNull = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
        this.persisted = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
        this.maxForeignAutoRefreshLevel = -1;
        this.persisterClass = DEFAULT_PERSISTER_CLASS;
        this.foreignCollectionMaxEagerLevel = DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL;
        this.foreignCollectionOrderAscending = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
        this.fieldName = fieldName;
    }

    public DatabaseFieldConfig(String fieldName, String columnName, DataType dataType, String defaultValue, int width, boolean canBeNull, boolean id, boolean generatedId, String generatedIdSequence, boolean foreign, DatabaseTableConfig<?> foreignTableConfig, boolean useGetSet, Enum<?> unknownEnumValue, boolean throwIfNull, String format, boolean unique, String indexName, String uniqueIndexName, boolean autoRefresh, int maxForeignAutoRefreshLevel, int maxForeignCollectionLevel) {
        this.dataType = DEFAULT_DATA_TYPE;
        this.canBeNull = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
        this.persisted = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
        this.maxForeignAutoRefreshLevel = -1;
        this.persisterClass = DEFAULT_PERSISTER_CLASS;
        this.foreignCollectionMaxEagerLevel = DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL;
        this.foreignCollectionOrderAscending = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.dataType = DataType.UNKNOWN;
        this.defaultValue = defaultValue;
        this.width = width;
        this.canBeNull = canBeNull;
        this.id = id;
        this.generatedId = generatedId;
        this.generatedIdSequence = generatedIdSequence;
        this.foreign = foreign;
        this.foreignTableConfig = foreignTableConfig;
        this.useGetSet = useGetSet;
        this.unknownEnumValue = unknownEnumValue;
        this.throwIfNull = throwIfNull;
        this.format = format;
        this.unique = unique;
        this.indexName = indexName;
        this.uniqueIndexName = uniqueIndexName;
        this.foreignAutoRefresh = autoRefresh;
        this.maxForeignAutoRefreshLevel = maxForeignAutoRefreshLevel;
        this.foreignCollectionMaxEagerLevel = maxForeignCollectionLevel;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public DataPersister getDataPersister() {
        if (this.dataPersister == null) {
            return this.dataType.getDataPersister();
        }
        return this.dataPersister;
    }

    public void setDataPersister(DataPersister dataPersister) {
        this.dataPersister = dataPersister;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isCanBeNull() {
        return this.canBeNull;
    }

    public void setCanBeNull(boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    public boolean isId() {
        return this.id;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public boolean isGeneratedId() {
        return this.generatedId;
    }

    public void setGeneratedId(boolean generatedId) {
        this.generatedId = generatedId;
    }

    public String getGeneratedIdSequence() {
        return this.generatedIdSequence;
    }

    public void setGeneratedIdSequence(String generatedIdSequence) {
        this.generatedIdSequence = generatedIdSequence;
    }

    public boolean isForeign() {
        return this.foreign;
    }

    public void setForeign(boolean foreign) {
        this.foreign = foreign;
    }

    public DatabaseTableConfig<?> getForeignTableConfig() {
        return this.foreignTableConfig;
    }

    public void setForeignTableConfig(DatabaseTableConfig<?> foreignTableConfig) {
        this.foreignTableConfig = foreignTableConfig;
    }

    public boolean isUseGetSet() {
        return this.useGetSet;
    }

    public void setUseGetSet(boolean useGetSet) {
        this.useGetSet = useGetSet;
    }

    public Enum<?> getUnknownEnumValue() {
        return this.unknownEnumValue;
    }

    public void setUnknownEnumValue(Enum<?> unknownEnumValue) {
        this.unknownEnumValue = unknownEnumValue;
    }

    public boolean isThrowIfNull() {
        return this.throwIfNull;
    }

    public void setThrowIfNull(boolean throwIfNull) {
        this.throwIfNull = throwIfNull;
    }

    public boolean isPersisted() {
        return this.persisted;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isUniqueCombo() {
        return this.uniqueCombo;
    }

    public void setUniqueCombo(boolean uniqueCombo) {
        this.uniqueCombo = uniqueCombo;
    }

    public boolean isIndex() {
        return this.index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }

    public String getIndexName(String tableName) {
        if (this.index && this.indexName == null) {
            this.indexName = findIndexName(tableName);
        }
        return this.indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public boolean isUniqueIndex() {
        return this.uniqueIndex;
    }

    public void setUniqueIndex(boolean uniqueIndex) {
        this.uniqueIndex = uniqueIndex;
    }

    public String getUniqueIndexName(String tableName) {
        if (this.uniqueIndex && this.uniqueIndexName == null) {
            this.uniqueIndexName = findIndexName(tableName);
        }
        return this.uniqueIndexName;
    }

    public void setUniqueIndexName(String uniqueIndexName) {
        this.uniqueIndexName = uniqueIndexName;
    }

    public void setForeignAutoRefresh(boolean foreignAutoRefresh) {
        this.foreignAutoRefresh = foreignAutoRefresh;
    }

    public boolean isForeignAutoRefresh() {
        return this.foreignAutoRefresh;
    }

    public int getMaxForeignAutoRefreshLevel() {
        return this.maxForeignAutoRefreshLevel;
    }

    public void setMaxForeignAutoRefreshLevel(int maxForeignLevel) {
        this.maxForeignAutoRefreshLevel = maxForeignLevel;
    }

    public boolean isForeignCollection() {
        return this.foreignCollection;
    }

    public void setForeignCollection(boolean foreignCollection) {
        this.foreignCollection = foreignCollection;
    }

    public boolean isForeignCollectionEager() {
        return this.foreignCollectionEager;
    }

    public void setForeignCollectionEager(boolean foreignCollectionEager) {
        this.foreignCollectionEager = foreignCollectionEager;
    }

    public int getForeignCollectionMaxEagerLevel() {
        return this.foreignCollectionMaxEagerLevel;
    }

    public void setForeignCollectionMaxEagerLevel(int foreignCollectionMaxEagerLevel) {
        this.foreignCollectionMaxEagerLevel = foreignCollectionMaxEagerLevel;
    }

    @Deprecated
    public void setMaxEagerForeignCollectionLevel(int maxEagerForeignCollectionLevel) {
        this.foreignCollectionMaxEagerLevel = maxEagerForeignCollectionLevel;
    }

    @Deprecated
    public void setForeignCollectionMaxEagerForeignCollectionLevel(int maxEagerForeignCollectionLevel) {
        this.foreignCollectionMaxEagerLevel = maxEagerForeignCollectionLevel;
    }

    public String getForeignCollectionColumnName() {
        return this.foreignCollectionColumnName;
    }

    public void setForeignCollectionColumnName(String foreignCollectionColumn) {
        this.foreignCollectionColumnName = foreignCollectionColumn;
    }

    public String getForeignCollectionOrderColumnName() {
        return this.foreignCollectionOrderColumnName;
    }

    @Deprecated
    public void setForeignCollectionOrderColumn(String foreignCollectionOrderColumn) {
        this.foreignCollectionOrderColumnName = foreignCollectionOrderColumn;
    }

    public void setForeignCollectionOrderColumnName(String foreignCollectionOrderColumn) {
        this.foreignCollectionOrderColumnName = foreignCollectionOrderColumn;
    }

    public boolean isForeignCollectionOrderAscending() {
        return this.foreignCollectionOrderAscending;
    }

    public void setForeignCollectionOrderAscending(boolean foreignCollectionOrderAscending) {
        this.foreignCollectionOrderAscending = foreignCollectionOrderAscending;
    }

    public String getForeignCollectionForeignFieldName() {
        return this.foreignCollectionForeignFieldName;
    }

    @Deprecated
    public void setForeignCollectionForeignColumnName(String foreignCollectionForeignColumnName) {
        this.foreignCollectionForeignFieldName = foreignCollectionForeignColumnName;
    }

    public void setForeignCollectionForeignFieldName(String foreignCollectionForeignFieldName) {
        this.foreignCollectionForeignFieldName = foreignCollectionForeignFieldName;
    }

    public Class<? extends DataPersister> getPersisterClass() {
        return this.persisterClass;
    }

    public void setPersisterClass(Class<? extends DataPersister> persisterClass) {
        this.persisterClass = persisterClass;
    }

    public boolean isAllowGeneratedIdInsert() {
        return this.allowGeneratedIdInsert;
    }

    public void setAllowGeneratedIdInsert(boolean allowGeneratedIdInsert) {
        this.allowGeneratedIdInsert = allowGeneratedIdInsert;
    }

    public String getColumnDefinition() {
        return this.columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public boolean isForeignAutoCreate() {
        return this.foreignAutoCreate;
    }

    public void setForeignAutoCreate(boolean foreignAutoCreate) {
        this.foreignAutoCreate = foreignAutoCreate;
    }

    public boolean isVersion() {
        return this.version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public String getForeignColumnName() {
        return this.foreignColumnName;
    }

    public void setForeignColumnName(String foreignColumnName) {
        this.foreignColumnName = foreignColumnName;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public static DatabaseFieldConfig fromField(DatabaseType databaseType, String tableName, Field field) throws SQLException {
        DatabaseField databaseField = (DatabaseField) field.getAnnotation(DatabaseField.class);
        if (databaseField == null) {
            ForeignCollectionField foreignCollection = (ForeignCollectionField) field.getAnnotation(ForeignCollectionField.class);
            if (foreignCollection != null) {
                return fromForeignCollection(databaseType, field, foreignCollection);
            }
            return JavaxPersistence.createFieldConfig(databaseType, field);
        } else if (databaseField.persisted()) {
            return fromDatabaseField(databaseType, tableName, field, databaseField);
        } else {
            return null;
        }
    }

    public static Method findGetMethod(Field field, boolean throwExceptions) {
        String methodName = methodFromField(field, "get");
        try {
            Method method = field.getDeclaringClass().getMethod(methodName, new Class[0]);
            if (method.getReturnType() == field.getType()) {
                return method;
            }
            if (!throwExceptions) {
                return null;
            }
            throw new IllegalArgumentException("Return type of get method " + methodName + " does not return " + field.getType());
        } catch (Exception e) {
            if (!throwExceptions) {
                return null;
            }
            throw new IllegalArgumentException("Could not find appropriate get method for " + field);
        }
    }

    public static Method findSetMethod(Field field, boolean throwExceptions) {
        String methodName = methodFromField(field, "set");
        try {
            Class declaringClass = field.getDeclaringClass();
            Class[] clsArr = new Class[DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL];
            clsArr[0] = field.getType();
            Method method = declaringClass.getMethod(methodName, clsArr);
            if (method.getReturnType() == Void.TYPE) {
                return method;
            }
            if (!throwExceptions) {
                return null;
            }
            throw new IllegalArgumentException("Return type of set method " + methodName + " returns " + method.getReturnType() + " instead of void");
        } catch (Exception e) {
            if (!throwExceptions) {
                return null;
            }
            throw new IllegalArgumentException("Could not find appropriate set method for " + field);
        }
    }

    public static DatabaseFieldConfig fromDatabaseField(DatabaseType databaseType, String tableName, Field field, DatabaseField databaseField) {
        DatabaseFieldConfig config = new DatabaseFieldConfig();
        config.fieldName = field.getName();
        if (databaseType.isEntityNamesMustBeUpCase()) {
            config.fieldName = config.fieldName.toUpperCase();
        }
        config.columnName = valueIfNotBlank(databaseField.columnName());
        config.dataType = databaseField.dataType();
        String defaultValue = databaseField.defaultValue();
        if (!defaultValue.equals(DatabaseField.DEFAULT_STRING)) {
            config.defaultValue = defaultValue;
        }
        config.width = databaseField.width();
        config.canBeNull = databaseField.canBeNull();
        config.id = databaseField.id();
        config.generatedId = databaseField.generatedId();
        config.generatedIdSequence = valueIfNotBlank(databaseField.generatedIdSequence());
        config.foreign = databaseField.foreign();
        config.useGetSet = databaseField.useGetSet();
        config.unknownEnumValue = findMatchingEnumVal(field, databaseField.unknownEnumName());
        config.throwIfNull = databaseField.throwIfNull();
        config.format = valueIfNotBlank(databaseField.format());
        config.unique = databaseField.unique();
        config.uniqueCombo = databaseField.uniqueCombo();
        config.index = databaseField.index();
        config.indexName = valueIfNotBlank(databaseField.indexName());
        config.uniqueIndex = databaseField.uniqueIndex();
        config.uniqueIndexName = valueIfNotBlank(databaseField.uniqueIndexName());
        config.foreignAutoRefresh = databaseField.foreignAutoRefresh();
        config.maxForeignAutoRefreshLevel = databaseField.maxForeignAutoRefreshLevel();
        config.persisterClass = databaseField.persisterClass();
        config.allowGeneratedIdInsert = databaseField.allowGeneratedIdInsert();
        config.columnDefinition = valueIfNotBlank(databaseField.columnDefinition());
        config.foreignAutoCreate = databaseField.foreignAutoCreate();
        config.version = databaseField.version();
        config.foreignColumnName = valueIfNotBlank(databaseField.foreignColumnName());
        config.readOnly = databaseField.readOnly();
        return config;
    }

    public void postProcess() {
        if (this.foreignColumnName != null) {
            this.foreignAutoRefresh = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
        }
        if (this.foreignAutoRefresh && this.maxForeignAutoRefreshLevel == -1) {
            this.maxForeignAutoRefreshLevel = 2;
        }
    }

    public static Enum<?> findMatchingEnumVal(Field field, String unknownEnumName) {
        if (unknownEnumName == null || unknownEnumName.length() == 0) {
            return null;
        }
        Enum[] arr$ = (Enum[]) field.getType().getEnumConstants();
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL) {
            Enum<?> enumVal = arr$[i$];
            if (enumVal.name().equals(unknownEnumName)) {
                return enumVal;
            }
        }
        throw new IllegalArgumentException("Unknwown enum unknown name " + unknownEnumName + " for field " + field);
    }

    private static DatabaseFieldConfig fromForeignCollection(DatabaseType databaseType, Field field, ForeignCollectionField foreignCollection) {
        DatabaseFieldConfig config = new DatabaseFieldConfig();
        config.fieldName = field.getName();
        if (foreignCollection.columnName().length() > 0) {
            config.columnName = foreignCollection.columnName();
        }
        config.foreignCollection = DEFAULT_FOREIGN_COLLECTION_ORDER_ASCENDING;
        config.foreignCollectionEager = foreignCollection.eager();
        int maxEagerLevel = foreignCollection.maxEagerForeignCollectionLevel();
        if (maxEagerLevel != DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL) {
            config.foreignCollectionMaxEagerLevel = maxEagerLevel;
        } else {
            config.foreignCollectionMaxEagerLevel = foreignCollection.maxEagerLevel();
        }
        config.foreignCollectionOrderColumnName = valueIfNotBlank(foreignCollection.orderColumnName());
        config.foreignCollectionOrderAscending = foreignCollection.orderAscending();
        config.foreignCollectionColumnName = valueIfNotBlank(foreignCollection.columnName());
        String foreignFieldName = valueIfNotBlank(foreignCollection.foreignFieldName());
        if (foreignFieldName == null) {
            config.foreignCollectionForeignFieldName = valueIfNotBlank(valueIfNotBlank(foreignCollection.foreignColumnName()));
        } else {
            config.foreignCollectionForeignFieldName = foreignFieldName;
        }
        return config;
    }

    private String findIndexName(String tableName) {
        if (this.columnName == null) {
            return tableName + EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR + this.fieldName + "_idx";
        }
        return tableName + EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR + this.columnName + "_idx";
    }

    private static String valueIfNotBlank(String newValue) {
        if (newValue == null || newValue.length() == 0) {
            return null;
        }
        return newValue;
    }

    private static String methodFromField(Field field, String prefix) {
        return prefix + field.getName().substring(0, DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL).toUpperCase() + field.getName().substring(DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL);
    }
}
