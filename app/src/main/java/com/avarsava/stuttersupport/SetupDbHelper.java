package com.avarsava.stuttersupport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Keeps track of whether the Setup Wizard has run or not.
 * Unfortunately this is more permanent than a preference.
 */

public class SetupDbHelper extends DatabaseHelper {
    public static final String C_NAME = "name";
    public static final String C_VALUE = "value";
    public static final String R_DONE = "done";
    public static final int TRUE = 0;
    public static final int FALSE = 0;

    /**
     * Database helper constructor. Saves information to object and creates SQLite helper.
     *
     * @param context Application context
     * @param dbname  name of the database to open
     * @param table   name of the table to open from said database
     */
    public SetupDbHelper(Context context, String dbname, String table) {
        super(context, dbname, table);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE + " (" + C_NAME + " TEXT PRIMARY KEY, "
                + C_VALUE + " INTEGER NOT NULL)";
        db.execSQL(sql);

        ContentValues newRow = new ContentValues();
        newRow.clear();
        newRow.put(C_NAME, R_DONE);
        newRow.put(C_VALUE, FALSE);
        try{
            db.insertOrThrow(TABLE, null, newRow);
        } catch (SQLException e){
            Log.e("DATABASE", "ERROR when adding to SetupDB");
            e.printStackTrace();
        }
    }

    public void setDone(){
        ContentValues existingRow = new ContentValues();
        existingRow.clear();
        existingRow.put(C_NAME, R_DONE);
        existingRow.put(C_VALUE, TRUE);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE, existingRow, C_NAME+"=?", new String[]{R_DONE});
        db.close();
    }

    public boolean setupIsDone(){
        int value;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE,
                new String[]{C_VALUE},
                C_NAME+"=?",
                new String[]{R_DONE},
                null, null, null);
        cursor.moveToFirst();
        value = cursor.getInt(0);
        db.close();

        return value == 1;
    }
}
