package natalya.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeStr {
    public static final SimpleDateFormat format;
    public static SimpleDateFormat sdf;
    public static SimpleDateFormat sdf2;
    public static SimpleDateFormat sdf3;
    public static SimpleDateFormat sdf4;
    public static SimpleDateFormat sdf5;
    public static SimpleDateFormat sdf6;
    public static SimpleDateFormat sdf7;
    public static SimpleDateFormat sdf8;

    static {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf = new SimpleDateFormat("M\u6708dd\u65e5 HH:mm");
        sdf2 = new SimpleDateFormat("yyyy\u5e74M\u6708dd\u65e5 HH:mm");
        sdf3 = new SimpleDateFormat("HH:mm\nM/dd");
        sdf4 = new SimpleDateFormat("yyyyMMdd");
        sdf5 = new SimpleDateFormat("M\u6708dd\u65e5\uff0cE");
        sdf6 = new SimpleDateFormat("M\u6708dd\u65e5");
        sdf7 = new SimpleDateFormat("yyyy-MM-dd");
        sdf8 = new SimpleDateFormat("MM/dd");
    }

    public static long getTimestamp(String time) {
        try {
            return format.parse(time, new ParsePosition(0)).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getTime(String time) {
        return getTime(getTimestamp(time));
    }

    public static String getTime(long time) {
        long timeSpace = (System.currentTimeMillis() - time) / 1000;
        if (timeSpace < 10) {
            return "\u521a\u521a";
        }
        if (timeSpace < 60) {
            return timeSpace + "\u79d2\u524d";
        }
        if (timeSpace < 3600) {
            return ((int) (timeSpace / 60)) + "\u5206\u949f\u524d";
        }
        if (timeSpace < 86400) {
            return ((int) ((timeSpace / 60) / 60)) + "\u5c0f\u65f6\u524d";
        }
        return sdf.format(new Date(time));
    }

    public static String getFullTime(String time) {
        return getFullTime(getTimestamp(time));
    }

    public static String getFullTime(long time) {
        return sdf2.format(new Date(1000 * time));
    }

    public static String getJavaFullTime(long time) {
        return sdf2.format(Long.valueOf(time));
    }

    public static String getShortTime(long time) {
        return sdf3.format(new Date(1000 * time));
    }

    public static String getToday() {
        return sdf4.format(Long.valueOf(System.currentTimeMillis()));
    }

    public static int getWeekDay() {
        return Calendar.getInstance().get(7);
    }

    public static String getToday1() {
        return sdf5.format(Long.valueOf(System.currentTimeMillis()));
    }

    public static String getToday2() {
        return sdf6.format(Long.valueOf(System.currentTimeMillis()));
    }

    public static String getToday3() {
        return sdf7.format(Long.valueOf(System.currentTimeMillis()));
    }

    public static String getToday4() {
        return sdf7.format(Long.valueOf(System.currentTimeMillis()));
    }
}
