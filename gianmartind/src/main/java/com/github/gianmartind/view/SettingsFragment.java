package com.github.gianmartind.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.gianmartind.R;
import com.github.gianmartind.DBHandler;
import com.github.gianmartind.SettingsPrefSaver;
import com.github.gianmartind.presenter.SettingsFragmentPresenter;

public class SettingsFragment extends Fragment implements View.OnClickListener, SettingsFragmentPresenter.ISettingsFragment {
    RadioGroup healthCount;
    RadioButton health1, health3, health5;
    Button clearScore;
    SettingsPrefSaver settingsPrefSaver;
    SettingsFragmentPresenter settingsFragmentPresenter;
    DBHandler db;
    public SettingsFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_page, container, false);

        this.healthCount = view.findViewById(R.id.health_count);
        this.health1 = view.findViewById(R.id.health_1);
        this.health3 = view.findViewById(R.id.health_3);
        this.health5 = view.findViewById(R.id.health_5);
        this.clearScore = view.findViewById(R.id.clear_score);

        this.db = new DBHandler(this.getActivity());
        this.settingsPrefSaver = new SettingsPrefSaver(this.getActivity());
        this.settingsFragmentPresenter = new SettingsFragmentPresenter(this, this.settingsPrefSaver, this.db);

        this.health1.setOnClickListener(this);
        this.health3.setOnClickListener(this);
        this.health5.setOnClickListener(this);
        this.clearScore.setOnClickListener(this);

        this.settingsFragmentPresenter.loadHealth();
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == this.health1){
            this.settingsFragmentPresenter.setHealth(1);
        } else if(v == this.health3){
            this.settingsFragmentPresenter.setHealth(3);
        } else if(v == this.health5){
            this.settingsFragmentPresenter.setHealth(5);
        } else if(v == this.clearScore){
            this.settingsFragmentPresenter.openClear(this.getActivity());
        }
    }

    @Override
    public void loadHealth(int count) {
        if(count == 1){
            this.health1.setChecked(true);
        } else if(count == 3){
            this.health3.setChecked(true);
        } else if(count == 5){
            this.health5.setChecked(true);
        }
    }

}
