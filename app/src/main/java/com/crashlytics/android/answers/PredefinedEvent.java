package com.crashlytics.android.answers;

import java.util.Map;

public abstract class PredefinedEvent<T extends PredefinedEvent> extends AnswersEvent<T> {
    final AnswersAttributes predefinedAttributes;

    abstract String getPredefinedType();

    public PredefinedEvent() {
        this.predefinedAttributes = new AnswersAttributes(this.validator);
    }

    Map<String, Object> getPredefinedAttributes() {
        return this.predefinedAttributes.attributes;
    }

    public String toString() {
        return "{type:\"" + getPredefinedType() + '\"' + ", predefinedAttributes:" + this.predefinedAttributes + ", customAttributes:" + this.customAttributes + "}";
    }
}
