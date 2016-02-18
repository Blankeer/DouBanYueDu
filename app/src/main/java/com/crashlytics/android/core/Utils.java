package com.crashlytics.android.core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

final class Utils {
    private Utils() {
    }

    public static void capFileCount(File directory, FilenameFilter filter, int maxAllowed, Comparator<File> fileComparator) {
        File[] sessionFiles = directory.listFiles(filter);
        if (sessionFiles != null && sessionFiles.length > maxAllowed) {
            Arrays.sort(sessionFiles, fileComparator);
            int i = sessionFiles.length;
            File[] arr$ = sessionFiles;
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                File file = arr$[i$];
                if (i > maxAllowed) {
                    file.delete();
                    i--;
                    i$++;
                } else {
                    return;
                }
            }
        }
    }
}
