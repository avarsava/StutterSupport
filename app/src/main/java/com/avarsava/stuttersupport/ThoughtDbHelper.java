package com.avarsava.stuttersupport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

    public DBEntry[] getTodaysThoughts(){
        return getThoughtsOnDate(DbDate.getDateString());
    }

    public DBEntry[] getThoughtsOnDate(String dateString){
        DBEntry[] list;
        String sql = "SELECT * FROM "
                + TABLE +
                " WHERE "
                + C_DATE +
                " = \""
                + dateString +"\""
                + " ORDER BY "
                + C_DATE + " ASC;";
        String thought, mood;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int size = cursor.getCount();
        list = new DBEntry[size];
        for(int i = 0; i < size; i++){
            thought = cursor.getString(cursor.getColumnIndex(C_THOUGHT));
            mood = cursor.getString(cursor.getColumnIndex(C_MOOD));

            list[i] = new DBEntry(thought, mood);

            cursor.moveToNext();
        }
        db.close();

        return list;
    }

    public List<String> mostCommonThoughts(){
        //Set up data
        List<String> sortedThoughts = new ArrayList<>();

        String currentThought;
        Map<String, Integer> thoughtList = new HashMap<>();
        List<DBCounter> dbCounterList = new ArrayList<>();
        String sql = "SELECT "
                + C_THOUGHT +
                " FROM "
                + TABLE + ";";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        //Count all the strings
        while(cursor.moveToNext()) {
            currentThought = cursor.getString(0);

            if (thoughtList.containsKey(currentThought)) {
                thoughtList.put(currentThought, ((Integer) thoughtList.get(currentThought)) + 1);
            } else {
                thoughtList.put(currentThought, 0);
            }
        }

        db.close();

        for(Map.Entry<String, Integer> e : thoughtList.entrySet()){
            dbCounterList.add(new DBCounter(e.getKey(), e.getValue()));
        }

        //Sort by count
        Collections.sort(dbCounterList, new Comparator<DBCounter>() {
            @Override
            public int compare(DBCounter o1, DBCounter o2) {
                return o1.getCount() - o2.getCount();
            }
        });

        for(DBCounter d : dbCounterList){
            sortedThoughts.add(d.getThought());
        }

        return sortedThoughts;
    }


    public List<DBEntry> lastThirtyDaysThoughts(){
        List<DBEntry> sortedList = new ArrayList<>();

        for(int offset = 0; offset <= 30; offset++){
            Date currentDate = new Date();
            currentDate.setDate(currentDate.getDate()-offset);
            List<DBEntry> newList = Arrays.asList
                    (getThoughtsOnDate(DbDate.getDateString(currentDate)));
            sortedList.addAll(newList);
        }

        return sortedList;
    }

    public int averageMoodOnDate(String dateString){
        String sql = "SELECT "
                + C_MOOD +
                " FROM "
                + TABLE +
                " WHERE "
                + C_DATE +
                " = \""
                + dateString + "\";";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        int count = cursor.getCount();
        int sum = 0;

        while(cursor.moveToNext()){
            sum += cursor.getInt(0);
        }

        return sum / count;
    }

    public class DBEntry{
        String thought;
        String mood;

        public DBEntry(String t, String m){
            this.thought = t;
            this.mood = m;
        }

        public String getThought() {
            return thought;
        }

        public String getMood(){
            return mood;
        }

    }

    public class DBCounter{
        String thought;
        Integer count;

        public DBCounter(String t, Integer i){
            this.thought = t;
            this.count = i;
        }

        public String getThought(){
            return thought;
        }

        public Integer getCount(){
            return count;
        }
    }
}
