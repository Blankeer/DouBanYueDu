package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class RegexGroupMacro extends FunctionCallImplementation {
    private static final String GROUP;
    private static final String ID;
    private static final String IGNORE_CASE;
    private static final String REGEX;
    private static final String TO_MATCH;

    static {
        ID = FunctionType.REGEX_GROUP.toString();
        TO_MATCH = Key.ARG0.toString();
        REGEX = Key.ARG1.toString();
        IGNORE_CASE = Key.IGNORE_CASE.toString();
        GROUP = Key.GROUP.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public RegexGroupMacro() {
        super(ID, TO_MATCH, REGEX);
    }

    public boolean isCacheable() {
        return true;
    }

    public Value evaluate(Map<String, Value> parameters) {
        Value toMatch = (Value) parameters.get(TO_MATCH);
        Value regex = (Value) parameters.get(REGEX);
        if (toMatch == null || toMatch == Types.getDefaultValue() || regex == null || regex == Types.getDefaultValue()) {
            return Types.getDefaultValue();
        }
        int flags = 64;
        if (Types.valueToBoolean((Value) parameters.get(IGNORE_CASE)).booleanValue()) {
            flags = 64 | 2;
        }
        int groupNumber = 1;
        Value groupNumberValue = (Value) parameters.get(GROUP);
        if (groupNumberValue != null) {
            Long groupNumberLong = Types.valueToInt64(groupNumberValue);
            if (groupNumberLong == Types.getDefaultInt64()) {
                return Types.getDefaultValue();
            }
            groupNumber = groupNumberLong.intValue();
            if (groupNumber < 0) {
                return Types.getDefaultValue();
            }
        }
        try {
            String extracted = null;
            Matcher m = Pattern.compile(Types.valueToString(regex), flags).matcher(Types.valueToString(toMatch));
            if (m.find() && m.groupCount() >= groupNumber) {
                extracted = m.group(groupNumber);
            }
            return extracted == null ? Types.getDefaultValue() : Types.objectToValue(extracted);
        } catch (PatternSyntaxException e) {
            return Types.getDefaultValue();
        }
    }
}
