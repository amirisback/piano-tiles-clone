package com.github.mihaimaximfii

import android.preference.PreferenceActivity
import android.os.Bundle

class MyPreferences : PreferenceActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager.sharedPreferencesName = "my_prefs"
        addPreferencesFromResource(R.xml.my_preferences)
    }

}