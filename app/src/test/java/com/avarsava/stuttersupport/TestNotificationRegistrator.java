package com.avarsava.stuttersupport;

import android.content.Context;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.*;


/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Tests validity of the NotificationRegistrator class.
 */

public class IntegrationTestNotificationRegistrator {
    /**
     * NotificationRegistrator with which to test
     */
    NotificationRegistrator nr;

    /**
     * Context to pass into nr's methods, as the Android components it calls wants one
     */
    Context context;

    /**
     * Tag for Logger
     */
    final String TAG = "UNIT TEST: NotificationRegistrator";

    /**
     * Before any tests are run, get a usable Context
     */
    @BeforeClass
    public void setUpBeforeAll() {
        //TODO: How to get context in this setting?
        //context =
    }

    /**
     * Test to ensure that alarmExists() reports true when we know an alarm exists.
     */
    @Test
    public void testAlarmExistsReportsTrueWhenAlarmExists(){
        nr = new NotificationRegistrator(false);

        //Provided we're confident in register()'s ability, this will ensure an alarm exists
        nr.register(context);

        boolean result = nr.alarmExists();

        assertTrue(result);
    }

    /**
     * Test to ensure alarmExists() reports false when we know there is no alarm.
     */
    @Test
    public void testAlarmExistsReportsFalseWhenAlarmDoesNotExist(){
        nr = new NotificationRegistrator(true);

        //Provided we're confident in deleteAlarm()'s ability, this will ensure no alarm exists
        nr.deleteAlarm(context);

        boolean result = nr.alarmExists();

        assertFalse(result);
    }

    /**
     * Test to ensure that deleteAlarm() does delete an alarm with the Android OS.
     * TODO: Same issue with this and testAlarmExistsReportsFalseWhenAlarmDoesNotExist
     */
    @Test
    public void testDeleteAlarmShouldDeleteAlarm(){
        nr = new NotificationRegistrator(true);

        //delete alarm
        nr.deleteAlarm(context);

        //get result of alarmExists
        boolean result = nr.alarmExists(context);

        assertFalse(result);
    }

    /**
     * After each test, let nr go
     */
    @After
    public void tearDownAfterEach(){
        if(nr != null) nr = null;
    }
}