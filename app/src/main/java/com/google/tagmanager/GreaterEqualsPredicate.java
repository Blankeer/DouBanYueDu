package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class GreaterEqualsPredicate extends NumberPredicate {
    private static final String ID;

    static {
        ID = FunctionType.GREATER_EQUALS.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public GreaterEqualsPredicate() {
        super(ID);
    }

    protected boolean evaluateNumber(TypedNumber arg0, TypedNumber arg1, Map<String, Value> map) {
        return arg0.compareTo(arg1) >= 0;
    }
}
