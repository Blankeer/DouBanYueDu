package com.douban.book.reader.util;

public class ViewStateUtils {
    public static boolean isSelectedOrPressed(int[] states) {
        for (int state : states) {
            if (state == 16842913 || state == 16842919) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDisabled(int[] states) {
        for (int state : states) {
            if (state == 16842910) {
                return false;
            }
        }
        return true;
    }
}
