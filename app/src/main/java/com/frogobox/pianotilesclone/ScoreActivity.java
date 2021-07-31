package com.frogobox.pianotilesclone;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button button;
    private String[] arr1, arr2, arr3;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        spinner = findViewById(R.id.spinner);
        ArrayList<String> arrayData = new ArrayList<>();
        arrayData.add("Easy");
        arrayData.add("Medium");
        arrayData.add("Hard");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, R.id.spinnerText, arrayData);
        spinner.setAdapter(arrayAdapter);
        Intent i = getIntent();
        arr1 = i.getStringArrayExtra("easy");
        arr2 = i.getStringArrayExtra("medium");
        arr3 = i.getStringArrayExtra("hard");

        button = findViewById(R.id.button2);
    }

    public void displayScore(View view) {
        ArrayList<String> toShow = new ArrayList<>();
        if (spinner.getSelectedItem().toString().contains("Easy")) {
            for (String s : arr1) {
                if (!s.split("\n")[1].contains("-1"))
                    toShow.add(s.split("\n")[0] + " : " + s.split("\n")[1]);
            }
        } else if (spinner.getSelectedItem().toString().contains("Medium")) {
            for (String s : arr2) {
                if (!s.split("\n")[1].contains("-1"))
                    toShow.add(s.split("\n")[0] + " : " + s.split("\n")[1]);
            }
        } else {
            for (String s : arr3) {
                if (!s.split("\n")[1].contains("-1"))
                    toShow.add(s.split("\n")[0] + " : " + s.split("\n")[1]);
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.score_item, R.id.scoreText, toShow);
        listView = findViewById(R.id.scoreList);
        listView.setAdapter(arrayAdapter);

    }
}