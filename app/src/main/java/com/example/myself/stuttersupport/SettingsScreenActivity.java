package com.example.myself.stuttersupport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 0.1
 * @since   0.1
 *
 * Allows for the creation of a Settings page based on an XML preferences list.
 */
public class SettingsScreenActivity extends PreferenceActivity {

    private int prefsFile = 0;

    /**
     * Creates a settings screen based on the preferences file carried in the Extras. Uses
     * an automatic function of Android to generate the settings screen.
     *
     * @param savedInstanceState Bundle containing the extras, including the preferences file.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        prefsFile = getIntent().getExtras().getInt("prefs");

        // Load the preferences from an XML resource
        addPreferencesFromResource(prefsFile);


    }

    @Override
    public void addPreferencesFromResource(int preferencesResId){
        super.addPreferencesFromResource(preferencesResId);

        Preference backButton = findPreference("backButton");
        backButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                finish();
                return true;
            }
        });

        Preference restoreButton = findPreference("restoreDefaults");
        restoreButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                setPreferenceScreen(null);
                addPreferencesFromResource(prefsFile);

                return true;
            }
        });
    }
}
