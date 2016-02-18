package org.androidannotations.api.sharedpreferences;

import android.content.SharedPreferences;
import java.util.Set;

public abstract class SharedPreferencesHelper {
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public final SharedPreferences getSharedPreferences() {
        return this.sharedPreferences;
    }

    public final void clear() {
        SharedPreferencesCompat.apply(this.sharedPreferences.edit().clear());
    }

    protected IntPrefField intField(String key, int defaultValue) {
        return new IntPrefField(this.sharedPreferences, key, Integer.valueOf(defaultValue));
    }

    protected StringPrefField stringField(String key, String defaultValue) {
        return new StringPrefField(this.sharedPreferences, key, defaultValue);
    }

    protected StringSetPrefField stringSetField(String key, Set<String> defaultValue) {
        return new StringSetPrefField(this.sharedPreferences, key, defaultValue);
    }

    protected BooleanPrefField booleanField(String key, boolean defaultValue) {
        return new BooleanPrefField(this.sharedPreferences, key, Boolean.valueOf(defaultValue));
    }

    protected FloatPrefField floatField(String key, float defaultValue) {
        return new FloatPrefField(this.sharedPreferences, key, Float.valueOf(defaultValue));
    }

    protected LongPrefField longField(String key, long defaultValue) {
        return new LongPrefField(this.sharedPreferences, key, Long.valueOf(defaultValue));
    }
}
