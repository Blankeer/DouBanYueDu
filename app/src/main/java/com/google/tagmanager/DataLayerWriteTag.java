package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.List;
import java.util.Map;

class DataLayerWriteTag extends TrackingTag {
    private static final String CLEAR_PERSISTENT_DATA_LAYER_PREFIX;
    private static final String ID;
    private static final String VALUE;
    private final DataLayer mDataLayer;

    static {
        ID = FunctionType.DATA_LAYER_WRITE.toString();
        VALUE = Key.VALUE.toString();
        CLEAR_PERSISTENT_DATA_LAYER_PREFIX = Key.CLEAR_PERSISTENT_DATA_LAYER_PREFIX.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public DataLayerWriteTag(DataLayer dataLayer) {
        super(ID, VALUE);
        this.mDataLayer = dataLayer;
    }

    public void evaluateTrackingTag(Map<String, Value> tag) {
        pushToDataLayer((Value) tag.get(VALUE));
        clearPersistent((Value) tag.get(CLEAR_PERSISTENT_DATA_LAYER_PREFIX));
    }

    private void clearPersistent(Value value) {
        if (value != null && value != Types.getDefaultObject()) {
            String prefix = Types.valueToString(value);
            if (prefix != Types.getDefaultString()) {
                this.mDataLayer.clearPersistentKeysWithPrefix(prefix);
            }
        }
    }

    private void pushToDataLayer(Value value) {
        if (value != null && value != Types.getDefaultObject()) {
            List<Object> o = Types.valueToObject(value);
            if (o instanceof List) {
                for (Map<Object, Object> obj : o) {
                    if (obj instanceof Map) {
                        this.mDataLayer.push(obj);
                    }
                }
            }
        }
    }
}
