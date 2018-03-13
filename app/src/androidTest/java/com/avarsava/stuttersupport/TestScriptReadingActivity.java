package com.avarsava.stuttersupport;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author  Aditi Trivedi <at15gp@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 */
@RunWith(AndroidJUnit4.class)
public class TestScriptReadingActivity{

    @Rule
    public ActivityTestRule<ScriptReadingActivity> ruleScriptReadingActivity  = new  ActivityTestRule<>(ScriptReadingActivity.class);

    @Before
    public void setUp() throws Exception {
        ScriptReadingActivity activity = ruleScriptReadingActivity.getActivity();


//        View splashButtonView = activity.findViewById(R.id.splashButton);
//        assertNotNull(splashButtonView);
//        assertThat(splashButtonView, instanceOf(Button.class));
//
//        View licensesButtonView = activity.findViewById(R.id.licensesButton);
//        assertNotNull(licensesButtonView);
//        assertThat(licensesButtonView, instanceOf(Button.class));
//
//        View notificationsSettingsButtonView = activity.findViewById(R.id.notificationSettingsButton);
//        assertNotNull(notificationsSettingsButtonView);
//        assertThat(notificationsSettingsButtonView, instanceOf(Button.class));
    }

}