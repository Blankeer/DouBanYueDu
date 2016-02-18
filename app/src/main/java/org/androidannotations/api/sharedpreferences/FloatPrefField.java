package org.androidannotations.api.sharedpreferences;

import android.content.SharedPreferences;
import io.realm.internal.Table;

public final class FloatPrefField extends AbstractPrefField<Float> {
    FloatPrefField(SharedPreferences sharedPreferences, String key, Float defaultValue) {
        super(sharedPreferences, key, defaultValue);
    }

    public Float getOr(Float defaultValue) {
        Float valueOf;
        try {
            valueOf = Float.valueOf(this.sharedPreferences.getFloat(this.key, defaultValue.floatValue()));
        } catch (ClassCastException e) {
            try {
                valueOf = Float.valueOf(Float.parseFloat(this.sharedPreferences.getString(this.key, Table.STRING_DEFAULT_VALUE + defaultValue)));
            } catch (Exception e2) {
                throw e;
            }
        }
        return valueOf;
    }

    protected void putInternal(Float value) {
        apply(edit().putFloat(this.key, value.floatValue()));
    }
}
