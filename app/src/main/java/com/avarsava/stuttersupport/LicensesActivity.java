package com.avarsava.stuttersupport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Displays licenses file on screen.
 */
public class LicensesActivity extends AppCompatActivity {

    /**
     * Inflates licenses file on screen in a TextView.
     *
     * @param savedInstanceState Used internally for communication
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
    }
}
