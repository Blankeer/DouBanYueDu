package com.douban.book.reader.util;

import java.util.Iterator;
import java.util.Map;

public class MapIterator implements Iterator<KeyValuePair> {
    private final Iterator<?> mKeyIterator;
    private final Map<?, ?> mMap;

    public MapIterator(Map<?, ?> map) {
        this.mMap = map;
        this.mKeyIterator = map.keySet().iterator();
    }

    public boolean hasNext() {
        return this.mKeyIterator.hasNext();
    }

    public KeyValuePair next() {
        Object nextKey = this.mKeyIterator.next();
        return new KeyValuePair(nextKey, this.mMap.get(nextKey));
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove unsupported.");
    }
}
