package com.douban.book.reader.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    public static void unzip(File file) throws IOException, InterruptedException {
        unzipFile(file, file.getParentFile().getAbsolutePath());
        file.delete();
    }

    public static void unzipFile(File fromFile, String toPath) throws IOException, InterruptedException {
        Throwable th;
        ZipInputStream zipInputStream = null;
        try {
            ZipInputStream zipInputStream2 = new ZipInputStream(new BufferedInputStream(new FileInputStream(fromFile)));
            while (true) {
                try {
                    ZipEntry entry = zipInputStream2.getNextEntry();
                    if (entry == null) {
                        IOUtils.closeSilently(zipInputStream2);
                        return;
                    } else if (Thread.currentThread().isInterrupted()) {
                        break;
                    } else if (entry.isDirectory()) {
                        FileUtils.createDir(new File(toPath, entry.getName()));
                    } else {
                        IOUtils.writePartlyStreamToFile(new File(toPath, entry.getName()), zipInputStream2, entry.getSize());
                    }
                } catch (Throwable th2) {
                    th = th2;
                    zipInputStream = zipInputStream2;
                }
            }
            throw new InterruptedException(String.format("Interrupted while unzipping %s", new Object[]{fromFile}));
        } catch (Throwable th3) {
            th = th3;
            IOUtils.closeSilently(zipInputStream);
            throw th;
        }
    }
}
