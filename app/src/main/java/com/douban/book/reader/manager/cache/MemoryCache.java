package com.douban.book.reader.manager.cache;

import java.util.HashMap;

public class MemoryCache<T extends Identifiable> extends MapCache<T> {
    public MemoryCache() {
        super(new HashMap());
    }
}
