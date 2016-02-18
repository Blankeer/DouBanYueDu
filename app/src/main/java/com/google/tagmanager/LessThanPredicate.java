package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class LessThanPredicate extends NumberPredicate {
    private static final String ID;

    static {
        ID = FunctionType.LESS_THAN.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public LessThanPredicate() {
        super(ID);
    }

    protected boolean evaluateNumber(TypedNumber arg0, TypedNumber arg1, Map<String, Value> map) {
        return arg0.compareTo(arg1) < 0;
    }
}
