package com.j256.ormlite.field;

import com.j256.ormlite.misc.SqlExceptionUtil;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;

public class DatabaseFieldConfigLoader {
    private static final String CONFIG_FILE_END_MARKER = "# --field-end--";
    private static final String CONFIG_FILE_START_MARKER = "# --field-start--";
    private static final DataPersister DEFAULT_DATA_PERSISTER;
    private static final int DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL = 1;
    private static final String FIELD_NAME_ALLOW_GENERATED_ID_INSERT = "allowGeneratedIdInsert";
    private static final String FIELD_NAME_CAN_BE_NULL = "canBeNull";
    private static final String FIELD_NAME_COLUMN_DEFINITION = "columnDefinition";
    private static final String FIELD_NAME_COLUMN_NAME = "columnName";
    private static final String FIELD_NAME_DATA_PERSISTER = "dataPersister";
    private static final String FIELD_NAME_DEFAULT_VALUE = "defaultValue";
    private static final String FIELD_NAME_FIELD_NAME = "fieldName";
    private static final String FIELD_NAME_FOREIGN = "foreign";
    private static final String FIELD_NAME_FOREIGN_AUTO_CREATE = "foreignAutoCreate";
    private static final String FIELD_NAME_FOREIGN_AUTO_REFRESH = "foreignAutoRefresh";
    private static final String FIELD_NAME_FOREIGN_COLLECTION = "foreignCollection";
    private static final String FIELD_NAME_FOREIGN_COLLECTION_COLUMN_NAME = "foreignCollectionColumnName";
    private static final String FIELD_NAME_FOREIGN_COLLECTION_EAGER = "foreignCollectionEager";
    private static final String FIELD_NAME_FOREIGN_COLLECTION_FOREIGN_FIELD_NAME = "foreignCollectionForeignFieldName";
    private static final String FIELD_NAME_FOREIGN_COLLECTION_FOREIGN_FIELD_NAME_OLD = "foreignCollectionForeignColumnName";
    private static final String FIELD_NAME_FOREIGN_COLLECTION_ORDER_ASCENDING = "foreignCollectionOrderAscending";
    private static final String FIELD_NAME_FOREIGN_COLLECTION_ORDER_COLUMN_NAME = "foreignCollectionOrderColumnName";
    private static final String FIELD_NAME_FOREIGN_COLLECTION_ORDER_COLUMN_NAME_OLD = "foreignCollectionOrderColumn";
    private static final String FIELD_NAME_FOREIGN_COLUMN_NAME = "foreignColumnName";
    private static final String FIELD_NAME_FORMAT = "format";
    private static final String FIELD_NAME_GENERATED_ID = "generatedId";
    private static final String FIELD_NAME_GENERATED_ID_SEQUENCE = "generatedIdSequence";
    private static final String FIELD_NAME_ID = "id";
    private static final String FIELD_NAME_INDEX = "index";
    private static final String FIELD_NAME_INDEX_NAME = "indexName";
    private static final String FIELD_NAME_MAX_EAGER_FOREIGN_COLLECTION_LEVEL = "foreignCollectionMaxEagerLevel";
    private static final String FIELD_NAME_MAX_EAGER_FOREIGN_COLLECTION_LEVEL_OLD = "maxEagerForeignCollectionLevel";
    private static final String FIELD_NAME_MAX_FOREIGN_AUTO_REFRESH_LEVEL = "maxForeignAutoRefreshLevel";
    private static final String FIELD_NAME_PERSISTER_CLASS = "persisterClass";
    private static final String FIELD_NAME_READ_ONLY = "readOnly";
    private static final String FIELD_NAME_THROW_IF_NULL = "throwIfNull";
    private static final String FIELD_NAME_UNIQUE = "unique";
    private static final String FIELD_NAME_UNIQUE_COMBO = "uniqueCombo";
    private static final String FIELD_NAME_UNIQUE_INDEX = "uniqueIndex";
    private static final String FIELD_NAME_UNIQUE_INDEX_NAME = "uniqueIndexName";
    private static final String FIELD_NAME_UNKNOWN_ENUM_VALUE = "unknownEnumValue";
    private static final String FIELD_NAME_USE_GET_SET = "useGetSet";
    private static final String FIELD_NAME_VERSION = "version";
    private static final String FIELD_NAME_WIDTH = "width";

