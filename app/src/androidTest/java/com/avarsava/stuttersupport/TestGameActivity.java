package com.avarsava.stuttersupport;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * @author  Aditi Trivedi <at15gp@brocku.ca>
 * @version 1.5
 * @since   1.1
 *
 * Tests behaviour of BasketballActivity, and GameActivity via BasketballActivity.
 */

@RunWith(AndroidJUnit4.class)
public class TestGameActivity {

    /**
     * Instance of BasketballGameActivity to test against
     */
    BasketballGameActivity bActivity;

    /**
     * Intent to test message passing
     */
    Intent testIntent;

    /**
     * Enables launching BasketballActivity
     */
    @Rule public ActivityTestRule<BasketballGameActivity> ruleBasketballActivity
            = new ActivityTestRule<>
            (BasketballGameActivity.class, false, true);

    /**
     * Gets activity and intent, waits for speech recognition to initialize to avoid crashing
     * when testing switchState
     *
     * @throws Exception if something fails
     */
    @Before
    public void setUp() throws Exception{
        bActivity = ruleBasketballActivity.getActivity();
        testIntent = bActivity.getIntent();
        long startTime = System.currentTimeMillis();

        Field speechRecognizer = bActivity.getClass().getSuperclass().getDeclaredField("recognizer");
        speechRecognizer.setAccessible(true);
        while(speechRecognizer.get(bActivity) == null){
            Thread.sleep(100);
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > 5000){
                fail("Recognizer did not initialize");
                return;
            }
        }
    }

    /**
     *  Ensure that the activity is killed properly with the appropriate intent
     *
     * @throws Exception if something fails
     */
    @Test
    public void testKillIfCountHigh() throws Exception {
        Field privateCycleCount = bActivity.getClass().getSuperclass().getDeclaredField
                ("cycleCount");
        privateCycleCount.setAccessible(true);
        privateCycleCount.set(bActivity, 5);
        Field privateMaxCycle = bActivity.getClass().getSuperclass().getDeclaredField
                ("maxCycles");
        privateMaxCycle.set(bActivity, 3);

        bActivity.killIfCountHigh("TestActivityName",1,1);
        Bundle bundle = testIntent.getExtras();

        assertEquals("TestActivityName", bundle.getString("activityName"));
        assertEquals(1, bundle.getInt("activityPerformance"));
        assertEquals(1, bundle.getInt("activityDifficulty"));
    }

    /**
     * Ensures BasketballGameActivity.switchState() functions as defined by forcing a switch
     *
     * @throws Exception if something fails
     */
    @Test
    public void testSwitchState() throws Exception {
        Field privateCurrentState = bActivity.getClass().getDeclaredField("currentState");
        privateCurrentState.setAccessible(true);
        privateCurrentState.set(bActivity, BasketballGameActivity.STATE.DRIBBLE_2);
        Field privateStateInfo = bActivity.getClass().getDeclaredField("stateInfo");
        privateStateInfo.setAccessible(true);
        int currentStateInfo = (int) privateStateInfo.get(bActivity);


        //Check for state dribble_2
        bActivity.switchState();
        int newStateInfo = (int) privateStateInfo.get(bActivity);
        BasketballGameActivity.STATE newState
                = (BasketballGameActivity.STATE) privateCurrentState.get(bActivity);
        assertEquals(currentStateInfo + 1,  newStateInfo);
        assertEquals(BasketballGameActivity.STATE.DRIBBLE_1, newState);

        //Check for state dribble_1
        privateCurrentState.set(bActivity, BasketballGameActivity.STATE.DRIBBLE_1);
        privateStateInfo.set(bActivity, 3);
        bActivity.switchState();
        newStateInfo = (int) privateStateInfo.get(bActivity);
        newState = (BasketballGameActivity.STATE) privateCurrentState.get(bActivity);
        assertEquals(0,  newStateInfo);
        assertEquals(BasketballGameActivity.STATE.SHOOTING, newState);
    }

}