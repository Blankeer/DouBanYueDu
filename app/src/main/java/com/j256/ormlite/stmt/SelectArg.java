package com.j256.ormlite.stmt;

import com.j256.ormlite.field.SqlType;

public class SelectArg extends BaseArgumentHolder implements ArgumentHolder {
    private boolean hasBeenSet;
    private Object value;

    public SelectArg() {
        this.hasBeenSet = false;
        this.value = null;
    }

    public SelectArg(String columnName, Object value) {
        super(columnName);
        this.hasBeenSet = false;
        this.value = null;
        setValue(value);
    }

    public SelectArg(SqlType sqlType, Object value) {
        super(sqlType);
        this.hasBeenSet = false;
        this.value = null;
        setValue(value);
    }

    public SelectArg(SqlType sqlType) {
        super(sqlType);
        this.hasBeenSet = false;
        this.value = null;
    }

    public SelectArg(Object value) {
        this.hasBeenSet = false;
        this.value = null;
        setValue(value);
    }

    protected Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.hasBeenSet = true;
        this.value = value;
    }

    protected boolean isValueSet() {
        return this.hasBeenSet;
    }
}
