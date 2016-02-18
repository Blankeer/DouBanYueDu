package com.igexin.download;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import com.douban.book.reader.fragment.WorksListFragment_;
import com.j256.ormlite.field.FieldType;
import com.tencent.open.SocialConstants;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SdkDownLoader {
    static int a;
    static String b;
    static SdkDownLoader c;
    Handler d;
    String[] e;
    private Context f;
    private List g;
    private Object h;
    public Map updateData;

    static {
        a = 3;
        b = "/libs/tmp";
    }

    private SdkDownLoader(Context context) {
        this.g = new ArrayList();
        this.updateData = new HashMap();
        this.h = new Object();
        this.e = new String[]{FieldType.FOREIGN_ID_FIELD_SUFFIX, Downloads._DATA, Downloads.COLUMN_FILE_NAME_HINT, SettingsJsonConstants.APP_STATUS_KEY, Downloads.COLUMN_TOTAL_BYTES, Downloads.COLUMN_CURRENT_BYTES};
        this.f = context;
        this.d = new j(this, context.getMainLooper());
    }

    private int a(ContentValues contentValues) {
        ContentResolver contentResolver = this.f.getContentResolver();
        contentValues.put(Downloads.COLUMN_DATA10, String.valueOf(System.currentTimeMillis()));
        Uri insert = contentResolver.insert(Downloads.a, contentValues);
        return insert != null ? Integer.parseInt((String) insert.getPathSegments().get(1)) : -1;
    }

    private int a(String str, String str2, ContentValues contentValues, String str3) {
        ContentValues contentValues2 = new ContentValues();
        if (contentValues != null) {
            contentValues2.putAll(contentValues);
        }
        contentValues2.put(Downloads.COLUMN_DESTINATION, Integer.valueOf(0));
        if (str != null) {
            contentValues2.put(WorksListFragment_.URI_ARG, str);
        }
        if (str2 != null) {
            contentValues2.put(Downloads.COLUMN_FILE_NAME_HINT, str2.replaceAll("\\*", Table.STRING_DEFAULT_VALUE));
        }
        if (str3 != null) {
            contentValues2.put(SocialConstants.PARAM_COMMENT, str3);
        }
        return a(contentValues2);
    }

    public static SdkDownLoader getInstantiate(Context context) {
        if (c == null) {
            c = new SdkDownLoader(context);
        }
        return c;
    }

    IDownloadCallback a(String str) {
        if (str == null) {
            return null;
        }
        for (IDownloadCallback iDownloadCallback : this.g) {
            if (str.equals(iDownloadCallback.getName())) {
                return iDownloadCallback;
            }
        }
        return null;
    }

    protected void a(Collection collection) {
        synchronized (this.h) {
            if (collection != null) {
                if (collection.size() > 0) {
                    Map hashMap = new HashMap();
                    for (DownloadInfo downloadInfo : collection) {
                        DownloadInfo downloadInfo2;
                        if (this.updateData.containsKey(Integer.valueOf(downloadInfo.mId))) {
                            downloadInfo2 = (DownloadInfo) this.updateData.get(Integer.valueOf(downloadInfo.mId));
                            if (downloadInfo2 != null) {
                                downloadInfo2.copyFrom(downloadInfo);
                                hashMap.put(Integer.valueOf(downloadInfo2.mId), downloadInfo2);
                            }
                        } else {
                            downloadInfo2 = downloadInfo.clone();
                            if (downloadInfo2 != null) {
                                hashMap.put(Integer.valueOf(downloadInfo.mId), downloadInfo2);
                            }
                        }
                    }
                    this.updateData = hashMap;
                } else {
                    this.updateData.clear();
                }
            }
        }
    }

    public boolean deleteTask(int i) {
        this.f.getContentResolver().delete(ContentUris.withAppendedId(Downloads.a, (long) i), null, null);
        return true;
    }

    public boolean deleteTask(int[] iArr) {
        ContentResolver contentResolver = this.f.getContentResolver();
        String[] strArr = new String[iArr.length];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = String.valueOf(iArr[i]);
        }
        contentResolver.delete(Downloads.a, "_id=?", strArr);
        return true;
    }

    public IDownloadCallback getCallback(String str) {
        if (str == null) {
            return null;
        }
        for (IDownloadCallback iDownloadCallback : this.g) {
            if (str.equals(iDownloadCallback.getName())) {
                return iDownloadCallback;
            }
        }
        return null;
    }

    public boolean isRegistered(String str) {
        for (IDownloadCallback name : this.g) {
            String name2 = name.getName();
            if (name2 != null && name2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public int newTask(String str, String str2, String str3, boolean z, String str4) {
        ContentValues contentValues = new ContentValues();
        if (str3 != null) {
            contentValues.put(Downloads.COLUMN_DATA6, str3);
        }
        if (z) {
            contentValues.put(Downloads.COLUMN_DATA9, "wifi");
        }
        contentValues.put(Downloads.COLUMN_DATA8, str4);
        return a(str, str2, contentValues, null);
    }

    public boolean pauseAllTask() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Downloads.COLUMN_CONTROL, Integer.valueOf(1));
        contentValues.put(SettingsJsonConstants.APP_STATUS_KEY, Integer.valueOf(Downloads.STATUS_RUNNING_PAUSED));
        this.f.getContentResolver().update(Downloads.a, contentValues, "status=? OR status=? OR(status=? AND control<>?)", new String[]{String.valueOf(Downloads.STATUS_RUNNING), String.valueOf(Downloads.STATUS_PENDING), String.valueOf(Downloads.STATUS_RUNNING_PAUSED), String.valueOf(1)});
        return true;
    }

    public boolean pauseTask(int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Downloads.COLUMN_CONTROL, Integer.valueOf(1));
        this.f.getContentResolver().update(ContentUris.withAppendedId(Downloads.a, (long) i), contentValues, null, null);
        return true;
    }

    public boolean queryTask(String str) {
        if (Downloads.a == null) {
            return false;
        }
        Cursor query = this.f.getContentResolver().query(Downloads.a, null, "data_8 = ? ", new String[]{str}, null);
        if (query == null) {
            return false;
        }
        int count = query.getCount();
        query.close();
        return count > 0;
    }

    public void refreshList() {
        Message message = new Message();
        message.what = 2;
        this.d.sendMessage(message);
    }

    public void registerDownloadCallback(IDownloadCallback iDownloadCallback) {
        if (!this.g.contains(iDownloadCallback)) {
            this.g.add(iDownloadCallback);
        }
    }

    public void setDownloadDir(String str) {
        b = str;
    }

    public boolean startTask(int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Downloads.COLUMN_CONTROL, Integer.valueOf(0));
        contentValues.put("numfailed", Integer.valueOf(0));
        this.f.getContentResolver().update(ContentUris.withAppendedId(Downloads.a, (long) i), contentValues, null, null);
        return true;
    }

    public void unregisterDownloadCallback(IDownloadCallback iDownloadCallback) {
        this.g.remove(iDownloadCallback);
    }

    public boolean updateTask(int i, String str, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(str, str2);
        this.f.getContentResolver().update(ContentUris.withAppendedId(Downloads.a, (long) i), contentValues, null, null);
        return true;
    }
}
