package com.j256.ormlite.stmt;

import android.support.v4.media.TransportMediator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.mapped.MappedPreparedStmt;
import com.j256.ormlite.table.TableInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class StatementBuilder<T, ID> {
    private static Logger logger;
    protected boolean addTableName;
    protected final Dao<T, ID> dao;
    protected final DatabaseType databaseType;
    protected final TableInfo<T, ID> tableInfo;
    protected final String tableName;
    protected StatementType type;
    protected Where<T, ID> where;

    public static class StatementInfo {
        private final List<ArgumentHolder> argList;
        private final String statement;

        private StatementInfo(String statement, List<ArgumentHolder> argList) {
            this.argList = argList;
            this.statement = statement;
        }

        public String getStatement() {
            return this.statement;
        }

        public List<ArgumentHolder> getArgList() {
            return this.argList;
        }
    }

    public enum StatementType {
        SELECT(true, true, false, false),
        SELECT_LONG(true, true, false, false),
        SELECT_RAW(true, true, false, false),
        UPDATE(true, false, true, false),
        DELETE(true, false, true, false),
        EXECUTE(false, false, false, true);
        
        private final boolean okForExecute;
        private final boolean okForQuery;
        private final boolean okForStatementBuilder;
        private final boolean okForUpdate;

        private StatementType(boolean okForStatementBuilder, boolean okForQuery, boolean okForUpdate, boolean okForExecute) {
            this.okForStatementBuilder = okForStatementBuilder;
            this.okForQuery = okForQuery;
            this.okForUpdate = okForUpdate;
            this.okForExecute = okForExecute;
        }

        public boolean isOkForStatementBuilder() {
            return this.okForStatementBuilder;
        }

        public boolean isOkForQuery() {
            return this.okForQuery;
        }

        public boolean isOkForUpdate() {
            return this.okForUpdate;
        }

        public boolean isOkForExecute() {
            return this.okForExecute;
        }
    }

    protected enum WhereOperation {
        FIRST("WHERE ", null),
        AND("AND (", ") "),
        OR("OR (", ") ");
        
        private final String after;
        private final String before;

        private WhereOperation(String before, String after) {
            this.before = before;
            this.after = after;
        }

        public void appendBefore(StringBuilder sb) {
            if (this.before != null) {
                sb.append(this.before);
            }
        }

        public void appendAfter(StringBuilder sb) {
            if (this.after != null) {
                sb.append(this.after);
            }
        }
    }

    protected abstract void appendStatementEnd(StringBuilder stringBuilder, List<ArgumentHolder> list) throws SQLException;

    protected abstract void appendStatementStart(StringBuilder stringBuilder, List<ArgumentHolder> list) throws SQLException;

    static {
        logger = LoggerFactory.getLogger(StatementBuilder.class);
    }

    public StatementBuilder(DatabaseType databaseType, TableInfo<T, ID> tableInfo, Dao<T, ID> dao, StatementType type) {
        this.where = null;
        this.databaseType = databaseType;
        this.tableInfo = tableInfo;
        this.tableName = tableInfo.getTableName();
        this.dao = dao;
        this.type = type;
        if (!type.isOkForStatementBuilder()) {
            throw new IllegalStateException("Building a statement from a " + type + " statement is not allowed");
        }
    }

    public Where<T, ID> where() {
        this.where = new Where(this.tableInfo, this, this.databaseType);
        return this.where;
    }

    public void setWhere(Where<T, ID> where) {
        this.where = where;
    }

    protected MappedPreparedStmt<T, ID> prepareStatement(Long limit) throws SQLException {
        List<ArgumentHolder> argList = new ArrayList();
        String statement = buildStatementString(argList);
        ArgumentHolder[] selectArgs = (ArgumentHolder[]) argList.toArray(new ArgumentHolder[argList.size()]);
        FieldType[] resultFieldTypes = getResultFieldTypes();
        FieldType[] argFieldTypes = new FieldType[argList.size()];
        for (int selectC = 0; selectC < selectArgs.length; selectC++) {
            argFieldTypes[selectC] = selectArgs[selectC].getFieldType();
        }
        if (this.type.isOkForStatementBuilder()) {
            return new MappedPreparedStmt(this.tableInfo, statement, argFieldTypes, resultFieldTypes, selectArgs, this.databaseType.isLimitSqlSupported() ? null : limit, this.type);
        }
        throw new IllegalStateException("Building a statement from a " + this.type + " statement is not allowed");
    }

    public String prepareStatementString() throws SQLException {
        return buildStatementString(new ArrayList());
    }

    public StatementInfo prepareStatementInfo() throws SQLException {
        List<ArgumentHolder> argList = new ArrayList();
        return new StatementInfo(argList, null);
    }

    @Deprecated
    public void clear() {
        reset();
    }

    public void reset() {
        this.where = null;
    }

    protected String buildStatementString(List<ArgumentHolder> argList) throws SQLException {
        StringBuilder sb = new StringBuilder(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        appendStatementString(sb, argList);
        Object statement = sb.toString();
        logger.debug("built statement {}", statement);
        return statement;
    }

    protected void appendStatementString(StringBuilder sb, List<ArgumentHolder> argList) throws SQLException {
        appendStatementStart(sb, argList);
        appendWhereStatement(sb, argList, WhereOperation.FIRST);
        appendStatementEnd(sb, argList);
    }

    protected boolean appendWhereStatement(StringBuilder sb, List<ArgumentHolder> argList, WhereOperation operation) throws SQLException {
        if (this.where != null) {
            operation.appendBefore(sb);
            this.where.appendSql(this.addTableName ? this.tableName : null, sb, argList);
            operation.appendAfter(sb);
            return false;
        } else if (operation == WhereOperation.FIRST) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean shouldPrependTableNameToColumns() {
        return false;
    }

    protected FieldType[] getResultFieldTypes() {
        return null;
    }

    protected FieldType verifyColumnName(String columnName) {
        return this.tableInfo.getFieldTypeByColumnName(columnName);
    }

    StatementType getType() {
        return this.type;
    }
}
