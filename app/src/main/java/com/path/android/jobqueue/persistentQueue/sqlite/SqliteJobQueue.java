package com.path.android.jobqueue.persistentQueue.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;
import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.JobHolder;
import com.path.android.jobqueue.JobQueue;
import com.path.android.jobqueue.log.JqLog;
import com.path.android.jobqueue.persistentQueue.sqlite.SqlHelper.Order;
import com.path.android.jobqueue.persistentQueue.sqlite.SqlHelper.Order.Type;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class SqliteJobQueue implements JobQueue {
    SQLiteDatabase db;
    DbOpenHelper dbOpenHelper;
    JobSerializer jobSerializer;
    QueryCache nextJobsQueryCache;
    QueryCache readyJobsQueryCache;
    private final long sessionId;
    SqlHelper sqlHelper;

    private static class InvalidBaseJobException extends Exception {
        private InvalidBaseJobException() {
        }
    }

    public interface JobSerializer {
        <T extends BaseJob> T deserialize(byte[] bArr) throws IOException, ClassNotFoundException;

        byte[] serialize(Object obj) throws IOException;
    }

    public static class JavaSerializer implements JobSerializer {
        public byte[] serialize(Object object) throws IOException {
            Throwable th;
            if (object == null) {
                return null;
            }
            ByteArrayOutputStream bos = null;
            try {
                ObjectOutput out;
                ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
                try {
                    out = new ObjectOutputStream(bos2);
                } catch (Throwable th2) {
                    th = th2;
                    bos = bos2;
                    if (bos != null) {
                        bos.close();
                    }
                    throw th;
                }
                try {
                    out.writeObject(object);
                    byte[] toByteArray = bos2.toByteArray();
                    if (bos2 == null) {
                        return toByteArray;
                    }
                    bos2.close();
                    return toByteArray;
                } catch (Throwable th3) {
                    th = th3;
                    ObjectOutput objectOutput = out;
                    bos = bos2;
                    if (bos != null) {
                        bos.close();
                    }
                    throw th;
                }
            } catch (Throwable th4) {
                th = th4;
                if (bos != null) {
                    bos.close();
                }
                throw th;
            }
        }

        public <T extends BaseJob> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
            Throwable th;
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            ObjectInputStream in = null;
            try {
                ObjectInputStream in2 = new ObjectInputStream(new ByteArrayInputStream(bytes));
                try {
                    BaseJob baseJob = (BaseJob) in2.readObject();
                    if (in2 == null) {
                        return baseJob;
                    }
                    in2.close();
                    return baseJob;
                } catch (Throwable th2) {
                    th = th2;
                    in = in2;
                    if (in != null) {
                        in.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (in != null) {
                    in.close();
                }
                throw th;
            }
        }
    }

    public SqliteJobQueue(Context context, long sessionId, String id, JobSerializer jobSerializer) {
        this.sessionId = sessionId;
        this.dbOpenHelper = new DbOpenHelper(context, "db_" + id);
        this.db = this.dbOpenHelper.getWritableDatabase();
        this.sqlHelper = new SqlHelper(this.db, "job_holder", DbOpenHelper.ID_COLUMN.columnName, 9, sessionId);
        this.jobSerializer = jobSerializer;
        this.readyJobsQueryCache = new QueryCache();
        this.nextJobsQueryCache = new QueryCache();
        this.sqlHelper.resetDelayTimesTo(Long.MIN_VALUE);
    }

    public long insert(JobHolder jobHolder) {
        long id;
        SQLiteStatement stmt = this.sqlHelper.getInsertStatement();
        synchronized (stmt) {
            stmt.clearBindings();
            bindValues(stmt, jobHolder);
            id = stmt.executeInsert();
        }
        jobHolder.setId(Long.valueOf(id));
        return id;
    }

    private void bindValues(SQLiteStatement stmt, JobHolder jobHolder) {
        if (jobHolder.getId() != null) {
            stmt.bindLong(DbOpenHelper.ID_COLUMN.columnIndex + 1, jobHolder.getId().longValue());
        }
        stmt.bindLong(DbOpenHelper.PRIORITY_COLUMN.columnIndex + 1, (long) jobHolder.getPriority());
        if (jobHolder.getGroupId() != null) {
            stmt.bindString(DbOpenHelper.GROUP_ID_COLUMN.columnIndex + 1, jobHolder.getGroupId());
        }
        stmt.bindLong(DbOpenHelper.RUN_COUNT_COLUMN.columnIndex + 1, (long) jobHolder.getRunCount());
        byte[] baseJob = getSerializeBaseJob(jobHolder);
        if (baseJob != null) {
            stmt.bindBlob(DbOpenHelper.BASE_JOB_COLUMN.columnIndex + 1, baseJob);
        }
        stmt.bindLong(DbOpenHelper.CREATED_NS_COLUMN.columnIndex + 1, jobHolder.getCreatedNs());
        stmt.bindLong(DbOpenHelper.DELAY_UNTIL_NS_COLUMN.columnIndex + 1, jobHolder.getDelayUntilNs());
        stmt.bindLong(DbOpenHelper.RUNNING_SESSION_ID_COLUMN.columnIndex + 1, jobHolder.getRunningSessionId());
        stmt.bindLong(DbOpenHelper.REQUIRES_NETWORK_COLUMN.columnIndex + 1, jobHolder.requiresNetwork() ? 1 : 0);
    }

    public long insertOrReplace(JobHolder jobHolder) {
        if (jobHolder.getId() == null) {
            return insert(jobHolder);
        }
        long id;
        jobHolder.setRunningSessionId(Long.MIN_VALUE);
        SQLiteStatement stmt = this.sqlHelper.getInsertOrReplaceStatement();
        synchronized (stmt) {
            stmt.clearBindings();
            bindValues(stmt, jobHolder);
            id = stmt.executeInsert();
        }
        jobHolder.setId(Long.valueOf(id));
        return id;
    }

    public void remove(JobHolder jobHolder) {
        if (jobHolder.getId() == null) {
            JqLog.e("called remove with null job id.", new Object[0]);
        } else {
            delete(jobHolder.getId());
        }
    }

    private void delete(Long id) {
        SQLiteStatement stmt = this.sqlHelper.getDeleteStatement();
        synchronized (stmt) {
            stmt.clearBindings();
            stmt.bindLong(1, id.longValue());
            stmt.execute();
        }
    }

    public int count() {
        int simpleQueryForLong;
        SQLiteStatement stmt = this.sqlHelper.getCountStatement();
        synchronized (stmt) {
            stmt.clearBindings();
            stmt.bindLong(1, this.sessionId);
            simpleQueryForLong = (int) stmt.simpleQueryForLong();
        }
        return simpleQueryForLong;
    }

    public int countReadyJobs(boolean hasNetwork, Collection<String> excludeGroups) {
        int i = 0;
        String sql = this.readyJobsQueryCache.get(hasNetwork, excludeGroups);
        if (sql == null) {
            sql = "SELECT SUM(case WHEN " + DbOpenHelper.GROUP_ID_COLUMN.columnName + " is null then group_cnt else 1 end) from (" + ("SELECT count(*) group_cnt, " + DbOpenHelper.GROUP_ID_COLUMN.columnName + " FROM " + "job_holder" + " WHERE " + createReadyJobWhereSql(hasNetwork, excludeGroups, true)) + ")";
            this.readyJobsQueryCache.set(sql, hasNetwork, excludeGroups);
        }
        Cursor cursor = this.db.rawQuery(sql, new String[]{Long.toString(this.sessionId), Long.toString(System.nanoTime())});
        try {
            if (cursor.moveToNext()) {
                i = cursor.getInt(0);
                cursor.close();
            }
            return i;
        } finally {
            cursor.close();
        }
    }

    public JobHolder findJobById(long id) {
        JobHolder jobHolder = null;
        Cursor cursor = this.db.rawQuery(this.sqlHelper.FIND_BY_ID_QUERY, new String[]{Long.toString(id)});
        try {
            if (cursor.moveToFirst()) {
                jobHolder = createJobHolderFromCursor(cursor);
                cursor.close();
            }
        } catch (InvalidBaseJobException e) {
            JqLog.e(e, "invalid job on findJobById", new Object[0]);
        } finally {
            cursor.close();
        }
        return jobHolder;
    }

    public JobHolder nextJobAndIncRunCount(boolean hasNetwork, Collection<String> excludeGroups) {
        String selectQuery = this.nextJobsQueryCache.get(hasNetwork, excludeGroups);
        if (selectQuery == null) {
            String where = createReadyJobWhereSql(hasNetwork, excludeGroups, false);
            selectQuery = this.sqlHelper.createSelect(where, Integer.valueOf(1), new Order(DbOpenHelper.PRIORITY_COLUMN, Type.DESC), new Order(DbOpenHelper.CREATED_NS_COLUMN, Type.ASC), new Order(DbOpenHelper.ID_COLUMN, Type.ASC));
            this.nextJobsQueryCache.set(selectQuery, hasNetwork, excludeGroups);
        }
        Cursor cursor = this.db.rawQuery(selectQuery, new String[]{Long.toString(this.sessionId), Long.toString(System.nanoTime())});
        JobHolder createJobHolderFromCursor;
        try {
            if (cursor.moveToNext()) {
                createJobHolderFromCursor = createJobHolderFromCursor(cursor);
                onJobFetchedForRunning(createJobHolderFromCursor);
                cursor.close();
                return createJobHolderFromCursor;
            }
            createJobHolderFromCursor = null;
            return createJobHolderFromCursor;
        } catch (InvalidBaseJobException e) {
            delete(Long.valueOf(cursor.getLong(0)));
            createJobHolderFromCursor = nextJobAndIncRunCount(true, null);
        } finally {
            cursor.close();
        }
    }

    private String createReadyJobWhereSql(boolean hasNetwork, Collection<String> excludeGroups, boolean groupByRunningGroup) {
        String where = DbOpenHelper.RUNNING_SESSION_ID_COLUMN.columnName + " != ? " + " AND " + DbOpenHelper.DELAY_UNTIL_NS_COLUMN.columnName + " <= ? ";
        if (!hasNetwork) {
            where = where + " AND " + DbOpenHelper.REQUIRES_NETWORK_COLUMN.columnName + " != 1 ";
        }
        String groupConstraint = null;
        if (excludeGroups != null && excludeGroups.size() > 0) {
            groupConstraint = DbOpenHelper.GROUP_ID_COLUMN.columnName + " IS NULL OR " + DbOpenHelper.GROUP_ID_COLUMN.columnName + " NOT IN('" + joinStrings("','", excludeGroups) + "')";
        }
        if (groupByRunningGroup) {
            where = where + " GROUP BY " + DbOpenHelper.GROUP_ID_COLUMN.columnName;
            if (groupConstraint != null) {
                return where + " HAVING " + groupConstraint;
            }
            return where;
        } else if (groupConstraint != null) {
            return where + " AND ( " + groupConstraint + " )";
        } else {
            return where;
        }
    }

    private static String joinStrings(String glue, Collection<String> strings) {
        StringBuilder builder = new StringBuilder();
        for (String str : strings) {
            if (builder.length() != 0) {
                builder.append(glue);
            }
            builder.append(str);
        }
        return builder.toString();
    }

    public Long getNextJobDelayUntilNs(boolean hasNetwork) {
        Long valueOf;
        SQLiteStatement stmt = hasNetwork ? this.sqlHelper.getNextJobDelayedUntilWithNetworkStatement() : this.sqlHelper.getNextJobDelayedUntilWithoutNetworkStatement();
        synchronized (stmt) {
            try {
                stmt.clearBindings();
                valueOf = Long.valueOf(stmt.simpleQueryForLong());
            } catch (SQLiteDoneException e) {
                valueOf = null;
            }
        }
        return valueOf;
    }

    public void clear() {
        this.sqlHelper.truncate();
        this.readyJobsQueryCache.clear();
        this.nextJobsQueryCache.clear();
    }

    private void onJobFetchedForRunning(JobHolder jobHolder) {
        SQLiteStatement stmt = this.sqlHelper.getOnJobFetchedForRunningStatement();
        jobHolder.setRunCount(jobHolder.getRunCount() + 1);
        jobHolder.setRunningSessionId(this.sessionId);
        synchronized (stmt) {
            stmt.clearBindings();
            stmt.bindLong(1, (long) jobHolder.getRunCount());
            stmt.bindLong(2, this.sessionId);
            stmt.bindLong(3, jobHolder.getId().longValue());
            stmt.execute();
        }
    }

    private JobHolder createJobHolderFromCursor(Cursor cursor) throws InvalidBaseJobException {
        BaseJob job = safeDeserialize(cursor.getBlob(DbOpenHelper.BASE_JOB_COLUMN.columnIndex));
        if (job != null) {
            return new JobHolder(Long.valueOf(cursor.getLong(DbOpenHelper.ID_COLUMN.columnIndex)), cursor.getInt(DbOpenHelper.PRIORITY_COLUMN.columnIndex), cursor.getString(DbOpenHelper.GROUP_ID_COLUMN.columnIndex), cursor.getInt(DbOpenHelper.RUN_COUNT_COLUMN.columnIndex), job, cursor.getLong(DbOpenHelper.CREATED_NS_COLUMN.columnIndex), cursor.getLong(DbOpenHelper.DELAY_UNTIL_NS_COLUMN.columnIndex), cursor.getLong(DbOpenHelper.RUNNING_SESSION_ID_COLUMN.columnIndex));
        }
        throw new InvalidBaseJobException();
    }

    private BaseJob safeDeserialize(byte[] bytes) {
        try {
            return this.jobSerializer.deserialize(bytes);
        } catch (Throwable t) {
            JqLog.e(t, "error while deserializing job", new Object[0]);
            return null;
        }
    }

    private byte[] getSerializeBaseJob(JobHolder jobHolder) {
        return safeSerialize(jobHolder.getBaseJob());
    }

    private byte[] safeSerialize(Object object) {
        try {
            return this.jobSerializer.serialize(object);
        } catch (Throwable t) {
            JqLog.e(t, "error while serializing object %s", object.getClass().getSimpleName());
            return null;
        }
    }
}
