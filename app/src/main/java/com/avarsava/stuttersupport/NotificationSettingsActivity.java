package com.avarsava.stuttersupport;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * @author  Alexis Varsava <av11sl@brocku.ca>
 * @version 1.1
 * @since   1.1
 *
 * Provides Settings screen for changing the Daily notification timing
 */

public class NotificationSettingsActivity extends SettingsScreenActivity{

    Context context = this;

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
        disableAlarm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(true/* TODO: This should be like disableAlarm.isChecked*/) {
                    notificationRegistrator.register();
                }else{
                    notificationRegistrator.deleteAlarm();
                }
                return true;
            }
        });

        disableAlarm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(notificationRegistrator.alarmExists()){
                    notificationRegistrator.deleteAlarm();
                    notificationRegistrator.register();
                }
                return true;
            }
        });

        //TODO: Is there a way to cut down code re-use here?
        disableAlarm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(notificationRegistrator.alarmExists()){
                    notificationRegistrator.deleteAlarm();
                    notificationRegistrator.register();
                }
                return true;
            }
        });
    }

}
