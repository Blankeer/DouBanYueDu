package com.google.tagmanager;

import android.content.Context;
import com.google.analytics.containertag.common.FunctionType;
import com.google.analytics.containertag.common.Key;
import com.google.analytics.midtier.proto.containertag.TypeSystem.Value;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.HitTypes;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.common.util.VisibleForTesting;
import com.sina.weibo.sdk.component.WidgetRequestParam;
import com.sina.weibo.sdk.register.mobile.SelectCountryActivity;
import com.tencent.connect.common.Constants;
import io.realm.internal.Table;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class UniversalAnalyticsTag extends TrackingTag {
    private static final String ACCOUNT;
    private static final String ANALYTICS_FIELDS;
    private static final String ANALYTICS_PASS_THROUGH;
    private static final String DEFAULT_TRACKING_ID = "_GTM_DEFAULT_TRACKER_";
    private static final String ID;
    private static final String TRACK_TRANSACTION;
    private static final String TRANSACTION_DATALAYER_MAP;
    private static final String TRANSACTION_ITEM_DATALAYER_MAP;
    private static Map<String, String> defaultItemMap;
    private static Map<String, String> defaultTransactionMap;
    private final DataLayer mDataLayer;
    private final TrackerProvider mTrackerProvider;
    private final Set<String> mTurnOffAnonymizeIpValues;

    static {
        ID = FunctionType.UNIVERSAL_ANALYTICS.toString();
        ACCOUNT = Key.ACCOUNT.toString();
        ANALYTICS_PASS_THROUGH = Key.ANALYTICS_PASS_THROUGH.toString();
        ANALYTICS_FIELDS = Key.ANALYTICS_FIELDS.toString();
        TRACK_TRANSACTION = Key.TRACK_TRANSACTION.toString();
        TRANSACTION_DATALAYER_MAP = Key.TRANSACTION_DATALAYER_MAP.toString();
        TRANSACTION_ITEM_DATALAYER_MAP = Key.TRANSACTION_ITEM_DATALAYER_MAP.toString();
    }

    public static String getFunctionId() {
        return ID;
    }

    public UniversalAnalyticsTag(Context context, DataLayer dataLayer) {
        this(context, dataLayer, new TrackerProvider(context));
    }

    @VisibleForTesting
    UniversalAnalyticsTag(Context context, DataLayer dataLayer, TrackerProvider trackerProvider) {
        super(ID, new String[0]);
        this.mDataLayer = dataLayer;
        this.mTrackerProvider = trackerProvider;
        this.mTurnOffAnonymizeIpValues = new HashSet();
        this.mTurnOffAnonymizeIpValues.add(Table.STRING_DEFAULT_VALUE);
        this.mTurnOffAnonymizeIpValues.add(Constants.VIA_RESULT_SUCCESS);
        this.mTurnOffAnonymizeIpValues.add("false");
    }

    private boolean checkBooleanProperty(Map<String, Value> tag, String key) {
        Value value = (Value) tag.get(key);
        return value == null ? false : Types.valueToBoolean(value).booleanValue();
    }

    public void evaluateTrackingTag(Map<String, Value> tag) {
        Tracker tracker = this.mTrackerProvider.getTracker(DEFAULT_TRACKING_ID);
        if (checkBooleanProperty(tag, ANALYTICS_PASS_THROUGH)) {
            tracker.send(convertToGaFields((Value) tag.get(ANALYTICS_FIELDS)));
        } else if (checkBooleanProperty(tag, TRACK_TRANSACTION)) {
            sendTransaction(tracker, tag);
        } else {
            Log.w("Ignoring unknown tag.");
        }
        this.mTrackerProvider.close(tracker);
    }

    private String getDataLayerString(String field) {
        Object data = this.mDataLayer.get(field);
        return data == null ? null : data.toString();
    }

    private List<Map<String, String>> getTransactionItems() {
        List<Object> data = this.mDataLayer.get("transactionProducts");
        if (data == null) {
            return null;
        }
        if (data instanceof List) {
            for (Object obj : data) {
                if (!(obj instanceof Map)) {
                    throw new IllegalArgumentException("Each element of transactionProducts should be of type Map.");
                }
            }
            return data;
        }
        throw new IllegalArgumentException("transactionProducts should be of type List.");
    }

    private void addParam(Map<String, String> itemParams, String gaKey, String value) {
        if (value != null) {
            itemParams.put(gaKey, value);
        }
    }

    private void sendTransaction(Tracker tracker, Map<String, Value> tag) {
        String transactionId = getDataLayerString("transactionId");
        if (transactionId == null) {
            Log.e("Cannot find transactionId in data layer.");
            return;
        }
        List<Map<String, String>> sendQueue = new LinkedList();
        try {
            Map<String, String> params = convertToGaFields((Value) tag.get(ANALYTICS_FIELDS));
            params.put(Fields.HIT_TYPE, HitTypes.TRANSACTION);
            for (Entry<String, String> entry : getTransactionFields(tag).entrySet()) {
                addParam(params, (String) entry.getValue(), getDataLayerString((String) entry.getKey()));
            }
            sendQueue.add(params);
            List<Map<String, String>> items = getTransactionItems();
            if (items != null) {
                for (Map<String, String> item : items) {
                    if (item.get(SelectCountryActivity.EXTRA_COUNTRY_NAME) == null) {
                        Log.e("Unable to send transaction item hit due to missing 'name' field.");
                        return;
                    }
                    Map<String, String> itemParams = convertToGaFields((Value) tag.get(ANALYTICS_FIELDS));
                    itemParams.put(Fields.HIT_TYPE, HitTypes.ITEM);
                    itemParams.put(Fields.TRANSACTION_ID, transactionId);
                    for (Entry<String, String> entry2 : getTransactionItemFields(tag).entrySet()) {
                        addParam(itemParams, (String) entry2.getValue(), (String) item.get(entry2.getKey()));
                    }
                    sendQueue.add(itemParams);
                }
            }
            for (Map<String, String> gaParam : sendQueue) {
                tracker.send(gaParam);
            }
        } catch (IllegalArgumentException e) {
            Log.e("Unable to send transaction", e);
        }
    }

    private Map<String, String> valueToMap(Value mapValue) {
        Map<Object, Object> o = Types.valueToObject(mapValue);
        if (!(o instanceof Map)) {
            return null;
        }
        Map<Object, Object> map = o;
        Map<String, String> stringMap = new LinkedHashMap();
        for (Entry entry : map.entrySet()) {
            stringMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return stringMap;
    }

    private Map<String, String> convertToGaFields(Value analyticsFields) {
        if (analyticsFields == null) {
            return new HashMap();
        }
        Map<String, String> params = valueToMap(analyticsFields);
        if (params == null) {
            return new HashMap();
        }
        String aip = (String) params.get(Fields.ANONYMIZE_IP);
        if (aip == null || !this.mTurnOffAnonymizeIpValues.contains(aip.toLowerCase())) {
            return params;
        }
        params.remove(Fields.ANONYMIZE_IP);
        return params;
    }

    private Map<String, String> getTransactionFields(Map<String, Value> tag) {
        Value map = (Value) tag.get(TRANSACTION_DATALAYER_MAP);
        if (map != null) {
            return valueToMap(map);
        }
        if (defaultTransactionMap == null) {
            HashMap<String, String> defaultMap = new HashMap();
            defaultMap.put("transactionId", Fields.TRANSACTION_ID);
            defaultMap.put("transactionAffiliation", Fields.TRANSACTION_AFFILIATION);
            defaultMap.put("transactionTax", Fields.TRANSACTION_TAX);
            defaultMap.put("transactionShipping", Fields.TRANSACTION_SHIPPING);
            defaultMap.put("transactionTotal", Fields.TRANSACTION_REVENUE);
            defaultMap.put("transactionCurrency", Fields.CURRENCY_CODE);
            defaultTransactionMap = defaultMap;
        }
        return defaultTransactionMap;
    }

    private Map<String, String> getTransactionItemFields(Map<String, Value> tag) {
        Value map = (Value) tag.get(TRANSACTION_ITEM_DATALAYER_MAP);
        if (map != null) {
            return valueToMap(map);
        }
        if (defaultItemMap == null) {
            HashMap<String, String> defaultMap = new HashMap();
            defaultMap.put(SelectCountryActivity.EXTRA_COUNTRY_NAME, Fields.ITEM_NAME);
            defaultMap.put("sku", Fields.ITEM_SKU);
            defaultMap.put(WidgetRequestParam.REQ_PARAM_COMMENT_CATEGORY, Fields.ITEM_CATEGORY);
            defaultMap.put("price", Fields.ITEM_PRICE);
            defaultMap.put("quantity", Fields.ITEM_QUANTITY);
            defaultMap.put("currency", Fields.CURRENCY_CODE);
            defaultItemMap = defaultMap;
        }
        return defaultItemMap;
    }
}
