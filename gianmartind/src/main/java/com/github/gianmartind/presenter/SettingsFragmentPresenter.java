package com.github.gianmartind.presenter;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.github.gianmartind.DBHandler;
import com.github.gianmartind.SettingsPrefSaver;

public class SettingsFragmentPresenter {
    SettingsPrefSaver settingsPrefSaver;
    ISettingsFragment ui;
    int health;
    DBHandler db;

    public SettingsFragmentPresenter(ISettingsFragment ui, SettingsPrefSaver settingsPrefSaver, DBHandler db){
        this.ui = ui;
        this.settingsPrefSaver = settingsPrefSaver;
        this.db = db;
    }

    public void loadHealth(){
        this.health = this.settingsPrefSaver.getKeyHealth();
        this.ui.loadHealth(this.health);
    }

    public void setHealth(int count){
        if(count == 1){
            this.health = 1;
            this.settingsPrefSaver.saveHealth(this.health);
            this.ui.loadHealth(this.health);
        } else if(count == 3){
            this.health = 3;
            this.settingsPrefSaver.saveHealth(this.health);
            this.ui.loadHealth(this.health);
        } else if(count == 5){
            this.health = 5;
            this.settingsPrefSaver.saveHealth(this.health);
            this.ui.loadHealth(this.health);
        }
    }
    public void openClear(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Clear Data Confirmation");
        builder.setMessage("Delete all item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                clearAll();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void clearAll(){
        this.db.deleteAllScore();
    }

    public interface ISettingsFragment{
        void loadHealth(int count);
    }
}
