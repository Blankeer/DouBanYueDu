package com.douban.book.reader.util;

public class KeyValuePair {
    private final Object mKey;
    private final Object mValue;

    public KeyValuePair(Object key, Object value) {
        this.mKey = key;
        this.mValue = value;
    }

    public Object getKey() {
        return this.mKey;
    }

    public Object getValue() {
        return this.mValue;
    }

    public String toString() {
        return String.format("%s : %s", new Object[]{this.mKey, this.mValue});
    }
}
