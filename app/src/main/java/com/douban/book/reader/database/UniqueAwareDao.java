package com.douban.book.reader.database;

import android.database.sqlite.SQLiteConstraintException;
import com.douban.book.reader.util.ExceptionUtils;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import java.sql.SQLException;
import java.util.List;

public class UniqueAwareDao<T, ID> extends AndroidDao<T, ID> {
    public UniqueAwareDao(Class<T> dataClass) throws SQLException {
        super(dataClass);
    }

    public UniqueAwareDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, (Class) dataClass);
    }

    public UniqueAwareDao(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig) throws SQLException {
        super(connectionSource, (DatabaseTableConfig) tableConfig);
    }

    public int create(T entity) throws SQLException {
        try {
            return super.create(entity);
        } catch (SQLException e) {
            if (ExceptionUtils.isCausedBy(e, SQLiteConstraintException.class)) {
                return createOrReuseId(entity);
            }
            throw e;
        }
    }

    public int update(T entity) throws SQLException {
        try {
            return super.update((Object) entity);
        } catch (SQLException e) {
            if (ExceptionUtils.isCausedBy(e, SQLiteConstraintException.class)) {
                return createOrUpdateId(entity);
            }
            throw e;
        }
    }

    public int createOrUpdateId(T entityToCreate) throws SQLException {
        T entityInDb = getConflictedItem(entityToCreate);
        if (entityInDb == null) {
            return super.create(entityToCreate);
        }
        updateId(entityInDb, extractId(entityToCreate));
        return update(entityToCreate);
    }

    public int createOrReuseId(T entityToCreate) throws SQLException {
        T entityInDb = getConflictedItem(entityToCreate);
        if (entityInDb == null) {
            return super.create(entityToCreate);
        }
        getTableInfo().getIdField().assignField(entityToCreate, extractId(entityInDb), false, null);
        return update(entityToCreate);
    }

    private T getConflictedItem(T entity) throws SQLException {
        QueryBuilder<T, ID> queryBuilder = queryBuilder();
        Where<T, ID> where = queryBuilder.where();
        boolean first = true;
        for (FieldType fieldType : getTableInfo().getFieldTypes()) {
            if (fieldType.isUniqueCombo()) {
                Object value = fieldType.getFieldValueIfNotDefault(entity);
                if (value != null) {
                    if (!first) {
                        where.and();
                    }
                    where.eq(fieldType.getColumnName(), value);
                    first = false;
                }
            }
        }
        List<T> itemList = queryBuilder.query();
        if (itemList.size() > 0) {
            return itemList.get(0);
        }
        return null;
    }
}
