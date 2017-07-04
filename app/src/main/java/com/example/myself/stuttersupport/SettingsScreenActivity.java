package com.example.myself.stuttersupport;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsScreenActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        int prefsFile = getIntent().getExtras().getInt("prefs");

        // Load the preferences from an XML resource
        addPreferencesFromResource(prefsFile);
    }
}
