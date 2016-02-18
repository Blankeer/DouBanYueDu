package com.douban.book.reader.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.crashlytics.android.Crashlytics;
import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Char;
import io.realm.internal.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

public class Pref {
    private static Pref sAppPref;
    private final SharedPreferences mPref;

    public Pref(SharedPreferences pref) {
        this.mPref = pref;
    }

    public static Pref ofApp() {
        if (sAppPref == null) {
            sAppPref = new Pref(PreferenceManager.getDefaultSharedPreferences(App.get()));
        }
        return sAppPref;
    }

    public static Pref ofWorks(int worksId) {
        return ofName(String.format("works_%s", new Object[]{Integer.valueOf(worksId)}));
    }

    public static Pref ofPackage(int packageId) {
        return ofName(String.format("package_%s", new Object[]{Integer.valueOf(packageId)}));
    }

    public static Pref ofObj(Object dbName) {
        return ofName(String.format("obj_%s", new Object[]{dbName}));
    }

    public static Pref ofName(String dbName) {
        return new Pref(App.get().getSharedPreferences(dbName.replace(Char.SLASH, Char.UNDERLINE), 0));
    }

    public SharedPreferences getPref() {
        return this.mPref;
    }

    public void clear() {
        this.mPref.edit().clear().apply();
    }

    public void set(String key, Object value) {
        if (isKeyValid(key)) {
            Editor e = this.mPref.edit();
            if (value instanceof Boolean) {
                e.putBoolean(key, ((Boolean) value).booleanValue());
            } else if (value instanceof Float) {
                e.putFloat(key, ((Float) value).floatValue());
            } else if (value instanceof Integer) {
                e.putInt(key, ((Integer) value).intValue());
            } else if (value instanceof Long) {
                e.putLong(key, ((Long) value).longValue());
            } else if (value instanceof CharSequence) {
                e.putString(key, String.valueOf(value));
            } else if (value instanceof JSONObject) {
                e.putString(key, value.toString());
            } else if (value instanceof Date) {
                e.putLong(key, ((Date) value).getTime());
            } else if (value instanceof Set) {
                e.putStringSet(key, (Set) value);
            } else if (value != null) {
                e.putString(key, JsonUtils.toJson(value));
            }
            e.apply();
            return;
        }
        Crashlytics.logException(new IllegalArgumentException(String.format("Invalid pref key %s (value to save was %s)", new Object[]{key, value})));
    }

    public JSONObject getJSONObject(String key) {
        try {
            return new JSONObject(getString(key));
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public <T> T getObject(String key, Class<T> cls) {
        return JsonUtils.fromJson(getString(key), (Class) cls);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return this.mPref.getBoolean(key, defValue);
    }

    public boolean getBoolean(String key) {
        return this.mPref.getBoolean(key, false);
    }

    public float getFloat(String key, float defValue) {
        return this.mPref.getFloat(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return this.mPref.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return this.mPref.getLong(key, defValue);
    }

    public String getString(String key) {
        return this.mPref.getString(key, Table.STRING_DEFAULT_VALUE);
    }

    public String getString(String key, String defValue) {
        return this.mPref.getString(key, defValue);
    }

    public String getString(String key, int defValueRes) {
        return this.mPref.getString(key, Res.getString(defValueRes));
    }

    public Date getDate(String key, Date defaultValue) {
        long timestamp = this.mPref.getLong(key, -1);
        if (timestamp >= 0) {
            return new Date(timestamp);
        }
        return defaultValue;
    }

    public Set<String> getStringSet(String key) {
        return this.mPref.getStringSet(key, new HashSet());
    }

    public boolean contains(String key) {
        return this.mPref.contains(key);
    }

    public void remove(String key) {
        this.mPref.edit().remove(key).apply();
    }

    private boolean isKeyValid(String key) {
        return StringUtils.isNotEmpty(key) && !StringUtils.containsLineBreak(key);
    }
}
