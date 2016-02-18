package com.j256.ormlite.table;

import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.field.DatabaseFieldConfigLoader;
import com.j256.ormlite.misc.SqlExceptionUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTableConfigLoader {
    private static final String CONFIG_FILE_END_MARKER = "# --table-end--";
    private static final String CONFIG_FILE_FIELDS_END = "# --table-fields-end--";
    private static final String CONFIG_FILE_FIELDS_START = "# --table-fields-start--";
    private static final String CONFIG_FILE_START_MARKER = "# --table-start--";
    private static final String FIELD_NAME_DATA_CLASS = "dataClass";
    private static final String FIELD_NAME_TABLE_NAME = "tableName";

    public static List<DatabaseTableConfig<?>> loadDatabaseConfigFromReader(BufferedReader reader) throws SQLException {
        List<DatabaseTableConfig<?>> list = new ArrayList();
        while (true) {
            DatabaseTableConfig<?> config = fromReader(reader);
            if (config == null) {
                return list;
            }
            list.add(config);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T> com.j256.ormlite.table.DatabaseTableConfig<T> fromReader(java.io.BufferedReader r8) throws java.sql.SQLException {
        /*
        r1 = new com.j256.ormlite.table.DatabaseTableConfig;
        r1.<init>();
        r0 = 0;
    L_0x0006:
        r3 = r8.readLine();	 Catch:{ IOException -> 0x000f }
        if (r3 != 0) goto L_0x0017;
    L_0x000c:
        if (r0 == 0) goto L_0x0070;
    L_0x000e:
        return r1;
    L_0x000f:
        r2 = move-exception;
        r5 = "Could not read DatabaseTableConfig from stream";
        r5 = com.j256.ormlite.misc.SqlExceptionUtil.create(r5, r2);
        throw r5;
    L_0x0017:
        r5 = "# --table-end--";
        r5 = r3.equals(r5);
        if (r5 != 0) goto L_0x000c;
    L_0x001f:
        r5 = "# --table-fields-start--";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x002b;
    L_0x0027:
        readFields(r8, r1);
        goto L_0x0006;
    L_0x002b:
        r5 = r3.length();
        if (r5 == 0) goto L_0x0006;
    L_0x0031:
        r5 = "#";
        r5 = r3.startsWith(r5);
        if (r5 != 0) goto L_0x0006;
    L_0x0039:
        r5 = "# --table-start--";
        r5 = r3.equals(r5);
        if (r5 != 0) goto L_0x0006;
    L_0x0041:
        r5 = "=";
        r6 = -2;
        r4 = r3.split(r5, r6);
        r5 = r4.length;
        r6 = 2;
        if (r5 == r6) goto L_0x0065;
    L_0x004c:
        r5 = new java.sql.SQLException;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "DatabaseTableConfig reading from stream cannot parse line: ";
        r6 = r6.append(r7);
        r6 = r6.append(r3);
        r6 = r6.toString();
        r5.<init>(r6);
        throw r5;
    L_0x0065:
        r5 = 0;
        r5 = r4[r5];
        r6 = 1;
        r6 = r4[r6];
        readTableField(r1, r5, r6);
        r0 = 1;
        goto L_0x0006;
    L_0x0070:
        r1 = 0;
        goto L_0x000e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.j256.ormlite.table.DatabaseTableConfigLoader.fromReader(java.io.BufferedReader):com.j256.ormlite.table.DatabaseTableConfig<T>");
    }

    public static <T> void write(BufferedWriter writer, DatabaseTableConfig<T> config) throws SQLException {
        try {
            writeConfig(writer, config);
        } catch (IOException e) {
            throw SqlExceptionUtil.create("Could not write config to writer", e);
        }
    }

    private static <T> void writeConfig(BufferedWriter writer, DatabaseTableConfig<T> config) throws IOException, SQLException {
        writer.append(CONFIG_FILE_START_MARKER);
        writer.newLine();
        if (config.getDataClass() != null) {
            writer.append(FIELD_NAME_DATA_CLASS).append('=').append(config.getDataClass().getName());
            writer.newLine();
        }
        if (config.getTableName() != null) {
            writer.append(FIELD_NAME_TABLE_NAME).append('=').append(config.getTableName());
            writer.newLine();
        }
        writer.append(CONFIG_FILE_FIELDS_START);
        writer.newLine();
        if (config.getFieldConfigs() != null) {
            for (DatabaseFieldConfig field : config.getFieldConfigs()) {
                DatabaseFieldConfigLoader.write(writer, field, config.getTableName());
            }
        }
        writer.append(CONFIG_FILE_FIELDS_END);
        writer.newLine();
        writer.append(CONFIG_FILE_END_MARKER);
        writer.newLine();
    }

    private static <T> void readTableField(DatabaseTableConfig<T> config, String field, String value) {
        if (field.equals(FIELD_NAME_DATA_CLASS)) {
            try {
                config.setDataClass(Class.forName(value));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Unknown class specified for dataClass: " + value);
            }
        } else if (field.equals(FIELD_NAME_TABLE_NAME)) {
            config.setTableName(value);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static <T> void readFields(java.io.BufferedReader r5, com.j256.ormlite.table.DatabaseTableConfig<T> r6) throws java.sql.SQLException {
        /*
        r2 = new java.util.ArrayList;
        r2.<init>();
    L_0x0005:
        r3 = r5.readLine();	 Catch:{ IOException -> 0x0017 }
        if (r3 == 0) goto L_0x0013;
    L_0x000b:
        r4 = "# --table-fields-end--";
        r4 = r3.equals(r4);
        if (r4 == 0) goto L_0x001f;
    L_0x0013:
        r6.setFieldConfigs(r2);
        return;
    L_0x0017:
        r0 = move-exception;
        r4 = "Could not read next field from config file";
        r4 = com.j256.ormlite.misc.SqlExceptionUtil.create(r4, r0);
        throw r4;
    L_0x001f:
        r1 = com.j256.ormlite.field.DatabaseFieldConfigLoader.fromReader(r5);
        if (r1 == 0) goto L_0x0013;
    L_0x0025:
        r2.add(r1);
        goto L_0x0005;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.j256.ormlite.table.DatabaseTableConfigLoader.readFields(java.io.BufferedReader, com.j256.ormlite.table.DatabaseTableConfig):void");
    }
}
