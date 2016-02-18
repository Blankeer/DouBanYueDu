package com.douban.amonsul.model;

import android.content.Context;
import android.text.TextUtils;
import com.douban.amonsul.StatConstant;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatUtils;
import com.douban.amonsul.device.AppInfo;
import com.douban.book.reader.helper.WorksListUri;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import io.realm.internal.Table;
import org.json.JSONException;
import org.json.JSONObject;

public class StatEvent {
    private static final String TAG;
    private String action;
    private String activity;
    private String attributes;
    private int count;
    private String date;
    private boolean extra;
    private String id;
    private String label;
    private double lac;
    private double lng;
    private String name;
    private String net;
    private long timeStamp;
    private int type;
    private String version;

    static {
        TAG = StatEvent.class.getSimpleName();
    }

    public StatEvent() {
        this.lac = 0.0d;
        this.lng = 0.0d;
        this.extra = true;
        this.count = 1;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getActivity() {
        return this.activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public double getLac() {
        return this.lac;
    }

    public void setLac(double lac) {
        this.lac = lac;
    }

    public double getLng() {
        return this.lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getNet() {
        return this.net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getAttributes() {
        return this.attributes;
    }

    private static StatEvent basicEvent(Context context, String eventId, String label, int count) {
        StatEvent evt = new StatEvent();
        evt.setName(eventId);
        if (!TextUtils.isEmpty(label)) {
            evt.setLabel(label);
        }
        evt.setCount(count);
        evt.setTimeStamp(System.currentTimeMillis());
        evt.setDate(StatUtils.getLocalTimeStamp());
        evt.setId(evt.getName() + EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR + evt.getTimeStamp());
        if (context != null) {
            evt.setActivity(context.getClass().getName());
            evt.setVersion(AppInfo.getVersionName(context));
        }
        return evt;
    }

    public static StatEvent setupEvent(Context context, String eventId, String label, int count, String action) {
        return setupEvent(context, eventId, label, count, action, Table.STRING_DEFAULT_VALUE);
    }

    public static StatEvent setupEvent(Context context, String eventId, String label, int count, String action, String attributes) {
        if (StatConstant.STAT_EVENT_ACTION_BEGIN.equals(action)) {
            return setupBeginEvent(context, eventId, label, count);
        }
        if (StatConstant.STAT_EVENT_ACTION_END.equals(action)) {
            return setupEndEvent(context, eventId, label, count);
        }
        StatEvent statEvent = setupEvent(context, eventId, label, count);
        statEvent.setAction(action);
        statEvent.setAttributes(attributes);
        return statEvent;
    }

    public static StatEvent setupEvent(Context context, String eventId, String label, int count) {
        StatEvent evt = basicEvent(context, eventId, label, count);
        if (context != null) {
            double[] loc = AppInfo.getLocation(context);
            if (loc != null && loc.length == 2) {
                evt.setLac(loc[0]);
                evt.setLng(loc[1]);
            }
            evt.setNet(AppInfo.getNetworkType(context));
        }
        return evt;
    }

    public static StatEvent setupBeginEvent(Context context, String eventId, String label, int count) {
        StatEvent statEvent = basicEvent(context, eventId, label, count);
        statEvent.setAction(StatConstant.STAT_EVENT_ACTION_BEGIN);
        return statEvent;
    }

    public static StatEvent setupEndEvent(Context context, String eventId, String label, int count) {
        StatEvent statEvent = basicEvent(context, eventId, label, count);
        statEvent.setAction(StatConstant.STAT_EVENT_ACTION_END);
        statEvent.setType(4);
        return statEvent;
    }

    public void parseJson(String jsonStr) {
        try {
            JSONObject obj = new JSONObject(jsonStr);
            this.id = obj.optString(WorksListUri.KEY_ID);
            this.name = obj.optString(StatConstant.JSON_KEY_EVENT_NAME);
            this.date = obj.optString(StatConstant.JSON_KEY_EVENT_DATE);
            this.activity = obj.optString(StatConstant.JSON_KEY_EVENT_PAGE);
            String tmp = obj.optString(StatConstant.JSON_KEY_EVENT_ACTION);
            if (!tmp.equals(Table.STRING_DEFAULT_VALUE)) {
                this.action = tmp;
            }
            this.count = obj.optInt(StatConstant.JSON_KEY_EVENT_COUNT);
            double db = obj.optDouble(StatConstant.JSON_KEY_LAC);
            if (db != Double.NaN) {
                this.lac = db;
            }
            db = obj.optDouble(StatConstant.JSON_KEY_LNG);
            if (db != Double.NaN) {
                this.lng = db;
            }
            this.label = obj.optString(StatConstant.JSON_KEY_EVENT_LABEL, null);
            this.net = obj.optString(StatConstant.JSON_KEY_NET, Table.STRING_DEFAULT_VALUE);
            this.version = obj.optString(StatConstant.JSON_KEY_EVENT_VERSION, Table.STRING_DEFAULT_VALUE);
            this.attributes = obj.optString(StatConstant.JSON_KEY_EVENT_ATTRIBUTES, Table.STRING_DEFAULT_VALUE);
        } catch (Throwable e) {
            StatLogger.e(TAG, e);
        }
    }

    public JSONObject toJson(Context context, boolean useid) {
        Throwable e;
        JSONObject obj = null;
        try {
            JSONObject obj2 = new JSONObject();
            if (useid) {
                try {
                    obj2.put(WorksListUri.KEY_ID, this.id);
                } catch (JSONException e2) {
                    e = e2;
                    obj = obj2;
                    StatLogger.e(TAG, e);
                    return obj;
                }
            }
            obj2.put(StatConstant.JSON_KEY_EVENT_NAME, this.name);
            obj2.put(StatConstant.JSON_KEY_EVENT_DATE, this.timeStamp);
            obj2.put(StatConstant.JSON_KEY_EVENT_PAGE, this.activity);
            obj2.put(StatConstant.JSON_KEY_EVENT_ATTRIBUTES, this.attributes);
            if (this.version != null) {
                obj2.put(StatConstant.JSON_KEY_EVENT_VERSION, this.version);
            }
            if (this.type == 1) {
                obj2.put(StatConstant.JSON_KEY_EVENT_COUNT, this.count);
            }
            if (this.action != null) {
                obj2.put(StatConstant.JSON_KEY_EVENT_ACTION, this.action);
            }
            if (!TextUtils.isEmpty(this.label)) {
                obj2.put(StatConstant.JSON_KEY_EVENT_LABEL, this.label);
            }
            if (this.extra) {
                if (this.lac > 0.0d) {
                    obj2.put(StatConstant.JSON_KEY_LAC, this.lac);
                }
                if (this.lng > 0.0d) {
                    obj2.put(StatConstant.JSON_KEY_LNG, this.lng);
                }
                obj2.put(StatConstant.JSON_KEY_NET, this.net);
            }
            return obj2;
        } catch (JSONException e3) {
            e = e3;
            StatLogger.e(TAG, e);
            return obj;
        }
    }

    public String toString() {
        return "StatEvent{name='" + this.name + '\'' + ", date='" + this.date + '\'' + ", type=" + this.type + ", count=" + this.count + ", action='" + this.action + '\'' + ", label='" + this.label + '\'' + ", lac=" + this.lac + ", lng=" + this.lng + ", net='" + this.net + '\'' + ", version='" + this.version + '\'' + ", extra=" + this.extra + ", id='" + this.id + '\'' + '}';
    }
}
