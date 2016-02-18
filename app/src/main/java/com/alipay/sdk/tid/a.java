package com.alipay.sdk.tid;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import com.alipay.sdk.encrypt.b;
import io.realm.internal.Table;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class a extends SQLiteOpenHelper {
    private static final String a = "msp.db";
    private static final int b = 1;
    private WeakReference<Context> c;

    public a(Context context) {
        super(context, a, null, b);
        this.c = new WeakReference(context);
    }

    public final void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("create table if not exists tb_tid (name text primary key, tid text, key_tid text, dt datetime);");
    }

    public final void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("drop table if exists tb_tid");
        onCreate(sQLiteDatabase);
    }

    public final void a(String str, String str2, String str3, String str4) {
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = getWritableDatabase();
            if (a(sQLiteDatabase, str, str2)) {
                a(sQLiteDatabase, str, str2, str3, str4);
            } else {
                String a = b.a(b, str3, com.alipay.sdk.util.b.c((Context) this.c.get()));
                sQLiteDatabase.execSQL("insert into tb_tid (name, tid, key_tid, dt) values (?, ?, ?, datetime('now', 'localtime'))", new Object[]{c(str, str2), a, str4});
                Cursor rawQuery = sQLiteDatabase.rawQuery("select name from tb_tid where tid!='' order by dt asc", null);
                if (rawQuery.getCount() <= 14) {
                    rawQuery.close();
                } else {
                    int i;
                    int count = rawQuery.getCount() - 14;
                    String[] strArr = new String[count];
                    if (rawQuery.moveToFirst()) {
                        i = 0;
                        do {
                            strArr[i] = rawQuery.getString(0);
                            i += b;
                            if (!rawQuery.moveToNext()) {
                                break;
                            }
                        } while (count > i);
                    }
                    rawQuery.close();
                    for (i = 0; i < count; i += b) {
                        if (!TextUtils.isEmpty(strArr[i])) {
                            a(sQLiteDatabase, strArr[i]);
                        }
                    }
                }
            }
            if (sQLiteDatabase != null && sQLiteDatabase.isOpen()) {
                sQLiteDatabase.close();
            }
        } catch (Exception e) {
            if (sQLiteDatabase != null && sQLiteDatabase.isOpen()) {
                sQLiteDatabase.close();
            }
        } catch (Throwable th) {
            if (sQLiteDatabase != null && sQLiteDatabase.isOpen()) {
                sQLiteDatabase.close();
            }
        }
    }

    private void d(String str, String str2) {
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = getWritableDatabase();
            a(sQLiteDatabase, str, str2, Table.STRING_DEFAULT_VALUE, Table.STRING_DEFAULT_VALUE);
            a(sQLiteDatabase, c(str, str2));
            if (sQLiteDatabase != null && sQLiteDatabase.isOpen()) {
                sQLiteDatabase.close();
            }
        } catch (Exception e) {
            if (sQLiteDatabase != null && sQLiteDatabase.isOpen()) {
                sQLiteDatabase.close();
            }
        } catch (Throwable th) {
            if (sQLiteDatabase != null && sQLiteDatabase.isOpen()) {
                sQLiteDatabase.close();
            }
        }
    }

    public final String a(String str, String str2) {
        SQLiteDatabase readableDatabase;
        Throwable th;
        String str3 = null;
        String str4 = "select tid from tb_tid where name=?";
        Cursor rawQuery;
        try {
            readableDatabase = getReadableDatabase();
            try {
                String[] strArr = new String[b];
                strArr[0] = c(str, str2);
                rawQuery = readableDatabase.rawQuery(str4, strArr);
            } catch (Exception e) {
                rawQuery = null;
                if (rawQuery != null) {
                    rawQuery.close();
                }
                if (readableDatabase == null) {
                }
                str4 = null;
                if (TextUtils.isEmpty(str4)) {
                    return b.a(2, str4, com.alipay.sdk.util.b.c((Context) this.c.get()));
                }
                return str4;
            } catch (Throwable th2) {
                Throwable th3 = th2;
                rawQuery = null;
                th = th3;
                if (rawQuery != null) {
                    rawQuery.close();
                }
                readableDatabase.close();
                throw th;
            }
            try {
                if (rawQuery.moveToFirst()) {
                    str3 = rawQuery.getString(0);
                }
                if (rawQuery != null) {
                    rawQuery.close();
                }
                if (readableDatabase == null || !readableDatabase.isOpen()) {
                    str4 = str3;
                } else {
                    readableDatabase.close();
                    str4 = str3;
                }
            } catch (Exception e2) {
                if (rawQuery != null) {
                    rawQuery.close();
                }
                if (readableDatabase == null && readableDatabase.isOpen()) {
                    readableDatabase.close();
                    str4 = null;
                } else {
                    str4 = null;
                }
                if (TextUtils.isEmpty(str4)) {
                    return str4;
                }
                return b.a(2, str4, com.alipay.sdk.util.b.c((Context) this.c.get()));
            } catch (Throwable th4) {
                th = th4;
                if (rawQuery != null) {
                    rawQuery.close();
                }
                if (readableDatabase != null && readableDatabase.isOpen()) {
                    readableDatabase.close();
                }
                throw th;
            }
        } catch (Exception e3) {
            rawQuery = null;
            readableDatabase = null;
            if (rawQuery != null) {
                rawQuery.close();
            }
            if (readableDatabase == null) {
            }
            str4 = null;
            if (TextUtils.isEmpty(str4)) {
                return str4;
            }
            return b.a(2, str4, com.alipay.sdk.util.b.c((Context) this.c.get()));
        } catch (Throwable th22) {
            readableDatabase = null;
            th = th22;
            rawQuery = null;
            if (rawQuery != null) {
                rawQuery.close();
            }
            readableDatabase.close();
            throw th;
        }
        if (TextUtils.isEmpty(str4)) {
            return b.a(2, str4, com.alipay.sdk.util.b.c((Context) this.c.get()));
        }
        return str4;
    }

    private long e(String str, String str2) {
        SQLiteDatabase readableDatabase;
        Throwable th;
        Cursor cursor = null;
        long j = 0;
        String str3 = "select dt from tb_tid where name=?";
        try {
            readableDatabase = getReadableDatabase();
            try {
                String[] strArr = new String[b];
                strArr[0] = c(str, str2);
                cursor = readableDatabase.rawQuery(str3, strArr);
                if (cursor.moveToFirst()) {
                    j = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(cursor.getString(0)).getTime();
                }
                if (cursor != null) {
                    cursor.close();
                }
                if (readableDatabase != null && readableDatabase.isOpen()) {
                    readableDatabase.close();
                }
            } catch (Exception e) {
                if (cursor != null) {
                    cursor.close();
                }
                if (readableDatabase != null && readableDatabase.isOpen()) {
                    readableDatabase.close();
                }
                return j;
            } catch (Throwable th2) {
                th = th2;
                if (cursor != null) {
                    cursor.close();
                }
                if (readableDatabase != null && readableDatabase.isOpen()) {
                    readableDatabase.close();
                }
                throw th;
            }
        } catch (Exception e2) {
            readableDatabase = cursor;
            if (cursor != null) {
                cursor.close();
            }
            readableDatabase.close();
            return j;
        } catch (Throwable th3) {
            th = th3;
            readableDatabase = cursor;
            if (cursor != null) {
                cursor.close();
            }
            readableDatabase.close();
            throw th;
        }
        return j;
    }

    private List<String> a() {
        Cursor rawQuery;
        SQLiteDatabase sQLiteDatabase;
        Throwable th;
        Cursor cursor = null;
        List<String> arrayList = new ArrayList();
        SQLiteDatabase readableDatabase;
        try {
            readableDatabase = getReadableDatabase();
            try {
                rawQuery = readableDatabase.rawQuery("select tid from tb_tid", null);
                while (rawQuery.moveToNext()) {
                    try {
                        Object string = rawQuery.getString(0);
                        if (!TextUtils.isEmpty(string)) {
                            arrayList.add(b.a(2, string, com.alipay.sdk.util.b.c((Context) this.c.get())));
                        }
                    } catch (Exception e) {
                        cursor = rawQuery;
                        sQLiteDatabase = readableDatabase;
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
                if (rawQuery != null) {
                    rawQuery.close();
                }
                if (readableDatabase != null && readableDatabase.isOpen()) {
                    readableDatabase.close();
                }
            } catch (Exception e2) {
                sQLiteDatabase = readableDatabase;
                if (cursor != null) {
                    cursor.close();
                }
                sQLiteDatabase.close();
                return arrayList;
            } catch (Throwable th3) {
                Throwable th4 = th3;
                rawQuery = null;
                th = th4;
                if (rawQuery != null) {
                    rawQuery.close();
                }
                readableDatabase.close();
                throw th;
            }
        } catch (Exception e3) {
            sQLiteDatabase = null;
            if (cursor != null) {
                cursor.close();
            }
            if (sQLiteDatabase != null && sQLiteDatabase.isOpen()) {
                sQLiteDatabase.close();
            }
            return arrayList;
        } catch (Throwable th32) {
            readableDatabase = null;
            th = th32;
            rawQuery = null;
            if (rawQuery != null) {
                rawQuery.close();
            }
            if (readableDatabase != null && readableDatabase.isOpen()) {
                readableDatabase.close();
            }
            throw th;
        }
        return arrayList;
    }

    public final String b(String str, String str2) {
        SQLiteDatabase readableDatabase;
        Cursor rawQuery;
        Throwable th;
        String str3 = null;
        String str4 = "select key_tid from tb_tid where name=?";
        try {
            readableDatabase = getReadableDatabase();
            try {
                String[] strArr = new String[b];
                strArr[0] = c(str, str2);
                rawQuery = readableDatabase.rawQuery(str4, strArr);
            } catch (Exception e) {
                rawQuery = null;
                if (rawQuery != null) {
                    rawQuery.close();
                }
                if (readableDatabase != null && readableDatabase.isOpen()) {
                    readableDatabase.close();
                }
                return str3;
            } catch (Throwable th2) {
                Throwable th3 = th2;
                rawQuery = null;
                th = th3;
                if (rawQuery != null) {
                    rawQuery.close();
                }
                if (readableDatabase != null && readableDatabase.isOpen()) {
                    readableDatabase.close();
                }
                throw th;
            }
            try {
                if (rawQuery.moveToFirst()) {
                    str3 = rawQuery.getString(0);
                }
                if (rawQuery != null) {
                    rawQuery.close();
                }
                if (readableDatabase != null && readableDatabase.isOpen()) {
                    readableDatabase.close();
                }
            } catch (Exception e2) {
                if (rawQuery != null) {
                    rawQuery.close();
                }
                readableDatabase.close();
                return str3;
            } catch (Throwable th4) {
                th = th4;
                if (rawQuery != null) {
                    rawQuery.close();
                }
                readableDatabase.close();
                throw th;
            }
        } catch (Exception e3) {
            rawQuery = null;
            readableDatabase = null;
            if (rawQuery != null) {
                rawQuery.close();
            }
            readableDatabase.close();
            return str3;
        } catch (Throwable th22) {
            readableDatabase = null;
            th = th22;
            rawQuery = null;
            if (rawQuery != null) {
                rawQuery.close();
            }
            readableDatabase.close();
            throw th;
        }
        return str3;
    }

    private static boolean a(SQLiteDatabase sQLiteDatabase, String str, String str2) {
        int i;
        String str3 = "select count(*) from tb_tid where name=?";
        Cursor cursor = null;
        try {
            int i2;
            String[] strArr = new String[b];
            strArr[0] = c(str, str2);
            cursor = sQLiteDatabase.rawQuery(str3, strArr);
            if (cursor.moveToFirst()) {
                i2 = cursor.getInt(0);
            } else {
                i2 = 0;
            }
            if (cursor != null) {
                cursor.close();
                i = i2;
            } else {
                i = i2;
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
                i = 0;
            } else {
                i = 0;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (i > 0) {
            return true;
        }
        return false;
    }

    public static String c(String str, String str2) {
        return str + str2;
    }

    private void b(SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, String str4) {
        int i = 0;
        String a = b.a(b, str3, com.alipay.sdk.util.b.c((Context) this.c.get()));
        sQLiteDatabase.execSQL("insert into tb_tid (name, tid, key_tid, dt) values (?, ?, ?, datetime('now', 'localtime'))", new Object[]{c(str, str2), a, str4});
        Cursor rawQuery = sQLiteDatabase.rawQuery("select name from tb_tid where tid!='' order by dt asc", null);
        if (rawQuery.getCount() <= 14) {
            rawQuery.close();
            return;
        }
        int count = rawQuery.getCount() - 14;
        String[] strArr = new String[count];
        if (rawQuery.moveToFirst()) {
            int i2 = 0;
            do {
                strArr[i2] = rawQuery.getString(0);
                i2 += b;
                if (!rawQuery.moveToNext()) {
                    break;
                }
            } while (count > i2);
        }
        rawQuery.close();
        while (i < count) {
            if (!TextUtils.isEmpty(strArr[i])) {
                a(sQLiteDatabase, strArr[i]);
            }
            i += b;
        }
    }

    public final void a(SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, String str4) {
        sQLiteDatabase.execSQL("update tb_tid set tid=?, key_tid=?, dt=datetime('now', 'localtime') where name=?", new Object[]{b.a(b, str3, com.alipay.sdk.util.b.c((Context) this.c.get())), str4, c(str, str2)});
    }

    public static void a(SQLiteDatabase sQLiteDatabase, String str) {
        try {
            String[] strArr = new String[b];
            strArr[0] = str;
            sQLiteDatabase.delete("tb_tid", "name=?", strArr);
        } catch (Exception e) {
        }
    }

    private static void a(SQLiteDatabase sQLiteDatabase) {
        int i = 0;
        Cursor rawQuery = sQLiteDatabase.rawQuery("select name from tb_tid where tid!='' order by dt asc", null);
        if (rawQuery.getCount() <= 14) {
            rawQuery.close();
            return;
        }
        int count = rawQuery.getCount() - 14;
        String[] strArr = new String[count];
        if (rawQuery.moveToFirst()) {
            int i2 = 0;
            do {
                strArr[i2] = rawQuery.getString(0);
                i2 += b;
                if (!rawQuery.moveToNext()) {
                    break;
                }
            } while (count > i2);
        }
        rawQuery.close();
        while (i < count) {
            if (!TextUtils.isEmpty(strArr[i])) {
                a(sQLiteDatabase, strArr[i]);
            }
            i += b;
        }
    }
}
