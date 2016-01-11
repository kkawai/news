package com.initech.news.util;

import android.content.res.Resources;

import com.initech.news.NewsApplication;
import com.initech.news.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Time calculations utilities based on the handy Joda-Time java library
 * (included as a .jar)
 */
public final class TimeUtil {

    private static final String TAG = TimeUtil.class.getSimpleName();

    private TimeUtil() {
    }

    private static final SimpleDateFormat TOUR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public class MonthDay {

        public String mMonth;
        public String mDay;
    }

    public static Date getDateForTourDateString(final String date) throws ParseException {

        final String start = date.replace("T", " ");
        final int index = start.indexOf(".");
        if (index == -1) {
            return TOUR_DATE_FORMAT.parse(start);
        } else {
            return TOUR_DATE_FORMAT.parse(start.substring(0, index));
        }


    }

    public static MonthDay getMonthDayForTourDate(final Date date) throws ParseException {

        final MonthDay ret = new TimeUtil().new MonthDay();
        final DateTime dt = new DateTime(date);
        ret.mMonth = dt.toString("MMM").toUpperCase();
        ret.mDay = dt.dayOfMonth().getAsString();
        return ret;
    }

    public static MonthDay getMonthDayForTourDate(String date) {

        final MonthDay ret = new TimeUtil().new MonthDay();

        String start = date.replace("T", " ");
        final int index = start.indexOf(".");
        start = start.substring(0, index);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;

        try {
            utcDate = format.parse(start);
            DateTime dt = new DateTime(utcDate);
            ret.mMonth = dt.toString("MMM").toUpperCase();
            ret.mDay = dt.dayOfMonth().getAsString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private static final SimpleDateFormat NOW_POST_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        NOW_POST_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String getTimeDiffStringForVevoNow(final String priorTime) {

        // TODO make these strings translatable
        final Resources r = NewsApplication.getInstance().getResources();

        final String MINS_STRING = " " + r.getString(R.string.time_stamp_minutes),
                HR_STRING = " " + r.getString(R.string.time_stamp_hour),
                HRS_STRING = " " + r.getString(R.string.time_stamp_hours),
                DAY_STRING = " " + r.getString(R.string.time_stamp_day),
                DAYS_STRING = " " + r.getString(R.string.time_stamp_days),
                JUST_NOW = r.getString(R.string.time_stamp_just_now);

        String ret = null;

        try {

            final String start = priorTime.replace("T", " ");

            final DateTime dt1 = new DateTime(NOW_POST_DATE_FORMAT.parse(start));
            final DateTime dt2 = new DateTime();

            final int days = Days.daysBetween(dt1.toLocalDate(), dt2.toLocalDate()).getDays();
            if (days > 0)
                return (1 == days) ? days + DAY_STRING : days + DAYS_STRING;

            final int hours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
            if (hours > 0) {
                if (1 == hours)
                    return hours + HR_STRING;
                else
                    return hours + HRS_STRING;
            }

            final int mins = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60;
            if (mins <= 5)
                return JUST_NOW;
            else
                return mins + MINS_STRING;

        } catch (final Exception e) {
            MLog.e(TAG, "", e);
        }

        return ret;
    }

    public static String getShowStartTimeForChannelDisplay(String utcDateTime) {

        if (null == utcDateTime || "" == utcDateTime)
            return null;

        Date date = null;
        String start = utcDateTime.replace("T", " ");
        int index = start.indexOf(".");
        if (index > 0)
            start = start.substring(0, index);

        // create a new Date object using the UTC timezone
        SimpleDateFormat lv_parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        lv_parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = lv_parser.parse(start);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("h:mma");
            format.setTimeZone(TimeZone.getDefault());
            return format.format(date);
        } else {
            return null;
        }
    }

    public static String getFormattedTweetTime(final String dateStr) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        dateFormat.setLenient(true);
        Date created = null;
        try {
            created = dateFormat.parse(dateStr);
        } catch (Exception e) {
            return "";
        }

        // today
        Date today = new Date();

        // how much time since (ms)
        Long duration = today.getTime() - created.getTime();

        int second = 1000;
        int minute = second * 60;
        int hour = minute * 60;
        int day = hour * 24;

        if (duration < (minute * 2)) {
            return "1m";
        }

        if (duration < hour) {
            int n = (int) Math.floor(duration / minute);
            return n + "m";
        }

        if (duration < (hour * 2)) {
            return "1h";
        }

        if (duration < day) {
            int n = (int) Math.floor(duration / hour);
            return n + "h";
        }
        if (duration > day && duration < (day * 2)) {
            return "1d";
        }

        int n = (int) Math.floor(duration / day);
        if (n < 365) {
            return n + "d";
        } else {
            return ">1y";
        }
    }

    /**
     * Returns age in years given birthdate in mm/dd/yyyy or yyyy-mm-dd format
     *
     * @param birthdate - mm/dd/yyyy or yyyy-mm-dd
     * @return - age or -1 if cannot parse given birthdate
     */
    public static int getYearsOld(final String birthdate) {
        try {
            SimpleDateFormat sdf = null;
            if (birthdate.charAt(2) == '/') {
                sdf = new SimpleDateFormat("mm/dd/yyyy");
            } else if (birthdate.charAt(2) == '-') {
                sdf = new SimpleDateFormat("mm-dd-yyyy");
            } else {
                sdf = new SimpleDateFormat("yyyy-mm-dd");
            }
            final Date bd = sdf.parse(birthdate);
            return (int) ((new Date().getTime() - bd.getTime()) / (1000 * 60 * 60 * 24)) / 365;

        } catch (final Exception e) {
            return -1;
        }
    }

    public static int getDaysOld(final String date) {
        try {
            final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            final Date d = formatter.parse(date);

            return (int) ((new Date().getTime() - d.getTime()) / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            return -1;
        }
    }

    public static String convertUTCtoLocalTime(String utcDateTime) {
        try {
            String lv_dateFormateInLocalTimeZone = "";//Will hold the final converted date
            Date lv_localDate = parseUTCTimeString(utcDateTime);
            SimpleDateFormat lv_formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//			Log.d(TAG,"convertUTCtoLocalTime: input string " + utcDateTime);

            //Format UTC time
//			lv_formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//			Log.d(TAG,"convertUTCtoLocalTime: The Date in the UTC time zone(UTC) " + lv_formatter.format(lv_localDate));

            //Convert the UTC date to Local timezone
            lv_formatter.setTimeZone(TimeZone.getDefault());
            lv_dateFormateInLocalTimeZone = lv_formatter.format(lv_localDate);
            MLog.d(TAG, "convertUTCtoLocalTime: The Date in the LocalTime Zone time zone " +
                    lv_formatter.format(lv_localDate));

            return lv_dateFormateInLocalTimeZone;
        } catch (Exception e) {
            return "";
        }
    }

    public static Date parseUTCTimeString(String utcDateTime) {
        //create a new Date object using the UTC timezone
        SimpleDateFormat lv_parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        lv_parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return lv_parser.parse(utcDateTime);
        } catch (Exception e) {
            MLog.e(TAG, "parseUTCTimeString() parse error on" + utcDateTime == null ? "<null>" :
                    utcDateTime);
            return null;
        }
    }

    public static String get12HourTimeString(String utcDateTime) {
        Date date = parseUTCTimeString(utcDateTime);
        return get12HourTimeString(date);
    }

    public static String get12HourTimeString(Date date) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("h:mma");
            format.setTimeZone(TimeZone.getDefault());
            return format.format(date);
        } else {
            return "";
        }
    }

}
