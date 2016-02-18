package com.igexin.push.core.c;

/* synthetic */ class x {
    static final /* synthetic */ int[] a;

    static {
        a = new int[y.a().length];
        try {
            a[y.NORMAL.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            a[y.BACKUP.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            a[y.TRY_NORMAL.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
    }
}
