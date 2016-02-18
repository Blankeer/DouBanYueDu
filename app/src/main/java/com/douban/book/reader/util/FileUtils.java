package com.douban.book.reader.util;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.alipay.security.mobile.module.commonutils.constants.a;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.douban.amonsul.StatConstant;
import com.douban.book.reader.constant.Char;
import com.douban.book.reader.helper.AppUri;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import io.realm.internal.Table;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class FileUtils {
    private static final String TAG;

    static {
        TAG = FileUtils.class.getSimpleName();
    }

    public static void removeFilesRecursively(File folderToSearch, String[] fileNamesToRemove) {
        File[] files = folderToSearch.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.exists()) {
                    if (file.isDirectory()) {
                        removeFilesRecursively(file, fileNamesToRemove);
                    } else if (Arrays.binarySearch(fileNamesToRemove, file.getName()) >= 0) {
                        file.delete();
                        Logger.d(TAG, "deleting " + file.getPath() + file.getName(), new Object[0]);
                    }
                }
            }
        }
    }

    public static boolean clearDir(File dir) {
        if (!dir.isDirectory()) {
            return true;
        }
        String[] children = dir.list();
        if (children == null) {
            return true;
        }
        for (String file : children) {
            if (!deleteDir(new File(dir, file))) {
                return false;
            }
        }
        return true;
    }

    public static boolean deleteDir(File dir) {
        if (!clearDir(dir)) {
            return false;
        }
        File fileToRemove = new File(dir.getParentFile(), String.valueOf(System.currentTimeMillis()));
        if (dir.renameTo(fileToRemove)) {
            return fileToRemove.delete();
        }
        return dir.delete();
    }

    public static void createParentDirIfNeeded(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null) {
            createDir(parent);
        }
    }

    public static void createDir(File dir) throws IOException {
        if (dir == null) {
            throw new IllegalArgumentException("dir cannot be null");
        } else if ((!dir.exists() || !dir.isDirectory()) && !dir.mkdirs()) {
            throw new IOException(String.format("Failed to create dir %s", new Object[]{dir.getName()}));
        }
    }

    public static File ensureFolder(File file) throws IOException {
        createDir(file);
        return file;
    }

    public static void copy(File copyFrom, File copyTo) throws IOException {
        Throwable th;
        String copyToPath = copyTo.getPath();
        if (copyTo.exists()) {
            backup(copyTo);
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            OutputStream out2;
            InputStream in2 = new FileInputStream(copyFrom);
            try {
                out2 = new FileOutputStream(copyToPath);
            } catch (Throwable th2) {
                th = th2;
                in = in2;
                IOUtils.closeSilently(in);
                IOUtils.closeSilently(out);
                throw th;
            }
            try {
                IOUtils.copyStream(in2, out2);
                IOUtils.closeSilently(in2);
                IOUtils.closeSilently(out2);
            } catch (Throwable th3) {
                th = th3;
                out = out2;
                in = in2;
                IOUtils.closeSilently(in);
                IOUtils.closeSilently(out);
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            IOUtils.closeSilently(in);
            IOUtils.closeSilently(out);
            throw th;
        }
    }

    @Nullable
    public static File backup(File file) {
        File backupFile = new File(file.getParent(), String.format("%s_%s.%s", new Object[]{getFileNameWithoutExtension(file), Long.valueOf(System.currentTimeMillis()), getExtension(file)}));
        return file.renameTo(backupFile) ? backupFile : null;
    }

    public static String getExtension(File file) {
        if (file.isDirectory()) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return StringUtils.lastSegment(file.getName(), Char.DOT);
    }

    public static String getFileNameWithoutExtension(File file) {
        if (file.isDirectory()) {
            return file.getName();
        }
        return StringUtils.removeLastSegment(file.getName(), Char.DOT);
    }

    public static String normalizeFilename(String name) {
        if (StringUtils.isEmpty(name)) {
            return EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR;
        }
        return name.replace(Char.SLASH, Char.UNDERLINE);
    }

    public static String formatExternalRelativePath(File file) {
        return formatRelativePath(file, FilePath.root());
    }

    public static String formatRelativePath(File file, File base) {
        return file.getAbsolutePath().replace(base.getAbsolutePath(), Table.STRING_DEFAULT_VALUE);
    }

    @TargetApi(19)
    public static void logFileStatInfo() {
        boolean z = true;
        if (VERSION.SDK_INT >= 19) {
            boolean z2;
            StatFs statInternal = new StatFs(FilePath.internalStorageRoot().getPath());
            StatFs statExternal = new StatFs(FilePath.originalRoot().getPath());
            Answers instance = Answers.getInstance();
            CustomEvent customEvent = (CustomEvent) ((CustomEvent) ((CustomEvent) ((CustomEvent) ((CustomEvent) ((CustomEvent) ((CustomEvent) ((CustomEvent) new CustomEvent("StorageStat").putCustomAttribute("isEmulated", String.valueOf(Environment.isExternalStorageEmulated()))).putCustomAttribute("isRemovable", String.valueOf(Environment.isExternalStorageRemovable()))).putCustomAttribute("available (internal)", getFileSizeRank(statInternal.getAvailableBytes()))).putCustomAttribute("total (internal)", getFileSizeRank(statInternal.getTotalBytes()))).putCustomAttribute("free (internal)", getFileSizeRank(statInternal.getFreeBytes()))).putCustomAttribute("available (external)", getFileSizeRank(statExternal.getAvailableBytes()))).putCustomAttribute("total (external)", getFileSizeRank(statExternal.getTotalBytes()))).putCustomAttribute("free (external)", getFileSizeRank(statExternal.getFreeBytes()));
            String str = "total (internal == external)";
            if (statInternal.getTotalBytes() == statExternal.getTotalBytes()) {
                z2 = true;
            } else {
                z2 = false;
            }
            customEvent = (CustomEvent) customEvent.putCustomAttribute(str, String.valueOf(z2));
            String str2 = "available (internal == external)";
            if (statInternal.getAvailableBytes() != statExternal.getAvailableBytes()) {
                z = false;
            }
            instance.logCustom((CustomEvent) customEvent.putCustomAttribute(str2, String.valueOf(z)));
        }
    }

    private static String getFileSizeRank(long sizeInByte) {
        int sizeInMega = Math.round((float) (sizeInByte / 1048576));
        if (sizeInMega < 10) {
            return "0-10MB";
        }
        if (sizeInMega < 100) {
            return "10-100MB";
        }
        if (sizeInMega < AppUri.READER) {
            return "100-300MB";
        }
        if (sizeInMega < StatConstant.DEFAULT_MAX_EVENT_COUNT) {
            return "300-500MB";
        }
        if (sizeInMega < AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT) {
            return "500MB-1GB";
        }
        if (sizeInMega < 3072) {
            return "1-3GB";
        }
        if (sizeInMega < 10240) {
            return "3-10GB";
        }
        if (sizeInMega < a.a) {
            return "10-50GB";
        }
        if (sizeInMega < 102400) {
            return "50-100GB";
        }
        return ">100GB";
    }
}
