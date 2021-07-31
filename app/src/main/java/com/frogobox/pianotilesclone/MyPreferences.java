package com.frogobox.pianotilesclone;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.myapplication.R;

public class MyPreferences extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("my_prefs");
        addPreferencesFromResource(R.xml.my_preferences);
    }
}
