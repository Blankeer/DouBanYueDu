package com.j256.ormlite.support;

import com.j256.ormlite.logger.Logger;
import java.sql.SQLException;

public abstract class BaseConnectionSource implements ConnectionSource {
    private ThreadLocal<NestedConnection> specialConnection;

    private static class NestedConnection {
        public final DatabaseConnection connection;
        private int nestedC;

        public NestedConnection(DatabaseConnection connection) {
            this.connection = connection;
            this.nestedC = 1;
        }

        public void increment() {
            this.nestedC++;
        }

        public int decrementAndGet() {
            this.nestedC--;
            return this.nestedC;
        }
    }

    public BaseConnectionSource() {
        this.specialConnection = new ThreadLocal();
    }

    public DatabaseConnection getSpecialConnection() {
        NestedConnection currentSaved = (NestedConnection) this.specialConnection.get();
        if (currentSaved == null) {
            return null;
        }
        return currentSaved.connection;
    }

    protected DatabaseConnection getSavedConnection() {
        NestedConnection nested = (NestedConnection) this.specialConnection.get();
        if (nested == null) {
            return null;
        }
        return nested.connection;
    }

    protected boolean isSavedConnection(DatabaseConnection connection) {
        NestedConnection currentSaved = (NestedConnection) this.specialConnection.get();
        if (currentSaved != null && currentSaved.connection == connection) {
            return true;
        }
        return false;
    }

    protected boolean saveSpecial(DatabaseConnection connection) throws SQLException {
        NestedConnection currentSaved = (NestedConnection) this.specialConnection.get();
        if (currentSaved == null) {
            this.specialConnection.set(new NestedConnection(connection));
            return true;
        } else if (currentSaved.connection != connection) {
            throw new SQLException("trying to save connection " + connection + " but already have saved connection " + currentSaved.connection);
        } else {
            currentSaved.increment();
            return false;
        }
    }

    protected boolean clearSpecial(DatabaseConnection connection, Logger logger) {
        NestedConnection currentSaved = (NestedConnection) this.specialConnection.get();
        if (connection == null) {
            return false;
        }
        if (currentSaved == null) {
            logger.error("no connection has been saved when clear() called");
            return false;
        } else if (currentSaved.connection == connection) {
            if (currentSaved.decrementAndGet() == 0) {
                this.specialConnection.set(null);
            }
            return true;
        } else {
            logger.error("connection saved {} is not the one being cleared {}", currentSaved.connection, (Object) connection);
            return false;
        }
    }
}
