package com.j256.ormlite.stmt;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.dao.RawRowObjectMapper;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.stmt.mapped.MappedCreate;
import com.j256.ormlite.stmt.mapped.MappedDelete;
import com.j256.ormlite.stmt.mapped.MappedDeleteCollection;
import com.j256.ormlite.stmt.mapped.MappedQueryForId;
import com.j256.ormlite.stmt.mapped.MappedRefresh;
import com.j256.ormlite.stmt.mapped.MappedUpdate;
import com.j256.ormlite.stmt.mapped.MappedUpdateId;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.TableInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class StatementExecutor<T, ID> implements GenericRowMapper<String[]> {
    private static Logger logger;
    private static final FieldType[] noFieldTypes;
    private String countStarQuery;
    private final Dao<T, ID> dao;
    private final DatabaseType databaseType;
    private FieldType[] ifExistsFieldTypes;
    private String ifExistsQuery;
    private MappedDelete<T, ID> mappedDelete;
    private MappedCreate<T, ID> mappedInsert;
    private MappedQueryForId<T, ID> mappedQueryForId;
    private MappedRefresh<T, ID> mappedRefresh;
    private MappedUpdate<T, ID> mappedUpdate;
    private MappedUpdateId<T, ID> mappedUpdateId;
    private PreparedQuery<T> preparedQueryForAll;
    private RawRowMapper<T> rawRowMapper;
    private final TableInfo<T, ID> tableInfo;

    private static class ObjectArrayRowMapper implements GenericRowMapper<Object[]> {
        private final DataType[] columnTypes;

        public ObjectArrayRowMapper(DataType[] columnTypes) {
            this.columnTypes = columnTypes;
        }

        public Object[] mapRow(DatabaseResults results) throws SQLException {
            int columnN = results.getColumnCount();
            Object[] result = new Object[columnN];
            for (int colC = 0; colC < columnN; colC++) {
                DataType dataType;
                if (colC >= this.columnTypes.length) {
                    dataType = DataType.STRING;
                } else {
                    dataType = this.columnTypes[colC];
                }
                result[colC] = dataType.getDataPersister().resultToJava(null, results, colC);
            }
            return result;
        }
    }

    private static class UserRawRowMapper<UO> implements GenericRowMapper<UO> {
        private String[] columnNames;
        private final RawRowMapper<UO> mapper;
        private final GenericRowMapper<String[]> stringRowMapper;

        public UserRawRowMapper(RawRowMapper<UO> mapper, GenericRowMapper<String[]> stringMapper) {
            this.mapper = mapper;
            this.stringRowMapper = stringMapper;
        }

        public UO mapRow(DatabaseResults results) throws SQLException {
            return this.mapper.mapRow(getColumnNames(results), (String[]) this.stringRowMapper.mapRow(results));
        }

        private String[] getColumnNames(DatabaseResults results) throws SQLException {
            if (this.columnNames != null) {
                return this.columnNames;
            }
            this.columnNames = results.getColumnNames();
            return this.columnNames;
        }
    }

    private static class UserRawRowObjectMapper<UO> implements GenericRowMapper<UO> {
        private String[] columnNames;
        private final DataType[] columnTypes;
        private final RawRowObjectMapper<UO> mapper;

        public UserRawRowObjectMapper(RawRowObjectMapper<UO> mapper, DataType[] columnTypes) {
            this.mapper = mapper;
            this.columnTypes = columnTypes;
        }

        public UO mapRow(DatabaseResults results) throws SQLException {
            int columnN = results.getColumnCount();
            Object[] objectResults = new Object[columnN];
            for (int colC = 0; colC < columnN; colC++) {
                if (colC >= this.columnTypes.length) {
                    objectResults[colC] = null;
                } else {
                    objectResults[colC] = this.columnTypes[colC].getDataPersister().resultToJava(null, results, colC);
                }
            }
            return this.mapper.mapRow(getColumnNames(results), this.columnTypes, objectResults);
        }

        private String[] getColumnNames(DatabaseResults results) throws SQLException {
            if (this.columnNames != null) {
                return this.columnNames;
            }
            this.columnNames = results.getColumnNames();
            return this.columnNames;
        }
    }

    static {
        logger = LoggerFactory.getLogger(StatementExecutor.class);
        noFieldTypes = new FieldType[0];
    }

    public StatementExecutor(DatabaseType databaseType, TableInfo<T, ID> tableInfo, Dao<T, ID> dao) {
        this.databaseType = databaseType;
        this.tableInfo = tableInfo;
        this.dao = dao;
    }

    public T queryForId(DatabaseConnection databaseConnection, ID id, ObjectCache objectCache) throws SQLException {
        if (this.mappedQueryForId == null) {
            this.mappedQueryForId = MappedQueryForId.build(this.databaseType, this.tableInfo, null);
        }
        return this.mappedQueryForId.execute(databaseConnection, id, objectCache);
    }

    public T queryForFirst(DatabaseConnection databaseConnection, PreparedStmt<T> preparedStmt, ObjectCache objectCache) throws SQLException {
        CompiledStatement stmt = preparedStmt.compile(databaseConnection, StatementType.SELECT);
        DatabaseResults databaseResults = null;
        try {
            T mapRow;
            databaseResults = stmt.runQuery(objectCache);
            if (databaseResults.first()) {
                logger.debug("query-for-first of '{}' returned at least 1 result", preparedStmt.getStatement());
                mapRow = preparedStmt.mapRow(databaseResults);
            } else {
                logger.debug("query-for-first of '{}' returned at 0 results", preparedStmt.getStatement());
                mapRow = null;
                if (databaseResults != null) {
                    databaseResults.close();
                }
                stmt.close();
            }
            return mapRow;
        } finally {
            if (databaseResults != null) {
                databaseResults.close();
            }
            stmt.close();
        }
    }

    public List<T> queryForAll(ConnectionSource connectionSource, ObjectCache objectCache) throws SQLException {
        prepareQueryForAll();
        return query(connectionSource, this.preparedQueryForAll, objectCache);
    }

    public long queryForCountStar(DatabaseConnection databaseConnection) throws SQLException {
        if (this.countStarQuery == null) {
            StringBuilder sb = new StringBuilder(64);
            sb.append("SELECT COUNT(*) FROM ");
            this.databaseType.appendEscapedEntityName(sb, this.tableInfo.getTableName());
            this.countStarQuery = sb.toString();
        }
        long count = databaseConnection.queryForLong(this.countStarQuery);
        logger.debug("query of '{}' returned {}", this.countStarQuery, Long.valueOf(count));
        return count;
    }

    public long queryForLong(DatabaseConnection databaseConnection, PreparedStmt<T> preparedStmt) throws SQLException {
        CompiledStatement stmt = preparedStmt.compile(databaseConnection, StatementType.SELECT_LONG);
        DatabaseResults results = null;
        try {
            results = stmt.runQuery(null);
            if (results.first()) {
                long j = results.getLong(0);
                return j;
            }
            throw new SQLException("No result found in queryForLong: " + preparedStmt.getStatement());
        } finally {
            if (results != null) {
                results.close();
            }
            stmt.close();
        }
    }

    public long queryForLong(DatabaseConnection databaseConnection, String query, String[] arguments) throws SQLException {
        logger.debug("executing raw query for long: {}", (Object) query);
        if (arguments.length > 0) {
            logger.trace("query arguments: {}", (Object) arguments);
        }
        CompiledStatement stmt = null;
        DatabaseResults results = null;
        try {
            stmt = databaseConnection.compileStatement(query, StatementType.SELECT, noFieldTypes, -1);
            assignStatementArguments(stmt, arguments);
            results = stmt.runQuery(null);
            if (results.first()) {
                long j = results.getLong(0);
                return j;
            }
            throw new SQLException("No result found in queryForLong: " + query);
        } finally {
            if (results != null) {
                results.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public List<T> query(ConnectionSource connectionSource, PreparedStmt<T> preparedStmt, ObjectCache objectCache) throws SQLException {
        SelectIterator<T, ID> iterator = buildIterator(null, connectionSource, preparedStmt, objectCache, -1);
        try {
            List<T> results = new ArrayList();
            while (iterator.hasNextThrow()) {
                results.add(iterator.nextThrow());
            }
            logger.debug("query of '{}' returned {} results", preparedStmt.getStatement(), Integer.valueOf(results.size()));
            return results;
        } finally {
            iterator.close();
        }
    }

    public SelectIterator<T, ID> buildIterator(BaseDaoImpl<T, ID> classDao, ConnectionSource connectionSource, int resultFlags, ObjectCache objectCache) throws SQLException {
        prepareQueryForAll();
        return buildIterator(classDao, connectionSource, this.preparedQueryForAll, objectCache, resultFlags);
    }

    public GenericRowMapper<T> getSelectStarRowMapper() throws SQLException {
        prepareQueryForAll();
        return this.preparedQueryForAll;
    }

    public RawRowMapper<T> getRawRowMapper() {
        if (this.rawRowMapper == null) {
            this.rawRowMapper = new RawRowMapperImpl(this.tableInfo);
        }
        return this.rawRowMapper;
    }

    public SelectIterator<T, ID> buildIterator(BaseDaoImpl<T, ID> classDao, ConnectionSource connectionSource, PreparedStmt<T> preparedStmt, ObjectCache objectCache, int resultFlags) throws SQLException {
        DatabaseConnection connection = connectionSource.getReadOnlyConnection();
        CompiledStatement compiledStatement = null;
        try {
            compiledStatement = preparedStmt.compile(connection, StatementType.SELECT, resultFlags);
            SelectIterator<T, ID> iterator = new SelectIterator(this.tableInfo.getDataClass(), classDao, preparedStmt, connectionSource, connection, compiledStatement, preparedStmt.getStatement(), objectCache);
            connection = null;
            compiledStatement = null;
            return iterator;
        } finally {
            if (compiledStatement != null) {
                compiledStatement.close();
            }
            if (connection != null) {
                connectionSource.releaseConnection(connection);
            }
        }
    }

    public GenericRawResults<String[]> queryRaw(ConnectionSource connectionSource, String query, String[] arguments, ObjectCache objectCache) throws SQLException {
        logger.debug("executing raw query for: {}", (Object) query);
        if (arguments.length > 0) {
            logger.trace("query arguments: {}", (Object) arguments);
        }
        DatabaseConnection connection = connectionSource.getReadOnlyConnection();
        CompiledStatement compiledStatement = null;
        try {
            compiledStatement = connection.compileStatement(query, StatementType.SELECT, noFieldTypes, -1);
            assignStatementArguments(compiledStatement, arguments);
            GenericRawResults<String[]> rawResults = new RawResultsImpl(connectionSource, connection, query, String[].class, compiledStatement, this, objectCache);
            compiledStatement = null;
            connection = null;
            return rawResults;
        } finally {
            if (compiledStatement != null) {
                compiledStatement.close();
            }
            if (connection != null) {
                connectionSource.releaseConnection(connection);
            }
        }
    }

    public <UO> GenericRawResults<UO> queryRaw(ConnectionSource connectionSource, String query, RawRowMapper<UO> rowMapper, String[] arguments, ObjectCache objectCache) throws SQLException {
        logger.debug("executing raw query for: {}", (Object) query);
        if (arguments.length > 0) {
            logger.trace("query arguments: {}", (Object) arguments);
        }
        DatabaseConnection connection = connectionSource.getReadOnlyConnection();
        CompiledStatement compiledStatement = null;
        try {
            compiledStatement = connection.compileStatement(query, StatementType.SELECT, noFieldTypes, -1);
            assignStatementArguments(compiledStatement, arguments);
            RawResultsImpl<UO> rawResults = new RawResultsImpl(connectionSource, connection, query, String[].class, compiledStatement, new UserRawRowMapper(rowMapper, this), objectCache);
            compiledStatement = null;
            connection = null;
            return rawResults;
        } finally {
            if (compiledStatement != null) {
                compiledStatement.close();
            }
            if (connection != null) {
                connectionSource.releaseConnection(connection);
            }
        }
    }

    public <UO> GenericRawResults<UO> queryRaw(ConnectionSource connectionSource, String query, DataType[] columnTypes, RawRowObjectMapper<UO> rowMapper, String[] arguments, ObjectCache objectCache) throws SQLException {
        logger.debug("executing raw query for: {}", (Object) query);
        if (arguments.length > 0) {
            logger.trace("query arguments: {}", (Object) arguments);
        }
        DatabaseConnection connection = connectionSource.getReadOnlyConnection();
        CompiledStatement compiledStatement = null;
        try {
            compiledStatement = connection.compileStatement(query, StatementType.SELECT, noFieldTypes, -1);
            assignStatementArguments(compiledStatement, arguments);
            RawResultsImpl<UO> rawResults = new RawResultsImpl(connectionSource, connection, query, String[].class, compiledStatement, new UserRawRowObjectMapper(rowMapper, columnTypes), objectCache);
            compiledStatement = null;
            connection = null;
            return rawResults;
        } finally {
            if (compiledStatement != null) {
                compiledStatement.close();
            }
            if (connection != null) {
                connectionSource.releaseConnection(connection);
            }
        }
    }

    public GenericRawResults<Object[]> queryRaw(ConnectionSource connectionSource, String query, DataType[] columnTypes, String[] arguments, ObjectCache objectCache) throws SQLException {
        logger.debug("executing raw query for: {}", (Object) query);
        if (arguments.length > 0) {
            logger.trace("query arguments: {}", (Object) arguments);
        }
        DatabaseConnection connection = connectionSource.getReadOnlyConnection();
        CompiledStatement compiledStatement = null;
        try {
            compiledStatement = connection.compileStatement(query, StatementType.SELECT, noFieldTypes, -1);
            assignStatementArguments(compiledStatement, arguments);
            RawResultsImpl<Object[]> rawResults = new RawResultsImpl(connectionSource, connection, query, Object[].class, compiledStatement, new ObjectArrayRowMapper(columnTypes), objectCache);
            compiledStatement = null;
            connection = null;
            return rawResults;
        } finally {
            if (compiledStatement != null) {
                compiledStatement.close();
            }
            if (connection != null) {
                connectionSource.releaseConnection(connection);
            }
        }
    }

    public int updateRaw(DatabaseConnection connection, String statement, String[] arguments) throws SQLException {
        logger.debug("running raw update statement: {}", (Object) statement);
        if (arguments.length > 0) {
            logger.trace("update arguments: {}", (Object) arguments);
        }
        CompiledStatement compiledStatement = connection.compileStatement(statement, StatementType.UPDATE, noFieldTypes, -1);
        try {
            assignStatementArguments(compiledStatement, arguments);
            int runUpdate = compiledStatement.runUpdate();
            return runUpdate;
        } finally {
            compiledStatement.close();
        }
    }

    public int executeRawNoArgs(DatabaseConnection connection, String statement) throws SQLException {
        logger.debug("running raw execute statement: {}", (Object) statement);
        return connection.executeStatement(statement, -1);
    }

    public int executeRaw(DatabaseConnection connection, String statement, String[] arguments) throws SQLException {
        logger.debug("running raw execute statement: {}", (Object) statement);
        if (arguments.length > 0) {
            logger.trace("execute arguments: {}", (Object) arguments);
        }
        CompiledStatement compiledStatement = connection.compileStatement(statement, StatementType.EXECUTE, noFieldTypes, -1);
        try {
            assignStatementArguments(compiledStatement, arguments);
            int runExecute = compiledStatement.runExecute();
            return runExecute;
        } finally {
            compiledStatement.close();
        }
    }

    public int create(DatabaseConnection databaseConnection, T data, ObjectCache objectCache) throws SQLException {
        if (this.mappedInsert == null) {
            this.mappedInsert = MappedCreate.build(this.databaseType, this.tableInfo);
        }
        return this.mappedInsert.insert(this.databaseType, databaseConnection, data, objectCache);
    }

    public int update(DatabaseConnection databaseConnection, T data, ObjectCache objectCache) throws SQLException {
        if (this.mappedUpdate == null) {
            this.mappedUpdate = MappedUpdate.build(this.databaseType, this.tableInfo);
        }
        return this.mappedUpdate.update(databaseConnection, data, objectCache);
    }

    public int updateId(DatabaseConnection databaseConnection, T data, ID newId, ObjectCache objectCache) throws SQLException {
        if (this.mappedUpdateId == null) {
            this.mappedUpdateId = MappedUpdateId.build(this.databaseType, this.tableInfo);
        }
        return this.mappedUpdateId.execute(databaseConnection, data, newId, objectCache);
    }

    public int update(DatabaseConnection databaseConnection, PreparedUpdate<T> preparedUpdate) throws SQLException {
        CompiledStatement stmt = preparedUpdate.compile(databaseConnection, StatementType.UPDATE);
        try {
            int runUpdate = stmt.runUpdate();
            return runUpdate;
        } finally {
            stmt.close();
        }
    }

    public int refresh(DatabaseConnection databaseConnection, T data, ObjectCache objectCache) throws SQLException {
        if (this.mappedRefresh == null) {
            this.mappedRefresh = MappedRefresh.build(this.databaseType, this.tableInfo);
        }
        return this.mappedRefresh.executeRefresh(databaseConnection, data, objectCache);
    }

    public int delete(DatabaseConnection databaseConnection, T data, ObjectCache objectCache) throws SQLException {
        if (this.mappedDelete == null) {
            this.mappedDelete = MappedDelete.build(this.databaseType, this.tableInfo);
        }
        return this.mappedDelete.delete(databaseConnection, data, objectCache);
    }

    public int deleteById(DatabaseConnection databaseConnection, ID id, ObjectCache objectCache) throws SQLException {
        if (this.mappedDelete == null) {
            this.mappedDelete = MappedDelete.build(this.databaseType, this.tableInfo);
        }
        return this.mappedDelete.deleteById(databaseConnection, id, objectCache);
    }

    public int deleteObjects(DatabaseConnection databaseConnection, Collection<T> datas, ObjectCache objectCache) throws SQLException {
        return MappedDeleteCollection.deleteObjects(this.databaseType, this.tableInfo, databaseConnection, datas, objectCache);
    }

    public int deleteIds(DatabaseConnection databaseConnection, Collection<ID> ids, ObjectCache objectCache) throws SQLException {
        return MappedDeleteCollection.deleteIds(this.databaseType, this.tableInfo, databaseConnection, ids, objectCache);
    }

    public int delete(DatabaseConnection databaseConnection, PreparedDelete<T> preparedDelete) throws SQLException {
        CompiledStatement stmt = preparedDelete.compile(databaseConnection, StatementType.DELETE);
        try {
            int runUpdate = stmt.runUpdate();
            return runUpdate;
        } finally {
            stmt.close();
        }
    }

    public <CT> CT callBatchTasks(DatabaseConnection connection, boolean saved, Callable<CT> callable) throws SQLException {
        if (this.databaseType.isBatchUseTransaction()) {
            return TransactionManager.callInTransaction(connection, saved, this.databaseType, callable);
        }
        boolean autoCommitAtStart = false;
        try {
            if (connection.isAutoCommitSupported()) {
                autoCommitAtStart = connection.isAutoCommit();
                if (autoCommitAtStart) {
                    connection.setAutoCommit(false);
                    logger.debug("disabled auto-commit on table {} before batch tasks", this.tableInfo.getTableName());
                }
            }
            CT call = callable.call();
            if (!autoCommitAtStart) {
                return call;
            }
            connection.setAutoCommit(true);
            logger.debug("re-enabled auto-commit on table {} after batch tasks", this.tableInfo.getTableName());
            return call;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e2) {
            throw SqlExceptionUtil.create("Batch tasks callable threw non-SQL exception", e2);
        } catch (Throwable th) {
            if (autoCommitAtStart) {
                connection.setAutoCommit(true);
                logger.debug("re-enabled auto-commit on table {} after batch tasks", this.tableInfo.getTableName());
            }
        }
    }

    public String[] mapRow(DatabaseResults results) throws SQLException {
        int columnN = results.getColumnCount();
        String[] result = new String[columnN];
        for (int colC = 0; colC < columnN; colC++) {
            result[colC] = results.getString(colC);
        }
        return result;
    }

    public boolean ifExists(DatabaseConnection connection, ID id) throws SQLException {
        if (this.ifExistsQuery == null) {
            QueryBuilder<T, ID> qb = new QueryBuilder(this.databaseType, this.tableInfo, this.dao);
            qb.selectRaw("COUNT(*)");
            qb.where().eq(this.tableInfo.getIdField().getColumnName(), new SelectArg());
            this.ifExistsQuery = qb.prepareStatementString();
            this.ifExistsFieldTypes = new FieldType[]{this.tableInfo.getIdField()};
        }
        long count = connection.queryForLong(this.ifExistsQuery, new Object[]{id}, this.ifExistsFieldTypes);
        logger.debug("query of '{}' returned {}", this.ifExistsQuery, Long.valueOf(count));
        if (count != 0) {
            return true;
        }
        return false;
    }

    private void assignStatementArguments(CompiledStatement compiledStatement, String[] arguments) throws SQLException {
        for (int i = 0; i < arguments.length; i++) {
            compiledStatement.setObject(i, arguments[i], SqlType.STRING);
        }
    }

    private void prepareQueryForAll() throws SQLException {
        if (this.preparedQueryForAll == null) {
            this.preparedQueryForAll = new QueryBuilder(this.databaseType, this.tableInfo, this.dao).prepare();
        }
    }
}
