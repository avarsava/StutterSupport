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
        String sql = "CREATE TABLE " + TABLE + " (" + C_TYPE + " TEXT PRIMARY KEY, " + C_VALUE +
        " TEXT)";
        db.execSQL(sql);
        Log.d("StreakDbHelper", "onCreate w sql: " + sql);
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
}
