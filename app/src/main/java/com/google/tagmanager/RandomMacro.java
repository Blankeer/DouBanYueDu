package com.google.tagmanager;

import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

class RandomMacro extends FunctionCallImplementation {
    private static final String ID;
    private static final String MAX;
    private static final String MIN;

    static {
        ID = FunctionType.RANDOM.toString();
        MIN = Key.MIN.toString();
        MAX = Key.MAX.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public RandomMacro() {
        super(ID, new String[0]);
    }

    public boolean isCacheable() {
        return false;
    }

    public Value evaluate(Map<String, Value> parameters) {
        double min = 0.0d;
        double max = 2.147483647E9d;
        Value minParameter = (Value) parameters.get(MIN);
        Value maxParameter = (Value) parameters.get(MAX);
        if (!(minParameter == null || minParameter == Types.getDefaultValue() || maxParameter == null || maxParameter == Types.getDefaultValue())) {
            TypedNumber minValue = Types.valueToNumber(minParameter);
            TypedNumber maxValue = Types.valueToNumber(maxParameter);
            if (!(minValue == Types.getDefaultNumber() || maxValue == Types.getDefaultNumber())) {
                double minDouble = minValue.doubleValue();
                double maxDouble = maxValue.doubleValue();
                if (minDouble <= maxDouble) {
                    min = minDouble;
                    max = maxDouble;
                }
            }
        }
        return Types.objectToValue(Long.valueOf(Math.round((Math.random() * (max - min)) + min)));
    }
}
