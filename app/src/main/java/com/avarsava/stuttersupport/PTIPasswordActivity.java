package com.avarsava.stuttersupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Prompts for parent's password then launches Parent-Teacher Interface if password is correct.
 *
 * TODO: Javadoc
 * TODO: Reset password???
 */

public class PTIPasswordActivity extends Activity {

    private final String PASSWORD_KEY = "parent_password";

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_pti_password);
    }

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
