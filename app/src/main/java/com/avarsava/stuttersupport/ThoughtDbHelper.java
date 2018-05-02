package com.avarsava.stuttersupport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.5
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
     * Details how Thought Tracker database should be laid out. Four columns: GUID, thought, mood,
     * date.
     *
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
     * Adds a DBEntry object to the database.
     *
     * @param entry DBEntry object to add to database.
     */
    public void addToDb(DBEntry entry){
        addToDb(entry.getThought(), MOOD.valueOf(entry.getMood()));
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
            Log.e(TAG, "ERROR when adding to DB");
            e.printStackTrace();
        }
        db.close();
    }

    /**
     * Clears all entries from the database table. Triggered from Settings.
     */
    public void clearDatabase(){
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE, null, null);
    }

    /**
     * Gets all thoughts entered on today's date.
     *
     * @return Array of DBEntry objects representing all thoughts entered on today's date
     */
    public DBEntry[] getTodaysThoughts(){
        return getThoughtsOnDate(DbDate.getDateString());
    }

    /**
     * Gets all thoughts entered on date represented by DbDate-formatted date string.
     *
     * @param dateString DbDate-formatted string representing target date.
     * @return Array of DBEntry objects representing all thoughts entered on target date.
     */
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
        String date, thought, mood;

        Log.d(TAG, "Getting thoughts on date: " + dateString);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int size = cursor.getCount();
        list = new DBEntry[size];
        for(int i = 0; i < size; i++){
            date = cursor.getString(cursor.getColumnIndex(C_DATE));
            thought = cursor.getString(cursor.getColumnIndex(C_THOUGHT));
            mood = cursor.getString(cursor.getColumnIndex(C_MOOD));

            list[i] = new DBEntry(date, thought, mood);

            Log.d(TAG, "Retrieved: " + list[i]);

            cursor.moveToNext();
        }
        db.close();

        return list;
    }

    /**
     * Generates list of 25 thoughts retrieved from database, ordered by most-entered.
     *
     * TODO: 25 is a magic number, move to constant
     *
     * @return List of Strings representing 25 most-entered thoughts in database, ordered by
     *          most-entered.
     */
    public List<String> mostCommonThoughts(){
        //Set up data
        List<String> sortedThoughts = new ArrayList<>();
        String currentThought;
        Map<String, Integer> thoughtList = new HashMap<>();
        List<DBCounter> dbCounterList = new ArrayList<>();

        Log.d(TAG, "Getting thoughts sorted by frequency...");

        String sql = "SELECT "
                + C_THOUGHT +
                " FROM "
                + TABLE + "" +
                " LIMIT 25;";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        //Count all the strings
        while(cursor.moveToNext()) {
            currentThought = cursor.getString(0);

            if (thoughtList.containsKey(currentThought)) {
                thoughtList.put(currentThought, ((Integer) thoughtList.get(currentThought)) + 1);
            } else {
                thoughtList.put(currentThought, 1);
            }
        }

        db.close();

        for(Map.Entry<String, Integer> e : thoughtList.entrySet()){
            DBCounter dbc = new DBCounter(e.getKey(), e.getValue());
            dbCounterList.add(dbc);
            Log.d(TAG, "Calculated: " + dbc);
        }

        //Sort by count
        Collections.sort(dbCounterList, new Comparator<DBCounter>() {
            @Override
            public int compare(DBCounter o1, DBCounter o2) {
                return o2.getCount() - o1.getCount();
            }
        });

        for(DBCounter d : dbCounterList){
            String t = d.getThought();
            sortedThoughts.add(t);
            Log.d(TAG, "Added to sorted list:" + d);

        }

        return sortedThoughts;
    }

    /**
     * Retrieves all thoughts entered in the 30 days prior to today's date as DBEntry objects.
     *
     * @return List of DBEntry objects representing all thoughts entered in the past 30 days.
     */
    public List<DBEntry> lastThirtyDaysThoughts(){
        List<DBEntry> sortedList = new ArrayList<>();
        Log.d(TAG, "Getting last 30 days' thoughts...");

        for(int offset = 0; offset <= 30; offset++){
            Date currentDate = new Date();
            currentDate.setDate(currentDate.getDate()-offset);
            List<DBEntry> newList = Arrays.asList
                    (getThoughtsOnDate(DbDate.getDateString(currentDate)));
            sortedList.addAll(newList);
        }

        Log.d(TAG, "Got list: " + sortedList);
        return sortedList;
    }

    /**
     * Calculates the average mood (rounded) on target date. Returns as integer.
     *
     * @param dateString DbDate-formatted String representing target date.
     * @return integer representing average (rounded) mood on target date.
     */
    public int averageMoodOnDate(String dateString){
        String sql = "SELECT "
                + C_MOOD +
                " FROM "
                + TABLE +
                " WHERE "
                + C_DATE +
                " = \""
                + dateString + "\";";
        Log.d(TAG, "Calculating average mood on date: " + dateString);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        int count = cursor.getCount();
        if(count == 0) return 0;

        int sum = 0;

        do {
            sum += MOOD.valueOf(cursor.getString(0)).getIntValue();
        } while(cursor.moveToNext());
        Log.d(TAG, "Sum = " + sum
        + " Count = " +
        count +
        " Mean = " + sum/count);
        return sum / count;
    }

    /**
     * Object representing one entry in database. Used to facilitate processing of entries.
     */
    public class DBEntry{
        /**
         * Date field, DbDate-formatted
         */
        String date;

        /**
         * Thought entered by user
         */
        String thought;

        /**
         * Mood assigned by user, represented as String
         */
        String mood;

        /**
         * Constructor for DBEntry with blank date field.
         *
         * @param t String representing thought entered by user.
         * @param m String representing mood entered by user.
         */
        public DBEntry(String t, String m){
            this.thought = t;
            this.mood = m;
            this.date = "";
        }

        /**
         * Constructor for DBEntry with all fields filled.
         *
         * @param d String representing DbDate-formatted Date
         * @param t String representing thought entered by user
         * @param m String representing mood entered by user
         */
        public DBEntry(String d, String t, String m){
            this.date = d;
            this.thought = t;
            this.mood = m;
        }

        /**
         * Gets Thought from this entry.
         *
         * @return String representing thought entered by user
         */
        public String getThought() {
            return thought;
        }

        /**
         * Gets Mood as String from this entry.
         *
         * @return String representing mood entered by user
         */
        public String getMood(){
            return mood;
        }

        /**
         * Gets DbDate-formatted String representing Date on which this entry was entered.
         *
         * @return DbDate-formatted String representing Date for this entry
         */
        public String getDate(){ return date;}

        /**
         * Formats this entry as "[date]: [thought], [mood]"
         *
         * @return String of formatted DBEntry
         */
        @Override
        public String toString(){
            return date + ": " + thought + ", " + mood;
        }

    }

    /**
     * Internal object used to facilitate counting of how often a thought occurs.
     */
    public class DBCounter{
        /**
         * String of thought being counted
         */
        String thought;

        /**
         * Integer representing number of times thought was entered.
         */
        Integer count;

        /**
         * Constructor for new DBCounter object.
         *
         * @param t Thought being counted
         * @param i integer representing number of occurences in database
         */
        public DBCounter(String t, Integer i){
            this.thought = t;
            this.count = i;
        }

        /**
         * Gets thought being counted
         *
         * @return String representing thought being counted
         */
        public String getThought(){
            return thought;
        }

        /**
         * Gets count of occurences of thought
         *
         * @return integer representing number of times thought occurs in database
         */
        public Integer getCount(){
            return count;
        }

        /**
         * Formats DBCounter object as "[thought], x[count]"
         *
         * @return String of formatted DBCounter
         */
        @Override
        public String toString(){
            return thought + ", x" + count;
        }
    }
}
