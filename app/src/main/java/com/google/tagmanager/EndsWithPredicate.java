package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class EndsWithPredicate extends StringPredicate {
    private static final String ID;

    static {
        ID = FunctionType.ENDS_WITH.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public EndsWithPredicate() {
        super(ID);
    }

    protected boolean evaluateString(String arg0, String arg1, Map<String, Value> map) {
        return arg0.endsWith(arg1);
    }
}
