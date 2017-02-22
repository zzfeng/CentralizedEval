package com.sundyn.centralizedeval.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/2/21.
 * 日期工具类
 */

public class DateUtil {
    public static final String TAG = DateUtil.class.getSimpleName();

    /**
     * 日期类型 *
     */
    public static final String yyyyMMDD = "yyyy-MM-dd";

    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String HHmmss = "HH:mm:ss";
    public static final String hhmmss = "hh:mm:ss";
    public static final String LOCALE_DATE_FORMAT = "yyyy年M月d日 HH:mm:ss";
    public static final String DB_DATA_FORMAT = "yyyy-MM-DD HH:mm:ss";
    public static final String NEWS_ITEM_DATE_FORMAT = "hh:mm M月d日 yyyy";
    private static final ThreadLocal<Formats> local = new ThreadLocal<Formats>();

    private static final Formats getFormats() {
        Formats f = (Formats) local.get();
        if (f == null) {
            f = new Formats();
            local.set(f);
        }
        return f;
    }
    public static String dateToString(Date date, String pattern) throws Exception {
        return new SimpleDateFormat(pattern).format(date);
    }
    public static final String simpleFormat(Date date) {
        if (date == null) {
            return "";
        }

        return getFormats().simple.format(date);
    }

    public static final Date gmtFormatStringDate(String strDate) {
        try {
            return getFormats().gmtFormat.parse(strDate);
        } catch (ParseException e) {
        }
        return null;
    }

    public static final String dtSimpleFormat(Date date) {
        if (date == null) {
            return "";
        }

        return getFormats().dtSimple.format(date);
    }

    public static final String dtSimpleChineseFormat(Date date) {
        if (date == null) {
            return "";
        }
        return getFormats().dtSimpleChinese.format(date);
    }

    public static final String frontFullChineseDate(Date date) {
        try {
            return getFormats().dtFullChinese.format(date);
        } catch (Exception e) {
        }
        return "";
    }

    public static final String frontFullChineseDate() {
        try {
            return getFormats().dtFullChinese.format(Calendar.getInstance()
                    .getTime());
        } catch (Exception e) {
        }
        return "";
    }

    public static final Date string2Date(String stringDate) {
        try {
            if (stringDate == null) {
                return null;
            }
            return getFormats().dtSimple.parse(stringDate);
        } catch (Exception localException) {
        }
        return null;
    }

    public static final Date string2DateTime(String stringDate)
            throws ParseException {
        if (stringDate == null) {
            return null;
        }

        return getFormats().simple.parse(stringDate);
    }

    public static final Date chineseString2Date(String stringDate) {
        if (stringDate == null)
            return null;
        try {
            return getFormats().dtSimpleChinese.parse(stringDate);
        } catch (ParseException localParseException) {
        }
        return null;
    }

    public static final String shortDate(Date Date) {
        if (Date == null) {
            return null;
        }

        return getFormats().dtShort.format(Date);
    }

    public static final Long string2DateLong(String stringDate)
            throws ParseException {
        Date d = string2Date(stringDate);

        if (d == null) {
            return null;
        }

        return new Long(d.getTime());
    }

    public static final String hmsFormat(Date date) {
        if (date == null) {
            return "";
        }

        return getFormats().hmsFormat.format(date);
    }

    public static final String simpleDate(Date date) {
        if (date == null) {
            return "";
        }

        return getFormats().simpleFormat.format(date);
    }

    public static final String getDiffDate(int diff) {
        Calendar c = Calendar.getInstance();

        c.setTime(new Date());
        c.add(Calendar.DATE, diff);
        return dtSimpleFormat(c.getTime());
    }

    public static final Date getDiffDateFromEnterDate(Date enterDate, int diff) {
        Calendar c = Calendar.getInstance();

        c.setTime(enterDate);
        c.add(Calendar.DATE, diff);
        return c.getTime();
    }

