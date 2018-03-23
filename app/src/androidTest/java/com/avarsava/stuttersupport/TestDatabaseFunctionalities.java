package com.avarsava.stuttersupport;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.*;

/**
 * @author  Aditi Trivedi <at15gp@brocku.ca>
 * @version 1.5
 * @since   1.1
 *
 * Testing functionality of TrackerDbHelper, StreakDbHelper, DatabaseHelper
 */


@RunWith(AndroidJUnit4.class)
public class TestDatabaseFunctionalities {
    /**
     * Accesses tracker database
     */
    public static TrackerDbHelper trackerDbHelper;

    /**
     * Accesses streak database
     */
    public static StreakDbHelper streakDbHelper;

    /**
     * Before running tests, get context and databases
     *
     * @throws Exception Throws exception if cannot get context
     */
    @BeforeClass
    public static void setUp() throws Exception {
        Log.d("TestDB", "calling setUp()...");
        Context context = InstrumentationRegistry.getTargetContext();
        trackerDbHelper = new TrackerDbHelper(context);
        streakDbHelper = new StreakDbHelper(context);
    }

    /**
     * After running tests, clear the tracker and streak databases and close connections.
     * ATTN: If you run the tests on an existing copy of the app,
     *      !!! IT WILL DELETE THE EXISTING TRACKING INFO !!!
     * This shouldn't be an issue as we probably won't be running these tests against
     * existing copies of the app.
     *
     * @throws Exception if database deletion fails
     */
    @AfterClass
    public static void tearDown() throws Exception {
        Log.d("TestDB", "calling tearDown()...");
        trackerDbHelper.getWritableDatabase().delete(trackerDbHelper.TABLE, null, null);
        streakDbHelper.getWritableDatabase().delete(streakDbHelper.TABLE, null, null);
        trackerDbHelper.close();
        streakDbHelper.close();
    }

    /**
     * Adds entries to Tracker Database and ensures they got added correctly.
     */
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


    /**
     * Tests for updateCurrentStreak and incrementBest
     */
    @Test
    public void testUpdateCurrentStreak() {
        int testCurrentValue = 5;
        int newStreak = 1;
        int currentBestValue = streakDbHelper.getBest();
        assertEquals(0, currentBestValue);

        streakDbHelper.updateCurrent(testCurrentValue);

        Cursor cursor = streakDbHelper.getWritableDatabase().query
                ("streak",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
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