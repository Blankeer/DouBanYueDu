package com.igexin.download;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import com.douban.book.reader.fragment.WorksListFragment_;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.query.SimpleComparison;
import com.sina.weibo.sdk.constant.WBPageConstants.ParamKey;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import u.aly.dx;

public final class DownloadProvider extends ContentProvider {
    private static String a;
    private static final UriMatcher b;
    private static final String[] c;
    private static HashSet d;
    private SQLiteOpenHelper e;

    static {
        int i = 0;
        a = "tmpd8.db";
        b = new UriMatcher(-1);
        c = new String[]{FieldType.FOREIGN_ID_FIELD_SUFFIX, Downloads.COLUMN_APP_DATA, Downloads._DATA, Downloads.COLUMN_MIME_TYPE, Downloads.COLUMN_VISIBILITY, Downloads.COLUMN_DESTINATION, Downloads.COLUMN_CONTROL, SettingsJsonConstants.APP_STATUS_KEY, Downloads.COLUMN_LAST_MODIFICATION, Downloads.COLUMN_CREATE_MODIFICATION, Downloads.COLUMN_TOTAL_BYTES, Downloads.COLUMN_CURRENT_BYTES, QzoneShare.SHARE_TO_QQ_TITLE, SocialConstants.PARAM_COMMENT, Downloads.COLUMN_DATA8, Downloads.COLUMN_DATA10};
        d = new HashSet();
        while (i < c.length) {
            d.add(c[i]);
            i++;
        }
    }

    public DownloadProvider() {
        this.e = null;
    }

    private void a(SQLiteDatabase sQLiteDatabase) {
        try {
            sQLiteDatabase.execSQL("CREATE TABLE downloads(_id INTEGER PRIMARY KEY AUTOINCREMENT,uri TEXT, method INTEGER, entity TEXT, no_integrity BOOLEAN, hint TEXT, otaupdate BOOLEAN, _data TEXT, mimetype TEXT, destination INTEGER, no_system BOOLEAN, visibility INTEGER, control INTEGER default 0, status INTEGER, numfailed INTEGER, lastmod BIGINT, createmod BIGINT, extras TEXT, cookiedata TEXT, useragent TEXT, referer TEXT, total_bytes INTEGER, current_bytes INTEGER, etag TEXT, uid INTEGER, otheruid INTEGER, title TEXT, description TEXT, scanned BOOLEAN,data_1 TEXT, data_2 TEXT, data_3 TEXT, data_4 TEXT, data_5 TEXT, data_6 TEXT, data_7 TEXT, data_8 TEXT, data_9 TEXT, data_10 BIGINT, iswebicon INTEGER);");
        } catch (SQLException e) {
            throw e;
        }
    }

    private static final void a(String str, ContentValues contentValues, ContentValues contentValues2) {
        Long asLong = contentValues.getAsLong(str);
        if (asLong != null) {
            contentValues2.put(str, asLong);
        }
    }

    private void b(SQLiteDatabase sQLiteDatabase) {
        try {
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS downloads");
        } catch (SQLException e) {
            throw e;
        }
    }

    private static final void b(String str, ContentValues contentValues, ContentValues contentValues2) {
        Integer asInteger = contentValues.getAsInteger(str);
        if (asInteger != null) {
            contentValues2.put(str, asInteger);
        }
    }

    private static final void c(String str, ContentValues contentValues, ContentValues contentValues2) {
        Boolean asBoolean = contentValues.getAsBoolean(str);
        if (asBoolean != null) {
            contentValues2.put(str, asBoolean);
        }
    }

    private static final void d(String str, ContentValues contentValues, ContentValues contentValues2) {
        String asString = contentValues.getAsString(str);
        if (asString != null) {
            contentValues2.put(str, asString);
        }
    }

