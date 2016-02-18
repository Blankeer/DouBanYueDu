package io.realm.processor;

import io.fabric.sdk.android.services.network.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    static final Map<String, String> CASTING_TYPES;
    public static final String DEFAULT_MODULE_CLASS_NAME = "DefaultRealmModule";
    static final Map<String, String> JAVA_TO_COLUMN_TYPES;
    static final Map<String, String> JAVA_TO_REALM_TYPES;
    static final Map<String, String> NULLABLE_JAVA_TYPES;
    public static final String PROXY_SUFFIX = "RealmProxy";
    public static final String REALM_PACKAGE_NAME = "io.realm";
    public static final String TABLE_PREFIX = "class_";

    static {
        JAVA_TO_REALM_TYPES = new HashMap();
        JAVA_TO_REALM_TYPES.put("byte", "Long");
        JAVA_TO_REALM_TYPES.put("short", "Long");
        JAVA_TO_REALM_TYPES.put("int", "Long");
        JAVA_TO_REALM_TYPES.put("long", "Long");
        JAVA_TO_REALM_TYPES.put("float", "Float");
        JAVA_TO_REALM_TYPES.put("double", "Double");
        JAVA_TO_REALM_TYPES.put("boolean", "Boolean");
        JAVA_TO_REALM_TYPES.put("Byte", "Long");
        JAVA_TO_REALM_TYPES.put("Short", "Long");
        JAVA_TO_REALM_TYPES.put("Integer", "Long");
        JAVA_TO_REALM_TYPES.put("Long", "Long");
        JAVA_TO_REALM_TYPES.put("Float", "Float");
        JAVA_TO_REALM_TYPES.put("Double", "Double");
        JAVA_TO_REALM_TYPES.put("Boolean", "Boolean");
        JAVA_TO_REALM_TYPES.put("java.lang.String", "String");
        JAVA_TO_REALM_TYPES.put("java.util.Date", HttpRequest.HEADER_DATE);
        JAVA_TO_REALM_TYPES.put("byte[]", "BinaryByteArray");
        NULLABLE_JAVA_TYPES = new HashMap();
        NULLABLE_JAVA_TYPES.put("java.util.Date", "new Date(0)");
        NULLABLE_JAVA_TYPES.put("java.lang.String", "\"\"");
        NULLABLE_JAVA_TYPES.put("byte[]", "new byte[0]");
        JAVA_TO_COLUMN_TYPES = new HashMap();
        JAVA_TO_COLUMN_TYPES.put("byte", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("short", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("int", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("long", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("float", "ColumnType.FLOAT");
        JAVA_TO_COLUMN_TYPES.put("double", "ColumnType.DOUBLE");
        JAVA_TO_COLUMN_TYPES.put("boolean", "ColumnType.BOOLEAN");
        JAVA_TO_COLUMN_TYPES.put("Byte", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("Short", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("Integer", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("Long", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("Float", "ColumnType.FLOAT");
        JAVA_TO_COLUMN_TYPES.put("Double", "ColumnType.DOUBLE");
        JAVA_TO_COLUMN_TYPES.put("Boolean", "ColumnType.BOOLEAN");
        JAVA_TO_COLUMN_TYPES.put("java.lang.String", "ColumnType.STRING");
        JAVA_TO_COLUMN_TYPES.put("java.util.Date", "ColumnType.DATE");
        JAVA_TO_COLUMN_TYPES.put("byte[]", "ColumnType.BINARY");
        CASTING_TYPES = new HashMap();
        CASTING_TYPES.put("byte", "long");
        CASTING_TYPES.put("short", "long");
        CASTING_TYPES.put("int", "long");
        CASTING_TYPES.put("long", "long");
        CASTING_TYPES.put("float", "float");
        CASTING_TYPES.put("double", "double");
        CASTING_TYPES.put("boolean", "boolean");
        CASTING_TYPES.put("Byte", "long");
        CASTING_TYPES.put("Short", "long");
        CASTING_TYPES.put("Integer", "long");
        CASTING_TYPES.put("Long", "long");
        CASTING_TYPES.put("Float", "float");
        CASTING_TYPES.put("Double", "double");
        CASTING_TYPES.put("Boolean", "boolean");
        CASTING_TYPES.put("java.lang.String", "String");
        CASTING_TYPES.put("java.util.Date", HttpRequest.HEADER_DATE);
        CASTING_TYPES.put("byte[]", "byte[]");
    }
}
