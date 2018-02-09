package com.avarsava.stuttersupport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Displays the splash screen, with a button to show the third party licenses. Sets up the daily
 * reminder notification.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Creates the Activity and displays the splash screen layout.
     *
     * @param savedInstanceState used for internal Android communication.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RegisterAlarmBroadcast();
    }

    /**
     * Handles clicking on the screen, launches the Main Menu or the Licenses screen depending
     * on which was clicked.
     *
     * @param view the view which was clicked (in this case, either the splash screen or licenses)
     */
    public void onClick(View view){
        Intent intent = null;
        switch(view.getId()){
            case R.id.splashButton:
                intent = new Intent(this, MainMenuActivity.class);
                break;
            case R.id.licensesButton:
                intent = new Intent(this, LicensesActivity.class);
                break;
            case R.id.notificationSettingsButton:
                intent = new Intent(this,
                        NotificationSettingsActivity.class);
                intent.putExtra("prefs", R.xml.notifications_prefs);
                break;
        }
        startActivity(intent);
    }

    /**
     * Registers a new alarm for timing daily notifications
     */
    private void RegisterAlarmBroadcast(){
        NotificationRegistrator register
                = new NotificationRegistrator(false, this);
        register.register();
    }
}
