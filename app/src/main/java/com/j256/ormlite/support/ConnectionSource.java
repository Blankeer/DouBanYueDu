package com.j256.ormlite.support;

import com.j256.ormlite.db.DatabaseType;
import java.sql.SQLException;

public interface ConnectionSource {
    void clearSpecialConnection(DatabaseConnection databaseConnection);

    void close() throws SQLException;

    void closeQuietly();

    DatabaseType getDatabaseType();

    DatabaseConnection getReadOnlyConnection() throws SQLException;

    DatabaseConnection getReadWriteConnection() throws SQLException;

    DatabaseConnection getSpecialConnection();

    boolean isOpen();

    void releaseConnection(DatabaseConnection databaseConnection) throws SQLException;

    boolean saveSpecialConnection(DatabaseConnection databaseConnection) throws SQLException;
}
