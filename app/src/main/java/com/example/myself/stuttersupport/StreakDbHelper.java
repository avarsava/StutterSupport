package com.example.myself.stuttersupport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by Myself on 6/16/2017.
 *
 * Based off DbHelper.java from Learning Android by Marko Gargenta
 */

public class StreakDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "streak.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE = "streak";
    public static final String C_TYPE = "type";
    public static final String C_VALUE = "value";
    public static final String R_CURRENT = "current";
    public static final String R_BEST = "best";
    Context context;

    public StreakDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * Called when DB is first created
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create table
        String sql = "CREATE TABLE " + TABLE + " (" + C_TYPE + " TEXT PRIMARY KEY, " + C_VALUE +
        " TEXT)";
        db.execSQL(sql);
        Log.d("StreakDbHelper", "onCreate w sql: " + sql);

        //Add two rows
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
     * Called whenever the database receives an update
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: Temporary and probably bad
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void updateCurrent(int newValue){

    }

    public void incrementBest(){
        int oldBest;
        ContentValues newRow = new ContentValues();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE,
                new String[]{C_VALUE},
                C_TYPE+"=?",
                new String[]{R_BEST},
                null, null, null);
        cursor.moveToFirst();

        oldBest = cursor.getInt(1);
        newRow.put(C_TYPE, R_BEST);
        newRow.put(C_VALUE, oldBest++);

        db.close();
        db = getWritableDatabase();
        db.update(TABLE, newRow, C_TYPE+"=?", new String[]{R_BEST});
    }
}
