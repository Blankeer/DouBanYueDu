package com.douban.book.reader.util;

import io.realm.internal.Table;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static final Date START_OF_ARK_ERA;
    private static final String TAG;
    private static final SimpleDateFormat sDateFormat;
    private static final SimpleDateFormat sDateFormatWithUnit;
    private static final SimpleDateFormat sDateTimeFormat;
    private static final SimpleDateFormat sISO8601Format;
    private static final SimpleDateFormat sYearMonthFormat;

    static {
        TAG = DateUtils.class.getSimpleName();
        sISO8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        sYearMonthFormat = new SimpleDateFormat("yyyy\u5e74MM\u6708");
        sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        sDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sDateFormatWithUnit = new SimpleDateFormat("yyyy\u5e74MM\u6708dd\u65e5");
        START_OF_ARK_ERA = new Date(1336320000000L);
    }

    public static Date parseIso8601(String timeStr) throws ParseException {
        if (StringUtils.isNotEmpty(timeStr)) {
            return sISO8601Format.parse(timeStr);
        }
        return null;
    }

    public static String formatIso8601(long timestamp) {
        return formatIso8601(new Date(timestamp));
    }

    public static String formatIso8601(Date date) {
        return sISO8601Format.format(date);
    }

    public static String formatYearMonth(Date date) {
        String dateString = Table.STRING_DEFAULT_VALUE;
        try {
            dateString = sYearMonthFormat.format(date);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        return dateString;
    }

    public static String formatDate(Date date) {
        String timeString = Table.STRING_DEFAULT_VALUE;
        try {
            timeString = sDateFormat.format(date);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        return timeString;
    }

    public static String formatDateWithUnit(Date date) {
        String dateString = Table.STRING_DEFAULT_VALUE;
        try {
            dateString = sDateFormatWithUnit.format(date);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        return dateString;
    }

    public static String formatDate(long timeStamp) {
        return formatDate(new Date(1000 * timeStamp));
    }

    public static String formatDate(String timeStr) throws ParseException {
        return formatDate(parseIso8601(timeStr));
    }

    public static String formatDateTime(long timeStamp) {
        return formatDateTime(new Date(1000 * timeStamp));
    }

    public static String formatDateTime(Date date) {
        String timeString = Table.STRING_DEFAULT_VALUE;
        try {
            timeString = sDateTimeFormat.format(date);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        return timeString;
    }

    public static long getDaysSince(Date date) {
        if (date == null) {
            return 0;
        }
        return (System.currentTimeMillis() - date.getTime()) / 8640000;
    }

    public static boolean isInRange(Date start, Date end) {
        if (start == null || end == null) {
            return false;
        }
        Date current = new Date();
        if (current.after(start) && current.before(end)) {
            return true;
        }
        return false;
    }

    public static long millisBetween(Date start, Date end) {
        if (start == null || end == null) {
            return Long.MAX_VALUE;
        }
        return end.getTime() - start.getTime();
    }

    public static long millisElapsed(Date date) {
        if (date == null) {
            return Long.MAX_VALUE;
        }
        return millisBetween(date, new Date());
    }
}
