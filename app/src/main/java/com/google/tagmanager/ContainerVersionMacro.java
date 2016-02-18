package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class ContainerVersionMacro extends FunctionCallImplementation {
    private static final String ID;
    private final Runtime mRuntime;

    static {
        ID = FunctionType.CONTAINER_VERSION.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public ContainerVersionMacro(Runtime runtime) {
        super(ID, new String[0]);
        this.mRuntime = runtime;
    }

    public boolean isCacheable() {
        return true;
    }

    public Value evaluate(Map<String, Value> map) {
        String containerVersion = this.mRuntime.getResource().getVersion();
        return containerVersion == null ? Types.getDefaultValue() : Types.objectToValue(containerVersion);
    }
}
