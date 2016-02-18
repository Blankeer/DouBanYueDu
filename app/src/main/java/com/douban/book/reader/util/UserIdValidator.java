package com.douban.book.reader.util;

import io.realm.internal.Table;
import java.util.regex.Pattern;

public class UserIdValidator {
    private static final Pattern EMAIL_PATTERN;
    private static final Pattern PHONE_PATTERN;

    static {
        EMAIL_PATTERN = Pattern.compile("^[_\\.0-9a-zA-Z+-]+@([0-9a-zA-Z]+[0-9a-zA-Z-]*\\.)+[a-zA-Z]{2,}$");
        PHONE_PATTERN = Pattern.compile("^[0-9]+$");
    }

    public static CharSequence normalizeInput(CharSequence input) {
        if (StringUtils.isEmpty(input)) {
            return Table.STRING_DEFAULT_VALUE;
        }
        return input.toString().trim().replace("\uff20", "@");
    }

    public static boolean looksLikeAnEmail(CharSequence input) {
        CharSequence normalized = normalizeInput(input);
        if (StringUtils.isEmpty(normalized)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(normalized).matches();
    }

    public static boolean looksLikeAPhoneNum(CharSequence input) {
        CharSequence normalized = normalizeInput(input);
        if (StringUtils.isEmpty(normalized)) {
            return false;
        }
        return PHONE_PATTERN.matcher(normalized).matches();
    }

    public static boolean looksLikeValidUid(CharSequence input) {
        return looksLikeAnEmail(input) || looksLikeAPhoneNum(input);
    }
}
