package com.douban.book.reader.util;

import java.util.Arrays;
import java.util.List;

public class IterableUtils {
    public static <T> boolean containsAny(Iterable<T> src, T... items) {
        if (src == null) {
            return false;
        }
        List<T> candidates = Arrays.asList(items);
        for (T item : src) {
            if (candidates.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean containsAll(Iterable<T> src, T... items) {
        if (src == null) {
            return false;
        }
        List<T> candidates = Arrays.asList(items);
        for (T item : src) {
            if (candidates.contains(item)) {
                candidates.remove(item);
            }
            if (candidates.isEmpty()) {
                return true;
            }
        }
        return candidates.isEmpty();
    }

    public static <T> boolean containsNone(Iterable<T> src, T... items) {
        if (src == null) {
            return true;
        }
        List<T> candidates = Arrays.asList(items);
        for (T item : src) {
            if (candidates.contains(item)) {
                return false;
            }
        }
        return true;
    }
}
