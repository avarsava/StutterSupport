package com.avarsava.stuttersupport;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.5
 * @since   1.1
 *
 * Utilities for saving and modifying dates in the databases.
 * Format is DD-mm-YYYY
 */

public class DbDate {

    /**
     * Gets the current month and year as text.
     *
     * @return Month and year as String
     */
    public static String getMonthAndYear(Context context) {
        String[] monthNames = new String[]{context.getString(R.string.jan),
                context.getString(R.string.feb),
                context.getString(R.string.mar),
                context.getString(R.string.apr),
                context.getString(R.string.may),
                context.getString(R.string.jun),
                context.getString(R.string.jul),
                context.getString(R.string.aug),
                context.getString(R.string.sep),
                context.getString(R.string.oct),
                context.getString(R.string.nov),
                context.getString(R.string.dec)};
        Date currentDate = new Date();
        int year = currentDate.getYear() + 1900; //Date represents the year as a difference
        int monthnum = currentDate.getMonth();
        String month = monthNames[monthnum];

        return month + " " + year;
    }

    /**
     * Gets the current day, month and year as text.
     *
     * @return Date, Month, and Year as String
     */
    public static String getDayMonthAndYear(Context context){
        Date currentDate = new Date();
        int date = currentDate.getDate();

        return Integer.toString(date) + " " + getMonthAndYear(context);
    }

    /**
     * Gets the current date and formats it for universal acceptance in
     * the app's multiple databases.
     *
     * @return Properly formatted current-date String.
     */
    protected static String getDateString(){
        return getDateString(new Date());
    }

    /**
     * Takes in a Java Date and formats it for universal acceptance in
     * the app's multiple databases.
     *
     * @param date Java Date to format
     * @return Properly formatted date String
     */
    protected static String getDateString(Date date){
        String dateString = (date.getYear() + 1900) + "-"
                + (date.getMonth() + 1) + "-" + date.getDate();
        return dateString;
    }

    /**
     * Erases the time from a Date object, as otherwise two Dates with the same calendar date
     * are not identical in the eyes of Java.
     *
     * @param dateEntry Date to sanitize
     * @return sanitized Date
     */
    protected static Date clearTime(Date dateEntry) {
        long fullTime = dateEntry.getTime();
        long millis = fullTime % 1000;
        Date newDate = new Date(fullTime - millis);
        newDate.setHours(0);
        newDate.setMinutes(0);
        newDate.setSeconds(0);
        return newDate;
    }

    /**
     * Takes a specially formatted String date and calculates whether or not the month in said date
     * is at or past October (month 10).
     *
     * @param strDate String numeric date with dash delimination
     * @return true if month >= 10, false otherwise
     */
    protected static boolean doubleDigitMonth(String strDate) {
        return strDate.charAt(5) == '1' && strDate.charAt(6) != '-';
    }

    /**
     * Takes in a String datestring generated from this class and converts it back to a Java Date
     *
     * @param dateString String to convert to Java Date
     * @return Java Date object, missing time info
     */
    protected static Date getDateFromString(String dateString){
        int date, month, year;

        date = Integer.valueOf(dateString.substring(0, 2));
        if(doubleDigitMonth(dateString)){
            month = Integer.valueOf(dateString.substring(5, 7));
            year = Integer.valueOf(dateString.substring(8,12));
        } else {
            month = Integer.valueOf(dateString.substring(5,6));
            year = Integer.valueOf(dateString.substring(7,11));
        }

        return new Date(year, month, date);
    }

    protected static String getRangeDate(DATE_RANGE range){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -range.getIntValue());
        Date rangeDate = cal.getTime();
        return getDateString(rangeDate);
    }
}
