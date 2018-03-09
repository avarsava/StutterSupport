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
            showDialog(this, "Sorry!", "Your password did not match" +
                    " the one we have saved. Please try again.");
        }
    }

    /**
     * Shows a dialog with a message, and 'OK' and 'Cancel' buttons. If the user presses 'OK',
     * restores the default settings for all activities in the app.
     *
     * TODO: Abstract to own file, this appears in 3 files now.
     *
     * based on https://stackoverflow.com/questions/8227820/alert-dialog-two-buttons
     * @param activity This activity
     * @param title Title for the dialog
     * @param message Message to display on dialog
     */
    public void showDialog(Activity activity, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.OK_button), null);
        builder.show();
    }
}
