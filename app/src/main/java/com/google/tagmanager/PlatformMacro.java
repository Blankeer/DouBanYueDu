package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.umeng.analytics.a;
import java.util.Map;

class PlatformMacro extends FunctionCallImplementation {
    private static final String ID;
    private static final Value PLATFORM;

    static {
        ID = FunctionType.PLATFORM.toString();
        PLATFORM = Types.objectToValue(a.b);
    }

    public static String getFunctionId() {
        return ID;
    }

    public PlatformMacro() {
        super(ID, new String[0]);
    }

    public boolean isCacheable() {
        return true;
    }

    public Value evaluate(Map<String, Value> map) {
        return PLATFORM;
    }
}
