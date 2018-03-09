package com.avarsava.stuttersupport;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

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
     * Resource ID of settings file to expand into layout.
     */
    protected int prefsFile = 0;

    /**
     * Creates a settings screen based on the preferences file carried in the Extras. Uses
     * an automatic function of Android to generate the settings screen.
     *
     * @param savedInstanceState Bundle containing the extras, including the preferences file.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        prefsFile = R.xml.setup_prefs;

        // Load the preferences from an XML resource
        addPreferencesFromResource(prefsFile);

        //TODO: show dialog
    }

    /**
     * Expands preferences from XML into a settings screen layout, then sets back and restore
     * buttons to listen for click activity.
     *
     * @param preferencesResId Resoure ID of the preferences file to expand
     */
    @Override
    public void addPreferencesFromResource(int preferencesResId){
        super.addPreferencesFromResource(preferencesResId);

        final Activity thisActivity = this;
        //TODO: Convert to submit button
        Preference backButton = findPreference("backButton");
        backButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                finish();
                return true;
            }
        });
    }
}
