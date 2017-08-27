package com.avarsava.stuttersupport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Eases accessing streak information from the database. The database contains two columns, 'type'
 * and 'value', and only ever has two rows, where type = 'current' and type = 'best'. These hold
 * the current and best streak scores, respectively, under 'value'.
 * Based off DbHelper.java from Learning Android by Marko Gargenta.
 */

public class StreakDbHelper extends DatabaseHelper {
    /**
     * Column name called 'type', details whether the row contains the current or best streak.
     */
    public static final String C_TYPE = "type";

    /**
     * Column name called 'value', holds the high score for each streak.
     */
    public static final String C_VALUE = "value";

    /**
     * Row name called 'current', that row holds the current streak score.
     */
    public static final String R_CURRENT = "current";

    /**
     * Row name called 'best', that row holds the all-time best streak score.
     */
    public static final String R_BEST = "best";

    /**
     * Creates a new database helper.
     *
     * @see DatabaseHelper
     * @param context The application context
     */
    public StreakDbHelper(Context context) {
        super(context, "streak.db", "streak");
    }

    /**
     * Called when DB is first created, initializes the table and adds rows for the current and
     * best streaks. Defaults both values to 0.
     *
     * @param db Database to create table on
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table
        String sql = "CREATE TABLE " + TABLE + " (" + C_TYPE + " TEXT PRIMARY KEY, " + C_VALUE +
        " TEXT)";
        db.execSQL(sql);
        Log.d("StreakDbHelper", "onCreate w sql: " + sql);

        //Add two rows and default values to 0
        ContentValues currentStreak = new ContentValues();
        ContentValues bestStreak = new ContentValues();
        currentStreak.clear();
        bestStreak.clear();
        currentStreak.put(C_TYPE, R_CURRENT);
        currentStreak.put(C_VALUE, 0);
        bestStreak.put(C_TYPE, R_BEST);
        bestStreak.put(C_VALUE, 0);
        try {
            db.insertOrThrow(TABLE, null, currentStreak);
            db.insertOrThrow(TABLE, null, bestStreak);
        } catch (SQLException e){
            Log.e("DATABASE", "ERROR when adding to DB");
            e.printStackTrace();
        }
    }

    /**
     * Gets the current streak from the database.
     *
     * @return current streak from the database
     */
    public int getCurrent(){
        return getValue(R_CURRENT);
    }

    /**
     * Gets the best streak from the database
     *
     * @return best streak from the database
     */
    public int getBest(){
        return getValue(R_BEST);
    }

    /**
     * Updates the current streak with a new value. Automatically increments the best streak if
     * the new current streak is higher than the best streak.
     *
     * @param newValue New value to set the current streak to.
     */
    public void updateCurrent(int newValue){
        int bestStreak = getBest();
        ContentValues newRow = new ContentValues();

        newRow.put(C_TYPE, R_CURRENT);
        newRow.put(C_VALUE, newValue);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE, newRow, C_TYPE+"=?", new String[]{R_CURRENT});
        if(newValue > bestStreak) incrementBest();
        db.close();
    }

    /**
     * Increments the best streak in the database.
     */
    public void incrementBest(){
        int oldBest = getBest();
        ContentValues newRow = new ContentValues();

        newRow.put(C_TYPE, R_BEST);
        newRow.put(C_VALUE, ++oldBest);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE, newRow, C_TYPE+"=?", new String[]{R_BEST});
        db.close();
    }

    /**
     * Gets the value from the 'value' column at a particular row. Used internally to get the
     * current and best streaks in getCurrent() and getBest().
     *
     * @param row Which row to retrieve the 'value' column from.
     * @return the value from that row, as an integer.
     */
    private int getValue(String row){
        int value;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE,
                new String[]{C_VALUE},
                C_TYPE+"=?",
                new String[]{row},
                null, null, null);
        cursor.moveToFirst();
        value = cursor.getInt(0);
        db.close();
        return value;
    }
}
