package io.realm.internal.android;

import android.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {
    private static Pattern jsonDate;

    static {
        jsonDate = Pattern.compile("/Date\\((\\d*)\\)/");
    }

    public static Date stringToDate(String date) {
        if (date == null || date.length() == 0) {
            return null;
        }
        Matcher matcher = jsonDate.matcher(date);
        if (matcher.matches()) {
            return new Date(Long.parseLong(matcher.group(1)));
        }
        return new Date(Long.parseLong(date));
    }

    public static byte[] stringToBytes(String str) {
        if (str == null || str.length() == 0) {
            return new byte[0];
        }
        return Base64.decode(str, 0);
    }
}
