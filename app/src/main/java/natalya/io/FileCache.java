package natalya.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import com.douban.book.reader.entity.DbCacheEntity;
import java.io.File;
import java.util.Set;
import natalya.app.ExternalStorageUtils;
import natalya.codec.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class FileCache {
    public static String SUFFIX;

    private static class DeleteTask extends AsyncTask<Void, Void, Integer> {
        Set<String> black;
        String dir;

        public DeleteTask(String dir, Set<String> bl) {
            this.dir = dir;
            this.black = bl;
        }

        protected Integer doInBackground(Void... params) {
            FileCache.deleteDirFiles(this.dir, this.black);
            return Integer.valueOf(0);
        }
    }

    static {
        SUFFIX = ".json";
    }

    public static void clear(Context context) {
        clear(context, false);
    }

    public static void clear(Context context, boolean clearFile) {
        clear(context, false, null);
    }

    public static void clear(Context context, boolean clearFile, Set<String> blackList) {
        context.getSharedPreferences(DbCacheEntity.TABLE_NAME, 0).edit().clear().commit();
        if (clearFile) {
            new DeleteTask(getFileDir(context), blackList).execute(new Void[0]);
        }
    }

    private static void deleteDirFiles(String path, Set<String> black) {
        Exception e;
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            String[] list = file.list();
            int i = 0;
            while (i < list.length) {
                try {
                    String name;
                    if (path.endsWith(File.separator)) {
                        name = path + list[i];
                    } else {
                        name = path + File.separator + list[i];
                    }
                    File temp = new File(name);
                    File file2;
                    try {
                        if (temp.isFile() && name.endsWith(SUFFIX) && (black == null || !black.contains(list[i]))) {
                            temp.delete();
                        }
                        file2 = temp;
                    } catch (Exception e2) {
                        e = e2;
                        file2 = temp;
                        e.printStackTrace();
                        i++;
                    }
                } catch (Exception e3) {
                    e = e3;
                    e.printStackTrace();
                    i++;
                }
                i++;
            }
        }
    }

    public static String getString(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(DbCacheEntity.TABLE_NAME, 0);
        String k = getFileName(key);
        long expire = pref.getLong(k, -1);
        if (expire > System.currentTimeMillis() / 1000 || expire == 0) {
            try {
                return FileUtils.getStringFromFile(getFileDir(context), k);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONObject get(Context context, String key) {
        return getJSONObject(context, key);
    }

    public static JSONObject getJSONObject(Context context, String key) {
        try {
            return new JSONObject(getString(context, key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray getJSONArray(Context context, String key) {
        try {
            return new JSONArray(getString(context, key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void set(Context context, String key, JSONArray value) {
        set(context, key, value, 0);
    }

    public static void set(Context context, String key, JSONArray value, long expire) {
        set(context, key, value.toString(), expire);
    }

    public static void set(Context context, String key, JSONObject value) {
        set(context, key, value, 0);
    }

    public static void set(Context context, String key, JSONObject value, long expire) {
        set(context, key, value.toString(), expire);
    }

    public static void set(Context context, String key, String value) {
        set(context, key, value, 0);
    }

    public static void set(Context context, String key, String value, long expire) {
        try {
            SharedPreferences pref = context.getSharedPreferences(DbCacheEntity.TABLE_NAME, 0);
            Editor edit = pref.edit();
            String k = getFileName(key);
            if (expire > 0) {
                edit.putLong(k, (System.currentTimeMillis() / 1000) + expire).commit();
            } else if (expire == 0) {
                edit.putLong(k, 0).commit();
            } else if (pref.contains(k)) {
                edit.putLong(k, -1).commit();
            }
            FileUtils.writeStringToFile(value, getFileDir(context), k);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(DbCacheEntity.TABLE_NAME, 0);
        Editor edit = pref.edit();
        String k = getFileName(key);
        if (pref.contains(k)) {
            edit.remove(k).commit();
        }
        String dir = getFileDir(context);
        if (FileUtils.isFileExists(dir, k)) {
            try {
                new File(dir + File.separator + k).delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean has(Context context, String key) {
        return new File(getFileDir(context), getFileName(key)).exists();
    }

    public static String getFileDir(Context context) {
        return ExternalStorageUtils.getCacheDirectory(context, "fileCaches").getAbsolutePath();
    }

    public static String getFileName(String key) {
        return DigestUtils.md5Hex(key) + SUFFIX;
    }
}
