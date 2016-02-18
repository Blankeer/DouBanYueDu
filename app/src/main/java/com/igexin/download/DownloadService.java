package com.igexin.download;

import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.media.TransportMediator;
import com.douban.book.reader.fragment.WorksListFragment_;
import com.j256.ormlite.field.FieldType;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class DownloadService extends Service {
    static boolean a;
    private d b;
    private ArrayList c;
    private f d;
    private boolean e;
    private e f;
    private boolean g;
    private Object h;
    private CharArrayBuffer i;
    private CharArrayBuffer j;

    static {
        a = false;
    }

    private long a(int i, long j) {
        DownloadInfo downloadInfo = (DownloadInfo) this.c.get(i);
        if (Downloads.isStatusCompleted(downloadInfo.mStatus)) {
            return -1;
        }
        if (downloadInfo.mStatus != Downloads.STATUS_RUNNING_PAUSED) {
            return 0;
        }
        if (downloadInfo.mNumFailed == 0) {
            return 0;
        }
        long restartTime = downloadInfo.restartTime();
        return restartTime <= j ? 0 : restartTime - j;
    }

    private String a(String str, Cursor cursor, String str2) {
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow(str2);
        if (str == null) {
            return cursor.getString(columnIndexOrThrow);
        }
        if (this.j == null) {
            this.j = new CharArrayBuffer(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        }
        cursor.copyStringToBuffer(columnIndexOrThrow, this.j);
        int i = this.j.sizeCopied;
        if (i != str.length()) {
            return cursor.getString(columnIndexOrThrow);
        }
        if (this.i == null || this.i.sizeCopied < i) {
            this.i = new CharArrayBuffer(i);
        }
        char[] cArr = this.i.data;
        char[] cArr2 = this.j.data;
        str.getChars(0, i, cArr, 0);
        for (columnIndexOrThrow = i - 1; columnIndexOrThrow >= 0; columnIndexOrThrow--) {
            if (cArr[columnIndexOrThrow] != cArr2[columnIndexOrThrow]) {
                return new String(cArr2, 0, i);
            }
        }
        return str;
    }

    private void a() {
        synchronized (this) {
            this.e = true;
            if (this.d == null) {
                this.d = new f(this);
                this.d.start();
            }
        }
    }

    private void a(Cursor cursor, int i, boolean z, boolean z2, long j) {
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow(SettingsJsonConstants.APP_STATUS_KEY);
        int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow("numfailed");
        int i2 = cursor.getInt(cursor.getColumnIndexOrThrow("method"));
        DownloadInfo downloadInfo = new DownloadInfo(cursor.getInt(cursor.getColumnIndexOrThrow(FieldType.FOREIGN_ID_FIELD_SUFFIX)), cursor.getString(cursor.getColumnIndexOrThrow(WorksListFragment_.URI_ARG)), cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_NO_INTEGRITY)) == 1, cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_FILE_NAME_HINT)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads._DATA)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_MIME_TYPE)), cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DESTINATION)), cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_VISIBILITY)), cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_CONTROL)), cursor.getInt(columnIndexOrThrow), cursor.getInt(columnIndexOrThrow2), 268435455 & i2, i2 >> 28, cursor.getLong(cursor.getColumnIndexOrThrow(Downloads.COLUMN_LAST_MODIFICATION)), cursor.getLong(cursor.getColumnIndexOrThrow(Downloads.COLUMN_CREATE_MODIFICATION)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_EXTRAS)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_COOKIE_DATA)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_USER_AGENT)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_REFERER)), cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_TOTAL_BYTES)), cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_CURRENT_BYTES)), cursor.getString(cursor.getColumnIndexOrThrow("etag")), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DATA1)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DATA2)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DATA3)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DATA4)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DATA5)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DATA6)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DATA7)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DATA8)), cursor.getString(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DATA9)), cursor.getLong(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DATA10)), cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_IS_WEB_ICON)), cursor.getInt(cursor.getColumnIndexOrThrow("scanned")) == 1);
        this.c.add(i, downloadInfo);
        if (!downloadInfo.canUseNetwork(z, z2)) {
            return;
        }
        if (("wifi".equals(downloadInfo.mData9) && !h.b((Context) this)) || !downloadInfo.isReadyToStart(j)) {
            return;
        }
        ContentValues contentValues;
        if (a(SdkDownLoader.a)) {
            if (!downloadInfo.mHasActiveThread) {
                if (downloadInfo.mStatus != Downloads.STATUS_RUNNING) {
                    downloadInfo.mStatus = Downloads.STATUS_RUNNING;
                    contentValues = new ContentValues();
                    contentValues.put(SettingsJsonConstants.APP_STATUS_KEY, Integer.valueOf(downloadInfo.mStatus));
                    getContentResolver().update(ContentUris.withAppendedId(Downloads.a, (long) downloadInfo.mId), contentValues, null, null);
                }
                g gVar = new g(this, downloadInfo);
                downloadInfo.mHasActiveThread = true;
                gVar.start();
                downloadInfo.mNotice = false;
            }
        } else if (downloadInfo.mStatus != Downloads.STATUS_PENDING) {
            downloadInfo.mStatus = Downloads.STATUS_PENDING;
            contentValues = new ContentValues();
            contentValues.put(SettingsJsonConstants.APP_STATUS_KEY, Integer.valueOf(downloadInfo.mStatus));
            getContentResolver().update(ContentUris.withAppendedId(Downloads.a, (long) downloadInfo.mId), contentValues, null, null);
        }
    }

    private boolean a(int i) {
        Cursor query = getContentResolver().query(Downloads.a, new String[]{FieldType.FOREIGN_ID_FIELD_SUFFIX}, "status == '192'", null, null);
        if (query == null) {
            return false;
        }
        boolean z = query.getCount() < i;
        query.close();
        return z;
    }

    private boolean a(Cursor cursor, int i) {
        DownloadInfo downloadInfo = (DownloadInfo) this.c.get(i);
        synchronized (this) {
            if (this.h != null) {
                try {
                    this.h.getClass().getMethod("scanFile", new Class[]{String.class, String.class}).invoke(this.h, new Object[]{downloadInfo.mFileName, downloadInfo.mMimeType});
                    downloadInfo.mMediaScanned = true;
                    if (cursor != null) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("scanned", Integer.valueOf(1));
                        getContentResolver().update(ContentUris.withAppendedId(Downloads.a, cursor.getLong(cursor.getColumnIndexOrThrow(FieldType.FOREIGN_ID_FIELD_SUFFIX))), contentValues, null, null);
                    }
                    return true;
                } catch (SecurityException e) {
                } catch (NoSuchMethodException e2) {
                } catch (IllegalArgumentException e3) {
                } catch (IllegalAccessException e4) {
                } catch (InvocationTargetException e5) {
                } catch (Exception e6) {
                }
            }
            return false;
        }
    }

    private boolean a(String str) {
        return true;
    }

    private void b(int i) {
        DownloadInfo downloadInfo = (DownloadInfo) this.c.get(i);
        if (downloadInfo.mStatus == Downloads.STATUS_RUNNING) {
            downloadInfo.mStatus = Downloads.STATUS_CANCELED;
        } else if (!(downloadInfo.mDestination == 0 || downloadInfo.mFileName == null)) {
            new File(downloadInfo.mFileName).delete();
        }
        this.c.remove(i);
    }

    private void b(Cursor cursor, int i, boolean z, boolean z2, long j) {
        DownloadInfo downloadInfo = (DownloadInfo) this.c.get(i);
        int columnIndexOrThrow = cursor.getColumnIndexOrThrow(SettingsJsonConstants.APP_STATUS_KEY);
        int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow("numfailed");
        downloadInfo.mId = cursor.getInt(cursor.getColumnIndexOrThrow(FieldType.FOREIGN_ID_FIELD_SUFFIX));
        downloadInfo.mUri = a(downloadInfo.mUri, cursor, WorksListFragment_.URI_ARG);
        downloadInfo.mNoIntegrity = cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_NO_INTEGRITY)) == 1;
        downloadInfo.mHint = a(downloadInfo.mHint, cursor, Downloads.COLUMN_FILE_NAME_HINT);
        downloadInfo.mFileName = a(downloadInfo.mFileName, cursor, Downloads._DATA);
        downloadInfo.mMimeType = a(downloadInfo.mMimeType, cursor, Downloads.COLUMN_MIME_TYPE);
        downloadInfo.mDestination = cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_DESTINATION));
        downloadInfo.mVisibility = cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_VISIBILITY));
        synchronized (downloadInfo) {
            downloadInfo.mControl = cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_CONTROL));
        }
        downloadInfo.mStatus = cursor.getInt(columnIndexOrThrow);
        downloadInfo.mNumFailed = cursor.getInt(columnIndexOrThrow2);
        int i2 = cursor.getInt(cursor.getColumnIndexOrThrow("method"));
        downloadInfo.mRetryAfter = 268435455 & i2;
        downloadInfo.mRedirectCount = i2 >> 28;
        downloadInfo.mLastMod = cursor.getLong(cursor.getColumnIndexOrThrow(Downloads.COLUMN_LAST_MODIFICATION));
        downloadInfo.mCreateMod = cursor.getLong(cursor.getColumnIndexOrThrow(Downloads.COLUMN_CREATE_MODIFICATION));
        downloadInfo.mCookies = a(downloadInfo.mCookies, cursor, Downloads.COLUMN_COOKIE_DATA);
        downloadInfo.mExtras = a(downloadInfo.mExtras, cursor, Downloads.COLUMN_EXTRAS);
        downloadInfo.mUserAgent = a(downloadInfo.mUserAgent, cursor, Downloads.COLUMN_USER_AGENT);
        downloadInfo.mReferer = a(downloadInfo.mReferer, cursor, Downloads.COLUMN_REFERER);
        downloadInfo.mTotalBytes = cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_TOTAL_BYTES));
        downloadInfo.mCurrentBytes = cursor.getInt(cursor.getColumnIndexOrThrow(Downloads.COLUMN_CURRENT_BYTES));
        downloadInfo.mETag = a(downloadInfo.mETag, cursor, "etag");
        if (!downloadInfo.canUseNetwork(z, z2)) {
            return;
        }
        if (("wifi".equals(downloadInfo.mData9) && !h.b((Context) this)) || !downloadInfo.isReadyToRestart(j)) {
            return;
        }
        ContentValues contentValues;
        if (a(SdkDownLoader.a)) {
            if (!downloadInfo.mHasActiveThread) {
                downloadInfo.mStatus = Downloads.STATUS_RUNNING;
                contentValues = new ContentValues();
                contentValues.put(SettingsJsonConstants.APP_STATUS_KEY, Integer.valueOf(downloadInfo.mStatus));
                getContentResolver().update(ContentUris.withAppendedId(Downloads.a, (long) downloadInfo.mId), contentValues, null, null);
                g gVar = new g(this, downloadInfo);
                downloadInfo.mHasActiveThread = true;
                gVar.start();
                downloadInfo.mNotice = false;
            }
        } else if (downloadInfo.mStatus != Downloads.STATUS_PENDING) {
            downloadInfo.mStatus = Downloads.STATUS_PENDING;
            contentValues = new ContentValues();
            contentValues.put(SettingsJsonConstants.APP_STATUS_KEY, Integer.valueOf(downloadInfo.mStatus));
            getContentResolver().update(ContentUris.withAppendedId(Downloads.a, (long) downloadInfo.mId), contentValues, null, null);
        }
    }

    private boolean b() {
        return this.h != null;
    }

    private boolean c(int i) {
        return ((DownloadInfo) this.c.get(i)).hasCompletionNotification();
    }

    private boolean d(int i) {
        DownloadInfo downloadInfo = (DownloadInfo) this.c.get(i);
        return !downloadInfo.mMediaScanned && downloadInfo.mDestination == 0 && Downloads.isStatusSuccess(downloadInfo.mStatus) && !a(downloadInfo.mMimeType);
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Cannot bind to Download Manager Service");
    }

    public void onCreate() {
        super.onCreate();
        try {
            this.c = (ArrayList) Class.forName("com.google.android.collect.Lists").getMethod("newArrayList", null).invoke(null, new Object[0]);
        } catch (ClassNotFoundException e) {
        } catch (SecurityException e2) {
        } catch (NoSuchMethodException e3) {
        } catch (IllegalArgumentException e4) {
        } catch (IllegalAccessException e5) {
        } catch (InvocationTargetException e6) {
        }
        this.b = new d(this);
        getContentResolver().registerContentObserver(Downloads.a, true, this.b);
        this.h = null;
        this.g = false;
        this.f = new e(this);
    }

    public void onDestroy() {
        getContentResolver().unregisterContentObserver(this.b);
        super.onDestroy();
    }

    public void onStart(Intent intent, int i) {
        super.onStart(intent, i);
        a();
    }
}
