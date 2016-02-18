package com.j256.ormlite.field;

import com.j256.ormlite.field.types.EnumStringType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataPersisterManager {
    private static final DataPersister DEFAULT_ENUM_PERSISTER;
    private static final Map<String, DataPersister> builtInMap;
    private static List<DataPersister> registeredPersisters;

    static {
        DEFAULT_ENUM_PERSISTER = EnumStringType.getSingleton();
        registeredPersisters = null;
        builtInMap = new HashMap();
        for (DataType dataType : DataType.values()) {
            DataPersister persister = dataType.getDataPersister();
            if (persister != null) {
                for (Class<?> clazz : persister.getAssociatedClasses()) {
                    builtInMap.put(clazz.getName(), persister);
                }
                if (persister.getAssociatedClassNames() != null) {
                    for (String className : persister.getAssociatedClassNames()) {
                        builtInMap.put(className, persister);
                    }
                }
            }
        }
    }

    private DataPersisterManager() {
    }

    public static void registerDataPersisters(DataPersister... dataPersisters) {
        List<DataPersister> newList = new ArrayList();
        if (registeredPersisters != null) {
            newList.addAll(registeredPersisters);
        }
        for (DataPersister persister : dataPersisters) {
            newList.add(persister);
        }
        registeredPersisters = newList;
    }

    public static void clear() {
        registeredPersisters = null;
    }

    public static DataPersister lookupForField(Field field) {
        if (registeredPersisters != null) {
            for (DataPersister persister : registeredPersisters) {
                if (persister.isValidForField(field)) {
                    return persister;
                }
                for (Class<?> clazz : persister.getAssociatedClasses()) {
                    if (field.getType() == clazz) {
                        return persister;
                    }
                }
            }
        }
        DataPersister dataPersister = (DataPersister) builtInMap.get(field.getType().getName());
        if (dataPersister != null) {
            return dataPersister;
        }
        if (field.getType().isEnum()) {
            return DEFAULT_ENUM_PERSISTER;
        }
        return null;
    }
}
