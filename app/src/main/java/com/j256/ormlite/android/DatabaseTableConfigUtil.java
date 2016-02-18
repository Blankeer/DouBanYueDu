package com.j256.ormlite.android;

import com.douban.book.reader.entity.DbCacheEntity.Column;
import com.douban.book.reader.helper.WorksListUri;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTableConfigUtil {
    private static final int ALLOW_GENERATED_ID_INSERT = 24;
    private static final int CAN_BE_NULL = 5;
    private static final int COLUMN_DEFINITON = 25;
    private static final int COLUMN_NAME = 1;
    private static final int DATA_TYPE = 2;
    private static final int DEFAULT_VALUE = 3;
    private static final int FOREIGN = 9;
    private static final int FOREIGN_AUTO_CREATE = 26;
    private static final int FOREIGN_AUTO_REFRESH = 21;
    private static final int FOREIGN_COLUMN_NAME = 28;
    private static final int FORMAT = 14;
    private static final int GENERATED_ID = 7;
    private static final int GENERATED_ID_SEQUENCE = 8;
    private static final int ID = 6;
    private static final int INDEX = 17;
    private static final int INDEX_NAME = 19;
    private static final int MAX_FOREIGN_AUTO_REFRESH_LEVEL = 22;
    private static final int PERSISTED = 13;
    private static final int PERSISTER_CLASS = 23;
    private static final int READ_ONLY = 29;
    private static final int THROW_IF_NULL = 12;
    private static final int UNIQUE = 15;
    private static final int UNIQUE_COMBO = 16;
    private static final int UNIQUE_INDEX = 18;
    private static final int UNIQUE_INDEX_NAME = 20;
    private static final int UNKNOWN_ENUM_NAME = 11;
    private static final int USE_GET_SET = 10;
    private static final int VERSION = 27;
    private static final int WIDTH = 4;
    private static Class<?> annotationFactoryClazz;
    private static Class<?> annotationMemberClazz;
    private static final int[] configFieldNums;
    private static Field elementsField;
    private static Field nameField;
    private static Field valueField;
    private static int workedC;

    private static class DatabaseFieldSample {
        @DatabaseField
        String field;

        private DatabaseFieldSample() {
        }
    }

    static {
        workedC = 0;
        configFieldNums = lookupClasses();
    }

    public static <T> DatabaseTableConfig<T> fromClass(ConnectionSource connectionSource, Class<T> clazz) throws SQLException {
        DatabaseType databaseType = connectionSource.getDatabaseType();
        String tableName = DatabaseTableConfig.extractTableName(clazz);
        List fieldConfigs = new ArrayList();
        for (Class<?> classWalk = clazz; classWalk != null; classWalk = classWalk.getSuperclass()) {
            Field[] arr$ = classWalk.getDeclaredFields();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$ += COLUMN_NAME) {
                DatabaseFieldConfig config = configFromField(databaseType, tableName, arr$[i$]);
                if (config != null && config.isPersisted()) {
                    fieldConfigs.add(config);
                }
            }
        }
        if (fieldConfigs.size() == 0) {
            return null;
        }
        return new DatabaseTableConfig((Class) clazz, tableName, fieldConfigs);
    }

    public static int getWorkedC() {
        return workedC;
    }

    private static int[] lookupClasses() {
        try {
            annotationFactoryClazz = Class.forName("org.apache.harmony.lang.annotation.AnnotationFactory");
            annotationMemberClazz = Class.forName("org.apache.harmony.lang.annotation.AnnotationMember");
            Class<?> annotationMemberArrayClazz = Class.forName("[Lorg.apache.harmony.lang.annotation.AnnotationMember;");
            try {
                elementsField = annotationFactoryClazz.getDeclaredField("elements");
                elementsField.setAccessible(true);
                nameField = annotationMemberClazz.getDeclaredField(SelectCountryActivity.EXTRA_COUNTRY_NAME);
                nameField.setAccessible(true);
                valueField = annotationMemberClazz.getDeclaredField(Column.VALUE);
                valueField.setAccessible(true);
                InvocationHandler proxy = Proxy.getInvocationHandler((DatabaseField) DatabaseFieldSample.class.getDeclaredField("field").getAnnotation(DatabaseField.class));
                if (proxy.getClass() != annotationFactoryClazz) {
                    return null;
                }
                try {
                    Object elements = elementsField.get(proxy);
                    if (elements == null || elements.getClass() != annotationMemberArrayClazz) {
                        return null;
                    }
                    Object[] elementArray = (Object[]) elements;
                    int[] configNums = new int[elementArray.length];
                    for (int i = 0; i < elementArray.length; i += COLUMN_NAME) {
                        configNums[i] = configFieldNameToNum((String) nameField.get(elementArray[i]));
                    }
                    return configNums;
                } catch (IllegalAccessException e) {
                    return null;
                }
            } catch (SecurityException e2) {
                return null;
            } catch (NoSuchFieldException e3) {
                return null;
            }
        } catch (ClassNotFoundException e4) {
            return null;
        }
    }

    private static int configFieldNameToNum(String configName) {
        if (configName.equals("columnName")) {
            return COLUMN_NAME;
        }
        if (configName.equals("dataType")) {
            return DATA_TYPE;
        }
        if (configName.equals("defaultValue")) {
            return DEFAULT_VALUE;
        }
        if (configName.equals(SettingsJsonConstants.ICON_WIDTH_KEY)) {
            return WIDTH;
        }
        if (configName.equals("canBeNull")) {
            return CAN_BE_NULL;
        }
        if (configName.equals(WorksListUri.KEY_ID)) {
            return ID;
        }
        if (configName.equals("generatedId")) {
            return GENERATED_ID;
        }
        if (configName.equals("generatedIdSequence")) {
            return GENERATED_ID_SEQUENCE;
        }
        if (configName.equals("foreign")) {
            return FOREIGN;
        }
        if (configName.equals("useGetSet")) {
            return USE_GET_SET;
        }
        if (configName.equals("unknownEnumName")) {
            return UNKNOWN_ENUM_NAME;
        }
        if (configName.equals("throwIfNull")) {
            return THROW_IF_NULL;
        }
        if (configName.equals("persisted")) {
            return PERSISTED;
        }
        if (configName.equals("format")) {
            return FORMAT;
        }
        if (configName.equals("unique")) {
            return UNIQUE;
        }
        if (configName.equals("uniqueCombo")) {
            return UNIQUE_COMBO;
        }
        if (configName.equals("index")) {
            return INDEX;
        }
        if (configName.equals("uniqueIndex")) {
            return UNIQUE_INDEX;
        }
        if (configName.equals("indexName")) {
            return INDEX_NAME;
        }
        if (configName.equals("uniqueIndexName")) {
            return UNIQUE_INDEX_NAME;
        }
        if (configName.equals("foreignAutoRefresh")) {
            return FOREIGN_AUTO_REFRESH;
        }
        if (configName.equals("maxForeignAutoRefreshLevel")) {
            return MAX_FOREIGN_AUTO_REFRESH_LEVEL;
        }
        if (configName.equals("persisterClass")) {
            return PERSISTER_CLASS;
        }
        if (configName.equals("allowGeneratedIdInsert")) {
            return ALLOW_GENERATED_ID_INSERT;
        }
        if (configName.equals("columnDefinition")) {
            return COLUMN_DEFINITON;
        }
        if (configName.equals("foreignAutoCreate")) {
            return FOREIGN_AUTO_CREATE;
        }
        if (configName.equals(ShareRequestParam.REQ_PARAM_VERSION)) {
            return VERSION;
        }
        if (configName.equals("foreignColumnName")) {
            return FOREIGN_COLUMN_NAME;
        }
        if (configName.equals(Keys.readOnly)) {
            return READ_ONLY;
        }
        throw new IllegalStateException("Could not find support for DatabaseField " + configName);
    }

    private static DatabaseFieldConfig configFromField(DatabaseType databaseType, String tableName, Field field) throws SQLException {
        if (configFieldNums == null) {
            return DatabaseFieldConfig.fromField(databaseType, tableName, field);
        }
        DatabaseField databaseField = (DatabaseField) field.getAnnotation(DatabaseField.class);
        DatabaseFieldConfig config = null;
        if (databaseField != null) {
            try {
                config = buildConfig(databaseField, tableName, field);
            } catch (Exception e) {
            }
        }
        if (config == null) {
            return DatabaseFieldConfig.fromField(databaseType, tableName, field);
        }
        workedC += COLUMN_NAME;
        return config;
    }

    private static DatabaseFieldConfig buildConfig(DatabaseField databaseField, String tableName, Field field) throws Exception {
        DatabaseFieldConfig databaseFieldConfig = null;
        InvocationHandler proxy = Proxy.getInvocationHandler(databaseField);
        if (proxy.getClass() == annotationFactoryClazz) {
            Object elementsObject = elementsField.get(proxy);
            if (elementsObject != null) {
                databaseFieldConfig = new DatabaseFieldConfig(field.getName());
                Object[] objs = (Object[]) elementsObject;
                for (int i = 0; i < configFieldNums.length; i += COLUMN_NAME) {
                    Object value = valueField.get(objs[i]);
                    if (value != null) {
                        assignConfigField(configFieldNums[i], databaseFieldConfig, field, value);
                    }
                }
            }
        }
        return databaseFieldConfig;
    }

    private static void assignConfigField(int configNum, DatabaseFieldConfig config, Field field, Object value) {
        switch (configNum) {
            case COLUMN_NAME /*1*/:
                config.setColumnName(valueIfNotBlank((String) value));
            case DATA_TYPE /*2*/:
                config.setDataType((DataType) value);
            case DEFAULT_VALUE /*3*/:
                String defaultValue = (String) value;
                if (defaultValue != null && !defaultValue.equals(DatabaseField.DEFAULT_STRING)) {
                    config.setDefaultValue(defaultValue);
                }
            case WIDTH /*4*/:
                config.setWidth(((Integer) value).intValue());
            case CAN_BE_NULL /*5*/:
                config.setCanBeNull(((Boolean) value).booleanValue());
            case ID /*6*/:
                config.setId(((Boolean) value).booleanValue());
            case GENERATED_ID /*7*/:
                config.setGeneratedId(((Boolean) value).booleanValue());
            case GENERATED_ID_SEQUENCE /*8*/:
                config.setGeneratedIdSequence(valueIfNotBlank((String) value));
            case FOREIGN /*9*/:
                config.setForeign(((Boolean) value).booleanValue());
            case USE_GET_SET /*10*/:
                config.setUseGetSet(((Boolean) value).booleanValue());
            case UNKNOWN_ENUM_NAME /*11*/:
                config.setUnknownEnumValue(DatabaseFieldConfig.findMatchingEnumVal(field, (String) value));
            case THROW_IF_NULL /*12*/:
                config.setThrowIfNull(((Boolean) value).booleanValue());
            case PERSISTED /*13*/:
                config.setPersisted(((Boolean) value).booleanValue());
            case FORMAT /*14*/:
                config.setFormat(valueIfNotBlank((String) value));
            case UNIQUE /*15*/:
                config.setUnique(((Boolean) value).booleanValue());
            case UNIQUE_COMBO /*16*/:
                config.setUniqueCombo(((Boolean) value).booleanValue());
            case INDEX /*17*/:
                config.setIndex(((Boolean) value).booleanValue());
            case UNIQUE_INDEX /*18*/:
                config.setUniqueIndex(((Boolean) value).booleanValue());
            case INDEX_NAME /*19*/:
                config.setIndexName(valueIfNotBlank((String) value));
            case UNIQUE_INDEX_NAME /*20*/:
                config.setUniqueIndexName(valueIfNotBlank((String) value));
            case FOREIGN_AUTO_REFRESH /*21*/:
                config.setForeignAutoRefresh(((Boolean) value).booleanValue());
            case MAX_FOREIGN_AUTO_REFRESH_LEVEL /*22*/:
                config.setMaxForeignAutoRefreshLevel(((Integer) value).intValue());
            case PERSISTER_CLASS /*23*/:
                config.setPersisterClass((Class) value);
            case ALLOW_GENERATED_ID_INSERT /*24*/:
                config.setAllowGeneratedIdInsert(((Boolean) value).booleanValue());
            case COLUMN_DEFINITON /*25*/:
                config.setColumnDefinition(valueIfNotBlank((String) value));
            case FOREIGN_AUTO_CREATE /*26*/:
                config.setForeignAutoCreate(((Boolean) value).booleanValue());
            case VERSION /*27*/:
                config.setVersion(((Boolean) value).booleanValue());
            case FOREIGN_COLUMN_NAME /*28*/:
                config.setForeignColumnName(valueIfNotBlank((String) value));
            case READ_ONLY /*29*/:
                config.setReadOnly(((Boolean) value).booleanValue());
            default:
                throw new IllegalStateException("Could not find support for DatabaseField number " + configNum);
        }
    }

    private static String valueIfNotBlank(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        return value;
    }
}
