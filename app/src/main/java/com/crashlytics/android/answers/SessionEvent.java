package com.crashlytics.android.answers;

import android.app.Activity;
import java.util.Collections;
import java.util.Map;

final class SessionEvent {
    static final String ACTIVITY_KEY = "activity";
    static final String SESSION_ID_KEY = "sessionId";
    public final Map<String, Object> customAttributes;
    public final String customType;
    public final Map<String, String> details;
    public final Map<String, Object> predefinedAttributes;
    public final String predefinedType;
    public final SessionEventMetadata sessionEventMetadata;
    private String stringRepresentation;
    public final long timestamp;
    public final Type type;

    static class Builder {
        Map<String, Object> customAttributes;
        String customType;
        Map<String, String> details;
        Map<String, Object> predefinedAttributes;
        String predefinedType;
        final long timestamp;
        final Type type;

        public Builder(Type type) {
            this.type = type;
            this.timestamp = System.currentTimeMillis();
            this.details = null;
            this.customType = null;
            this.customAttributes = null;
            this.predefinedType = null;
            this.predefinedAttributes = null;
        }

        public Builder details(Map<String, String> details) {
            this.details = details;
            return this;
        }

        public Builder customType(String customType) {
            this.customType = customType;
            return this;
        }

        public Builder customAttributes(Map<String, Object> customAttributes) {
            this.customAttributes = customAttributes;
            return this;
        }

        public Builder predefinedType(String predefinedType) {
            this.predefinedType = predefinedType;
            return this;
        }

        public Builder predefinedAttributes(Map<String, Object> predefinedAttributes) {
            this.predefinedAttributes = predefinedAttributes;
            return this;
        }

        public SessionEvent build(SessionEventMetadata sessionEventMetadata) {
            return new SessionEvent(this.timestamp, this.type, this.details, this.customType, this.customAttributes, this.predefinedType, this.predefinedAttributes, null);
        }
    }

    enum Type {
        START,
        RESUME,
        PAUSE,
        STOP,
        CRASH,
        INSTALL,
        CUSTOM,
        PREDEFINED
    }

    public static Builder lifecycleEventBuilder(Type type, Activity activity) {
        return new Builder(type).details(Collections.singletonMap(ACTIVITY_KEY, activity.getClass().getName()));
    }

    public static Builder installEventBuilder() {
        return new Builder(Type.INSTALL);
    }

    public static Builder crashEventBuilder(String sessionId) {
        return new Builder(Type.CRASH).details(Collections.singletonMap(SESSION_ID_KEY, sessionId));
    }

    public static Builder customEventBuilder(CustomEvent event) {
        return new Builder(Type.CUSTOM).customType(event.getCustomType()).customAttributes(event.getCustomAttributes());
    }

    public static Builder predefinedEventBuilder(PredefinedEvent<?> event) {
        return new Builder(Type.PREDEFINED).predefinedType(event.getPredefinedType()).predefinedAttributes(event.getPredefinedAttributes()).customAttributes(event.getCustomAttributes());
    }

    private SessionEvent(SessionEventMetadata sessionEventMetadata, long timestamp, Type type, Map<String, String> details, String customType, Map<String, Object> customAttributes, String predefinedType, Map<String, Object> predefinedAttributes) {
        this.sessionEventMetadata = sessionEventMetadata;
        this.timestamp = timestamp;
        this.type = type;
        this.details = details;
        this.customType = customType;
        this.customAttributes = customAttributes;
        this.predefinedType = predefinedType;
        this.predefinedAttributes = predefinedAttributes;
    }

    public String toString() {
        if (this.stringRepresentation == null) {
            this.stringRepresentation = "[" + getClass().getSimpleName() + ": " + "timestamp=" + this.timestamp + ", type=" + this.type + ", details=" + this.details + ", customType=" + this.customType + ", customAttributes=" + this.customAttributes + ", predefinedType=" + this.predefinedType + ", predefinedAttributes=" + this.predefinedAttributes + ", metadata=[" + this.sessionEventMetadata + "]]";
        }
        return this.stringRepresentation;
    }
}
