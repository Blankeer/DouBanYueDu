package com.douban.book.reader.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTableConfig;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Collection;

public class AndroidDao<T, ID> extends BaseDaoImpl<T, ID> {
    public AndroidDao(Class<T> dataClass) throws SQLException {
        super(dataClass);
    }

    public AndroidDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, (Class) dataClass);
    }

    public AndroidDao(ConnectionSource connectionSource, DatabaseTableConfig<T> tableConfig) throws SQLException {
        super(connectionSource, (DatabaseTableConfig) tableConfig);
    }

    public void bulkInsert(Collection<T> dataList) throws SQLException {
        DatabaseConnection conn = null;
        try {
            conn = startThreadConnection();
            Savepoint savepoint = conn.setSavePoint("bulk_insert");
            for (T data : dataList) {
                createOrUpdate(data);
            }
            conn.commit(savepoint);
            if (conn != null) {
                endThreadConnection(conn);
            }
        } catch (Throwable th) {
            if (conn != null) {
                endThreadConnection(conn);
            }
        }
    }
}
