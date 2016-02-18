package com.path.android.jobqueue.persistentQueue.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.j256.ormlite.field.FieldType;
import com.path.android.jobqueue.persistentQueue.sqlite.SqlHelper.Property;

public class DbOpenHelper extends SQLiteOpenHelper {
    static final Property BASE_JOB_COLUMN;
    static final int COLUMN_COUNT = 9;
    static final Property CREATED_NS_COLUMN;
    private static final int DB_VERSION = 3;
    static final Property DELAY_UNTIL_NS_COLUMN;
    static final Property GROUP_ID_COLUMN;
    static final Property ID_COLUMN;
    static final String JOB_HOLDER_TABLE_NAME = "job_holder";
    static final Property PRIORITY_COLUMN;
    static final Property REQUIRES_NETWORK_COLUMN;
    static final Property RUNNING_SESSION_ID_COLUMN;
    static final Property RUN_COUNT_COLUMN;

    static {
        ID_COLUMN = new Property(FieldType.FOREIGN_ID_FIELD_SUFFIX, "integer", 0);
        PRIORITY_COLUMN = new Property("priority", "integer", 1);
        GROUP_ID_COLUMN = new Property("group_id", "text", 2);
        RUN_COUNT_COLUMN = new Property("run_count", "integer", DB_VERSION);
        BASE_JOB_COLUMN = new Property("base_job", "byte", 4);
        CREATED_NS_COLUMN = new Property("created_ns", "long", 5);
        DELAY_UNTIL_NS_COLUMN = new Property("delay_until_ns", "long", 6);
        RUNNING_SESSION_ID_COLUMN = new Property("running_session_id", "long", 7);
        REQUIRES_NETWORK_COLUMN = new Property("requires_network", "integer", 8);
    }

    public DbOpenHelper(Context context, String name) {
        super(context, name, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SqlHelper.create(JOB_HOLDER_TABLE_NAME, ID_COLUMN, PRIORITY_COLUMN, GROUP_ID_COLUMN, RUN_COUNT_COLUMN, BASE_JOB_COLUMN, CREATED_NS_COLUMN, DELAY_UNTIL_NS_COLUMN, RUNNING_SESSION_ID_COLUMN, REQUIRES_NETWORK_COLUMN));
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SqlHelper.drop(JOB_HOLDER_TABLE_NAME));
        onCreate(sqLiteDatabase);
    }
}