    public int delete(Uri uri, String str, String[] strArr) {
        h.a(str, d);
        SQLiteDatabase writableDatabase = this.e.getWritableDatabase();
        int match = b.match(uri);
        switch (match) {
            case dx.b /*1*/:
            case dx.c /*2*/:
                r1 = str != null ? match == 1 ? "( " + str + " )" : "( " + str + " ) AND " : Table.STRING_DEFAULT_VALUE;
                String str2 = match == 2 ? r1 + " ( _id = " + Long.parseLong((String) uri.getPathSegments().get(1)) + " ) " : r1;
                if (!(Binder.getCallingPid() == Process.myPid() || Binder.getCallingUid() == 0)) {
                    str2 = str2 + " AND ( uid=" + Binder.getCallingUid() + " OR " + Downloads.COLUMN_OTHER_UID + SimpleComparison.EQUAL_TO_OPERATION + Binder.getCallingUid() + " )";
                }
                match = writableDatabase.delete("downloads", str2, strArr);
                getContext().getContentResolver().notifyChange(uri, null);
                return match;
            default:
                throw new UnsupportedOperationException("Cannot delete URI: " + uri);
        }
    }

    public String getType(Uri uri) {
        switch (b.match(uri)) {
            case dx.b /*1*/:
                return "vnd.android.cursor.dir/download";
            case dx.c /*2*/:
                return "vnd.android.cursor.item/download";
            case dx.d /*3*/:
                return "vnd.android.cursor.sql/download";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase writableDatabase = this.e.getWritableDatabase();
        if (b.match(uri) != 1) {
            throw new IllegalArgumentException("Unknown/Invalid URI " + uri);
        }
        ContentValues contentValues2 = new ContentValues();
        d(Downloads._DATA, contentValues, contentValues2);
        d(WorksListFragment_.URI_ARG, contentValues, contentValues2);
        d(Downloads.COLUMN_APP_DATA, contentValues, contentValues2);
        c(Downloads.COLUMN_NO_INTEGRITY, contentValues, contentValues2);
        d(Downloads.COLUMN_FILE_NAME_HINT, contentValues, contentValues2);
        d(Downloads.COLUMN_MIME_TYPE, contentValues, contentValues2);
        Integer asInteger = contentValues.getAsInteger(Downloads.COLUMN_DESTINATION);
        if (asInteger != null) {
            contentValues2.put(Downloads.COLUMN_DESTINATION, asInteger);
        }
        Integer asInteger2 = contentValues.getAsInteger(Downloads.COLUMN_VISIBILITY);
        if (asInteger2 != null) {
            contentValues2.put(Downloads.COLUMN_VISIBILITY, asInteger2);
        } else if (asInteger.intValue() == 0) {
            contentValues2.put(Downloads.COLUMN_VISIBILITY, Integer.valueOf(1));
        } else {
            contentValues2.put(Downloads.COLUMN_VISIBILITY, Integer.valueOf(2));
        }
        b(Downloads.COLUMN_CONTROL, contentValues, contentValues2);
        if (contentValues.containsKey(SettingsJsonConstants.APP_STATUS_KEY)) {
            b(SettingsJsonConstants.APP_STATUS_KEY, contentValues, contentValues2);
        } else {
            contentValues2.put(SettingsJsonConstants.APP_STATUS_KEY, Integer.valueOf(Downloads.STATUS_PENDING));
        }
        contentValues2.put(Downloads.COLUMN_LAST_MODIFICATION, Long.valueOf(System.currentTimeMillis()));
        contentValues2.put(Downloads.COLUMN_CREATE_MODIFICATION, Long.valueOf(System.currentTimeMillis()));
        d(Downloads.COLUMN_EXTRAS, contentValues, contentValues2);
        d(Downloads.COLUMN_DATA1, contentValues, contentValues2);
        d(Downloads.COLUMN_DATA2, contentValues, contentValues2);
        d(Downloads.COLUMN_DATA3, contentValues, contentValues2);
        d(Downloads.COLUMN_DATA4, contentValues, contentValues2);
        d(Downloads.COLUMN_DATA5, contentValues, contentValues2);
        d(Downloads.COLUMN_DATA6, contentValues, contentValues2);
        d(Downloads.COLUMN_DATA7, contentValues, contentValues2);
        d(Downloads.COLUMN_DATA8, contentValues, contentValues2);
        d(Downloads.COLUMN_DATA9, contentValues, contentValues2);
        a(Downloads.COLUMN_DATA10, contentValues, contentValues2);
        b(Downloads.COLUMN_IS_WEB_ICON, contentValues, contentValues2);
        d(Downloads.COLUMN_COOKIE_DATA, contentValues, contentValues2);
        d(Downloads.COLUMN_USER_AGENT, contentValues, contentValues2);
        d(Downloads.COLUMN_REFERER, contentValues, contentValues2);
        contentValues2.put(ParamKey.UID, Integer.valueOf(Binder.getCallingUid()));
        if (Binder.getCallingUid() == 0) {
            b(ParamKey.UID, contentValues, contentValues2);
        }
        d(QzoneShare.SHARE_TO_QQ_TITLE, contentValues, contentValues2);
        d(SocialConstants.PARAM_COMMENT, contentValues, contentValues2);
        Context context = getContext();
        context.startService(new Intent(context, DownloadService.class));
        long insert = writableDatabase.insert("downloads", null, contentValues2);
        if (insert == -1) {
            return null;
        }
        context.startService(new Intent(context, DownloadService.class));
        Uri parse = Uri.parse(Downloads.a + "/" + insert);
        context.getContentResolver().notifyChange(uri, null);
        return parse;
    }

    public boolean onCreate() {
        this.e = new b(this, getContext());
        String str = "downloads." + getContext().getPackageName();
        b.addURI(str, "download", 1);
        b.addURI(str, "download/#", 2);
        b.addURI(str, "download/full/item", 3);
        Downloads.setContentUrl(str);
        return true;
    }

    public ParcelFileDescriptor openFile(Uri uri, String str) {
        String str2;
        ContentValues contentValues;
        Throwable th;
        Cursor cursor = null;
        Cursor query;
        ParcelFileDescriptor open;
        try {
            int count;
            query = query(uri, new String[]{Downloads._DATA}, null, null, null);
            if (query != null) {
                try {
                    count = query.getCount();
                } catch (Exception e) {
                    if (query == null) {
                        query.close();
                        str2 = null;
                    } else {
                        str2 = null;
                    }
                    if (str2 != null) {
                        throw new FileNotFoundException("No filename found.");
                    } else if (h.a(str2)) {
                        throw new FileNotFoundException("Invalid filename.");
                    } else if ("r".equals(str)) {
                        throw new FileNotFoundException("Bad mode for " + uri + ": " + str);
                    } else {
                        open = ParcelFileDescriptor.open(new File(str2), 268435456);
                        if (open == null) {
                            contentValues = new ContentValues();
                            contentValues.put(Downloads.COLUMN_LAST_MODIFICATION, Long.valueOf(System.currentTimeMillis()));
                            update(uri, contentValues, null, null);
                            return open;
                        }
                        throw new FileNotFoundException("couldn't open file");
                    }
                } catch (Throwable th2) {
                    cursor = query;
                    th = th2;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            count = 0;
            if (count == 1) {
                query.moveToFirst();
                String string = query.getString(0);
                if (query != null) {
                    query.close();
                    str2 = string;
                } else {
                    str2 = string;
                }
                if (str2 != null) {
                    throw new FileNotFoundException("No filename found.");
                } else if (h.a(str2)) {
                    throw new FileNotFoundException("Invalid filename.");
                } else if ("r".equals(str)) {
                    throw new FileNotFoundException("Bad mode for " + uri + ": " + str);
                } else {
                    open = ParcelFileDescriptor.open(new File(str2), 268435456);
                    if (open == null) {
                        throw new FileNotFoundException("couldn't open file");
                    }
                    contentValues = new ContentValues();
                    contentValues.put(Downloads.COLUMN_LAST_MODIFICATION, Long.valueOf(System.currentTimeMillis()));
                    update(uri, contentValues, null, null);
                    return open;
                }
            } else if (count == 0) {
                throw new FileNotFoundException("No entry for " + uri);
            } else {
                throw new FileNotFoundException("Multiple items at " + uri);
            }
        } catch (Exception e2) {
            query = null;
            if (query == null) {
                str2 = null;
            } else {
                query.close();
                str2 = null;
            }
            if (str2 != null) {
                throw new FileNotFoundException("No filename found.");
            } else if (h.a(str2)) {
                throw new FileNotFoundException("Invalid filename.");
            } else if ("r".equals(str)) {
                throw new FileNotFoundException("Bad mode for " + uri + ": " + str);
            } else {
                open = ParcelFileDescriptor.open(new File(str2), 268435456);
                if (open == null) {
                    throw new FileNotFoundException("couldn't open file");
                }
                contentValues = new ContentValues();
                contentValues.put(Downloads.COLUMN_LAST_MODIFICATION, Long.valueOf(System.currentTimeMillis()));
                update(uri, contentValues, null, null);
                return open;
            }
        } catch (Throwable th3) {
            th = th3;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.database.Cursor query(android.net.Uri r10, java.lang.String[] r11, java.lang.String r12, java.lang.String[] r13, java.lang.String r14) {
        /*
        r9 = this;
        r2 = 1;
        r5 = 0;
        r3 = 0;
        r0 = d;
        com.igexin.download.h.a(r12, r0);
        r0 = r9.e;
        r1 = r0.getReadableDatabase();
        r0 = new android.database.sqlite.SQLiteQueryBuilder;
        r0.<init>();
        r4 = b;
        r4 = r4.match(r10);
        switch(r4) {
            case 1: goto L_0x0035;
            case 2: goto L_0x00b6;
            case 3: goto L_0x00d0;
            default: goto L_0x001c;
        };
    L_0x001c:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Unknown URI: ";
        r1 = r1.append(r2);
        r1 = r1.append(r10);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0035:
        r6 = "downloads";
        r0.setTables(r6);
    L_0x003a:
        r6 = android.os.Binder.getCallingPid();
        r7 = android.os.Process.myPid();
        if (r6 == r7) goto L_0x012c;
    L_0x0044:
        r6 = android.os.Binder.getCallingUid();
        if (r6 == 0) goto L_0x012c;
    L_0x004a:
        r6 = android.os.Process.supportsProcesses();
        if (r6 == 0) goto L_0x012c;
    L_0x0050:
        if (r2 != 0) goto L_0x0057;
    L_0x0052:
        r2 = " AND ";
        r0.appendWhere(r2);
    L_0x0057:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r6 = "( uid=";
        r2 = r2.append(r6);
        r6 = android.os.Binder.getCallingUid();
        r2 = r2.append(r6);
        r6 = " OR ";
        r2 = r2.append(r6);
        r6 = "otheruid";
        r2 = r2.append(r6);
        r6 = "=";
        r2 = r2.append(r6);
        r6 = android.os.Binder.getCallingUid();
        r2 = r2.append(r6);
        r6 = " )";
        r2 = r2.append(r6);
        r2 = r2.toString();
        r0.appendWhere(r2);
        if (r11 != 0) goto L_0x00d7;
    L_0x0093:
        r11 = c;
        r2 = r11;
    L_0x0096:
        switch(r4) {
            case 3: goto L_0x0109;
            default: goto L_0x0099;
        };
    L_0x0099:
        r3 = r12;
        r4 = r13;
        r6 = r5;
        r7 = r14;
        r1 = r0.query(r1, r2, r3, r4, r5, r6, r7);
    L_0x00a1:
        if (r1 == 0) goto L_0x0129;
    L_0x00a3:
        r0 = new com.igexin.download.c;
        r0.<init>(r9, r1);
    L_0x00a8:
        if (r0 == 0) goto L_0x00b5;
    L_0x00aa:
        r1 = r9.getContext();
        r1 = r1.getContentResolver();
        r0.setNotificationUri(r1, r10);
    L_0x00b5:
        return r0;
    L_0x00b6:
        r6 = "downloads";
        r0.setTables(r6);
        r6 = "_id=";
        r0.appendWhere(r6);
        r6 = r10.getPathSegments();
        r2 = r6.get(r2);
        r2 = (java.lang.CharSequence) r2;
        r0.appendWhere(r2);
        r2 = r3;
        goto L_0x003a;
    L_0x00d0:
        r6 = "downloads";
        r0.setTables(r6);
        goto L_0x003a;
    L_0x00d7:
        r2 = r3;
    L_0x00d8:
        r6 = r11.length;
        if (r2 >= r6) goto L_0x012c;
    L_0x00db:
        r6 = d;
        r7 = r11[r2];
        r6 = r6.contains(r7);
        if (r6 != 0) goto L_0x0106;
    L_0x00e5:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r3 = "column ";
        r1 = r1.append(r3);
        r2 = r11[r2];
        r1 = r1.append(r2);
        r2 = " is not allowed in queries";
        r1 = r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0106:
        r2 = r2 + 1;
        goto L_0x00d8;
    L_0x0109:
        if (r13 == 0) goto L_0x0099;
    L_0x010b:
        r4 = r13.length;
        if (r4 <= 0) goto L_0x0099;
    L_0x010e:
        r8 = r13[r3];
        r4 = r13.length;
        r6 = r4 + -1;
        r4 = new java.lang.String[r6];
    L_0x0115:
        if (r3 >= r6) goto L_0x0120;
    L_0x0117:
        r7 = r3 + 1;
        r7 = r13[r7];
        r4[r3] = r7;
        r3 = r3 + 1;
        goto L_0x0115;
    L_0x0120:
        r3 = r12;
        r6 = r5;
        r7 = r14;
        r1 = r0.query(r1, r2, r3, r4, r5, r6, r7, r8);
        goto L_0x00a1;
    L_0x0129:
        r0 = r1;
        goto L_0x00a8;
    L_0x012c:
        r2 = r11;
        goto L_0x0096;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.igexin.download.DownloadProvider.query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String):android.database.Cursor");
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int i;
        int i2;
        int i3 = 0;
        h.a(str, d);
        SQLiteDatabase writableDatabase = this.e.getWritableDatabase();
        if (Binder.getCallingPid() != Process.myPid()) {
            ContentValues contentValues2 = new ContentValues();
            d(Downloads.COLUMN_APP_DATA, contentValues, contentValues2);
            b(Downloads.COLUMN_VISIBILITY, contentValues, contentValues2);
            Integer asInteger = contentValues.getAsInteger(Downloads.COLUMN_CONTROL);
            if (asInteger != null) {
                contentValues2.put(Downloads.COLUMN_CONTROL, asInteger);
                i = 1;
            } else {
                i = 0;
            }
            b(Downloads.COLUMN_CONTROL, contentValues, contentValues2);
            d(QzoneShare.SHARE_TO_QQ_TITLE, contentValues, contentValues2);
            d(SocialConstants.PARAM_COMMENT, contentValues, contentValues2);
            contentValues = contentValues2;
            i2 = i;
        } else {
            i2 = 0;
        }
        i = b.match(uri);
        switch (i) {
            case dx.b /*1*/:
            case dx.c /*2*/:
                r4 = str != null ? i == 1 ? "( " + str + " )" : "( " + str + " ) AND " : Table.STRING_DEFAULT_VALUE;
                String str2 = i == 2 ? r4 + " ( _id = " + Long.parseLong((String) uri.getPathSegments().get(1)) + " ) " : r4;
                if (!(Binder.getCallingPid() == Process.myPid() || Binder.getCallingUid() == 0)) {
                    str2 = str2 + " AND ( uid=" + Binder.getCallingUid() + " OR " + Downloads.COLUMN_OTHER_UID + SimpleComparison.EQUAL_TO_OPERATION + Binder.getCallingUid() + " )";
                }
                if (contentValues.size() > 0) {
                    i3 = writableDatabase.update("downloads", contentValues, str2, strArr);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                if (i2 != 0) {
                    Context context = getContext();
                    context.startService(new Intent(context, DownloadService.class));
                }
                return i3;
            default:
                throw new UnsupportedOperationException("Cannot update URI: " + uri);
        }
    }
}
