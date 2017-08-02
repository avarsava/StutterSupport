package com.example.myself.stuttersupport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Displays the splash screen, with a button to show the third party licenses
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
        }
        startActivity(intent);
    }
}
