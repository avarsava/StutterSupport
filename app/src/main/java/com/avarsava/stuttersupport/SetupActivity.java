package com.avarsava.stuttersupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * @author Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * First time the app is launched, user is sent to this screen
 * to fill out a form with starting preferences.
 */

public class SetupActivity extends PreferenceActivity {
    /**
     * Number of settings present in XML
     */
    private final int NUM_OF_SETTINGS = 12;

    /**
     * Resource ID of settings file to expand into layout.
     */
    protected int prefsFile = 0;

    /**
     * Database helper for tracking Setup completedness
     */
    private SetupDbHelper dbHelper;

    /**
     * Creates a settings screen based on the preferences file carried in the Extras. Uses
     * an automatic function of Android to generate the settings screen.
     *
     * @param savedInstanceState Bundle containing the extras, including the preferences file.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        dbHelper = new SetupDbHelper(this, "setup.db", "SETUP");
        super.onCreate(savedInstanceState);

        prefsFile = R.xml.setup_prefs;

        // Load the preferences from an XML resource
        addPreferencesFromResource(prefsFile);

        //TODO: Make strings not hardcoded
        Dialog.showDialog(this, "Welcome!", "Please set your initial settings before" +
                " beginning to use this app.");
    }

    /**
     * Expands preferences from XML into a settings screen layout, then sets back and restore
     * buttons to listen for click activity.
     *
     * Since all the settings are just SharedPreferences, don't need to worry about actually
     * 'submitting' anything.
     *
     * @param preferencesResId Resoure ID of the preferences file to expand
     */
    @Override
    public void addPreferencesFromResource(int preferencesResId){
        super.addPreferencesFromResource(preferencesResId);

        final Activity thisActivity = this;
        Preference submitButton = findPreference("submitButton");

        submitButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(allPrefsSet()) {
                    dbHelper.setDone();
                    finish();
                    return true;
                } else {
                    //TODO: Make strings not hardcoded
                    Dialog.showDialog(thisActivity, "Sorry!", "Please fill out every" +
                            " setting before pressing Submit.");
                    return false;
                }

            }
        });
    }

    /**
     * Determines whether all preferences in Setup have a value if applicable.
     *
     * @return true if all preferences have been set appropriately.
     */
    private boolean allPrefsSet(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> allprefs = preferences.getAll();

        if(allprefs.size() != NUM_OF_SETTINGS) return false;

        for(Map.Entry<String, ?> entry : allprefs.entrySet()){
            String prefValue = entry.getValue().toString();
            if (prefValue == null){
                return false;
            }
        }

        return true;
    }
}
