package com.avarsava.stuttersupport;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.6
 * @since   0.1
 *
 * Generic database helper class to interface with SQLite.
 */
public abstract class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * Restricts vocabulary for arguments
     */
    protected enum COUNT_TYPE {DATE, PERFORMANCE, DIFFICULTY}

    /**
     * Name of database.
     */
    protected final String DB_NAME;

    /**
     * Name of database table.
     */
    protected final String TABLE;

    /**
     * Version of database. Not really used for anything but required.
     */
    private static final int DB_VERSION = 2;

    /**
     * Database helper constructor. Saves information to object and creates SQLite helper.
     *
     * @param context Application context
     * @param dbname name of the database to open
     * @param table name of the table to open from said database
     */
    public DatabaseHelper(Context context, String dbname, String table){
        super(context, dbname, null, DB_VERSION);
        this.DB_NAME = dbname;
        this.TABLE = table;
    }

    /**
     * Children of DatabaseHelper must define how their database should be created, as each
     * database has its own column layout.
     *
     * @param db SQLite database to have table created.
     */
    @Override
    public abstract void onCreate(SQLiteDatabase db);

    /**
     * Called whenever the database receives an update.
     *
     * @param db SQLite database in question
     * @param oldVersion version number previously held by database
     * @param newVersion new version number held by database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //For now, just drop the table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);

        //and recreate it from scratch
        onCreate(db);
    }

    public int getSum(){

    }

    public double getMean(){

    }

    /**
     * Gets results from a constructed query.
     *
     * @param countType Valid options are defined by COUNT_TYPE. Cannot be null.
     * @param activity Valid options are defined by ACTIVITY. Null is taken to mean 'any'
     * @param dateRange Valid options are defined by DATE_RANGE. Cannot be null.
     * @param difficulty Valid options are "1", "2", "3", and "*". Cannot be null.
     * @return Cursor of results from database.
     */
    public Cursor getResults(@NonNull COUNT_TYPE countType,
                             ACTIVITY activity,
                             @NonNull DATE_RANGE dateRange,
                             @NonNull String difficulty){

        List<String> validDifficulties = new LinkedList<String>(Arrays.asList("1", "2", "3", "*"));
        if (!validDifficulties.contains(difficulty)){
            throw new IllegalArgumentException("Difficulty must be 1, 2, 3, or *");
        }

        String activityStr;
        if (activity == null){
            activityStr = "*";
        } else {
            activityStr = activity.name();
        }

        String today, rangeDate;
        today = DbDate.getDateString();
        rangeDate = DbDate.getRangeDate(dateRange);

        SQLiteDatabase db = getReadableDatabase();


        Cursor cursor = db.rawQuery(
                "SELECT " + countType.name()
                + " FROM " + TABLE
                + " WHERE ( date BETWEEN " + today + " AND " + rangeDate + ")"
                + " AND ( difficulty = " + difficulty + ")"
                + " AND ( activity = '" + activityStr + "')",
                null);

        return cursor;
    }
}
