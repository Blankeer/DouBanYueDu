package com.j256.ormlite.field;

import com.douban.book.reader.constant.Char;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.BaseForeignCollection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.EagerForeignCollection;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.LazyForeignCollection;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.types.VoidType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.stmt.mapped.MappedQueryForId;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableInfo;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public class FieldType {
    private static boolean DEFAULT_VALUE_BOOLEAN = false;
    private static byte DEFAULT_VALUE_BYTE = (byte) 0;
    private static char DEFAULT_VALUE_CHAR = '\u0000';
    private static double DEFAULT_VALUE_DOUBLE = 0.0d;
    private static float DEFAULT_VALUE_FLOAT = 0.0f;
    private static int DEFAULT_VALUE_INT = 0;
    private static long DEFAULT_VALUE_LONG = 0;
    private static short DEFAULT_VALUE_SHORT = (short) 0;
    public static final String FOREIGN_ID_FIELD_SUFFIX = "_id";
    private static final ThreadLocal<LevelCounters> threadLevelCounters;
    private final String columnName;
    private final ConnectionSource connectionSource;
    private DataPersister dataPersister;
    private Object dataTypeConfigObj;
    private Object defaultValue;
    private final Field field;
    private final DatabaseFieldConfig fieldConfig;
    private FieldConverter fieldConverter;
    private final Method fieldGetMethod;
    private final Method fieldSetMethod;
    private BaseDaoImpl<?, ?> foreignDao;
    private FieldType foreignFieldType;
    private FieldType foreignIdField;
    private TableInfo<?, ?> foreignTableInfo;
    private final String generatedIdSequence;
    private final boolean isGeneratedId;
    private final boolean isId;
    private MappedQueryForId<Object, Object> mappedQueryForId;
    private final Class<?> parentClass;
    private final String tableName;

    private static class LevelCounters {
        int autoRefreshLevel;
        int autoRefreshLevelMax;
        int foreignCollectionLevel;
        int foreignCollectionLevelMax;

        private LevelCounters() {
        }
    }

    static {
        threadLevelCounters = new ThreadLocal<LevelCounters>() {
            protected LevelCounters initialValue() {
                return new LevelCounters();
            }
        };
    }

    public FieldType(ConnectionSource connectionSource, String tableName, Field field, DatabaseFieldConfig fieldConfig, Class<?> parentClass) throws SQLException {
        this.connectionSource = connectionSource;
        this.tableName = tableName;
        DatabaseType databaseType = connectionSource.getDatabaseType();
        this.field = field;
        this.parentClass = parentClass;
        fieldConfig.postProcess();
        Class<?> clazz = field.getType();
        DataPersister dataPersister;
        if (fieldConfig.getDataPersister() == null) {
            Class<? extends DataPersister> persisterClass = fieldConfig.getPersisterClass();
            if (persisterClass == null || persisterClass == VoidType.class) {
                dataPersister = DataPersisterManager.lookupForField(field);
            } else {
                try {
                    String str = "getSingleton";
                    try {
                        Object result = persisterClass.getDeclaredMethod(r16, new Class[0]).invoke(null, new Object[0]);
                        if (result == null) {
                            throw new SQLException("Static getSingleton method should not return null on class " + persisterClass);
                        }
                        try {
                            dataPersister = (DataPersister) result;
                        } catch (Exception e) {
                            throw SqlExceptionUtil.create("Could not cast result of static getSingleton method to DataPersister from class " + persisterClass, e);
                        }
                    } catch (InvocationTargetException e2) {
                        throw SqlExceptionUtil.create("Could not run getSingleton method on class " + persisterClass, e2.getTargetException());
                    } catch (Exception e3) {
                        throw SqlExceptionUtil.create("Could not run getSingleton method on class " + persisterClass, e3);
                    }
                } catch (Exception e32) {
                    throw SqlExceptionUtil.create("Could not find getSingleton static method on class " + persisterClass, e32);
                }
            }
        }
        dataPersister = fieldConfig.getDataPersister();
        if (!dataPersister.isValidForField(field)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Field class ").append(clazz.getName());
            sb.append(" for field ").append(this);
            sb.append(" is not valid for type ").append(dataPersister);
            Class<?> primaryClass = dataPersister.getPrimaryClass();
            if (primaryClass != null) {
                sb.append(", maybe should be " + primaryClass);
            }
            throw new IllegalArgumentException(sb.toString());
        }
        String foreignColumnName = fieldConfig.getForeignColumnName();
        String defaultFieldName = field.getName();
        if (fieldConfig.isForeign() || fieldConfig.isForeignAutoRefresh() || foreignColumnName != null) {
            if (dataPersister == null || !dataPersister.isPrimitive()) {
                if (foreignColumnName == null) {
                    defaultFieldName = defaultFieldName + FOREIGN_ID_FIELD_SUFFIX;
                } else {
                    defaultFieldName = defaultFieldName + EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR + foreignColumnName;
                }
                if (ForeignCollection.class.isAssignableFrom(clazz)) {
                    throw new SQLException("Field '" + field.getName() + "' in class " + clazz + "' should use the @" + ForeignCollectionField.class.getSimpleName() + " annotation not foreign=true");
                }
            }
            throw new IllegalArgumentException("Field " + this + " is a primitive class " + clazz + " but marked as foreign");
        } else if (fieldConfig.isForeignCollection()) {
            if (clazz != Collection.class) {
                if (!ForeignCollection.class.isAssignableFrom(clazz)) {
                    throw new SQLException("Field class for '" + field.getName() + "' must be of class " + ForeignCollection.class.getSimpleName() + " or Collection.");
                }
            }
            Type type = field.getGenericType();
            if (!(type instanceof ParameterizedType)) {
                throw new SQLException("Field class for '" + field.getName() + "' must be a parameterized Collection.");
            } else if (((ParameterizedType) type).getActualTypeArguments().length == 0) {
                throw new SQLException("Field class for '" + field.getName() + "' must be a parameterized Collection with at least 1 type.");
            }
        } else if (dataPersister == null && !fieldConfig.isForeignCollection()) {
            if (byte[].class.isAssignableFrom(clazz)) {
                throw new SQLException("ORMLite does not know how to store " + clazz + " for field '" + field.getName() + "'. byte[] fields must specify dataType=DataType.BYTE_ARRAY or SERIALIZABLE");
            }
            if (Serializable.class.isAssignableFrom(clazz)) {
                throw new SQLException("ORMLite does not know how to store " + clazz + " for field '" + field.getName() + "'.  Use another class, custom persister, or to serialize it use " + "dataType=DataType.SERIALIZABLE");
            } else {
                throw new IllegalArgumentException("ORMLite does not know how to store " + clazz + " for field " + field.getName() + ". Use another class or a custom persister.");
            }
        }
        if (fieldConfig.getColumnName() == null) {
            this.columnName = defaultFieldName;
        } else {
            this.columnName = fieldConfig.getColumnName();
        }
        this.fieldConfig = fieldConfig;
        if (fieldConfig.isId()) {
            if (fieldConfig.isGeneratedId() || fieldConfig.getGeneratedIdSequence() != null) {
                throw new IllegalArgumentException("Must specify one of id, generatedId, and generatedIdSequence with " + field.getName());
            }
            this.isId = true;
            this.isGeneratedId = false;
            this.generatedIdSequence = null;
        } else if (fieldConfig.isGeneratedId()) {
            if (fieldConfig.getGeneratedIdSequence() != null) {
                throw new IllegalArgumentException("Must specify one of id, generatedId, and generatedIdSequence with " + field.getName());
            }
            this.isId = true;
            this.isGeneratedId = true;
            if (databaseType.isIdSequenceNeeded()) {
                this.generatedIdSequence = databaseType.generateIdSequenceName(tableName, this);
            } else {
                this.generatedIdSequence = null;
            }
        } else if (fieldConfig.getGeneratedIdSequence() != null) {
            this.isId = true;
            this.isGeneratedId = true;
            String seqName = fieldConfig.getGeneratedIdSequence();
            if (databaseType.isEntityNamesMustBeUpCase()) {
                seqName = seqName.toUpperCase();
            }
            this.generatedIdSequence = seqName;
        } else {
            this.isId = false;
            this.isGeneratedId = false;
            this.generatedIdSequence = null;
        }
        if (this.isId && (fieldConfig.isForeign() || fieldConfig.isForeignAutoRefresh())) {
            throw new IllegalArgumentException("Id field " + field.getName() + " cannot also be a foreign object");
        }
        if (fieldConfig.isUseGetSet()) {
            this.fieldGetMethod = DatabaseFieldConfig.findGetMethod(field, true);
            this.fieldSetMethod = DatabaseFieldConfig.findSetMethod(field, true);
        } else {
            if (!field.isAccessible()) {
                try {
                    this.field.setAccessible(true);
                } catch (SecurityException e4) {
                    throw new IllegalArgumentException("Could not open access to field " + field.getName() + ".  You may have to set useGetSet=true to fix.");
                }
            }
            this.fieldGetMethod = null;
            this.fieldSetMethod = null;
        }
        if (fieldConfig.isAllowGeneratedIdInsert() && !fieldConfig.isGeneratedId()) {
            throw new IllegalArgumentException("Field " + field.getName() + " must be a generated-id if allowGeneratedIdInsert = true");
        } else if (fieldConfig.isForeignAutoRefresh() && !fieldConfig.isForeign()) {
            throw new IllegalArgumentException("Field " + field.getName() + " must have foreign = true if foreignAutoRefresh = true");
        } else if (fieldConfig.isForeignAutoCreate() && !fieldConfig.isForeign()) {
            throw new IllegalArgumentException("Field " + field.getName() + " must have foreign = true if foreignAutoCreate = true");
        } else if (fieldConfig.getForeignColumnName() != null && !fieldConfig.isForeign()) {
            throw new IllegalArgumentException("Field " + field.getName() + " must have foreign = true if foreignColumnName is set");
        } else if (fieldConfig.isVersion() && (dataPersister == null || !dataPersister.isValidForVersion())) {
            throw new IllegalArgumentException("Field " + field.getName() + " is not a valid type to be a version field");
        } else if (fieldConfig.getMaxForeignAutoRefreshLevel() <= 0 || fieldConfig.isForeignAutoRefresh()) {
            assignDataType(databaseType, dataPersister);
        } else {
            throw new IllegalArgumentException("Field " + field.getName() + " has maxForeignAutoRefreshLevel set but not foreignAutoRefresh is false");
        }
    }

    public void configDaoInformation(ConnectionSource connectionSource, Class<?> parentClass) throws SQLException {
        BaseDaoImpl<?, ?> foreignDao;
        TableInfo<?, ?> foreignTableInfo;
        FieldType foreignIdField;
        MappedQueryForId<Object, Object> mappedQueryForId;
        FieldType foreignFieldType;
        Class fieldClass = this.field.getType();
        DatabaseType databaseType = connectionSource.getDatabaseType();
        String foreignColumnName = this.fieldConfig.getForeignColumnName();
        DatabaseTableConfig tableConfig;
        if (this.fieldConfig.isForeignAutoRefresh() || foreignColumnName != null) {
            tableConfig = this.fieldConfig.getForeignTableConfig();
            if (tableConfig == null) {
                foreignDao = (BaseDaoImpl) DaoManager.createDao(connectionSource, fieldClass);
                foreignTableInfo = foreignDao.getTableInfo();
            } else {
                tableConfig.extractFieldTypes(connectionSource);
                foreignDao = (BaseDaoImpl) DaoManager.createDao(connectionSource, tableConfig);
                foreignTableInfo = foreignDao.getTableInfo();
            }
            if (foreignColumnName == null) {
                foreignIdField = foreignTableInfo.getIdField();
                if (foreignIdField == null) {
                    throw new IllegalArgumentException("Foreign field " + fieldClass + " does not have id field");
                }
            }
            foreignIdField = foreignTableInfo.getFieldTypeByColumnName(foreignColumnName);
            if (foreignIdField == null) {
                throw new IllegalArgumentException("Foreign field " + fieldClass + " does not have field named '" + foreignColumnName + "'");
            }
            mappedQueryForId = MappedQueryForId.build(databaseType, foreignTableInfo, foreignIdField);
            foreignFieldType = null;
        } else {
            if (this.fieldConfig.isForeign()) {
                if (this.dataPersister != null) {
                    if (this.dataPersister.isPrimitive()) {
                        throw new IllegalArgumentException("Field " + this + " is a primitive class " + fieldClass + " but marked as foreign");
                    }
                }
                tableConfig = this.fieldConfig.getForeignTableConfig();
                if (tableConfig != null) {
                    tableConfig.extractFieldTypes(connectionSource);
                    foreignDao = (BaseDaoImpl) DaoManager.createDao(connectionSource, tableConfig);
                } else {
                    foreignDao = (BaseDaoImpl) DaoManager.createDao(connectionSource, fieldClass);
                }
                foreignTableInfo = foreignDao.getTableInfo();
                foreignIdField = foreignTableInfo.getIdField();
                if (foreignIdField == null) {
                    throw new IllegalArgumentException("Foreign field " + fieldClass + " does not have id field");
                } else if (!isForeignAutoCreate() || foreignIdField.isGeneratedId()) {
                    foreignFieldType = null;
                    mappedQueryForId = null;
                } else {
                    throw new IllegalArgumentException("Field " + this.field.getName() + ", if foreignAutoCreate = true then class " + fieldClass.getSimpleName() + " must have id field with generatedId = true");
                }
            }
            if (this.fieldConfig.isForeignCollection()) {
                if (fieldClass != Collection.class) {
                    if (!ForeignCollection.class.isAssignableFrom(fieldClass)) {
                        throw new SQLException("Field class for '" + this.field.getName() + "' must be of class " + ForeignCollection.class.getSimpleName() + " or Collection.");
                    }
                }
                Type type = this.field.getGenericType();
                if (type instanceof ParameterizedType) {
                    Type[] genericArguments = ((ParameterizedType) type).getActualTypeArguments();
                    if (genericArguments.length == 0) {
                        throw new SQLException("Field class for '" + this.field.getName() + "' must be a parameterized Collection with at least 1 type.");
                    }
                    if (genericArguments[0] instanceof Class) {
                        BaseDaoImpl<Object, Object> foundDao;
                        Class collectionClazz = genericArguments[0];
                        tableConfig = this.fieldConfig.getForeignTableConfig();
                        if (tableConfig == null) {
                            foundDao = (BaseDaoImpl) DaoManager.createDao(connectionSource, collectionClazz);
                        } else {
                            foundDao = (BaseDaoImpl) DaoManager.createDao(connectionSource, tableConfig);
                        }
                        foreignDao = foundDao;
                        foreignFieldType = findForeignFieldType(collectionClazz, parentClass, foundDao);
                        foreignIdField = null;
                        foreignTableInfo = null;
                        mappedQueryForId = null;
                    } else {
                        throw new SQLException("Field class for '" + this.field.getName() + "' must be a parameterized Collection whose generic argument is an entity class not: " + genericArguments[0]);
                    }
                }
                throw new SQLException("Field class for '" + this.field.getName() + "' must be a parameterized Collection.");
            }
            foreignTableInfo = null;
            foreignIdField = null;
            foreignFieldType = null;
            foreignDao = null;
            mappedQueryForId = null;
        }
        this.mappedQueryForId = mappedQueryForId;
        this.foreignTableInfo = foreignTableInfo;
        this.foreignFieldType = foreignFieldType;
        this.foreignDao = foreignDao;
        this.foreignIdField = foreignIdField;
        if (this.foreignIdField != null) {
            assignDataType(databaseType, this.foreignIdField.getDataPersister());
        }
    }

    public Field getField() {
        return this.field;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getFieldName() {
        return this.field.getName();
    }

    public Class<?> getType() {
        return this.field.getType();
    }

    public String getColumnName() {
        return this.columnName;
    }

    public DataPersister getDataPersister() {
        return this.dataPersister;
    }

    public Object getDataTypeConfigObj() {
        return this.dataTypeConfigObj;
    }

    public SqlType getSqlType() {
        return this.fieldConverter.getSqlType();
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public int getWidth() {
        return this.fieldConfig.getWidth();
    }

    public boolean isCanBeNull() {
        return this.fieldConfig.isCanBeNull();
    }

    public boolean isId() {
        return this.isId;
    }

    public boolean isGeneratedId() {
        return this.isGeneratedId;
    }

    public boolean isGeneratedIdSequence() {
        return this.generatedIdSequence != null;
    }

    public String getGeneratedIdSequence() {
        return this.generatedIdSequence;
    }

    public boolean isForeign() {
        return this.fieldConfig.isForeign();
    }

    public void assignField(Object data, Object val, boolean parentObject, ObjectCache objectCache) throws SQLException {
        if (!(this.foreignIdField == null || val == null)) {
            Object foreignId = extractJavaFieldValue(data);
            if (foreignId == null || !foreignId.equals(val)) {
                Object val2;
                ObjectCache foreignCache = this.foreignDao.getObjectCache();
                if (foreignCache == null) {
                    val2 = null;
                } else {
                    val2 = foreignCache.get(getType(), val);
                }
                if (val2 != null) {
                    val = val2;
                } else if (!parentObject) {
                    Object foreignObject;
                    LevelCounters levelCounters = (LevelCounters) threadLevelCounters.get();
                    if (levelCounters.autoRefreshLevel == 0) {
                        levelCounters.autoRefreshLevelMax = this.fieldConfig.getMaxForeignAutoRefreshLevel();
                    }
                    if (levelCounters.autoRefreshLevel >= levelCounters.autoRefreshLevelMax) {
                        foreignObject = this.foreignTableInfo.createObject();
                        this.foreignIdField.assignField(foreignObject, val, false, objectCache);
                    } else {
                        if (this.mappedQueryForId == null) {
                            this.mappedQueryForId = MappedQueryForId.build(this.connectionSource.getDatabaseType(), this.foreignDao.getTableInfo(), this.foreignIdField);
                        }
                        levelCounters.autoRefreshLevel++;
                        DatabaseConnection databaseConnection;
                        try {
                            databaseConnection = this.connectionSource.getReadOnlyConnection();
                            foreignObject = this.mappedQueryForId.execute(databaseConnection, val, objectCache);
                            this.connectionSource.releaseConnection(databaseConnection);
                            levelCounters.autoRefreshLevel--;
                            if (levelCounters.autoRefreshLevel <= 0) {
                                threadLevelCounters.remove();
                            }
                        } catch (Throwable th) {
                            levelCounters.autoRefreshLevel--;
                            if (levelCounters.autoRefreshLevel <= 0) {
                                threadLevelCounters.remove();
                            }
                        }
                    }
                    val = foreignObject;
                }
            } else {
                return;
            }
        }
        if (this.fieldSetMethod == null) {
            try {
                this.field.set(data, val);
                return;
            } catch (IllegalArgumentException e) {
                throw SqlExceptionUtil.create("Could not assign object '" + val + "' to field " + this, e);
            } catch (IllegalAccessException e2) {
                throw SqlExceptionUtil.create("Could not assign object '" + val + "' to field " + this, e2);
            }
        }
        try {
            this.fieldSetMethod.invoke(data, new Object[]{val});
        } catch (Exception e3) {
            throw SqlExceptionUtil.create("Could not call " + this.fieldSetMethod + " on object with '" + val + "' for " + this, e3);
        }
    }

    public Object assignIdValue(Object data, Number val, ObjectCache objectCache) throws SQLException {
        Object idVal = this.dataPersister.convertIdNumber(val);
        if (idVal == null) {
            throw new SQLException("Invalid class " + this.dataPersister + " for sequence-id " + this);
        }
        assignField(data, idVal, false, objectCache);
        return idVal;
    }

    public <FV> FV extractRawJavaFieldValue(Object object) throws SQLException {
        FV val;
        if (this.fieldGetMethod == null) {
            try {
                val = this.field.get(object);
            } catch (Exception e) {
                throw SqlExceptionUtil.create("Could not get field value for " + this, e);
            }
        }
        try {
            val = this.fieldGetMethod.invoke(object, new Object[0]);
        } catch (Exception e2) {
            throw SqlExceptionUtil.create("Could not call " + this.fieldGetMethod + " for " + this, e2);
        }
        return val;
    }

    public Object extractJavaFieldValue(Object object) throws SQLException {
        Object val = extractRawJavaFieldValue(object);
        if (this.foreignIdField == null || val == null) {
            return val;
        }
        return this.foreignIdField.extractRawJavaFieldValue(val);
    }

    public Object extractJavaFieldToSqlArgValue(Object object) throws SQLException {
        return convertJavaFieldToSqlArgValue(extractJavaFieldValue(object));
    }

    public Object convertJavaFieldToSqlArgValue(Object fieldVal) throws SQLException {
        if (fieldVal == null) {
            return null;
        }
        return this.fieldConverter.javaToSqlArg(this, fieldVal);
    }

    public Object convertStringToJavaField(String value, int columnPos) throws SQLException {
        if (value == null) {
            return null;
        }
        return this.fieldConverter.resultStringToJava(this, value, columnPos);
    }

    public Object moveToNextValue(Object val) {
        if (this.dataPersister == null) {
            return null;
        }
        return this.dataPersister.moveToNextValue(val);
    }

    public FieldType getForeignIdField() {
        return this.foreignIdField;
    }

    public boolean isEscapedValue() {
        return this.dataPersister.isEscapedValue();
    }

    public Enum<?> getUnknownEnumVal() {
        return this.fieldConfig.getUnknownEnumValue();
    }

    public String getFormat() {
        return this.fieldConfig.getFormat();
    }

    public boolean isUnique() {
        return this.fieldConfig.isUnique();
    }

    public boolean isUniqueCombo() {
        return this.fieldConfig.isUniqueCombo();
    }

    public String getIndexName() {
        return this.fieldConfig.getIndexName(this.tableName);
    }

    public String getUniqueIndexName() {
        return this.fieldConfig.getUniqueIndexName(this.tableName);
    }

    public boolean isEscapedDefaultValue() {
        return this.dataPersister.isEscapedDefaultValue();
    }

    public boolean isComparable() throws SQLException {
        if (this.fieldConfig.isForeignCollection()) {
            return false;
        }
        if (this.dataPersister != null) {
            return this.dataPersister.isComparable();
        }
        throw new SQLException("Internal error.  Data-persister is not configured for field.  Please post _full_ exception with associated data objects to mailing list: " + this);
    }

    public boolean isArgumentHolderRequired() {
        return this.dataPersister.isArgumentHolderRequired();
    }

    public boolean isForeignCollection() {
        return this.fieldConfig.isForeignCollection();
    }

    public <FT, FID> BaseForeignCollection<FT, FID> buildForeignCollection(Object parent, FID id) throws SQLException {
        if (this.foreignFieldType == null) {
            return null;
        }
        Dao<FT, FID> castDao = this.foreignDao;
        if (this.fieldConfig.isForeignCollectionEager()) {
            LevelCounters levelCounters = (LevelCounters) threadLevelCounters.get();
            if (levelCounters.foreignCollectionLevel == 0) {
                levelCounters.foreignCollectionLevelMax = this.fieldConfig.getForeignCollectionMaxEagerLevel();
            }
            if (levelCounters.foreignCollectionLevel >= levelCounters.foreignCollectionLevelMax) {
                return new LazyForeignCollection(castDao, parent, id, this.foreignFieldType, this.fieldConfig.getForeignCollectionOrderColumnName(), this.fieldConfig.isForeignCollectionOrderAscending());
            }
            levelCounters.foreignCollectionLevel++;
            try {
                BaseForeignCollection<FT, FID> eagerForeignCollection = new EagerForeignCollection(castDao, parent, id, this.foreignFieldType, this.fieldConfig.getForeignCollectionOrderColumnName(), this.fieldConfig.isForeignCollectionOrderAscending());
                return eagerForeignCollection;
            } finally {
                levelCounters.foreignCollectionLevel--;
            }
        } else {
            return new LazyForeignCollection(castDao, parent, id, this.foreignFieldType, this.fieldConfig.getForeignCollectionOrderColumnName(), this.fieldConfig.isForeignCollectionOrderAscending());
        }
    }

    public <T> T resultToJava(DatabaseResults results, Map<String, Integer> columnPositions) throws SQLException {
        Integer dbColumnPos = (Integer) columnPositions.get(this.columnName);
        if (dbColumnPos == null) {
            dbColumnPos = Integer.valueOf(results.findColumn(this.columnName));
            columnPositions.put(this.columnName, dbColumnPos);
        }
        T converted = this.fieldConverter.resultToJava(this, results, dbColumnPos.intValue());
        if (this.fieldConfig.isForeign()) {
            return results.wasNull(dbColumnPos.intValue()) ? null : converted;
        } else {
            if (this.dataPersister.isPrimitive()) {
                if (!this.fieldConfig.isThrowIfNull() || !results.wasNull(dbColumnPos.intValue())) {
                    return converted;
                }
                throw new SQLException("Results value for primitive field '" + this.field.getName() + "' was an invalid null value");
            } else if (this.fieldConverter.isStreamType() || !results.wasNull(dbColumnPos.intValue())) {
                return converted;
            } else {
                return null;
            }
        }
    }

    public boolean isSelfGeneratedId() {
        return this.dataPersister.isSelfGeneratedId();
    }

    public boolean isAllowGeneratedIdInsert() {
        return this.fieldConfig.isAllowGeneratedIdInsert();
    }

    public String getColumnDefinition() {
        return this.fieldConfig.getColumnDefinition();
    }

    public boolean isForeignAutoCreate() {
        return this.fieldConfig.isForeignAutoCreate();
    }

    public boolean isVersion() {
        return this.fieldConfig.isVersion();
    }

    public Object generateId() {
        return this.dataPersister.generateId();
    }

    public boolean isReadOnly() {
        return this.fieldConfig.isReadOnly();
    }

    public <FV> FV getFieldValueIfNotDefault(Object object) throws SQLException {
        FV fieldValue = extractJavaFieldValue(object);
        if (isFieldValueDefault(fieldValue)) {
            return null;
        }
        return fieldValue;
    }

    public boolean isObjectsFieldValueDefault(Object object) throws SQLException {
        return isFieldValueDefault(extractJavaFieldValue(object));
    }

    public Object getJavaDefaultValueDefault() {
        if (this.field.getType() == Boolean.TYPE) {
            return Boolean.valueOf(DEFAULT_VALUE_BOOLEAN);
        }
        if (this.field.getType() == Byte.TYPE || this.field.getType() == Byte.class) {
            return Byte.valueOf(DEFAULT_VALUE_BYTE);
        }
        if (this.field.getType() == Character.TYPE || this.field.getType() == Character.class) {
            return Character.valueOf(DEFAULT_VALUE_CHAR);
        }
        if (this.field.getType() == Short.TYPE || this.field.getType() == Short.class) {
            return Short.valueOf(DEFAULT_VALUE_SHORT);
        }
        if (this.field.getType() == Integer.TYPE || this.field.getType() == Integer.class) {
            return Integer.valueOf(DEFAULT_VALUE_INT);
        }
        if (this.field.getType() == Long.TYPE || this.field.getType() == Long.class) {
            return Long.valueOf(DEFAULT_VALUE_LONG);
        }
        if (this.field.getType() == Float.TYPE || this.field.getType() == Float.class) {
            return Float.valueOf(DEFAULT_VALUE_FLOAT);
        }
        if (this.field.getType() == Double.TYPE || this.field.getType() == Double.class) {
            return Double.valueOf(DEFAULT_VALUE_DOUBLE);
        }
        return null;
    }

    public <T> int createWithForeignDao(T foreignData) throws SQLException {
        return this.foreignDao.create(foreignData);
    }

    public static FieldType createFieldType(ConnectionSource connectionSource, String tableName, Field field, Class<?> parentClass) throws SQLException {
        DatabaseFieldConfig fieldConfig = DatabaseFieldConfig.fromField(connectionSource.getDatabaseType(), tableName, field);
        if (fieldConfig == null) {
            return null;
        }
        return new FieldType(connectionSource, tableName, field, fieldConfig, parentClass);
    }

    public boolean equals(Object arg) {
        if (arg == null || arg.getClass() != getClass()) {
            return false;
        }
        FieldType other = (FieldType) arg;
        if (!this.field.equals(other.field)) {
            return false;
        }
        if (this.parentClass == null) {
            if (other.parentClass != null) {
                return false;
            }
        } else if (!this.parentClass.equals(other.parentClass)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.field.hashCode();
    }

    public String toString() {
        return getClass().getSimpleName() + ":name=" + this.field.getName() + ",class=" + this.field.getDeclaringClass().getSimpleName();
    }

    private boolean isFieldValueDefault(Object fieldValue) {
        if (fieldValue == null) {
            return true;
        }
        return fieldValue.equals(getJavaDefaultValueDefault());
    }

    private FieldType findForeignFieldType(Class<?> clazz, Class<?> foreignClass, BaseDaoImpl<?, ?> foreignDao) throws SQLException {
        String foreignColumnName = this.fieldConfig.getForeignCollectionForeignFieldName();
        FieldType[] arr$ = foreignDao.getTableInfo().getFieldTypes();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            FieldType fieldType = arr$[i$];
            if (fieldType.getType() != foreignClass || (foreignColumnName != null && !fieldType.getField().getName().equals(foreignColumnName))) {
                i$++;
            } else if (fieldType.fieldConfig.isForeign() || fieldType.fieldConfig.isForeignAutoRefresh()) {
                return fieldType;
            } else {
                throw new SQLException("Foreign collection object " + clazz + " for field '" + this.field.getName() + "' contains a field of class " + foreignClass + " but it's not foreign");
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Foreign collection class ").append(clazz.getName());
        sb.append(" for field '").append(this.field.getName()).append("' column-name does not contain a foreign field");
        if (foreignColumnName != null) {
            sb.append(" named '").append(foreignColumnName).append('\'');
        }
        sb.append(" of class ").append(foreignClass.getName());
        throw new SQLException(sb.toString());
    }

    private void assignDataType(DatabaseType databaseType, DataPersister dataPersister) throws SQLException {
        this.dataPersister = dataPersister;
        if (dataPersister != null) {
            this.fieldConverter = databaseType.getFieldConverter(dataPersister);
            if (this.isGeneratedId && !dataPersister.isValidGeneratedType()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Generated-id field '").append(this.field.getName());
                sb.append("' in ").append(this.field.getDeclaringClass().getSimpleName());
                sb.append(" can't be type ").append(this.dataPersister.getSqlType());
                sb.append(".  Must be one of: ");
                for (DataType dataType : DataType.values()) {
                    DataPersister persister = dataType.getDataPersister();
                    if (persister != null && persister.isValidGeneratedType()) {
                        sb.append(dataType).append(Char.SPACE);
                    }
                }
                throw new IllegalArgumentException(sb.toString());
            } else if (this.fieldConfig.isThrowIfNull() && !dataPersister.isPrimitive()) {
                throw new SQLException("Field " + this.field.getName() + " must be a primitive if set with throwIfNull");
            } else if (!this.isId || dataPersister.isAppropriateId()) {
                this.dataTypeConfigObj = dataPersister.makeConfigObject(this);
                String defaultStr = this.fieldConfig.getDefaultValue();
                if (defaultStr == null) {
                    this.defaultValue = null;
                } else if (this.isGeneratedId) {
                    throw new SQLException("Field '" + this.field.getName() + "' cannot be a generatedId and have a default value '" + defaultStr + "'");
                } else {
                    this.defaultValue = this.fieldConverter.parseDefaultString(this, defaultStr);
                }
            } else {
                throw new SQLException("Field '" + this.field.getName() + "' is of data type " + dataPersister + " which cannot be the ID field");
            }
        } else if (!this.fieldConfig.isForeign() && !this.fieldConfig.isForeignCollection()) {
            throw new SQLException("Data persister for field " + this + " is null but the field is not a foreign or foreignCollection");
        }
    }
}
