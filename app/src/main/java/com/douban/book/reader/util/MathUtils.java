package com.douban.book.reader.util;

public class MathUtils {
    public static float roundToRange(float value, float minBound, float maxBound) {
        return Math.min(maxBound, Math.max(value, minBound));
    }

    public static boolean hasBit(int value, int bit) {
        return (value & bit) != 0;
    }

    public static int setBit(int value, int bit) {
        return value | bit;
    }
}
