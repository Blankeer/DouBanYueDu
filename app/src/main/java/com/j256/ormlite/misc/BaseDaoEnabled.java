package com.j256.ormlite.misc;

import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;

public abstract class BaseDaoEnabled<T, ID> {
    protected transient Dao<T, ID> dao;

    public int create() throws SQLException {
        checkForDao();
        return this.dao.create(this);
    }

    public int refresh() throws SQLException {
        checkForDao();
        return this.dao.refresh(this);
    }

    public int update() throws SQLException {
        checkForDao();
        return this.dao.update((Object) this);
    }

    public int updateId(ID newId) throws SQLException {
        checkForDao();
        return this.dao.updateId(this, newId);
    }

    public int delete() throws SQLException {
        checkForDao();
        return this.dao.delete((Object) this);
    }

    public String objectToString() {
        try {
            checkForDao();
            return this.dao.objectToString(this);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public ID extractId() throws SQLException {
        checkForDao();
        return this.dao.extractId(this);
    }

    public boolean objectsEqual(T other) throws SQLException {
        checkForDao();
        return this.dao.objectsEqual(this, other);
    }

    public void setDao(Dao<T, ID> dao) {
        this.dao = dao;
    }

    public Dao<T, ID> getDao() {
        return this.dao;
    }

    private void checkForDao() throws SQLException {
        if (this.dao == null) {
            throw new SQLException("Dao has not been set on " + getClass() + " object: " + this);
        }
    }
}
