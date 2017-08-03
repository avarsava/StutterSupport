package com.example.myself.stuttersupport;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Allows for the creation of a Settings page based on an XML preferences list.
 */
public class SettingsScreenActivity extends PreferenceActivity {

    /**
     * Creates a settings screen based on the preferences file carried in the Extras. Uses
     * an automatic function of Android to generate the settings screen.
     *
     * @param savedInstanceState Bundle containing the extras, including the preferences file.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        int prefsFile = getIntent().getExtras().getInt("prefs");

        // Load the preferences from an XML resource
        addPreferencesFromResource(prefsFile);

        Preference button = findPreference("backButton");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                finish();
                return true;
            }
        });
    }
}
