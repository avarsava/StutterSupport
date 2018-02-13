package com.avarsava.stuttersupport;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Provides Settings screen for changing the Daily notification timing
 */

public class NotificationSettingsActivity extends SettingsScreenActivity{

    /**
     * Tag for logger
     */
    private final String TAG = "NotificationSettings";

    /**
     * Context for passing to methods which require it
     */
    Context context = this;

    /**
     * Manages notifications with the OS
     */
    NotificationRegistrator notificationRegistrator;

    /**
     * Creates a settings screen based on the preferences file carried in the Extras. Uses
     * an automatic function of Android to generate the settings screen.
     *
     * @param savedInstanceState Bundle containing the extras, including the preferences file.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        notificationRegistrator = new NotificationRegistrator(true, this);
    }

    /**
     * Expands preferences from XML into a settings screen layout, then sets back and restore
     * buttons to listen for click activity.
     *
     * @param preferencesResId Resource ID of the preferences file to expand
     */
    @Override
    public void addPreferencesFromResource(int preferencesResId){
        //First, set all pre-existing button listeners
        super.addPreferencesFromResource(preferencesResId);

        final Preference disableAlarm = findPreference("notificationUserDisable");
        final Preference changeHour = findPreference("notificationCustomHour");
        final Preference changeMinute = findPreference("notificationCustomMinute");
        disableAlarm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(isEnableChecked()){
                    Log.i(TAG, "Enable was checked, registering new alarm");
                    notificationRegistrator.register();
                }else{
                    Log.i(TAG, "Enable was unchecked, deleting existing alarm");
                    notificationRegistrator.deleteAlarm();
                }
                return true;
            }
        });

        changeHour.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                int newHour = Integer.valueOf(o.toString());
                int oldMinute =
                        Integer.valueOf(sp.getString("notificationCustomMinute", "00"));
                notificationRegistrator.updateAlarm(newHour, oldMinute);
                return true;
            }
        });

        changeMinute.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                int newMinute = Integer.valueOf(o.toString());
                int oldHour =
                        Integer.valueOf(sp.getString("notificationCustomHour", "16"));
                notificationRegistrator.updateAlarm(oldHour, newMinute);
                return true;
            }
        });
    }

    /**
     * Checks whether the 'Enable' checkbox is currently checked
     * @return true if 'Enable' is currently checked
     */
    private boolean isEnableChecked(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = sp.getBoolean("notificationUserDisable", true);
        return enabled;
    }
}
