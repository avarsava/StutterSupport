package com.example.myself.stuttersupport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Eases access to database containing dates on which at least one activity was successfully
 * completed. This database is used for tracking the activity of the user and for calculating the
 * current and best streak scores.
 * Based off DbHelper.java from Learning Android by Marko Gargenta.
 */

public class TrackerDbHelper extends DatabaseHelper {
    /**
     * The name of the single column in the table, named 'date'.
     */
    public static final String C_DATE = "date";

    /**
     * Creates a new DatabaseHelper with the tracker database.
     *
     * @param context The application context.
     */
    public TrackerDbHelper(Context context) {
        super(context, "tracker.db", "tracker");
    }

    /**
     * Called when DB is first created. Creates a table of a single column labelled 'date', which
     * will be added onto as new rows.
     *
     * @param db Database to create table on.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE + " (" + C_DATE + " DATE PRIMARY KEY)";
        db.execSQL(sql);
        Log.d("TrackerDbHelper", "onCreate w sql: " + sql);
    }

    /**
     * Adds today's date to the database as a new row. Then updates the streaks on the Streak
     * Database, as this new row may imply a change in the information there.
     *
     * @param sdbh StreakDatabaseHelper so that the streak update can be triggered.
     */
    public void addDateToDb(StreakDbHelper sdbh) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        String dateString = "";
        Date currentDate = new Date();
        dateString = (currentDate.getYear() + 1900) + "-"
                + (currentDate.getMonth()) + "-" + currentDate.getDate();
        values.put(C_DATE, dateString);
        try {
            db.insertOrThrow(TABLE, null, values);
        } catch (SQLException e){
            Log.e("DATABASE", "ERROR when adding to DB");
            e.printStackTrace();
        }
        db.close();

        updateStreaks(sdbh);
    }

    /**
     * By updating current, may update best as a side effect
     * Calendar is required as it obeys calendar rules when adding and subtracting
     * Date does not appear to do so.
     *
     * @param sdbh Needs to be provided a helper due to Context requirement
     */
    public void updateStreaks(StreakDbHelper sdbh) {
        int newStreak = 0;
        HashSet<Date> dates = getDates();
        Date dateEntry = new Date();
        Calendar cal = Calendar.getInstance();

        dateEntry = clearTime(dateEntry);
        while(dates.contains(dateEntry)){
            newStreak++;

            cal.setTime(dateEntry);
            cal.add(Calendar.DATE, -1);
            dateEntry = cal.getTime();
        }

        sdbh.updateCurrent(newStreak);
    }

    /**
     * Erases the time from a Date object, as otherwise two Dates with the same calendar date
     * are not identical in the eyes of Java.
     *
     * @param dateEntry Date to sanitize
     * @return sanitized Date
     */
    private Date clearTime(Date dateEntry) {
        long fullTime = dateEntry.getTime();
        long millis = fullTime % 1000;
        Date newDate = new Date(fullTime - millis);
        newDate.setHours(0);
        newDate.setMinutes(0);
        newDate.setSeconds(0);
        return newDate;
    }

    /**
     * Gets all of the dates which are recorded in the database.
     *
     * @return HashSet of all the dates in the database.
     */
    public HashSet<Date> getDates() {
        HashSet<Date> dates = new HashSet<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true, TABLE, null, null, null, null, null, null, null);
        String strDate;
        int year, month, date;
        final int COLUMN_INDEX = 0; //There's only 1 column in this table

        cursor.moveToFirst();
        try {
            if (cursor.isNull(COLUMN_INDEX)) {
                db.close();
                return dates;
            }
        } catch (CursorIndexOutOfBoundsException e){
            db.close();
            return dates;
        }
        //Past this point, safe to assume there's dates in the database
        do{
            strDate = cursor.getString(COLUMN_INDEX);
            year = Integer.parseInt(strDate.substring(0, 4)) - 1900;
            if (doubleDigitMonth(strDate)){
                month = Integer.parseInt(strDate.substring(5,7));
                date = Integer.parseInt(strDate.substring(8));
            } else {
                month = Integer.parseInt(String.valueOf(strDate.charAt(5)));
                date = Integer.parseInt(strDate.substring(7));
            }
            dates.add(new Date(year, month, date));
        } while (cursor.moveToNext());
        db.close();
        return dates;
    }

    /**
     * Takes a specially formatted String date and calculates whether or not the month in said date
     * is at or past October (month 10).
     *
     * @param strDate String numeric date with dash delimination
     * @return true if month >= 10, false otherwise
     */
    private boolean doubleDigitMonth(String strDate) {
        return strDate.charAt(5) == '1' && strDate.charAt(6) != '-';
    }
}
