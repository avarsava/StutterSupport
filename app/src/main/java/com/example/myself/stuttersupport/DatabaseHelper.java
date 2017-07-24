package com.example.myself.stuttersupport;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Myself on 7/24/2017.
 */

public abstract class DatabaseHelper extends SQLiteOpenHelper {
    protected final String DB_NAME;
    protected final String TABLE;
    private static final int DB_VERSION = 1;
    Context context; //TODO: Why do I need this

    public DatabaseHelper(Context context, String dbname, String table){
        super(context, dbname, null, DB_VERSION);
        this.DB_NAME = dbname;
        this.TABLE = table;
        this.context = context;
    }

    @Override
    public abstract void onCreate(SQLiteDatabase db);

    /**
     * Called whenever the database receives an update
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
