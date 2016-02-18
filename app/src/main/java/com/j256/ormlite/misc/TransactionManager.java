package com.j256.ormlite.misc;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionManager {
    private static final String SAVE_POINT_PREFIX = "ORMLITE";
    private static final Logger logger;
    private static AtomicInteger savePointCounter;
    private ConnectionSource connectionSource;

    static {
        logger = LoggerFactory.getLogger(TransactionManager.class);
        savePointCounter = new AtomicInteger();
    }

    public TransactionManager(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        initialize();
    }

    public void initialize() {
        if (this.connectionSource == null) {
            throw new IllegalStateException("dataSource was not set on " + getClass().getSimpleName());
        }
    }

    public <T> T callInTransaction(Callable<T> callable) throws SQLException {
        return callInTransaction(this.connectionSource, callable);
    }

    public static <T> T callInTransaction(ConnectionSource connectionSource, Callable<T> callable) throws SQLException {
        DatabaseConnection connection = connectionSource.getReadWriteConnection();
        try {
            T callInTransaction = callInTransaction(connection, connectionSource.saveSpecialConnection(connection), connectionSource.getDatabaseType(), callable);
            return callInTransaction;
        } finally {
            connectionSource.clearSpecialConnection(connection);
            connectionSource.releaseConnection(connection);
        }
    }

    public static <T> T callInTransaction(DatabaseConnection connection, DatabaseType databaseType, Callable<T> callable) throws SQLException {
        return callInTransaction(connection, false, databaseType, callable);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T> T callInTransaction(com.j256.ormlite.support.DatabaseConnection r10, boolean r11, com.j256.ormlite.db.DatabaseType r12, java.util.concurrent.Callable<T> r13) throws java.sql.SQLException {
        /*
        r9 = 1;
        r0 = 0;
        r3 = 0;
        r5 = 0;
        if (r11 != 0) goto L_0x000c;
    L_0x0006:
        r6 = r12.isNestedSavePointsSupported();	 Catch:{ all -> 0x006c }
        if (r6 == 0) goto L_0x004a;
    L_0x000c:
        r6 = r10.isAutoCommitSupported();	 Catch:{ all -> 0x006c }
        if (r6 == 0) goto L_0x0023;
    L_0x0012:
        r0 = r10.isAutoCommit();	 Catch:{ all -> 0x006c }
        if (r0 == 0) goto L_0x0023;
    L_0x0018:
        r6 = 0;
        r10.setAutoCommit(r6);	 Catch:{ all -> 0x006c }
        r6 = logger;	 Catch:{ all -> 0x006c }
        r7 = "had to set auto-commit to false";
        r6.debug(r7);	 Catch:{ all -> 0x006c }
    L_0x0023:
        r6 = new java.lang.StringBuilder;	 Catch:{ all -> 0x006c }
        r6.<init>();	 Catch:{ all -> 0x006c }
        r7 = "ORMLITE";
        r6 = r6.append(r7);	 Catch:{ all -> 0x006c }
        r7 = savePointCounter;	 Catch:{ all -> 0x006c }
        r7 = r7.incrementAndGet();	 Catch:{ all -> 0x006c }
        r6 = r6.append(r7);	 Catch:{ all -> 0x006c }
        r6 = r6.toString();	 Catch:{ all -> 0x006c }
        r5 = r10.setSavePoint(r6);	 Catch:{ all -> 0x006c }
        if (r5 != 0) goto L_0x0060;
    L_0x0042:
        r6 = logger;	 Catch:{ all -> 0x006c }
        r7 = "started savePoint transaction";
        r6.debug(r7);	 Catch:{ all -> 0x006c }
    L_0x0049:
        r3 = 1;
    L_0x004a:
        r4 = r13.call();	 Catch:{ SQLException -> 0x007a, Exception -> 0x008a }
        if (r3 == 0) goto L_0x0053;
    L_0x0050:
        commit(r10, r5);	 Catch:{ SQLException -> 0x007a, Exception -> 0x008a }
    L_0x0053:
        if (r0 == 0) goto L_0x005f;
    L_0x0055:
        r10.setAutoCommit(r9);
        r6 = logger;
        r7 = "restored auto-commit to true";
        r6.debug(r7);
    L_0x005f:
        return r4;
    L_0x0060:
        r6 = logger;	 Catch:{ all -> 0x006c }
        r7 = "started savePoint transaction {}";
        r8 = r5.getSavepointName();	 Catch:{ all -> 0x006c }
        r6.debug(r7, r8);	 Catch:{ all -> 0x006c }
        goto L_0x0049;
    L_0x006c:
        r6 = move-exception;
        if (r0 == 0) goto L_0x0079;
    L_0x006f:
        r10.setAutoCommit(r9);
        r7 = logger;
        r8 = "restored auto-commit to true";
        r7.debug(r8);
    L_0x0079:
        throw r6;
    L_0x007a:
        r1 = move-exception;
        if (r3 == 0) goto L_0x0080;
    L_0x007d:
        rollBack(r10, r5);	 Catch:{ SQLException -> 0x0081 }
    L_0x0080:
        throw r1;	 Catch:{ all -> 0x006c }
    L_0x0081:
        r2 = move-exception;
        r6 = logger;	 Catch:{ all -> 0x006c }
        r7 = "after commit exception, rolling back to save-point also threw exception";
        r6.error(r1, r7);	 Catch:{ all -> 0x006c }
        goto L_0x0080;
    L_0x008a:
        r1 = move-exception;
        if (r3 == 0) goto L_0x0090;
    L_0x008d:
        rollBack(r10, r5);	 Catch:{ SQLException -> 0x0097 }
    L_0x0090:
        r6 = "Transaction callable threw non-SQL exception";
        r6 = com.j256.ormlite.misc.SqlExceptionUtil.create(r6, r1);	 Catch:{ all -> 0x006c }
        throw r6;	 Catch:{ all -> 0x006c }
    L_0x0097:
        r2 = move-exception;
        r6 = logger;	 Catch:{ all -> 0x006c }
        r7 = "after commit exception, rolling back to save-point also threw exception";
        r6.error(r1, r7);	 Catch:{ all -> 0x006c }
        goto L_0x0090;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.j256.ormlite.misc.TransactionManager.callInTransaction(com.j256.ormlite.support.DatabaseConnection, boolean, com.j256.ormlite.db.DatabaseType, java.util.concurrent.Callable):T");
    }

    public void setConnectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    private static void commit(DatabaseConnection connection, Savepoint savePoint) throws SQLException {
        Object name = savePoint == null ? null : savePoint.getSavepointName();
        connection.commit(savePoint);
        if (name == null) {
            logger.debug("committed savePoint transaction");
        } else {
            logger.debug("committed savePoint transaction {}", name);
        }
    }

    private static void rollBack(DatabaseConnection connection, Savepoint savePoint) throws SQLException {
        Object name = savePoint == null ? null : savePoint.getSavepointName();
        connection.rollback(savePoint);
        if (name == null) {
            logger.debug("rolled back savePoint transaction");
        } else {
            logger.debug("rolled back savePoint transaction {}", name);
        }
    }
}
