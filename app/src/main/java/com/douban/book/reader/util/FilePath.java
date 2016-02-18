package com.douban.book.reader.util;

import com.douban.book.reader.app.App;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.database.DatabaseHelper;
import com.douban.book.reader.entity.DbCacheEntity;
import com.douban.book.reader.fragment.BaseShareEditFragment;
import java.io.File;
import java.io.IOException;

public class FilePath {
    private static final String VERSION = "1";

    public static File root() {
        return App.get().getExternalFilesDir(VERSION);
    }

    public static File originalRoot() {
        return App.get().getExternalFilesDir(null);
    }

    public static File internalStorageRoot() {
        return App.get().getFilesDir().getParentFile();
    }

    public static File externalStorageRoot() {
        return originalRoot().getParentFile();
    }

    public static File imageCache() {
        return getFile(root(), "images");
    }

    public static File userRoot() {
        return root();
    }

    public static File externalStorageDatabase() {
        return getFile(root(), "databases", DatabaseHelper.DATABASE_NAME);
    }

    public static File internalStorageDatabase() {
        return getFile(internalStorageRoot(), "databases", DatabaseHelper.DATABASE_NAME);
    }

    public static File mapDb() {
        return getFile(userRoot(), "mapdb");
    }

    public static File realmRoot() {
        if (!DebugSwitch.on(Key.APP_DEBUG_SAVE_DATABASE_TO_SD_CARD)) {
            return App.get().getFilesDir();
        }
        return getFile(userRoot(), "realm");
    }

    public static File works(int worksId) {
        return getFile(userRoot(), BaseShareEditFragment.CONTENT_TYPE_WORKS, Integer.valueOf(worksId));
    }

    public static File manifest(int worksId) {
        return getFile(works(worksId), "manifest");
    }

    public static File packRoot(int worksId) {
        return getFile(works(worksId), "pack");
    }

    public static File pack(int worksId, int packageId) {
        return getFile(packRoot(worksId), Integer.valueOf(packageId));
    }

    public static File composite(int worksId) {
        return getFile(works(worksId), "pack", "composite");
    }

    public static File worksCache(int worksId) {
        return getFile(works(worksId), DbCacheEntity.TABLE_NAME);
    }

    public static File worksMapDb(int worksId) {
        return getFile(worksCache(worksId), "m2");
    }

    public static File packCache(int worksId, int packageId) {
        return getFile(worksCache(worksId), Integer.valueOf(packageId));
    }

    public static File packIndexer(int worksId, int packageId) {
        return getFile(packCache(worksId, packageId), "indexer");
    }

    private static File getFile(File root, Object... path) {
        File file = new File(root, StringUtils.join(File.separator, path).toString());
        try {
            FileUtils.createParentDirIfNeeded(file);
        } catch (IOException e) {
        }
        return file;
    }
}
