package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;

public class VerifyResult implements Identifiable {
    public boolean isValid;

    public Object getId() {
        return Integer.valueOf(0);
    }
}
