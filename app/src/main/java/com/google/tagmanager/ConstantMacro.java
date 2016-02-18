package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class ConstantMacro extends FunctionCallImplementation {
    private static final String ID;
    private static final String VALUE;

    static {
        ID = FunctionType.CONSTANT.toString();
        VALUE = Key.VALUE.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public ConstantMacro() {
        super(ID, VALUE);
    }

    public static String getValueKey() {
        return VALUE;
    }

    public boolean isCacheable() {
        return true;
    }

    public Value evaluate(Map<String, Value> parameters) {
        return (Value) parameters.get(VALUE);
    }
}
