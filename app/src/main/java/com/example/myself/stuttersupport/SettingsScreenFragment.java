package com.example.myself.stuttersupport;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsScreenFragment extends PreferenceFragment {

    public static final SettingsScreenFragment newInstance(){
        SettingsScreenFragment f = new SettingsScreenFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