    static {
        DEFAULT_DATA_PERSISTER = DatabaseFieldConfig.DEFAULT_DATA_TYPE.getDataPersister();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.j256.ormlite.field.DatabaseFieldConfig fromReader(java.io.BufferedReader r8) throws java.sql.SQLException {
        /*
        r1 = new com.j256.ormlite.field.DatabaseFieldConfig;
        r1.<init>();
        r0 = 0;
    L_0x0006:
        r3 = r8.readLine();	 Catch:{ IOException -> 0x000f }
        if (r3 != 0) goto L_0x0017;
    L_0x000c:
        if (r0 == 0) goto L_0x0064;
    L_0x000e:
        return r1;
    L_0x000f:
        r2 = move-exception;
        r5 = "Could not read DatabaseFieldConfig from stream";
        r5 = com.j256.ormlite.misc.SqlExceptionUtil.create(r5, r2);
        throw r5;
    L_0x0017:
        r5 = "# --field-end--";
        r5 = r3.equals(r5);
        if (r5 != 0) goto L_0x000c;
    L_0x001f:
        r5 = r3.length();
        if (r5 == 0) goto L_0x0006;
    L_0x0025:
        r5 = "#";
        r5 = r3.startsWith(r5);
        if (r5 != 0) goto L_0x0006;
    L_0x002d:
        r5 = "# --field-start--";
        r5 = r3.equals(r5);
        if (r5 != 0) goto L_0x0006;
    L_0x0035:
        r5 = "=";
        r6 = -2;
        r4 = r3.split(r5, r6);
        r5 = r4.length;
        r6 = 2;
        if (r5 == r6) goto L_0x0059;
    L_0x0040:
        r5 = new java.sql.SQLException;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "DatabaseFieldConfig reading from stream cannot parse line: ";
        r6 = r6.append(r7);
        r6 = r6.append(r3);
        r6 = r6.toString();
        r5.<init>(r6);
        throw r5;
    L_0x0059:
        r5 = 0;
        r5 = r4[r5];
        r6 = 1;
        r6 = r4[r6];
        readField(r1, r5, r6);
        r0 = 1;
        goto L_0x0006;
    L_0x0064:
        r1 = 0;
        goto L_0x000e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.j256.ormlite.field.DatabaseFieldConfigLoader.fromReader(java.io.BufferedReader):com.j256.ormlite.field.DatabaseFieldConfig");
    }

    public static void write(BufferedWriter writer, DatabaseFieldConfig config, String tableName) throws SQLException {
        try {
            writeConfig(writer, config, tableName);
        } catch (IOException e) {
            throw SqlExceptionUtil.create("Could not write config to writer", e);
        }
    }

    public static void writeConfig(BufferedWriter writer, DatabaseFieldConfig config, String tableName) throws IOException {
        writer.append(CONFIG_FILE_START_MARKER);
        writer.newLine();
        if (config.getFieldName() != null) {
            writer.append(FIELD_NAME_FIELD_NAME).append('=').append(config.getFieldName());
            writer.newLine();
        }
        if (config.getColumnName() != null) {
            writer.append(FIELD_NAME_COLUMN_NAME).append('=').append(config.getColumnName());
            writer.newLine();
        }
        if (config.getDataPersister() != DEFAULT_DATA_PERSISTER) {
            boolean found = false;
            DataType[] arr$ = DataType.values();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$ += DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL) {
                DataType dataType = arr$[i$];
                if (dataType.getDataPersister() == config.getDataPersister()) {
                    writer.append(FIELD_NAME_DATA_PERSISTER).append('=').append(dataType.name());
                    writer.newLine();
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Unknown data persister field: " + config.getDataPersister());
            }
        }
        if (config.getDefaultValue() != null) {
            writer.append(FIELD_NAME_DEFAULT_VALUE).append('=').append(config.getDefaultValue());
            writer.newLine();
        }
        if (config.getWidth() != 0) {
            writer.append(FIELD_NAME_WIDTH).append('=').append(Integer.toString(config.getWidth()));
            writer.newLine();
        }
        if (!config.isCanBeNull()) {
            writer.append(FIELD_NAME_CAN_BE_NULL).append('=').append(Boolean.toString(config.isCanBeNull()));
            writer.newLine();
        }
        if (config.isId()) {
            writer.append(FIELD_NAME_ID).append('=').append("true");
            writer.newLine();
        }
        if (config.isGeneratedId()) {
            writer.append(FIELD_NAME_GENERATED_ID).append('=').append("true");
            writer.newLine();
        }
        if (config.getGeneratedIdSequence() != null) {
            writer.append(FIELD_NAME_GENERATED_ID_SEQUENCE).append('=').append(config.getGeneratedIdSequence());
            writer.newLine();
        }
        if (config.isForeign()) {
            writer.append(FIELD_NAME_FOREIGN).append('=').append("true");
            writer.newLine();
        }
        if (config.isUseGetSet()) {
            writer.append(FIELD_NAME_USE_GET_SET).append('=').append("true");
            writer.newLine();
        }
        if (config.getUnknownEnumValue() != null) {
            writer.append(FIELD_NAME_UNKNOWN_ENUM_VALUE).append('=').append(config.getUnknownEnumValue().getClass().getName()).append("#").append(config.getUnknownEnumValue().name());
            writer.newLine();
        }
        if (config.isThrowIfNull()) {
            writer.append(FIELD_NAME_THROW_IF_NULL).append('=').append("true");
            writer.newLine();
        }
        if (config.getFormat() != null) {
            writer.append(FIELD_NAME_FORMAT).append('=').append(config.getFormat());
            writer.newLine();
        }
        if (config.isUnique()) {
            writer.append(FIELD_NAME_UNIQUE).append('=').append("true");
            writer.newLine();
        }
        if (config.isUniqueCombo()) {
            writer.append(FIELD_NAME_UNIQUE_COMBO).append('=').append("true");
            writer.newLine();
        }
        String indexName = config.getIndexName(tableName);
        if (indexName != null) {
            writer.append(FIELD_NAME_INDEX_NAME).append('=').append(indexName);
            writer.newLine();
        }
        String uniqueIndexName = config.getUniqueIndexName(tableName);
        if (uniqueIndexName != null) {
            writer.append(FIELD_NAME_UNIQUE_INDEX_NAME).append('=').append(uniqueIndexName);
            writer.newLine();
        }
        if (config.isForeignAutoRefresh()) {
            writer.append(FIELD_NAME_FOREIGN_AUTO_REFRESH).append('=').append("true");
            writer.newLine();
        }
        if (config.getMaxForeignAutoRefreshLevel() != -1) {
            writer.append(FIELD_NAME_MAX_FOREIGN_AUTO_REFRESH_LEVEL).append('=').append(Integer.toString(config.getMaxForeignAutoRefreshLevel()));
            writer.newLine();
        }
        if (config.getPersisterClass() != DatabaseFieldConfig.DEFAULT_PERSISTER_CLASS) {
            writer.append(FIELD_NAME_PERSISTER_CLASS).append('=').append(config.getPersisterClass().getName());
            writer.newLine();
        }
        if (config.isAllowGeneratedIdInsert()) {
            writer.append(FIELD_NAME_ALLOW_GENERATED_ID_INSERT).append('=').append("true");
            writer.newLine();
        }
        if (config.getColumnDefinition() != null) {
            writer.append(FIELD_NAME_COLUMN_DEFINITION).append('=').append(config.getColumnDefinition());
            writer.newLine();
        }
        if (config.isForeignAutoCreate()) {
            writer.append(FIELD_NAME_FOREIGN_AUTO_CREATE).append('=').append("true");
            writer.newLine();
        }
        if (config.isVersion()) {
            writer.append(FIELD_NAME_VERSION).append('=').append("true");
            writer.newLine();
        }
        String foreignColumnName = config.getForeignColumnName();
        if (foreignColumnName != null) {
            writer.append(FIELD_NAME_FOREIGN_COLUMN_NAME).append('=').append(foreignColumnName);
            writer.newLine();
        }
        if (config.isReadOnly()) {
            writer.append(FIELD_NAME_READ_ONLY).append('=').append("true");
            writer.newLine();
        }
        if (config.isForeignCollection()) {
            writer.append(FIELD_NAME_FOREIGN_COLLECTION).append('=').append("true");
            writer.newLine();
        }
        if (config.isForeignCollectionEager()) {
            writer.append(FIELD_NAME_FOREIGN_COLLECTION_EAGER).append('=').append("true");
            writer.newLine();
        }
        if (config.getForeignCollectionMaxEagerLevel() != DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL) {
            writer.append(FIELD_NAME_MAX_EAGER_FOREIGN_COLLECTION_LEVEL).append('=').append(Integer.toString(config.getForeignCollectionMaxEagerLevel()));
            writer.newLine();
        }
        if (config.getForeignCollectionColumnName() != null) {
            writer.append(FIELD_NAME_FOREIGN_COLLECTION_COLUMN_NAME).append('=').append(config.getForeignCollectionColumnName());
            writer.newLine();
        }
        if (config.getForeignCollectionOrderColumnName() != null) {
            writer.append(FIELD_NAME_FOREIGN_COLLECTION_ORDER_COLUMN_NAME).append('=').append(config.getForeignCollectionOrderColumnName());
            writer.newLine();
        }
        if (!config.isForeignCollectionOrderAscending()) {
            writer.append(FIELD_NAME_FOREIGN_COLLECTION_ORDER_ASCENDING).append('=').append(Boolean.toString(config.isForeignCollectionOrderAscending()));
            writer.newLine();
        }
        if (config.getForeignCollectionForeignFieldName() != null) {
            writer.append(FIELD_NAME_FOREIGN_COLLECTION_FOREIGN_FIELD_NAME).append('=').append(config.getForeignCollectionForeignFieldName());
            writer.newLine();
        }
        writer.append(CONFIG_FILE_END_MARKER);
        writer.newLine();
    }

    private static void readField(DatabaseFieldConfig config, String field, String value) {
        if (field.equals(FIELD_NAME_FIELD_NAME)) {
            config.setFieldName(value);
            return;
        }
        if (field.equals(FIELD_NAME_COLUMN_NAME)) {
            config.setColumnName(value);
            return;
        }
        if (field.equals(FIELD_NAME_DATA_PERSISTER)) {
            config.setDataPersister(DataType.valueOf(value).getDataPersister());
            return;
        }
        if (field.equals(FIELD_NAME_DEFAULT_VALUE)) {
            config.setDefaultValue(value);
            return;
        }
        if (field.equals(FIELD_NAME_WIDTH)) {
            config.setWidth(Integer.parseInt(value));
            return;
        }
        if (field.equals(FIELD_NAME_CAN_BE_NULL)) {
            config.setCanBeNull(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_ID)) {
            config.setId(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_GENERATED_ID)) {
            config.setGeneratedId(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_GENERATED_ID_SEQUENCE)) {
            config.setGeneratedIdSequence(value);
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN)) {
            config.setForeign(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_USE_GET_SET)) {
            config.setUseGetSet(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_UNKNOWN_ENUM_VALUE)) {
            String[] parts = value.split("#", -2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid value for unknownEnumValue which should be in class#name format: " + value);
            }
            try {
                Object[] consts = Class.forName(parts[0]).getEnumConstants();
                if (consts == null) {
                    throw new IllegalArgumentException("Invalid class is not an Enum for unknownEnumValue: " + value);
                }
                boolean found = false;
                Enum[] arr$ = (Enum[]) consts;
                int len$ = arr$.length;
                for (int i$ = 0; i$ < len$; i$ += DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL) {
                    Enum<?> enumInstance = arr$[i$];
                    if (enumInstance.name().equals(parts[DEFAULT_MAX_EAGER_FOREIGN_COLLECTION_LEVEL])) {
                        config.setUnknownEnumValue(enumInstance);
                        found = true;
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException("Invalid enum value name for unknownEnumvalue: " + value);
                }
                return;
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Unknown class specified for unknownEnumValue: " + value);
            }
        }
        if (field.equals(FIELD_NAME_THROW_IF_NULL)) {
            config.setThrowIfNull(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_FORMAT)) {
            config.setFormat(value);
            return;
        }
        if (field.equals(FIELD_NAME_UNIQUE)) {
            config.setUnique(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_UNIQUE_COMBO)) {
            config.setUniqueCombo(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_INDEX)) {
            config.setIndex(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_INDEX_NAME)) {
            config.setIndex(true);
            config.setIndexName(value);
            return;
        }
        if (field.equals(FIELD_NAME_UNIQUE_INDEX)) {
            config.setUniqueIndex(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_UNIQUE_INDEX_NAME)) {
            config.setUniqueIndex(true);
            config.setUniqueIndexName(value);
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_AUTO_REFRESH)) {
            config.setForeignAutoRefresh(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_MAX_FOREIGN_AUTO_REFRESH_LEVEL)) {
            config.setMaxForeignAutoRefreshLevel(Integer.parseInt(value));
            return;
        }
        if (field.equals(FIELD_NAME_PERSISTER_CLASS)) {
            try {
                config.setPersisterClass(Class.forName(value));
                return;
            } catch (ClassNotFoundException e2) {
                throw new IllegalArgumentException("Could not find persisterClass: " + value);
            }
        }
        if (field.equals(FIELD_NAME_ALLOW_GENERATED_ID_INSERT)) {
            config.setAllowGeneratedIdInsert(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_COLUMN_DEFINITION)) {
            config.setColumnDefinition(value);
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_AUTO_CREATE)) {
            config.setForeignAutoCreate(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_VERSION)) {
            config.setVersion(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_COLUMN_NAME)) {
            config.setForeignColumnName(value);
            return;
        }
        if (field.equals(FIELD_NAME_READ_ONLY)) {
            config.setReadOnly(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_COLLECTION)) {
            config.setForeignCollection(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_COLLECTION_EAGER)) {
            config.setForeignCollectionEager(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_MAX_EAGER_FOREIGN_COLLECTION_LEVEL_OLD)) {
            config.setForeignCollectionMaxEagerLevel(Integer.parseInt(value));
            return;
        }
        if (field.equals(FIELD_NAME_MAX_EAGER_FOREIGN_COLLECTION_LEVEL)) {
            config.setForeignCollectionMaxEagerLevel(Integer.parseInt(value));
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_COLLECTION_COLUMN_NAME)) {
            config.setForeignCollectionColumnName(value);
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_COLLECTION_ORDER_COLUMN_NAME_OLD)) {
            config.setForeignCollectionOrderColumnName(value);
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_COLLECTION_ORDER_COLUMN_NAME)) {
            config.setForeignCollectionOrderColumnName(value);
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_COLLECTION_ORDER_ASCENDING)) {
            config.setForeignCollectionOrderAscending(Boolean.parseBoolean(value));
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_COLLECTION_FOREIGN_FIELD_NAME_OLD)) {
            config.setForeignCollectionForeignFieldName(value);
            return;
        }
        if (field.equals(FIELD_NAME_FOREIGN_COLLECTION_FOREIGN_FIELD_NAME)) {
            config.setForeignCollectionForeignFieldName(value);
        }
    }
}
