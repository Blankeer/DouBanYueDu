package com.douban.book.reader.util;

public class WorksIdentity {
    public static final int ID_BIT_BOOK_NOT_IN_USE = 2;
    public static final int ID_BIT_CHAPTER = 64;
    public static final int ID_BIT_COLUMN_WORKS = 128;
    public static final int ID_BIT_COMPETITION = 32;
    public static final int ID_BIT_FICTION = 4;
    public static final int ID_BIT_FINALIZE = 256;
    public static final int ID_BIT_GALLERY = 16;
    public static final int ID_BIT_ORIGINAL = 1;
    public static final int ID_BIT_SERIAL = 8;

    public static boolean isGallery(int identities) {
        return hasBit(identities, ID_BIT_GALLERY);
    }

    public static boolean isColumnOrSerial(int identities) {
        return hasBit(identities, ID_BIT_COLUMN_WORKS) || isSerial(identities);
    }

    public static boolean isSerial(int identities) {
        return hasBit(identities, ID_BIT_SERIAL);
    }

    public static boolean isCompleted(int identities) {
        return hasBit(identities, ID_BIT_FINALIZE);
    }

    public static boolean isUncompletedColumnOrSerial(int identities) {
        return isColumnOrSerial(identities) && !isCompleted(identities);
    }

    private static boolean hasBit(int identities, int bit) {
        return (identities & bit) != 0;
    }
}
