package com.avarsava.stuttersupport;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Facilitates data storage for the Thought Tracker activity.
 * Baased off DbHelper.java from Learning Android by Marko Gargenta.
 */

public class ThoughtDbHelper extends DatabaseHelper {
    /**
     * Tag for logs
     */
    private final String TAG = "ThoughtDbHelper";

    /**
     * The names of the columns in the table
     */
    public static final String C_THOUGHT = "thought";
    public static final String C_MOOD = "mood";
    public static final String C_DATE = "date";

    /**
     * Database helper constructor. Saves information to object and creates SQLite helper.
     *
     * @param context Application context
     */
    public ThoughtDbHelper(Context context) {
        super(context, "thought.db", "thought");
    }

    /**
     * Details how Thought Tracker database should be laid out.
     * @param db SQLite database to have table created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE + " (" + BaseColumns._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + C_THOUGHT + " TEXT NOT NULL, "
                + C_MOOD + " TEXT NOT NULL, "
                + C_DATE + " DATE NOT NULL)";
        db.execSQL(sql);
        Log.d(TAG, "onCreate w sql: " + sql);
    }

    /**
     * Adds a thought entry to the database. Automatically inputs the correct ID and Date.
     *
     * @param thought String of the thought entered.
     * @param mood MOOD enum of the mood selected.
     */
    public void addToDb(String thought, MOOD mood){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        String dateString = DbDate.getDateString();

        values.put(C_THOUGHT, thought);
        values.put(C_MOOD, mood.toString());
        values.put(C_DATE, dateString);

        try {
            db.insertOrThrow(TABLE, null, values);
        } catch (SQLException e){
            Log.e("DATABASE", "ERROR when adding to DB");
            e.printStackTrace();
        }
        db.close();
    }
}
