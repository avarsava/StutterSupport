package com.avarsava.stuttersupport;

import android.content.Context;

import java.util.Date;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Utilities for saving and modifying dates in the databases.
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
}
