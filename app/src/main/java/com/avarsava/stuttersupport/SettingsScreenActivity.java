package com.avarsava.stuttersupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.0
 * @since   0.1
 *
 * Allows for the creation of a Settings page based on an XML preferences list.
 */
public class SettingsScreenActivity extends PreferenceActivity{
    /**
     * Resource ID of settings file to expand into layout. Default value of 0 will be overwritten.
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

        prefsFile = getIntent().getExtras().getInt("prefs");

        // Load the preferences from an XML resource
        addPreferencesFromResource(prefsFile);


    }

    /**
     * Expands preferences from XML into a settings screen layout, then sets back and restore
     * buttons to listen for click activity. Disables the Restore Defaults button if the parent
     * has locked any settings. Then sets certain difficulty-related preferences as
     * enabled or disabled based on parent settings.
     *
     * @param preferencesResId Resource ID of the preferences file to expand
     */
    @Override
    public void addPreferencesFromResource(int preferencesResId){
        super.addPreferencesFromResource(preferencesResId);

        final Activity thisActivity = this;
        final SharedPreferences sharedPrefs
                = PreferenceManager.getDefaultSharedPreferences(thisActivity);

        Preference backButton = findPreference("backButton");
        backButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                finish();
                return true;
            }
        });

        Preference restoreButton = findPreference("restoreDefaults");

        //If any of the preferences are locked by the parent, disable restoring the defaults
        Boolean prefsLocked = Boolean.valueOf(sharedPrefs.getBoolean("pti_tg_override",
                false))
                || Boolean.valueOf(sharedPrefs.getBoolean("pti_override_deepBreathing",
                false));
        if (prefsLocked) restoreButton.setEnabled(false);

        restoreButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Dialog.showDialogWithPosListener(thisActivity,
                        getString(R.string.restore_defaults),
                        getString(R.string.restore_defaults_warning),
                        new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return restoreDefaults();
                            }
                        });
                return true;
            }
        });

        //Render based on Parent/Teacher difficulty locks
        switch(preferencesResId){
            case R.xml.train_game_prefs:
                Preference tgDifficulty = findPreference("tg_Difficulty");
                Boolean tgLocked
                        = Boolean.valueOf(sharedPrefs.getBoolean("pti_tg_override", false));

                //If Train Game is NOT locked, enable the preference.
                tgDifficulty.setEnabled(!tgLocked);
                break;
            case R.xml.deep_breathe_prefs:
                Boolean dbLocked =
                        Boolean.valueOf(sharedPrefs.getBoolean("pti_override_deepBreathing",
                                false));
                Preference dbInhaleLength = findPreference("db_inhaleLength");
                Preference dbExhaleLength = findPreference("db_exhaleLength");
                Preference dbNumBreaths = findPreference("db_noOfBreaths");

                List<Preference> prefs = new LinkedList<>();
                prefs.add(dbInhaleLength);
                prefs.add(dbExhaleLength);
                prefs.add(dbNumBreaths);

                //If Deep Breathe is NOT locked, enable the preferences.
                for(Preference pref : prefs){
                    pref.setEnabled(!dbLocked);
                }
                break;
        }
    }

    /**
     * Restores the default settings for all activities, then refreshes the settings screen
     * to reflect the changes.
     */
    private boolean restoreDefaults(){
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        setPreferenceScreen(null);
        addPreferencesFromResource(prefsFile);

        return true;
    }
}
