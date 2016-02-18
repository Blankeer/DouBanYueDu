package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class ContainsPredicate extends StringPredicate {
    private static final String ID;

    static {
        ID = FunctionType.CONTAINS.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public ContainsPredicate() {
        super(ID);
    }

    protected boolean evaluateString(String arg0, String arg1, Map<String, Value> map) {
        return arg0.contains(arg1);
    }
}
