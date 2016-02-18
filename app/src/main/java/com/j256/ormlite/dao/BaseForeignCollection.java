package com.j256.ormlite.dao;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.mapped.MappedPreparedStmt;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;

public abstract class BaseForeignCollection<T, ID> implements ForeignCollection<T>, Serializable {
    private static final long serialVersionUID = -5158840898186237589L;
    protected final transient Dao<T, ID> dao;
    private final transient FieldType foreignFieldType;
    private final transient boolean orderAscending;
    private final transient String orderColumn;
    private final transient Object parent;
    private final transient Object parentId;
    private transient PreparedQuery<T> preparedQuery;

    public abstract boolean remove(Object obj);

    public abstract boolean removeAll(Collection<?> collection);

    protected BaseForeignCollection(Dao<T, ID> dao, Object parent, Object parentId, FieldType foreignFieldType, String orderColumn, boolean orderAscending) {
        this.dao = dao;
        this.foreignFieldType = foreignFieldType;
        this.parentId = parentId;
        this.orderColumn = orderColumn;
        this.orderAscending = orderAscending;
        this.parent = parent;
    }

    public boolean add(T data) {
        try {
            return addElement(data);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not create data element in dao", e);
        }
    }

    public boolean addAll(Collection<? extends T> collection) {
        boolean changed = false;
        for (T data : collection) {
            try {
                if (addElement(data)) {
                    changed = true;
                }
            } catch (SQLException e) {
                throw new IllegalStateException("Could not create data elements in dao", e);
            }
        }
        return changed;
    }

    public boolean retainAll(Collection<?> collection) {
        if (this.dao == null) {
            return false;
        }
        boolean changed = false;
        CloseableIterator<T> iterator = closeableIterator();
        while (iterator.hasNext()) {
            try {
                if (!collection.contains(iterator.next())) {
                    iterator.remove();
                    changed = true;
                }
            } finally {
                try {
                    iterator.close();
                } catch (SQLException e) {
                }
            }
        }
        return changed;
    }

    public void clear() {
        if (this.dao != null) {
            CloseableIterator<T> iterator = closeableIterator();
            while (iterator.hasNext()) {
                try {
                    iterator.next();
                    iterator.remove();
                } finally {
                    try {
                        iterator.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }

    public int update(T data) throws SQLException {
        if (this.dao == null) {
            return 0;
        }
        return this.dao.update((Object) data);
    }

    public int refresh(T data) throws SQLException {
        if (this.dao == null) {
            return 0;
        }
        return this.dao.refresh(data);
    }

    protected PreparedQuery<T> getPreparedQuery() throws SQLException {
        if (this.dao == null) {
            return null;
        }
        if (this.preparedQuery == null) {
            SelectArg fieldArg = new SelectArg();
            fieldArg.setValue(this.parentId);
            QueryBuilder<T, ID> qb = this.dao.queryBuilder();
            if (this.orderColumn != null) {
                qb.orderBy(this.orderColumn, this.orderAscending);
            }
            this.preparedQuery = qb.where().eq(this.foreignFieldType.getColumnName(), fieldArg).prepare();
            if (this.preparedQuery instanceof MappedPreparedStmt) {
                this.preparedQuery.setParentInformation(this.parent, this.parentId);
            }
        }
        return this.preparedQuery;
    }

    private boolean addElement(T data) throws SQLException {
        if (this.dao == null) {
            return false;
        }
        if (this.parent != null && this.foreignFieldType.getFieldValueIfNotDefault(data) == null) {
            this.foreignFieldType.assignField(data, this.parent, true, null);
        }
        this.dao.create(data);
        return true;
    }
}
