package com.google.tagmanager;

import com.google.analytics.containertag.proto.Debug.ResolvedFunctionCall;
import com.google.analytics.containertag.proto.Debug.ResolvedProperty;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;

class DebugResolvedFunctionCallBuilder implements ResolvedFunctionCallBuilder {
    private ResolvedFunctionCall resolvedFunctionCall;

    public DebugResolvedFunctionCallBuilder(ResolvedFunctionCall functionCall) {
        this.resolvedFunctionCall = functionCall;
    }

    public ResolvedPropertyBuilder createResolvedPropertyBuilder(String key) {
        ResolvedProperty newProperty = new ResolvedProperty();
        newProperty.key = key;
        this.resolvedFunctionCall.properties = ArrayUtils.appendToArray(this.resolvedFunctionCall.properties, newProperty);
        return new DebugResolvedPropertyBuilder(newProperty);
    }

    public void setFunctionResult(Value functionResult) {
        this.resolvedFunctionCall.result = DebugValueBuilder.copyImmutableValue(functionResult);
    }
}
