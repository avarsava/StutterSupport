package com.avarsava.stuttersupport;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * @author  Aditi Trivedi <at15gp@brocku.ca>
 * @version 1.1
 * @since   0.1
 *
 * TODO: Add intent testing
 * Test MainActivity
 */
@RunWith(AndroidJUnit4.class)
public class TestMainActivity {

    @Rule
    public ActivityTestRule<MainActivity> ruleMainActivity  = new  ActivityTestRule<>(MainActivity.class);

    @Test
    public void ensureButtonsArePresent() throws Exception {
        MainActivity activity = ruleMainActivity.getActivity();

        View splashButtonView = activity.findViewById(R.id.splashButton);
        assertNotNull(splashButtonView);
        assertThat(splashButtonView, instanceOf(ImageView.class));

        View licensesButtonView = activity.findViewById(R.id.licensesButton);
        assertNotNull(licensesButtonView);
        assertThat(licensesButtonView, instanceOf(Button.class));

        View notificationsSettingsButtonView = activity.findViewById(R.id.notificationSettingsButton);
        assertNotNull(notificationsSettingsButtonView);
        assertThat(notificationsSettingsButtonView, instanceOf(Button.class));
    }

}