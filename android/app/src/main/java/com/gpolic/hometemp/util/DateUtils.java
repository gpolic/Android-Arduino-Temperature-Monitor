package com.gpolic.hometemp.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    /**
     * returns the number of days in the specific month
     * @param year
     * @param month
     * @return the number of days in the month
     */
    public static int DaysInTheMonth(int year, int month) {
        // Create a calendar object and set year and month
        Calendar mycal = new GregorianCalendar(year, month, 1);

        // Get the number of days in that month
        return mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 28
    }


    public static int DayInDate(Date date) {
        Calendar mycal = new GregorianCalendar();
        mycal.setTime(date);

        return mycal.get(Calendar.DAY_OF_MONTH);
    }

        /**
         * dateToString converts Date to a String: "Name of day, MM DD - hour:minute"
         *
         * @param date
         * @return format date to a string useful for the UI
         */
    public static String dateToString(@NonNull Date date) {
        if (date == null)
            throw new IllegalArgumentException("date argument is null in dateToString");

//        SimpleDateFormat sdf = new SimpleDateFormat("E, MMM d\nHH:mm");  TODO for testing
        SimpleDateFormat sdf = new SimpleDateFormat("E, MMM d - HH:mm");
        return sdf.format(date);
    }

    /**
     * getDB DateString converts Date to a String: "YEAR-MM-DD  HH:MM:SS"
     *
     * @param date
     * @return string formatted date for use in a database query
     */
    public static String dateToDBString(@NonNull Date date) {
        if (date == null)
            throw new IllegalArgumentException("date argument is null in dateToDBString");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * stringToDateTime
     * converts a string of "YYYY-MM-DD HH:MM:SS" to a Date object
     *
     * @param stringDate string with date
     * @return Date object
     */
    public static Date stringToDateTime(@NonNull String stringDate) {
        if (TextUtils.isEmpty(stringDate))
            throw new IllegalArgumentException("stringDate argument is empty in stringToDateTime");

        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            date = simpleDateFormat.parse(stringDate);
        } catch (ParseException parseEx) {
            parseEx.printStackTrace();
        }
        return date;
    }


    /**
     * stringToDateTime
     * converts a string of "YYYY-MM-DD HH:MM:SS" to a Date object
     *
     * @param stringDate string with date
     * @return Date object
     */
    public static Date stringToDate(@NonNull String stringDate) {
        if (TextUtils.isEmpty(stringDate))
            throw new IllegalArgumentException("stringDate argument is empty in stringToDateTime");

        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date = simpleDateFormat.parse(stringDate);
        } catch (ParseException parseEx) {
            parseEx.printStackTrace();
        }
        return date;
    }


    public static Date removeTime(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}