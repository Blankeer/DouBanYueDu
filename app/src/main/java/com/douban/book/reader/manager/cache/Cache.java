package com.douban.book.reader.manager.cache;

import com.douban.book.reader.manager.exception.DataLoadException;
import java.util.Collection;
import java.util.List;

public interface Cache<T extends Identifiable> {
    void add(T t) throws DataLoadException;

    void addAll(Collection<T> collection) throws DataLoadException;

    void delete(Object obj) throws DataLoadException;

    void deleteAll() throws DataLoadException;

    T get(Object obj) throws DataLoadException;

    List<T> getAll() throws DataLoadException;

    void sync(Collection<T> collection) throws DataLoadException;
}
