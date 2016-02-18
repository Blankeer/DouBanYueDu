package org.androidannotations.api.sharedpreferences;

import android.content.SharedPreferences;
import io.realm.internal.Table;

public final class IntPrefField extends AbstractPrefField<Integer> {
    IntPrefField(SharedPreferences sharedPreferences, String key, Integer defaultValue) {
        super(sharedPreferences, key, defaultValue);
    }

    public Integer getOr(Integer defaultValue) {
        Integer valueOf;
        try {
            valueOf = Integer.valueOf(this.sharedPreferences.getInt(this.key, defaultValue.intValue()));
        } catch (ClassCastException e) {
            try {
                valueOf = Integer.valueOf(Integer.parseInt(this.sharedPreferences.getString(this.key, Table.STRING_DEFAULT_VALUE + defaultValue)));
            } catch (Exception e2) {
                throw e;
            }
        }
        return valueOf;
    }

    protected void putInternal(Integer value) {
        apply(edit().putInt(this.key, value.intValue()));
    }
}
