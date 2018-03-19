package com.avarsava.stuttersupport;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.*;

/**
 * @author  Aditi Trivedi <at15gp@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Testing functionality of TrackerDbHelper, StreakDbHelper, DatabaseHelper
 */


//test updateStreak in trackerdbhelper
@RunWith(AndroidJUnit4.class)
public class TestDatabaseFunctionalities {

    public static TrackerDbHelper trackerDbHelper;
    public static StreakDbHelper streakDbHelper;
    //public static SQLiteDatabase trackerDb;
    //public static SQLiteDatabase streakDb;


    @BeforeClass
    public static void setUp() throws Exception {
        Log.d("TestDB", "calling setUp()...");
        //Possible fix: InstrumentationRegistry.getTargetContext().deleteDatabase(DatabaseHelper.) might have to delete old stuff
        Context context = InstrumentationRegistry.getTargetContext();
        trackerDbHelper = new TrackerDbHelper(context);
        //trackerDb = trackerDbHelper.getWritableDatabase();

        streakDbHelper = new StreakDbHelper(context);
        //streakDb = streakDbHelper.getWritableDatabase();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Log.d("TestDB", "calling tearDown()...");
        trackerDbHelper.close();
        streakDbHelper.close();
    }

    @Test
    public void testAddingToTrackerDb() {
        String testActivityNameItem1 = "testActivityForTracker1";
        int testPerformanceItem1 = 3;
        int testDifficultyItem1 = 1;
        String testActivityNameItem2 = "testActivityForTracker2";
        int testPerformanceItem2 = 2;
        int testDifficultyItem2 = 3;
        trackerDbHelper.addToDb(testActivityNameItem1, testPerformanceItem1, testDifficultyItem1, streakDbHelper);
        trackerDbHelper.addToDb(testActivityNameItem2, testPerformanceItem2, testDifficultyItem2, streakDbHelper);
        assertEquals(2, DatabaseUtils.queryNumEntries(trackerDbHelper.getWritableDatabase(), "tracker"));

        // check data is correct in db
        Cursor cursor = trackerDbHelper.getWritableDatabase().query("tracker", null, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());
        int activityColIndex = cursor.getColumnIndex(TrackerDbHelper.C_ACTIVITY);
        int performanceColIndex = cursor.getColumnIndex(TrackerDbHelper.C_PERFORMANCE);
        int difficultyColIndex = cursor.getColumnIndex(TrackerDbHelper.C_DIFFICULTY);

        assertEquals(testActivityNameItem1, cursor.getString(activityColIndex));
        assertEquals(testPerformanceItem1, cursor.getInt(performanceColIndex));
        assertEquals(testDifficultyItem1, cursor.getInt(difficultyColIndex));
    }


    // Tests for updateCurrentStreak and incrementBest
    @Test
    public void testUpdateCurrentStreak() {
        int testCurrentValue = 5;
        int newStreak = 1;
        int currentBestValue = streakDbHelper.getBest();
        assertEquals(0, currentBestValue);

        streakDbHelper.updateCurrent(testCurrentValue);

        Cursor cursor = streakDbHelper.getWritableDatabase().query("streak", null, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());

        int typeColumnIndex = cursor.getColumnIndex(StreakDbHelper.C_TYPE);
        int valueColumnIndex = cursor.getColumnIndex(StreakDbHelper.C_VALUE);

        assertEquals(StreakDbHelper.R_CURRENT, cursor.getString(typeColumnIndex));
        assertEquals(testCurrentValue, cursor.getInt(valueColumnIndex));

        assertTrue(cursor.moveToNext());

        int typeColumnIndex2 = cursor.getColumnIndex(StreakDbHelper.C_TYPE);
        int valueColumnIndex2= cursor.getColumnIndex(StreakDbHelper.C_VALUE);

        assertEquals(StreakDbHelper.R_BEST, cursor.getString(typeColumnIndex2));
        assertEquals(newStreak, cursor.getInt(valueColumnIndex2));

        int updatedBestValue = streakDbHelper.getBest();
        assertEquals(newStreak, updatedBestValue);


    }
}