package com.avarsava.stuttersupport;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * @author  Aditi Trivedi <at15gp@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 */

// Tests for GameActivity and BasketballGameActivity
@RunWith(AndroidJUnit4.class)
public class TestGameActivity {

    GameActivity activity;
    BasketballGameActivity bActivity;
    Intent testIntent;

    @Rule
    public ActivityTestRule<GameActivity> ruleGameActivity  = new  ActivityTestRule<>(GameActivity.class);
    @Rule ActivityTestRule<BasketballGameActivity> ruleBasketballActivity = new ActivityTestRule<>(BasketballGameActivity.class);

    @Before
    public void setUp() {
        activity = ruleGameActivity.getActivity();
        bActivity = ruleBasketballActivity.getActivity();
        testIntent = activity.getIntent();
        testIntent.putExtra("activityName", "TestActivityName");
        testIntent.putExtra("activityPerformance", 1);
        testIntent.putExtra("activityDifficulty", 1);
    }

    // Ensure that the activity is killed properly with the appropriate intent
    @Test
    public void testKillIfCountHigh() throws Exception {
        Field privateCycleCount = activity.getClass().getDeclaredField("cycleCount");
        privateCycleCount.setAccessible(true);
        privateCycleCount.set(activity, 5);
        Field privateMaxCycle = activity.getClass().getDeclaredField("maxCycles");
        privateMaxCycle.set(activity, 3);

        activity.killIfCountHigh("TestActivityName",1,1);
        Field resultCode = activity.getClass().getDeclaredField("mResultCode");
        resultCode.setAccessible(true);
        int actualResultCode = (Integer) resultCode.get(activity);
        assertThat(actualResultCode, is(-1));

        Field resultData = activity.getClass().getDeclaredField("mResultData");
        resultData.setAccessible(true);
        assertEquals(testIntent, resultData.get(activity));
    }

    @Test
    public void testSwitchState() throws Exception {
        Field privateCurrentState = bActivity.getClass().getDeclaredField("currentState");
        privateCurrentState.setAccessible(true);
        privateCurrentState.set(bActivity, BasketballGameActivity.STATE.DRIBBLE_2);
        Field privateStateInfo = bActivity.getClass().getDeclaredField("stateInfo");
        int currentStateInfo = (int) privateStateInfo.get(bActivity);
        privateStateInfo.setAccessible(true);

        //Check for state dribble_2
        bActivity.switchState();
        int newStateInfo = (int) privateStateInfo.get(bActivity);
        BasketballGameActivity.STATE newState = (BasketballGameActivity.STATE) privateCurrentState.get(bActivity);
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