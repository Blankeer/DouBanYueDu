package com.google.tagmanager;

import com.google.analytics.containertag.proto.Debug.ResolvedProperty;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;

class DebugResolvedPropertyBuilder implements ResolvedPropertyBuilder {
    private ResolvedProperty resolvedProperty;

    public DebugResolvedPropertyBuilder(ResolvedProperty resolvedProperty) {
        this.resolvedProperty = resolvedProperty;
    }

    public ValueBuilder createPropertyValueBuilder(Value propertyValue) {
        Value val = DebugValueBuilder.copyImmutableValue(propertyValue);
        this.resolvedProperty.value = val;
        return new DebugValueBuilder(val);
    }
}
