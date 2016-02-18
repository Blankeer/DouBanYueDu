package com.douban.book.reader.manager.cache;

import com.douban.book.reader.database.AndroidDao;
import com.douban.book.reader.location.SortIndexable;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.OrmUtils;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class DbCache<T extends Identifiable> implements Cache<T> {
    private AndroidDao<T, Object> mDao;

    public DbCache(Class<T> cls) {
        this.mDao = null;
        this.mDao = OrmUtils.getDao(cls);
    }

    public T get(Object id) throws DataLoadException {
        try {
            return (Identifiable) this.mDao.queryForId(id);
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public void add(T entity) throws DataLoadException {
        try {
            if (entity instanceof SortIndexable) {
                ((SortIndexable) entity).calculateSortIndex();
            }
            this.mDao.createOrUpdate(entity);
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public void addAll(Collection<T> dataList) throws DataLoadException {
        for (T data : dataList) {
            if (data instanceof SortIndexable) {
                ((SortIndexable) data).calculateSortIndex();
            }
        }
        try {
            this.mDao.bulkInsert(dataList);
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public void delete(Object id) throws DataLoadException {
        try {
            this.mDao.deleteById(id);
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public void deleteAll() throws DataLoadException {
        try {
            this.mDao.deleteBuilder().delete();
        } catch (Throwable e) {
            throw new DataLoadException(e);
        }
    }

    public List<T> getAll() throws DataLoadException {
        throw new UnsupportedOperationException();
    }

    public void sync(Collection<T> collection) throws DataLoadException {
        throw new UnsupportedOperationException();
    }

    public boolean has(Object id) {
        try {
            return getDao().queryForId(id) != null;
        } catch (SQLException e) {
            return false;
        }
    }

    public Dao<T, Object> getDao() {
        return this.mDao;
    }
}
