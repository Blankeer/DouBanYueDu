package com.douban.book.reader.util;

import java.text.BreakIterator;

public class BreakIteratorUtils {
    public static final String TAG = "BreakIteratorUtils";
    private static final BreakIterator[] sLineIteratorCache;
    private static final BreakIterator[] sWordIteratorCache;

    static {
        sWordIteratorCache = new BreakIterator[3];
        sLineIteratorCache = new BreakIterator[3];
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.text.BreakIterator getWordBreakIterator() {
        /*
        r3 = sWordIteratorCache;
        monitor-enter(r3);
        r0 = 0;
    L_0x0004:
        r2 = sWordIteratorCache;	 Catch:{ all -> 0x0025 }
        r2 = r2.length;	 Catch:{ all -> 0x0025 }
        if (r0 >= r2) goto L_0x001d;
    L_0x0009:
        r2 = sWordIteratorCache;	 Catch:{ all -> 0x0025 }
        r2 = r2[r0];	 Catch:{ all -> 0x0025 }
        if (r2 == 0) goto L_0x001a;
    L_0x000f:
        r2 = sWordIteratorCache;	 Catch:{ all -> 0x0025 }
        r1 = r2[r0];	 Catch:{ all -> 0x0025 }
        r2 = sWordIteratorCache;	 Catch:{ all -> 0x0025 }
        r4 = 0;
        r2[r0] = r4;	 Catch:{ all -> 0x0025 }
        monitor-exit(r3);	 Catch:{ all -> 0x0025 }
    L_0x0019:
        return r1;
    L_0x001a:
        r0 = r0 + 1;
        goto L_0x0004;
    L_0x001d:
        monitor-exit(r3);	 Catch:{ all -> 0x0025 }
        r2 = java.util.Locale.CHINA;
        r1 = java.text.BreakIterator.getWordInstance(r2);
        goto L_0x0019;
    L_0x0025:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0025 }
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.util.BreakIteratorUtils.getWordBreakIterator():java.text.BreakIterator");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void recycleWordBreakIterator(java.text.BreakIterator r3) {
        /*
        r2 = sWordIteratorCache;
        monitor-enter(r2);
        r0 = 0;
    L_0x0004:
        r1 = sWordIteratorCache;	 Catch:{ all -> 0x0018 }
        r1 = r1.length;	 Catch:{ all -> 0x0018 }
        if (r0 >= r1) goto L_0x0013;
    L_0x0009:
        r1 = sWordIteratorCache;	 Catch:{ all -> 0x0018 }
        r1 = r1[r0];	 Catch:{ all -> 0x0018 }
        if (r1 != 0) goto L_0x0015;
    L_0x000f:
        r1 = sWordIteratorCache;	 Catch:{ all -> 0x0018 }
        r1[r0] = r3;	 Catch:{ all -> 0x0018 }
    L_0x0013:
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        return;
    L_0x0015:
        r0 = r0 + 1;
        goto L_0x0004;
    L_0x0018:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.util.BreakIteratorUtils.recycleWordBreakIterator(java.text.BreakIterator):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.text.BreakIterator getLineBreakIterator() {
        /*
        r3 = sLineIteratorCache;
        monitor-enter(r3);
        r0 = 0;
    L_0x0004:
        r2 = sLineIteratorCache;	 Catch:{ all -> 0x0025 }
        r2 = r2.length;	 Catch:{ all -> 0x0025 }
        if (r0 >= r2) goto L_0x001d;
    L_0x0009:
        r2 = sLineIteratorCache;	 Catch:{ all -> 0x0025 }
        r2 = r2[r0];	 Catch:{ all -> 0x0025 }
        if (r2 == 0) goto L_0x001a;
    L_0x000f:
        r2 = sLineIteratorCache;	 Catch:{ all -> 0x0025 }
        r1 = r2[r0];	 Catch:{ all -> 0x0025 }
        r2 = sLineIteratorCache;	 Catch:{ all -> 0x0025 }
        r4 = 0;
        r2[r0] = r4;	 Catch:{ all -> 0x0025 }
        monitor-exit(r3);	 Catch:{ all -> 0x0025 }
    L_0x0019:
        return r1;
    L_0x001a:
        r0 = r0 + 1;
        goto L_0x0004;
    L_0x001d:
        monitor-exit(r3);	 Catch:{ all -> 0x0025 }
        r2 = java.util.Locale.CHINA;
        r1 = java.text.BreakIterator.getLineInstance(r2);
        goto L_0x0019;
    L_0x0025:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0025 }
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.util.BreakIteratorUtils.getLineBreakIterator():java.text.BreakIterator");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void recycleLineBreakIterator(java.text.BreakIterator r3) {
        /*
        r2 = sLineIteratorCache;
        monitor-enter(r2);
        r0 = 0;
    L_0x0004:
        r1 = sLineIteratorCache;	 Catch:{ all -> 0x0018 }
        r1 = r1.length;	 Catch:{ all -> 0x0018 }
        if (r0 >= r1) goto L_0x0013;
    L_0x0009:
        r1 = sLineIteratorCache;	 Catch:{ all -> 0x0018 }
        r1 = r1[r0];	 Catch:{ all -> 0x0018 }
        if (r1 != 0) goto L_0x0015;
    L_0x000f:
        r1 = sLineIteratorCache;	 Catch:{ all -> 0x0018 }
        r1[r0] = r3;	 Catch:{ all -> 0x0018 }
    L_0x0013:
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        return;
    L_0x0015:
        r0 = r0 + 1;
        goto L_0x0004;
    L_0x0018:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.douban.book.reader.util.BreakIteratorUtils.recycleLineBreakIterator(java.text.BreakIterator):void");
    }
}
