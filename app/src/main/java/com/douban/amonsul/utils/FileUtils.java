package com.douban.amonsul.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import com.douban.amonsul.MobileStat;
import com.douban.amonsul.StatLogger;
import com.douban.amonsul.StatUtils;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.sina.weibo.sdk.component.ShareRequestParam;
import com.umeng.analytics.a;
import io.fabric.sdk.android.services.network.HttpRequest;
import io.realm.internal.Table;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import org.apache.http.util.EncodingUtils;

public class FileUtils {
    private static final String TAG;

    static {
        TAG = FileUtils.class.getSimpleName();
    }

    public static boolean renameFile(Context context, String originFile, String desFile) {
        try {
            context.getFileStreamPath(originFile).renameTo(new File(context.getFilesDir(), desFile));
            StatLogger.d(TAG, " rename file " + originFile + " to " + desFile);
            return true;
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static boolean removeFile(Context context, String fileName, boolean isSavedSd) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        if (!isSavedSd) {
            return context.deleteFile(fileName);
        }
        if (!StatUtils.hasPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") || !Environment.getExternalStorageState().equals("mounted")) {
            return false;
        }
        File file = getSDMobileStatFile(context, fileName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static String saveDataToSD(Context context, String fileName, byte[] data) {
        if (TextUtils.isEmpty(fileName)) {
            return Table.STRING_DEFAULT_VALUE;
        }
        File file = getSDMobileStatFile(context, fileName);
        if (file == null) {
            try {
                return Table.STRING_DEFAULT_VALUE;
            } catch (Exception e) {
                if (MobileStat.DEBUG) {
                    e.printStackTrace();
                }
            }
        } else {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        }
    }

    public static boolean saveDataToLocalFile(Context context, String filePath, String content, boolean append) {
        Exception e;
        Throwable th;
        boolean z = false;
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        String separator = System.getProperty("line.separator");
        if (!append) {
            removeFile(context, filePath, false);
        }
        try {
            fos = context.openFileOutput(filePath, LruDiskCache.DEFAULT_BUFFER_SIZE);
            OutputStreamWriter osw2 = new OutputStreamWriter(fos);
            try {
                osw2.append(content);
                osw2.append(separator);
                z = true;
                if (osw2 != null) {
                    try {
                        osw2.flush();
                        osw2.close();
                    } catch (Exception e2) {
                        if (MobileStat.DEBUG) {
                            e2.printStackTrace();
                        }
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e22) {
                        if (MobileStat.DEBUG) {
                            e22.printStackTrace();
                        }
                    }
                }
                osw = osw2;
            } catch (Exception e3) {
                e22 = e3;
                osw = osw2;
                try {
                    if (MobileStat.DEBUG) {
                        e22.printStackTrace();
                    }
                    if (osw != null) {
                        try {
                            osw.flush();
                            osw.close();
                        } catch (Exception e222) {
                            if (MobileStat.DEBUG) {
                                e222.printStackTrace();
                            }
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (Exception e2222) {
                            if (MobileStat.DEBUG) {
                                e2222.printStackTrace();
                            }
                        }
                    }
                    return z;
                } catch (Throwable th2) {
                    th = th2;
                    if (osw != null) {
                        try {
                            osw.flush();
                            osw.close();
                        } catch (Exception e22222) {
                            if (MobileStat.DEBUG) {
                                e22222.printStackTrace();
                            }
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (Exception e222222) {
                            if (MobileStat.DEBUG) {
                                e222222.printStackTrace();
                            }
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                osw = osw2;
                if (osw != null) {
                    osw.flush();
                    osw.close();
                }
                if (fos != null) {
                    fos.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e222222 = e4;
            if (MobileStat.DEBUG) {
                e222222.printStackTrace();
            }
            if (osw != null) {
                osw.flush();
                osw.close();
            }
            if (fos != null) {
                fos.close();
            }
            return z;
        }
        return z;
    }

    public static boolean saveDataToLocalFile(Context context, String filePath, byte[] data) {
        Exception e;
        Throwable th;
        boolean z = false;
        BufferedOutputStream out = null;
        try {
            BufferedOutputStream out2 = new BufferedOutputStream(context.openFileOutput(filePath, 0));
            try {
                out2.write(data);
                out2.flush();
                out2.close();
                z = true;
                if (out2 != null) {
                    try {
                        out2.close();
                    } catch (Exception e2) {
                        if (MobileStat.DEBUG) {
                            e2.printStackTrace();
                        }
                    }
                }
                out = out2;
            } catch (Exception e3) {
                e2 = e3;
                out = out2;
                try {
                    if (MobileStat.DEBUG) {
                        e2.printStackTrace();
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e22) {
                            if (MobileStat.DEBUG) {
                                e22.printStackTrace();
                            }
                        }
                    }
                    return z;
                } catch (Throwable th2) {
                    th = th2;
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e222) {
                            if (MobileStat.DEBUG) {
                                e222.printStackTrace();
                            }
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                out = out2;
                if (out != null) {
                    out.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e222 = e4;
            if (MobileStat.DEBUG) {
                e222.printStackTrace();
            }
            if (out != null) {
                out.close();
            }
            return z;
        }
        return z;
    }

    public static File getSDMobileStatFile(Context context, String fileName) {
        File file = new File(getMobileStatDir(context), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                if (MobileStat.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    public static byte[] getFileData(Context context, String fileName, boolean sdcard) {
        if (!sdcard) {
            return getLocalFileData(context, fileName);
        }
        File file = getSDMobileStatFile(context, fileName);
        try {
            byte[] bytes = new byte[((int) file.length())];
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            return bytes;
        } catch (Exception e) {
            if (MobileStat.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static byte[] getLocalFileData(Context context, String fileName) {
        String res = Table.STRING_DEFAULT_VALUE;
        FileInputStream fin = null;
        try {
            fin = context.openFileInput(fileName);
            byte[] buffer = new byte[fin.available()];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, HttpRequest.CHARSET_UTF8);
            fin.close();
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e) {
                    if (MobileStat.DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e2) {
            if (MobileStat.DEBUG) {
                e2.printStackTrace();
            }
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e22) {
                    if (MobileStat.DEBUG) {
                        e22.printStackTrace();
                    }
                }
            }
        } catch (Throwable th) {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e222) {
                    if (MobileStat.DEBUG) {
                        e222.printStackTrace();
                    }
                }
            }
        }
        return res.getBytes();
    }

    public static File getMobileStatDir(Context context) {
        File dir = new File(getFileDirectory(context), "amonsul");
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                if (MobileStat.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        return dir;
    }

    public static File getFileDirectory(Context context) {
        File appFileDir = null;
        if (StatUtils.hasPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") && Environment.getExternalStorageState().equals("mounted")) {
            appFileDir = getExternalDir(context);
        }
        if (appFileDir == null) {
            return context.getFilesDir();
        }
        return appFileDir;
    }

    private static File getExternalDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), a.b), ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA);
        File appDir = new File(new File(dataDir, context.getPackageName()), Keys.file);
        if (appDir.exists()) {
            return appDir;
        }
        if (!appDir.mkdirs()) {
            return null;
        }
        try {
            new File(dataDir, ".nomedia").createNewFile();
            return appDir;
        } catch (Throwable e) {
            StatLogger.e(TAG, e);
            return appDir;
        }
    }
}
