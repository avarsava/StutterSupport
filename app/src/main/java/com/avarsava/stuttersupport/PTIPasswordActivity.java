package com.avarsava.stuttersupport;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.5
 * @since   1.1
 *
 * Prompts for parent's password then launches Parent-Teacher Interface if password is correct.
 */

public class PTIPasswordActivity extends Activity {
    /**
     * Name of password field in Settings XML
     */
    private final String PASSWORD_KEY = "parent_password";

    /**
     * Android object representing all preferences stored to device
     */
    SharedPreferences prefs;

    /**
     * Gets preferences from OS and expands XML to screen.
     *
     * @param savedInstanceState Bundle used for internal Android messaging
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_pti_password);
    }

    /**
     * Handles button press. Gets password from preferences and string-compares to password
     * entered in EditText field. Insecure, but effective for our purposes for now.
     *
     * @param view Button. Unused
     */
    public void onClick(View view){
        String expectedPassword, enteredPassword;
        expectedPassword = prefs.getString(PASSWORD_KEY, "");
        enteredPassword = ((EditText)findViewById(R.id.passwordInput)).getText().toString();

        if(enteredPassword.equals(expectedPassword)){
            Intent cont = new Intent(this, ParentTeacherInterfaceActivity.class);
            startActivity(cont);
        } else {
            Dialog.showDialog(this, "Sorry!", "Your password did not match" +
                    " the one we have saved. Please try again.");
        }
    }
}