    public static final Date getDiffCurrentDate(int diff) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, diff);
        return c.getTime();
    }

    public static final Date getMonthFirstDay(Date dt, int mon) {
        if (dt == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.MONTH, mon);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return string2Date(dtSimpleFormat(c.getTime()));
    }

    public static final String getYearMon(Date dt, int mon) {
        if (dt == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.MONTH, mon);
        String year = String.valueOf(c.get(Calendar.YEAR));
        int month = c.get(Calendar.MONTH) + 1;
        String sMonth;
        if (month < 10)
            sMonth = "0" + String.valueOf(month);
        else {
            sMonth = String.valueOf(month);
        }
        return year + "." + sMonth;
    }

    public static final String getYearMonWithoutDot(Date dt, int mon) {
        if (dt == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.MONTH, mon);
        String year = String.valueOf(c.get(Calendar.YEAR));
        int month = c.get(Calendar.MONTH) + 1;
        String sMonth;
        if (month < 10)
            sMonth = "0" + String.valueOf(month);
        else {
            sMonth = String.valueOf(month);
        }
        return year + sMonth;
    }

    public static final String getCurrentMon() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        String sMonth;
        if (month < 10)
            sMonth = "0" + String.valueOf(month);
        else {
            sMonth = String.valueOf(month);
        }
        return sMonth;
    }

    public static String getFirstDayOfWeek(Date dt) {
        String strTemp = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayofweek == 0)
            dayofweek = 7;
        cal.add(Calendar.DAY_OF_MONTH, -dayofweek + 1);
        strTemp = cal.get(Calendar.YEAR) + "-";
        if (cal.get(Calendar.MONTH) + 1 < 10)
            strTemp = strTemp + "0";
        strTemp = strTemp + (cal.get(Calendar.MONTH) + 1) + "-";
        if (cal.get(Calendar.DAY_OF_MONTH) < 10)
            strTemp = strTemp + "0";
        strTemp = strTemp + cal.get(Calendar.DAY_OF_MONTH);
        return strTemp;
    }

    public static int getBetweenDate(Date start, Date endDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        int end = c.get(Calendar.YEAR) * 365 + c.get(Calendar.DAY_OF_YEAR);
        c.setTime(start);
        return end - (c.get(Calendar.YEAR) * 365 + c.get(Calendar.DAY_OF_YEAR));
    }

    public static final String getDiffDate(Date dt, int idiff) {
        Calendar c = Calendar.getInstance();

        c.setTime(dt);
        c.add(Calendar.DATE, idiff);
        return dtSimpleFormat(c.getTime());
    }

    public static Date getDelayTime(Date start, int delayMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.MINUTE, delayMinutes);
        return cal.getTime();
    }

    public static final String getDiffMon(Date dt, int idiff) {
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.MONTH, idiff);
        return dtSimpleFormat(c.getTime());
    }

    public static String getYear() {
        return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    }

    public static Date getDayBegin(Date date) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setLenient(false);

        String dateString = df.format(date);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
        }
        return date;
    }

    public static String dateDifString(String arg1, Date arg2) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date date = df.parse(arg1);
            long l = arg2.getTime() - date.getTime();
            long day = l / 86400000L;
            long hour = l / 3600000L - day * 24L;
            long min = l / 60000L - day * 24L * 60L - hour * 60L;
            long s = l / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min
                    * 60L;
            return day + "天" + hour + "小时" + min + "分" + s + "秒";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long dateDif(String arg1, Date arg2) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date date = df.parse(arg1);
            long l = arg2.getTime() - date.getTime();
            long day = l / 86400000L;
            long hour = l / 3600000L - day * 24L;
            long min = l / 60000L - day * 24L * 60L - hour * 60L;
            long s = l / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min
                    * 60L;
            System.out.println(day + "天" + hour + "小时" + min + "分" + s + "秒");
            return day * 24L + hour;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

//	public static void main(String[] argus) {
//		System.out.println(dtSimpleChineseFormat(new Date()));
//	}

    private static class Formats {
        private final DateFormat simple = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        private final DateFormat dtSimple = new SimpleDateFormat("yyyy-MM-dd");

        private final DateFormat dtSimpleChinese = new SimpleDateFormat(
                "yyyy年MM月dd日");

        private final DateFormat dtFullChinese = new SimpleDateFormat(
                "yyyy年MM月dd日 HH时mm分");

        private final DateFormat dtShort = new SimpleDateFormat("yyyyMMdd");

        private final DateFormat hmsFormat = new SimpleDateFormat("HH:mm:ss");

        private final DateFormat simpleFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm");

        private final DateFormat gmtFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
    }

}
