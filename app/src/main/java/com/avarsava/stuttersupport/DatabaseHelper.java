package com.avarsava.stuttersupport;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Generic database helper class to interface with SQLite.
 */
public abstract class DatabaseHelper extends SQLiteOpenHelper {
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
}
