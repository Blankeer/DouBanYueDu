package io.realm.internal;

import io.realm.RealmObject;
import java.util.HashMap;
import java.util.Map;

public class ColumnIndices {
    private Map<Class<? extends RealmObject>, Map<String, Long>> classes;

    public ColumnIndices() {
        this.classes = new HashMap();
    }

    public void addClass(Class<? extends RealmObject> clazz, Map<String, Long> indicies) {
        this.classes.put(clazz, indicies);
    }

    public Map<String, Long> getClassFields(Class<? extends RealmObject> clazz) {
        return (Map) this.classes.get(clazz);
    }

    public long getColumnIndex(Class<? extends RealmObject> clazz, String fieldName) {
        Map<String, Long> mapping = (Map) this.classes.get(clazz);
        if (mapping == null) {
            return -1;
        }
        Long index = (Long) mapping.get(fieldName);
        if (index != null) {
            return index.longValue();
        }
        return -1;
    }
}
