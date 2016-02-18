package com.douban.book.reader.manager.cache;

import com.douban.book.reader.manager.exception.DataLoadException;

public interface Pinnable {
    void pin(Object obj) throws DataLoadException;

    void unpin(Object obj) throws DataLoadException;
}
