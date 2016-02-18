package com.google.tagmanager;

import com.google.analytics.containertag.proto.Debug.EventInfo;
import com.google.analytics.containertag.proto.Debug.ResolvedFunctionCall;
import com.google.analytics.containertag.proto.Debug.ResolvedProperty;
import com.google.analytics.containertag.proto.Debug.ResolvedRule;

class ArrayUtils {
    private ArrayUtils() {
    }

    public static EventInfo[] appendToArray(EventInfo[] from, EventInfo itemToAppend) {
        EventInfo[] result = new EventInfo[(from.length + 1)];
        System.arraycopy(from, 0, result, 0, from.length);
        result[from.length] = itemToAppend;
        return result;
    }

    public static ResolvedFunctionCall[] appendToArray(ResolvedFunctionCall[] from, ResolvedFunctionCall itemToAppend) {
        ResolvedFunctionCall[] result = new ResolvedFunctionCall[(from.length + 1)];
        System.arraycopy(from, 0, result, 0, from.length);
        result[from.length] = itemToAppend;
        return result;
    }

    public static ResolvedProperty[] appendToArray(ResolvedProperty[] from, ResolvedProperty itemToAppend) {
        ResolvedProperty[] result = new ResolvedProperty[(from.length + 1)];
        System.arraycopy(from, 0, result, 0, from.length);
        result[from.length] = itemToAppend;
        return result;
    }

    public static ResolvedRule[] appendToArray(ResolvedRule[] from, ResolvedRule itemToAppend) {
        ResolvedRule[] result = new ResolvedRule[(from.length + 1)];
        System.arraycopy(from, 0, result, 0, from.length);
        result[from.length] = itemToAppend;
        return result;
    }
}
