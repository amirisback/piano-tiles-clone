package com.github.gianmartind;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsPrefSaver {
    protected SharedPreferences sharedPreferences;
    protected final static String SHARED_PREF_NAME = "com.github.gianmartind.sharedprefs";
    protected final static String KEY_HEALTH = "HEALTH";

    public SettingsPrefSaver(Context context){
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveHealth(int health){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putInt(KEY_HEALTH, health);
        editor.commit();
    }

    public int getKeyHealth(){
        return this.sharedPreferences.getInt(KEY_HEALTH, 3);
    }
}
