package com.douban.book.reader.util;

import java.util.Iterator;
import org.json.JSONObject;

public class JsonIterator implements Iterator<KeyValuePair> {
    private final JSONObject mJsonObject;
    private final Iterator<String> mKeyIterator;

    public JsonIterator(JSONObject json) {
        this.mJsonObject = json;
        this.mKeyIterator = json.keys();
    }

    public boolean hasNext() {
        return this.mKeyIterator.hasNext();
    }

    public KeyValuePair next() {
        String nextKey = (String) this.mKeyIterator.next();
        return new KeyValuePair(nextKey, this.mJsonObject.opt(nextKey));
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove unsupported.");
    }
}
