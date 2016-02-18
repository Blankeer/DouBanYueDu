package org.jsoup.nodes;

import io.realm.internal.Table;

public class BooleanAttribute extends Attribute {
    public BooleanAttribute(String key) {
        super(key, Table.STRING_DEFAULT_VALUE);
    }

    protected boolean isBooleanAttribute() {
        return true;
    }
}
