package org.androidannotations.api.sharedpreferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class AbstractPrefField<T> {
    protected final T defaultValue;
    protected final String key;
    protected final SharedPreferences sharedPreferences;

    public abstract T getOr(T t);

    protected abstract void putInternal(T t);

    public AbstractPrefField(SharedPreferences sharedPreferences, String key, T defaultValue) {
        this.sharedPreferences = sharedPreferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public final boolean exists() {
        return this.sharedPreferences.contains(this.key);
    }

    public String key() {
        return this.key;
    }

    public final T get() {
        return getOr(this.defaultValue);
    }

    public final void put(T value) {
        if (value == null) {
            value = this.defaultValue;
        }
        putInternal(value);
    }

    public final void remove() {
        apply(edit().remove(this.key));
    }

    protected Editor edit() {
        return this.sharedPreferences.edit();
    }

    protected final void apply(Editor editor) {
        SharedPreferencesCompat.apply(editor);
    }
}
