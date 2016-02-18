package com.crashlytics.android.answers;

import io.fabric.sdk.android.Fabric;
import java.util.Map;

public abstract class AnswersEvent<T extends AnswersEvent> {
    public static final int MAX_NUM_ATTRIBUTES = 20;
    public static final int MAX_STRING_LENGTH = 100;
    final AnswersAttributes customAttributes;
    final AnswersEventValidator validator;

    public AnswersEvent() {
        this.validator = new AnswersEventValidator(MAX_NUM_ATTRIBUTES, MAX_STRING_LENGTH, Fabric.isDebuggable());
        this.customAttributes = new AnswersAttributes(this.validator);
    }

    Map<String, Object> getCustomAttributes() {
        return this.customAttributes.attributes;
    }

    public T putCustomAttribute(String key, String value) {
        this.customAttributes.put(key, value);
        return this;
    }

    public T putCustomAttribute(String key, Number value) {
        this.customAttributes.put(key, value);
        return this;
    }
}
