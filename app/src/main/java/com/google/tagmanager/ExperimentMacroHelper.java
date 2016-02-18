package com.google.tagmanager;

import com.google.analytics.containertag.proto.Serving.GaExperimentRandom;
import com.google.analytics.containertag.proto.Serving.GaExperimentSupplemental;
import com.google.analytics.containertag.proto.Serving.Supplemental;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import java.util.Map;

public class ExperimentMacroHelper {
    public static void handleExperimentSupplemental(DataLayer dataLayer, Supplemental supplemental) {
        if (supplemental.experimentSupplemental == null) {
            Log.w("supplemental missing experimentSupplemental");
            return;
        }
        clearKeys(dataLayer, supplemental.experimentSupplemental);
        pushValues(dataLayer, supplemental.experimentSupplemental);
        setRandomValues(dataLayer, supplemental.experimentSupplemental);
    }

    private static void clearKeys(DataLayer dataLayer, GaExperimentSupplemental expSupplemental) {
        for (Value value : expSupplemental.valueToClear) {
            dataLayer.clearPersistentKeysWithPrefix(Types.valueToString(value));
        }
    }

    private static void pushValues(DataLayer dataLayer, GaExperimentSupplemental expSupplemental) {
        for (Value value : expSupplemental.valueToPush) {
            Map<Object, Object> map = valueToMap(value);
            if (map != null) {
                dataLayer.push(map);
            }
        }
    }

    private static void setRandomValues(DataLayer dataLayer, GaExperimentSupplemental expSupplemental) {
        for (GaExperimentRandom expRandom : expSupplemental.experimentRandom) {
            if (expRandom.key == null) {
                Log.w("GaExperimentRandom: No key");
            } else {
                Object random;
                Number random2 = dataLayer.get(expRandom.key);
                Long randomValue;
                if (random2 instanceof Number) {
                    randomValue = Long.valueOf(random2.longValue());
                } else {
                    randomValue = null;
                }
                long minRandom = expRandom.minRandom;
                long maxRandom = expRandom.maxRandom;
                if (!expRandom.retainOriginalValue || randomValue == null || randomValue.longValue() < minRandom || randomValue.longValue() > maxRandom) {
                    if (minRandom <= maxRandom) {
                        random = Long.valueOf(Math.round((Math.random() * ((double) (maxRandom - minRandom))) + ((double) minRandom)));
                    } else {
                        Log.w("GaExperimentRandom: random range invalid");
                    }
                }
                dataLayer.clearPersistentKeysWithPrefix(expRandom.key);
                Map<Object, Object> map = dataLayer.expandKeyValue(expRandom.key, random);
                if (expRandom.lifetimeInMilliseconds > 0) {
                    if (map.containsKey("gtm")) {
                        Map<Object, Object> o = map.get("gtm");
                        if (o instanceof Map) {
                            o.put("lifetime", Long.valueOf(expRandom.lifetimeInMilliseconds));
                        } else {
                            Log.w("GaExperimentRandom: gtm not a map");
                        }
                    } else {
                        Object[] objArr = new Object[2];
                        objArr[0] = "lifetime";
                        objArr[1] = Long.valueOf(expRandom.lifetimeInMilliseconds);
                        map.put("gtm", DataLayer.mapOf(objArr));
                    }
                }
                dataLayer.push(map);
            }
        }
    }

    private static Map<Object, Object> valueToMap(Value value) {
        Map<Object, Object> valueAsObject = Types.valueToObject(value);
        if (valueAsObject instanceof Map) {
            return valueAsObject;
        }
        Log.w("value: " + valueAsObject + " is not a map value, ignored.");
        return null;
    }
}
